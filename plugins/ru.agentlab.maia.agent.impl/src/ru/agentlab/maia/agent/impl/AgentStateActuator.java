package ru.agentlab.maia.agent.impl;

import javax.inject.Inject;

import ru.agentlab.maia.agent.IAgent;
import ru.agentlab.maia.agent.annotation.Prefix;
import ru.agentlab.maia.agent.annotation.state.HaveBeliefClassAssertionAxiom;
import ru.agentlab.maia.agent.annotation.trigger.AddedGoalClassAssertionAxiom;

@Prefix(name = "maia", namespace = MaiaOntology.NAMESPACE)
public class AgentStateActuator {

	@Inject
	IAgent agent;

	@AddedGoalClassAssertionAxiom({ "maia:AgentActive", "{this}" })
	@HaveBeliefClassAssertionAxiom({ "maia:AgentIdle", "{this}" })
	public void start() {
		agent.start();
	}

	@AddedGoalClassAssertionAxiom({ "maia:AgentIdle", "{this}" })
	@HaveBeliefClassAssertionAxiom({ "maia:AgentActive", "{this}" })
	public void stop() {
		agent.stop();
	}

}
