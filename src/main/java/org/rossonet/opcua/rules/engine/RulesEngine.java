package org.rossonet.opcua.rules.engine;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.eclipse.milo.opcua.sdk.server.Lifecycle;
import org.eclipse.milo.opcua.sdk.server.api.DataItem;
import org.eclipse.milo.opcua.sdk.server.api.EventItem;
import org.eclipse.milo.opcua.sdk.server.api.MonitoredItem;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.structured.ReadValueId;
import org.rossonet.opcua.milo.server.namespace.ManagedNamespace;

public class RulesEngine implements Lifecycle {

	private final ManagedNamespace managedNamespace;

	public RulesEngine(final ManagedNamespace managedNamespace) {
		this.managedNamespace = managedNamespace;
	}

	public void onCreateDataItem(final ReadValueId itemToMonitor, final Double requestedSamplingInterval,
			final UInteger requestedQueueSize, final BiConsumer<Double, UInteger> revisionCallback) {
		// TODO Auto-generated method stub

	}

	public void onCreateEventItem(final ReadValueId itemToMonitor, final UInteger requestedQueueSize,
			final Consumer<UInteger> revisionCallback) {
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

	public void onEventItemsCreated(final List<EventItem> eventItems) {
		// TODO Auto-generated method stub

	}

	public void onEventItemsDeleted(final List<EventItem> eventItems) {
		// TODO Auto-generated method stub

	}

	public void onEventItemsModified(final List<EventItem> eventItems) {
		// TODO Auto-generated method stub

	}

	public void onModifyDataItem(final ReadValueId itemToModify, final Double requestedSamplingInterval,
			final UInteger requestedQueueSize, final BiConsumer<Double, UInteger> revisionCallback) {
		// TODO Auto-generated method stub

	}

	public void onModifyEventItem(final ReadValueId itemToModify, final UInteger requestedQueueSize,
			final Consumer<UInteger> revisionCallback) {
		// TODO Auto-generated method stub

	}

	public void onMonitoringModeChanged(final List<MonitoredItem> monitoredItems) {
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

}
