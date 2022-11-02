/*
 * Copyright (c) 2019 the Eclipse Milo Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.rossonet.opcua.milo.server.namespace.type;

import org.eclipse.milo.opcua.stack.core.serialization.SerializationContext;
import org.eclipse.milo.opcua.stack.core.serialization.UaDecoder;
import org.eclipse.milo.opcua.stack.core.serialization.UaEncoder;
import org.eclipse.milo.opcua.stack.core.serialization.UaEnumeration;
import org.eclipse.milo.opcua.stack.core.serialization.codecs.GenericDataTypeCodec;
import org.eclipse.milo.opcua.stack.core.types.builtin.ExpandedNodeId;
import org.rossonet.opcua.milo.server.namespace.ManagedNamespace;

public enum CustomEnumType implements UaEnumeration {

	Field0(0), Field1(1), Field2(2);

	public static class Codec extends GenericDataTypeCodec<CustomEnumType> {
		@Override
		public CustomEnumType decode(final SerializationContext context, final UaDecoder decoder) {
			return CustomEnumType.from(decoder.readInt32(null));
		}

		@Override
		public void encode(final SerializationContext context, final UaEncoder encoder, final CustomEnumType value) {
			encoder.writeInt32(null, value.getValue());
		}

		@Override
		public Class<CustomEnumType> getType() {
			return CustomEnumType.class;
		}
	}

	public static final ExpandedNodeId TYPE_ID = ExpandedNodeId
			.parse(String.format("nsu=%s;s=%s", ManagedNamespace.NAMESPACE_URI, "DataType.CustomEnumType"));

	public static CustomEnumType from(final int value) {
		switch (value) {
		case 0:
			return Field0;
		case 1:
			return Field1;
		case 2:
			return Field2;
		default:
			return null;
		}
	}

	private final int value;

	CustomEnumType(final int value) {
		this.value = value;
	}

	@Override
	public int getValue() {
		return value;
	}

}
