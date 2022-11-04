/*
 * Copyright (c) 2019 the Eclipse Milo Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.milo.examples.server.methods;

import org.eclipse.milo.examples.RunnerMiloTests;
import org.eclipse.milo.opcua.sdk.core.ValueRanks;
import org.eclipse.milo.opcua.sdk.server.api.methods.AbstractMethodInvocationHandler;
import org.eclipse.milo.opcua.sdk.server.nodes.UaMethodNode;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.structured.Argument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShutdownMethod extends AbstractMethodInvocationHandler {

	public static final Argument REASON = new Argument("shutdown-reason", Identifiers.String, ValueRanks.Scalar, null,
			new LocalizedText("The reason for the shutdown."));
	private final Logger logger = LoggerFactory.getLogger(getClass());

	public ShutdownMethod(final UaMethodNode node) {
		super(node);
	}

	@Override
	public Argument[] getInputArguments() {
		return new Argument[] { REASON };
	}

	@Override
	public Argument[] getOutputArguments() {
		return new Argument[0];
	}

	@Override
	protected Variant[] invoke(final InvocationContext invocationContext, final Variant[] inputValues) {
		logger.info("Invoking shutdown() method of objectId={}", invocationContext.getObjectId());

		logger.warn("REASON OF SHUTDOWN " + inputValues[0].toString());
		RunnerMiloTests.running = false;
		return new Variant[0];
	}

}
