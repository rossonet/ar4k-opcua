package org.rossonet.opcua.milo.server;

import java.io.Serializable;

public class OpcUaServerConfiguration implements Serializable {

	private static final int DEFAULT_HTTPS_BIND_PORT = 8443;
	public static final String DEFAULT_DISCOVERY_PATH = "/milo/discovery";
	public static final int DEFAULT_TCP_BIND_PORT = 12686;

	private static final long serialVersionUID = -4757483926119718893L;

	public String getDiscoveryPath() {
		// TODO Auto-generated method stub
		return DEFAULT_DISCOVERY_PATH;
	}

	public int getHttpsBindPort() {
		// TODO Auto-generated method stub
		return DEFAULT_HTTPS_BIND_PORT;
	}

	public int getTcpBindPort() {
		// TODO Auto-generated method stub
		return DEFAULT_TCP_BIND_PORT;
	}

}
