package ru.agentlab.maia.fipa;

import org.semanticweb.owlapi.model.OWLAxiom;

@FunctionalInterface
public interface IBeliefParser {

	OWLAxiom parse(String content);

}
