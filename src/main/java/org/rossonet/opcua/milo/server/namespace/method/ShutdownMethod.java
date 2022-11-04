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

import org.eclipse.milo.opcua.sdk.core.ValueRanks;
import org.eclipse.milo.opcua.sdk.server.api.methods.AbstractMethodInvocationHandler;
import org.eclipse.milo.opcua.sdk.server.nodes.UaMethodNode;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.structured.Argument;
import org.rossonet.opcua.milo.server.Ar4kOpcUaServer;
import org.rossonet.opcua.milo.server.listener.AuditListener;
import org.rossonet.opcua.milo.server.listener.ShutdownListener;
import org.rossonet.opcua.milo.server.listener.ShutdownReason;
import org.rossonet.opcua.milo.server.listener.ShutdownReason.ReasonCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShutdownMethod extends AbstractMethodInvocationHandler {

	public static final Argument REASON = new Argument("shutdown-reason", Identifiers.ServerStatusType_ShutdownReason,
			ValueRanks.Scalar, null, new LocalizedText("The reason for the shutdown."));
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final Ar4kOpcUaServer server;

	public ShutdownMethod(final Ar4kOpcUaServer server, final UaMethodNode node) {
		super(node);
		this.server = server;
	}

	@Override
	public Argument[] getInputArguments() {
		return new Argument[] { REASON };
	}

	@Override
	public Argument[] getOutputArguments() {
		return new Argument[0];
	}

	public void notifyListeners(final String reasonText) {
		final ShutdownReason reason = new ShutdownReason(ReasonCategory.NORMAL_SHUTDOWN, reasonText);
		for (final ShutdownListener shutdownListener : server.listShutdownHooks()) {
			try {
				shutdownListener.shutdown(reason);
			} catch (final Exception a) {
				logger.error("invoke shutdown hook", a);
			}
		}
		for (final AuditListener auditListener : server.listAuditHooks()) {
			try {
				auditListener.notifyShutdown(reason);
			} catch (final Exception a) {
				logger.error("invoke audit hook", a);
			}
		}
	}

	@Override
	protected Variant[] invoke(final InvocationContext invocationContext, final Variant[] inputValues) {
		logger.info("Invoking shutdown() method of objectId={}", invocationContext.getObjectId());
		notifyListeners(inputValues[0].toString());
		return new Variant[0];
	}

}
