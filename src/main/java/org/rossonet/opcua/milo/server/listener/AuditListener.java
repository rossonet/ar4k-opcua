package org.rossonet.opcua.milo.server.listener;

import java.util.List;

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

public interface AuditListener {

	public default void addNode(final AddNodesContext context, final AddNodesItem addNodeItem) {
	}

	public default void addReference(final AddReferencesContext context, final AddReferencesItem reference) {
	}

	public void attributeChanged(UaNode node, AttributeId attributeId, Object value);

	public default void call(final CallContext context, final List<CallMethodRequest> requests) {
	}

	public default void deleteNodes(final DeleteNodesContext context, final List<DeleteNodesItem> nodesToDelete) {
	}

	public default void deleteReferences(final DeleteReferencesContext context,
			final List<DeleteReferencesItem> referencesToDelete) {
	}

	public default void historyUpdate(final HistoryUpdateContext context,
			final List<HistoryUpdateDetails> updateDetails) {
	}

	public default void notifyShutdown(final ShutdownReason reason) {
	}

	public default void onDataItemsCreated(final List<DataItem> dataItems) {
	}

	public default void onDataItemsDeleted(final List<DataItem> dataItems) {
	}

	public default void onDataItemsModified(final List<DataItem> dataItems) {
	}

	public default void onMonitoringModeChanged(final List<MonitoredItem> monitoredItems) {
	}

	public default void read(final ReadContext context, final Double maxAge, final TimestampsToReturn timestamps,
			final List<ReadValueId> readValueIds) {
	}

	public default void write(final WriteContext context, final List<WriteValue> writeValues) {
	}

}
