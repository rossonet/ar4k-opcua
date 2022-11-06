package org.rossonet.opcua.milo.server.storage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.eclipse.milo.opcua.sdk.core.Reference;
import org.eclipse.milo.opcua.sdk.server.nodes.UaNode;
import org.eclipse.milo.opcua.stack.core.NamespaceTable;
import org.eclipse.milo.opcua.stack.core.types.builtin.ExpandedNodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.LinkedHashMultiset;

public class OnMemoryStorageController implements StorageController {

	private static final Logger logger = LoggerFactory.getLogger(OnMemoryStorageController.class);
	private final ConcurrentHashMap<NodeId, UaNode> nodeMap = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<NodeId, LinkedHashMultiset<Reference>> referenceMap = new ConcurrentHashMap<>();

	@Override
	public Optional<UaNode> addNode(final UaNode node) {
		return Optional.ofNullable(nodeMap.put(node.getNodeId(), node));
	}

	@Override
	public synchronized void addReference(final Reference reference) {
		final LinkedHashMultiset<Reference> references = referenceMap.computeIfAbsent(reference.getSourceNodeId(),
				nodeId -> LinkedHashMultiset.create());

		references.add(reference);
	}

	@Override
	public synchronized void addReferences(final Reference reference, final NamespaceTable namespaceTable) {
		addReference(reference);

		reference.invert(namespaceTable).ifPresent(this::addReference);
	}

	@Override
	public boolean containsNode(final ExpandedNodeId nodeId, final NamespaceTable namespaceTable) {
		return nodeId.toNodeId(namespaceTable).map(this::containsNode).orElse(false);
	}

	@Override
	public boolean containsNode(final NodeId nodeId) {
		return nodeMap.containsKey(nodeId);
	}

	@Override
	public Optional<UaNode> getNode(final ExpandedNodeId nodeId, final NamespaceTable namespaceTable) {
		logger.info("getNode " + nodeId);
		return nodeId.toNodeId(namespaceTable).flatMap(this::getNode);
	}

	@Override
	public Optional<UaNode> getNode(final NodeId nodeId) {
		logger.info("getNode " + nodeId);
		return Optional.ofNullable(nodeMap.get(nodeId));
	}

	@Override
	public List<NodeId> getNodeIds() {
		return new ArrayList<>(nodeMap.keySet());
	}

	@Override
	public ConcurrentMap<NodeId, UaNode> getNodeMap() {
		return nodeMap;
	}

	@Override
	public List<UaNode> getNodes() {
		return new ArrayList<>(nodeMap.values());
	}

	@Override
	public ConcurrentMap<NodeId, LinkedHashMultiset<Reference>> getReferenceMap() {
		return referenceMap;
	}

	@Override
	public synchronized List<Reference> getReferences(final NodeId nodeId) {
		final LinkedHashMultiset<Reference> references = referenceMap.get(nodeId);

		if (references != null) {
			return new ArrayList<>(references);
		} else {
			return Collections.emptyList();
		}
	}

	@Override
	public List<Reference> getReferences(final NodeId nodeId, final Predicate<Reference> filter) {
		return getReferences(nodeId).stream().filter(filter).collect(Collectors.toList());
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public Optional<UaNode> removeNode(final ExpandedNodeId nodeId, final NamespaceTable namespaceTable) {
		return nodeId.toNodeId(namespaceTable).flatMap(this::removeNode);
	}

	@Override
	public Optional<UaNode> removeNode(final NodeId nodeId) {
		return Optional.ofNullable(nodeMap.remove(nodeId));
	}

	@Override
	public synchronized void removeReference(final Reference reference) {
		final LinkedHashMultiset<Reference> references = referenceMap.get(reference.getSourceNodeId());

		if (references != null) {
			references.remove(reference);

			if (references.isEmpty()) {
				referenceMap.remove(reference.getSourceNodeId());
			}
		}
	}

	@Override
	public synchronized void removeReferences(final Reference reference, final NamespaceTable namespaceTable) {
		removeReference(reference);
		reference.invert(namespaceTable).ifPresent(this::removeReference);
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub

	}

	@Override
	public void startup() {
		// TODO Auto-generated method stub

	}

}
