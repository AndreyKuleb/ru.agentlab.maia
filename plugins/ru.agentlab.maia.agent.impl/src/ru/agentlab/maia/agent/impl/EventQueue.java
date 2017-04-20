package ru.agentlab.maia.agent.impl;

import java.util.concurrent.ConcurrentLinkedQueue;

import ru.agentlab.maia.agent.AgentState;

public class EventQueue<T> extends ConcurrentLinkedQueue<T> {

	private static final long serialVersionUID = 1L;

	private Agent agent;

	public EventQueue(Agent agent) {
		this.agent = agent;
	}

	@Override
	public boolean offer(T e) {
		if (agent.getState() == AgentState.WAITING) {
			agent.setState(AgentState.ACTIVE);
			boolean result = super.offer(e);
			agent.executor.submit(new ActionExecute(agent));
			return result;
		} else {
			return super.offer(e);
		}
		// boolean started = agent.state.compareAndSet(AgentState.WAITING,
		// AgentState.ACTIVE);
		// if (started) {
		// agent.executor.submit(new ActionExecute(agent));
		// }
		// return super.offer(e);
	}

}