package org.rossonet.opcua.milo.server.storage;

import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.milo.opcua.sdk.core.Reference;
import org.eclipse.milo.opcua.sdk.server.Lifecycle;
import org.eclipse.milo.opcua.sdk.server.nodes.UaNode;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;

import com.google.common.collect.LinkedHashMultiset;

public interface StorageController extends Lifecycle {

	ConcurrentHashMap<NodeId, UaNode> getNodeMap();

	ConcurrentHashMap<NodeId, LinkedHashMultiset<Reference>> getReferenceMap();

}