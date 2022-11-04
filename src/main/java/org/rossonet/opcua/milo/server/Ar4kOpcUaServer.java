package org.rossonet.opcua.milo.server;

import java.io.Closeable;
import java.util.Collection;

import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.rossonet.opcua.milo.server.listener.AuditListener;
import org.rossonet.opcua.milo.server.listener.ShutdownListener;
import org.rossonet.opcua.milo.server.namespace.ManagedNamespace;
import org.rossonet.opcua.milo.server.storage.StorageController;

public interface Ar4kOpcUaServer extends Closeable {

	public static Ar4kOpcUaServer getNewServer(final OpcUaServerConfiguration opcUaServerConfiguration,
			final StorageController storageController) {
		return DefaultAr4kOpcUaServer.getNewServer(opcUaServerConfiguration, storageController);
	}

	public void addAuditHook(AuditListener shutdownListener);

	public void addShutdownHook(ShutdownListener shutdownListener);

	public ManagedNamespace getNamespace();

	public OpcUaServerConfiguration getOpcUaServerConfiguration();

	public OpcUaServer getServer();

	public boolean isStarted();

	public Collection<AuditListener> listAuditHooks();

	public Collection<ShutdownListener> listShutdownHooks();

	public void removeAuditHook(AuditListener shutdownListener);

	public void removeShutdownHook(ShutdownListener shutdownListener);

	public void shutdown() throws Exception;

	public void startup() throws Exception;

	public void waitServerStarted();

}
