package org.eclipse.milo.examples.client;

import org.eclipse.milo.examples.server.ExampleNamespace;
import org.rossonet.opcua.milo.server.Ar4kOpcUaServer;
import org.rossonet.opcua.milo.server.LogAuditListener;
import org.rossonet.opcua.milo.server.LoggerStorageController;
import org.rossonet.opcua.milo.server.conf.OpcUaServerConfiguration;
import org.rossonet.opcua.milo.server.storage.OnMemoryStorageController;

public class ExampleServer {
	private Ar4kOpcUaServer server;

	public ExampleServer() {
		try {
			OpcUaServerConfiguration opcServerConfiguration;
			opcServerConfiguration = new OpcUaServerConfiguration();
			final OnMemoryStorageController storageController = new LoggerStorageController();
			server = Ar4kOpcUaServer.getNewServer(opcServerConfiguration, storageController);
			server.addAuditHook(new LogAuditListener());
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public void shutdown() throws Exception {
		server.shutdown();
	}

	public void startup() throws Exception {
		server.startup();
		ExampleNamespace.addNodesAndEdges(server);
	}

}
