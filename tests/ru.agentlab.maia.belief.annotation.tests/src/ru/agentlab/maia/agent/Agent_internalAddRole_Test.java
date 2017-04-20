package ru.agentlab.maia.agent;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mockito;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import ru.agentlab.maia.agent.doubles.DummyService;
import ru.agentlab.maia.agent.impl.Agent;
import ru.agentlab.maia.container.IInjector;
import ru.agentlab.maia.container.InjectorException;
import ru.agentlab.maia.tests.util.LoggerRule;

/**
 * <p>
 * Test side-effects of {@link Agent#internalAddRole(Class, Map)}.
 * <p>
 * Test converted initial beliefs, goals and plans are added into corresponding
 * agent bases. After all role object should be added into role base.
 * <p>
 * If some exception will be thrown while method execution then no belief, goal
 * or plan should be added.
 * 
 * @see #testCases()
 * @see #evaluateTestCase()
 * 
 * @author Dmitriy Shishkin <shishkindimon@gmail.com>
 */
@RunWith(Parameterized.class)
@SuppressWarnings("unchecked")
public class Agent_internalAddRole_Test {

	// --------------- Injector variants----------------
	private static final DummyService ROLE_MOCK = mock(DummyService.class);

	// --------------- Converter initial beliefs variants----------------
	private static final Set<OWLAxiom> INITIAL_BELIEFS_0 = Collections.emptySet();
	private static final Set<OWLAxiom> INITIAL_BELIEFS_1 = ImmutableSet.of(mock(OWLClassAssertionAxiom.class));
	private static final Set<OWLAxiom> INITIAL_BELIEFS_3 = ImmutableSet.of(
			// @formatter:off
			mock(OWLClassAssertionAxiom.class),
			mock(OWLDataPropertyAssertionAxiom.class), 
			mock(OWLObjectPropertyAssertionAxiom.class)
			// @formatter:on
	);

	// --------------- Converter initial goals variants----------------
	private static final Set<OWLAxiom> INITIAL_GOALS_0 = Collections.emptySet();
	private static final Set<OWLAxiom> INITIAL_GOALS_1 = ImmutableSet.of(mock(OWLDataPropertyAssertionAxiom.class));
	private static final Set<OWLAxiom> INITIAL_GOALS_3 = ImmutableSet.of(
			// @formatter:off
			mock(OWLClassAssertionAxiom.class),
			mock(OWLDataPropertyAssertionAxiom.class), 
			mock(OWLObjectPropertyAssertionAxiom.class)
			// @formatter:on
	);

	// --------------- Converter plans variants----------------
	private static final Map<IPlan, EventType> INITIAL_PLANS_0 = Collections.emptyMap();
	private static final Map<IPlan, EventType> INITIAL_PLANS_1 = ImmutableMap.of(mock(IPlan.class),
			EventType.REMOVED_BELIEF);
	private static final Map<IPlan, EventType> INITIAL_PLANS_3 = ImmutableMap.of(
			// @formatter:off
			mock(IPlan.class), EventType.ADDED_BELIEF, 
			mock(IPlan.class), EventType.FAILED_GOAL,
			mock(IPlan.class), EventType.ADDED_GOAL
			// @formatter:on
	);

	@Parameters
	public static Collection<Object[]> testCases() {
		return Arrays.asList(new Object[][] {
		// @formatter:off
		/* ----------------------------------------------------------------------------------------------------------------------------------------------------
		 *|      | State                                                                                                               | Outputs               |
		 *|      |---------------------------------------------------------------------------------------------------------------------------------------------|
		 *|  	 | IInjector                             | IConverter                                                                  | Result                |
		 *| ##   |---------------------------------------------------------------------------------------------------------------------------------------------|
		 *|  	 | make         | invoke                 | getInitialBeliefs       | getInitialGoals         | getInitialPlans         | result                |
		 *----------------------------------------------------------------------------------------------------------------------------------------------------*/
		/*  0 */ { ROLE_MOCK,   null,                    INITIAL_BELIEFS_3,        INITIAL_GOALS_3,          INITIAL_PLANS_3,          ROLE_MOCK },
		/*  1 */ { ROLE_MOCK,   null,                    INITIAL_BELIEFS_0,        INITIAL_GOALS_1,          INITIAL_PLANS_3,          ROLE_MOCK },
		/*  2 */ { ROLE_MOCK,   null,                    INITIAL_BELIEFS_3,        INITIAL_GOALS_0,          INITIAL_PLANS_3,          ROLE_MOCK },
		/*  3 */ { ROLE_MOCK,   null,                    INITIAL_BELIEFS_3,        INITIAL_GOALS_3,          INITIAL_PLANS_0,          ROLE_MOCK },
		/*  4 */ { ROLE_MOCK,   null,                    INITIAL_BELIEFS_0,        INITIAL_GOALS_0,          INITIAL_PLANS_1,          ROLE_MOCK },
		/*  5 */ { ROLE_MOCK,   null,                    INITIAL_BELIEFS_1,        INITIAL_GOALS_0,          INITIAL_PLANS_0,          ROLE_MOCK },
		/*  6 */ { ROLE_MOCK,   null,                    INITIAL_BELIEFS_0,        INITIAL_GOALS_3,          INITIAL_PLANS_0,          ROLE_MOCK },
		/*  7 */ { ROLE_MOCK,   null,                    INITIAL_BELIEFS_0,        INITIAL_GOALS_0,          INITIAL_PLANS_0,          ROLE_MOCK },
		// Negative tests                                                                                    
		/*  8 */ { ROLE_MOCK,   null,                    INITIAL_BELIEFS_3,        INITIAL_GOALS_3,          ConverterException.class, ResolveException.class },
		/*  9 */ { ROLE_MOCK,   null,                    INITIAL_BELIEFS_3,        ConverterException.class, INITIAL_PLANS_3,          ResolveException.class },
		/* 10 */ { ROLE_MOCK,   null,                    ConverterException.class, INITIAL_GOALS_3,          INITIAL_PLANS_3,          ResolveException.class },
		/* 11 */ { ROLE_MOCK,   InjectorException.class, INITIAL_BELIEFS_3,        INITIAL_GOALS_3,          INITIAL_PLANS_3,          ResolveException.class },
		/* 12 */ { InjectorException.class,  null,       INITIAL_BELIEFS_3,        INITIAL_GOALS_3,          INITIAL_PLANS_3,          ResolveException.class },
		// @formatter:on
		});
	}

	@Rule
	public LoggerRule rule = new LoggerRule(this);

	@Parameter(0)
	public Object makeResult;

	@Parameter(1)
	public Object invokeResult;

	@Parameter(2)
	public Object initialBeliefs;

	@Parameter(3)
	public Object initialGoals;

	@Parameter(4)
	public Object initialPlans;

	@Parameter(5)
	public Object result;

	@Test
	public void evaluateTestCase() throws Exception {
		// Given
		Agent agent = spy(new Agent());
		IInjector injector = mockInjector();
		doReturn(injector).when(agent).getInjector();
		doReturn(mockConverter(injector)).when(injector).make(Mockito.any());
		spyFields(agent);

		try {
			// When
			Object role = agent.internalAddRole(DummyService.class, null);

			// Then
			if (result instanceof Class) {
				Assert.fail("Expected [" + result + "], but was: [" + role + "]");
			}
			checkBeliefBase(agent);
			checkGoalBase(agent);
			checkPlanBase(agent);
			verify(agent.internalEventQueue, times(1)).offer(new RoleAddedEvent(role));
			verify(agent.internalEventQueue, times(1)).offer(new RoleResolvedEvent(role));
			assertThat(role, equalTo(result));
		} catch (Exception e) {
			// Then
			if (!(result instanceof Class)) {
				e.printStackTrace();
				Assert.fail("Expected [" + result + "], but was: [" + e + "]");
			}
			assertThat(e, instanceOf((Class<?>) result));

			// If exception was thrown then check no one belief, goal, plan or
			// role added. Only ROLE_UNRESOLVED event should be added to event
			// queue.
			verifyZeroInteractions(agent.beliefBase, agent.goalBase, agent.planBase);
			verify(agent.internalEventQueue, times(1)).offer(new RoleUnresolvedEvent(DummyService.class));
		}
	}

	private void spyFields(IAgent agent) throws Exception {
		List<Field> allFields = new ArrayList<>();
		addDeclaredAndInheritedFields(agent.getClass(), allFields);
		spyField(agent, "eventQueue", allFields);
		spyField(agent, "beliefBase", allFields);
		spyField(agent, "goalBase", allFields);
		spyField(agent, "planBase", allFields);
		spyField(agent, "roles", allFields);
	}

	private void checkBeliefBase(Agent agent) {
		Set<OWLAxiom> beliefs = (Set<OWLAxiom>) initialBeliefs;
		if (beliefs != null) {
			verify(agent.beliefBase, times(beliefs.size())).add(Mockito.any(OWLAxiom.class));
			beliefs.stream().forEach(axiom -> verify(agent.beliefBase).add(axiom));
		} else {
			verifyZeroInteractions(agent.beliefBase);
		}
	}

	private void checkGoalBase(Agent agent) {
		Set<OWLAxiom> goals = (Set<OWLAxiom>) initialGoals;
		if (goals != null) {
			verify(agent.goalBase, times(goals.size())).add(Mockito.any(OWLAxiom.class));
			goals.stream().forEach(axiom -> verify(agent.goalBase).add(axiom));
		} else {
			verifyZeroInteractions(agent.goalBase);
		}
	}

	private void checkPlanBase(Agent agent) {
		Map<IPlan, EventType> plans = (Map<IPlan, EventType>) initialPlans;
		if (plans != null) {
			verify(agent.planBase, times(plans.size())).add(Mockito.any(EventType.class), Mockito.any(IPlan.class));
			plans.forEach((plan, event) -> verify(agent.planBase).add(event, plan));
		} else {
			verifyZeroInteractions(agent.planBase);
		}
	}

	private Object spyField(Object source, String name, Collection<Field> allFields) throws Exception {
		System.out.println(allFields);
		Field field = allFields.stream().filter(f -> f.getName().equals(name)).findFirst().get();
		field.setAccessible(true);
		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
		Object value = field.get(source);
		Object spyed = spy(value);
		field.set(source, spyed);
		return spyed;
	}

	private void addDeclaredAndInheritedFields(Class<?> c, Collection<Field> fields) {
		fields.addAll(Arrays.asList(c.getDeclaredFields()));
		Class<?> superClass = c.getSuperclass();
		if (superClass != null) {
			addDeclaredAndInheritedFields(superClass, fields);
		}
	}

	private IConverter mockConverter(IInjector injector) throws ConverterException {
		IConverter converter = mock(IConverter.class);
		if (initialBeliefs instanceof Class) {
			when(converter.getInitialBeliefs(ROLE_MOCK)).thenThrow((Class<? extends Exception>) initialBeliefs);
		} else {
			when(converter.getInitialBeliefs(ROLE_MOCK)).thenReturn((Set<OWLAxiom>) initialBeliefs);
		}
		if (initialGoals instanceof Class) {
			when(converter.getInitialGoals(ROLE_MOCK)).thenThrow((Class<? extends Exception>) initialGoals);
		} else {
			when(converter.getInitialGoals(ROLE_MOCK)).thenReturn((Set<OWLAxiom>) initialGoals);
		}
		if (initialPlans instanceof Class) {
			when(converter.getInitialPlans(ROLE_MOCK)).thenThrow((Class<? extends Exception>) initialPlans);
		} else {
			when(converter.getInitialPlans(ROLE_MOCK)).thenReturn((Map<IPlan, EventType>) initialPlans);
		}
		return converter;
	}

	private IInjector mockInjector() throws InjectorException {
		IInjector injector = mock(IInjector.class);
		if (makeResult instanceof Class) {
			when(injector.make(DummyService.class, null)).thenThrow((Class<? extends Exception>) makeResult);
		} else {
			when(injector.make(DummyService.class, null)).thenReturn((DummyService) makeResult);
		}
		if (invokeResult != null && invokeResult instanceof Class) {
			when(injector.invoke(ROLE_MOCK, PostConstruct.class, null, null))
					.thenThrow((Class<? extends Exception>) invokeResult);
		} else {
			when(injector.invoke(ROLE_MOCK, PostConstruct.class, null, null)).thenReturn(invokeResult);
		}
		return injector;
	}
}