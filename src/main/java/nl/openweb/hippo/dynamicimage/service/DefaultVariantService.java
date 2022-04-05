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
package nl.openweb.hippo.dynamicimage.service;

import nl.openweb.hippo.dynamicimage.ImageVariantJob;
import nl.openweb.hippo.dynamicimage.RemoveImageVariantJob;
import nl.openweb.hippo.dynamicimage.strategy.VariantStrategy;
import org.hippoecm.hst.content.beans.standard.HippoGalleryImageBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.Calendar;
import java.util.concurrent.*;

/**
 * Default service for creating image variants
 * @author Ivor Boers
 */
public class DefaultVariantService implements VariantService {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultVariantService.class);
    public static final String JCR_LAST_MODIFIED = "jcr:lastModified";
    private VariantStrategy variantStrategy;
    private int timeoutMilliseconds = 3000;

    public DefaultVariantService() {
    }

    @Override
    public void setVariantStrategy(VariantStrategy variantStrategy) {
        this.variantStrategy = variantStrategy;
    }

    @Override
    public VariantStrategy getVariantStrategy() {
        return variantStrategy;
    }

    @Override
    public void setTimeoutMilliseconds(int timeoutMilliseconds) {
        this.timeoutMilliseconds = timeoutMilliseconds;
    }

    @Override
    public int getTimeoutMilliseconds() {
        return timeoutMilliseconds;
    }

    @Override
    public Node getOrCreateVariant(HippoGalleryImageBean sourceVariant, int width, int height) throws RepositoryException {
        // first handle the 90% case where the variant is available
        Node sourceVariantNode = sourceVariant.getNode();
        String variantName = getVariantName(sourceVariantNode.getName(), width, height);

        if (hasVariant(sourceVariantNode, variantName) &&
                !needsToBeUpdated(sourceVariantNode.getParent().getNode(variantName), sourceVariantNode)) {
            return sourceVariantNode.getParent().getNode(variantName);
        }
        // variant does not exist or needs updating.
        // synchronize next block of code to prevent multiple threads to create the same variant
        // recheck hasVariant etc in synchronized block because of other threads
        // key is a unique String to identify a variant of an imageset
        // key.intern() is a threadsafe way to get the String from the pool maintained by String.
        String key = sourceVariantNode.getParent().getIdentifier() + variantName;
        synchronized (key.intern()){
            try {
                if (hasVariant(sourceVariantNode, variantName)) {
                    Node variantNode = sourceVariantNode.getParent().getNode(variantName);

                    if (needsToBeUpdated(variantNode, sourceVariantNode)) {
                        removeVariant(variantNode);
                    } else {
                        return variantNode;
                    }
                }
                return createVariant(sourceVariant, width, height, variantName);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                LOG.error("Failed to create or update image variant at " + sourceVariant.getPath() + "/" + variantName, e);
                return null;
            }
        }
    }

    private void removeVariant(final Node variantNode) throws ExecutionException, InterruptedException {
        RemoveImageVariantJob job = new RemoveImageVariantJob(variantNode);
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(job).get();
        executor.shutdown(); // This does not cancel the already-scheduled task.
    }

    private Node createVariant(HippoGalleryImageBean sourceVariant, int width, int height, String variantName) throws InterruptedException, ExecutionException, TimeoutException {
        ImageVariantJob job = new ImageVariantJob(getVariantStrategy(), sourceVariant.getNode(), variantName, width, height);

        final ExecutorService executor = Executors.newSingleThreadExecutor();
        long time0 = System.currentTimeMillis();
        final Future<Node> future = executor.submit(job);
        executor.shutdown(); // This does not cancel the already-scheduled task.
        Node node = future.get(getTimeoutMilliseconds(), TimeUnit.MILLISECONDS);
        if (LOG.isTraceEnabled()) {
            LOG.trace("ImageVariantJob took " + (System.currentTimeMillis() - time0) + "ms to complete and return " +
                    (node == null ? "null" : "a newly created node for the image variant"));
        }
        return node;
    }

    /**
     * determines if the imagevariant needs to be updated. An editor might have cropped the imagevariant
     * or uploaded a new image, etc.
     *
     * @param dynamicVariant
     *            the (new) node, this could be a node with only a name and an image type
     * @param sourceVariant
     *            the original image, which contains all the properties
     * @return whether the image should be created or updated
     * @throws RepositoryException
     */
    protected boolean needsToBeUpdated(Node dynamicVariant, Node sourceVariant) throws RepositoryException {
        if (dynamicVariant == null || !dynamicVariant.hasProperty(JCR_LAST_MODIFIED)) {
            return true;
        }
        Calendar formatDate = dynamicVariant.getProperty(JCR_LAST_MODIFIED).getDate();
        Calendar originalDate = sourceVariant.getProperty(JCR_LAST_MODIFIED).getDate();
        if (LOG.isTraceEnabled()) {
            LOG.trace("Image at {} has lastmodified date {} and the original {}, image will be reset: {}",
                    dynamicVariant.getPath(), formatDate.getTime(), originalDate.getTime(), originalDate.after(formatDate));
        }
        return originalDate.after(formatDate);
    }

    protected boolean hasVariant(Node sourceVariantNode, String variantName) throws RepositoryException {
        return sourceVariantNode.getParent().hasNode(variantName);
    }

    /**
     * get the name of the node of the dynamic imagevariant
     *
     * @param width Width of the new variant
     * @param height Height of the new variant
     * @return the name of the node where the variant is stored
     */
    String getVariantName(String sourceVariantName, Integer width, Integer height) {
        if ((width == null || width <= 0) && (height == null || height <= 0)) {
            return sourceVariantName;
        }
        StringBuilder sb = new StringBuilder("d");
        sb.append("_").append(sourceVariantName.replaceAll(":", "-")).append("_");
        if (width == null || width <= 0) {
            sb.append("var");
        } else {
            sb.append(width);
        }
        sb.append("x");
        if (height == null || height <= 0) {
            sb.append("var");
        } else {
            sb.append(height);
        }

        return sb.toString();
    }
}
