/*******************************************************************************
 * Copyright (c) 2016 AgentLab.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package ru.agentlab.maia.agent.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import ru.agentlab.maia.agent.AgentState;
import ru.agentlab.maia.agent.IAgent;
import ru.agentlab.maia.agent.IAgentRegistry;
import ru.agentlab.maia.agent.IAgentStateChangeListener;
import ru.agentlab.maia.agent.IBeliefBase;
import ru.agentlab.maia.agent.IEvent;
import ru.agentlab.maia.agent.IGoalBase;
import ru.agentlab.maia.agent.IMessage;
import ru.agentlab.maia.agent.IPlanBase;
import ru.agentlab.maia.agent.IRole;
import ru.agentlab.maia.agent.IRoleBase;
import ru.agentlab.maia.agent.LocalAgentAddress;
import ru.agentlab.maia.agent.event.AddedExternalEvent;
import ru.agentlab.maia.container.IContainer;
import ru.agentlab.maia.container.IInjector;
import ru.agentlab.maia.container.impl.Container;

/**
 * 
 * @author Dmitriy Shishkin <shishkindimon@gmail.com>
 */
public class Agent implements IAgent {

	@Inject
	protected ForkJoinPool executor;

	protected final UUID uuid = UUID.randomUUID();

	private AgentState state = AgentState.UNKNOWN;

	protected final IContainer agentContainer = new Container();

	protected Queue<IEvent<?>> eventQueue = new EventQueue<>(this);

	protected IBeliefBase beliefBase;

	protected IGoalBase goalBase;

	protected IPlanBase planBase = new PlanBase();

	protected IRoleBase roleBase = new RoleBase(eventQueue, getInjector(), planBase);

	private OWLOntologyChangeListener listener;

	protected OWLIndividual selfIndividual;

	@Inject
	protected OWLDataFactory factory;

	private final ReentrantReadWriteLock.WriteLock lock = new ReentrantReadWriteLock().writeLock();

	{
		agentContainer.put(UUID.class, uuid);
		agentContainer.put(IAgent.class, this);
		agentContainer.put(Queue.class, eventQueue);
		agentContainer.put(IPlanBase.class, planBase);
		agentContainer.put(IRoleBase.class, roleBase);
	}

	@PostConstruct
	public void init(OWLOntologyManager manager, OWLDataFactory factory) throws OWLOntologyCreationException {
		selfIndividual = factory.getOWLNamedIndividual(IRI.create(uuid.toString()));
		OWLOntology ontology = getOntology(manager);
		OWLAnnotationProperty desiredAnnotationProperty = factory
			.getOWLAnnotationProperty(IGoalBase.DESIRED_ANNOTATION_IRI);
		Set<OWLAnnotation> annotations = Collections
			.singleton(factory.getOWLAnnotation(desiredAnnotationProperty, factory.getOWLLiteral(true)));

		beliefBase = new BeliefBase(manager, ontology);
		goalBase = new GoalBase(manager, ontology, annotations);
		agentContainer.put(IBeliefBase.class, beliefBase);
		agentContainer.put(IGoalBase.class, goalBase);
		// OWLReasoner reasoner = (new
		// StructuralReasonerFactory()).createReasoner(ontology);
		// QueryEngine engine = QueryEngine.create(manager, reasoner, true);
		if (listener != null) {
			manager.removeOntologyChangeListener(listener);
		}
		listener = new OntologyChangeListener(
			eventQueue,
			ontology,
			axiom -> !axiom.getAnnotations(desiredAnnotationProperty).isEmpty());
		manager.addOntologyChangeListener(listener);
	}

	@Override
	public void submit(Runnable runnabale) {
		executor.submit(new ActionExternal(this, runnabale));
	}

	private OWLOntology getOntology(OWLOntologyManager manager) throws OWLOntologyCreationException {
		IRI ontologyIRI = IRI.create(uuid.toString());
		OWLOntology ontology = manager.getOntology(ontologyIRI);
		if (ontology == null) {
			ontology = manager.createOntology(ontologyIRI);
		}
		return ontology;
	}

	@Override
	public IRole addRole(Class<?> roleClass, Map<String, Object> parameters) {
		checkNotNull(roleClass, "Role class to create should be non null");
		checkNotNull(parameters, "Extra should be non null, use empty map instead");
		lock.lock();
		try {
			return internalAddRole(roleClass, parameters);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public IRole addRole(Object role, Map<String, Object> parameters) {
		checkNotNull(role, "Role to create should be non null");
		checkNotNull(parameters, "Extra should be non null, use empty map instead");
		lock.lock();
		try {
			return internalAddRole(role, parameters);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public IRole tryAddRole(Class<?> roleClass, Map<String, Object> parameters) {
		checkNotNull(roleClass, "Role class to create should be non null");
		checkNotNull(parameters, "Extra should be non null, use empty map instead");
		if (lock.tryLock()) {
			try {
				return internalAddRole(roleClass, parameters);
			} finally {
				lock.unlock();
			}
		} else {
			throw new ConcurrentModificationException();
		}
	}

	@Override
	public IRole tryAddRole(Object role, Map<String, Object> parameters) {
		checkNotNull(role, "Role to create should be non null");
		checkNotNull(parameters, "Extra should be non null, use empty map instead");
		if (lock.tryLock()) {
			try {
				return internalAddRole(role, parameters);
			} finally {
				lock.unlock();
			}
		} else {
			throw new ConcurrentModificationException();
		}
	}

	@Override
	public IRole tryAddRole(Class<?> roleClass, Map<String, Object> parameters, long timeout, TimeUnit unit)
			throws InterruptedException {
		checkNotNull(roleClass, "Role class to create should be non null");
		checkNotNull(parameters, "Extra should be non null, use empty map instead");
		if (lock.tryLock(timeout, unit)) {
			try {
				return internalAddRole(roleClass, parameters);
			} finally {
				lock.unlock();
			}
		} else {
			throw new ConcurrentModificationException();
		}
	}

	@Override
	public IRole tryAddRole(Object role, Map<String, Object> parameters, long timeout, TimeUnit unit)
			throws InterruptedException {
		checkNotNull(role, "Role to create should be non null");
		checkNotNull(parameters, "Extra should be non null, use empty map instead");
		if (lock.tryLock(timeout, unit)) {
			try {
				return internalAddRole(role, parameters);
			} finally {
				lock.unlock();
			}
		} else {
			throw new ConcurrentModificationException();
		}
	}

	@Override
	public void activate(IRole role) {
		checkNotNull(role, "Role to activate should be non null");
		lock.lock();
		try {
			internalActivateRole(role);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void deployTo(IContainer container) {
		checkNotNull(container, "Container should be non null");
		setState(AgentState.TRANSIT);
		IInjector injector = agentContainer.getInjector();
		agentContainer.setParent(container);
		injector.inject(this);
		injector.invoke(this, PostConstruct.class);
		for (Object service : agentContainer.values()) {
			injector.inject(service);
			injector.invoke(service, PostConstruct.class);
		}
		container.put(uuid.toString(), this);
		IAgentRegistry registry = container.get(IAgentRegistry.class);
		registry.put(uuid, new LocalAgentAddress(this));
		setState(AgentState.IDLE);
	}

	@Override
	public IContainer getContainer() {
		return agentContainer.getParent();
	}

	@Override
	public Collection<IRole> getRoles() {
		return roleBase.getRoles();
	}

	@Override
	public AgentState getState() {
		return state;
	}

	@Override
	public UUID getUuid() {
		return uuid;
	}

	@Override
	public void notify(IMessage message) {
		eventQueue.offer(new AddedExternalEvent(message));
	}

	@Override
	public void clearRoles() {
		lock.lock();
		try {
			internalClearRoles();
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void tryClearRoles() {
		if (lock.tryLock()) {
			try {
				internalClearRoles();
			} finally {
				lock.unlock();
			}
		} else {
			throw new ConcurrentModificationException();
		}
	}

	@Override
	public void tryClearRoles(long timeout, TimeUnit unit) throws InterruptedException {
		if (lock.tryLock(timeout, unit)) {
			try {
				internalClearRoles();
			} finally {
				lock.unlock();
			}
		} else {
			throw new ConcurrentModificationException();
		}
	}

	@Override
	public void removeRole(IRole role) {
		checkNotNull(role, "Role to remove should be non null");
		lock.lock();
		try {
			internalRemoveRole(role);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void tryRemoveRole(IRole role) {
		if (lock.tryLock()) {
			try {
				internalRemoveRole(role);
			} finally {
				lock.unlock();
			}
		} else {
			throw new ConcurrentModificationException();
		}
	}

	@Override
	public void tryRemoveRole(IRole role, long timeout, TimeUnit unit) throws InterruptedException {
		if (lock.tryLock(timeout, unit)) {
			try {
				internalRemoveRole(role);
			} finally {
				lock.unlock();
			}
		} else {
			throw new ConcurrentModificationException();
		}
	}

	@Override
	public void start() {
		lock.lock();
		executor.submit(new ActionStart(this));
	}

	@Override
	public void tryStart() {
		if (lock.tryLock()) {
			executor.submit(new ActionStart(this));
		} else {
			throw new ConcurrentModificationException();
		}
	}

	@Override
	public void tryStart(long timeout, TimeUnit unit) throws InterruptedException {
		if (lock.tryLock(timeout, unit)) {
			executor.submit(new ActionStart(this));
		} else {
			throw new ConcurrentModificationException();
		}
	}

	@Override
	public void stop() {
		executor.submit(new ActionStop(this));
	}

	protected IInjector getInjector() {
		return agentContainer.getInjector();
	}

	protected IRole internalAddRole(Class<?> roleClass, Map<String, Object> parameters) {
		switch (state) {
		case UNKNOWN:
			throw new IllegalStateException("Agent should be deployed into container before adding new roles.");
		case ACTIVE:
		case WAITING:
			throw new IllegalStateException("Agent is in ACTIVE state, use submit method instead.");
		case TRANSIT:
			throw new IllegalStateException("Agent is in TRANSIT state, can't add new roles.");
		case STOPPING:
			throw new IllegalStateException("Agent is in STOPPING state, can't add new roles.");
		case IDLE:
		default:
			IRole role = roleBase.create(roleClass, parameters);
			roleBase.add(role);
			return role;
		}
	}

	protected IRole internalAddRole(Object roleObject, Map<String, Object> parameters) {
		switch (state) {
		case UNKNOWN:
			throw new IllegalStateException("Agent should be deployed into container before adding new roles.");
		case ACTIVE:
		case WAITING:
			throw new IllegalStateException("Agent is in ACTIVE state, use submit method instead.");
		case TRANSIT:
			throw new IllegalStateException("Agent is in TRANSIT state, can't add new roles.");
		case STOPPING:
			throw new IllegalStateException("Agent is in STOPPING state, can't add new roles.");
		case IDLE:
		default:
			IRole role = roleBase.create(roleObject, parameters);
			roleBase.add(role);
			return role;
		}
	}

	protected void internalActivateRole(IRole role) {
		switch (state) {
		case UNKNOWN:
			throw new IllegalStateException("Agent should be deployed into container before adding new roles.");
		case ACTIVE:
		case WAITING:
			throw new IllegalStateException("Agent is in ACTIVE state, use submit method instead.");
		case TRANSIT:
			throw new IllegalStateException("Agent is in TRANSIT state, can't add new roles.");
		case STOPPING:
			throw new IllegalStateException("Agent is in STOPPING state, can't add new roles.");
		case IDLE:
		default:
			roleBase.activate(role);
		}
	}

	protected void internalRemoveRole(IRole role) {
		switch (state) {
		case UNKNOWN:
			throw new IllegalStateException("Agent should be deployed into container before adding new roles.");
		case ACTIVE:
		case WAITING:
			throw new IllegalStateException("Agent is in ACTIVE state, use submit method instead.");
		case TRANSIT:
			throw new IllegalStateException("Agent is in TRANSIT state, can't add new roles.");
		case STOPPING:
			throw new IllegalStateException("Agent is in STOPPING state, can't add new roles.");
		case IDLE:
		default:
			roleBase.remove(role);
		}
	}

	protected void internalClearRoles() {
		switch (state) {
		case UNKNOWN:
			throw new IllegalStateException("Agent should be deployed into container before adding new roles.");
		case ACTIVE:
		case WAITING:
			throw new IllegalStateException("Agent is in ACTIVE state, use submit method instead.");
		case TRANSIT:
			throw new IllegalStateException("Agent is in TRANSIT state, can't add new roles.");
		case STOPPING:
			throw new IllegalStateException("Agent is in STOPPING state, can't add new roles.");
		case IDLE:
		default:
			roleBase.clear();
		}
	}

	protected boolean isActive() {
		return getState() == AgentState.ACTIVE;
	}

	final List<IAgentStateChangeListener> listeners = new ArrayList<>();

	private boolean allow = true;

	protected void setState(AgentState newState) {
		if (allow && state != newState) {
			AgentState oldState = state;
			state = newState;
			allow = !(newState == AgentState.WAITING);
			listeners.forEach(listener -> listener.changed(oldState, newState));
			allow = true;
		}
	}

	void unlock() {
		lock.unlock();
	}

	@Override
	public void addStateChangeListener(IAgentStateChangeListener listener) {
		listeners.add(listener);

	}

	@Override
	public void removeStateChangeListener(IAgentStateChangeListener listener) {
		listeners.remove(listener);
	}

	@Override
	public OWLIndividual getSelfIndividual() {
		return selfIndividual;
	}

}
