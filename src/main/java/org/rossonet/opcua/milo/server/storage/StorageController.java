package org.rossonet.opcua.milo.server.storage;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;

import org.eclipse.milo.opcua.sdk.core.Reference;
import org.eclipse.milo.opcua.sdk.server.Lifecycle;
import org.eclipse.milo.opcua.sdk.server.nodes.UaNode;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.NamespaceTable;
import org.eclipse.milo.opcua.stack.core.types.builtin.ExpandedNodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;

import com.google.common.collect.LinkedHashMultiset;

public interface StorageController extends Lifecycle {

	Optional<UaNode> addNode(UaNode node);

	void addReference(Reference reference);

	void addReferences(Reference reference, NamespaceTable namespaceTable);

	void attributeChanged(UaNode node, AttributeId attributeId, Object value);

	boolean containsNode(ExpandedNodeId nodeId, NamespaceTable namespaceTable);

	boolean containsNode(NodeId nodeId);

	Optional<UaNode> getNode(ExpandedNodeId nodeId, NamespaceTable namespaceTable);

	Optional<UaNode> getNode(NodeId nodeId);

	List<NodeId> getNodeIds();

	ConcurrentMap<NodeId, UaNode> getNodeMap();

	List<UaNode> getNodes();

	ConcurrentMap<NodeId, LinkedHashMultiset<Reference>> getReferenceMap();

	List<Reference> getReferences(NodeId nodeId);

	List<Reference> getReferences(NodeId nodeId, Predicate<Reference> filter);

	void init();

	Optional<UaNode> removeNode(ExpandedNodeId nodeId, NamespaceTable namespaceTable);

	Optional<UaNode> removeNode(NodeId nodeId);

	void removeReference(Reference reference);

	void removeReferences(Reference reference, NamespaceTable namespaceTable);

}