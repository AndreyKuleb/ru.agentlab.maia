package ru.agentlab.maia.belief.filter;

import java.util.Map;

import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLPropertyExpression;

import ru.agentlab.maia.filter.IPlanEventFilter;
import ru.agentlab.maia.filter.TypeSafeEventFilter;

public class OWLPropertyExpressionIsDataPropertyExpression extends TypeSafeEventFilter<OWLPropertyExpression> {

	IPlanEventFilter<? super OWLDataProperty> matcher;

	public OWLPropertyExpressionIsDataPropertyExpression(IPlanEventFilter<? super OWLDataProperty> matcher) {
		super();
		this.matcher = matcher;
	}

	@Override
	protected boolean matchesSafely(OWLPropertyExpression property, Map<String, Object> values) {
		return property.isDataPropertyExpression()
				&& matcher.matches(((OWLDataPropertyExpression) property).asOWLDataProperty(), values);
	}

}
