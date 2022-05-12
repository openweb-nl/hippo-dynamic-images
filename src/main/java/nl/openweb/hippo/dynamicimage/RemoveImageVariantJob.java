/*
 * Copyright 2017 Open Web IT B.V. (https://www.openweb.nl/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.openweb.hippo.dynamicimage;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Job that removes the node of the requested variant.
 */
public class RemoveImageVariantJob extends RepositoryJob<Void> {
    private static final Logger LOG = LoggerFactory.getLogger(RemoveImageVariantJob.class);

    private final Node variantNode;

    /**
     * Job to remove an imagevariant from the repository

     * @param variantNode the node stored in the repository
     */
    public RemoveImageVariantJob(Node variantNode) {
        super();
        this.variantNode = variantNode;
    }

    @Override
    public Void doJob(Session session)  throws JobExecutionException{
        try {
            // get the node in the writable session
            Node node = session.getNodeByIdentifier(variantNode.getIdentifier());
            node.remove();
        } catch (RepositoryException e) {
            throw new JobExecutionException("Failed to create dynamic imagevariant", e);
        }
        return null;
    }
}
