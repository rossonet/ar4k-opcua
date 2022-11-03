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

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;

import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.milo.opcua.sdk.core.ValueRanks;
import org.eclipse.milo.opcua.sdk.server.api.methods.AbstractMethodInvocationHandler;
import org.eclipse.milo.opcua.sdk.server.nodes.UaMethodNode;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.enumerated.StructureType;
import org.eclipse.milo.opcua.stack.core.types.structured.Argument;
import org.eclipse.milo.opcua.stack.core.types.structured.StructureDefinition;
import org.eclipse.milo.opcua.stack.core.types.structured.StructureDescription;
import org.eclipse.milo.opcua.stack.core.types.structured.StructureField;
import org.rossonet.opcua.milo.server.namespace.ManagedNamespace;
import org.rossonet.opcua.milo.server.namespace.type.CustomUnionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jsonldjava.utils.JsonUtils;

public class GenerateTypeObjectMethod extends AbstractMethodInvocationHandler {

	private static final String DTDLV2_MD_LINK = "https://github.com/rossonet/ar4k-opcua/blob/main/dtdlv2.md";

	public static final Argument DTDL_JSON_DESCRIPTION = new Argument("dtdlJson", Identifiers.String, ValueRanks.Scalar,
			null,
			new LocalizedText("Digital Twins Definition Language (DTDL) in json format. ( " + DTDLV2_MD_LINK + " )"));

	private static final Logger logger = LoggerFactory.getLogger(GenerateTypeObjectMethod.class);

	private final ManagedNamespace managedNamespace;

	public GenerateTypeObjectMethod(ManagedNamespace managedNamespace, UaMethodNode methodNode) {
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
	protected Variant[] invoke(InvocationContext invocationContext, Variant[] inputValues) throws UaException {
		try {
			if (inputValues != null) {
				final String dtdlV2String = (String) inputValues[0].getValue();
				final Object data = JsonUtils.fromString(dtdlV2String);
				if (data instanceof Map) {
					for (final Entry<String, Object> v : ((Map<String, Object>) data).entrySet()) {
						logger.info(v.getKey() + " -> " + v.getValue());
					}
				} else {
					logger.info("data is " + data.getClass());
				}
			}
			final NodeId dataTypeId = CustomUnionType.TYPE_ID
					.toNodeIdOrThrow(managedNamespace.getServer().getNamespaceTable());
			final NodeId binaryEncodingId = CustomUnionType.BINARY_ENCODING_ID
					.toNodeIdOrThrow(managedNamespace.getServer().getNamespaceTable());
			managedNamespace.getDictionaryManager().registerUnionCodec(new CustomUnionType.Codec().asBinaryCodec(),
					"CustomUnionType", dataTypeId, binaryEncodingId);
			final StructureField[] fields = new StructureField[] {
					new StructureField("foo", LocalizedText.NULL_VALUE, Identifiers.UInt32, ValueRanks.Scalar, null,
							managedNamespace.getServer().getConfig().getLimits().getMaxStringLength(), false),
					new StructureField("bar", LocalizedText.NULL_VALUE, Identifiers.String, ValueRanks.Scalar, null,
							uint(0), false) };
			final StructureDefinition definition = new StructureDefinition(binaryEncodingId, Identifiers.Structure,
					StructureType.Union, fields);
			final StructureDescription description = new StructureDescription(dataTypeId,
					new QualifiedName(managedNamespace.getNamespaceIndex(), "CustomUnionType"), definition);
			managedNamespace.getDictionaryManager().registerStructureDescription(description, binaryEncodingId);
			logger.info("added " + dataTypeId.toString() + " with binaryEncoder " + binaryEncodingId.toString());
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
			throw new UaException(StatusCode.BAD.getValue(), e.getMessage(), e);
		}
		return new Variant[0];
	}

}
