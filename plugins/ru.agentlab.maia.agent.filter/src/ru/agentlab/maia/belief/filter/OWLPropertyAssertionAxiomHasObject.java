package ru.agentlab.maia.belief.filter;

import java.util.Map;

import org.semanticweb.owlapi.model.OWLPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLPropertyAssertionObject;

import ru.agentlab.maia.filter.IPlanEventFilter;
import ru.agentlab.maia.filter.TypeSafeEventFilter;

public class OWLPropertyAssertionAxiomHasObject extends TypeSafeEventFilter<OWLPropertyAssertionAxiom<?, ?>> {

	IPlanEventFilter<? super OWLPropertyAssertionObject> matcher;

	public OWLPropertyAssertionAxiomHasObject(IPlanEventFilter<? super OWLPropertyAssertionObject> matcher) {
		super();
		this.matcher = matcher;
	}

	@Override
	protected boolean matchesSafely(OWLPropertyAssertionAxiom<?, ?> axiom, Map<String, Object> values) {
		return matcher.matches(axiom.getObject(), values);
	}

}
