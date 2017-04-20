/*******************************************************************************
 * Copyright (c) 2016 AgentLab.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package ru.agentlab.maia.agent;

import java.util.Set;
import java.util.stream.Stream;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.parameters.ChangeApplied;

public interface IBeliefBase {

	ChangeApplied add(OWLAxiom axiom);

	ChangeApplied addAll(OWLAxiom... axioms);

	ChangeApplied addAll(Set<OWLAxiom> axioms);

	ChangeApplied addAll(Stream<OWLAxiom> axioms);

	ChangeApplied remove(OWLAxiom axiom);

	ChangeApplied removeAll(OWLAxiom... axioms);

	ChangeApplied removeAll(Set<OWLAxiom> axioms);

	ChangeApplied removeAll(Stream<OWLAxiom> axioms);

	ChangeApplied clean();

	boolean contains(OWLAxiom axiom);

	boolean containsAll(OWLAxiom... axioms);

	boolean containsAll(Set<OWLAxiom> axioms);

	boolean containsAll(Stream<OWLAxiom> axioms);

	boolean containsAny(OWLAxiom... axioms);

	boolean containsAny(Set<OWLAxiom> axioms);

	boolean containsAny(Stream<OWLAxiom> axioms);

}
