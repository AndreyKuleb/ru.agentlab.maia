package ru.agentlab.maia.belief.filter;

import java.util.Map;

import org.semanticweb.owlapi.model.OWLClassExpression;

import ru.agentlab.maia.filter.TypeSafeEventFilter;

public class OWLClassExpressionIsThing extends TypeSafeEventFilter<OWLClassExpression> {

	@Override
	protected boolean matchesSafely(OWLClassExpression expression, Map<String, Object> values) {
		return expression.isOWLThing();
	}

}
