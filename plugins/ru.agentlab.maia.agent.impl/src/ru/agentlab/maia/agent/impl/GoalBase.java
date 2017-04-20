package ru.agentlab.maia.agent.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLIndividualAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.parameters.ChangeApplied;

import ru.agentlab.maia.agent.IGoalBase;

public class GoalBase implements IGoalBase {

	private OWLOntologyManager manager;

	private OWLOntology ontology;

	private Set<OWLAnnotation> annotations;

	public GoalBase(OWLOntologyManager manager, OWLOntology ontology, Set<OWLAnnotation> annotations) {
		checkNotNull(ontology, "OWLOntologyManager for goal base should be non null");
		checkNotNull(ontology, "OWLOntology for goal base should be non null");
		checkNotNull(ontology, "Annotations for goal should be non null");
		checkArgument(!annotations.isEmpty(), "Annotations for goal should be non empty");
		this.manager = manager;
		this.ontology = ontology;
		this.annotations = annotations;
	}

	@Override
	public ChangeApplied add(OWLIndividualAxiom axiom) {
		checkNotNull(axiom, "Axiom to add should be non null");
		return manager.addAxiom(ontology, axiom.getAnnotatedAxiom(annotations));
	}

	@Override
	public ChangeApplied addAll(OWLIndividualAxiom... axioms) {
		checkNotNull(axioms, "Axioms array to add should be non null");
		Set<OWLIndividualAxiom> set = Stream
			.of(axioms)
			.map(axiom -> (OWLIndividualAxiom) axiom.getAnnotatedAxiom(annotations))
			.collect(Collectors.toSet());
		return manager.addAxioms(ontology, set);
	}

	@Override
	public ChangeApplied addAll(Set<OWLIndividualAxiom> axioms) {
		checkNotNull(axioms, "Axioms set to add should be non null");
		Set<OWLIndividualAxiom> set = axioms
			.stream()
			.map(axiom -> (OWLIndividualAxiom) axiom.getAnnotatedAxiom(annotations))
			.collect(Collectors.toSet());
		return manager.addAxioms(ontology, set);
	}

	@Override
	public ChangeApplied addAll(Stream<OWLIndividualAxiom> axioms) {
		checkNotNull(axioms, "Axioms stream to add should be non null");
		Set<OWLIndividualAxiom> set = axioms
			.map(axiom -> (OWLIndividualAxiom) axiom.getAnnotatedAxiom(annotations))
			.collect(Collectors.toSet());
		return manager.addAxioms(ontology, set);
	}

	@Override
	public ChangeApplied remove(OWLIndividualAxiom axiom) {
		checkNotNull(axiom, "Axiom to remove should be non null");
		return manager.removeAxiom(ontology, axiom.getAnnotatedAxiom(annotations));
	}

	@Override
	public ChangeApplied removeAll(OWLIndividualAxiom... axioms) {
		checkNotNull(axioms, "Axioms array to remove should be non null");
		Set<OWLIndividualAxiom> set = Stream
			.of(axioms)
			.map(axiom -> (OWLIndividualAxiom) axiom.getAnnotatedAxiom(annotations))
			.collect(Collectors.toSet());
		return manager.removeAxioms(ontology, set);
	}

	@Override
	public ChangeApplied removeAll(Set<OWLIndividualAxiom> axioms) {
		checkNotNull(axioms, "Axioms set to remove should be non null");
		Set<OWLIndividualAxiom> set = axioms
			.stream()
			.map(axiom -> (OWLIndividualAxiom) axiom.getAnnotatedAxiom(annotations))
			.collect(Collectors.toSet());
		return manager.removeAxioms(ontology, set);
	}

	@Override
	public ChangeApplied removeAll(Stream<OWLIndividualAxiom> axioms) {
		checkNotNull(axioms, "Axioms stream to remove should be non null");
		Set<OWLIndividualAxiom> set = axioms
			.map(axiom -> (OWLIndividualAxiom) axiom.getAnnotatedAxiom(annotations))
			.collect(Collectors.toSet());
		return manager.removeAxioms(ontology, set);
	}

	@Override
	public ChangeApplied clean() {
		Set<OWLAxiom> axioms = ontology
			.getAxioms()
			.stream()
			.filter(axiom -> axiom.getAnnotations().containsAll(annotations))
			.collect(Collectors.toSet());
		return manager.removeAxioms(ontology, axioms);
	}

}
