package org.rossonet.opcua.milo.server.storage;

import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.milo.opcua.sdk.core.Reference;
import org.eclipse.milo.opcua.sdk.server.nodes.UaNode;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;

import com.google.common.collect.LinkedHashMultiset;

public class OnMemoryStorageController implements StorageController {

	private final ConcurrentHashMap<NodeId, UaNode> nodeMap = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<NodeId, LinkedHashMultiset<Reference>> referenceMap = new ConcurrentHashMap<>();

	@Override
	public ConcurrentHashMap<NodeId, UaNode> getNodeMap() {
		return nodeMap;
	}

	@Override
	public ConcurrentHashMap<NodeId, LinkedHashMultiset<Reference>> getReferenceMap() {
		return referenceMap;
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
