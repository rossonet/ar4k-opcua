package org.rossonet.opcua.milo.server;

import java.io.Closeable;

import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.rossonet.opcua.milo.server.namespace.ManagedNamespace;

public interface Ar4kOpcUaServer extends Closeable {

	public static Ar4kOpcUaServer getNewServer(OpcUaServerConfiguration opcUaServerConfiguration) {
		return DefaultAr4kOpcUaServer.getNewServer(opcUaServerConfiguration);
	}

	ManagedNamespace getNamespace();

	OpcUaServerConfiguration getOpcUaServerConfiguration();

	OpcUaServer getServer();

	void shutdown();

	void startup() throws Exception;

}
