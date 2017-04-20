package ru.agentlab.maia.belief.filter;

import java.util.Map;

import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLPropertyAssertionAxiom;

import ru.agentlab.maia.filter.IPlanEventFilter;
import ru.agentlab.maia.filter.TypeSafeEventFilter;

public class OWLPropertyAssertionAxiomHasSubject extends TypeSafeEventFilter<OWLPropertyAssertionAxiom<?, ?>> {

	IPlanEventFilter<? super OWLIndividual> matcher;

	public OWLPropertyAssertionAxiomHasSubject(IPlanEventFilter<? super OWLIndividual> matcher) {
		super();
		this.matcher = matcher;
	}

	@Override
	protected boolean matchesSafely(OWLPropertyAssertionAxiom<?, ?> axiom, Map<String, Object> values) {
		return matcher.matches(axiom.getSubject(), values);
	}

}
