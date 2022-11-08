package org.rossonet.opcua.milo.utils;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.ubyte;
import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;
import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.ulong;
import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.ushort;

import java.io.IOException;
import java.util.UUID;

import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.eclipse.milo.opcua.sdk.server.UaNodeManager;
import org.eclipse.milo.opcua.sdk.server.api.NodeManager;
import org.eclipse.milo.opcua.sdk.server.api.services.NodeManagementServices.AddNodesContext;
import org.eclipse.milo.opcua.sdk.server.nodes.UaDataTypeNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaMethodNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaNodeContext;
import org.eclipse.milo.opcua.sdk.server.nodes.UaObjectNode.UaObjectNodeBuilder;
import org.eclipse.milo.opcua.sdk.server.nodes.UaObjectTypeNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaReferenceTypeNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaVariableNode.UaVariableNodeBuilder;
import org.eclipse.milo.opcua.sdk.server.nodes.UaVariableTypeNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaViewNode;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.ByteString;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.DateTime;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.builtin.XmlElement;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UByte;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.enumerated.NodeClass;
import org.eclipse.milo.opcua.stack.core.types.structured.AddNodesItem;
import org.rossonet.opcua.milo.server.namespace.ManagedNamespace;
import org.rossonet.opcua.milo.utils.dtdl.InterfaceObject;

import com.fasterxml.jackson.core.JsonParseException;

public class MiloHelper {

	public static final Object[][] STATIC_ARRAY_NODES = new Object[][] { { "BooleanArray", Identifiers.Boolean, false },
			{ "ByteArray", Identifiers.Byte, ubyte(0) }, { "SByteArray", Identifiers.SByte, (byte) 0x00 },
			{ "Int16Array", Identifiers.Int16, (short) 16 }, { "Int32Array", Identifiers.Int32, 32 },
			{ "Int64Array", Identifiers.Int64, 64L }, { "UInt16Array", Identifiers.UInt16, ushort(16) },
			{ "UInt32Array", Identifiers.UInt32, uint(32) }, { "UInt64Array", Identifiers.UInt64, ulong(64L) },
			{ "FloatArray", Identifiers.Float, 3.14f }, { "DoubleArray", Identifiers.Double, 3.14d },
			{ "StringArray", Identifiers.String, "string value" },
			{ "DateTimeArray", Identifiers.DateTime, DateTime.now() },
			{ "GuidArray", Identifiers.Guid, UUID.randomUUID() },
			{ "ByteStringArray", Identifiers.ByteString, new ByteString(new byte[] { 0x01, 0x02, 0x03, 0x04 }) },
			{ "XmlElementArray", Identifiers.XmlElement, new XmlElement("<a>hello</a>") },
			{ "LocalizedTextArray", Identifiers.LocalizedText, LocalizedText.english("localized text") },
			{ "QualifiedNameArray", Identifiers.QualifiedName, new QualifiedName(1234, "defg") },
			{ "NodeIdArray", Identifiers.NodeId, new NodeId(1234, "abcd") } };

	public static final Object[][] STATIC_SCALAR_NODES = new Object[][] {
			{ "Boolean", Identifiers.Boolean, new Variant(false) },
			{ "Byte", Identifiers.Byte, new Variant(ubyte(0x00)) },
			{ "SByte", Identifiers.SByte, new Variant((byte) 0x00) },
			{ "Integer", Identifiers.Integer, new Variant(32) },
			{ "Int16", Identifiers.Int16, new Variant((short) 16) }, { "Int32", Identifiers.Int32, new Variant(32) },
			{ "Int64", Identifiers.Int64, new Variant(64L) },
			{ "UInteger", Identifiers.UInteger, new Variant(uint(32)) },
			{ "UInt16", Identifiers.UInt16, new Variant(ushort(16)) },
			{ "UInt32", Identifiers.UInt32, new Variant(uint(32)) },
			{ "UInt64", Identifiers.UInt64, new Variant(ulong(64L)) },
			{ "Float", Identifiers.Float, new Variant(3.14f) }, { "Double", Identifiers.Double, new Variant(3.14d) },
			{ "String", Identifiers.String, new Variant("string value") },
			{ "DateTime", Identifiers.DateTime, new Variant(DateTime.now()) },
			{ "Guid", Identifiers.Guid, new Variant(UUID.randomUUID()) },
			{ "ByteString", Identifiers.ByteString,
					new Variant(new ByteString(new byte[] { 0x01, 0x02, 0x03, 0x04 })) },
			{ "XmlElement", Identifiers.XmlElement, new Variant(new XmlElement("<a>hello</a>")) },
			{ "LocalizedText", Identifiers.LocalizedText, new Variant(LocalizedText.english("localized text")) },
			{ "QualifiedName", Identifiers.QualifiedName, new Variant(new QualifiedName(1234, "defg")) },
			{ "NodeId", Identifiers.NodeId, new Variant(new NodeId(1234, "abcd")) },
			{ "Variant", Identifiers.BaseDataType, new Variant(32) },
			{ "Duration", Identifiers.Duration, new Variant(1.0) },
			{ "UtcTime", Identifiers.UtcTime, new Variant(DateTime.now()) }, };

	public static UaNode createNodeFromAddNodeItem(final ManagedNamespace managedNamespace,
			final AddNodesContext context, final AddNodesItem addNodeItem) throws Exception {
		final NodeId nodeId = addNodeItem.getRequestedNewNodeId()
				.toNodeIdOrThrow(context.getServer().getNamespaceTable());
		final QualifiedName browseName = addNodeItem.getBrowseName();
		final LocalizedText displayName = LocalizedText.english(browseName.getName());
		final LocalizedText description = LocalizedText.english(browseName.getName());
		final UInteger writeMask = null;
		final UInteger userWriteMask = null;
		final boolean isAbstract = false;
		final DataValue value = null;
		final NodeId dataType = null;
		final Integer valueRank = null;
		final UInteger[] arrayDimensions = null;
		final Boolean isAbstrac = null;
		final Boolean containsNoLoops = null;
		final UByte eventNotifier = null;
		return createNodeFromParameters(managedNamespace.getNodeManager(), context, addNodeItem, nodeId, browseName,
				displayName, description, writeMask, userWriteMask, isAbstract, value, dataType, valueRank,
				arrayDimensions, isAbstrac, containsNoLoops, eventNotifier);

	}

	public static UaNode createNodeFromParameters(final UaNodeManager uaNodeManager, final AddNodesContext context,
			final AddNodesItem addNodeItem, final NodeId nodeId, final QualifiedName browseName,
			final LocalizedText displayName, final LocalizedText description, final UInteger writeMask,
			final UInteger userWriteMask, final boolean isAbstract, final DataValue value, final NodeId dataType,
			final Integer valueRank, final UInteger[] arrayDimensions, final Boolean isAbstrac,
			final Boolean containsNoLoops, final UByte eventNotifier) throws UaException {
		final UaNodeContext nodeContext = new UaNodeContext() {
			@Override
			public NodeManager<UaNode> getNodeManager() {
				return uaNodeManager;
			}

			@Override
			public OpcUaServer getServer() {
				return context.getServer();
			}
		};
		UaNode node = null;
		switch (NodeClass.from(addNodeItem.getNodeClass().getValue())) {
		case DataType:
			node = new UaDataTypeNode(nodeContext, nodeId, browseName, displayName, description, writeMask,
					userWriteMask, isAbstract);
			break;
		case Method:
			node = UaMethodNode.builder(nodeContext).setNodeId(nodeId).setBrowseName(browseName)
					.setDisplayName(displayName).setDescription(description).setWriteMask(writeMask)
					.setUserWriteMask(userWriteMask).build();
			break;
		case Object:
			node = new UaObjectNodeBuilder(nodeContext).setNodeId(nodeId).setBrowseName(browseName)
					.setDisplayName(displayName).setDescription(description).setWriteMask(writeMask)
					.setUserWriteMask(userWriteMask).build();
			break;
		case ObjectType:
			node = UaObjectTypeNode.builder(nodeContext).setNodeId(nodeId).setBrowseName(browseName)
					.setDisplayName(displayName).setDescription(description).setWriteMask(writeMask)
					.setUserWriteMask(userWriteMask).build();
			break;
		case ReferenceType:
			final Boolean symmetric = null;
			final LocalizedText inverseName = null;
			node = new UaReferenceTypeNode(nodeContext, nodeId, browseName, displayName, description, writeMask,
					userWriteMask, isAbstract, symmetric, inverseName);
			break;
		case Unspecified:
			throw new UaException(StatusCode.BAD.getValue(),
					"nodeClass is Unspecified [" + NodeClass.Unspecified.getValue() + "]");
		case Variable:
			node = new UaVariableNodeBuilder(nodeContext).setNodeId(nodeId).setBrowseName(browseName)
					.setDisplayName(displayName).setDescription(description).setWriteMask(writeMask)
					.setUserWriteMask(userWriteMask).build();
			break;
		case VariableType:

			node = new UaVariableTypeNode(nodeContext, nodeId, browseName, displayName, description, writeMask,
					userWriteMask, value, dataType, valueRank, arrayDimensions, isAbstrac);
			break;
		case View:
			node = new UaViewNode(nodeContext, nodeId, browseName, displayName, description, writeMask, userWriteMask,
					containsNoLoops, eventNotifier);
			break;
		default:
			throw new UaException(StatusCode.BAD.getValue(),
					"nodeClass is not valid, value:" + addNodeItem.getNodeClass());
		}
		return node;
	}

	public static InterfaceObject generateTypeObjectFromDtdl(final ManagedNamespace managedNamespace,
			final String dtdlV2String) throws JsonParseException, IOException, Exception {
		final InterfaceObject templateObject = InterfaceObject.newFromDtdlV2(dtdlV2String);
		return templateObject;

	}

	private MiloHelper() {
		throw new UnsupportedOperationException("Just for static usage");
	}

}
