package org.rossonet.opcua.milo.client;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.client.security.DefaultClientCertificateValidator;
import org.eclipse.milo.opcua.stack.core.security.DefaultTrustListManager;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.rossonet.opcua.milo.KeyStoreLoader;
import org.slf4j.LoggerFactory;

class DefaultAr4kOpcUaClient implements Ar4kOpcUaClient {

	static Ar4kOpcUaClient getNewClient(OpcUaClientConfiguration opcUaClientConfiguration) {
		return new DefaultAr4kOpcUaClient(opcUaClientConfiguration);
	}

	private final OpcUaClientConfiguration opcUaClientConfiguration;

	private DefaultTrustListManager trustListManager;

	private OpcUaClient client = null;

	private DefaultAr4kOpcUaClient(OpcUaClientConfiguration opcUaClientConfiguration) {
		this.opcUaClientConfiguration = opcUaClientConfiguration;
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

		final KeyStoreLoader loader = new KeyStoreLoader().load(securityTempDir);

		trustListManager = new DefaultTrustListManager(pkiDir);

		final DefaultClientCertificateValidator certificateValidator = new DefaultClientCertificateValidator(
				trustListManager);

		return OpcUaClient.create(opcUaClientConfiguration.getEndpointUrl(),
				endpoints -> endpoints.stream().filter(opcUaClientConfiguration.endpointFilter()).findFirst(),
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
	public OpcUaClient getClient() {
		return client;
	}

	@Override
	public OpcUaClientConfiguration getOpcUaClientConfiguration() {
		return opcUaClientConfiguration;
	}

	@Override
	public void connect() throws Exception {
		if (client == null) {
			client = createClient();
		}
		client.connect();
	}

	@Override
	public void disconnect() throws Exception {
		if (client != null) {
			client.disconnect();
		}
		client = null;
	}

}
