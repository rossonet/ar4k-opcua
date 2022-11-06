package org.rossonet.opcua.milo.server.storage;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;

import org.eclipse.milo.opcua.sdk.core.Reference;
import org.eclipse.milo.opcua.sdk.server.UaNodeManager;
import org.eclipse.milo.opcua.sdk.server.nodes.UaNode;
import org.eclipse.milo.opcua.stack.core.NamespaceTable;
import org.eclipse.milo.opcua.stack.core.types.builtin.ExpandedNodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.LinkedHashMultiset;

public class StorageNodeManager extends UaNodeManager {

	public static final Logger logger = LoggerFactory.getLogger(StorageNodeManager.class);

	private final StorageController storageController;

	public StorageNodeManager(final StorageController storageController) {
		this.storageController = storageController;
		storageController.init();
	}

	@Override
	public Optional<UaNode> addNode(final UaNode node) {
		return storageController.addNode(node);
	}

	@Override
	public void addReference(final Reference reference) {
		storageController.addReference(reference);
	}

	@Override
	public void addReferences(final Reference reference, final NamespaceTable namespaceTable) {
		storageController.addReferences(reference, namespaceTable);
	}

	@Override
	public boolean containsNode(final ExpandedNodeId nodeId, final NamespaceTable namespaceTable) {
		return storageController.containsNode(nodeId, namespaceTable);
	}

	@Override
	public boolean containsNode(final NodeId nodeId) {
		return storageController.containsNode(nodeId);
	}

	@Override
	public Optional<UaNode> getNode(final ExpandedNodeId nodeId, final NamespaceTable namespaceTable) {
		return storageController.getNode(nodeId, namespaceTable);
	}

	@Override
	public Optional<UaNode> getNode(final NodeId nodeId) {
		return storageController.getNode(nodeId);
	}

	@Override
	public List<NodeId> getNodeIds() {
		return storageController.getNodeIds();
	}

	@Override
	public List<UaNode> getNodes() {
		return storageController.getNodes();
	}

	@Override
	public ConcurrentMap<NodeId, LinkedHashMultiset<Reference>> getReferenceMap() {
		return storageController.getReferenceMap();
	}

	@Override
	public List<Reference> getReferences(final NodeId nodeId) {
		return storageController.getReferences(nodeId);
	}

	@Override
	public List<Reference> getReferences(final NodeId nodeId, final Predicate<Reference> filter) {
		return storageController.getReferences(nodeId, filter);
	}

	@Override
	public Optional<UaNode> removeNode(final ExpandedNodeId nodeId, final NamespaceTable namespaceTable) {
		return storageController.removeNode(nodeId, namespaceTable);
	}

	@Override
	public Optional<UaNode> removeNode(final NodeId nodeId) {
		return storageController.removeNode(nodeId);
	}

	@Override
	public void removeReference(final Reference reference) {
		storageController.removeReference(reference);
	}

	@Override
	public void removeReferences(final Reference reference, final NamespaceTable namespaceTable) {
		storageController.removeReferences(reference, namespaceTable);
	}

	@Override
	protected ConcurrentMap<NodeId, UaNode> getNodeMap() {
		return storageController.getNodeMap();
	}

}
