/*
 * Copyright (c) 2021 the Eclipse Milo Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.milo.examples.server;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.ubyte;
import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;
import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.ushort;

import java.lang.reflect.Array;
import java.util.Random;
import java.util.UUID;

import org.eclipse.milo.examples.server.methods.GenerateEventMethod;
import org.eclipse.milo.examples.server.methods.ShutdownMethod;
import org.eclipse.milo.examples.server.methods.SqrtMethod;
import org.eclipse.milo.examples.server.types.CustomEnumType;
import org.eclipse.milo.examples.server.types.CustomStructType;
import org.eclipse.milo.examples.server.types.CustomUnionType;
import org.eclipse.milo.opcua.sdk.core.AccessLevel;
import org.eclipse.milo.opcua.sdk.core.Reference;
import org.eclipse.milo.opcua.sdk.core.ValueRank;
import org.eclipse.milo.opcua.sdk.core.ValueRanks;
import org.eclipse.milo.opcua.sdk.server.Lifecycle;
import org.eclipse.milo.opcua.sdk.server.model.nodes.objects.BaseEventTypeNode;
import org.eclipse.milo.opcua.sdk.server.model.nodes.objects.ServerTypeNode;
import org.eclipse.milo.opcua.sdk.server.model.nodes.variables.AnalogItemTypeNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaDataTypeNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaFolderNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaMethodNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaObjectNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaObjectTypeNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaVariableNode;
import org.eclipse.milo.opcua.sdk.server.nodes.factories.NodeFactory;
import org.eclipse.milo.opcua.sdk.server.nodes.filters.AttributeFilters;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.BuiltinDataType;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.ByteString;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.DateTime;
import org.eclipse.milo.opcua.stack.core.types.builtin.ExtensionObject;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.enumerated.StructureType;
import org.eclipse.milo.opcua.stack.core.types.structured.EnumDefinition;
import org.eclipse.milo.opcua.stack.core.types.structured.EnumDescription;
import org.eclipse.milo.opcua.stack.core.types.structured.EnumField;
import org.eclipse.milo.opcua.stack.core.types.structured.Range;
import org.eclipse.milo.opcua.stack.core.types.structured.StructureDefinition;
import org.eclipse.milo.opcua.stack.core.types.structured.StructureDescription;
import org.eclipse.milo.opcua.stack.core.types.structured.StructureField;
import org.rossonet.opcua.milo.server.Ar4kOpcUaServer;
import org.rossonet.opcua.milo.utils.MiloHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExampleNamespace {

	private static Thread eventThread;

	private static boolean keepPostingEvents = true;

	private static final Logger logger = LoggerFactory.getLogger(ExampleNamespace.class);

	private static final Random random = new Random();

	public static void addNodesAndEdges(final Ar4kOpcUaServer server) {
		createAndAddNodes(server);
		final Lifecycle eventDemoLifecycle = getEventDemoLifecycle(server);
		server.getNamespace().getLifecycleManager().addLifecycle(eventDemoLifecycle);
		eventDemoLifecycle.startup();
	}

	private static void addAdminReadableNodes(final Ar4kOpcUaServer server, final UaFolderNode rootNode) {
		final UaFolderNode adminFolder = new UaFolderNode(server.getNamespace().getNodeContext(),
				server.getNamespace().newNodeId("HelloWorld/OnlyAdminCanRead"),
				server.getNamespace().newQualifiedName("OnlyAdminCanRead"), LocalizedText.english("OnlyAdminCanRead"));

		server.getNamespace().getNodeManager().addNode(adminFolder);
		rootNode.addOrganizes(adminFolder);

		final String name = "String";
		final UaVariableNode node = new UaVariableNode.UaVariableNodeBuilder(server.getNamespace().getNodeContext())
				.setNodeId(server.getNamespace().newNodeId("HelloWorld/OnlyAdminCanRead/" + name))
				.setAccessLevel(AccessLevel.READ_WRITE).setBrowseName(server.getNamespace().newQualifiedName(name))
				.setDisplayName(LocalizedText.english(name)).setDataType(Identifiers.String)
				.setTypeDefinition(Identifiers.BaseDataVariableType).build();

		node.setValue(new DataValue(new Variant("shh... don't tell the lusers")));
		node.getFilterChain().addLast(new RestrictedAccessFilter(identity -> {
			if ("admin".equals(identity)) {
				return AccessLevel.READ_WRITE;
			} else {
				return AccessLevel.NONE;
			}
		}));

		server.getNamespace().getNodeManager().addNode(node);
		adminFolder.addOrganizes(node);
	}

	private static void addAdminWritableNodes(final Ar4kOpcUaServer server, final UaFolderNode rootNode) {
		final UaFolderNode adminFolder = new UaFolderNode(server.getNamespace().getNodeContext(),
				server.getNamespace().newNodeId("HelloWorld/OnlyAdminCanWrite"),
				server.getNamespace().newQualifiedName("OnlyAdminCanWrite"),
				LocalizedText.english("OnlyAdminCanWrite"));

		server.getNamespace().getNodeManager().addNode(adminFolder);
		rootNode.addOrganizes(adminFolder);

		final String name = "String";
		final UaVariableNode node = new UaVariableNode.UaVariableNodeBuilder(server.getNamespace().getNodeContext())
				.setNodeId(server.getNamespace().newNodeId("HelloWorld/OnlyAdminCanWrite/" + name))
				.setAccessLevel(AccessLevel.READ_WRITE).setBrowseName(server.getNamespace().newQualifiedName(name))
				.setDisplayName(LocalizedText.english(name)).setDataType(Identifiers.String)
				.setTypeDefinition(Identifiers.BaseDataVariableType).build();

		node.setValue(new DataValue(new Variant("admin was here")));

		node.getFilterChain().addLast(new RestrictedAccessFilter(identity -> {
			if ("admin".equals(identity)) {
				return AccessLevel.READ_WRITE;
			} else {
				return AccessLevel.READ_ONLY;
			}
		}));

		server.getNamespace().getNodeManager().addNode(node);
		adminFolder.addOrganizes(node);
	}

	private static void addArrayNodes(final Ar4kOpcUaServer server, final UaFolderNode rootNode) {
		final UaFolderNode arrayTypesFolder = new UaFolderNode(server.getNamespace().getNodeContext(),
				server.getNamespace().newNodeId("HelloWorld/ArrayTypes"),
				server.getNamespace().newQualifiedName("ArrayTypes"), LocalizedText.english("ArrayTypes"));

		server.getNamespace().getNodeManager().addNode(arrayTypesFolder);
		rootNode.addOrganizes(arrayTypesFolder);

		for (final Object[] os : MiloHelper.STATIC_ARRAY_NODES) {
			final String name = (String) os[0];
			final NodeId typeId = (NodeId) os[1];
			final Object value = os[2];
			final Object array = Array.newInstance(value.getClass(), 5);
			for (int i = 0; i < 5; i++) {
				Array.set(array, i, value);
			}
			final Variant variant = new Variant(array);

			UaVariableNode.build(server.getNamespace().getNodeContext(), builder -> {
				builder.setNodeId(server.getNamespace().newNodeId("HelloWorld/ArrayTypes/" + name));
				builder.setAccessLevel(AccessLevel.READ_WRITE);
				builder.setUserAccessLevel(AccessLevel.READ_WRITE);
				builder.setBrowseName(server.getNamespace().newQualifiedName(name));
				builder.setDisplayName(LocalizedText.english(name));
				builder.setDataType(typeId);
				builder.setTypeDefinition(Identifiers.BaseDataVariableType);
				builder.setValueRank(ValueRank.OneDimension.getValue());
				builder.setArrayDimensions(new UInteger[] { uint(0) });
				builder.setValue(new DataValue(variant));

				builder.addAttributeFilter(new AttributeLoggingFilter(AttributeId.Value::equals));

				builder.addReference(new Reference(builder.getNodeId(), Identifiers.Organizes,
						arrayTypesFolder.getNodeId().expanded(), Reference.Direction.INVERSE));

				return builder.buildAndAdd();
			});
		}
	}

	private static void addCustomEnumTypeVariable(final Ar4kOpcUaServer server, final UaFolderNode rootFolder)
			throws Exception {
		final NodeId dataTypeId = CustomEnumType.TYPE_ID
				.toNodeIdOrThrow(server.getNamespace().getServer().getNamespaceTable());

		@SuppressWarnings("deprecation")
		final UaVariableNode customEnumTypeVariable = UaVariableNode.builder(server.getNamespace().getNodeContext())
				.setNodeId(server.getNamespace().newNodeId("HelloWorld/CustomEnumTypeVariable"))
				.setAccessLevel(AccessLevel.READ_WRITE).setUserAccessLevel(AccessLevel.READ_WRITE)
				.setBrowseName(server.getNamespace().newQualifiedName("CustomEnumTypeVariable"))
				.setDisplayName(LocalizedText.english("CustomEnumTypeVariable")).setDataType(dataTypeId)
				.setTypeDefinition(Identifiers.BaseDataVariableType).build();

		customEnumTypeVariable.setValue(new DataValue(new Variant(CustomEnumType.Field1)));

		server.getNamespace().getNodeManager().addNode(customEnumTypeVariable);

		customEnumTypeVariable.addReference(new Reference(customEnumTypeVariable.getNodeId(), Identifiers.Organizes,
				rootFolder.getNodeId().expanded(), false));
	}

	@SuppressWarnings("deprecation")
	private static void addCustomObjectTypeAndInstance(final Ar4kOpcUaServer server, final UaFolderNode rootFolder) {
		// Define a new ObjectType called "MyObjectType".
		final UaObjectTypeNode objectTypeNode = UaObjectTypeNode.builder(server.getNamespace().getNodeContext())
				.setNodeId(server.getNamespace().newNodeId("ObjectTypes/MyObjectType"))
				.setBrowseName(server.getNamespace().newQualifiedName("MyObjectType"))
				.setDisplayName(LocalizedText.english("MyObjectType")).setIsAbstract(false).build();

		// "Foo" and "Bar" are members. These nodes are what are called "instance
		// declarations" by the spec.
		final UaVariableNode foo = UaVariableNode.builder(server.getNamespace().getNodeContext())
				.setNodeId(server.getNamespace().newNodeId("ObjectTypes/MyObjectType.Foo"))
				.setAccessLevel(AccessLevel.READ_WRITE).setBrowseName(server.getNamespace().newQualifiedName("Foo"))
				.setDisplayName(LocalizedText.english("Foo")).setDataType(Identifiers.Int16)
				.setTypeDefinition(Identifiers.BaseDataVariableType).build();

		foo.addReference(new Reference(foo.getNodeId(), Identifiers.HasModellingRule,
				Identifiers.ModellingRule_Mandatory.expanded(), true));

		foo.setValue(new DataValue(new Variant(0)));
		objectTypeNode.addComponent(foo);

		final UaVariableNode bar = UaVariableNode.builder(server.getNamespace().getNodeContext())
				.setNodeId(server.getNamespace().newNodeId("ObjectTypes/MyObjectType.Bar"))
				.setAccessLevel(AccessLevel.READ_WRITE).setBrowseName(server.getNamespace().newQualifiedName("Bar"))
				.setDisplayName(LocalizedText.english("Bar")).setDataType(Identifiers.String)
				.setTypeDefinition(Identifiers.BaseDataVariableType).build();

		bar.addReference(new Reference(bar.getNodeId(), Identifiers.HasModellingRule,
				Identifiers.ModellingRule_Mandatory.expanded(), true));

		bar.setValue(new DataValue(new Variant("bar")));
		objectTypeNode.addComponent(bar);

		// Tell the ObjectTypeManager about our new type.
		// This let's us use NodeFactory to instantiate instances of the type.
		server.getNamespace().getServer().getObjectTypeManager().registerObjectType(objectTypeNode.getNodeId(),
				UaObjectNode.class, UaObjectNode::new);

		// Add the inverse SubtypeOf relationship.
		objectTypeNode.addReference(new Reference(objectTypeNode.getNodeId(), Identifiers.HasSubtype,
				Identifiers.BaseObjectType.expanded(), false));

		// Add type definition and declarations to address space.
		server.getNamespace().getNodeManager().addNode(objectTypeNode);
		server.getNamespace().getNodeManager().addNode(foo);
		server.getNamespace().getNodeManager().addNode(bar);

		// Use NodeFactory to create instance of MyObjectType called "MyObject".
		// NodeFactory takes care of recursively instantiating MyObject member nodes
		// as well as adding all nodes to the address space.
		try {
			final UaObjectNode myObject = (UaObjectNode) server.getNamespace().getNodeFactory()
					.createNode(server.getNamespace().newNodeId("HelloWorld/MyObject"), objectTypeNode.getNodeId());
			myObject.setBrowseName(server.getNamespace().newQualifiedName("MyObject"));
			myObject.setDisplayName(LocalizedText.english("MyObject"));

			// Add forward and inverse references from the root folder.
			rootFolder.addOrganizes(myObject);

			myObject.addReference(new Reference(myObject.getNodeId(), Identifiers.Organizes,
					rootFolder.getNodeId().expanded(), false));
		} catch (final UaException e) {
			logger.error("Error creating MyObjectType instance: {}", e.getMessage(), e);
		}
	}

	@SuppressWarnings("deprecation")
	private static void addCustomStructTypeVariable(final Ar4kOpcUaServer server, final UaFolderNode rootFolder)
			throws Exception {
		final NodeId dataTypeId = CustomStructType.TYPE_ID
				.toNodeIdOrThrow(server.getNamespace().getServer().getNamespaceTable());

		final NodeId binaryEncodingId = CustomStructType.BINARY_ENCODING_ID
				.toNodeIdOrThrow(server.getNamespace().getServer().getNamespaceTable());

		final UaVariableNode customStructTypeVariable = UaVariableNode.builder(server.getNamespace().getNodeContext())
				.setNodeId(server.getNamespace().newNodeId("HelloWorld/CustomStructTypeVariable"))
				.setAccessLevel(AccessLevel.READ_WRITE).setUserAccessLevel(AccessLevel.READ_WRITE)
				.setBrowseName(server.getNamespace().newQualifiedName("CustomStructTypeVariable"))
				.setDisplayName(LocalizedText.english("CustomStructTypeVariable")).setDataType(dataTypeId)
				.setTypeDefinition(Identifiers.BaseDataVariableType).build();

		final CustomStructType value = new CustomStructType("foo", uint(42), true, CustomEnumType.Field0);

		final ExtensionObject xo = ExtensionObject.encodeDefaultBinary(
				server.getNamespace().getServer().getSerializationContext(), value, binaryEncodingId);

		customStructTypeVariable.setValue(new DataValue(new Variant(xo)));

		server.getNamespace().getNodeManager().addNode(customStructTypeVariable);

		customStructTypeVariable.addReference(new Reference(customStructTypeVariable.getNodeId(), Identifiers.Organizes,
				rootFolder.getNodeId().expanded(), false));
	}

	@SuppressWarnings("deprecation")
	private static void addCustomUnionTypeVariable(final Ar4kOpcUaServer server, final UaFolderNode rootFolder)
			throws Exception {
		final NodeId dataTypeId = CustomUnionType.TYPE_ID
				.toNodeIdOrThrow(server.getNamespace().getServer().getNamespaceTable());

		final NodeId binaryEncodingId = CustomUnionType.BINARY_ENCODING_ID
				.toNodeIdOrThrow(server.getNamespace().getServer().getNamespaceTable());

		final UaVariableNode customUnionTypeVariable = UaVariableNode.builder(server.getNamespace().getNodeContext())
				.setNodeId(server.getNamespace().newNodeId("HelloWorld/CustomUnionTypeVariable"))
				.setAccessLevel(AccessLevel.READ_WRITE).setUserAccessLevel(AccessLevel.READ_WRITE)
				.setBrowseName(server.getNamespace().newQualifiedName("CustomUnionTypeVariable"))
				.setDisplayName(LocalizedText.english("CustomUnionTypeVariable")).setDataType(dataTypeId)
				.setTypeDefinition(Identifiers.BaseDataVariableType).build();

		final CustomUnionType value = CustomUnionType.ofBar("hello");

		final ExtensionObject xo = ExtensionObject.encodeDefaultBinary(
				server.getNamespace().getServer().getSerializationContext(), value, binaryEncodingId);

		customUnionTypeVariable.setValue(new DataValue(new Variant(xo)));

		server.getNamespace().getNodeManager().addNode(customUnionTypeVariable);

		customUnionTypeVariable.addReference(new Reference(customUnionTypeVariable.getNodeId(), Identifiers.Organizes,
				rootFolder.getNodeId().expanded(), false));
	}

	private static void addDataAccessNodes(final Ar4kOpcUaServer server, final UaFolderNode rootNode) {
		// DataAccess folder
		final UaFolderNode dataAccessFolder = new UaFolderNode(server.getNamespace().getNodeContext(),
				server.getNamespace().newNodeId("HelloWorld/DataAccess"),
				server.getNamespace().newQualifiedName("DataAccess"), LocalizedText.english("DataAccess"));

		server.getNamespace().getNodeManager().addNode(dataAccessFolder);
		rootNode.addOrganizes(dataAccessFolder);

		try {
			final AnalogItemTypeNode node = (AnalogItemTypeNode) server.getNamespace().getNodeFactory().createNode(
					server.getNamespace().newNodeId("HelloWorld/DataAccess/AnalogValue"), Identifiers.AnalogItemType,
					new NodeFactory.InstantiationCallback() {
						@Override
						public boolean includeOptionalNode(final NodeId typeDefinitionId,
								final QualifiedName browseName) {
							return true;
						}
					});

			node.setBrowseName(server.getNamespace().newQualifiedName("AnalogValue"));
			node.setDisplayName(LocalizedText.english("AnalogValue"));
			node.setDataType(Identifiers.Double);
			node.setValue(new DataValue(new Variant(3.14d)));

			node.setEURange(new Range(0.0, 100.0));

			server.getNamespace().getNodeManager().addNode(node);
			dataAccessFolder.addOrganizes(node);
		} catch (final UaException e) {
			logger.error("Error creating AnalogItemType instance: {}", e.getMessage(), e);
		}
	}

	private static void addDynamicNodes(final Ar4kOpcUaServer server, final UaFolderNode rootNode) {
		final UaFolderNode dynamicFolder = new UaFolderNode(server.getNamespace().getNodeContext(),
				server.getNamespace().newNodeId("HelloWorld/Dynamic"),
				server.getNamespace().newQualifiedName("Dynamic"), LocalizedText.english("Dynamic"));

		server.getNamespace().getNodeManager().addNode(dynamicFolder);
		rootNode.addOrganizes(dynamicFolder);

		// Dynamic Boolean
		{
			final String name = "Boolean";
			final NodeId typeId = Identifiers.Boolean;
			final Variant variant = new Variant(false);

			final UaVariableNode node = new UaVariableNode.UaVariableNodeBuilder(server.getNamespace().getNodeContext())
					.setNodeId(server.getNamespace().newNodeId("HelloWorld/Dynamic/" + name))
					.setAccessLevel(AccessLevel.READ_WRITE).setBrowseName(server.getNamespace().newQualifiedName(name))
					.setDisplayName(LocalizedText.english(name)).setDataType(typeId)
					.setTypeDefinition(Identifiers.BaseDataVariableType).build();

			node.setValue(new DataValue(variant));

			node.getFilterChain().addLast(new AttributeLoggingFilter(),
					AttributeFilters.getValue(ctx -> new DataValue(new Variant(random.nextBoolean()))));

			server.getNamespace().getNodeManager().addNode(node);
			dynamicFolder.addOrganizes(node);
		}

		// Dynamic Int32
		{
			final String name = "Int32";
			final NodeId typeId = Identifiers.Int32;
			final Variant variant = new Variant(0);

			final UaVariableNode node = new UaVariableNode.UaVariableNodeBuilder(server.getNamespace().getNodeContext())
					.setNodeId(server.getNamespace().newNodeId("HelloWorld/Dynamic/" + name))
					.setAccessLevel(AccessLevel.READ_WRITE).setBrowseName(server.getNamespace().newQualifiedName(name))
					.setDisplayName(LocalizedText.english(name)).setDataType(typeId)
					.setTypeDefinition(Identifiers.BaseDataVariableType).build();

			node.setValue(new DataValue(variant));

			node.getFilterChain().addLast(new AttributeLoggingFilter(),
					AttributeFilters.getValue(ctx -> new DataValue(new Variant(random.nextInt()))));

			server.getNamespace().getNodeManager().addNode(node);
			dynamicFolder.addOrganizes(node);
		}

		// Dynamic Double
		{
			final String name = "Double";
			final NodeId typeId = Identifiers.Double;
			final Variant variant = new Variant(0.0);

			final UaVariableNode node = new UaVariableNode.UaVariableNodeBuilder(server.getNamespace().getNodeContext())
					.setNodeId(server.getNamespace().newNodeId("HelloWorld/Dynamic/" + name))
					.setAccessLevel(AccessLevel.READ_WRITE).setBrowseName(server.getNamespace().newQualifiedName(name))
					.setDisplayName(LocalizedText.english(name)).setDataType(typeId)
					.setTypeDefinition(Identifiers.BaseDataVariableType).build();

			node.setValue(new DataValue(variant));

			node.getFilterChain().addLast(new AttributeLoggingFilter(),
					AttributeFilters.getValue(ctx -> new DataValue(new Variant(random.nextDouble()))));

			server.getNamespace().getNodeManager().addNode(node);
			dynamicFolder.addOrganizes(node);
		}
	}

	private static void addGenerateEventMethod(final Ar4kOpcUaServer server, final UaFolderNode folderNode) {
		final UaMethodNode methodNode = UaMethodNode.builder(server.getNamespace().getNodeContext())
				.setNodeId(server.getNamespace().newNodeId("HelloWorld/generateEvent(eventTypeId)"))
				.setBrowseName(server.getNamespace().newQualifiedName("generateEvent(eventTypeId)"))
				.setDisplayName(new LocalizedText(null, "generateEvent(eventTypeId)"))
				.setDescription(
						LocalizedText.english("Generate an Event with the TypeDefinition indicated by eventTypeId."))
				.build();

		final GenerateEventMethod generateEventMethod = new GenerateEventMethod(methodNode);
		methodNode.setInputArguments(generateEventMethod.getInputArguments());
		methodNode.setOutputArguments(generateEventMethod.getOutputArguments());
		methodNode.setInvocationHandler(generateEventMethod);

		server.getNamespace().getNodeManager().addNode(methodNode);

		methodNode.addReference(new Reference(methodNode.getNodeId(), Identifiers.HasComponent,
				folderNode.getNodeId().expanded(), false));
	}

	private static void addScalarNodes(final Ar4kOpcUaServer server, final UaFolderNode rootNode) {
		final UaFolderNode scalarTypesFolder = new UaFolderNode(server.getNamespace().getNodeContext(),
				server.getNamespace().newNodeId("HelloWorld/ScalarTypes"),
				server.getNamespace().newQualifiedName("ScalarTypes"), LocalizedText.english("ScalarTypes"));

		server.getNamespace().getNodeManager().addNode(scalarTypesFolder);
		rootNode.addOrganizes(scalarTypesFolder);

		for (final Object[] os : MiloHelper.STATIC_SCALAR_NODES) {
			final String name = (String) os[0];
			final NodeId typeId = (NodeId) os[1];
			final Variant variant = (Variant) os[2];

			final UaVariableNode node = new UaVariableNode.UaVariableNodeBuilder(server.getNamespace().getNodeContext())
					.setNodeId(server.getNamespace().newNodeId("HelloWorld/ScalarTypes/" + name))
					.setAccessLevel(AccessLevel.READ_WRITE).setUserAccessLevel(AccessLevel.READ_WRITE)
					.setBrowseName(server.getNamespace().newQualifiedName(name))
					.setDisplayName(LocalizedText.english(name)).setDataType(typeId)
					.setTypeDefinition(Identifiers.BaseDataVariableType).build();

			node.setValue(new DataValue(variant));

			node.getFilterChain().addLast(new AttributeLoggingFilter(AttributeId.Value::equals));

			server.getNamespace().getNodeManager().addNode(node);
			scalarTypesFolder.addOrganizes(node);
		}
	}

	private static void addShutdownMethod(final Ar4kOpcUaServer server, final UaFolderNode folderNode) {
		final UaMethodNode methodNode = UaMethodNode.builder(server.getNamespace().getNodeContext())
				.setNodeId(server.getNamespace().newNodeId("HelloWorld/shutdown()"))
				.setBrowseName(server.getNamespace().newQualifiedName("shutdown()"))
				.setDisplayName(new LocalizedText(null, "shutdown()"))
				.setDescription(LocalizedText.english("Shutdown the opcua server.")).build();

		final ShutdownMethod shutdownMethod = new ShutdownMethod(methodNode);
		methodNode.setInputArguments(shutdownMethod.getInputArguments());
		methodNode.setOutputArguments(shutdownMethod.getOutputArguments());
		methodNode.setInvocationHandler(shutdownMethod);

		server.getNamespace().getNodeManager().addNode(methodNode);

		methodNode.addReference(new Reference(methodNode.getNodeId(), Identifiers.HasComponent,
				folderNode.getNodeId().expanded(), false));
	}

	private static void addSqrtMethod(final Ar4kOpcUaServer server, final UaFolderNode folderNode) {
		final UaMethodNode methodNode = UaMethodNode.builder(server.getNamespace().getNodeContext())
				.setNodeId(server.getNamespace().newNodeId("HelloWorld/sqrt(x)"))
				.setBrowseName(server.getNamespace().newQualifiedName("sqrt(x)"))
				.setDisplayName(new LocalizedText(null, "sqrt(x)"))
				.setDescription(
						LocalizedText.english("Returns the correctly rounded positive square root of a double value."))
				.build();

		final SqrtMethod sqrtMethod = new SqrtMethod(methodNode);
		methodNode.setInputArguments(sqrtMethod.getInputArguments());
		methodNode.setOutputArguments(sqrtMethod.getOutputArguments());
		methodNode.setInvocationHandler(sqrtMethod);

		server.getNamespace().getNodeManager().addNode(methodNode);

		methodNode.addReference(new Reference(methodNode.getNodeId(), Identifiers.HasComponent,
				folderNode.getNodeId().expanded(), false));
	}

	private static void addVariableNodes(final Ar4kOpcUaServer server, final UaFolderNode rootNode) {
		addArrayNodes(server, rootNode);
		addScalarNodes(server, rootNode);
		addAdminReadableNodes(server, rootNode);
		addAdminWritableNodes(server, rootNode);
		addDynamicNodes(server, rootNode);
		addDataAccessNodes(server, rootNode);
		addWriteOnlyNodes(server, rootNode);
	}

	private static void addWriteOnlyNodes(final Ar4kOpcUaServer server, final UaFolderNode rootNode) {
		final UaFolderNode writeOnlyFolder = new UaFolderNode(server.getNamespace().getNodeContext(),
				server.getNamespace().newNodeId("HelloWorld/WriteOnly"),
				server.getNamespace().newQualifiedName("WriteOnly"), LocalizedText.english("WriteOnly"));

		server.getNamespace().getNodeManager().addNode(writeOnlyFolder);
		rootNode.addOrganizes(writeOnlyFolder);

		final String name = "String";
		final UaVariableNode node = new UaVariableNode.UaVariableNodeBuilder(server.getNamespace().getNodeContext())
				.setNodeId(server.getNamespace().newNodeId("HelloWorld/WriteOnly/" + name))
				.setAccessLevel(AccessLevel.WRITE_ONLY).setUserAccessLevel(AccessLevel.WRITE_ONLY)
				.setBrowseName(server.getNamespace().newQualifiedName(name)).setDisplayName(LocalizedText.english(name))
				.setDataType(Identifiers.String).setTypeDefinition(Identifiers.BaseDataVariableType).build();

		node.setValue(new DataValue(new Variant("can't read this")));

		server.getNamespace().getNodeManager().addNode(node);
		writeOnlyFolder.addOrganizes(node);
	}

	private static void createAndAddNodes(final Ar4kOpcUaServer server) {
		// Create a "HelloWorld" folder and add it to the node manager
		final NodeId folderNodeId = server.getNamespace().newNodeId("HelloWorld");

		final UaFolderNode folderNode = new UaFolderNode(server.getNamespace().getNodeContext(), folderNodeId,
				server.getNamespace().newQualifiedName("HelloWorld"), LocalizedText.english("HelloWorld"));

		server.getNamespace().getNodeManager().addNode(folderNode);

		// Make sure our new folder shows up under the server's Objects folder.
		folderNode.addReference(new Reference(folderNode.getNodeId(), Identifiers.Organizes,
				Identifiers.ObjectsFolder.expanded(), false));

		// Add the rest of the nodes
		addVariableNodes(server, folderNode);

		addSqrtMethod(server, folderNode);

		addGenerateEventMethod(server, folderNode);

		addShutdownMethod(server, folderNode);

		try {
			registerCustomEnumType(server);
			addCustomEnumTypeVariable(server, folderNode);
		} catch (final Exception e) {
			logger.warn("Failed to register custom enum type", e);
		}

		try {
			registerCustomStructType(server);
			addCustomStructTypeVariable(server, folderNode);
		} catch (final Exception e) {
			logger.warn("Failed to register custom struct type", e);
		}

		try {
			registerCustomUnionType(server);
			addCustomUnionTypeVariable(server, folderNode);
		} catch (final Exception e) {
			logger.warn("Failed to register custom struct type", e);
		}

		addCustomObjectTypeAndInstance(server, folderNode);
	}

	private static Lifecycle getEventDemoLifecycle(final Ar4kOpcUaServer server) {
		return new Lifecycle() {
			@Override
			public void shutdown() {
				ExampleNamespace.keepPostingEvents = false;
				try {
					if (eventThread != null) {
						ExampleNamespace.eventThread.interrupt();
						ExampleNamespace.eventThread.join();
					}
				} catch (final InterruptedException ignored) {
					// ignored
				}
			}

			@Override
			public void startup() {
				startBogusEventNotifier(server);
			}
		};
	}

	private static void registerCustomEnumType(final Ar4kOpcUaServer server) throws Exception {
		final NodeId dataTypeId = CustomEnumType.TYPE_ID
				.toNodeIdOrThrow(server.getNamespace().getServer().getNamespaceTable());

		server.getNamespace().getDictionaryManager().registerEnumCodec(new CustomEnumType.Codec().asBinaryCodec(),
				"CustomEnumType", dataTypeId);

		final UaNode node = server.getNamespace().getNodeManager().get(dataTypeId);
		if (node instanceof UaDataTypeNode) {
			final UaDataTypeNode dataTypeNode = (UaDataTypeNode) node;

			dataTypeNode.setEnumStrings(new LocalizedText[] { LocalizedText.english("Field0"),
					LocalizedText.english("Field1"), LocalizedText.english("Field2") });
		}

		final EnumField[] fields = new EnumField[] {
				new EnumField(0L, LocalizedText.english("Field0"), LocalizedText.NULL_VALUE, "Field0"),
				new EnumField(1L, LocalizedText.english("Field1"), LocalizedText.NULL_VALUE, "Field1"),
				new EnumField(2L, LocalizedText.english("Field2"), LocalizedText.NULL_VALUE, "Field2") };

		final EnumDefinition definition = new EnumDefinition(fields);

		final EnumDescription description = new EnumDescription(dataTypeId,
				new QualifiedName(server.getNamespace().getNamespaceIndex(), "CustomEnumType"), definition,
				ubyte(BuiltinDataType.Int32.getTypeId()));

		server.getNamespace().getDictionaryManager().registerEnumDescription(description);
	}

	private static void registerCustomStructType(final Ar4kOpcUaServer server) throws Exception {
		// Get the NodeId for the DataType and encoding Nodes.

		final NodeId dataTypeId = CustomStructType.TYPE_ID
				.toNodeIdOrThrow(server.getNamespace().getServer().getNamespaceTable());

		final NodeId binaryEncodingId = CustomStructType.BINARY_ENCODING_ID
				.toNodeIdOrThrow(server.getNamespace().getServer().getNamespaceTable());

		// At a minimum, custom types must have their codec registered.
		// If clients don't need to dynamically discover types and will
		// register the codecs on their own then this is all that is
		// necessary.
		// The dictionary manager will add a corresponding DataType Node to
		// the AddressSpace.

		server.getNamespace().getDictionaryManager().registerStructureCodec(
				new CustomStructType.Codec().asBinaryCodec(), "CustomStructType", dataTypeId, binaryEncodingId);

		// If the custom type also needs to be discoverable by clients then it
		// needs an entry in a DataTypeDictionary that can be read by those
		// clients. We describe the type using StructureDefinition or
		// EnumDefinition and register it with the dictionary manager.
		// The dictionary manager will add all the necessary nodes to the
		// AddressSpace and generate the required dictionary bsd.xml file.

		final StructureField[] fields = new StructureField[] {
				new StructureField("foo", LocalizedText.NULL_VALUE, Identifiers.String, ValueRanks.Scalar, null,
						server.getNamespace().getServer().getConfig().getLimits().getMaxStringLength(), false),
				new StructureField("bar", LocalizedText.NULL_VALUE, Identifiers.UInt32, ValueRanks.Scalar, null,
						uint(0), false),
				new StructureField("baz", LocalizedText.NULL_VALUE, Identifiers.Boolean, ValueRanks.Scalar, null,
						uint(0), false),
				new StructureField("customEnumType", LocalizedText.NULL_VALUE,
						CustomEnumType.TYPE_ID.toNodeIdOrThrow(server.getNamespace().getServer().getNamespaceTable()),
						ValueRanks.Scalar, null, uint(0), false) };

		final StructureDefinition definition = new StructureDefinition(binaryEncodingId, Identifiers.Structure,
				StructureType.Structure, fields);

		final StructureDescription description = new StructureDescription(dataTypeId,
				new QualifiedName(server.getNamespace().getNamespaceIndex(), "CustomStructType"), definition);

		server.getNamespace().getDictionaryManager().registerStructureDescription(description, binaryEncodingId);
	}

	private static void registerCustomUnionType(final Ar4kOpcUaServer server) throws Exception {
		final NodeId dataTypeId = CustomUnionType.TYPE_ID
				.toNodeIdOrThrow(server.getNamespace().getServer().getNamespaceTable());

		final NodeId binaryEncodingId = CustomUnionType.BINARY_ENCODING_ID
				.toNodeIdOrThrow(server.getNamespace().getServer().getNamespaceTable());

		server.getNamespace().getDictionaryManager().registerUnionCodec(new CustomUnionType.Codec().asBinaryCodec(),
				"CustomUnionType", dataTypeId, binaryEncodingId);

		final StructureField[] fields = new StructureField[] {
				new StructureField("foo", LocalizedText.NULL_VALUE, Identifiers.UInt32, ValueRanks.Scalar, null,
						server.getNamespace().getServer().getConfig().getLimits().getMaxStringLength(), false),
				new StructureField("bar", LocalizedText.NULL_VALUE, Identifiers.String, ValueRanks.Scalar, null,
						uint(0), false) };

		final StructureDefinition definition = new StructureDefinition(binaryEncodingId, Identifiers.Structure,
				StructureType.Union, fields);

		final StructureDescription description = new StructureDescription(dataTypeId,
				new QualifiedName(server.getNamespace().getNamespaceIndex(), "CustomUnionType"), definition);

		server.getNamespace().getDictionaryManager().registerStructureDescription(description, binaryEncodingId);
	}

	private static void startBogusEventNotifier(final Ar4kOpcUaServer server) {
		// Set the EventNotifier bit on Server Node for Events.
		final UaNode serverNode = server.getNamespace().getServer().getAddressSpaceManager()
				.getManagedNode(Identifiers.Server).orElse(null);

		if (serverNode instanceof ServerTypeNode) {
			((ServerTypeNode) serverNode).setEventNotifier(ubyte(1));

			// Post a bogus Event every couple seconds
			eventThread = new Thread(() -> {
				while (keepPostingEvents) {
					logger.info("send event");
					try {
						final BaseEventTypeNode eventNode = server.getNamespace().getServer().getEventFactory()
								.createEvent(server.getNamespace().newNodeId(UUID.randomUUID()),
										Identifiers.BaseEventType);

						eventNode.setBrowseName(new QualifiedName(1, "foo"));
						eventNode.setDisplayName(LocalizedText.english("foo"));
						eventNode.setEventId(ByteString.of(new byte[] { 0, 1, 2, 3 }));
						eventNode.setEventType(Identifiers.BaseEventType);
						eventNode.setSourceNode(serverNode.getNodeId());
						eventNode.setSourceName(serverNode.getDisplayName().getText());
						eventNode.setTime(DateTime.now());
						eventNode.setReceiveTime(DateTime.NULL_VALUE);
						eventNode.setMessage(LocalizedText.english("event message!"));
						eventNode.setSeverity(ushort(2));

						// noinspection UnstableApiUsage
						server.getNamespace().getServer().getEventBus().post(eventNode);

						eventNode.delete();
					} catch (final Throwable e) {
						logger.error("Error creating EventNode: {}", e.getMessage(), e);
					}

					try {
						// noinspection BusyWait
						Thread.sleep(4_000);
					} catch (final InterruptedException ignored) {
						// ignored
					}
				}
			}, "bogus-event-poster");

			eventThread.start();
		}
	}

}
