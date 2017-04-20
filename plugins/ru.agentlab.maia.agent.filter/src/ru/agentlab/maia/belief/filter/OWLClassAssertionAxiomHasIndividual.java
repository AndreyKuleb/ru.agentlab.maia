package ru.agentlab.maia.belief.filter;

import java.util.Map;

import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;

import ru.agentlab.maia.filter.IPlanEventFilter;
import ru.agentlab.maia.filter.TypeSafeEventFilter;

public class OWLClassAssertionAxiomHasIndividual extends TypeSafeEventFilter<OWLClassAssertionAxiom> {

	IPlanEventFilter<? super OWLIndividual> matcher;

	public OWLClassAssertionAxiomHasIndividual(IPlanEventFilter<? super OWLIndividual> matcher) {
		super();
		this.matcher = matcher;
	}

	@Override
	protected boolean matchesSafely(OWLClassAssertionAxiom axiom, Map<String, Object> values) {
		OWLIndividual subject = axiom.getIndividual();
		return subject.isNamed() && matcher.matches(subject.asOWLNamedIndividual(), values);
	}

}
