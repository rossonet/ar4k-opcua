package org.rossonet.opcua.milo.client;

import java.io.Serializable;
import java.util.function.Predicate;

import org.eclipse.milo.opcua.sdk.client.api.identity.AnonymousProvider;
import org.eclipse.milo.opcua.sdk.client.api.identity.IdentityProvider;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;

public class OpcUaClientConfiguration implements Serializable {

	private static final long serialVersionUID = 3956970075719682616L;
	private static final int DEFAULT_TCP_BIND_PORT = 12686;
	private static final String DEFAULT_MILO_DISCOVERY_PATH = "/milo/discovery";

	public Predicate<? super EndpointDescription> endpointFilter() {
		// TODO Auto-generated method stub
		return e -> getSecurityPolicy().getUri().equals(e.getSecurityPolicyUri());
	}

	public String getApplicationUri() {
		// TODO Auto-generated method stub
		return "urn:eclipse:milo:examples:client";
	}

	public String getEndpointUrl() {
		// TODO Auto-generated method stub
		return "opc.tcp://127.0.0.1:" + DEFAULT_TCP_BIND_PORT + DEFAULT_MILO_DISCOVERY_PATH;
	}

	public IdentityProvider getIdentityProvider() {
		// TODO Auto-generated method stub
		return new AnonymousProvider();
	}

	public String getLocalizedEnglishText() {
		// TODO Auto-generated method stub
		return "eclipse milo opc-ua client";
	}

	public SecurityPolicy getSecurityPolicy() {
		// TODO Auto-generated method stub
		return SecurityPolicy.None;
	}
}
