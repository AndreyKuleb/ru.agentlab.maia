package ru.agentlab.maia.agent.impl;

import static ru.agentlab.maia.agent.annotation.AxiomType.CLASS_ASSERTION;
import static ru.agentlab.maia.agent.annotation.AxiomType.DATA_PROPERTY_ASSERTION;
import static ru.agentlab.maia.agent.annotation.AxiomType.SUBCLASS_OF;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;
import javax.inject.Named;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.vocab.Namespaces;

import de.derivo.sparqldlapi.QueryResult;
import ru.agentlab.maia.agent.IAgent;
import ru.agentlab.maia.agent.IBeliefBase;
import ru.agentlab.maia.agent.IEvent;
import ru.agentlab.maia.agent.ResolveException;
import ru.agentlab.maia.agent.annotation.InitialBelief;
import ru.agentlab.maia.agent.annotation.OnBeliefAdded;
import ru.agentlab.maia.agent.annotation.WhenHaveBelief;
import ru.agentlab.maia.agent.event.BeliefAddedEvent;
import ru.agentlab.maia.agent.factory.Agents;
import ru.agentlab.maia.container.ContainerException;
import ru.agentlab.maia.container.IContainer;
import ru.agentlab.maia.container.InjectorException;
import ru.agentlab.maia.container.factory.Containers;

public class Agent_AcceptanceTest {

	public static void main(String[] args)
			throws InjectorException, ContainerException, ResolveException, InterruptedException {
		IContainer container = Containers.createDefaultBDI();
		OWLDataFactory factory = container.get(OWLDataFactory.class);
		List<Agent> agents = new ArrayList<>();

		for (int j = 0; j < 10; j++) {
			Agent agent = (Agent) Agents.createBDI();
			System.out.println("Create Agent " + agent.getUuid());
			agents.add(agent);
			agent.deployTo(container);
			agent.addRole(TestRole.class);
			IntStream.range(0, 500_000).forEach(i -> {
				IEvent<?> event = new BeliefAddedEvent(factory.getOWLDataPropertyAssertionAxiom(
						factory.getOWLDataProperty(IRI.create(Namespaces.RDF.toString(), "hasProperty")),
						factory.getOWLNamedIndividual(IRI.create(Namespaces.RDF.toString(), "ind")),
						factory.getOWLLiteral(2)));
				agent.internalEventQueue.offer(event);
			});
		}

		System.out.println("START AGENTS");
		for (IAgent agent : agents) {
			agent.start();
		}
		Thread.sleep(50_000);
		for (IAgent agent : agents) {
			agent.stop();
		}
		for (Agent agent : agents) {
			System.err.println();
			System.err.println(agent.uuid);
			System.err.println(agent.state);
			System.err.println(agent.i.get());
		}
		System.err.println(agents.stream().mapToInt(a -> a.i.get()).sum());

	}

	@InitialBelief(type = DATA_PROPERTY_ASSERTION, value = { "rdf:ind", "rdf:hasProperty", "2^^xsd:integer" })
	@InitialBelief(type = SUBCLASS_OF, value = { "rdf:Some", "owl:Thing" })
	@InitialBelief(type = CLASS_ASSERTION, value = { "rdf:Some", "rdf:ind" })
	public static class TestRole {

		Logger logger = Logger.getLogger(this.getClass().getName());

		@PostConstruct
		public void init(IAgent agent, IBeliefBase bb) {
			System.out.println(agent.getUuid());
			PrefixManager prefixes = new DefaultPrefixManager();
			OWLDataFactory factory = bb.getFactory();
			bb.add(factory.getOWLSubClassOfAxiom(factory.getOWLClass(prefixes.getIRI("rdf:Some")),
					factory.getOWLClass(prefixes.getIRI("owl:Thing"))));
			bb.add(factory.getOWLClassAssertionAxiom(factory.getOWLClass(prefixes.getIRI("rdf:Some")),
					factory.getOWLNamedIndividual(prefixes.getIRI("rdf:ind"))));
		}

		@OnBeliefAdded(type = DATA_PROPERTY_ASSERTION, value = { "rdf:ind", "rdf:hasProperty", "2^^xsd:integer" })
		@WhenHaveBelief(type = SUBCLASS_OF, value = { "rdf:Some", "owl:Thing" })
		@WhenHaveBelief(type = CLASS_ASSERTION, value = { "rdf:ind", "rdf:Some" })
		public void exe() {
//			logger.info(() -> "WORKS");
			// System.out.println();

		}

		@OnBeliefAdded(type = DATA_PROPERTY_ASSERTION, value = { "?ind", "?property", "2^^xsd:integer" })
		@WhenHaveBelief(type = SUBCLASS_OF, value = { "rdf:Some", "owl:Thing" })
		@WhenHaveBelief(type = CLASS_ASSERTION, value = { "rdf:ind", "?ind" })
		public void exe2(@Named("property") OWLDataProperty property, @Named("ind") OWLIndividual ind,
				QueryResult res) {
//			logger.info(() -> "WORKS2 " + property.toString());
//			logger.info(() -> "WORKS2 " + ind.toString());
			// System.out.println("WORKS2 " + property.toString());
			// System.out.println("WORKS2 " + ind.toString());

		}

	}
}
