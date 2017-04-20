package ru.agentlab.maia.belief.filter;

import java.util.Map;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLNamedObject;

import ru.agentlab.maia.filter.IPlanEventFilter;
import ru.agentlab.maia.filter.TypeSafeEventFilter;

public class OWLNamedObjectHasIRI extends TypeSafeEventFilter<OWLNamedObject> {

	IPlanEventFilter<? super IRI> matcher;

	public OWLNamedObjectHasIRI(IPlanEventFilter<? super IRI> matcher) {
		super();
		this.matcher = matcher;
	}

	@Override
	protected boolean matchesSafely(OWLNamedObject named, Map<String, Object> values) {
		return matcher.matches(named.getIRI(), values);
	}

}
