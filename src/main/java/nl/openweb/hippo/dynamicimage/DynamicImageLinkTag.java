package nl.openweb.hippo.dynamicimage;

import nl.openweb.hippo.dynamicimage.service.DefaultVariantService;
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
    private transient HippoGalleryImageBean imageBean;
    private final DefaultVariantService service = new DefaultVariantService();

    @Override
    public int doEndTag() throws JspException {
        long time = System.currentTimeMillis();
        linkForAttributeSet = false;

        VariantService variantService = HstServices.getComponentManager().getComponent(VariantService.class);

        if (variantService != null) {
            try {
                Node variantNode = variantService.getOrCreateVariant(getImageBean(), getWidth(), getHeight());

                if (variantNode != null) {
                    try {
                        String name = variantNode.getName();
                        setHippobean(getImageBean().getParentBean().getBean(name, HippoGalleryImageBean.class));
                    } catch (RepositoryException e) {
                        LOG.error("Failed to create image variant.", e);
                    }
                } else {
                    LOG.error("Failed to create image variant.");
                }
            } catch (RepositoryException e) {
                e.printStackTrace();
            }
        }

        LOG.trace("Image processing took " + (System.currentTimeMillis() - time) + " ms extra");
        return super.doEndTag();
    }

    @Override
    protected void cleanup() {
        super.cleanup();
        width = null;
        height = null;
        imageBean = null;
    }

    /**
     * get the width, defaults to zero
     * 
     * @return
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
     * @return
     */
    public Integer getHeight() {
        return height == null ? 0 : height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public HippoGalleryImageBean getImageBean() {
        return imageBean;
    }
    
    public void setImageBean(HippoGalleryImageBean imageBean) {
        this.imageBean = imageBean;
    }

}