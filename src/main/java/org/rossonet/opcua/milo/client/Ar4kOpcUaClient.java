package org.rossonet.opcua.milo.client;

import java.io.Closeable;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;

public interface Ar4kOpcUaClient extends Closeable {

	public static Ar4kOpcUaClient getNewClient(OpcUaClientConfiguration opcUaClientConfiguration) {
		return DefaultAr4kOpcUaClient.getNewClient(opcUaClientConfiguration);
	}

	public void connect() throws Exception;

	public void disconnect() throws Exception;

	public OpcUaClient getClient();

	public OpcUaClientConfiguration getOpcUaClientConfiguration();

}
