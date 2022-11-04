package org.rossonet.opcua.milo.server.conf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.eclipse.milo.opcua.sdk.server.util.HostnameUtil;
import org.eclipse.milo.opcua.stack.core.types.builtin.DateTime;
import org.eclipse.milo.opcua.stack.core.util.SelfSignedCertificateBuilder;
import org.eclipse.milo.opcua.stack.core.util.SelfSignedCertificateGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

public class OpcUaServerConfiguration implements Serializable {

	public static final String DEFAULT_DISCOVERY_PATH = "/milo/discovery";

	public static final int DEFAULT_TCP_BIND_PORT = 12686;

	public static final String DEFAULT_URN_ROSSONET_OPCUA_SERVER = "urn:rossonet:opcua:server";

	private static final int DEFAULT_HTTPS_BIND_PORT = 8443;

	private static final Pattern IP_ADDR_PATTERN = Pattern
			.compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
	private static final Logger logger = LoggerFactory.getLogger(OpcUaServerConfiguration.class);

	private static final char[] PASSWORD = "password".toCharArray();

	private static final long serialVersionUID = -4757483926119718893L;

	private static final String SERVER_ALIAS = "server-ai";

	private final File pkiDir;

	private X509Certificate serverCertificate;

	private X509Certificate[] serverCertificateChain;

	private KeyPair serverKeyPair;

	public OpcUaServerConfiguration() throws Exception {
		final Path securityTempDir = Paths.get(System.getProperty("java.io.tmpdir"), "server", "security");
		Files.createDirectories(securityTempDir);
		if (!Files.exists(securityTempDir)) {
			throw new Exception("unable to create security temp dir: " + securityTempDir);
		}
		pkiDir = securityTempDir.resolve("pki").toFile();
		logger.info("security dir: {}", securityTempDir.toAbsolutePath());
		logger.info("security pki dir: {}", pkiDir.getAbsolutePath());
		load(securityTempDir);
	}

	public boolean enableDemoDatas() {
		// TODO Auto-generated method stub
		return false;
	}

	public String getApplicationName() {
		// TODO Auto-generated method stub
		return "Eclipse Milo OPC UA Example Server";
	}

	public String getBindAddresses() {
		// TODO Auto-generated method stub
		return "0.0.0.0";
	}

	public DateTime getBuildData() {
		// TODO Auto-generated method stub
		return DateTime.now();
	}

	public String getBuildNumber() {
		// TODO Auto-generated method stub
		return "007";
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

	public String getManufacturerName() {
		// TODO Auto-generated method stub
		return "rossonet";
	}

	public String getNameSpaceUri() {
		// TODO Auto-generated method stub
		return DEFAULT_URN_ROSSONET_OPCUA_SERVER;
	}

	public String getPath() {
		// TODO Auto-generated method stub
		return "/milo";
	}

	public File getPkiDir() {
		// TODO Auto-generated method stub
		return pkiDir;
	}

	public String getProductName() {
		// TODO Auto-generated method stub
		return "rossonet opcua server";
	}

	public String getProductUri() {
		// TODO Auto-generated method stub
		return "urn:rossonet:milo:ar4k-server";
	}

	public String getRootBrowseName() {
		// TODO Auto-generated method stub
		return "server-data";
	}

	public String getRootDescriptionEnglish() {
		// TODO Auto-generated method stub
		return "Data folder managed by Beacon";
	}

	public String getRootDisplayNameEnglish() {
		// TODO Auto-generated method stub
		return "Datas";
	}

	public String getRootNodeId() {
		// TODO Auto-generated method stub
		return "server-data";
	}

	public X509Certificate[] getServerCertificateChain() {
		return serverCertificateChain;
	}

	public KeyPair getServerKeyPair() {
		return serverKeyPair;
	}

	public String getSoftwareVersion() {
		// TODO Auto-generated method stub
		return OpcUaServer.SDK_VERSION;
	}

	public int getTcpBindPort() {
		// TODO Auto-generated method stub
		return DEFAULT_TCP_BIND_PORT;
	}

	private String getCommonName() {
		return "Eclipse Milo Example Server";
	}

	private String getCountryCode() {
		return "US";
	}

	private String getLocalityName() {
		return "Folsom";
	}

	private String getOrganization() {
		return "digitalpetri";
	}

	private String getOrganizationalUnit() {
		return "dev";
	}

	private String getStateName() {
		return "CA";
	}

	X509Certificate getServerCertificate() {
		return serverCertificate;
	}

	void load(final Path baseDir) throws Exception {
		final KeyStore keyStore = KeyStore.getInstance("PKCS12");
		final File serverKeyStore = baseDir.resolve("opcua-server" + UUID.randomUUID().toString() + ".pfx").toFile();
		logger.info("Loading KeyStore at {}", serverKeyStore);
		if (!serverKeyStore.exists()) {
			keyStore.load(null, PASSWORD);
			final KeyPair keyPair = SelfSignedCertificateGenerator.generateRsaKeyPair(2048);
			final String applicationUri = getNameSpaceUri();
			final SelfSignedCertificateBuilder builder = new SelfSignedCertificateBuilder(keyPair)
					.setCommonName(getCommonName()).setOrganization(getOrganization())
					.setOrganizationalUnit(getOrganizationalUnit()).setLocalityName(getLocalityName())
					.setStateName(getStateName()).setCountryCode(getCountryCode()).setApplicationUri(applicationUri);
			// Get as many hostnames and IP addresses as we can listed in the certificate.
			final Set<String> hostnames = Sets.union(Sets.newHashSet(HostnameUtil.getHostname()),
					HostnameUtil.getHostnames("0.0.0.0", false));
			for (final String hostname : hostnames) {
				if (IP_ADDR_PATTERN.matcher(hostname).matches()) {
					builder.addIpAddress(hostname);
				} else {
					builder.addDnsName(hostname);
				}
			}
			final X509Certificate certificate = builder.build();
			keyStore.setKeyEntry(SERVER_ALIAS, keyPair.getPrivate(), PASSWORD, new X509Certificate[] { certificate });
			keyStore.store(new FileOutputStream(serverKeyStore), PASSWORD);
		} else {
			keyStore.load(new FileInputStream(serverKeyStore), PASSWORD);
		}
		final Key serverPrivateKey = keyStore.getKey(SERVER_ALIAS, PASSWORD);
		if (serverPrivateKey instanceof PrivateKey) {
			serverCertificate = (X509Certificate) keyStore.getCertificate(SERVER_ALIAS);
			serverCertificateChain = Arrays.stream(keyStore.getCertificateChain(SERVER_ALIAS))
					.map(X509Certificate.class::cast).toArray(X509Certificate[]::new);
			final PublicKey serverPublicKey = serverCertificate.getPublicKey();
			serverKeyPair = new KeyPair(serverPublicKey, (PrivateKey) serverPrivateKey);
		}

	}

}
