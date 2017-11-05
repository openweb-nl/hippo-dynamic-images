package nl.openweb.hippo.dynamicimage;

/**
 * Utility class for calculating the optimal crop area within a specific image dimension.
 *
 * @author Rob Haring
 */
public class CropArea {

    private int width;
    private int height;
    private int x;
    private int y;

    /**
     * calculated optimal crop area
     * @param sourceWidth
     * @param sourceHeight
     * @param targetWidth
     * @param targetHeight
     */
    public CropArea(final int sourceWidth, final int sourceHeight, final int targetWidth, final int targetHeight) {

        if (targetWidth > targetHeight) {
            // target image is in landscape mode
            setLandcapeCropArea(sourceWidth, sourceHeight, targetWidth, targetHeight);
        } else {
            // target image is in portrait mode
            setPortraitCropArea(sourceWidth, sourceHeight, targetWidth, targetHeight);
        }

        // Make sure that the cropping area does not exceed the source image
        // boundaries.
        // If so... reverse the cropping area.
        if (this.width > sourceWidth) {
            setLandcapeCropArea(sourceWidth, sourceHeight, targetWidth, targetHeight);
        } else if (this.height > sourceHeight) {
            setPortraitCropArea(sourceWidth, sourceHeight, targetWidth, targetHeight);
        }
    }

    private void setPortraitCropArea(final int sourceWidth, final int sourceHeight, final int targetWidth,
            final int targetHeight) {

        float scaleFactor = (float) sourceHeight / (float) targetHeight;

        this.x = (int) (((float) sourceWidth / 2) - (((float) targetWidth / 2) * scaleFactor));
        this.y = 0;
        this.width = (int) (targetWidth * scaleFactor);
        this.height = sourceHeight;
    }

    private void setLandcapeCropArea(final int sourceWidth, final int sourceHeight, final int targetWidth,
            final int targetHeight) {

        float scaleFactor = (float) sourceWidth / (float) targetWidth;

        this.x = 0;
        this.y = (int) (((float) sourceHeight / 2) - (((float) targetHeight / 2) * scaleFactor));
        this.width = sourceWidth;
        this.height = (int) (targetHeight * scaleFactor);
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

}
