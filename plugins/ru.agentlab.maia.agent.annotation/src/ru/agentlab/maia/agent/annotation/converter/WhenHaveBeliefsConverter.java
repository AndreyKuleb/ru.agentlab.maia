package ru.agentlab.maia.agent.annotation.converter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.semanticweb.owlapi.model.PrefixManager;

import de.derivo.sparqldlapi.QueryArgument;
import de.derivo.sparqldlapi.QueryAtom;
import de.derivo.sparqldlapi.QueryResult;
import de.derivo.sparqldlapi.Var;
import de.derivo.sparqldlapi.impl.QueryAtomGroupImpl;
import de.derivo.sparqldlapi.impl.QueryImpl;
import de.derivo.sparqldlapi.types.QueryAtomType;
import de.derivo.sparqldlapi.types.QueryType;
import ru.agentlab.maia.agent.annotation.AxiomType;
import ru.agentlab.maia.agent.annotation.WhenHaveBelief;
import ru.agentlab.maia.agent.annotation.WhenHaveBeliefs;
import ru.agentlab.maia.belief.filter.HaveBeliefsStateMatcher;
import ru.agentlab.maia.container.IInjector;
import ru.agentlab.maia.container.InjectorException;
import ru.agentlab.maia.converter.ConverterException;
import ru.agentlab.maia.converter.IPlanStateFilterConverter;
import ru.agentlab.maia.converter.Util;
import ru.agentlab.maia.filter.IPlanStateFilter;

public class WhenHaveBeliefsConverter implements IPlanStateFilterConverter {

	protected static final String VALUE = "value";

	protected static final String TYPE = "type";

	@Inject
	IInjector injector;

	@Inject
	PrefixManager prefixes;

	@Override
	public IPlanStateFilter getMatcher(Object role, Method method, Annotation annotation,
			Map<String, Object> customData) {
		WhenHaveBeliefs whenHaveBeliefs = (WhenHaveBeliefs) annotation;
		WhenHaveBelief[] annotations = whenHaveBeliefs.value();
		QueryImpl query = new QueryImpl(getQueryType(method));
		QueryAtomGroupImpl queryAtomGroup = new QueryAtomGroupImpl();
		query.addAtomGroup(queryAtomGroup);
		for (WhenHaveBelief haveBelief : annotations) {
			QueryAtomType type = getQueryType(haveBelief);
			List<QueryArgument> arguments = new ArrayList<>();
			for (String arg : haveBelief.value()) {
				if (Util.isVariable(arg)) {
					QueryArgument queryArgument = new QueryArgument(new Var(arg.substring(1)));
					arguments.add(queryArgument);
					if (!query.isAsk()) {
						query.addResultVar(queryArgument);
					}
				}
				arguments.add(new QueryArgument(prefixes.getIRI(arg)));
			}
			QueryAtom atom = new QueryAtom(type, arguments);
			queryAtomGroup.addAtom(atom);
		}
		try {
			HaveBeliefsStateMatcher haveBeliefMatcher = new HaveBeliefsStateMatcher(query);
			injector.inject(haveBeliefMatcher);
			injector.invoke(haveBeliefMatcher, PostConstruct.class);
			return haveBeliefMatcher;
		} catch (InjectorException e) {
			throw new ConverterException(e);
		}
	}

	public QueryAtomType getQueryType(Annotation ann) throws ConverterException {
		AxiomType type = Util.getMethodActualValue(ann, TYPE, AxiomType.class);
		String[] args = Util.getMethodActualValue(ann, VALUE, String[].class);
		checkLength(args, type.getArity());
		switch (type) {
		case ANNOTATION_ASSERTION:
			return QueryAtomType.ANNOTATION_PROPERTY;
		case ANNOTATION_PROPERTY_DOMAIN:
			return QueryAtomType.DOMAIN;
		case ANNOTATION_PROPERTY_RANGE:
			return QueryAtomType.RANGE;
		case ASYMMETRIC_OBJECT_PROPERTY:
			throw new UnsupportedOperationException();
		case CLASS_ASSERTION:
			return QueryAtomType.TYPE;
		case DATATYPE_DEFINITION:
			throw new UnsupportedOperationException();
		case DATA_PROPERTY_ASSERTION:
			return QueryAtomType.DATA_PROPERTY;
		case DATA_PROPERTY_DOMAIN:
			return QueryAtomType.DOMAIN;
		case DATA_PROPERTY_RANGE:
			return QueryAtomType.RANGE;
		case DECLARATION:
			throw new UnsupportedOperationException();
		case DIFFERENT_INDIVIDUALS:
			return QueryAtomType.DIFFERENT_FROM;
		case DISJOINT_CLASSES:
			return QueryAtomType.DISJOINT_WITH;
		case DISJOINT_DATA_PROPERTIES:
			return QueryAtomType.DISJOINT_WITH;
		case DISJOINT_OBJECT_PROPERTIES:
			return QueryAtomType.DISJOINT_WITH;
		case DISJOINT_UNION:
			return QueryAtomType.DISJOINT_WITH;
		case EQUIVALENT_CLASSES:
			return QueryAtomType.EQUIVALENT_CLASS;
		case EQUIVALENT_DATA_PROPERTIES:
			return QueryAtomType.EQUIVALENT_PROPERTY;
		case EQUIVALENT_OBJECT_PROPERTIES:
			return QueryAtomType.EQUIVALENT_PROPERTY;
		case FUNCTIONAL_DATA_PROPERTY:
			return QueryAtomType.FUNCTIONAL;
		case FUNCTIONAL_OBJECT_PROPERTY:
			return QueryAtomType.FUNCTIONAL;
		case HAS_KEY:
			throw new UnsupportedOperationException();
		case INVERSE_FUNCTIONAL_OBJECT_PROPERTY:
			return QueryAtomType.INVERSE_FUNCTIONAL;
		case INVERSE_OBJECT_PROPERTIES:
			return QueryAtomType.INVERSE_OF;
		case IRREFLEXIVE_OBJECT_PROPERTY:
			return QueryAtomType.IRREFLEXIVE;
		case NEGATIVE_DATA_PROPERTY_ASSERTION:
			throw new UnsupportedOperationException();
		case NEGATIVE_OBJECT_PROPERTY_ASSERTION:
			throw new UnsupportedOperationException();
		case OBJECT_PROPERTY_ASSERTION:
			return QueryAtomType.OBJECT_PROPERTY;
		case OBJECT_PROPERTY_DOMAIN:
			return QueryAtomType.DOMAIN;
		case OBJECT_PROPERTY_RANGE:
			return QueryAtomType.RANGE;
		case REFLEXIVE_OBJECT_PROPERTY:
			return QueryAtomType.REFLEXIVE;
		case SAME_INDIVIDUAL:
			return QueryAtomType.SAME_AS;
		case SUBCLASS_OF:
			return QueryAtomType.SUB_CLASS_OF;
		case SUB_ANNOTATION_PROPERTY_OF:
			return QueryAtomType.SUB_PROPERTY_OF;
		case SUB_DATA_PROPERTY:
			return QueryAtomType.SUB_PROPERTY_OF;
		case SUB_OBJECT_PROPERTY:
			return QueryAtomType.SUB_PROPERTY_OF;
		case SUB_PROPERTY_CHAIN_OF:
			throw new UnsupportedOperationException();
		case SWRL_RULE:
			throw new UnsupportedOperationException();
		case SYMMETRIC_OBJECT_PROPERTY:
			return QueryAtomType.SYMMETRIC;
		case TRANSITIVE_OBJECT_PROPERTY:
			return QueryAtomType.TRANSITIVE;
		default:
			throw new UnsupportedOperationException();
		}
	}

	private void checkLength(String[] args, int length) throws ConverterException {
		if (length == -1) {
			return;
		}
		if (args.length != length) {
			throw new ConverterException("Initial goal for Annotation assertion should contain 3 arguments");
		}
	}

	private QueryType getQueryType(Method method) {
		for (Parameter parameter : method.getParameters()) {
			if (parameter.getType() == QueryResult.class) {
				return QueryType.SELECT;
			}
		}
		return QueryType.ASK;
	}

}
