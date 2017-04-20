package ru.agentlab.maia.belief.filter;

import java.util.Map;

import org.semanticweb.owlapi.model.OWLPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLPropertyExpression;

import ru.agentlab.maia.filter.IPlanEventFilter;
import ru.agentlab.maia.filter.TypeSafeEventFilter;

public class OWLPropertyAssertionAxiomHasProperty extends TypeSafeEventFilter<OWLPropertyAssertionAxiom<?, ?>> {

	IPlanEventFilter<? super OWLPropertyExpression> matcher;

	public OWLPropertyAssertionAxiomHasProperty(IPlanEventFilter<? super OWLPropertyExpression> matcher) {
		super();
		this.matcher = matcher;
	}

	@Override
	protected boolean matchesSafely(OWLPropertyAssertionAxiom<?, ?> axiom, Map<String, Object> values) {
		return matcher.matches(axiom.getProperty(), values);
	}

}
