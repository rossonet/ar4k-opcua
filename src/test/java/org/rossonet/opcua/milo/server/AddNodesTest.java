package org.rossonet.opcua.milo.server;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.milo.examples.client.ClientExample;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.ExpandedNodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.ExtensionObject;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.enumerated.BrowseDirection;
import org.eclipse.milo.opcua.stack.core.types.enumerated.NodeClass;
import org.eclipse.milo.opcua.stack.core.types.structured.AddNodesItem;
import org.eclipse.milo.opcua.stack.core.types.structured.BrowseDescription;
import org.eclipse.milo.opcua.stack.core.types.structured.BrowseResult;
import org.junit.jupiter.api.Test;

public class AddNodesTest {

	private class AddNodeExample implements ClientExample {

		private final NodeClass nodeClass;
		private final String parent;
		private final String shortName;
		private final String target;
		boolean result = false;

		public AddNodeExample(final String parent, final String target, final String shortName,
				final NodeClass nodeClass) {
			this.parent = parent;
			this.target = target;
			this.shortName = shortName;
			this.nodeClass = nodeClass;
		}

		public AddNodesItem createVariable(final OpcUaClient client) throws Exception {
			final ExpandedNodeId parentNodeId = ExpandedNodeId.parse(parent);
			final NodeId referenceTypeId = Identifiers.ReferenceNode.expanded()
					.toNodeIdOrThrow(client.getNamespaceTable());
			final ExpandedNodeId requestedNewNodeId = ExpandedNodeId.parse(target);
			final QualifiedName browseName = QualifiedName.parse("1:" + shortName);
			final NodeClass nodeClass = this.nodeClass;
			final ExtensionObject nodeAttributes = null;
			final ExpandedNodeId typeDefinition = Identifiers.String.expanded();
			final AddNodesItem nodeToAdd = new AddNodesItem(parentNodeId, referenceTypeId, requestedNewNodeId,
					browseName, nodeClass, nodeAttributes, typeDefinition);
			return nodeToAdd;
		}

		@Override
		public boolean getTestResult() {
			return result;
		}

		@Override
		public void run(final OpcUaClient client, final CompletableFuture<OpcUaClient> future) throws Exception {
			try {
				client.connect().get();
				final List<AddNodesItem> nodes = new ArrayList<>();
				final AddNodesItem nodeVariable = createVariable(client);
				nodes.add(nodeVariable);
				client.addNodes(nodes);
				Thread.sleep(6000);
				final BrowseDescription browseDescription = BrowseDescription.builder()
						.browseDirection(BrowseDirection.Forward).nodeId(NodeId.parse(parent)).build();
				final CompletableFuture<BrowseResult> replyBrowse = client.browse(browseDescription);
				final String browse = replyBrowse.get().toString();
				result = browse.contains(shortName);
				System.out.println(result + " *** REPLY BROWSE " + browse);
				Thread.sleep(2000);
				future.complete(client);
			} catch (final Exception aa) {
				aa.printStackTrace();
			}
		}

	}

	@Test
	public void addDataTypeNodeTest() throws Exception {
		final AddNodeExample example = new AddNodeExample("ns=1;s=server-data", "ns=1;s=server-data/test-node-data",
				"test-node-data", NodeClass.DataType);
		final ClientRossonetTestRunner client = new ClientRossonetTestRunner(example);
		client.run();
		assertTrue(example.getTestResult());
	}

	@Test
	public void addObjectNodeTest() throws Exception {
		final AddNodeExample example = new AddNodeExample("ns=1;s=server-data", "ns=1;s=server-data/test-node-object",
				"test-node-object", NodeClass.ObjectType);
		final ClientRossonetTestRunner client = new ClientRossonetTestRunner(example);
		client.run();
		assertTrue(example.getTestResult());
	}

	@Test
	public void addVariableNodeTest() throws Exception {
		final AddNodeExample example = new AddNodeExample("ns=1;s=server-data", "ns=1;s=server-data/test-node-variable",
				"test-node-variable", NodeClass.Variable);
		final ClientRossonetTestRunner client = new ClientRossonetTestRunner(example);
		client.run();
		assertTrue(example.getTestResult());
	}

}
