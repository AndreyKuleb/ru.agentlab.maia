package ru.agentlab.maia.agent.impl;

import java.util.Map;
import java.util.concurrent.RecursiveAction;

import ru.agentlab.maia.agent.IEvent;
import ru.agentlab.maia.agent.IPlan;

abstract class Action extends RecursiveAction {

	private static final long serialVersionUID = 1L;

	final Agent agent;

	Action(Agent agent) {
		this.agent = agent;
	}

	void handleEvent(IEvent<?> event) {
		System.out.println("Action handle " + event);
		agent.planBase.getOptions(event).forEach(option -> {
			Map<String, Object> values = option.getValues();
			IPlan<?> plan = option.getPlan();
			try {
				plan.execute(agent.getInjector(), values);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("Exception while execute [" + plan + "] plan");
			}
		});
	}

}
