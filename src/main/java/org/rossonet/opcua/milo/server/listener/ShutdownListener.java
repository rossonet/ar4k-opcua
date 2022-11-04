package org.rossonet.opcua.milo.server.listener;

public interface ShutdownListener {

	public void shutdown(ShutdownReason reason);

}
