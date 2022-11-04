package org.rossonet.opcua.milo.utils;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;

import java.io.IOException;

import org.eclipse.milo.opcua.sdk.core.ValueRanks;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.enumerated.StructureType;
import org.eclipse.milo.opcua.stack.core.types.structured.StructureDefinition;
import org.eclipse.milo.opcua.stack.core.types.structured.StructureDescription;
import org.eclipse.milo.opcua.stack.core.types.structured.StructureField;
import org.rossonet.opcua.milo.server.namespace.ManagedNamespace;
import org.rossonet.opcua.milo.server.namespace.type.CustomUnionType;
import org.rossonet.opcua.milo.utils.dtdl.InterfaceObject;
import org.rossonet.opcua.trace.ControlTrace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;

public class MiloHelper {

	private static final Logger logger = LoggerFactory.getLogger(MiloHelper.class);

	public static void generateTypeObjectFromDtdl(ManagedNamespace managedNamespace, final String dtdlV2String)
			throws JsonParseException, IOException, Exception {
		final InterfaceObject templateObject = InterfaceObject.newFromDtdlV2(dtdlV2String);
// TODO realizzare configurazione
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

		// trace per cluster e ripristino configurazione
		ControlTrace.getInstance().registerGenerateTypeObjectFromDtdl(dtdlV2String);
	}

	private MiloHelper() {
		// only for static usage
	}

}
