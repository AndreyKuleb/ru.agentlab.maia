package ru.agentlab.maia.belief.filter;

import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.equalTo;
import static ru.agentlab.maia.filter.impl.PlanEventFilters.hamcrest;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLNamedObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLPropertyAssertionObject;
import org.semanticweb.owlapi.model.OWLPropertyExpression;

import ru.agentlab.maia.filter.IPlanEventFilter;

public class Matchers {

	public static IPlanEventFilter<OWLDisjointClassesAxiom> hasDisjointClasses(
			IPlanEventFilter<? super OWLClassExpression>[] matchers) {
		return new OWLDisjointClassesAxiomHasClasses(matchers);
	}

	public static IPlanEventFilter<OWLDifferentIndividualsAxiom> hasDifferentIndividuals(
			IPlanEventFilter<? super OWLIndividual>[] matchers) {
		return new OWLDifferentIndividualsAxiomHasIndividuals(matchers);
	}

	public static IPlanEventFilter<OWLClassExpression> isNamedClass(IPlanEventFilter<? super OWLClass> IEventMatcher) {
		return new OWLClassExpressionIsClass(IEventMatcher);
	}

	public static IPlanEventFilter<OWLClassAssertionAxiom> hasClassExpression(
			IPlanEventFilter<? super OWLClassExpression> IEventMatcher) {
		return new OWLClassAssertionAxiomHasClassExpression(IEventMatcher);
	}

	public static IPlanEventFilter<OWLClassAssertionAxiom> hasIndividual(
			IPlanEventFilter<? super OWLIndividual> IEventMatcher) {
		return new OWLClassAssertionAxiomHasIndividual(IEventMatcher);
	}

	public static IPlanEventFilter<OWLIndividual> isNamedIndividual(
			IPlanEventFilter<? super OWLNamedIndividual> IEventMatcher) {
		return new OWLIndividualIsNamed(IEventMatcher);
	}

	public static IPlanEventFilter<OWLIndividual> isNamed() {
		return new OWLIndividualIsNamed(hamcrest(anything()));
	}

	public static IPlanEventFilter<OWLPropertyAssertionAxiom<?, ?>> hasSubject(
			IPlanEventFilter<? super OWLIndividual> IEventMatcher) {
		return new OWLPropertyAssertionAxiomHasSubject(IEventMatcher);
	}

	public static IPlanEventFilter<OWLPropertyAssertionAxiom<?, ?>> hasObject(
			IPlanEventFilter<? super OWLPropertyAssertionObject> IEventMatcher) {
		return new OWLPropertyAssertionAxiomHasObject(IEventMatcher);
	}

	public static IPlanEventFilter<OWLPropertyExpression> isObjectProperty(
			IPlanEventFilter<? super OWLObjectProperty> IEventMatcher) {
		return new OWLPropertyExpressionIsObjectPropertyExpression(IEventMatcher);
	}

	public static IPlanEventFilter<OWLPropertyExpression> isDataProperty(
			IPlanEventFilter<? super OWLDataProperty> IEventMatcher) {
		return new OWLPropertyExpressionIsDataPropertyExpression(IEventMatcher);
	}

	public static IPlanEventFilter<OWLPropertyAssertionAxiom<?, ?>> hasProperty(
			IPlanEventFilter<? super OWLPropertyExpression> IEventMatcher) {
		return new OWLPropertyAssertionAxiomHasProperty(IEventMatcher);
	}

	public static IPlanEventFilter<OWLNamedObject> hasIRI(IPlanEventFilter<? super IRI> IEventMatcher) {
		return new OWLNamedObjectHasIRI(IEventMatcher);
	}

	public static IPlanEventFilter<OWLNamedObject> hasIRI(IRI iri) {
		return hasIRI(hamcrest(equalTo(iri)));
	}

	public static IPlanEventFilter<OWLNamedObject> hasIRI(String iri) {
		return hasIRI(hamcrest(equalTo(IRI.create(iri))));
	}

	public static IPlanEventFilter<OWLNamedObject> hasIRI(String namespace, String name) {
		return hasIRI(hamcrest(equalTo(IRI.create(namespace, name))));
	}

	public static IPlanEventFilter<OWLPropertyAssertionObject> isLiteral(IPlanEventFilter<? super OWLLiteral> IEventMatcher) {
		return new OWLPropertyAssertionObjectIsLiteral(IEventMatcher);
	}

	public static IPlanEventFilter<OWLPropertyAssertionObject> isIndividual(
			IPlanEventFilter<? super OWLIndividual> IEventMatcher) {
		return new OWLPropertyAssertionObjectIsIndividual(IEventMatcher);
	}

	public static IPlanEventFilter<OWLLiteral> isBooleanLiteral(IPlanEventFilter<? super Boolean> IEventMatcher) {
		return new OWLLiteralIsBoolean(IEventMatcher);
	}

	public static IPlanEventFilter<OWLLiteral> isBooleanLiteral(boolean value) {
		return new OWLLiteralIsBoolean(hamcrest(equalTo(value)));
	}

	public static IPlanEventFilter<OWLLiteral> isFloatLiteral(IPlanEventFilter<? super Float> IEventMatcher) {
		return new OWLLiteralIsFloat(IEventMatcher);
	}

	public static IPlanEventFilter<OWLLiteral> isFloatLiteral(float value) {
		return new OWLLiteralIsFloat(hamcrest(equalTo(value)));
	}

	public static IPlanEventFilter<OWLLiteral> isDoubleLiteral(IPlanEventFilter<? super Double> IEventMatcher) {
		return new OWLLiteralIsDouble(IEventMatcher);
	}

	public static IPlanEventFilter<OWLLiteral> isDoubleLiteral(double value) {
		return new OWLLiteralIsDouble(hamcrest(equalTo(value)));
	}

	public static IPlanEventFilter<OWLLiteral> isIntegerLiteral(IPlanEventFilter<? super Integer> IEventMatcher) {
		return new OWLLiteralIsInteger(IEventMatcher);
	}

	public static IPlanEventFilter<OWLLiteral> isTypedLiteral(IPlanEventFilter<? super String> valueMatcher,
			IPlanEventFilter<? super OWLDatatype> datatypeMatcher) {
		return new OWLLiteralIsTyped(valueMatcher, datatypeMatcher);
	}

	public static IPlanEventFilter<OWLLiteral> isPlainLiteral(IPlanEventFilter<? super String> valueMatcher,
			IPlanEventFilter<? super String> languageMatcher) {
		return new OWLLiteralIsPlain(valueMatcher, languageMatcher);
	}

	public static IPlanEventFilter<OWLLiteral> isPlain(IPlanEventFilter<? super String> valueMatcher) {
		return new OWLLiteralIsPlain(valueMatcher, hamcrest(anything()));
	}

}
