package nl.openweb.hippo.dynamicimage.strategy;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

public interface VariantStrategy {
    Node createVariant(Node sourceVariantNode, String variantName, int width, int height) throws RepositoryException;
}
