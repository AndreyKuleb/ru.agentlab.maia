package ru.agentlab.maia.agent.annotation.converter;

import static ru.agentlab.maia.belief.filter.Matchers.hasIRI;
import static ru.agentlab.maia.filter.impl.PlanEventFilters.var;

import org.semanticweb.owlapi.model.OWLNamedObject;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

import ru.agentlab.maia.converter.ConverterException;
import ru.agentlab.maia.converter.Util;
import ru.agentlab.maia.filter.IPlanEventFilter;

public abstract class TemplateConverter {

	protected static final String VALUE = "value";

	PrefixManager prefixManager = new DefaultPrefixManager();

	protected String[] split(String[] values) {
		if (values.length == 1) {
			return values[0].split(" ");
		}
		return values;
	}

	protected String[] merge(String[] actualValues, String[] defaultValues) {
		int diff = defaultValues.length - actualValues.length;
		if (diff < 0) {
			throw new ConverterException("Annotation assertion should contain " + actualValues.length + " arguments");
		}
		String[] result = new String[defaultValues.length];
		System.arraycopy(actualValues, 0, result, 0, actualValues.length);
		System.arraycopy(defaultValues, actualValues.length, result, actualValues.length, diff);
		return result;
	}

	protected IPlanEventFilter<? super OWLNamedObject> hasName(String string) {
		if (Util.isVariable(string)) {
			return var(Util.getVariableName(string));
		} else {
			return hasIRI(prefixManager.getIRI(string));
		}
	}

}