/*
 * Copyright (c) 2019 the Eclipse Milo Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.rossonet.opcua.milo.server.namespace.method;

import org.eclipse.milo.opcua.sdk.server.api.methods.AbstractMethodInvocationHandler;
import org.eclipse.milo.opcua.sdk.server.nodes.UaMethodNode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.structured.Argument;
import org.rossonet.opcua.milo.server.Ar4kOpcUaServer;
import org.rossonet.opcua.milo.server.listener.ShutdownListener;
import org.rossonet.opcua.milo.server.listener.ShutdownListener.ShutdownReason;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShutdownMethod extends AbstractMethodInvocationHandler {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final Ar4kOpcUaServer server;

	public ShutdownMethod(Ar4kOpcUaServer server, UaMethodNode node) {
		super(node);
		this.server = server;
	}

	@Override
	public Argument[] getInputArguments() {
		return new Argument[0];
	}

	@Override
	public Argument[] getOutputArguments() {
		return new Argument[0];
	}

	@Override
	protected Variant[] invoke(InvocationContext invocationContext, Variant[] inputValues) {
		logger.info("Invoking shutdown() method of objectId={}", invocationContext.getObjectId());
		for (final ShutdownListener shutdownListener : server.listShutdownHooks()) {
			try {
				final ShutdownReason reason = new ShutdownReason();
				shutdownListener.shutdown(reason);
			} catch (final Exception a) {
				logger.error("invoke shutdown hook", a);
			}
		}
		return new Variant[0];
	}

}
