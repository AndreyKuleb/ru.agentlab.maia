package ru.agentlab.maia.converter;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Util {

	private static final String FULL_SUFFIX = ">";
	private static final String FULL_PREFIX = "<";
	private static final String VAR_PREFIX = "?";
	private static final String SEPARATOR_LANGUAGE = "@";
	private static final String SEPARATOR_DATATYPE = "^^";

	public static <T> T getMethodActualValue(Object object, String methodName, Class<T> clazz) {
		try {
			Method valueMethod = object.getClass().getMethod(methodName);
			Object result = valueMethod.invoke(object);
			return clazz.cast(result);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static <T> T getMethodDefaultValue(Class<? extends Annotation> annotationClass, String methodName, Class<T> clazz) {
		try {
			Method method = annotationClass.getMethod(methodName,(Class[])null);
			Object defaultValue = method.getDefaultValue();
			return clazz.cast(defaultValue);
		} catch (NoSuchMethodException | SecurityException | IllegalArgumentException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * <p>
	 * Splits input string into 3 parts:
	 * <ol>
	 * <li>Literal value template (required, but can be empty);
	 * <li>Literal language template (optional);
	 * <li>Literal datatype template (optional);
	 * </ol>
	 * <p>
	 * String should have format: {@code <value> ['@' <language>] ['^^' 
	 * <datatype>]}
	 * <p>
	 * For example for string: [<i>some string@en^^xsd:string</i>]
	 * <ol>
	 * <li><i>some string</i> - is a value;
	 * <li><i>en</i> - is a language;
	 * <li><i>xsd:string</i> - is a datatype;
	 * </ol>
	 * 
	 * @param string
	 *            input string
	 * @return string array containing value, language and datatype parts of
	 *         input string.
	 */
	public static String[] splitDatatypeLiteral(String string) {
		String value = string;
		String language = null;
		String datatype = null;
		int datatypeIndex = string.lastIndexOf(SEPARATOR_DATATYPE);
		if (datatypeIndex != -1) {
			value = string.substring(0, datatypeIndex);
			datatype = string.substring(datatypeIndex + SEPARATOR_DATATYPE.length(), string.length());
			return new String[] { value, language, datatype };
		}
		int languageIndex = value.lastIndexOf(SEPARATOR_LANGUAGE);
		if (languageIndex != -1) {
			value = value.substring(0, languageIndex);
			language = value.substring(languageIndex + SEPARATOR_LANGUAGE.length(), value.length());
			return new String[] { value, language, datatype };
		}
		return new String[] { value, language, datatype };
	}

	public static boolean isVariable(String string) {
		return string.startsWith(VAR_PREFIX);
	}

	public static String getVariableName(String string) {
		return string.substring(VAR_PREFIX.length());
	}

	public static boolean isFullIRI(String string) {
		return string.startsWith(FULL_PREFIX) && string.endsWith(FULL_SUFFIX);
	}

	public static String getFullIRI(String string) {
		return string.substring(FULL_PREFIX.length(), string.length() - FULL_SUFFIX.length());
	}

}
