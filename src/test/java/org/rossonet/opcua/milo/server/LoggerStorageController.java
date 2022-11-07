package org.rossonet.opcua.milo.server;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;

import org.eclipse.milo.opcua.sdk.core.Reference;
import org.eclipse.milo.opcua.sdk.server.nodes.UaNode;
import org.eclipse.milo.opcua.stack.core.NamespaceTable;
import org.eclipse.milo.opcua.stack.core.types.builtin.ExpandedNodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.rossonet.opcua.milo.server.storage.OnMemoryStorageController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.LinkedHashMultiset;

public class LoggerStorageController extends OnMemoryStorageController {

	private static final Logger logger = LoggerFactory.getLogger(LoggerStorageController.class);

	@Override
	public Optional<UaNode> addNode(final UaNode node) {
		log(node.toString());
		return super.addNode(node);
	}

	@Override
	public void addReference(final Reference reference) {
		log(reference.toString());
		super.addReference(reference);
	}

	@Override
	public void addReferences(final Reference reference, final NamespaceTable namespaceTable) {
		log(reference.toString());
		super.addReferences(reference, namespaceTable);
	}

	@Override
	public boolean containsNode(final ExpandedNodeId nodeId, final NamespaceTable namespaceTable) {
		log(nodeId.toString());
		return super.containsNode(nodeId, namespaceTable);
	}

	@Override
	public boolean containsNode(final NodeId nodeId) {
		log(nodeId.toString());
		return super.containsNode(nodeId);
	}

	@Override
	public void createClass() {
		log(null);
		super.createClass();
	}

	@Override
	public Optional<UaNode> getNode(final ExpandedNodeId nodeId, final NamespaceTable namespaceTable) {
		log(nodeId.toString());
		return super.getNode(nodeId, namespaceTable);
	}

	@Override
	public Optional<UaNode> getNode(final NodeId nodeId) {
		log(nodeId.toString());
		return super.getNode(nodeId);
	}

	@Override
	public List<NodeId> getNodeIds() {
		log(null);
		return super.getNodeIds();
	}

	@Override
	public ConcurrentMap<NodeId, UaNode> getNodeMap() {
		log(null);
		return super.getNodeMap();
	}

	@Override
	public List<UaNode> getNodes() {
		log(null);
		return super.getNodes();
	}

	@Override
	public ConcurrentMap<NodeId, LinkedHashMultiset<Reference>> getReferenceMap() {
		log(null);
		return super.getReferenceMap();
	}

	@Override
	public List<Reference> getReferences(final NodeId nodeId) {
		log(nodeId.toString());
		return super.getReferences(nodeId);
	}

	@Override
	public List<Reference> getReferences(final NodeId nodeId, final Predicate<Reference> filter) {
		log(nodeId.toString());
		return super.getReferences(nodeId, filter);
	}

	public void log(final String parameter) {
		logger.trace(" ** " + Thread.currentThread().getStackTrace()[2].getMethodName() + " -> " + parameter);
	}

	@Override
	public Optional<UaNode> removeNode(final ExpandedNodeId nodeId, final NamespaceTable namespaceTable) {
		log(nodeId.toString());
		return super.removeNode(nodeId, namespaceTable);
	}

	@Override
	public Optional<UaNode> removeNode(final NodeId nodeId) {
		log(nodeId.toString());
		return super.removeNode(nodeId);
	}

	@Override
	public void removeReference(final Reference reference) {
		log(reference.toString());
		super.removeReference(reference);
	}

	@Override
	public void removeReferences(final Reference reference, final NamespaceTable namespaceTable) {
		log(reference.toString());
		super.removeReferences(reference, namespaceTable);
	}

	@Override
	public void shutdown() {
		log(null);
		super.shutdown();
	}

	@Override
	public void startup() {
		log(null);
		super.startup();
	}

}
