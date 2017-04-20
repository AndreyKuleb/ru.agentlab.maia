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
import ru.agentlab.maia.agent.annotation.converter.LiteralNotInLexicalSpaceException;
import ru.agentlab.maia.agent.annotation.converter.LiteralWrongBuildInDatatypeException;
import ru.agentlab.maia.agent.converter.AbstractGetOWLLiteralMatcherTest;

/**
 * @author Dmitriy Shishkin <shishkindimon@gmail.com>
 */
@RunWith(Parameterized.class)
public class ConverterGetOWLLiteralXSDNormalizedStringMatcherTest extends AbstractGetOWLLiteralMatcherTest {

	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
			// @formatter:off
			/* ---------------------------------------------------------------------------------------------------------------------------------------------------------
			 *| ##		| Input Parameter 							| Result Literal													| Comment						|
			  ----------------------------------------------------------------------------------------------------------------------------------------------------------*/
			// xsd:normalizedString
			/*  0 */ 	{ "^^xsd:normalizedString", 				isTypedLiteral(equalTo(""), 				hasIRI(XSD, NORMALIZED_STRING))},	// test empty string
			/*  1 */ 	{ "test^^xsd:normalizedString", 			isTypedLiteral(equalTo("test"), 			hasIRI(XSD, NORMALIZED_STRING))},	// test non-empty string
			/*  2 */ 	{ "test string^^xsd:normalizedString", 		isTypedLiteral(equalTo("test string"), 	hasIRI(XSD, NORMALIZED_STRING))},	// test value with whitespace
			/*  3 */ 	{ "string^^<" + XSD + "normalizedString>",	isTypedLiteral(equalTo("string"), 		hasIRI(XSD, NORMALIZED_STRING))},	// test full name
//			/*  4 */ 	{ "?var^^xsd:normalizedString", 			isTyped(_var("var"),			hasIRI(XSD, NORMALIZED_STRING))}, // test variable value
			/*  5 */ 	{ "?var@?lang^^xsd:normalizedString", 		LiteralIllelgalLanguageTagException.class }, 						// test variable value and lang
			/*  6 */ 	{ "test \rstring^^xsd:normalizedString", 	LiteralNotInLexicalSpaceException.class},							// test value with whitespace
			/*  7 */ 	{ "test \nstring^^xsd:normalizedString", 	LiteralNotInLexicalSpaceException.class},							// test value with whitespace
			/*  8 */ 	{ "test\tstring^^xsd:normalizedString", 	LiteralNotInLexicalSpaceException.class},							// test value with whitespace
			/*  9 */ 	{ "test string^^rdfs:normalizedString", 	LiteralWrongBuildInDatatypeException.class }, 						// wrong namespace
			/* 10 */ 	{ "test^^<" + RDF + "normalizedString>",	LiteralWrongBuildInDatatypeException.class }, 						// wrong namespace
			// @formatter:on
		});
	}

}
