package nl.openweb.hippo.dynamicimage.strategy;

import org.hippoecm.frontend.plugins.gallery.processor.ScalingGalleryProcessor;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

public interface VariantStrategy {

    void setGalleryProcessor(ScalingGalleryProcessor processor);

    Node createVariant(Node sourceVariantNode, String variantName, int width, int height) throws RepositoryException;
}
