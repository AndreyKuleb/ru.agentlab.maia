package ru.agentlab.maia.belief.filter;

import java.util.Map;

import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;

import ru.agentlab.maia.filter.IPlanEventFilter;
import ru.agentlab.maia.filter.TypeSafeEventFilter;

public class OWLDifferentIndividualsAxiomHasIndividuals extends TypeSafeEventFilter<OWLDifferentIndividualsAxiom> {

	IPlanEventFilter<Iterable<? extends OWLIndividual>> anyOrderMatcher;

	public OWLDifferentIndividualsAxiomHasIndividuals(IPlanEventFilter<? super OWLIndividual>[] matchers) {
		super();
//		this.anyOrderMatcher = IsIterableContainingInAnyOrder.containsInAnyOrder(matchers);
	}

	@Override
	protected boolean matchesSafely(OWLDifferentIndividualsAxiom axiom, Map<String, Object> values) {
		return anyOrderMatcher.matches(axiom.getIndividuals(), values);
	}

}
