package org.eclipse.milo.examples;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rossonet.opcua.milo.server.Ar4kOpcUaServer;
import org.rossonet.opcua.milo.server.OpcUaServerConfiguration;
import org.rossonet.opcua.milo.server.storage.OnMemoryStorageController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManageNodesTests {

	private static final Logger logger = LoggerFactory.getLogger(ManageNodesTests.class);
	private Ar4kOpcUaServer server = null;

	@BeforeEach
	public void runMiloServerExample() throws Exception {
		final OpcUaServerConfiguration serverConfiguration = new OpcUaServerConfiguration();
		server = Ar4kOpcUaServer.getNewServer(serverConfiguration, new OnMemoryStorageController());
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
		System.out.println(server.getServer().getEndpointDescriptions());
		// TODO completare test con creazione dinamica nodi
	}

}
