package org.eclipse.milo.examples;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.milo.examples.server.ExampleServer;
import org.eclipse.milo.opcua.stack.core.types.structured.AddNodesItem;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.rossonet.opcua.milo.client.Ar4kOpcUaClient;
import org.rossonet.opcua.milo.client.OpcUaClientConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManageNodesTests {

	public static boolean running = true;

	private static final Logger logger = LoggerFactory.getLogger(ManageNodesTests.class);
	private ExampleServer server = null;

	@BeforeAll
	public void runMiloServerExample() throws Exception {
		server = new ExampleServer();
		server.startup();
		logger.info("server started");
	}

	@AfterAll
	public void stopMiloServerExample() throws Exception {
		if (server != null) {
			server.shutdown();
		}
		logger.info("server stopped");
	}

	@Test
	public void tryCreateNode() throws Exception {
		final OpcUaClientConfiguration clientConfiguration = new OpcUaClientConfiguration();
		final Ar4kOpcUaClient client = Ar4kOpcUaClient.getNewClient(clientConfiguration);
		client.connect();
		final List<AddNodesItem> nodes = new ArrayList<>();
	}

}
