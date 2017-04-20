package ru.agentlab.maia.examples;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import ru.agentlab.maia.agent.IPlan;
import ru.agentlab.maia.agent.IPlanBase;
import ru.agentlab.maia.agent.event.BeliefAddedEvent;
import ru.agentlab.maia.agent.impl.Plan;

public class ExampleLambdaPlan {

	@Inject
	IPlanBase planBase;

	@Inject
	Object service;

	@PostConstruct
	public void setup() {
		IPlan<BeliefAddedEvent> plan = new Plan<>(BeliefAddedEvent.class, () -> {
			System.out.println(service);
			System.out.println(service.hashCode());
		});
		planBase.add(plan);
	}

}
