package org.rossonet.opcua.milo.server.namespace;

import java.util.List;
import java.util.UUID;

import org.eclipse.milo.opcua.sdk.core.Reference;
import org.eclipse.milo.opcua.sdk.server.Lifecycle;
import org.eclipse.milo.opcua.sdk.server.LifecycleManager;
import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.eclipse.milo.opcua.sdk.server.UaNodeManager;
import org.eclipse.milo.opcua.sdk.server.api.AddressSpaceFilter;
import org.eclipse.milo.opcua.sdk.server.api.AddressSpaceFragment;
import org.eclipse.milo.opcua.sdk.server.api.DataItem;
import org.eclipse.milo.opcua.sdk.server.api.ManagedAddressSpaceFragment;
import org.eclipse.milo.opcua.sdk.server.api.MonitoredItem;
import org.eclipse.milo.opcua.sdk.server.api.Namespace;
import org.eclipse.milo.opcua.sdk.server.api.SimpleAddressSpaceFilter;
import org.eclipse.milo.opcua.sdk.server.dtd.DataTypeDictionaryManager;
import org.eclipse.milo.opcua.sdk.server.nodes.AttributeObserver;
import org.eclipse.milo.opcua.sdk.server.nodes.UaFolderNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaMethodNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaNodeContext;
import org.eclipse.milo.opcua.sdk.server.nodes.factories.NodeFactory;
import org.eclipse.milo.opcua.sdk.server.util.SubscriptionModel;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UShort;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.AddNodesItem;
import org.eclipse.milo.opcua.stack.core.types.structured.AddReferencesItem;
import org.eclipse.milo.opcua.stack.core.types.structured.CallMethodRequest;
import org.eclipse.milo.opcua.stack.core.types.structured.DeleteNodesItem;
import org.eclipse.milo.opcua.stack.core.types.structured.DeleteReferencesItem;
import org.eclipse.milo.opcua.stack.core.types.structured.HistoryReadDetails;
import org.eclipse.milo.opcua.stack.core.types.structured.HistoryReadValueId;
import org.eclipse.milo.opcua.stack.core.types.structured.HistoryUpdateDetails;
import org.eclipse.milo.opcua.stack.core.types.structured.ReadValueId;
import org.eclipse.milo.opcua.stack.core.types.structured.ViewDescription;
import org.eclipse.milo.opcua.stack.core.types.structured.WriteValue;
import org.rossonet.opcua.milo.server.Ar4kOpcUaServer;
import org.rossonet.opcua.milo.server.listener.AuditListener;
import org.rossonet.opcua.milo.server.namespace.method.GenerateTypeObjectMethod;
import org.rossonet.opcua.milo.server.namespace.method.InstantiateObjectMethod;
import org.rossonet.opcua.milo.server.namespace.method.ShutdownMethod;
import org.rossonet.opcua.milo.server.storage.StorageController;
import org.rossonet.opcua.milo.server.storage.StorageNodeManager;
import org.rossonet.opcua.milo.utils.MiloHelper;
import org.rossonet.opcua.rules.engine.RulesEngine;
import org.rossonet.utils.LogHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManagedNamespace extends ManagedAddressSpaceFragment implements Namespace, AttributeObserver {

	private static final Logger logger = LoggerFactory.getLogger(ManagedNamespace.class);

	private final DataTypeDictionaryManager dictionaryManager;

	private final AddressSpaceFilter filter;

	private final LifecycleManager lifecycleManager = new LifecycleManager();

	private final UShort namespaceIndex;

	private final String namespaceUri;
	private final RulesEngine rulesEngine;
	private final StorageController storageController;
	private final SubscriptionModel subscriptionModel;
	private final Ar4kOpcUaServer wrapperOpcUaServer;

	public ManagedNamespace(final Ar4kOpcUaServer opcUaServer, final StorageController storageController) {
		super(opcUaServer.getServer(), new StorageNodeManager(opcUaServer, storageController));
		this.storageController = storageController;
		this.namespaceUri = opcUaServer.getOpcUaServerConfiguration().getNameSpaceUri();
		this.namespaceIndex = opcUaServer.getServer().getNamespaceTable().addUri(namespaceUri);
		filter = SimpleAddressSpaceFilter.create(nodeId -> nodeId.getNamespaceIndex().equals(getNamespaceIndex()));
		getLifecycleManager().addLifecycle(this.storageController);
		getLifecycleManager().addLifecycle(new Lifecycle() {
			@Override
			public void shutdown() {
				unregisterAddressSpace(ManagedNamespace.this);
				unregisterNodeManager(getNodeManager());
			}

			@Override
			public void startup() {
				registerAddressSpace(ManagedNamespace.this);
				registerNodeManager(getNodeManager());
			}
		});
		this.wrapperOpcUaServer = opcUaServer;
		subscriptionModel = new SubscriptionModel(this.wrapperOpcUaServer.getServer(), this);
		dictionaryManager = new DataTypeDictionaryManager(getNodeContext(), this.namespaceUri);
		getLifecycleManager().addLifecycle(dictionaryManager);
		getLifecycleManager().addLifecycle(subscriptionModel);
		getLifecycleManager().addStartupTask(this::createAndAddNodes);
		rulesEngine = new RulesEngine(this);
		getLifecycleManager().addLifecycle(rulesEngine);
		logger.debug("ManagedNamespace created with NodeManager -> " + super.getNodeManager());
	}

	public void addNode(final AddNodesContext context, final AddNodesItem addNodeItem) throws Exception {
		rulesEngine.addNode(context, addNodeItem);
		for (final AuditListener auditListener : wrapperOpcUaServer.listAuditHooks()) {
			try {
				auditListener.addNode(context, addNodeItem);
			} catch (final Exception a) {
				logger.error("invoke audit hook", a);
			}
		}
		final UaNode node = MiloHelper.createNodeFromAddNodeItem(this, context, addNodeItem);
		getNodeManager().addNode(node);
		node.addReference(new Reference(getNodeManager().get(node.getNodeId()).getNodeId(), Identifiers.Organizes,
				addNodeItem.getParentNodeId(), false));
	}

	@Override
	public void addNodes(final AddNodesContext context, final List<AddNodesItem> nodesToAdd) {
		for (final AddNodesItem n : nodesToAdd) {
			try {
				addNode(context, n);
			} catch (final Exception e) {
				logger.error("try to add " + n.toString() + "\n" + LogHelper.stackTraceToString(e));
			}
		}

	}

	@Override
	public void addReferences(final AddReferencesContext context, final List<AddReferencesItem> referencesToAdd) {
		for (final AddReferencesItem r : referencesToAdd) {
			try {
				addReference(context, r);
			} catch (final Exception e) {
				logger.error("try to add " + r.toString() + "\n" + LogHelper.stackTraceToString(e));
			}
		}
	}

	@Override
	public void attributeChanged(final UaNode node, final AttributeId attributeId, final Object value) {
		storageController.attributeChanged(node, attributeId, value);
		rulesEngine.attributeChanged(node, attributeId, value);
		for (final AuditListener auditListener : wrapperOpcUaServer.listAuditHooks()) {
			try {
				auditListener.attributeChanged(node, attributeId, value);
			} catch (final Exception a) {
				logger.error("invoke audit hook", a);
			}
		}
	}

	@Override
	public void browse(final BrowseContext context, final NodeId nodeId) {
		super.browse(context, nodeId);
	}

	@Override
	public void browse(final BrowseContext context, final ViewDescription viewDescription, final NodeId nodeId) {
		super.browse(context, viewDescription, nodeId);
	}

	@Override
	public void call(final CallContext context, final List<CallMethodRequest> requests) {
		rulesEngine.call(context, requests);
		for (final AuditListener auditListener : wrapperOpcUaServer.listAuditHooks()) {
			try {
				auditListener.call(context, requests);
			} catch (final Exception a) {
				logger.error("invoke audit hook", a);
			}
		}
		super.call(context, requests);
	}

	@Override
	public void deleteNodes(final DeleteNodesContext context, final List<DeleteNodesItem> nodesToDelete) {
		rulesEngine.deleteNodes(context, nodesToDelete);
		for (final AuditListener auditListener : wrapperOpcUaServer.listAuditHooks()) {
			try {
				auditListener.deleteNodes(context, nodesToDelete);
			} catch (final Exception a) {
				logger.error("invoke audit hook", a);
			}
		}
		super.deleteNodes(context, nodesToDelete);
	}

	@Override
	public void deleteReferences(final DeleteReferencesContext context,
			final List<DeleteReferencesItem> referencesToDelete) {
		rulesEngine.deleteReferences(context, referencesToDelete);
		for (final AuditListener auditListener : wrapperOpcUaServer.listAuditHooks()) {
			try {
				auditListener.deleteReferences(context, referencesToDelete);
			} catch (final Exception a) {
				logger.error("invoke audit hook", a);
			}
		}
		super.deleteReferences(context, referencesToDelete);
	}

	public DataTypeDictionaryManager getDictionaryManager() {
		return dictionaryManager;
	}

	@Override
	public AddressSpaceFilter getFilter() {
		return filter;
	}

	public LifecycleManager getLifecycleManager() {
		return lifecycleManager;
	}

	@Override
	public final UShort getNamespaceIndex() {
		return namespaceIndex;
	}

	@Override
	public final String getNamespaceUri() {
		return namespaceUri;
	}

	@Override
	public UaNodeContext getNodeContext() {
		return super.getNodeContext();
	}

	@Override
	public NodeFactory getNodeFactory() {
		return super.getNodeFactory();
	}

	@Override
	public UaNodeManager getNodeManager() {
		return super.getNodeManager();
	}

	@Override
	public void getReferences(final BrowseContext context, final ViewDescription viewDescription, final NodeId nodeId) {
		super.getReferences(context, viewDescription, nodeId);
	}

	@Override
	public OpcUaServer getServer() {
		return super.getServer();
	}

	public StorageController getStorageController() {
		return storageController;
	}

	@Override
	public UInteger getViewCount() {
		return super.getViewCount();
	}

	@Override
	public void historyRead(final HistoryReadContext context, final HistoryReadDetails readDetails,
			final TimestampsToReturn timestamps, final List<HistoryReadValueId> readValueIds) {
		super.historyRead(context, readDetails, timestamps, readValueIds);
	}

	@Override
	public void historyUpdate(final HistoryUpdateContext context, final List<HistoryUpdateDetails> updateDetails) {
		rulesEngine.historyUpdate(context, updateDetails);
		for (final AuditListener auditListener : wrapperOpcUaServer.listAuditHooks()) {
			try {
				auditListener.historyUpdate(context, updateDetails);
			} catch (final Exception a) {
				logger.error("invoke audit hook", a);
			}
		}
		super.historyUpdate(context, updateDetails);
	}

	public final NodeId newNodeId(final String id) {
		return new NodeId(namespaceIndex, id);
	}

	public final NodeId newNodeId(final UUID id) {
		return new NodeId(namespaceIndex, id);
	}

	public final QualifiedName newQualifiedName(final String name) {
		return new QualifiedName(namespaceIndex, name);
	}

	@Override
	public void onDataItemsCreated(final List<DataItem> dataItems) {
		rulesEngine.onDataItemsCreated(dataItems);
		for (final AuditListener auditListener : wrapperOpcUaServer.listAuditHooks()) {
			try {
				auditListener.onDataItemsCreated(dataItems);
			} catch (final Exception a) {
				logger.error("invoke audit hook", a);
			}
		}
	}

	@Override
	public void onDataItemsDeleted(final List<DataItem> dataItems) {
		rulesEngine.onDataItemsDeleted(dataItems);
		for (final AuditListener auditListener : wrapperOpcUaServer.listAuditHooks()) {
			try {
				auditListener.onDataItemsDeleted(dataItems);
			} catch (final Exception a) {
				logger.error("invoke audit hook", a);
			}
		}

	}

	@Override
	public void onDataItemsModified(final List<DataItem> dataItems) {
		rulesEngine.onDataItemsModified(dataItems);
		for (final AuditListener auditListener : wrapperOpcUaServer.listAuditHooks()) {
			try {
				auditListener.onDataItemsModified(dataItems);
			} catch (final Exception a) {
				logger.error("invoke audit hook", a);
			}
		}

	}

	@Override
	public void onMonitoringModeChanged(final List<MonitoredItem> monitoredItems) {
		rulesEngine.onMonitoringModeChanged(monitoredItems);
		for (final AuditListener auditListener : wrapperOpcUaServer.listAuditHooks()) {
			try {
				auditListener.onMonitoringModeChanged(monitoredItems);
			} catch (final Exception a) {
				logger.error("invoke audit hook", a);
			}
		}

	}

	@Override
	public void read(final ReadContext context, final Double maxAge, final TimestampsToReturn timestamps,
			final List<ReadValueId> readValueIds) {
		rulesEngine.read(context, maxAge, timestamps, readValueIds);
		for (final AuditListener auditListener : wrapperOpcUaServer.listAuditHooks()) {
			try {
				auditListener.read(context, maxAge, timestamps, readValueIds);
			} catch (final Exception a) {
				logger.error("invoke audit hook", a);
			}
		}
		super.read(context, maxAge, timestamps, readValueIds);
	}

	@Override
	public void registerNodes(final RegisterNodesContext context, final List<NodeId> nodeIds) {
		super.registerNodes(context, nodeIds);
	}

	public final void shutdown() {
		lifecycleManager.shutdown();
	}

	public final void startup() {
		lifecycleManager.startup();
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("ManagedNamespace [");
		if (namespaceIndex != null) {
			builder.append("namespaceIndex=");
			builder.append(namespaceIndex);
			builder.append(", ");
		}
		if (namespaceUri != null) {
			builder.append("namespaceUri=");
			builder.append(namespaceUri);
			builder.append(", ");
		}
		if (wrapperOpcUaServer != null) {
			builder.append("wrapperOpcUaServer=");
			builder.append(wrapperOpcUaServer);
		}
		builder.append("]");
		return builder.toString();
	}

	@Override
	public void unregisterNodes(final UnregisterNodesContext context, final List<NodeId> nodeIds) {
		super.unregisterNodes(context, nodeIds);
	}

	@Override
	public void write(final WriteContext context, final List<WriteValue> writeValues) {
		rulesEngine.write(context, writeValues);
		for (final AuditListener auditListener : wrapperOpcUaServer.listAuditHooks()) {
			try {
				auditListener.write(context, writeValues);
			} catch (final Exception a) {
				logger.error("invoke audit hook", a);
			}
		}
		super.write(context, writeValues);
	}

	private void addGenerateFromDtdlMethod() {
		final UaMethodNode methodNode = UaMethodNode.builder(getNodeContext())
				.setNodeId(newNodeId("server/generateTypeFromDtdl"))
				.setBrowseName(newQualifiedName("generateTypeFromDtdl"))
				.setDisplayName(new LocalizedText(null, "generateTypeFromDtdl"))
				.setDescription(LocalizedText.english("Generate type model from dtdl in json format.")).build();

		final GenerateTypeObjectMethod generateTypeMethod = new GenerateTypeObjectMethod(this, methodNode);
		methodNode.setInputArguments(generateTypeMethod.getInputArguments());
		methodNode.setOutputArguments(generateTypeMethod.getOutputArguments());
		methodNode.setInvocationHandler(generateTypeMethod);

		getNodeManager().addNode(methodNode);

		methodNode.addReference(
				new Reference(methodNode.getNodeId(), Identifiers.HasComponent, Identifiers.Server.expanded(), false));
	}

	private void addInstantiateObjectMethod() {
		final UaMethodNode methodNode = UaMethodNode.builder(getNodeContext()).setNodeId(newNodeId("server/addNode"))
				.setBrowseName(newQualifiedName("addNode")).setDisplayName(new LocalizedText(null, "addNode"))
				.setDescription(LocalizedText.english("Add node to the namespace")).build();

		final InstantiateObjectMethod instantiateObjectMethod = new InstantiateObjectMethod(this, methodNode);
		methodNode.setInputArguments(instantiateObjectMethod.getInputArguments());
		methodNode.setOutputArguments(instantiateObjectMethod.getOutputArguments());
		methodNode.setInvocationHandler(instantiateObjectMethod);

		getNodeManager().addNode(methodNode);

		methodNode.addReference(
				new Reference(methodNode.getNodeId(), Identifiers.Organizes, Identifiers.Server.expanded(), false));

	}

	private void addReference(final AddReferencesContext context, final AddReferencesItem reference) {
		rulesEngine.addReference(context, reference);
		for (final AuditListener auditListener : wrapperOpcUaServer.listAuditHooks()) {
			try {
				auditListener.addReference(context, reference);
			} catch (final Exception a) {
				logger.error("invoke audit hook", a);
			}
		}
		// TODO Implementare aggiunta dinamica referenza

	}

	private void addShutdownMethod() {
		final UaMethodNode methodNode = UaMethodNode.builder(getNodeContext()).setNodeId(newNodeId("server/shutdown"))
				.setBrowseName(newQualifiedName("shutdown")).setDisplayName(new LocalizedText(null, "shutdown"))
				.setDescription(LocalizedText.english("Shutdown the opcua server.")).build();
		final ShutdownMethod shutdownMethod = new ShutdownMethod(wrapperOpcUaServer, methodNode);
		methodNode.setInputArguments(shutdownMethod.getInputArguments());
		methodNode.setOutputArguments(shutdownMethod.getOutputArguments());
		methodNode.setInvocationHandler(shutdownMethod);
		getNodeManager().addNode(methodNode);
		methodNode.addReference(
				new Reference(methodNode.getNodeId(), Identifiers.Organizes, Identifiers.Server.expanded(), false));
	}

	// TODO revisionare creazione namespace
	private void createAndAddNodes() {
		final NodeId folderNodeId = newNodeId(wrapperOpcUaServer.getOpcUaServerConfiguration().getRootNodeId());
		final NodeId rulesEngineNodeId = newNodeId("rules-engine");
		final NodeId traceNodeId = newNodeId("traces");

		final UaFolderNode folderNode = new UaFolderNode(getNodeContext(), folderNodeId,
				newQualifiedName(wrapperOpcUaServer.getOpcUaServerConfiguration().getRootBrowseName()),
				LocalizedText.english(wrapperOpcUaServer.getOpcUaServerConfiguration().getRootDisplayNameEnglish()));
		folderNode.setDescription(
				LocalizedText.english(wrapperOpcUaServer.getOpcUaServerConfiguration().getRootDescriptionEnglish()));
		getNodeManager().addNode(folderNode);

		final UaFolderNode rulesEngineNode = new UaFolderNode(getNodeContext(), rulesEngineNodeId,
				newQualifiedName("rules-engine"), LocalizedText.english("rules-engine"));
		rulesEngineNode.setDescription(LocalizedText.english("OPC UA Server Rules Engine"));
		getNodeManager().addNode(rulesEngineNode);

		final UaFolderNode traceNode = new UaFolderNode(getNodeContext(), traceNodeId, newQualifiedName("traces"),
				LocalizedText.english("traces"));
		traceNode.setDescription(LocalizedText.english("Trace of admin events"));
		getNodeManager().addNode(traceNode);

		folderNode.addReference(new Reference(folderNode.getNodeId(), Identifiers.Organizes,
				Identifiers.ObjectsFolder.expanded(), false));
		rulesEngineNode.addReference(new Reference(rulesEngineNode.getNodeId(), Identifiers.Organizes,
				Identifiers.Server.expanded(), false));
		traceNode.addReference(
				new Reference(traceNode.getNodeId(), Identifiers.Organizes, Identifiers.Server.expanded(), false));

		addShutdownMethod();
		addGenerateFromDtdlMethod();
		addInstantiateObjectMethod();
	}

	private void registerAddressSpace(final AddressSpaceFragment addressSpace) {
		getServer().getAddressSpaceManager().register(addressSpace);
	}

	private void registerNodeManager(final UaNodeManager nodeManager) {
		getServer().getAddressSpaceManager().register(nodeManager);
	}

	private void unregisterAddressSpace(final AddressSpaceFragment addressSpace) {
		getServer().getAddressSpaceManager().unregister(addressSpace);
	}

	private void unregisterNodeManager(final UaNodeManager nodeManager) {
		getServer().getAddressSpaceManager().unregister(nodeManager);
	}

}
