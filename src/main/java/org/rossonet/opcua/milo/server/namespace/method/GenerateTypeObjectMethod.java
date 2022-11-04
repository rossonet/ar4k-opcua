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
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.structured.Argument;
import org.rossonet.opcua.milo.server.namespace.ManagedNamespace;
import org.rossonet.opcua.milo.utils.MiloHelper;
import org.rossonet.opcua.milo.utils.dtdl.InterfaceObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenerateTypeObjectMethod extends AbstractMethodInvocationHandler {
	private static final String _DTDLV2_MD_LINK = "https://github.com/rossonet/ar4k-opcua/blob/main/dtdlv2.md";

	private static final Argument DTDL_JSON_DESCRIPTION = new Argument("dtdlJson", Identifiers.String,
			ValueRanks.Scalar, null,
			new LocalizedText("Digital Twins Definition Language (DTDL) in json format. ( " + _DTDLV2_MD_LINK + " )"));

	private static final Logger logger = LoggerFactory.getLogger(GenerateTypeObjectMethod.class);

	private final ManagedNamespace managedNamespace;

	public GenerateTypeObjectMethod(final ManagedNamespace managedNamespace, final UaMethodNode methodNode) {
		super(methodNode);
		this.managedNamespace = managedNamespace;
	}

	@Override
	public Argument[] getInputArguments() {
		return new Argument[] { DTDL_JSON_DESCRIPTION };
	}

	@Override
	public Argument[] getOutputArguments() {
		return new Argument[0];
	}

	@Override
	protected Variant[] invoke(final InvocationContext invocationContext, final Variant[] inputValues)
			throws UaException {
		try {
			final String dtdlV2String = (String) inputValues[0].getValue();
			final InterfaceObject templateObject = MiloHelper.generateTypeObjectFromDtdl(managedNamespace,
					dtdlV2String);
			// TODO creare oggetto opc e agganciarlo al namespace
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
			throw new UaException(StatusCode.BAD.getValue(), e.getMessage(), e);
		}
		return new Variant[0];
	}

}
