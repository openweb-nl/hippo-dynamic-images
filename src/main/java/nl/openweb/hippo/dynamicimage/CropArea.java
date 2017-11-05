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
