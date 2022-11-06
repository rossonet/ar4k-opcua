package org.rossonet.opcua.milo.server;

import static com.google.common.collect.Lists.newArrayList;
import static org.eclipse.milo.opcua.sdk.server.api.config.OpcUaServerConfig.USER_TOKEN_POLICY_ANONYMOUS;
import static org.eclipse.milo.opcua.sdk.server.api.config.OpcUaServerConfig.USER_TOKEN_POLICY_USERNAME;
import static org.eclipse.milo.opcua.sdk.server.api.config.OpcUaServerConfig.USER_TOKEN_POLICY_X509;

import java.security.KeyPair;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.eclipse.milo.opcua.sdk.server.api.config.OpcUaServerConfig;
import org.eclipse.milo.opcua.sdk.server.identity.CompositeValidator;
import org.eclipse.milo.opcua.sdk.server.identity.UsernameIdentityValidator;
import org.eclipse.milo.opcua.sdk.server.identity.X509IdentityValidator;
import org.eclipse.milo.opcua.sdk.server.util.HostnameUtil;
import org.eclipse.milo.opcua.stack.core.StatusCodes;
import org.eclipse.milo.opcua.stack.core.UaRuntimeException;
import org.eclipse.milo.opcua.stack.core.security.DefaultCertificateManager;
import org.eclipse.milo.opcua.stack.core.security.DefaultTrustListManager;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.transport.TransportProfile;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.enumerated.MessageSecurityMode;
import org.eclipse.milo.opcua.stack.core.types.structured.BuildInfo;
import org.eclipse.milo.opcua.stack.core.util.CertificateUtil;
import org.eclipse.milo.opcua.stack.core.util.NonceUtil;
import org.eclipse.milo.opcua.stack.core.util.SelfSignedCertificateGenerator;
import org.eclipse.milo.opcua.stack.core.util.SelfSignedHttpsCertificateBuilder;
import org.eclipse.milo.opcua.stack.server.EndpointConfiguration;
import org.eclipse.milo.opcua.stack.server.security.DefaultServerCertificateValidator;
import org.rossonet.opcua.milo.server.conf.OpcUaServerConfiguration;
import org.rossonet.opcua.milo.server.listener.AuditListener;
import org.rossonet.opcua.milo.server.listener.ShutdownListener;
import org.rossonet.opcua.milo.server.namespace.ManagedNamespace;
import org.rossonet.opcua.milo.server.storage.StorageController;
import org.rossonet.utils.LogHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class DefaultAr4kOpcUaServer implements Ar4kOpcUaServer {

	private static final Logger logger = LoggerFactory.getLogger(DefaultAr4kOpcUaServer.class);

	static {
		Security.addProvider(new BouncyCastleProvider());
		try {
			NonceUtil.blockUntilSecureRandomSeeded(10, TimeUnit.SECONDS);
		} catch (ExecutionException | InterruptedException | TimeoutException e) {
			logger.info("in static init", e);
		}
	}

	static Ar4kOpcUaServer getNewServer(final OpcUaServerConfiguration opcUaServerConfiguration,
			final StorageController storageController) {
		return new DefaultAr4kOpcUaServer(opcUaServerConfiguration, storageController);
	}

	private final Set<AuditListener> auditListeners = new HashSet<>();

	private ManagedNamespace namespace = null;

	private final OpcUaServerConfiguration opcUaServerConfiguration;

	private OpcUaServer server = null;

	private final Set<ShutdownListener> shutdownListeners = new HashSet<>();

	private CompletableFuture<OpcUaServer> startingFuture;

	private final StorageController storageController;

	private DefaultAr4kOpcUaServer(final OpcUaServerConfiguration opcUaServerConfiguration,
			final StorageController storageController) {
		this.opcUaServerConfiguration = opcUaServerConfiguration;
		this.storageController = storageController;
	}

	@Override
	public void addAuditHook(final AuditListener auditListener) {
		auditListeners.add(auditListener);

	}

	@Override
	public void addShutdownHook(final ShutdownListener shutdownListener) {
		shutdownListeners.add(shutdownListener);

	}

	@Override
	public void close() {
		shutdown();
	}

	@Override
	public ManagedNamespace getNamespace() {
		return namespace;
	}

	@Override
	public OpcUaServerConfiguration getOpcUaServerConfiguration() {
		return opcUaServerConfiguration;
	}

	@Override
	public OpcUaServer getServer() {
		return server;
	}

	public StorageController getStorageController() {
		return storageController;
	}

	@Override
	public boolean isStarted() {
		return startingFuture.isDone();
	}

	@Override
	public Collection<AuditListener> listAuditHooks() {
		return auditListeners;
	}

	@Override
	public Collection<ShutdownListener> listShutdownHooks() {
		return shutdownListeners;
	}

	@Override
	public void removeAuditHook(final AuditListener auditListener) {
		auditListeners.remove(auditListener);

	}

	@Override
	public void removeShutdownHook(final ShutdownListener shutdownListener) {
		shutdownListeners.remove(shutdownListener);

	}

	@Override
	public void shutdown() {
		if (server != null) {
			server.shutdown();
		}
		if (namespace != null) {
			namespace.shutdown();
		}
	}

	@Override
	public void startup() throws Exception {
		createServer();
	}

	@Override
	public void waitServerStarted() {
		try {
			startingFuture.get();
		} catch (InterruptedException | ExecutionException e) {
			logger.error(LogHelper.stackTraceToString(e));
		}

	}

	private EndpointConfiguration buildHttpsEndpoint(final EndpointConfiguration.Builder base) {
		return base.copy().setTransportProfile(TransportProfile.HTTPS_UABINARY)
				.setBindPort(opcUaServerConfiguration.getHttpsBindPort()).build();
	}

	private EndpointConfiguration buildTcpEndpoint(final EndpointConfiguration.Builder base) {
		return base.copy().setTransportProfile(TransportProfile.TCP_UASC_UABINARY)
				.setBindPort(opcUaServerConfiguration.getTcpBindPort()).build();
	}

	private Set<EndpointConfiguration> createEndpointConfigurations(final X509Certificate certificate) {
		final Set<EndpointConfiguration> endpointConfigurations = new LinkedHashSet<>();
		final List<String> bindAddresses = newArrayList();
		bindAddresses.add(opcUaServerConfiguration.getBindAddresses());
		final Set<String> hostnames = new LinkedHashSet<>();
		hostnames.add(HostnameUtil.getHostname());
		hostnames.addAll(opcUaServerConfiguration.getHostnames());
		for (final String bindAddress : bindAddresses) {
			for (final String hostname : hostnames) {
				final EndpointConfiguration.Builder builder = EndpointConfiguration.newBuilder()
						.setBindAddress(bindAddress).setHostname(hostname).setPath(opcUaServerConfiguration.getPath())
						.setCertificate(certificate).addTokenPolicies(USER_TOKEN_POLICY_ANONYMOUS,
								USER_TOKEN_POLICY_USERNAME, USER_TOKEN_POLICY_X509);
				final EndpointConfiguration.Builder noSecurityBuilder = builder.copy()
						.setSecurityPolicy(SecurityPolicy.None).setSecurityMode(MessageSecurityMode.None);
				endpointConfigurations.add(buildTcpEndpoint(noSecurityBuilder));
				endpointConfigurations.add(buildHttpsEndpoint(noSecurityBuilder));
				// TCP Basic256Sha256 / SignAndEncrypt
				endpointConfigurations
						.add(buildTcpEndpoint(builder.copy().setSecurityPolicy(SecurityPolicy.Basic256Sha256)
								.setSecurityMode(MessageSecurityMode.SignAndEncrypt)));
				endpointConfigurations.add(buildTcpEndpoint(builder.copy()
						.setSecurityPolicy(SecurityPolicy.Basic256Sha256).setSecurityMode(MessageSecurityMode.Sign)));
				endpointConfigurations.add(buildTcpEndpoint(builder.copy().setSecurityPolicy(SecurityPolicy.Basic256)
						.setSecurityMode(MessageSecurityMode.SignAndEncrypt)));
				endpointConfigurations.add(buildTcpEndpoint(builder.copy().setSecurityPolicy(SecurityPolicy.Basic256)
						.setSecurityMode(MessageSecurityMode.Sign)));
				endpointConfigurations
						.add(buildTcpEndpoint(builder.copy().setSecurityPolicy(SecurityPolicy.Basic128Rsa15)
								.setSecurityMode(MessageSecurityMode.SignAndEncrypt)));
				endpointConfigurations.add(buildTcpEndpoint(builder.copy()
						.setSecurityPolicy(SecurityPolicy.Basic128Rsa15).setSecurityMode(MessageSecurityMode.Sign)));
				endpointConfigurations
						.add(buildTcpEndpoint(builder.copy().setSecurityPolicy(SecurityPolicy.Aes128_Sha256_RsaOaep)
								.setSecurityMode(MessageSecurityMode.SignAndEncrypt)));
				endpointConfigurations
						.add(buildTcpEndpoint(builder.copy().setSecurityPolicy(SecurityPolicy.Aes128_Sha256_RsaOaep)
								.setSecurityMode(MessageSecurityMode.Sign)));
				endpointConfigurations
						.add(buildTcpEndpoint(builder.copy().setSecurityPolicy(SecurityPolicy.Aes256_Sha256_RsaPss)
								.setSecurityMode(MessageSecurityMode.SignAndEncrypt)));
				endpointConfigurations
						.add(buildTcpEndpoint(builder.copy().setSecurityPolicy(SecurityPolicy.Aes256_Sha256_RsaPss)
								.setSecurityMode(MessageSecurityMode.Sign)));
				// HTTPS Basic256Sha256 / Sign (SignAndEncrypt not allowed for HTTPS)
				endpointConfigurations.add(buildHttpsEndpoint(builder.copy()
						.setSecurityPolicy(SecurityPolicy.Basic256Sha256).setSecurityMode(MessageSecurityMode.Sign)));
				final EndpointConfiguration.Builder discoveryBuilder = builder.copy()
						.setPath(opcUaServerConfiguration.getDiscoveryPath()).setSecurityPolicy(SecurityPolicy.None)
						.setSecurityMode(MessageSecurityMode.None);
				endpointConfigurations.add(buildTcpEndpoint(discoveryBuilder));
				endpointConfigurations.add(buildHttpsEndpoint(discoveryBuilder));
			}
		}
		return endpointConfigurations;
	}

	private void createServer() throws Exception {
		final DefaultCertificateManager certificateManager = new DefaultCertificateManager(
				opcUaServerConfiguration.getServerKeyPair(), opcUaServerConfiguration.getServerCertificateChain());
		final DefaultTrustListManager trustListManager = new DefaultTrustListManager(
				opcUaServerConfiguration.getPkiDir());
		final DefaultServerCertificateValidator certificateValidator = new DefaultServerCertificateValidator(
				trustListManager);
		final KeyPair httpsKeyPair = SelfSignedCertificateGenerator.generateRsaKeyPair(2048);
		final SelfSignedHttpsCertificateBuilder httpsCertificateBuilder = new SelfSignedHttpsCertificateBuilder(
				httpsKeyPair);
		httpsCertificateBuilder.setCommonName(HostnameUtil.getHostname());
		HostnameUtil.getHostnames("0.0.0.0").forEach(httpsCertificateBuilder::addDnsName);
		final X509Certificate httpsCertificate = httpsCertificateBuilder.build();
		final UsernameIdentityValidator identityValidator = new UsernameIdentityValidator(true, authChallenge -> {
			final String username = authChallenge.getUsername();
			final String password = authChallenge.getPassword();
			final boolean userOk = "user".equals(username) && "password1".equals(password);
			final boolean adminOk = "admin".equals(username) && "password2".equals(password);
			return userOk || adminOk;
		});
		final X509IdentityValidator x509IdentityValidator = new X509IdentityValidator(c -> true);
		// If you need to use multiple certificates you'll have to be smarter than this.
		final X509Certificate certificate = certificateManager.getCertificates().stream().findFirst()
				.orElseThrow(() -> new UaRuntimeException(StatusCodes.Bad_ConfigurationError, "no certificate found"));
		// The configured application URI must match the one in the certificate(s)
		final String applicationUri = CertificateUtil.getSanUri(certificate)
				.orElseThrow(() -> new UaRuntimeException(StatusCodes.Bad_ConfigurationError,
						"certificate is missing the application URI"));
		final Set<EndpointConfiguration> endpointConfigurations = createEndpointConfigurations(certificate);
		@SuppressWarnings("unchecked")
		final OpcUaServerConfig serverConfig = OpcUaServerConfig.builder().setApplicationUri(applicationUri)
				.setApplicationName(LocalizedText.english(opcUaServerConfiguration.getApplicationName()))
				.setEndpoints(endpointConfigurations)
				.setBuildInfo(new BuildInfo(opcUaServerConfiguration.getProductUri(),
						opcUaServerConfiguration.getManufacturerName(), opcUaServerConfiguration.getProductName(),
						opcUaServerConfiguration.getSoftwareVersion(), opcUaServerConfiguration.getBuildNumber(),
						opcUaServerConfiguration.getBuildData()))
				.setCertificateManager(certificateManager).setTrustListManager(trustListManager)
				.setCertificateValidator(certificateValidator).setHttpsKeyPair(httpsKeyPair)
				.setHttpsCertificateChain(new X509Certificate[] { httpsCertificate })
				.setIdentityValidator(new CompositeValidator<>(identityValidator, x509IdentityValidator))
				.setProductUri(opcUaServerConfiguration.getProductUri()).build();
		server = new OpcUaServer(serverConfig);
		namespace = new ManagedNamespace(this, storageController);
		namespace.startup();
		startingFuture = server.startup();
		logger.info("OPCUA server started");
	}

}
