package ru.agentlab.maia.belief.filter;

import java.util.Map;

import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;

import ru.agentlab.maia.filter.IPlanEventFilter;
import ru.agentlab.maia.filter.TypeSafeEventFilter;

public class OWLDisjointClassesAxiomHasClasses extends TypeSafeEventFilter<OWLDisjointClassesAxiom> {

	IPlanEventFilter<Iterable<? extends OWLClassExpression>> anyOrderMatcher;

	public OWLDisjointClassesAxiomHasClasses(IPlanEventFilter<? super OWLClassExpression>[] matchers) {
		super();
//		this.anyOrderMatcher = IsIterableContainingInAnyOrder.containsInAnyOrder(matchers);
	}

	@Override
	protected boolean matchesSafely(OWLDisjointClassesAxiom axiom, Map<String, Object> values) {
		return anyOrderMatcher.matches(axiom.getClassExpressions(), values);
	}

}
