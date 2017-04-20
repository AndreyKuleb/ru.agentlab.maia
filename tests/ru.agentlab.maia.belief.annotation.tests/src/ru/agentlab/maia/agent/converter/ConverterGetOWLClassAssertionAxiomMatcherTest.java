/*******************************************************************************
 * Copyright (c) 2016 AgentLab.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package ru.agentlab.maia.agent.converter;

import java.lang.reflect.InvocationTargetException;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.FromDataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import ru.agentlab.maia.agent.annotation.converter.AnnotationFormatException;
import ru.agentlab.maia.annotation.Converter;

/**
 * 
 * Test cases:
 * URI = http://www.agentlab.ru/test/ontology#
 * <!-- @formatter:off -->
 * <table border="thin single black collapse">
 * 	<thead>
 * 		<tr><th rowspan="2">##<th>Input<th>Output
 * 		<tr><th>Parameter<th>Result
 * 	<thead>
 * 	<tbody>
 * 		<tr><td colspan="4">Negative tests
 * 		<tr><td>0  <td>"" 														<td>{@link AnnotationFormatException}
 * 		<tr><td>1  <td>"a" 														<td>{@link AnnotationFormatException}
 * 		<tr><td>2  <td>"a b c" 													<td>{@link AnnotationFormatException}
 * 		<tr><td>1  <td>"?a" 													<td>{@link AnnotationFormatException}
 * 		<tr><td>2  <td>"?a ?b ?c" 												<td>{@link AnnotationFormatException}
 * 
 * 		<tr><td colspan="4">Positive tests
 * 		<tr><td>0  <td>"?a b" 													<td>{@link AnnotationFormatException}
 * 		<tr><td>2  <td>"a ?b" 													<td>{@link AnnotationFormatException}
 * 		<tr><td>2  <td>"?a ?b" 													<td>{@link AnnotationFormatException}
 * 		<tr><td>2  <td>"?a &lt;http://www.w3.org/2002/07/owl#Class&gt;" 		<td>{@link AnnotationFormatException}
 * 		<tr><td>2  <td>"&lt;http://www.agentlab.ru/test/ontology#a&gt; ?b" 		<td>{@link AnnotationFormatException}
 * 		<tr><td>2  <td>"?a ?b" 													<td>{@link AnnotationFormatException}
 * 	</tbody>
 * </table>
 * <!-- @formatter:on -->
 * 
 * @author Dmitriy Shishkin <shishkindimon@gmail.com>
 */
@RunWith(Theories.class)
public class ConverterGetOWLClassAssertionAxiomMatcherTest {

	private static final String SEPARATOR = " ";

	// @formatter:off
	@DataPoints("all literals")
	public static String[] allLiterals() {
		Stream<String> legalFirstLiterals = Stream.of(legalFirstLiterals());
		Stream<String> illegalFirstLiterals = Stream.of(illegalFirstLiterals());
		Stream<String> legalSecondLiterals = Stream.of(legalSecondLiterals());
		Stream<String> illegalSecondLiterals = Stream.of(illegalSecondLiterals());
		 
		Stream<String> stream3 = Stream.concat(legalFirstLiterals, illegalFirstLiterals);
		Stream<String> stream4 = Stream.concat(legalSecondLiterals, illegalSecondLiterals);
		Stream<String> stream5 = Stream.concat(stream3, stream4);
		return stream5.distinct().toArray(String[]::new);
	}

	@DataPoints("legal first literals")
	public static String[] legalFirstLiterals() {
		return new String[] { 
			"a", // local individual short name
			"b", // local individual short name
			"?a", // individual variable
			"?b", // individual variable
			"<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>", 
			"<http://www.w3.org/2002/07/owl#Class>", 
			"<http://www.agentlab.ru/test/ontology#a>" // local individual full name
		};
	}

	@DataPoints("illegal first literals")
	public static String[] illegalFirstLiterals() {
		return new String[] { 
			"<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>", 
			"<http://www.w3.org/2002/07/owl#Class>", 
			"<http://www.agentlab.ru/test/ontology#a>" // local individual full name
		};
	}

	@DataPoints("legal second literals")
	public static String[] legalSecondLiterals() {
		return new String[] { 
			"a", // local individual short name
			"b", // local individual short name
			"?a", // individual variable
			"?b", // individual variable
			"<http://www.w3.org/2002/07/owl#Class>", 
			"owl:Class", 
			"<http://www.agentlab.ru/test/ontology#TestClass>"
		};
	}

	@DataPoints("illegal second literals")
	public static String[] illegalSecondLiterals() {
		return new String[] { 
			"<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>", 			// should be owl class, not object property
			"<http://www.w3.org/1999/02/22-rdf-syntax-ns#comment>", 		// should be owl class, not data property
			"rdf:type", 			// should be owl class, not object property
			"rdf:comment", 			// should be owl class, not data property
			
			"<http://www.agentlab.ru/test/ontology#TestObjectProperty>", 	// should be owl class, not object property
			"<http://www.agentlab.ru/test/ontology#TestDataProperty>", 		// should be owl class, not data property
			"<http://www.agentlab.ru/test/ontology#TestAnnotationProperty>",// should be owl class, not annotation property
			"TestObjectProperty", 		// should be owl class, not object property
			"TestDataProperty", 		// should be owl class, not data property
			"TestAnnotationProperty"	// should be owl class, not annotation property
		};
	}
	// @formatter:on

	static int i = 0;

	Converter converter = new Converter();

	@Theory
	public void anyEmptyLiteralShouldThrow()
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		System.out.println(i++ + "@BeliefAdded(\"\")");
		try {
			converter.getOWLClassAssertionAxiomMatcher("");
			Assert.fail("Empty parameter shold throw " + AnnotationFormatException.class.getName());
		} catch (AnnotationFormatException e) {
			Assert.assertNotNull(e);
		}
	}

	@Theory
	public void any1LiteralShouldThrow(@FromDataPoints("all literals") String firstLiteral)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		String parameter = firstLiteral;
		System.out.println(i++ + "@BeliefAdded(\"" + parameter + "\")");
		try {
			converter.getOWLClassAssertionAxiomMatcher(parameter);
			Assert.fail("Empty parameter shold throw " + AnnotationFormatException.class.getName());
		} catch (AnnotationFormatException e) {
			Assert.assertNotNull(e);
		}
	}

	@Theory
	public void any3LiteralShouldThrow(@FromDataPoints("all literals") String firstLiteral,
			@FromDataPoints("all literals") String secondLiteral, @FromDataPoints("all literals") String thirdLiteral)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		String parameter = firstLiteral + SEPARATOR + secondLiteral + SEPARATOR + thirdLiteral;
		System.out.println(i++ + "@BeliefAdded(\"" + parameter + "\")");
		try {
			converter.getOWLClassAssertionAxiomMatcher(parameter);
			Assert.fail("Empty parameter shold throw " + AnnotationFormatException.class.getName());
		} catch (AnnotationFormatException e) {
			Assert.assertNotNull(e);
		}
	}

}
