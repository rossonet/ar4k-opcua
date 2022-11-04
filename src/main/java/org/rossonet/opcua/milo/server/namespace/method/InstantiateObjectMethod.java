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
import org.eclipse.milo.opcua.sdk.server.api.services.NodeManagementServices.AddNodesContext;
import org.eclipse.milo.opcua.sdk.server.nodes.UaMethodNode;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.ExpandedNodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.ExtensionObject;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.enumerated.NodeClass;
import org.eclipse.milo.opcua.stack.core.types.structured.AddNodesItem;
import org.eclipse.milo.opcua.stack.core.types.structured.Argument;
import org.rossonet.opcua.milo.server.namespace.ManagedNamespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InstantiateObjectMethod extends AbstractMethodInvocationHandler {

	public static final Argument BROWSE_NAME_DESCRIPTION = new Argument("browseName", Identifiers.QualifiedName,
			ValueRanks.Scalar, null, new LocalizedText("browseName"));

	public static final Argument NODE_CLASS_DESCRIPTION = new Argument("nodeClass", Identifiers.NodeClass,
			ValueRanks.Scalar, null, new LocalizedText(
					"node class as int [ Unspecified(0), Object(1), Variable(2), Method(4), ObjectType(8), VariableType(16) ReferenceType(32) DataType(64), View(128);]"));

	public static final Argument PARENT_NODE_ID_DESCRIPTION = new Argument("parentNodeId", Identifiers.ExpandedNodeId,
			ValueRanks.Scalar, null, new LocalizedText("Parent Node ID"));

	public static final Argument REFERENCE_TYPE_ID_DESCRIPTION = new Argument("referenceTypeId", Identifiers.NodeId,
			ValueRanks.Scalar, null, new LocalizedText("Reference Type ID"));

	public static final Argument REQUESTED_NEW_NODE_ID_DESCRIPTION = new Argument("requestedNewNodeId",
			Identifiers.ExpandedNodeId, ValueRanks.Scalar, null, new LocalizedText("Requested new Node ID"));

	public static final Argument TYPE_DEFINITION_DESCRIPTION = new Argument("typeDefinition",
			Identifiers.ExpandedNodeId, ValueRanks.Scalar, null, new LocalizedText("Type Definition"));

	private static final Logger logger = LoggerFactory.getLogger(InstantiateObjectMethod.class);

	private final ManagedNamespace managedNamespace;

	public InstantiateObjectMethod(final ManagedNamespace managedNamespace, final UaMethodNode methodNode) {
		super(methodNode);
		this.managedNamespace = managedNamespace;
	}

	@Override
	public Argument[] getInputArguments() {
		return new Argument[] { BROWSE_NAME_DESCRIPTION, NODE_CLASS_DESCRIPTION, PARENT_NODE_ID_DESCRIPTION,
				REFERENCE_TYPE_ID_DESCRIPTION, REQUESTED_NEW_NODE_ID_DESCRIPTION, TYPE_DEFINITION_DESCRIPTION };
	}

	@Override
	public Argument[] getOutputArguments() {
		return new Argument[0];
	}

	@Override
	protected Variant[] invoke(final InvocationContext invocationContext, final Variant[] inputValues)
			throws UaException {
		try {
			final ExpandedNodeId parentNodeId = (ExpandedNodeId) inputValues[0].getValue();
			final NodeId referenceTypeId = (NodeId) inputValues[1].getValue();
			final ExpandedNodeId requestedNewNodeId = (ExpandedNodeId) inputValues[2].getValue();
			final QualifiedName browseName = (QualifiedName) inputValues[3].getValue();
			final NodeClass nodeClass = (NodeClass) inputValues[4].getValue();
			final ExtensionObject nodeAttributes = null;// (ExtensionObject) inputValues[5].getValue();
			final ExpandedNodeId typeDefinition = (ExpandedNodeId) inputValues[5].getValue();
			final AddNodesItem nodeToAdd = new AddNodesItem(parentNodeId, referenceTypeId, requestedNewNodeId,
					browseName, nodeClass, nodeAttributes, typeDefinition);
			final AddNodesContext addNodeContext = new AddNodesContext(invocationContext.getServer(), null);
			managedNamespace.addNode(addNodeContext, nodeToAdd);
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
			throw new UaException(StatusCode.BAD.getValue(), e.getMessage(), e);
		}
		return new Variant[0];
	}

}
