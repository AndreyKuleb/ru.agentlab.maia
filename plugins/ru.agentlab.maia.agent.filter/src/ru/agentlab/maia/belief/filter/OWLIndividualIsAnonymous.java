package ru.agentlab.maia.belief.filter;

import java.util.Map;

import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLIndividual;

import ru.agentlab.maia.filter.IPlanEventFilter;
import ru.agentlab.maia.filter.TypeSafeEventFilter;

public class OWLIndividualIsAnonymous extends TypeSafeEventFilter<OWLIndividual> {

	IPlanEventFilter<? super OWLAnonymousIndividual> matcher;

	public OWLIndividualIsAnonymous(IPlanEventFilter<? super OWLAnonymousIndividual> matcher) {
		super();
		this.matcher = matcher;
	}

	@Override
	protected boolean matchesSafely(OWLIndividual individual, Map<String, Object> values) {
		return individual.isAnonymous() && matcher.matches(individual.asOWLAnonymousIndividual(), values);
	}

}
