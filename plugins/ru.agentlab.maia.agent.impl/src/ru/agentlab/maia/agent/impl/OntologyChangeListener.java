package ru.agentlab.maia.agent.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.function.Function;
import java.util.function.Predicate;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;

import ru.agentlab.maia.agent.IEvent;
import ru.agentlab.maia.agent.event.AddedBeliefClassAssertionAxiomEvent;
import ru.agentlab.maia.agent.event.AddedBeliefDataPropertyAssertionAxiomEvent;
import ru.agentlab.maia.agent.event.AddedBeliefNegativeDataPropertyAssertionAxiomEvent;
import ru.agentlab.maia.agent.event.AddedBeliefNegativeObjectPropertyAssertionAxiomEvent;
import ru.agentlab.maia.agent.event.AddedBeliefObjectPropertyAssertionAxiomEvent;
import ru.agentlab.maia.agent.event.AddedGoalClassAssertionAxiomEvent;
import ru.agentlab.maia.agent.event.AddedGoalDataPropertyAssertionAxiomEvent;
import ru.agentlab.maia.agent.event.AddedGoalNegativeDataPropertyAssertionAxiomEvent;
import ru.agentlab.maia.agent.event.AddedGoalNegativeObjectPropertyAssertionAxiomEvent;
import ru.agentlab.maia.agent.event.AddedGoalObjectPropertyAssertionAxiomEvent;
import ru.agentlab.maia.agent.event.RemovedBeliefClassAssertionAxiomEvent;
import ru.agentlab.maia.agent.event.RemovedBeliefDataPropertyAssertionAxiomEvent;
import ru.agentlab.maia.agent.event.RemovedBeliefNegativeDataPropertyAssertionAxiomEvent;
import ru.agentlab.maia.agent.event.RemovedBeliefNegativeObjectPropertyAssertionAxiomEvent;
import ru.agentlab.maia.agent.event.RemovedBeliefObjectPropertyAssertionAxiomEvent;
import ru.agentlab.maia.agent.event.RemovedGoalClassAssertionAxiomEvent;
import ru.agentlab.maia.agent.event.RemovedGoalDataPropertyAssertionAxiomEvent;
import ru.agentlab.maia.agent.event.RemovedGoalNegativeDataPropertyAssertionAxiomEvent;
import ru.agentlab.maia.agent.event.RemovedGoalNegativeObjectPropertyAssertionAxiomEvent;
import ru.agentlab.maia.agent.event.RemovedGoalObjectPropertyAssertionAxiomEvent;

public class OntologyChangeListener implements OWLOntologyChangeListener {

	protected Queue<IEvent<?>> eventQueue;

	protected OWLOntology ontology;

	protected Predicate<OWLAxiom> desiredPredicate;

	public OntologyChangeListener(Queue<IEvent<?>> eventQueue, OWLOntology ontology,
			Predicate<OWLAxiom> desiredPredicate) {
		this.eventQueue = eventQueue;
		this.ontology = ontology;
		this.desiredPredicate = desiredPredicate;
	}

	static Map<AxiomType<?>, Function<OWLAxiom, IEvent<?>>> addedBeliefMapping = new HashMap<>();
	static Map<AxiomType<?>, Function<OWLAxiom, IEvent<?>>> removedBeliefMapping = new HashMap<>();
	static Map<AxiomType<?>, Function<OWLAxiom, IEvent<?>>> addedGoalMapping = new HashMap<>();
	static Map<AxiomType<?>, Function<OWLAxiom, IEvent<?>>> removedGoalMapping = new HashMap<>();
	static {
		// @formatter:off
		addedBeliefMapping.put(AxiomType.CLASS_ASSERTION,                      axiom -> new AddedBeliefClassAssertionAxiomEvent((OWLClassAssertionAxiom) axiom));
		addedBeliefMapping.put(AxiomType.DATA_PROPERTY_ASSERTION,              axiom -> new AddedBeliefDataPropertyAssertionAxiomEvent((OWLDataPropertyAssertionAxiom) axiom));
		addedBeliefMapping.put(AxiomType.OBJECT_PROPERTY_ASSERTION,            axiom -> new AddedBeliefObjectPropertyAssertionAxiomEvent((OWLObjectPropertyAssertionAxiom) axiom));
		addedBeliefMapping.put(AxiomType.NEGATIVE_DATA_PROPERTY_ASSERTION,     axiom -> new AddedBeliefNegativeDataPropertyAssertionAxiomEvent((OWLNegativeDataPropertyAssertionAxiom) axiom));
		addedBeliefMapping.put(AxiomType.NEGATIVE_OBJECT_PROPERTY_ASSERTION,   axiom -> new AddedBeliefNegativeObjectPropertyAssertionAxiomEvent((OWLNegativeObjectPropertyAssertionAxiom) axiom));
		
		removedBeliefMapping.put(AxiomType.CLASS_ASSERTION,                    axiom -> new RemovedBeliefClassAssertionAxiomEvent((OWLClassAssertionAxiom) axiom));
		removedBeliefMapping.put(AxiomType.DATA_PROPERTY_ASSERTION,            axiom -> new RemovedBeliefDataPropertyAssertionAxiomEvent((OWLDataPropertyAssertionAxiom) axiom));
		removedBeliefMapping.put(AxiomType.OBJECT_PROPERTY_ASSERTION,          axiom -> new RemovedBeliefObjectPropertyAssertionAxiomEvent((OWLObjectPropertyAssertionAxiom) axiom));
		removedBeliefMapping.put(AxiomType.NEGATIVE_DATA_PROPERTY_ASSERTION,   axiom -> new RemovedBeliefNegativeDataPropertyAssertionAxiomEvent((OWLNegativeDataPropertyAssertionAxiom) axiom));
		removedBeliefMapping.put(AxiomType.NEGATIVE_OBJECT_PROPERTY_ASSERTION, axiom -> new RemovedBeliefNegativeObjectPropertyAssertionAxiomEvent((OWLNegativeObjectPropertyAssertionAxiom) axiom));

		addedGoalMapping.put(AxiomType.CLASS_ASSERTION,                        axiom -> new AddedGoalClassAssertionAxiomEvent((OWLClassAssertionAxiom) axiom));
		addedGoalMapping.put(AxiomType.DATA_PROPERTY_ASSERTION,                axiom -> new AddedGoalDataPropertyAssertionAxiomEvent((OWLDataPropertyAssertionAxiom) axiom));
		addedGoalMapping.put(AxiomType.OBJECT_PROPERTY_ASSERTION,              axiom -> new AddedGoalObjectPropertyAssertionAxiomEvent((OWLObjectPropertyAssertionAxiom) axiom));
		addedGoalMapping.put(AxiomType.NEGATIVE_DATA_PROPERTY_ASSERTION,       axiom -> new AddedGoalNegativeDataPropertyAssertionAxiomEvent((OWLNegativeDataPropertyAssertionAxiom) axiom));
		addedGoalMapping.put(AxiomType.NEGATIVE_OBJECT_PROPERTY_ASSERTION,     axiom -> new AddedGoalNegativeObjectPropertyAssertionAxiomEvent((OWLNegativeObjectPropertyAssertionAxiom) axiom));
		
		removedGoalMapping.put(AxiomType.CLASS_ASSERTION,                      axiom -> new RemovedGoalClassAssertionAxiomEvent((OWLClassAssertionAxiom) axiom));
		removedGoalMapping.put(AxiomType.DATA_PROPERTY_ASSERTION,              axiom -> new RemovedGoalDataPropertyAssertionAxiomEvent((OWLDataPropertyAssertionAxiom) axiom));
		removedGoalMapping.put(AxiomType.OBJECT_PROPERTY_ASSERTION,            axiom -> new RemovedGoalObjectPropertyAssertionAxiomEvent((OWLObjectPropertyAssertionAxiom) axiom));
		removedGoalMapping.put(AxiomType.NEGATIVE_DATA_PROPERTY_ASSERTION,     axiom -> new RemovedGoalNegativeDataPropertyAssertionAxiomEvent((OWLNegativeDataPropertyAssertionAxiom) axiom));
		removedGoalMapping.put(AxiomType.NEGATIVE_OBJECT_PROPERTY_ASSERTION,   axiom -> new RemovedGoalNegativeObjectPropertyAssertionAxiomEvent((OWLNegativeObjectPropertyAssertionAxiom) axiom));
		// @formatter:on
	}

	@Override
	public void ontologiesChanged(List<? extends OWLOntologyChange> changes) throws OWLException {
		changes
			.stream()
			.filter(change -> change.getOntology() == ontology)
			.filter(change -> change.isAddAxiom() || change.isRemoveAxiom())
			.filter(change -> AxiomType.ABoxAxiomTypes.contains(change.getAxiom().getAxiomType()))
			.map(change -> {
				OWLAxiom axiom = change.getAxiom();
				if (desiredPredicate.test(axiom)) {
					if (change.isAddAxiom()) {
						return addedGoalMapping.get(axiom.getAxiomType()).apply(axiom);
					} else {
						return removedGoalMapping.get(axiom.getAxiomType()).apply(axiom);
					}
				} else {
					if (change.isAddAxiom()) {
						return addedBeliefMapping.get(axiom.getAxiomType()).apply(axiom);
					} else {
						return removedBeliefMapping.get(axiom.getAxiomType()).apply(axiom);
					}
				}
			})
			.forEach(eventQueue::offer);
	}

}
