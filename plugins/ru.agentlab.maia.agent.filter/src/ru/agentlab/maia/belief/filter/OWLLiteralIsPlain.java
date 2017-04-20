package ru.agentlab.maia.belief.filter;

import java.util.Map;

import org.semanticweb.owlapi.model.OWLLiteral;

import ru.agentlab.maia.filter.IPlanEventFilter;
import ru.agentlab.maia.filter.TypeSafeEventFilter;

public class OWLLiteralIsPlain extends TypeSafeEventFilter<OWLLiteral> {

	IPlanEventFilter<? super String> valueMatcher;

	IPlanEventFilter<? super String> languageMatcher;

	public OWLLiteralIsPlain(IPlanEventFilter<? super String> valueMatcher,
			IPlanEventFilter<? super String> languageMatcher) {
		super();
		this.valueMatcher = valueMatcher;
		this.languageMatcher = languageMatcher;
	}

	@Override
	protected boolean matchesSafely(OWLLiteral literal, Map<String, Object> values) {
		return literal.isRDFPlainLiteral() && valueMatcher.matches(literal.getLiteral(), values)
				&& languageMatcher.matches(literal.getLang(), values);
	}

}
