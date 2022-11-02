package org.rossonet.opcua.milo.server;

import java.io.Serializable;
import java.util.Collection;

import org.eclipse.milo.opcua.sdk.server.util.HostnameUtil;

public class OpcUaServerConfiguration implements Serializable {

	private static final int DEFAULT_HTTPS_BIND_PORT = 8443;
	public static final String DEFAULT_DISCOVERY_PATH = "/milo/discovery";
	public static final int DEFAULT_TCP_BIND_PORT = 12686;

	private static final long serialVersionUID = -4757483926119718893L;

	public String getBindAddresses() {
		// TODO Auto-generated method stub
		return "0.0.0.0";
	}

	public String getDiscoveryPath() {
		// TODO Auto-generated method stub
		return DEFAULT_DISCOVERY_PATH;
	}

	public Collection<String> getHostnames() {
		// TODO Auto-generated method stub
		return HostnameUtil.getHostnames("0.0.0.0");
	}

	public int getHttpsBindPort() {
		// TODO Auto-generated method stub
		return DEFAULT_HTTPS_BIND_PORT;
	}

	public String getPath() {
		// TODO Auto-generated method stub
		return "/milo";
	}

	public int getTcpBindPort() {
		// TODO Auto-generated method stub
		return DEFAULT_TCP_BIND_PORT;
	}

}
