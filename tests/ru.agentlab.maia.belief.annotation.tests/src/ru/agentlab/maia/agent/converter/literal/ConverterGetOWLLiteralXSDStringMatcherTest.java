/*******************************************************************************
 * Copyright (c) 2016 AgentLab.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package ru.agentlab.maia.agent.converter.literal;

import static org.hamcrest.Matchers.equalTo;
import static ru.agentlab.maia.belief.match.Matchers.hasIRI;
import static ru.agentlab.maia.belief.match.Matchers.isTypedLiteral;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import ru.agentlab.maia.agent.annotation.converter.LiteralIllelgalLanguageTagException;
import ru.agentlab.maia.agent.annotation.converter.LiteralWrongBuildInDatatypeException;
import ru.agentlab.maia.agent.converter.AbstractGetOWLLiteralMatcherTest;

/**
 * @author Dmitriy Shishkin <shishkindimon@gmail.com>
 */
@RunWith(Parameterized.class)
public class ConverterGetOWLLiteralXSDStringMatcherTest extends AbstractGetOWLLiteralMatcherTest {

	protected static int i = 0;

	// Name is not working because some of the test strings have \r\n symbols
	@Parameters // (name="When parameter is [{0}] then result is [{1}]")
	public static Collection<Object[]> data() {
		return Arrays
				.asList(new Object[][] {
			// @formatter:off
			/* -----------------------------------------------------------------------------------------------------------------------------------------
			 *| ##		| Input Parameter 						| Result Literal										| Comment						|
			  -----------------------------------------------------------------------------------------------------------------------------------------*/
			// xsd:string
			/*  0 */ 	{ "^^xsd:string", 						isTypedLiteral(equalTo(""), 				hasIRI(XSD, STRING))},// test empty string
			/*  1 */ 	{ "test^^xsd:string", 					isTypedLiteral(equalTo("test"), 			hasIRI(XSD, STRING))},// test non-empty string
			/*  2 */ 	{ "test string^^xsd:string", 			isTypedLiteral(equalTo("test string"), 	hasIRI(XSD, STRING))},// test value with whitespace
			/*  3 */ 	{ "test \rstring^^xsd:string", 			isTypedLiteral(equalTo("test \rstring"), hasIRI(XSD, STRING))},// test value with whitespace
			/*  4 */ 	{ "test \nstring^^xsd:string", 			isTypedLiteral(equalTo("test \nstring"), hasIRI(XSD, STRING))},// test value with whitespace
			/*  5 */ 	{ "test\tstring^^xsd:string", 			isTypedLiteral(equalTo("test\tstring"), 	hasIRI(XSD, STRING))},// test value with whitespace
			/*  6 */ 	{ "test string^^<" + XSD + "string>", 	isTypedLiteral(equalTo("test string"), 	hasIRI(XSD, STRING))},// test full name
//			/*  7 */ 	{ "?var^^xsd:string", 					typedMatcher(_var("var"),			hasIRI(XSD, STRING))},// test variable value
			/*  8 */ 	{ "?var@?lang^^xsd:string", 			LiteralIllelgalLanguageTagException.class }, 			// test variable value and lang
			/*  9 */ 	{ "test string^^rdfs:string", 			LiteralWrongBuildInDatatypeException.class }, 			// wrong namespace
			/* 10 */ 	{ "test string^^<" + RDF + "string>", 	LiteralWrongBuildInDatatypeException.class }, 			// wrong namespace
			// @formatter:on
		});
	}

}
