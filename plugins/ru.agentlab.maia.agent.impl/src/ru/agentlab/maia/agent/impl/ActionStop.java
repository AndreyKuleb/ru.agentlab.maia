package ru.agentlab.maia.agent.impl;

import ru.agentlab.maia.agent.AgentState;
import ru.agentlab.maia.agent.event.AddedBeliefClassAssertionAxiomEvent;

final class ActionStop extends Action {

	private static final long serialVersionUID = 1L;

	ActionStop(Agent agent) {
		super(agent);
	}

	@Override
	protected void compute() {
		try {
			agent.setState(AgentState.STOPPING);
			handleEvent(
				new AddedBeliefClassAssertionAxiomEvent(
					agent.factory.getOWLClassAssertionAxiom(
						agent.factory.getOWLClass(MaiaOntology.IRIs.AGENT_STOPPING_IRI),
						agent.getSelfIndividual())));
			agent.roleBase.deactivateAll();
			agent.setState(AgentState.IDLE);
			agent.eventQueue.clear();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			agent.unlock();
		}
	}
}