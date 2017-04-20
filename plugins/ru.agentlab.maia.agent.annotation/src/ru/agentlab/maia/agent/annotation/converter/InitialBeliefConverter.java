package ru.agentlab.maia.agent.annotation.converter;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import ru.agentlab.maia.agent.annotation.AxiomType;
import ru.agentlab.maia.converter.ConverterException;
import ru.agentlab.maia.converter.Util;

public class InitialBeliefConverter {

	private static final String INF_NEG = "-INF";

	private static final String INF = "INF";

	private static final String NaN = "NaN";

	@Inject
	OWLOntologyManager manager;

	@Inject
	OWLDataFactory factory;

	@Inject
	PrefixManager prefixManager;

	public OWLAxiom getAxiom(Annotation ann) {
		AxiomType type = Util.getMethodActualValue(ann, "type", AxiomType.class);
		String[] args = Util.getMethodActualValue(ann, "value", String[].class);
		checkNoVariables(args);
		checkLength(args, type.getArity());
		switch (type) {
		// @formatter:off
		case ANNOTATION_ASSERTION:
			// TODO: value can be literal
			return factory.getOWLAnnotationAssertionAxiom(
					factory.getOWLAnnotationProperty(args[1], prefixManager), 
					prefixManager.getIRI(args[0]),
					prefixManager.getIRI(args[2]));
		case ANNOTATION_PROPERTY_DOMAIN:
			return factory.getOWLAnnotationPropertyDomainAxiom(
					factory.getOWLAnnotationProperty(args[0], prefixManager), 
					prefixManager.getIRI(args[1]));
		case ANNOTATION_PROPERTY_RANGE:
			return factory.getOWLAnnotationPropertyRangeAxiom(
					factory.getOWLAnnotationProperty(args[0], prefixManager), 
					prefixManager.getIRI(args[1]));
		case ASYMMETRIC_OBJECT_PROPERTY:
			return factory.getOWLAsymmetricObjectPropertyAxiom(
					factory.getOWLObjectProperty(args[0], prefixManager));
		case CLASS_ASSERTION:
			return factory.getOWLClassAssertionAxiom(
					factory.getOWLClass(args[0], prefixManager),
					factory.getOWLNamedIndividual(args[1], prefixManager));
		case DATATYPE_DEFINITION:
			// TODO:
			return factory.getOWLDatatypeDefinitionAxiom(
					factory.getOWLDatatype(args[0], prefixManager),
					factory.getOWLDatatype(args[1], prefixManager));
		case DATA_PROPERTY_ASSERTION:
			return factory.getOWLDataPropertyAssertionAxiom(
					factory.getOWLDataProperty(args[0], prefixManager),
					factory.getOWLNamedIndividual(args[1], prefixManager),
					getOWLLiteral(args[2]));
		case DATA_PROPERTY_DOMAIN:
			return factory.getOWLDataPropertyDomainAxiom(
					factory.getOWLDataProperty(args[0], prefixManager),
					factory.getOWLClass(args[1], prefixManager));
		case DATA_PROPERTY_RANGE:
			return factory.getOWLDataPropertyRangeAxiom(
					factory.getOWLDataProperty(args[0], prefixManager),
					factory.getOWLDatatype(args[1], prefixManager));
		case DIFFERENT_INDIVIDUALS:
			Set<OWLIndividual> differentIndividuals = new HashSet<>();
			for (String arg : args) {
				differentIndividuals.add(factory.getOWLNamedIndividual(arg, prefixManager));
			}
			return factory.getOWLDifferentIndividualsAxiom(differentIndividuals);
		case DISJOINT_CLASSES:
			Set<OWLClass> disjointClasses = new HashSet<>();
			for (String arg : args) {
				disjointClasses.add(factory.getOWLClass(arg, prefixManager));
			}
			return factory.getOWLDisjointClassesAxiom(disjointClasses);
		case DISJOINT_DATA_PROPERTIES:
			Set<OWLDataProperty> disjointDataProperties = new HashSet<>();
			for (String arg : args) {
				disjointDataProperties.add(factory.getOWLDataProperty(arg, prefixManager));
			}
			return factory.getOWLDisjointDataPropertiesAxiom(disjointDataProperties);
		case DISJOINT_OBJECT_PROPERTIES:
			Set<OWLObjectProperty> disjointObjectProperties = new HashSet<>();
			for (String arg : args) {
				disjointObjectProperties.add(factory.getOWLObjectProperty(arg, prefixManager));
			}
			return factory.getOWLDisjointObjectPropertiesAxiom(disjointObjectProperties);
		case DISJOINT_UNION:
			Set<OWLClassExpression> classExpressions = new HashSet<>();
			for (int i = 1; i < args.length; i++) {
				classExpressions.add(factory.getOWLClass(prefixManager.getIRI(args[i])));
			}
			return factory.getOWLDisjointUnionAxiom(factory.getOWLClass(args[0], prefixManager),
					classExpressions);
		case EQUIVALENT_CLASSES:
			Set<OWLClass> equivalentClasses = new HashSet<>();
			for (String arg : args) {
				equivalentClasses.add(factory.getOWLClass(arg, prefixManager));
			}
			return factory.getOWLEquivalentClassesAxiom(equivalentClasses);
		case EQUIVALENT_DATA_PROPERTIES:
			Set<OWLDataProperty> equivalentDataProperties = new HashSet<>();
			for (String arg : args) {
				equivalentDataProperties.add(factory.getOWLDataProperty(arg, prefixManager));
			}
			return factory.getOWLEquivalentDataPropertiesAxiom(equivalentDataProperties);
		case EQUIVALENT_OBJECT_PROPERTIES:
			Set<OWLObjectProperty> equivalentObjectProperties = new HashSet<>();
			for (String arg : args) {
				equivalentObjectProperties.add(factory.getOWLObjectProperty(arg, prefixManager));
			}
			return factory.getOWLEquivalentObjectPropertiesAxiom(equivalentObjectProperties);
		case FUNCTIONAL_DATA_PROPERTY:
			return factory.getOWLFunctionalDataPropertyAxiom(
					factory.getOWLDataProperty(args[0], prefixManager));
		case FUNCTIONAL_OBJECT_PROPERTY:
			return factory.getOWLFunctionalObjectPropertyAxiom(
					factory.getOWLObjectProperty(args[0], prefixManager));
		case INVERSE_FUNCTIONAL_OBJECT_PROPERTY:
			return factory.getOWLInverseFunctionalObjectPropertyAxiom(
					factory.getOWLObjectProperty(args[0], prefixManager));
		case INVERSE_OBJECT_PROPERTIES:
			return factory.getOWLInverseObjectPropertiesAxiom(
					factory.getOWLObjectProperty(args[0], prefixManager),
					factory.getOWLObjectProperty(args[1], prefixManager));
		case IRREFLEXIVE_OBJECT_PROPERTY:
			return factory.getOWLIrreflexiveObjectPropertyAxiom(
					factory.getOWLObjectProperty(args[0], prefixManager));
		case NEGATIVE_DATA_PROPERTY_ASSERTION:
			return factory.getOWLNegativeDataPropertyAssertionAxiom(
					factory.getOWLDataProperty(args[1], prefixManager),
					factory.getOWLNamedIndividual(args[0], prefixManager),
					getOWLLiteral(args[2]));
		case NEGATIVE_OBJECT_PROPERTY_ASSERTION:
			return factory.getOWLNegativeObjectPropertyAssertionAxiom(
					factory.getOWLObjectProperty(args[1], prefixManager),
					factory.getOWLNamedIndividual(args[0], prefixManager),
					factory.getOWLNamedIndividual(args[2], prefixManager));
		case OBJECT_PROPERTY_ASSERTION:
			return factory.getOWLObjectPropertyAssertionAxiom(
					factory.getOWLObjectProperty(args[1], prefixManager),
					factory.getOWLNamedIndividual(args[0], prefixManager),
					factory.getOWLNamedIndividual(args[2], prefixManager));
		case OBJECT_PROPERTY_DOMAIN:
			return factory.getOWLObjectPropertyDomainAxiom(
					factory.getOWLObjectProperty(args[0], prefixManager),
					factory.getOWLClass(args[1], prefixManager));
		case OBJECT_PROPERTY_RANGE:
			return factory.getOWLObjectPropertyRangeAxiom(
					factory.getOWLObjectProperty(args[0], prefixManager),
					factory.getOWLClass(args[1], prefixManager));
		case REFLEXIVE_OBJECT_PROPERTY:
			return factory.getOWLReflexiveObjectPropertyAxiom(
					factory.getOWLObjectProperty(args[0], prefixManager));
		case SAME_INDIVIDUAL:
			Set<OWLIndividual> sameIndividuals = new HashSet<>();
			for (String arg : args) {
				sameIndividuals.add(factory.getOWLNamedIndividual(arg, prefixManager));
			}
			return factory.getOWLSameIndividualAxiom(sameIndividuals);
		case SUBCLASS_OF:
			return factory.getOWLSubClassOfAxiom(
					factory.getOWLClass(args[0], prefixManager),
					factory.getOWLClass(args[1], prefixManager));
		case SUB_ANNOTATION_PROPERTY_OF:
			return factory.getOWLSubAnnotationPropertyOfAxiom(
					factory.getOWLAnnotationProperty(args[0], prefixManager),
					factory.getOWLAnnotationProperty(args[1], prefixManager));
		case SUB_DATA_PROPERTY:
			return factory.getOWLSubDataPropertyOfAxiom(
					factory.getOWLDataProperty(args[0], prefixManager),
					factory.getOWLDataProperty(args[1], prefixManager));
		case SUB_OBJECT_PROPERTY:
			return factory.getOWLSubObjectPropertyOfAxiom(
					factory.getOWLObjectProperty(args[0], prefixManager),
					factory.getOWLObjectProperty(args[1], prefixManager));
		case SUB_PROPERTY_CHAIN_OF:
			List<OWLObjectProperty> chainObjectProperties = new ArrayList<>();
			for (int i = 0; i < args.length-1; i++) {
				chainObjectProperties.add(factory.getOWLObjectProperty(prefixManager.getIRI(args[i])));
			}
			return factory.getOWLSubPropertyChainOfAxiom(
					chainObjectProperties,
					factory.getOWLObjectProperty(prefixManager.getIRI(args[args.length - 1])));
		case SYMMETRIC_OBJECT_PROPERTY:
			return factory.getOWLSymmetricObjectPropertyAxiom(
					factory.getOWLObjectProperty(args[0], prefixManager));
		case TRANSITIVE_OBJECT_PROPERTY:
			return factory.getOWLTransitiveObjectPropertyAxiom(
					factory.getOWLObjectProperty(args[0], prefixManager));
		case DECLARATION:
		case HAS_KEY:
		case SWRL_RULE:
		default:
			throw new UnsupportedOperationException();
		}
		// @formatter:on
	}

	private OWLLiteral getOWLLiteral(String arg) {
		String[] parts = Util.splitDatatypeLiteral(arg);
		String literal = parts[0];
		checkNoVariable(literal);
		String language = parts[1];
		String datatype = parts[2];
		if (datatype != null) {
			checkNoVariable(datatype);
			IRI datatypeIRI = prefixManager.getIRI(datatype);
			if (OWL2Datatype.isBuiltIn(datatypeIRI)) {
				OWL2Datatype owl2datatype = OWL2Datatype.getDatatype(datatypeIRI);
				try {
					switch (owl2datatype) {
					case XSD_BOOLEAN:
						return factory.getOWLLiteral(getBoolean(literal));
					case XSD_FLOAT:
						return factory.getOWLLiteral(getFloat(literal));
					case XSD_DOUBLE:
						return factory.getOWLLiteral(getDouble(literal));
					case XSD_INT:
					case XSD_INTEGER:
						return factory.getOWLLiteral(getInteger(literal));
					case RDF_PLAIN_LITERAL:
					default:
						return factory.getOWLLiteral(literal, owl2datatype);
					}
				} catch (LiteralNotInValueSpaceException e) {
					throw new ConverterException(e);
				}
			}
		}
		if (language != null) {
			checkNoVariable(language);
			return factory.getOWLLiteral(literal, language);
		}
		return factory.getOWLLiteral(literal);
	}

	private boolean getBoolean(String string) throws LiteralNotInValueSpaceException {
		if (string.equalsIgnoreCase("true") || string.equals("1")) {
			return true;
		} else if (string.equalsIgnoreCase("false") || string.equals("0")) {
			return false;
		} else {
			throw new LiteralNotInValueSpaceException("Argument should be [true|false|1|0]");
		}
	}

	private float getFloat(String string) throws LiteralNotInValueSpaceException {
		if (string.equalsIgnoreCase(NaN)) {
			return Float.NaN;
		} else if (string.equalsIgnoreCase(INF)) {
			return Float.POSITIVE_INFINITY;
		} else if (string.equalsIgnoreCase(INF_NEG)) {
			return Float.NEGATIVE_INFINITY;
		} else {
			return Float.parseFloat(string);
		}
	}

	private double getDouble(String string) throws LiteralNotInValueSpaceException {
		if (string.equalsIgnoreCase(NaN)) {
			return Double.NaN;
		}
		if (string.equalsIgnoreCase(INF)) {
			return Double.POSITIVE_INFINITY;
		}
		if (string.equalsIgnoreCase(INF_NEG)) {
			return Double.NEGATIVE_INFINITY;
		} else {
			return Double.parseDouble(string);
		}
	}

	private int getInteger(String string) throws LiteralNotInValueSpaceException {
		return Integer.parseInt(string);
	}

	private void checkLength(String[] args, int length) {
		if (length == -1) {
			return;
		}
		if (args.length != length) {
			throw new ConverterException("Initial goal for Annotation assertion should contain 3 arguments");
		}
	}

	private void checkNoVariables(String[] args) {
		for (String arg : args) {
			checkNoVariable(arg);
		}
	}

	private void checkNoVariable(String arg) {
		if (arg.startsWith("?")) {
			throw new ConverterException("Initial goal should not contain variables");
		}
	}
}
