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

import nl.openweb.hippo.dynamicimage.service.VariantService;
import org.hippoecm.hst.content.beans.standard.HippoGalleryImageBean;
import org.hippoecm.hst.site.HstServices;
import org.hippoecm.hst.tag.HstLinkTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.servlet.jsp.JspException;

/**
 * This extension of the HstLinkTag creates imagevariants wen they don't exist. The result of the tag is a link to an
 * image variant. This tag supports scaling and cropping.
 * 
 * @author Ivor Boers
 *
 */
public class DynamicImageLinkTag extends HstLinkTag {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(DynamicImageLinkTag.class);

    private Integer width;
    private Integer height;
    private transient HippoGalleryImageBean imagebean;

    @Override
    public int doEndTag() throws JspException {
        long time = System.currentTimeMillis();
        linkForAttributeSet = false;

        VariantService variantService = HstServices.getComponentManager().getComponent(VariantService.class);

        if (variantService != null) {
            try {
                Node variantNode = variantService.getOrCreateVariant(getImagebean(), getWidth(), getHeight());

                if (variantNode != null) {
                    try {
                        String name = variantNode.getName();
                        setHippobean(getImagebean().getParentBean().getBean(name, HippoGalleryImageBean.class));
                    } catch (RepositoryException e) {
                        LOG.error("Failed to create image variant.", e);
                    }
                } else {
                    LOG.error("Failed to create image variant. No image will be available.");
                }
            } catch (RepositoryException e) {
                LOG.error("Failed to get variant", e);
            }
        } else {
            LOG.warn("No VariantService found in Spring context");
        }

        LOG.trace("Image processing took " + (System.currentTimeMillis() - time) + " ms extra");
        return super.doEndTag();
    }

    @Override
    protected void cleanup() {
        super.cleanup();
        width = null;
        height = null;
        imagebean = null;
    }

    /**
     * get the width, defaults to zero
     * 
     * @return the width, defaults to zero
     */
    public Integer getWidth() {
        return width == null ? 0 : width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    /**
     * get the height, defaults to zero
     * 
     * @return the height, defaults to zero
     */
    public Integer getHeight() {
        return height == null ? 0 : height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public HippoGalleryImageBean getImagebean() {
        return imagebean;
    }
    
    public void setImagebean(HippoGalleryImageBean imagebean) {
        this.imagebean = imagebean;
    }

}