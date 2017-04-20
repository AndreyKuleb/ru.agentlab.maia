package ru.agentlab.maia.belief.filter;

import java.util.Map;

import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLPropertyAssertionObject;

import ru.agentlab.maia.filter.IPlanEventFilter;
import ru.agentlab.maia.filter.TypeSafeEventFilter;

public class OWLPropertyAssertionObjectIsLiteral extends TypeSafeEventFilter<OWLPropertyAssertionObject> {

	IPlanEventFilter<? super OWLLiteral> matcher;

	public OWLPropertyAssertionObjectIsLiteral(IPlanEventFilter<? super OWLLiteral> matcher) {
		super();
		this.matcher = matcher;
	}

	@Override
	protected boolean matchesSafely(OWLPropertyAssertionObject object, Map<String, Object> values) {
		return (object instanceof OWLLiteral) && matcher.matches((OWLLiteral) object, values);
	}

}
