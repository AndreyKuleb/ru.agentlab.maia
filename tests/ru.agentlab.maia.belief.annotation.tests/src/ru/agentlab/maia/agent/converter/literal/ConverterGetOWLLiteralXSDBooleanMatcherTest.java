/*******************************************************************************
 * Copyright (c) 2016 AgentLab.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package ru.agentlab.maia.agent.converter.literal;

import static ru.agentlab.maia.belief.match.Matchers.isBooleanLiteral;

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
public class ConverterGetOWLLiteralXSDBooleanMatcherTest extends AbstractGetOWLLiteralMatcherTest {

	// Name is not working because some of the test strings have \r\n symbols
	@Parameters // (name="When parameter is [{0}] then result is [{1}]")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
			// @formatter:off
			/* ---------------------------------------------------------------------------------------------------------------------
			 *| ##   | Input Parameter                | Result Literal                             | Comment                        |
			  ---------------------------------------------------------------------------------------------------------------------*/
			// xsd:boolean
			/*  0 */ { "^^xsd:boolean",               LiteralNotInLexicalSpaceException.class },   // wrong empty value
			/*  1 */ { "true^^xsd:boolean",           isBooleanLiteral(true) },                    // test true string
			/*  2 */ { "false^^xsd:boolean",          isBooleanLiteral(false) },                   // test false string
			/*  3 */ { "1^^xsd:boolean",              isBooleanLiteral(true) },                    // test 1 string
			/*  4 */ { "0^^xsd:boolean",              isBooleanLiteral(false) },                   // test 0 string
			/*  5 */ { "true^^<" + XSD + "boolean>",  isBooleanLiteral(true) },                    // test full name
			/*  6 */ { "false^^<" + XSD + "boolean>", isBooleanLiteral(false) },                   // test full name
			/*  7 */ { "+1^^xsd:boolean",             LiteralNotInLexicalSpaceException.class },   // wrong value format
			/*  8 */ { "-1^^xsd:boolean",             LiteralNotInLexicalSpaceException.class },   // wrong value format
			/*  9 */ { "+0^^xsd:boolean",             LiteralNotInLexicalSpaceException.class },   // wrong value format
			/* 10 */ { "-0^^xsd:boolean",             LiteralNotInLexicalSpaceException.class },   // wrong value format
			/* 11 */ { "test string^^xsd:boolean",    LiteralNotInLexicalSpaceException.class },   // wrong value format
			/* 12 */ { "2^^xsd:boolean",              LiteralNotInLexicalSpaceException.class },   // wrong value format
//			/* 13 */ { "?var^^xsd:boolean",           isBoolean(_var("var")) },                    // test variable value
			/* 14 */ { "?var@?lang^^xsd:boolean",     LiteralIllelgalLanguageTagException.class }, // test variable value and lang
			/* 15 */ { "true^^rdf:boolean",           LiteralWrongBuildInDatatypeException.class}, // wrong namespace
			/* 16 */ { "true^^<" + OWL + "boolean>",  LiteralWrongBuildInDatatypeException.class}, // wrong namespace
			// @formatter:on
		});
	}

}
