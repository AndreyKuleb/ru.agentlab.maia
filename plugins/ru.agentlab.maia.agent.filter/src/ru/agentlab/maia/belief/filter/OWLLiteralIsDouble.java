package ru.agentlab.maia.belief.filter;

import java.util.Map;

import org.semanticweb.owlapi.model.OWLLiteral;

import ru.agentlab.maia.filter.IPlanEventFilter;
import ru.agentlab.maia.filter.TypeSafeEventFilter;

public class OWLLiteralIsDouble extends TypeSafeEventFilter<OWLLiteral> {

	IPlanEventFilter<? super Double> matcher;

	public OWLLiteralIsDouble(IPlanEventFilter<? super Double> matcher) {
		super();
		this.matcher = matcher;
	}

	@Override
	protected boolean matchesSafely(OWLLiteral literal, Map<String, Object> values) {
		return literal.isDouble() && matcher.matches(literal.parseDouble(), values);
	}

}
