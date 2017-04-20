package ru.agentlab.maia.belief.filter;

import java.util.Map;

import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLLiteral;

import ru.agentlab.maia.filter.IPlanEventFilter;
import ru.agentlab.maia.filter.TypeSafeEventFilter;

public class OWLLiteralIsTyped extends TypeSafeEventFilter<OWLLiteral> {

	IPlanEventFilter<? super String> valueMatcher;

	IPlanEventFilter<? super OWLDatatype> datatypeMatcher;

	public OWLLiteralIsTyped(IPlanEventFilter<? super String> valueMatcher,
			IPlanEventFilter<? super OWLDatatype> datatypeMatcher) {
		super();
		this.valueMatcher = valueMatcher;
		this.datatypeMatcher = datatypeMatcher;
	}

	@Override
	protected boolean matchesSafely(OWLLiteral literal, Map<String, Object> values) {
		return literal.isRDFPlainLiteral() && valueMatcher.matches(literal.getLiteral(), values)
				&& datatypeMatcher.matches(literal.getDatatype(), values);
	}

}
