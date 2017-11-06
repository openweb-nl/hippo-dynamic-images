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

import nl.openweb.hippo.dynamicimage.strategy.VariantStrategy;
import org.hippoecm.hst.content.beans.standard.HippoGalleryImageBean;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

/**
 * Service for creating image variants
 * @author Ivor Boers
 */
public interface VariantService {

    /**
     * @param variantStrategy the strategy to use for creating the image variant
     */
    void setVariantStrategy(VariantStrategy variantStrategy);

    /**
     * @return the strategy to use for creating the image variant
     */
    VariantStrategy getVariantStrategy();

    /**
     * @param timeoutMilliseconds the timeout for creating the image variant in the repository
     */
    void setTimeoutMilliseconds(int timeoutMilliseconds);

    /**
     * @return the timeout in milliseconds for creating the image variant in the repository
     */
    int getTimeoutMilliseconds();

    /**
     *
     * @param sourceVariant the existing variant to use as the source
     * @param width the width of the new variant
     * @param height the height of the new variant
     * @return the Node for the
     * @throws RepositoryException
     */
    Node getOrCreateVariant(HippoGalleryImageBean sourceVariant, int width, int height) throws RepositoryException;
}
