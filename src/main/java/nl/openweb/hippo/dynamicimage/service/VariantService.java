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

    void setVariantStrategy(VariantStrategy variantStrategy);

    VariantStrategy getVariantStrategy();

    void setTimeoutMilliseconds(int timeoutMilliseconds);

    int getTimeoutMilliseconds();

    Node getOrCreateVariant(HippoGalleryImageBean sourceVariant, int width, int height) throws RepositoryException;
}
