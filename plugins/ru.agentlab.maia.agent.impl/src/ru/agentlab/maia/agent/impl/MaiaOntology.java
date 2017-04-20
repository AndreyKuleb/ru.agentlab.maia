package ru.agentlab.maia.agent.impl;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

public class MaiaOntology {

	public static final String NAMESPACE = "http://www.agentlab.ru/ontologies/maia#";

	public static class ShortNames {
		// @formatter:off
		public static final String HAS_UUID =       "hasUUID";
		public static final String AGENT_ACTIVE =   "ACTIVE";
		public static final String AGENT_IDLE =     "IDLE";
		public static final String AGENT_STOPPING = "STOPPING";
		public static final String AGENT_WAITING =  "WAITING";
		public static final String DESIRED =        "Desired";
		// @formatter:on
	}

	public static class FullNames {
		// @formatter:off
		public static final String MAIA_DESIRED =   "<" + NAMESPACE + ShortNames.DESIRED + ">";
		public static final String AGENT_STARTED =  "<" + NAMESPACE + ShortNames.AGENT_ACTIVE + ">";
		public static final String AGENT_WAITED =   "<" + NAMESPACE + ShortNames.AGENT_WAITING + ">";
		public static final String AGENT_STOPPING = "<" + NAMESPACE + ShortNames.AGENT_STOPPING + ">";
		public static final String AGENT_STOPPED =  "<" + NAMESPACE + ShortNames.AGENT_IDLE + ">";
		public static final String HAS_UUID =       "<" + NAMESPACE + ShortNames.HAS_UUID + ">";
		// @formatter:on
	}

	public static class IRIs {
		// @formatter:off
		public static final IRI DESIRED_IRI =        IRI.create(NAMESPACE, ShortNames.DESIRED);
		public static final IRI AGENT_ACTIVE_IRI =   IRI.create(NAMESPACE, ShortNames.AGENT_ACTIVE);
		public static final IRI AGENT_WAITING_IRI =  IRI.create(NAMESPACE, ShortNames.AGENT_WAITING);
		public static final IRI AGENT_STOPPING_IRI = IRI.create(NAMESPACE, ShortNames.AGENT_STOPPING);
		public static final IRI AGENT_IDLE_IRI =     IRI.create(NAMESPACE, ShortNames.AGENT_IDLE);
		public static final IRI HAS_UUID_IRI =       IRI.create(NAMESPACE, ShortNames.HAS_UUID);
		// @formatter:on
	}

	public static OWLNamedIndividual thisAgent;
	public static OWLClassAssertionAxiom agentStarted;
	public static OWLClassAssertionAxiom agentWaited;
	public static OWLClassAssertionAxiom agentStopping;
	public static OWLClassAssertionAxiom agentIdle;

	// private OWLDataPropertyAssertionAxiom createHasUuid(Agent agent,
	// OWLDataFactory factory) {
	// return factory.getOWLDataPropertyAssertionAxiom(
	// factory.getOWLDataProperty(IRIs.HAS_UUID_IRI),
	// thisAgent,
	// factory.getOWLLiteral(agent.uuid.toString()));
	// }
	//
	// public OWLClassAssertionAxiom getAgentWaited() {
	// return agentWaited;
	// }
	//
	// public OWLClassAssertionAxiom getAgentStarted() {
	// return agentStarted;
	// }
	//
	// public OWLClassAssertionAxiom getAgentStopping() {
	// return agentStopping;
	// }
	//
	// public OWLClassAssertionAxiom getAgentIdle() {
	// return agentIdle;
	// }

}
