package nl.openweb.hippo.dynamicimage.strategy;

import nl.openweb.hippo.dynamicimage.CropArea;
import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.value.BinaryImpl;
import org.hippoecm.frontend.plugins.gallery.imageutil.ImageUtils;
import org.hippoecm.frontend.plugins.gallery.imageutil.ScalingParameters;
import org.hippoecm.frontend.plugins.gallery.processor.ScalingGalleryProcessor;
import org.hippoecm.repository.gallery.HippoGalleryNodeType;
import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageInputStream;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class CenteredVariantStrategy implements VariantStrategy {
    private static final Logger LOG = LoggerFactory.getLogger(CenteredVariantStrategy.class);
    protected static final float EPSILON = 0.000001f;

    @Override
    public Node createVariant(Node sourceVariantNode, String variantName, int width, int height) throws RepositoryException {
        if (LOG.isInfoEnabled()) {
            LOG.info("Image format node {} not found; creating it now", variantName);
        }
        Node node = sourceVariantNode.getParent();
        Node newVariantNode = node.addNode(variantName, HippoGalleryNodeType.IMAGE);
        newVariantNode.setProperty(JcrConstants.JCR_MIMETYPE, "image/jpeg");
        newVariantNode.setProperty(JcrConstants.JCR_DATA, new BinaryImpl(new byte[0]));
        newVariantNode.setProperty(JcrConstants.JCR_LASTMODIFIED, Calendar.getInstance());
        setImage(newVariantNode, sourceVariantNode, width, height);
        return newVariantNode;
    }

    private void setImage(Node newVariantNode, Node sourceVariantNode, int width, int height) throws RepositoryException {
        String originalFileName = getOrCreateFileName(sourceVariantNode.getParent());
        ScalingGalleryProcessor processor = new ScalingGalleryProcessor();

        Calendar lastModified = new GregorianCalendar();
        InputStream imageStream = getData(sourceVariantNode);
        String mimeType = getMimeType(sourceVariantNode);
        if (width > 0 && height > 0) {
            // only crop if both width and height are specified. Otherwise no need for cropping
            imageStream = cropImage(imageStream, mimeType, width, height);
            //if cropped the width should be the most important
            processor.addScalingParameters(newVariantNode.getName(), new ScalingParameters(width, 0, true));
        }else{
            processor.addScalingParameters(newVariantNode.getName(), new ScalingParameters(width, height, true));
        }
        processor.initGalleryResource(newVariantNode, imageStream, mimeType, originalFileName, lastModified);
    }

    protected InputStream cropImage(final InputStream data, final String mimeType, int width, int height) {
        try {
            ImageWriter writer = ImageUtils.getImageWriter(mimeType);
            ImageReader reader = ImageUtils.getImageReader(mimeType);

            MemoryCacheImageInputStream imageInputStream = new MemoryCacheImageInputStream(data);
            reader.setInput(imageInputStream);
            BufferedImage originalImage = reader.read(0);

            int originalWidth = originalImage.getWidth();
            int originalHeight = originalImage.getHeight();

            float imageRatio = (float) originalWidth / (float) originalHeight;
            float targetRatio = (float) width / (float) height;

            if (Math.abs(targetRatio - imageRatio) > EPSILON) {
                // Cropping is needed!

                // create the best-fit cropping area.
                CropArea cropArea = new CropArea(originalWidth, originalHeight, width, height);
                // use ImageScalr library to crop the the image
                originalImage = Scalr.crop(originalImage, cropArea.getX(), cropArea.getY(), cropArea.getWidth(),
                        cropArea.getHeight());
            }

            return new ByteArrayInputStream(ImageUtils.writeImage(writer, originalImage).toByteArray());
        } catch (IOException x) {
            LOG.warn("Failed to crop image", x);
        }
        return data;
    }

    private String getOrCreateFileName(final Node node) throws RepositoryException {
        String fileName;
        if (node.hasProperty(HippoGalleryNodeType.IMAGE_SET_FILE_NAME)) {
            fileName = node.getProperty(HippoGalleryNodeType.IMAGE_SET_FILE_NAME).getString().toLowerCase();
        } else {
            if (LOG.isInfoEnabled()) {
                LOG.info("Node didn't have a {} property, setting it now", HippoGalleryNodeType.IMAGE_SET_FILE_NAME);
            }
            fileName = node.getName().toLowerCase();
            node.setProperty(HippoGalleryNodeType.IMAGE_SET_FILE_NAME, fileName);
        }
        return fileName;
    }

    private String getMimeType(Node node) throws RepositoryException {
        return node.getProperty(JcrConstants.JCR_MIMETYPE).getString();
    }

    private InputStream getData(Node node) throws RepositoryException {
        return node.getProperty(JcrConstants.JCR_DATA).getBinary().getStream();
    }
}
