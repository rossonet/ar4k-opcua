package org.rossonet.opcua.milo.server;

import org.eclipse.milo.examples.RunnerMiloTests;
import org.rossonet.opcua.milo.server.listener.ShutdownListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerRunnerTest {

	public static boolean running = true;

	private static final Logger logger = LoggerFactory.getLogger(RunnerMiloTests.class);
	private static OpcUaServerConfiguration opcServerConfiguration;

	private static Ar4kOpcUaServer server = null;

	public static void main(final String[] args) throws Exception {
		logger.info("starting OPCUA server");
		runMiloServerExample();
		while (running) {
			Thread.sleep(2000);
		}
		stopMiloServerExample();
		logger.info("OPCUA server shutdown completed!");
	}

	private static void runMiloServerExample() throws Exception {
		opcServerConfiguration = new OpcUaServerConfiguration();
		server = Ar4kOpcUaServer.getNewServer(opcServerConfiguration);
		final ShutdownListener shutdownReason = new ShutdownListener() {
			@Override
			public void shutdown(final ShutdownReason reason) {
				logger.warn("shutdown reason " + reason);
				running = false;
			}
		};
		server.addShutdownHook(shutdownReason);
		server.startup();
	}

	private static void stopMiloServerExample() throws Exception {
		if (server != null) {
			server.shutdown();
		}
	}
}
