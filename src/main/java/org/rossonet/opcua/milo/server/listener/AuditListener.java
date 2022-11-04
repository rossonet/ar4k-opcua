package org.rossonet.opcua.milo.server.listener;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.eclipse.milo.opcua.sdk.server.api.DataItem;
import org.eclipse.milo.opcua.sdk.server.api.EventItem;
import org.eclipse.milo.opcua.sdk.server.api.MonitoredItem;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.structured.ReadValueId;

public interface AuditListener {

	public default void notifyShutdown(final ShutdownReason reason) {
	};

	public default void onCreateDataItem(final ReadValueId itemToMonitor, final Double requestedSamplingInterval,
			final UInteger requestedQueueSize, final BiConsumer<Double, UInteger> revisionCallback) {
	};

	public default void onCreateEventItem(final ReadValueId itemToMonitor, final UInteger requestedQueueSize,
			final Consumer<UInteger> revisionCallback) {
	};

	public default void onDataItemsCreated(final List<DataItem> dataItems) {
	};

	public default void onDataItemsDeleted(final List<DataItem> dataItems) {
	};

	public default void onDataItemsModified(final List<DataItem> dataItems) {
	};

	public default void onEventItemsCreated(final List<EventItem> eventItems) {
	};

	public default void onEventItemsDeleted(final List<EventItem> eventItems) {
	};

	public default void onEventItemsModified(final List<EventItem> eventItems) {
	};

	public default void onModifyDataItem(final ReadValueId itemToModify, final Double requestedSamplingInterval,
			final UInteger requestedQueueSize, final BiConsumer<Double, UInteger> revisionCallback) {
	};

	public default void onModifyEventItem(final ReadValueId itemToModify, final UInteger requestedQueueSize,
			final Consumer<UInteger> revisionCallback) {
	};

	public default void onMonitoringModeChanged(final List<MonitoredItem> monitoredItems) {
	};

}
