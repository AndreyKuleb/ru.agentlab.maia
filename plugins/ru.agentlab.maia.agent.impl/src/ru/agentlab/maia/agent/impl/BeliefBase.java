/*******************************************************************************
 * Copyright (c) 2016 AgentLab.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package ru.agentlab.maia.agent.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.parameters.ChangeApplied;

import com.google.common.collect.Sets;

import ru.agentlab.maia.agent.IBeliefBase;

public class BeliefBase implements IBeliefBase {

	protected OWLOntologyManager manager;

	OWLOntology ontology;

	public BeliefBase(OWLOntologyManager manager, OWLOntology ontology) {
		checkNotNull(manager, "OWLOntologyManager for belief base should be non null");
		checkNotNull(ontology, "OWLOntology for belief base should be non null");
		this.manager = manager;
		this.ontology = ontology;
	}

	@Override
	public ChangeApplied add(OWLAxiom axiom) {
		checkNotNull(axiom, "Axiom to add should be non null");
		return manager.addAxiom(ontology, axiom);
	}

	@Override
	public ChangeApplied addAll(OWLAxiom... axioms) {
		checkNotNull(axioms, "Axioms array to add should be non null");
		Set<OWLAxiom> set = Sets.newHashSet(axioms);
		return manager.addAxioms(ontology, set);
	}

	@Override
	public ChangeApplied addAll(Set<OWLAxiom> axioms) {
		checkNotNull(axioms, "Axioms set to add should be non null");
		return manager.addAxioms(ontology, axioms);
	}

	@Override
	public ChangeApplied addAll(Stream<OWLAxiom> axioms) {
		checkNotNull(axioms, "Axioms stream to add should be non null");
		Set<OWLAxiom> set = axioms.collect(Collectors.toSet());
		return manager.addAxioms(ontology, set);
	}

	@Override
	public ChangeApplied remove(OWLAxiom axiom) {
		checkNotNull(axiom, "Axiom to remove should be non null");
		return manager.removeAxiom(ontology, axiom);
	}

	@Override
	public ChangeApplied removeAll(OWLAxiom... axioms) {
		checkNotNull(axioms, "Axioms array to remove should be non null");
		Set<OWLAxiom> set = Sets.newHashSet(axioms);
		return manager.removeAxioms(ontology, set);
	}

	@Override
	public ChangeApplied removeAll(Set<OWLAxiom> axioms) {
		checkNotNull(axioms, "Axioms set to remove should be non null");
		return manager.removeAxioms(ontology, axioms);
	}

	@Override
	public ChangeApplied removeAll(Stream<OWLAxiom> axioms) {
		checkNotNull(axioms, "Axioms stream to remove should be non null");
		Set<OWLAxiom> set = axioms.collect(Collectors.toSet());
		return manager.removeAxioms(ontology, set);
	}

	@Override
	public ChangeApplied clean() {
		return manager.removeAxioms(ontology, ontology.getAxioms());
	}

	@Override
	public boolean contains(OWLAxiom axiom) {
		checkNotNull(axiom, "Axiom to test should be non null");
		return ontology.containsAxiom(axiom);
	}

	@Override
	public boolean containsAll(OWLAxiom... axioms) {
		checkNotNull(axioms, "Axioms array to test should be non null");
		return Stream.of(axioms).allMatch(ontology::containsAxiom);
	}

	@Override
	public boolean containsAll(Set<OWLAxiom> axioms) {
		checkNotNull(axioms, "Axioms set to test should be non null");
		return axioms.stream().allMatch(ontology::containsAxiom);
	}

	@Override
	public boolean containsAll(Stream<OWLAxiom> axioms) {
		checkNotNull(axioms, "Axioms stream to test should be non null");
		return axioms.allMatch(ontology::containsAxiom);
	}

	@Override
	public boolean containsAny(OWLAxiom... axioms) {
		checkNotNull(axioms, "Axioms array to test should be non null");
		return Stream.of(axioms).anyMatch(ontology::containsAxiom);
	}

	@Override
	public boolean containsAny(Set<OWLAxiom> axioms) {
		checkNotNull(axioms, "Axioms set to test should be non null");
		return axioms.stream().anyMatch(ontology::containsAxiom);
	}

	@Override
	public boolean containsAny(Stream<OWLAxiom> axioms) {
		checkNotNull(axioms, "Axioms stream to test should be non null");
		return axioms.anyMatch(ontology::containsAxiom);
	}

}
