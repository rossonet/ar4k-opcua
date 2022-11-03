package org.jeasy.rules.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.StringReader;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.jeasy.rules.mvel.MVELRuleFactory;
import org.jeasy.rules.support.reader.JsonRuleDefinitionReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MVELTests {

	private static final String RULE_IN_JSON_FORMAT = "[\n" + "  {\n" + "    \"name\": \"adult rule\",\n"
			+ "    \"description\": \"when age is greater than 18, then mark as adult\",\n" + "    \"priority\": 1,\n"
			+ "    \"condition\": \"person.age > 18\",\n" + "    \"actions\": [\n"
			+ "      \"person.setAdult(true);\"\n" + "    ]\n" + "  },\n" + "  {\n"
			+ "    \"name\": \"weather rule\",\n" + "    \"description\": \"when it rains, then take an umbrella\",\n"
			+ "    \"priority\": 2,\n" + "    \"condition\": \"rain == true\",\n" + "    \"actions\": [\n"
			+ "      \"controller.testListenerRain();\"\n" + "    ]\n" + "  }\n" + "]";
	private boolean check = false;

	private Rules testRules = null;

	@Test
	public void personRuleTest() {
		final Person person = new Person("foo", 20);
		System.out.println("before -> " + person);
		final Facts facts = new Facts();
		facts.put("person", person);
		facts.put("rain", true);
		facts.put("controller", this);
		boolean found = false;
		for (final Rule r : testRules) {
			if (r.getName().equals("adult rule")) {
				final boolean evaluationResult = r.evaluate(facts);
				assertTrue(evaluationResult);
				found = true;
				break;
			}
		}
		assertTrue(found);
		final RulesEngine rulesEngine = new DefaultRulesEngine();
		rulesEngine.fire(testRules, facts);
		System.out.println("after -> " + person);
		assertTrue(check);
		assertTrue(person.isAdult());
	}

	@BeforeEach
	public void prepare() throws Exception {
		final MVELRuleFactory ruleFactory = new MVELRuleFactory(new JsonRuleDefinitionReader());
		testRules = ruleFactory.createRules(new StringReader(RULE_IN_JSON_FORMAT));
	}

	public void testListenerRain() {
		System.out.println("test listener rain called");
		check = true;
	}

}
