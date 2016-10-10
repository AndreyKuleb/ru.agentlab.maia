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

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import ru.agentlab.maia.agent.AgentState;
import ru.agentlab.maia.agent.IAgent;
import ru.agentlab.maia.agent.IAgentRegistry;
import ru.agentlab.maia.agent.IMessage;
import ru.agentlab.maia.agent.IPlan;
import ru.agentlab.maia.agent.IPlanBase;
import ru.agentlab.maia.agent.IRole;
import ru.agentlab.maia.agent.IRoleBase;
import ru.agentlab.maia.agent.LocalAgentAddress;
import ru.agentlab.maia.container.IContainer;
import ru.agentlab.maia.container.IInjector;
import ru.agentlab.maia.container.impl.Container;

/**
 * 
 * @author Dmitriy Shishkin <shishkindimon@gmail.com>
 */
public class Agent implements IAgent {

	protected final class ExecuteAction extends RecursiveAction {

		private static final long serialVersionUID = 1L;

		@Override
		protected void compute() {
			Object event = eventQueue.poll();
			if (event == null) {
				setState(AgentState.WAITING);
				return;
			}

			planBase.getOptions(event).forEach(option -> {
				Map<String, Object> values = option.getValues();
				IPlan<?> plan = option.getPlan();
				try {
					plan.execute(getInjector(), values);
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("Exception while execute [" + plan + "] plan");
				}
			});

			if (isActive()) {
				ExecuteAction action = new ExecuteAction();
				executor.submit(action);
			} else {
				setState(AgentState.IDLE);
			}
		}

	}

	protected final class StartAgentAction extends RecursiveAction {

		private static final long serialVersionUID = 1L;

		@Override
		protected void compute() {
			setState(AgentState.ACTIVE);
			planBase.getStartPlans().forEach(plan -> {
				try {
					plan.execute(getInjector(), null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});

			if (isActive()) {
				ExecuteAction action = new ExecuteAction();
				executor.submit(action);
			} else {
				setState(AgentState.IDLE);
			}
		}
	}

	protected final class StopAgentAction extends RecursiveAction {

		private static final long serialVersionUID = 1L;

		@Override
		protected void compute() {
			setState(AgentState.STOPPING);
			planBase.getStopPlans().forEach(plan -> {
				try {
					plan.execute(getInjector(), null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			setState(AgentState.IDLE);
			unlock();
		}
	}

	@Inject
	protected IAgentRegistry registry;

	@Inject
	protected ForkJoinPool executor;

	protected final UUID uuid = UUID.randomUUID();

	protected final AtomicReference<AgentState> state = new AtomicReference<>(AgentState.UNKNOWN);

	protected final IContainer agentContainer = new Container();

	protected final Queue<Object> eventQueue = new EventQueue<Object>(this);

	protected final IPlanBase planBase = new PlanBase(eventQueue);

	protected final IRoleBase roleBase = new RoleBase(eventQueue, getInjector(), planBase);

	protected final ReentrantReadWriteLock.WriteLock lock = new ReentrantReadWriteLock().writeLock();

	{
		agentContainer.put(UUID.class, uuid);
		agentContainer.put(IAgent.class, this);
		agentContainer.put(Queue.class, eventQueue);
		agentContainer.put(IPlanBase.class, planBase);
		agentContainer.put(IRoleBase.class, roleBase);
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

	public <T> T deployService(Class<? super T> interf, Class<T> clazz) {
		T service = getInjector().make(clazz);
		return getInjector().deploy(service, interf);
	}

	public <T> T deployService(Class<T> clazz) {
		return getInjector().deploy(clazz);
	}

	public void deployService(String key, Object value) {
		agentContainer.put(key, value);
	}

	public <T> T deployService(T service, Class<T> interf) {
		return getInjector().deploy(service, interf);
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

	public <T> T getService(Class<T> key) {
		return agentContainer.get(key);
	}

	public Object getService(String key) {
		return agentContainer.get(key);
	}

	@Override
	public AgentState getState() {
		return state.get();
	}

	@Override
	public UUID getUuid() {
		return uuid;
	}

	@Override
	public void notify(IMessage message) {
		eventQueue.offer(message);
	}

	public <T> void putService(Class<T> key, T value) {
		agentContainer.put(key, value);
	}

	public void putService(String key, Object value) {
		agentContainer.put(key, value);
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
		executor.submit(new StartAgentAction());
	}

	@Override
	public void tryStart() {
		if (lock.tryLock()) {
			executor.submit(new StartAgentAction());
		} else {
			throw new ConcurrentModificationException();
		}
	}

	@Override
	public void tryStart(long timeout, TimeUnit unit) throws InterruptedException {
		if (lock.tryLock(timeout, unit)) {
			executor.submit(new StartAgentAction());
		} else {
			throw new ConcurrentModificationException();
		}
	}

	@Override
	public void stop() {
		executor.submit(new StopAgentAction());
	}

	protected IInjector getInjector() {
		return agentContainer.getInjector();
	}

	protected IRole internalAddRole(Class<?> roleClass, Map<String, Object> parameters) {
		switch (state.get()) {
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
		switch (state.get()) {
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
		switch (state.get()) {
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
		switch (state.get()) {
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
		switch (state.get()) {
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

	protected void setState(AgentState newState) {
		state.set(newState);
	}

	private void unlock() {
		lock.unlock();
	}

}
