/*
 * Copyright (c) 2019 the Eclipse Milo Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.milo.examples.server.types;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;

import org.eclipse.milo.opcua.stack.core.UaSerializationException;
import org.eclipse.milo.opcua.stack.core.serialization.SerializationContext;
import org.eclipse.milo.opcua.stack.core.serialization.UaDecoder;
import org.eclipse.milo.opcua.stack.core.serialization.UaEncoder;
import org.eclipse.milo.opcua.stack.core.serialization.UaStructure;
import org.eclipse.milo.opcua.stack.core.serialization.codecs.GenericDataTypeCodec;
import org.eclipse.milo.opcua.stack.core.types.builtin.ExpandedNodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.rossonet.opcua.milo.server.conf.OpcUaServerConfiguration;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class CustomStructType implements UaStructure {

	public static class Codec extends GenericDataTypeCodec<CustomStructType> {
		@Override
		public CustomStructType decode(final SerializationContext context, final UaDecoder decoder)
				throws UaSerializationException {

			final String foo = decoder.readString("Foo");
			final UInteger bar = decoder.readUInt32("Bar");
			final boolean baz = decoder.readBoolean("Baz");
			final CustomEnumType customEnumType = decoder.readEnum("CustomEnumType", CustomEnumType.class);

			return new CustomStructType(foo, bar, baz, customEnumType);
		}

		@Override
		public void encode(final SerializationContext context, final UaEncoder encoder, final CustomStructType value)
				throws UaSerializationException {

			encoder.writeString("Foo", value.foo);
			encoder.writeUInt32("Bar", value.bar);
			encoder.writeBoolean("Baz", value.baz);
			encoder.writeEnum("CustomEnumType", value.customEnumType);
		}

		@Override
		public Class<CustomStructType> getType() {
			return CustomStructType.class;
		}
	}

	public static final ExpandedNodeId BINARY_ENCODING_ID = ExpandedNodeId.parse(String.format("nsu=%s;s=%s",
			OpcUaServerConfiguration.DEFAULT_URI_ROSSONET_OPCUA_SERVER, "DataType.CustomStructType.BinaryEncoding"));

	public static final ExpandedNodeId TYPE_ID = ExpandedNodeId.parse(String.format("nsu=%s;s=%s",
			OpcUaServerConfiguration.DEFAULT_URI_ROSSONET_OPCUA_SERVER, "DataType.CustomStructType"));
	private final UInteger bar;
	private final boolean baz;

	private final CustomEnumType customEnumType;

	private final String foo;

	public CustomStructType() {
		this(null, uint(0), false, CustomEnumType.Field0);
	}

	public CustomStructType(final String foo, final UInteger bar, final boolean baz,
			final CustomEnumType customEnumType) {
		this.foo = foo;
		this.bar = bar;
		this.baz = baz;
		this.customEnumType = customEnumType;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		final CustomStructType that = (CustomStructType) o;
		return baz == that.baz && Objects.equal(foo, that.foo) && Objects.equal(bar, that.bar)
				&& customEnumType == that.customEnumType;
	}

	public UInteger getBar() {
		return bar;
	}

	@Override
	public ExpandedNodeId getBinaryEncodingId() {
		return BINARY_ENCODING_ID;
	}

	public CustomEnumType getCustomEnumType() {
		return customEnumType;
	}

	public String getFoo() {
		return foo;
	}

	@Override
	public ExpandedNodeId getTypeId() {
		return TYPE_ID;
	}

	@Override
	public ExpandedNodeId getXmlEncodingId() {
		// XML encoding not supported
		return ExpandedNodeId.NULL_VALUE;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(foo, bar, baz, customEnumType);
	}

	public boolean isBaz() {
		return baz;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("foo", foo).add("bar", bar).add("baz", baz)
				.add("customEnumType", customEnumType).toString();
	}

}
