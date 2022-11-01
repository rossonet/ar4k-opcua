package org.rossonet.tests;

import java.util.logging.Logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.rossonet.opcua.milo.Main;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(OrderAnnotation.class)
public class MainTest {

	private static final Logger logger = Logger.getLogger(MainTest.class.getName());

	@AfterEach
	public void cleanTestsContext() throws Exception {
		logger.info("test completed");
	}

	@Test
	@Order(1)
	public void runMain() throws Exception {
		final String[] args = new String[] {};
		Main.main(args);
		logger.info("ok");
	}

}
