package ru.agentlab.maia.belief.filter;

import java.util.Map;

import org.semanticweb.owlapi.model.OWLAnonymousClassExpression;
import org.semanticweb.owlapi.model.OWLClassExpression;

import ru.agentlab.maia.filter.IPlanEventFilter;
import ru.agentlab.maia.filter.TypeSafeEventFilter;

public class OWLClassExpressionIsAnonymous extends TypeSafeEventFilter<OWLClassExpression> {

	IPlanEventFilter<? super OWLAnonymousClassExpression> matcher;

	public OWLClassExpressionIsAnonymous(IPlanEventFilter<? super OWLAnonymousClassExpression> matcher) {
		super();
		this.matcher = matcher;
	}

	@Override
	protected boolean matchesSafely(OWLClassExpression expression, Map<String, Object> values) {
		return expression.isAnonymous() && matcher.matches(expression, values);
	}

}
