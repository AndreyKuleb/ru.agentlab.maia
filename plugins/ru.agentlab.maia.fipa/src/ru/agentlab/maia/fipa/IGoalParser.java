package ru.agentlab.maia.fipa;

import org.semanticweb.owlapi.model.OWLIndividualAxiom;

@FunctionalInterface
public interface IGoalParser {

	OWLIndividualAxiom parse(String content);

}
