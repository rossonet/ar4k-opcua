package org.rossonet.opcua.milo.client;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;

public interface Ar4kOpcUaClient {

	public static Ar4kOpcUaClient getNewClient(OpcUaClientConfiguration opcUaClientConfiguration) {
		return DefaultAr4kOpcUaClient.getNewClient(opcUaClientConfiguration);
	}

	public OpcUaClient getClient();

	public OpcUaClientConfiguration getOpcUaClientConfiguration();

	public void connect() throws Exception;

	public void disconnect() throws Exception;

}
