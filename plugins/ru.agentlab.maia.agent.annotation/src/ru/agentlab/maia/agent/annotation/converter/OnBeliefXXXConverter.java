package ru.agentlab.maia.agent.annotation.converter;

import static org.hamcrest.CoreMatchers.equalTo;
import static ru.agentlab.maia.belief.filter.Matchers.hasClassExpression;
import static ru.agentlab.maia.belief.filter.Matchers.hasIRI;
import static ru.agentlab.maia.belief.filter.Matchers.hasIndividual;
import static ru.agentlab.maia.belief.filter.Matchers.hasObject;
import static ru.agentlab.maia.belief.filter.Matchers.hasProperty;
import static ru.agentlab.maia.belief.filter.Matchers.hasSubject;
import static ru.agentlab.maia.belief.filter.Matchers.isBooleanLiteral;
import static ru.agentlab.maia.belief.filter.Matchers.isDataProperty;
import static ru.agentlab.maia.belief.filter.Matchers.isDoubleLiteral;
import static ru.agentlab.maia.belief.filter.Matchers.isFloatLiteral;
import static ru.agentlab.maia.belief.filter.Matchers.isIndividual;
import static ru.agentlab.maia.belief.filter.Matchers.isIntegerLiteral;
import static ru.agentlab.maia.belief.filter.Matchers.isLiteral;
import static ru.agentlab.maia.belief.filter.Matchers.isNamedClass;
import static ru.agentlab.maia.belief.filter.Matchers.isNamedIndividual;
import static ru.agentlab.maia.belief.filter.Matchers.isObjectProperty;
import static ru.agentlab.maia.belief.filter.Matchers.isPlainLiteral;
import static ru.agentlab.maia.belief.filter.Matchers.isTypedLiteral;
import static ru.agentlab.maia.filter.impl.PlanEventFilters.allOf;
import static ru.agentlab.maia.filter.impl.PlanEventFilters.anything;
import static ru.agentlab.maia.filter.impl.PlanEventFilters.hamcrest;
import static ru.agentlab.maia.filter.impl.PlanEventFilters.var;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedObject;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.vocab.Namespaces;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import ru.agentlab.maia.agent.annotation.AxiomType;
import ru.agentlab.maia.converter.ConverterException;
import ru.agentlab.maia.converter.IPlanEventFilterConverter;
import ru.agentlab.maia.converter.Util;
import ru.agentlab.maia.filter.IPlanEventFilter;

public class OnBeliefXXXConverter implements IPlanEventFilterConverter {

	private static final String INF_NEG = "-INF";

	private static final String INF = "INF";

	private static final String NaN = "NaN";

	protected static final String VALUE = "value";

	protected static final String TYPE = "type";

	private static final Set<String> BUILDIN_DATATYPE_NAMESPACES = ImmutableSet.of(
		// @formatter:off
		Namespaces.OWL.toString(),
		Namespaces.RDF.toString(),
		Namespaces.RDFS.toString(),
		Namespaces.XSD.toString()
		// @formatter:on
	);

	PrefixManager prefixManager = new DefaultPrefixManager();

	@SuppressWarnings("unchecked")
	@Override
	public IPlanEventFilter<?> getMatcher(Object role, Method method, Annotation annotation,
			Map<String, Object> customData) {
		Annotation ann = annotation;
		AxiomType type = Util.getMethodActualValue(ann, TYPE, AxiomType.class);
		String[] args = Util.getMethodActualValue(ann, VALUE, String[].class);
		checkLength(args, type.getArity());
		// int length = args.length;
		try {
			switch (type) {
			case ANNOTATION_ASSERTION:
				return null;
			case ANNOTATION_PROPERTY_DOMAIN:
				return null;
			case ANNOTATION_PROPERTY_RANGE:
				return null;
			case ASYMMETRIC_OBJECT_PROPERTY:
				return null;
			case CLASS_ASSERTION:
				return allOf(Lists.newArrayList(hasClassExpression(isNamedClass(hasName(args[1]))),
						hasIndividual(isNamedIndividual(hasName(args[0])))));
			case DATATYPE_DEFINITION:
				return null;
			case DATA_PROPERTY_ASSERTION:
				return allOf(Lists.newArrayList(hasSubject(isNamedIndividual(hasName(args[0]))),
						hasProperty(isDataProperty(hasName(args[1]))),
						hasObject(isLiteral(getOWLLiteralMatcher(args[2])))));
			case DATA_PROPERTY_DOMAIN:
				return null;
			case DATA_PROPERTY_RANGE:
				return null;
			case DECLARATION:
				return null;
			case DIFFERENT_INDIVIDUALS:
				// Matcher<? super OWLIndividual>[] differentIndividuals = new
				// Matcher[length];
				// for (int i = 0; i < length; i++) {
				// differentIndividuals[i] =
				// isNamedIndividual(hasName(args[i]));
				// }
				// return hasDifferentIndividuals(differentIndividuals);
				return null;
			case DISJOINT_CLASSES:
				// Matcher<? super OWLClassExpression>[] disjointClasses = new
				// Matcher[length];
				// for (int i = 0; i < length; i++) {
				// disjointClasses[i] = isNamedClass(hasName(args[i]));
				// }
				// return hasDisjointClasses(disjointClasses);
				return null;
			case DISJOINT_DATA_PROPERTIES:
				return null;
			case DISJOINT_OBJECT_PROPERTIES:
				return null;
			case DISJOINT_UNION:
				return null;
			case EQUIVALENT_CLASSES:
				return null;
			case EQUIVALENT_DATA_PROPERTIES:
				return null;
			case EQUIVALENT_OBJECT_PROPERTIES:
				return null;
			case FUNCTIONAL_DATA_PROPERTY:
				return null;
			case FUNCTIONAL_OBJECT_PROPERTY:
				return null;
			case HAS_KEY:
				return null;
			case INVERSE_FUNCTIONAL_OBJECT_PROPERTY:
				return null;
			case INVERSE_OBJECT_PROPERTIES:
				return null;
			case IRREFLEXIVE_OBJECT_PROPERTY:
				return null;
			case NEGATIVE_DATA_PROPERTY_ASSERTION:
				return null;
			case NEGATIVE_OBJECT_PROPERTY_ASSERTION:
				return null;
			case OBJECT_PROPERTY_ASSERTION:
				return allOf(Lists.newArrayList(hasSubject(isNamedIndividual(hasName(args[0]))),
						hasProperty(isObjectProperty(hasName(args[1]))),
						hasObject(isIndividual(isNamedIndividual(hasName(args[2]))))));
			case OBJECT_PROPERTY_DOMAIN:
				return null;
			case OBJECT_PROPERTY_RANGE:
				return null;
			case REFLEXIVE_OBJECT_PROPERTY:
				return null;
			case SAME_INDIVIDUAL:
				return null;
			case SUBCLASS_OF:
				return null;
			case SUB_ANNOTATION_PROPERTY_OF:
				return null;
			case SUB_DATA_PROPERTY:
				return null;
			case SUB_OBJECT_PROPERTY:
				return null;
			case SUB_PROPERTY_CHAIN_OF:
				return null;
			case SWRL_RULE:
				throw new UnsupportedOperationException();
			case SYMMETRIC_OBJECT_PROPERTY:
				return null;
			case TRANSITIVE_OBJECT_PROPERTY:
				return null;
			default:
				return null;
			}
		} catch (LiteralFormatException e) {
			throw new ConverterException(e);
		}
	}

	/**
	 * 
	 * @param string
	 * @param variables
	 * @return
	 * @throws LiteralFormatException
	 */
	protected IPlanEventFilter<? super OWLLiteral> getOWLLiteralMatcher(String string) throws LiteralFormatException {
		String[] parts = Util.splitDatatypeLiteral(string);
		String literal = parts[0];
		String language = parts[1];
		String datatype = parts[2];

		if (language != null) {
			return isPlainLiteral(hasString(literal), hasString(language));
		}
		if (datatype != null) {
			if (Util.isVariable(datatype)) {
				return isTypedLiteral(hasString(literal), var(Util.getVariableName(datatype)));
			} else {
				IRI datatypeIRI = prefixManager.getIRI(datatype);
				String datatypeNamespace = datatypeIRI.getNamespace();
				if (BUILDIN_DATATYPE_NAMESPACES.contains(datatypeNamespace)) {
					if (!OWL2Datatype.isBuiltIn(datatypeIRI)) {
						throw new LiteralWrongBuildInDatatypeException("Literal [" + string
								+ "] has wrong format. Ontology [" + datatypeNamespace
								+ "] does not contain build-in datatype [" + datatypeIRI.toQuotedString() + "]");
					}
					OWL2Datatype owl2datatype = OWL2Datatype.getDatatype(datatypeIRI);
					if (!Util.isVariable(literal) && !owl2datatype.isInLexicalSpace(literal)) {
						throw new LiteralNotInLexicalSpaceException("Literal [" + string + "] has wrong format. Value ["
								+ literal + "] is not in lexical space of datatype [" + datatypeIRI.toQuotedString()
								+ "]");
					}
					switch (owl2datatype) {
					case XSD_BOOLEAN:
						return isBooleanLiteral(hasBoolean(literal));
					case XSD_FLOAT:
						return isFloatLiteral(hasFloat(literal));
					case XSD_DOUBLE:
						return isDoubleLiteral(hasDouble(literal));
					case XSD_INT:
					case XSD_INTEGER:
						return isIntegerLiteral(hasInteger(literal));
					case RDF_PLAIN_LITERAL:
						return isPlainLiteral(hamcrest(equalTo(literal)), hamcrest(equalTo(language)));
					default:
						return isTypedLiteral(hasString(literal), hasIRI(datatypeIRI));
					}
				} else {
					return isTypedLiteral(hasString(literal), hasIRI(datatypeIRI));
				}
			}
		}
		return isPlainLiteral(hasString(literal), anything());
	}

	private IPlanEventFilter<? super OWLNamedObject> hasName(String string) {
		if (Util.isVariable(string)) {
			return var(Util.getVariableName(string));
		} else {
			return hasIRI(prefixManager.getIRI(string));
		}
	}

	private IPlanEventFilter<? super String> hasString(String string) {
		if (string == null) {
			return anything();
		}
		if (Util.isVariable(string)) {
			return var(Util.getVariableName(string));
		} else {
			return hamcrest(equalTo(string));
		}
	}

	private IPlanEventFilter<? super Boolean> hasBoolean(String string) throws LiteralNotInValueSpaceException {
		if (string == null) {
			return anything();
		}
		if (Util.isVariable(string)) {
			return var(Util.getVariableName(string));
		} else {
			boolean value;
			if (string.equals("true") || string.equals("1")) {
				value = true;
			} else if (string.equals("false") || string.equals("0")) {
				value = false;
			} else {
				throw new LiteralNotInValueSpaceException("Argument should be [true|false|1|0]");
			}
			return hamcrest(equalTo(value));
		}
	}

	private IPlanEventFilter<? super Float> hasFloat(String string) throws LiteralNotInValueSpaceException {
		if (string == null) {
			return anything();
		}
		if (Util.isVariable(string)) {
			return var(Util.getVariableName(string));
		} else {
			float value;
			if (string.equals(NaN)) {
				value = Float.NaN;
			} else if (string.equals(INF)) {
				value = Float.POSITIVE_INFINITY;
			} else if (string.equals(INF_NEG)) {
				value = Float.NEGATIVE_INFINITY;
			} else {
				value = Float.parseFloat(string);
			}
			return hamcrest(equalTo(value));
		}
	}

	private IPlanEventFilter<? super Double> hasDouble(String string) throws LiteralNotInValueSpaceException {
		if (string == null) {
			return anything();
		}
		if (Util.isVariable(string)) {
			return var(Util.getVariableName(string));
		} else {
			double value;
			if (string.equals(NaN)) {
				value = Double.NaN;
			}
			if (string.equals(INF)) {
				value = Double.POSITIVE_INFINITY;
			}
			if (string.equals(INF_NEG)) {
				value = Double.NEGATIVE_INFINITY;
			} else {
				value = Double.parseDouble(string);
			}
			return hamcrest(equalTo(value));
		}
	}

	private IPlanEventFilter<? super Integer> hasInteger(String string) throws LiteralNotInValueSpaceException {
		if (string == null) {
			return anything();
		}
		if (Util.isVariable(string)) {
			return var(Util.getVariableName(string));
		} else {
			return hamcrest(equalTo(Integer.parseInt(string)));
		}
	}

	private void checkLength(String[] args, int length) {
		if (length == -1) {
			return;
		}
		if (args.length != length) {
			throw new ConverterException(
					"Initial goal for Annotation assertion should contain " + length + " arguments");
		}
	}

}
