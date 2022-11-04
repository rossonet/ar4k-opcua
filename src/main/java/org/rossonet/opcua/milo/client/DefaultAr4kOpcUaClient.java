package org.rossonet.opcua.milo.client;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Predicate;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.client.security.DefaultClientCertificateValidator;
import org.eclipse.milo.opcua.stack.core.security.DefaultTrustListManager;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;
import org.rossonet.opcua.milo.client.conf.OpcUaClientConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class DefaultAr4kOpcUaClient implements Ar4kOpcUaClient {

	private static final Logger logger = LoggerFactory.getLogger(DefaultAr4kOpcUaClient.class);

	static Ar4kOpcUaClient getNewClient(OpcUaClientConfiguration opcUaClientConfiguration) {
		return new DefaultAr4kOpcUaClient(opcUaClientConfiguration);
	}

	private final OpcUaClientConfiguration opcUaClientConfiguration;

	private DefaultTrustListManager trustListManager;

	private OpcUaClient client = null;

	private DefaultAr4kOpcUaClient(OpcUaClientConfiguration opcUaClientConfiguration) {
		this.opcUaClientConfiguration = opcUaClientConfiguration;
	}

	@Override
	public void close() throws IOException {
		try {
			disconnect();
		} catch (final Exception e) {
			throw new IOException(e);
		}

	}

	@Override
	public void connect() throws Exception {
		if (client == null) {
			client = createClient();
		}
		client.connect();
		logger.info("client started");
	}

	private OpcUaClient createClient() throws Exception {
		final Path securityTempDir = Paths.get(System.getProperty("java.io.tmpdir"), "client", "security");
		Files.createDirectories(securityTempDir);
		if (!Files.exists(securityTempDir)) {
			throw new Exception("unable to create security dir: " + securityTempDir);
		}

		final File pkiDir = securityTempDir.resolve("pki").toFile();

		LoggerFactory.getLogger(getClass()).info("security dir: {}", securityTempDir.toAbsolutePath());
		LoggerFactory.getLogger(getClass()).info("security pki dir: {}", pkiDir.getAbsolutePath());

		final KeyStoreLoaderClient loader = new KeyStoreLoaderClient().load(securityTempDir);

		trustListManager = new DefaultTrustListManager(pkiDir);

		final DefaultClientCertificateValidator certificateValidator = new DefaultClientCertificateValidator(
				trustListManager);

		return OpcUaClient.create(opcUaClientConfiguration.getEndpointUrl(),
				endpoints -> endpoints.stream().filter(endpointFilter()).findFirst(),
				configBuilder -> configBuilder
						.setApplicationName(LocalizedText.english(opcUaClientConfiguration.getLocalizedEnglishText()))
						.setApplicationUri(opcUaClientConfiguration.getApplicationUri())
						.setKeyPair(loader.getClientKeyPair()).setCertificate(loader.getClientCertificate())
						.setCertificateChain(loader.getClientCertificateChain())
						.setCertificateValidator(certificateValidator)
						.setIdentityProvider(opcUaClientConfiguration.getIdentityProvider())
						.setRequestTimeout(uint(5000)).build());
	}

	@Override
	public void disconnect() throws Exception {
		if (client != null) {
			client.disconnect();
		}
		client = null;
	}

	public Predicate<EndpointDescription> endpointFilter() {
		return e -> opcUaClientConfiguration.getSecurityPolicy().getUri().equals(e.getSecurityPolicyUri());
	}

	@Override
	public OpcUaClient getClient() {
		return client;
	}

	@Override
	public OpcUaClientConfiguration getOpcUaClientConfiguration() {
		return opcUaClientConfiguration;
	}

}
