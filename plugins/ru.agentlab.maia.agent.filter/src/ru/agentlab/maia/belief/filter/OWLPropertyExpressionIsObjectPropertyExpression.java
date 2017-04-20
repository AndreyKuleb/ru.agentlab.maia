package ru.agentlab.maia.belief.filter;

import java.util.Map;

import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLPropertyExpression;

import ru.agentlab.maia.filter.IPlanEventFilter;
import ru.agentlab.maia.filter.TypeSafeEventFilter;

public class OWLPropertyExpressionIsObjectPropertyExpression extends TypeSafeEventFilter<OWLPropertyExpression> {

	IPlanEventFilter<? super OWLObjectProperty> matcher;

	public OWLPropertyExpressionIsObjectPropertyExpression(IPlanEventFilter<? super OWLObjectProperty> matcher) {
		super();
		this.matcher = matcher;
	}

	@Override
	protected boolean matchesSafely(OWLPropertyExpression property, Map<String, Object> values) {
		return property.isObjectPropertyExpression()
				&& matcher.matches(((OWLObjectPropertyExpression) property).asOWLObjectProperty(), values);
	}

}
