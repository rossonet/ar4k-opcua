package org.rossonet.opcua.rules.engine;

import java.util.List;

import org.eclipse.milo.opcua.sdk.server.Lifecycle;
import org.eclipse.milo.opcua.sdk.server.api.DataItem;
import org.eclipse.milo.opcua.sdk.server.api.MonitoredItem;
import org.eclipse.milo.opcua.sdk.server.api.services.AttributeHistoryServices.HistoryUpdateContext;
import org.eclipse.milo.opcua.sdk.server.api.services.AttributeServices.ReadContext;
import org.eclipse.milo.opcua.sdk.server.api.services.AttributeServices.WriteContext;
import org.eclipse.milo.opcua.sdk.server.api.services.MethodServices.CallContext;
import org.eclipse.milo.opcua.sdk.server.api.services.NodeManagementServices.AddNodesContext;
import org.eclipse.milo.opcua.sdk.server.api.services.NodeManagementServices.AddReferencesContext;
import org.eclipse.milo.opcua.sdk.server.api.services.NodeManagementServices.DeleteNodesContext;
import org.eclipse.milo.opcua.sdk.server.api.services.NodeManagementServices.DeleteReferencesContext;
import org.eclipse.milo.opcua.sdk.server.nodes.UaNode;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.AddNodesItem;
import org.eclipse.milo.opcua.stack.core.types.structured.AddReferencesItem;
import org.eclipse.milo.opcua.stack.core.types.structured.CallMethodRequest;
import org.eclipse.milo.opcua.stack.core.types.structured.DeleteNodesItem;
import org.eclipse.milo.opcua.stack.core.types.structured.DeleteReferencesItem;
import org.eclipse.milo.opcua.stack.core.types.structured.HistoryUpdateDetails;
import org.eclipse.milo.opcua.stack.core.types.structured.ReadValueId;
import org.eclipse.milo.opcua.stack.core.types.structured.WriteValue;
import org.rossonet.opcua.milo.server.namespace.ManagedNamespace;

public class RulesEngine implements Lifecycle {

	@SuppressWarnings("unused")
	private final ManagedNamespace managedNamespace;

	public RulesEngine(final ManagedNamespace managedNamespace) {
		this.managedNamespace = managedNamespace;
	}

	public void addNode(final AddNodesContext context, final AddNodesItem addNodeItem) {
		// TODO Auto-generated method stub

	}

	public void addReference(final AddReferencesContext context, final AddReferencesItem reference) {
		// TODO Auto-generated method stub

	}

	public void attributeChanged(final UaNode node, final AttributeId attributeId, final Object value) {
		// TODO Auto-generated method stub

	}

	public void call(final CallContext context, final List<CallMethodRequest> requests) {
		// TODO Auto-generated method stub

	}

	public void deleteNodes(final DeleteNodesContext context, final List<DeleteNodesItem> nodesToDelete) {
		// TODO Auto-generated method stub

	}

	public void deleteReferences(final DeleteReferencesContext context,
			final List<DeleteReferencesItem> referencesToDelete) {
		// TODO Auto-generated method stub

	}

	public void historyUpdate(final HistoryUpdateContext context, final List<HistoryUpdateDetails> updateDetails) {
		// TODO Auto-generated method stub

	}

	public void onDataItemsCreated(final List<DataItem> dataItems) {
		// TODO Auto-generated method stub

	}

	public void onDataItemsDeleted(final List<DataItem> dataItems) {
		// TODO Auto-generated method stub

	}

	public void onDataItemsModified(final List<DataItem> dataItems) {
		// TODO Auto-generated method stub

	}

	public void onMonitoringModeChanged(final List<MonitoredItem> monitoredItems) {
		// TODO Auto-generated method stub

	}

	public void read(final ReadContext context, final Double maxAge, final TimestampsToReturn timestamps,
			final List<ReadValueId> readValueIds) {
		// TODO Auto-generated method stub

	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub

	}

	@Override
	public void startup() {
		// TODO Auto-generated method stub

	}

	public void write(final WriteContext context, final List<WriteValue> writeValues) {
		// TODO Auto-generated method stub

	}

}
