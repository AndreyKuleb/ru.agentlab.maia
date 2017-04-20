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
import static ru.agentlab.maia.belief.filter.Matchers.hasIRI;
import static ru.agentlab.maia.belief.filter.Matchers.isPlain;
import static ru.agentlab.maia.belief.filter.Matchers.isTypedLiteral;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import ru.agentlab.maia.agent.annotation.converter.LiteralNotInLexicalSpaceException;
import ru.agentlab.maia.agent.annotation.converter.LiteralUnknownPrefixException;
import ru.agentlab.maia.agent.annotation.converter.LiteralWrongFormatException;
import ru.agentlab.maia.agent.converter.AbstractGetOWLLiteralMatcherTest;

/**
 * @author Dmitriy Shishkin <shishkindimon@gmail.com>
 */
@RunWith(Parameterized.class)
public class ConverterGetOWLLiteralMiscMatcherTest extends AbstractGetOWLLiteralMatcherTest {

	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
			// @formatter:off
			/* ----------------------------------------------------------------------------------------------------------------------------------------
			 *| ##   | Input Parameter                    | Result Literal                                              | Comment                      |
			  ----------------------------------------------------------------------------------------------------------------------------------------*/
			// Misc
			/*  0 */ { "test string^rdf:XMLLiteral",      isPlain(equalTo("test string^rdf:XMLLiteral")) },             // wrong separator, should be ^^
			/*  1 */ { "test string^rdf:Literal",         isPlain(equalTo("test string^rdf:Literal")) },                // wrong separator, should be ^^
			/*  2 */ { "test string^rdf:real",            isPlain(equalTo("test string^rdf:real")) },                   // wrong separator, should be ^^
			/*  3 */ { "true^xsd:boolean",                isPlain(equalTo("true^xsd:boolean")) },                       // wrong separator, should be ^^
			/*  4 */ { "test string^^^rdf:XMLLiteral",    isTypedLiteral(equalTo("test string^"), hasIRI(RDF, XML_LITERAL)) }, // wrong separator, should be ^^
			/*  5 */ { "test string^^^rdfs:Literal",      isTypedLiteral(equalTo("test string^"), hasIRI(RDFS, LITERAL)) }, 	// wrong separator, should be ^^
			/*  6 */ { "test string^^^owl:real",          isTypedLiteral(equalTo("test string^"), hasIRI(OWL, REAL)) }, 		// wrong separator, should be ^^
//			/*  7 */ { "?var^^?type",                     isTyped(var("var"),			var("type")) },                 // test variable value and type
			/*  8 */ { "false^^^xsd:boolean",             LiteralNotInLexicalSpaceException.class },                    // wrong value [true^] format
			/*  9 */ { "false^^some:type",                LiteralUnknownPrefixException.class },                        // unknown prefix
			/* 10 */ { "false^^xsd : b o o lean:boolean", LiteralWrongFormatException.class },                          // wrong format
			/* 11 */ { "?v ar^^?t; pe",                   LiteralWrongFormatException.class },                          // test variable value and type
//			/* 12 */ { "?var@?lang^^?type",               plainMatcher(_var("var"), _var("lang"), _var("type")) },      // test variable value, lang and type
			// @formatter:on
		});
	}

}
