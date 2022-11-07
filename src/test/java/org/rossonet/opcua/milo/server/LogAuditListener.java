package org.rossonet.opcua.milo.server;

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
import org.rossonet.opcua.milo.server.listener.AuditListener;
import org.rossonet.opcua.milo.server.listener.ShutdownReason;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogAuditListener implements AuditListener {
	private static final Logger logger = LoggerFactory.getLogger(LogAuditListener.class);

	@Override
	public void addNode(final AddNodesContext context, final AddNodesItem addNodeItem) {
		log(addNodeItem.toString());
		AuditListener.super.addNode(context, addNodeItem);
	}

	@Override
	public void addReference(final AddReferencesContext context, final AddReferencesItem reference) {
		log(reference.toString());
		AuditListener.super.addReference(context, reference);
	}

	@Override
	public void attributeChanged(final UaNode node, final AttributeId attributeId, final Object value) {
		log(node.toString() + "\nvalue:" + value);

	}

	@Override
	public void call(final CallContext context, final List<CallMethodRequest> requests) {
		log(requests.toString());
		AuditListener.super.call(context, requests);
	}

	@Override
	public void deleteNodes(final DeleteNodesContext context, final List<DeleteNodesItem> nodesToDelete) {
		log(nodesToDelete.toString());
		AuditListener.super.deleteNodes(context, nodesToDelete);
	}

	@Override
	public void deleteReferences(final DeleteReferencesContext context,
			final List<DeleteReferencesItem> referencesToDelete) {
		log(referencesToDelete.toString());
		AuditListener.super.deleteReferences(context, referencesToDelete);
	}

	@Override
	public void historyUpdate(final HistoryUpdateContext context, final List<HistoryUpdateDetails> updateDetails) {
		log(updateDetails.toString());
		AuditListener.super.historyUpdate(context, updateDetails);
	}

	public void log(final String parameter) {
		logger.info(" ## " + Thread.currentThread().getStackTrace()[2].getMethodName() + " -> " + parameter);
	}

	@Override
	public void notifyShutdown(final ShutdownReason reason) {
		log(reason.toString());
		AuditListener.super.notifyShutdown(reason);
	}

	@Override
	public void onDataItemsCreated(final List<DataItem> dataItems) {
		log(dataItems.toString());
		AuditListener.super.onDataItemsCreated(dataItems);
	}

	@Override
	public void onDataItemsDeleted(final List<DataItem> dataItems) {
		log(dataItems.toString());
		AuditListener.super.onDataItemsDeleted(dataItems);
	}

	@Override
	public void onDataItemsModified(final List<DataItem> dataItems) {
		log(dataItems.toString());
		AuditListener.super.onDataItemsModified(dataItems);
	}

	@Override
	public void onMonitoringModeChanged(final List<MonitoredItem> monitoredItems) {
		log(monitoredItems.toString());
		AuditListener.super.onMonitoringModeChanged(monitoredItems);
	}

	@Override
	public void read(final ReadContext context, final Double maxAge, final TimestampsToReturn timestamps,
			final List<ReadValueId> readValueIds) {
		log(readValueIds.toString());
		AuditListener.super.read(context, maxAge, timestamps, readValueIds);
	}

	@Override
	public void write(final WriteContext context, final List<WriteValue> writeValues) {
		log(writeValues.toString());
		AuditListener.super.write(context, writeValues);
	}
}