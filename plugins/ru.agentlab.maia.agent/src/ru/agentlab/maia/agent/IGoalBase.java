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

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLIndividualAxiom;
import org.semanticweb.owlapi.model.parameters.ChangeApplied;

public interface IGoalBase {

	public final static String MAIA_NS = "http://www.agentlab.ru/ontologies/maia#";

	public final static IRI DESIRED_ANNOTATION_IRI = IRI.create(MAIA_NS + "Desired");

	ChangeApplied add(OWLIndividualAxiom axiom);

	ChangeApplied addAll(OWLIndividualAxiom... axioms);

	ChangeApplied addAll(Set<OWLIndividualAxiom> axioms);

	ChangeApplied addAll(Stream<OWLIndividualAxiom> axioms);

	ChangeApplied remove(OWLIndividualAxiom event);

	ChangeApplied removeAll(OWLIndividualAxiom... axioms);

	ChangeApplied removeAll(Set<OWLIndividualAxiom> axioms);

	ChangeApplied removeAll(Stream<OWLIndividualAxiom> axioms);

	ChangeApplied clean();

}
