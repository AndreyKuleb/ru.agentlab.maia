package ru.agentlab.maia.agent.impl;

import java.util.EnumMap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;

import ru.agentlab.maia.agent.AgentState;
import ru.agentlab.maia.agent.IAgent;
import ru.agentlab.maia.agent.IAgentStateChangeListener;
import ru.agentlab.maia.agent.IBeliefBase;

public class AgentStateSensor implements IAgentStateChangeListener {

	@Inject
	IAgent agent;

	@Inject
	IBeliefBase beliefBase;

	private EnumMap<AgentState, OWLClassAssertionAxiom> mapping = new EnumMap<>(AgentState.class);

	@PostConstruct
	public void init(OWLDataFactory factory) {
		initMapping(factory);
		agent.addStateChangeListener(this);
		beliefBase.add(mapping.get(agent.getState()));
	}

	@Override
	public void changed(AgentState oldState, AgentState newState) {
		beliefBase.remove(mapping.get(oldState));
		beliefBase.add(mapping.get(newState));
	}

	@PreDestroy
	public void destroy() {
		agent.removeStateChangeListener(this);
		beliefBase.remove(mapping.get(agent.getState()));
	}

	private void initMapping(OWLDataFactory factory) {
		mapping.put(
			AgentState.ACTIVE,
			factory.getOWLClassAssertionAxiom(
				factory.getOWLClass(MaiaOntology.IRIs.AGENT_ACTIVE_IRI),
				agent.getSelfIndividual()));
		mapping.put(
			AgentState.WAITING,
			factory.getOWLClassAssertionAxiom(
				factory.getOWLClass(MaiaOntology.IRIs.AGENT_WAITING_IRI),
				agent.getSelfIndividual()));
		mapping.put(
			AgentState.STOPPING,
			factory.getOWLClassAssertionAxiom(
				factory.getOWLClass(MaiaOntology.IRIs.AGENT_STOPPING_IRI),
				agent.getSelfIndividual()));
		mapping.put(
			AgentState.IDLE,
			factory.getOWLClassAssertionAxiom(
				factory.getOWLClass(MaiaOntology.IRIs.AGENT_IDLE_IRI),
				agent.getSelfIndividual()));
	}

}
