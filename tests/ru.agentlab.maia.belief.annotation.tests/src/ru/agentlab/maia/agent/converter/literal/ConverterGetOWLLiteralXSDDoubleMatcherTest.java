/*******************************************************************************
 * Copyright (c) 2016 AgentLab.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package ru.agentlab.maia.agent.converter.literal;

import static ru.agentlab.maia.belief.match.Matchers.isDoubleLiteral;

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
public class ConverterGetOWLLiteralXSDDoubleMatcherTest extends AbstractGetOWLLiteralMatcherTest {

	// Name is not working because some of the test strings have \r\n symbols
	@Parameters // (name="When parameter is [{0}] then result is [{1}]")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
			// @formatter:off
			/* ---------------------------------------------------------------------------------------------------------------------------------
			 *| ##		| Input Parameter 						| Result Literal								| Comment						|
			  ---------------------------------------------------------------------------------------------------------------------------------*/
			// xsd:double
			/*  0 */ 	{ "^^xsd:double", 						LiteralNotInLexicalSpaceException.class },		// wrong empty value
			/*  1 */ 	{ "0^^xsd:double", 						isDoubleLiteral(0) },						    // test 0 string
			/*  2 */ 	{ "-0^^xsd:double", 					isDoubleLiteral(0) },				            // test 0 string
			/*  3 */ 	{ "+0^^xsd:double", 					isDoubleLiteral(0) },						    // test 0 string
			/*  4 */ 	{ "1^^xsd:double", 						isDoubleLiteral(1) },						    // test 1 string
			/*  5 */ 	{ "-1^^xsd:double", 					isDoubleLiteral(-1) },						    // test 1 string
			/*  6 */ 	{ "+1^^xsd:double", 					isDoubleLiteral(1) },						    // test 1 string
			/*  7 */ 	{ "12345^^<" + XSD + "double>",			isDoubleLiteral(12345) }, 					    // test more numbers
			/*  8 */ 	{ "2.2E12^^<" + XSD + "double>",		isDoubleLiteral(2.2e12) }, 						// test dot name
			/*  9 */ 	{ Double.MAX_VALUE+"^^xsd:double",		isDoubleLiteral(Double.MAX_VALUE) }, 		    // test max value
			/* 10 */ 	{ "-"+Double.MAX_VALUE+"^^xsd:double",	isDoubleLiteral(-Double.MAX_VALUE) }, 		    // test min value
			/* 11 */ 	{ Double.MAX_VALUE+"0^^xsd:double",		isDoubleLiteral(Double.POSITIVE_INFINITY) },    // test > +infinity
			/* 12 */ 	{ "INF^^xsd:double",					isDoubleLiteral(Double.POSITIVE_INFINITY) },    // test +infinity
			/* 13 */ 	{ "-"+Double.MAX_VALUE+"0^^xsd:double",	isDoubleLiteral(Double.NEGATIVE_INFINITY) },    // test < -infinity
			/* 14 */ 	{ "-INF^^xsd:double",					isDoubleLiteral(Double.NEGATIVE_INFINITY) },    // test +infinity
			/* 15 */ 	{ "NaN^^xsd:double",					isDoubleLiteral(Double.NaN) }, 				    // test NaN
			/* 16 */ 	{ "-NaN^^xsd:double",					LiteralNotInLexicalSpaceException.class }, 		// test -NaN
			/* 17 */ 	{ "+-1^^xsd:double", 					LiteralNotInLexicalSpaceException.class },		// wrong value format
			/* 18 */ 	{ "-+1^^xsd:double", 					LiteralNotInLexicalSpaceException.class },		// wrong value format
			/* 19 */ 	{ "+0.3.3^^xsd:double", 				LiteralNotInLexicalSpaceException.class },		// wrong value format
			/* 20 */ 	{ "-0-^^xsd:double", 					LiteralNotInLexicalSpaceException.class },		// wrong value format
			/* 21 */ 	{ "test string^^xsd:double", 			LiteralNotInLexicalSpaceException.class },		// wrong value format
			/* 22 */ 	{ "2/4^^xsd:double", 					LiteralNotInLexicalSpaceException.class },		// wrong value format
//			/* 23 */ 	{ "?var^^xsd:double", 					doublMatcher(_var("var")) }, 					// test variable value
			/* 24 */ 	{ "?var@?lang^^xsd:double", 			LiteralIllelgalLanguageTagException.class }, 	// test variable value and lang
			/* 25 */ 	{ "true^^rdf:boolean",					LiteralWrongBuildInDatatypeException.class }, 	// wrong namespace
			/* 26 */ 	{ "true^^<" + OWL + "double>",			LiteralWrongBuildInDatatypeException.class }, 	// wrong namespace
			// @formatter:on
		});
	}

}
