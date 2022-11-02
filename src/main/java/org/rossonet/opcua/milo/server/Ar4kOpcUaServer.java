package org.rossonet.opcua.milo.server;

import java.io.Closeable;
import java.util.Collection;

import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.rossonet.opcua.milo.server.listener.ShutdownListener;
import org.rossonet.opcua.milo.server.namespace.ManagedNamespace;

public interface Ar4kOpcUaServer extends Closeable {

	public static Ar4kOpcUaServer getNewServer(OpcUaServerConfiguration opcUaServerConfiguration) {
		return DefaultAr4kOpcUaServer.getNewServer(opcUaServerConfiguration);
	}

	public void addShutdownHook(ShutdownListener shutdownListener);

	public void removeShutdownHook(ShutdownListener shutdownListener);

	public ManagedNamespace getNamespace();

	public OpcUaServerConfiguration getOpcUaServerConfiguration();

	public OpcUaServer getServer();

	public Collection<ShutdownListener> listShutdownHooks();

	public void shutdown() throws Exception;

	public void startup() throws Exception;

}
