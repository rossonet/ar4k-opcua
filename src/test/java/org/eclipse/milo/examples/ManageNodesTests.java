package org.eclipse.milo.examples;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.milo.opcua.stack.core.types.structured.AddNodesItem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rossonet.opcua.milo.client.Ar4kOpcUaClient;
import org.rossonet.opcua.milo.client.OpcUaClientConfiguration;
import org.rossonet.opcua.milo.server.Ar4kOpcUaServer;
import org.rossonet.opcua.milo.server.OpcUaServerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManageNodesTests {

	public static boolean running = true;

	private static final Logger logger = LoggerFactory.getLogger(ManageNodesTests.class);
	private Ar4kOpcUaServer server = null;

	@BeforeEach
	public void runMiloServerExample() throws Exception {
		final OpcUaServerConfiguration serverConfiguration = new OpcUaServerConfiguration();
		server = Ar4kOpcUaServer.getNewServer(serverConfiguration);
		server.startup();
		logger.info("server started");
	}

	@AfterEach
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
		Thread.sleep(60000);
	}

}
