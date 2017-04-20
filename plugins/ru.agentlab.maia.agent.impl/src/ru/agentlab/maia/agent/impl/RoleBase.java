package ru.agentlab.maia.agent.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.tuple.Pair;

import ru.agentlab.maia.agent.IEvent;
import ru.agentlab.maia.agent.IPlan;
import ru.agentlab.maia.agent.IPlanBase;
import ru.agentlab.maia.agent.IRole;
import ru.agentlab.maia.agent.IRoleBase;
import ru.agentlab.maia.container.IInjector;
import ru.agentlab.maia.converter.IPlanEventFilterConverter;
import ru.agentlab.maia.converter.IPlanEventTypeConverter;
import ru.agentlab.maia.converter.IPlanExtraConverter;
import ru.agentlab.maia.converter.IPlanStateFilterConverter;
import ru.agentlab.maia.converter.PlanEventFilterConverter;
import ru.agentlab.maia.converter.PlanEventType;
import ru.agentlab.maia.converter.PlanEventTypeConverter;
import ru.agentlab.maia.converter.PlanExtraConverter;
import ru.agentlab.maia.converter.PlanStateFilterConverter;
import ru.agentlab.maia.filter.IPlanEventFilter;
import ru.agentlab.maia.filter.IPlanStateFilter;
import ru.agentlab.maia.filter.impl.PlanStateFilters;

public class RoleBase implements IRoleBase {

	protected final Set<IRole> roles = new HashSet<>();

	protected IInjector injector;

	protected Queue<IEvent<?>> eventQueue;

	protected IPlanBase planBase;

	public RoleBase(Queue<IEvent<?>> eventQueue, IInjector injector, IPlanBase planBase) {
		checkNotNull(eventQueue, "Event Queue should be non null");
		checkNotNull(injector, "Injector should be non null");
		checkNotNull(planBase, "Plan Base should be non null");
		this.injector = injector;
		this.eventQueue = eventQueue;
		this.planBase = planBase;
	}

	@Override
	public IRole create(Class<?> roleClass, Map<String, Object> parameters) {
		checkNotNull(roleClass, "Role class to create should be non null");
		checkNotNull(parameters, "Extra should be non null, use empty map instead");
		checkArgument(!ClassUtils.isPrimitiveOrWrapper(roleClass), "Role class to create should be non primitive type");
		Role role = new Role();
		parameters.put(IRole.class.getName(), role);
		Object roleObject = injector.make(roleClass, parameters);
		role.extra = parameters;
		role.plans = getPlans(roleObject);
		role.roleObject = roleObject;
		return role;
	}

	@Override
	public IRole create(Object roleObject, Map<String, Object> parameters) {
		checkNotNull(roleObject, "Role should be non null");
		checkNotNull(parameters, "Extra should be non null, use empty map instead");
		checkArgument(
			!ClassUtils.isPrimitiveOrWrapper(roleObject.getClass()),
			"Role class to create should be non primitive type");
		Role role = new Role();
		parameters.put(IRole.class.getName(), role);
		role.extra = parameters;
		role.plans = getPlans(roleObject);
		role.roleObject = roleObject;
		return role;
	}

	@Override
	public boolean add(IRole role) {
		checkNotNull(role, "Role to add should be non null");
		if (roles.add(role)) {
			// eventQueue.offer(new RoleAddedEvent(role));
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean activate(IRole role) {
		checkNotNull(role, "Role to activate should be non null");
		checkArgument(roles.contains(role), "Unknown role to activate, role should exists into role base");
		if (!role.isActive()) {
			Map<String, Object> extra = role.getExtra();
			Object roleObject = role.getRoleObject();
			injector.inject(roleObject, extra);
			injector.invoke(roleObject, PostConstruct.class, extra);
			planBase.addAll(role.getPlans());
			role.setActive(true);
			// eventQueue.offer(new RoleActivatedEvent(role));
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean deactivate(IRole role) {
		checkNotNull(role, "Role to deactivate should be non null");
		checkArgument(roles.contains(role), "Unknown role to deactivate, role should exists into role base");
		return deactivateInternal(role);
	}

	@Override
	public boolean remove(IRole role) {
		checkNotNull(role, "Role to remove should be non null");
		if (roles.remove(role)) {
			deactivateInternal(role);
			// eventQueue.offer(new RoleRemovedEvent(role));
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Collection<IRole> getRoles() {
		return roles;
	}

	@Override
	public void clear() {
		roles.forEach(role -> {
			deactivateInternal(role);
			// eventQueue.offer(new RoleRemovedEvent(role));
		});
		roles.clear();
	}

	protected IPlan<?>[] getPlans(Object role) {
		List<IPlan<?>> result = new ArrayList<>();
		for (Method method : role.getClass().getDeclaredMethods()) {
			Map<String, Object> customData = new HashMap<>();
			IPlanStateFilter stateMatcher = getStateMatcher(role, method, customData);
			List<Pair<Class<?>, IPlanEventFilter<?>>> registrations = getRegistrations(role, method, customData);
			registrations.forEach(
				registration -> result
					.add(new Plan(registration.getLeft(), role, method, registration.getRight(), stateMatcher)));
			List<IPlan<?>> extraPlans = getExtraPlans(role, method, customData);
			result.addAll(extraPlans);
		}
		return result.toArray(new IPlan[result.size()]);
	}

	private boolean deactivateInternal(IRole role) {
		if (role.isActive()) {
			Map<String, Object> extra = role.getExtra();
			Object roleObject = role.getRoleObject();
			planBase.removeAll(role.getPlans());
			injector.invoke(roleObject, PreDestroy.class, extra);
			injector.uninject(roleObject);
			role.setActive(false);
			// eventQueue.offer(new RoleDeactivatedEvent(role));
			return true;
		} else {
			return false;
		}
	}

	private Class<?> getEventType(Object role, Method method, Annotation annotation, Map<String, Object> customData) {
		PlanEventType eventType = annotation.annotationType().getAnnotation(PlanEventType.class);
		if (eventType != null) {
			return eventType.value();
		}
		PlanEventTypeConverter eventTypeConverter = annotation
			.annotationType()
			.getAnnotation(PlanEventTypeConverter.class);
		if (eventTypeConverter != null) {
			Class<? extends IPlanEventTypeConverter> converterClass = eventTypeConverter.value();
			IPlanEventTypeConverter converter = injector.make(converterClass);
			injector.inject(converter);
			injector.invoke(converter, PostConstruct.class);
			return converter.getEventType(role, method, annotation, customData);
		}
		return null;
	}

	private List<Pair<Class<?>, IPlanEventFilter<?>>> getRegistrations(Object role, Method method,
			Map<String, Object> customData) {
		Annotation[] annotations = method.getAnnotations();
		List<Pair<Class<?>, IPlanEventFilter<?>>> result = new ArrayList<>();
		for (Annotation annotation : annotations) {
			PlanEventFilterConverter eventMatcher = annotation
				.annotationType()
				.getAnnotation(PlanEventFilterConverter.class);
			if (eventMatcher != null) {
				Class<? extends IPlanEventFilterConverter> converterClass = eventMatcher.value();
				Class<?> eventType = getEventType(role, method, annotation, customData);
				IPlanEventFilterConverter converter = injector.make(converterClass);
				injector.inject(converter);
				injector.invoke(converter, PostConstruct.class);
				result.add(Pair.of(eventType, converter.getMatcher(role, method, annotation, customData)));
			}
		}
		return result;
	}

	private IPlanStateFilter getStateMatcher(Object role, Method method, Map<String, Object> customData) {
		Annotation[] annotations = method.getAnnotations();
		List<IPlanStateFilter> result = new LinkedList<>();
		for (Annotation annotation : annotations) {
			PlanStateFilterConverter stateMatcher = annotation
				.annotationType()
				.getAnnotation(PlanStateFilterConverter.class);
			if (stateMatcher != null) {
				Class<? extends IPlanStateFilterConverter> converterClass = stateMatcher.value();
				IPlanStateFilterConverter converter = injector.make(converterClass);
				injector.inject(converter);
				injector.invoke(converter, PostConstruct.class);
				result.add(converter.getMatcher(role, method, annotation, customData));
			}
		}
		if (result.size() == 0) {
			return PlanStateFilters.anything();
		} else if (result.size() == 1) {
			return result.get(0);
		} else {
			return PlanStateFilters.allOf(result);
		}
	}

	private List<IPlan<?>> getExtraPlans(Object role, Method method, Map<String, Object> customData) {
		Annotation[] annotations = method.getAnnotations();
		for (Annotation annotation : annotations) {
			PlanExtraConverter extraPlansAnnotation = annotation
				.annotationType()
				.getAnnotation(PlanExtraConverter.class);
			if (extraPlansAnnotation != null) {
				Class<? extends IPlanExtraConverter> converterClass = extraPlansAnnotation.value();
				IPlanExtraConverter converter = injector.make(converterClass);
				injector.inject(converter);
				injector.invoke(converter, PostConstruct.class);
				return converter.getPlans(role, method, annotation, customData);
			}
		}
		return Collections.emptyList();
	}

}
