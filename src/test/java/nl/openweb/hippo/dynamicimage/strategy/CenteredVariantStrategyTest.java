package nl.openweb.hippo.dynamicimage.strategy;

import junit.framework.Assert;

import org.hippoecm.frontend.plugins.gallery.imageutil.ImageUtils;
import org.hippoecm.hst.content.beans.standard.HippoGalleryImageSet;
import org.junit.Test;

import javax.imageio.ImageReader;
import javax.imageio.stream.MemoryCacheImageInputStream;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import static org.mockito.Mockito.*;

public class CenteredVariantStrategyTest {


    private HippoGalleryImageSet imageSet = mock(HippoGalleryImageSet.class);

    @Test
    public void testCropLandscapeFromLandscapeImage() {

        InputStream imageStream = getLandscapeImage();
        CenteredVariantStrategy strategy = new CenteredVariantStrategy();
        InputStream croppedImageStream = strategy.cropImage(imageStream, "image/jpeg", 200, 50);

        BufferedImage image = readJpgImage(croppedImageStream);

        Assert.assertNotNull(image);
        Assert.assertEquals(600, image.getWidth());
        Assert.assertEquals(150, image.getHeight());
    }

    @Test
    public void testCropWideLandscapeFromLandscapeImage() {

        InputStream imageStream = getLandscapeImage();
        CenteredVariantStrategy strategy = new CenteredVariantStrategy();
        InputStream croppedImageStream = strategy.cropImage(imageStream, "image/jpeg", 1200, 100);

        BufferedImage image = readJpgImage(croppedImageStream);

        Assert.assertNotNull(image);
        Assert.assertEquals(600, image.getWidth());
        Assert.assertEquals(50, image.getHeight());
    }

    @Test
    public void testCropPortraitFromLandscapeImage() {

        InputStream imageStream = getLandscapeImage();
        CenteredVariantStrategy strategy = new CenteredVariantStrategy();
        InputStream croppedImageStream = strategy.cropImage(imageStream, "image/jpeg", 200, 300);

        BufferedImage image = readJpgImage(croppedImageStream);

        Assert.assertNotNull(image);
        Assert.assertEquals(266, image.getWidth());
        Assert.assertEquals(400, image.getHeight());
    }

    @Test
    public void testCropPortraitFromPortraitImage() {

        InputStream imageStream = getPortraitImage();
        CenteredVariantStrategy strategy = new CenteredVariantStrategy();
        InputStream croppedImageStream = strategy.cropImage(imageStream, "image/jpeg", 100, 120);

        BufferedImage image = readJpgImage(croppedImageStream);

        Assert.assertNotNull(image);
        Assert.assertEquals(400, image.getWidth());
        Assert.assertEquals(480, image.getHeight());
    }

    @Test
    public void testCropTallPortraitFromPortraitImage() {

        InputStream imageStream = getPortraitImage();
        CenteredVariantStrategy strategy = new CenteredVariantStrategy();
        InputStream croppedImageStream = strategy.cropImage(imageStream, "image/jpeg", 50, 500);

        BufferedImage image = readJpgImage(croppedImageStream);

        Assert.assertNotNull(image);
        Assert.assertEquals(60, image.getWidth());
        Assert.assertEquals(600, image.getHeight());
    }

    @Test
    public void testCropLandscapeFromPortraitImage() {

        InputStream imageStream = getPortraitImage();
        CenteredVariantStrategy strategy = new CenteredVariantStrategy();
        InputStream croppedImageStream = strategy.cropImage(imageStream, "image/jpeg", 200, 50);

        BufferedImage image = readJpgImage(croppedImageStream);

        Assert.assertNotNull(image);
        Assert.assertEquals(400, image.getWidth());
        Assert.assertEquals(100, image.getHeight());
    }

    private BufferedImage readJpgImage(InputStream croppedImageStream) {
        BufferedImage image = null;
        try {
            ImageReader reader = ImageUtils.getImageReader("image/jpeg");
            MemoryCacheImageInputStream imageInputStream = new MemoryCacheImageInputStream(croppedImageStream);
            reader.setInput(imageInputStream);
            image = reader.read(0);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    private InputStream getLandscapeImage() {
        return getImageStream("/imagejob/landscape-image.jpg");
    }

    private InputStream getPortraitImage() {
        return getImageStream("/imagejob/portrait-image.jpg");
    }

    private InputStream getImageStream(String resource) {
        return this.getClass().getResourceAsStream(resource);
    }
}
