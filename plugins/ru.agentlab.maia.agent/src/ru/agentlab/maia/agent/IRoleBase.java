package ru.agentlab.maia.agent;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public interface IRoleBase {

	IRole create(Class<?> roleClass, Map<String, Object> parameters);

	default IRole create(Class<?> roleClass) {
		checkNotNull(roleClass, "Role class to create should be non null");
		return create(roleClass, new HashMap<String, Object>());
	}

	IRole create(Object role, Map<String, Object> parameters);

	default IRole create(Object role) {
		checkNotNull(role, "Role to create should be non null");
		return create(role, new HashMap<String, Object>());
	}

	boolean add(IRole role);

	default boolean addAll(IRole[] roles) {
		checkNotNull(roles, "Roles to add should be non null");
		return Stream.of(roles).map(this::add).reduce(false, (a, b) -> a || b);
	}

	default boolean addAll(Collection<IRole> roles) {
		checkNotNull(roles, "Roles to add should be non null");
		return roles.stream().map(this::add).reduce(false, (a, b) -> a || b);
	}

	default boolean addAll(Stream<IRole> roles) {
		checkNotNull(roles, "Roles to add should be non null");
		return roles.map(this::add).reduce(false, (a, b) -> a || b);
	}

	default boolean addAndActivate(IRole role) {
		checkNotNull(role, "Role to add should be non null");
		if (add(role)) {
			return activate(role);
		} else {
			return false;
		}
	}

	boolean activate(IRole role);

	default void activateAll() {
		getRoles().stream().forEach(this::activate);
	}

	boolean deactivate(IRole role);

	default void deactivateAll() {
		getRoles().stream().forEach(this::deactivate);
	}

	boolean remove(IRole role);

	default boolean removeAll(IRole[] roles) {
		checkNotNull(roles, "Roles to remove should be non null");
		return Stream.of(roles).map(this::remove).reduce(false, (a, b) -> a || b);
	}

	default boolean removeAll(Collection<IRole> roles) {
		checkNotNull(roles, "Roles to remove should be non null");
		return roles.stream().map(this::remove).reduce(false, (a, b) -> a || b);
	}

	default boolean removeAll(Stream<IRole> roles) {
		checkNotNull(roles, "Roles to remove should be non null");
		return roles.map(this::remove).reduce(false, (a, b) -> a || b);
	}

	Collection<IRole> getRoles();

	default void clear() {
		getRoles().forEach(this::remove);
	}

}