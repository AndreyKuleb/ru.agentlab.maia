/*******************************************************************************
 * Copyright (c) 2016 AgentLab.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package ru.agentlab.maia.agent.converter;

import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.semanticweb.owlapi.vocab.Namespaces;

import ru.agentlab.maia.agent.annotation.converter.AssertionWrongFormatException;
import ru.agentlab.maia.annotation.Converter;
import ru.agentlab.maia.tests.util.LoggerRule;

/**
 * Test method {@link Converter#splitDataPropertyAssertioin(String)} which split
 * literal into 3 parts:
 * <ol>
 * <li>OWLIndividual template;
 * <li>OWLDataProperty template;
 * <li>OWLLiteral template;
 * </ol>
 * 
 * @see #testCases()
 * @see #evaluateTestCase()
 * 
 * @author Dmitriy Shishkin <shishkindimon@gmail.com>
 */
@RunWith(Parameterized.class)
public class ConverterSplitDataPropertyAssertionTest {

	private final static String RDF = Namespaces.RDF.toString();
	private final static String OWL = Namespaces.OWL.toString();

	@Parameters
	public static Collection<Object[]> testCases() {
		return Arrays.asList(new Object[][] {
		// @formatter:off
		/* ---------------------------------------------------------------------------------------------------------------------
		 *| ##	| Input Parameter 				| Result										| Comment						|
		 *----------------------------------------------------------------------------------------------------------------------*/
		// 0 words
		/*  0 */ { "", 							AssertionWrongFormatException.class },			// test empty string
		/*  1 */ { "  ", 						AssertionWrongFormatException.class },			// test empty string
		// 1 word
		/*  2 */ { "xxx", 						AssertionWrongFormatException.class },			// test 1 word
		/*  3 */ { " xxx", 						AssertionWrongFormatException.class },			// test 1 word with first space
		/*  4 */ { "^^rdf:XMLLiteral", 			AssertionWrongFormatException.class },			// test 1 word
		/*  5 */ { "<" + RDF + "XMLLiteral>", 	AssertionWrongFormatException.class },			// test 1 word
		// 2 words
		/*  6 */ { "xxx xxx", 					AssertionWrongFormatException.class },			// 2 words with space
		/*  7 */ { "123 456", 					AssertionWrongFormatException.class },			// 2 words with space
		/*  8 */ { "  123  456", 				AssertionWrongFormatException.class },			// 2 words with first spaces
		/*  9 */ { "123  456  ", 				AssertionWrongFormatException.class },			// 2 words with last spaces
		/* 10 */ { "  123  456    ", 			AssertionWrongFormatException.class },			// 2 words with surrounded by spaces
		/* 11 */ { "123 456", 					AssertionWrongFormatException.class },			// 2 words with space
		/* 12 */ { "123\r\n456", 				AssertionWrongFormatException.class },			// 2 words with caret return
		/* 13 */ { "123\n456", 					AssertionWrongFormatException.class },			// 2 words with new line
		/* 14 */ { "\r\n123  456", 				AssertionWrongFormatException.class },			// 2 words with first new line
		/* 15 */ { "123  456\r\n", 				AssertionWrongFormatException.class },			// 2 words with last new line
		/* 16 */ { "\r\n123  456\r\n", 			AssertionWrongFormatException.class },			// 2 words with surrounded by new lines
		/* 17 */ { "123\t456", 					AssertionWrongFormatException.class },			// 2 words with tabulation
		/* 18 */ { "123	456", 					AssertionWrongFormatException.class },			// 2 words with tabulation
		/* 19 */ { "123\t\t\t456", 				AssertionWrongFormatException.class },			// 2 words with multiple tabulations
		/* 20 */ { "123			456", 			AssertionWrongFormatException.class },			// 2 words with multiple tabulations
		// 3 words
		/* 21 */ { "xxx xxx xxx", 				new String[] {"xxx", "xxx", "xxx"} },			// 3 words with space
		/* 22 */ { "123 456 789", 				new String[] {"123", "456", "789"} },			// 3 words with space
		/* 23 */ { "  123  456 789", 			new String[] {"123", "456", "789"} },			// 3 words with first spaces
		/* 24 */ { "123  456 789  ", 			new String[] {"123", "456", "789  "} },			// 3 words with last spaces
		/* 25 */ { "  123  456  789  ", 		new String[] {"123", "456", "789  "} },			// 3 words with surrounded by spaces
		/* 26 */ { "123 456 789", 				new String[] {"123", "456", "789"} },			// 3 words with space
		/* 27 */ { "123\r\n456\r\n789", 		new String[] {"123", "456", "789"} },			// 3 words with caret return
		/* 28 */ { "123\n456\n789", 			new String[] {"123", "456", "789"} },			// 3 words with new line
		/* 29 */ { "\r\n123  456 789", 			new String[] {"123", "456", "789"} },			// 3 words with first new line
		/* 30 */ { "123  456 789\r\n", 			new String[] {"123", "456", "789\r\n"} },		// 3 words with last new line
		/* 31 */ { "\r\n123  456 789\r\n", 		new String[] {"123", "456", "789\r\n"} },		// 3 words with surrounded by new lines
		/* 32 */ { "123\t456\t789", 			new String[] {"123", "456", "789"} },			// 3 words with tabulation
		/* 33 */ { "123	456	789", 				new String[] {"123", "456", "789"} },			// 3 words with tabulation
		/* 34 */ { "123\t\t\t456\t\t789", 		new String[] {"123", "456", "789"} },			// 3 words with multiple tabulations
		/* 35 */ { "123			456		789",	new String[] {"123", "456", "789"} },			// 3 words with multiple tabulations
		// 4 words
		/* 36 */ { "xxx xxx xxx xxx", 			new String[] {"xxx", "xxx", "xxx xxx"} },		// 4 words with space
		/* 37 */ { "123 456 789 012", 			new String[] {"123", "456", "789 012"} },		// 4 words with space
		/* 38 */ { "  123  456 789 012", 		new String[] {"123", "456", "789 012"} },		// 4 words with first spaces
		/* 39 */ { "123  456 789 012  ", 		new String[] {"123", "456", "789 012  "} },		// 4 words with last spaces
		/* 40 */ { "  123  456  789 012  ", 	new String[] {"123", "456", "789 012  "} },		// 4 words with surrounded by spaces
		/* 41 */ { "123 456 789 012", 			new String[] {"123", "456", "789 012"} },		// 4 words with space
		/* 42 */ { "123\r\n456\r\n789\r\n012", 	new String[] {"123", "456", "789\r\n012"} },	// 4 words with caret return
		/* 43 */ { "123\n456\n789\n012", 		new String[] {"123", "456", "789\n012"} },		// 4 words with new line
		/* 44 */ { "\r\n123  456 789	012", 	new String[] {"123", "456", "789	012"} },	// 4 words with first new line
		/* 45 */ { "123  456 789 012\r\n", 		new String[] {"123", "456", "789 012\r\n"} },	// 4 words with last new line
		/* 46 */ { "\r\n123  456 789 012\r\n", 	new String[] {"123", "456", "789 012\r\n"} },	// 4 words with surrounded by new lines
		/* 47 */ { "123\t456\t789\t012", 		new String[] {"123", "456", "789\t012"} },		// 4 words with tabulation
		/* 48 */ { "123	456	789	012", 			new String[] {"123", "456", "789	012"} },	// 4 words with tabulation
		/* 49 */ { "123\t\t\t456\t\t789\t\t012",new String[] {"123", "456", "789\t\t012"} },	// 4 words with multiple tabulations
		/* 50 */ { "123		456		789 012",	new String[] {"123", "456", "789 012"} },		// 4 words with multiple tabulations
		// More words
		/* 51 */ { "xxx xxx xxx xxx", 			new String[] {"xxx", "xxx", "xxx xxx"} },		// more words with space         
		/* 52 */ { "xxx\rxxx\rxxx\rxxx", 		new String[] {"xxx", "xxx", "xxx\rxxx"} },		// more words with caret return  
		/* 53 */ { "xxx\nxxx\nxxx\nxxx", 		new String[] {"xxx", "xxx", "xxx\nxxx"} },		// more words with new line      
		/* 54 */ { "xxx\txxx\txxx\txxx", 		new String[] {"xxx", "xxx", "xxx\txxx"} },		// more words with new tabulation
		/* 55 */ { "xxx	xxx	xxx	xxx", 			new String[] {"xxx", "xxx", "xxx	xxx"} },	// more words with new tabulation
		/* 56 */ { "xxx\r\nxxx\txxx\txxx", 		new String[] {"xxx", "xxx", "xxx\txxx"} },		// more words all
		// Special characters
		/* 57 */ { "ns:some rdf:type owl:Class", new String[] {"ns:some", "rdf:type", "owl:Class"} },// test : in literal
		/* 58 */ { "xxx rdf:type <" + OWL + "Class>", new String[] {"xxx", "rdf:type", "<" + OWL + "Class>"} },// test < # > in literal
		/* 59 */ { "<htt://s@s:www.a#Class> rdf:type  xxx",new String[] {"<htt://s@s:www.a#Class>", "rdf:type", "xxx"} },// test @ in literal
		/* 60 */ { "ns:some rdf:type a b c d^^xsd:string", new String[] {"ns:some", "rdf:type", "a b c d^^xsd:string"} },// test multiple words
		// @formatter:on
		});
	}

	@Rule
	public LoggerRule rule = new LoggerRule(this);

	@Parameter(0)
	public String parameter;

	@Parameter(1)
	public Object result;

	Converter converter = new Converter();

	@Test
	public void evaluateTestCase() {
		// Given
		Converter converter = new Converter();
		try {
			// When
			String[] splitted = converter.splitDataPropertyAssertioin(parameter);
			System.out.println("Result splitted: " + Arrays.toString(splitted));
			// Then
			if (result.getClass().isArray()) {
				assertThat(splitted, arrayWithSize(3));
				assertThat(splitted, arrayContaining((String[]) result));
			} else {
				Assert.fail("Expected [" + result + "]");
			}
		} catch (Exception e) {
			// Then
			if (result instanceof Class<?>) {
				assertThat(e, instanceOf((Class<?>) result));
			} else {
				Assert.fail("Expected [" + result + "], but was: [" + e + "]");
			}
		}
	}

}
