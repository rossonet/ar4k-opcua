/*
 * Copyright (c) 2019 the Eclipse Milo Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.milo.examples.client;

import static com.google.common.collect.Lists.newArrayList;
import static org.eclipse.milo.opcua.stack.core.util.ConversionUtil.l;

import java.util.concurrent.CompletableFuture;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.structured.BrowsePath;
import org.eclipse.milo.opcua.stack.core.types.structured.BrowsePathResult;
import org.eclipse.milo.opcua.stack.core.types.structured.RelativePath;
import org.eclipse.milo.opcua.stack.core.types.structured.RelativePathElement;
import org.eclipse.milo.opcua.stack.core.types.structured.TranslateBrowsePathsToNodeIdsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TranslateBrowsePathExample implements ClientExample {

	public static void main(final String[] args) throws Exception {
		final TranslateBrowsePathExample example = new TranslateBrowsePathExample();

		new ClientExampleRunner(example).run();
	}

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public boolean getTestResult() {
		// TODO verificare risultato test
		return true;
	}

	@Override
	public void run(final OpcUaClient client, final CompletableFuture<OpcUaClient> future) throws Exception {
		// synchronous connect
		client.connect().get();

		final TranslateBrowsePathsToNodeIdsResponse response = client
				.translateBrowsePaths(newArrayList(new BrowsePath(Identifiers.ObjectsFolder,
						new RelativePath(new RelativePathElement[] {
								new RelativePathElement(Identifiers.HierarchicalReferences, false, true,
										new QualifiedName(2, "HelloWorld")),
								new RelativePathElement(Identifiers.HierarchicalReferences, false, true,
										new QualifiedName(2, "ScalarTypes")),
								new RelativePathElement(Identifiers.HierarchicalReferences, false, true,
										new QualifiedName(2, "UInt64")) }))))
				.get();

		final BrowsePathResult result = l(response.getResults()).get(0);
		final StatusCode statusCode = result.getStatusCode();
		logger.info("Status={}", statusCode);

		l(result.getTargets()).forEach(target -> logger.info("TargetId={}", target.getTargetId()));

		future.complete(client);
	}

}
