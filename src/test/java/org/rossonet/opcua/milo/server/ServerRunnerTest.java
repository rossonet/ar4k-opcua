package org.rossonet.opcua.milo.server;

import org.rossonet.opcua.milo.server.conf.OpcUaServerConfiguration;
import org.rossonet.opcua.milo.server.listener.ShutdownListener;
import org.rossonet.opcua.milo.server.listener.ShutdownReason;
import org.rossonet.opcua.milo.server.storage.OnMemoryStorageController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerRunnerTest {

	public static boolean running = true;

	private static final Logger logger = LoggerFactory.getLogger(ServerRunnerTest.class);
	private static OpcUaServerConfiguration opcServerConfiguration;

	private static Ar4kOpcUaServer server = null;

	public static OpcUaServerConfiguration getOpcServerConfiguration() {
		return opcServerConfiguration;
	}

	public static Ar4kOpcUaServer getServer() {
		return server;
	}

	public static boolean isRunning() {
		return running;
	}

	public static void main(final String[] args) throws Exception {
		logger.info("starting OPCUA server");
		runMiloServerExample();
		while (running) {
			Thread.sleep(2000);
		}
		stopMiloServerExample();
		logger.info("OPCUA server shutdown completed!");
	}

	public static void runMiloServerExample() throws Exception {
		opcServerConfiguration = new OpcUaServerConfiguration();
		final OnMemoryStorageController storageController = new LoggerStorageController();
		server = Ar4kOpcUaServer.getNewServer(opcServerConfiguration, storageController);
		final ShutdownListener shutdownReason = new ShutdownListener() {
			@Override
			public void shutdown(final ShutdownReason reason) {
				logger.warn("shutdown reason " + reason);
				running = false;
			}
		};
		server.addShutdownHook(shutdownReason);
		server.addAuditHook(new LogAuditListener());
		server.startup();
	}

	public static void stopMiloServerExample() throws Exception {
		if (server != null) {
			server.shutdown();
		}
	}

	public static void waitStarted() throws InterruptedException {
		if (server != null) {
			server.waitServerStarted();
		}

	}
}
