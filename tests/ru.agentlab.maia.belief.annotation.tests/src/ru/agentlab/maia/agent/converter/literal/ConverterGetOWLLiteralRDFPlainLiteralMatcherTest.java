/*******************************************************************************
 * Copyright (c) 2016 AgentLab.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package ru.agentlab.maia.agent.converter.literal;

import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.equalTo;
import static ru.agentlab.maia.belief.filter.Matchers.hasIRI;
import static ru.agentlab.maia.belief.filter.Matchers.isPlainLiteral;
import static ru.agentlab.maia.belief.filter.Matchers.isTypedLiteral;
import static ru.agentlab.maia.filter.impl.PlanEventFilters.hamcrest;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import ru.agentlab.maia.agent.annotation.converter.LiteralWrongBuildInDatatypeException;
import ru.agentlab.maia.agent.converter.AbstractGetOWLLiteralMatcherTest;

/**
 * @author Dmitriy Shishkin <shishkindimon@gmail.com>
 */
@RunWith(Parameterized.class)
public class ConverterGetOWLLiteralRDFPlainLiteralMatcherTest extends AbstractGetOWLLiteralMatcherTest {

	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
			// @formatter:off
			/* -------------------------------------------------------------------------------------------------------------------------------------------
			 *| ##   | Input Parameter                           | Result Literal                                           | Comment                     |
			  -------------------------------------------------------------------------------------------------------------------------------------------*/
			// rdf:PlainLiteral
			/*  0 */ { "",                                       isPlainLiteral(hamcrest(equalTo("")), hamcrest(anything())) },                 // test empty string
			/*  1 */ { "^^rdf:PlainLiteral",                     isTypedLiteral(hamcrest(equalTo("")),hasIRI(RDF, PLAIN_LITERAL)) },  // test empty string
			/*  2 */ { "test^^rdf:PlainLiteral",                 isTypedLiteral(hamcrest(equalTo("test")),hasIRI(RDF, PLAIN_LITERAL)) },// test non-empty string
			/*  3 */ { "test string^^rdf:PlainLiteral",          isTypedLiteral(hamcrest(equalTo("test string")),hasIRI(RDF, PLAIN_LITERAL)) },// test value with whitespace
			/*  4 */ { "test string^^<" + RDF + "PlainLiteral>", isTypedLiteral(hamcrest(equalTo("test string")),hasIRI(RDF, PLAIN_LITERAL)) },// test full name
			/*  5 */ { "Padredefamilia@es",                      isPlainLiteral(hamcrest(equalTo("Padredefamilia")),hamcrest(equalTo("es"))) }, // test language tag
			/*  6 */ { "Padredefamilia@es^^rdf:PlainLiteral",    isPlainLiteral(hamcrest(equalTo("Padredefamilia")),hamcrest(equalTo("es"))) }, // test language tag
			/*  7 */ { "Family Guy@",                            isPlainLiteral(hamcrest(equalTo("Family Guy")), hamcrest(equalTo(""))) },      // test language tag
			/*  8 */ { "Family Guy@en",                          isPlainLiteral(hamcrest(equalTo("Family Guy")), hamcrest(equalTo("en"))) },    // test language tag
			/*  9 */ { "Тест строка@ru^^rdf:PlainLiteral",       isPlainLiteral(hamcrest(equalTo("Тест строка")), hamcrest(equalTo("ru"))) },   // test language tag
			/* 10 */ { "Тест@строка@ru^^rdf:PlainLiteral",       isPlainLiteral(hamcrest(equalTo("Тест@строка")), hamcrest(equalTo("ru"))) },   // test language tag
//			/* 11 */ { "?var^^rdf:PlainLiteral",                 isPlain(_var("var")) },                                    // test variable
//			/* 12 */ { "?var@?lang^^rdf:PlainLiteral",           isPlain(_var("var"), _var("lang")) },                      // test variable value and lang
//			/* 13 */ { "?var@?lang",                             isPlain(_var("var"), _var("lang")) },                      // test variable value, lang and no type
//			/* 14 */ { "?var",                                   isPlain(_var("var")) },                                    // test variable value only
			/* 15 */ { "Тест строка@ru^^xsd:string",             isTypedLiteral(hamcrest(equalTo("Тест строка@ru")),hasIRI(XSD, STRING))},// test language tag
			/* 16 */ { "Padre de familia@es^^owl:PlainLiteral",  LiteralWrongBuildInDatatypeException.class },              // wrong namespace
			/* 17 */ { "test string^^<" + OWL + "PlainLiteral>", LiteralWrongBuildInDatatypeException.class },              // wrong namespace
			// @formatter:on
		});
	}

}
