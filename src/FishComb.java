import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

class FishComb {
    private BufferedImage combImage;
    private BufferedImage[] fishImages;

    public FishComb(String combImagePath, String[] fishImagePaths) {
        try {
            combImage = ImageIO.read(new File(combImagePath));
            fishImages = new BufferedImage[fishImagePaths.length];
            for (int i = 0; i < fishImagePaths.length; i++) {
                fishImages[i] = ImageIO.read(new File(fishImagePaths[i]));
            }
        } catch (IOException e) {
            System.out.println("Error loading images: " + e.getMessage());
        }
    }

    public BufferedImage getCombImage() {
        return combImage;
    }

    public BufferedImage[] getFishImages() {
        return fishImages;
    }

    public boolean matchesFish(BufferedImage fishImage) {
        for (BufferedImage img : fishImages) {
            if (imagesAreEqual(img, fishImage)) {
                return true;
            }
        }
        return false;
    }

    private boolean imagesAreEqual(BufferedImage imgA, BufferedImage imgB) {
        if (imgA.getWidth() != imgB.getWidth() || imgA.getHeight() != imgB.getHeight()) {
            return false;
        }

        for (int y = 0; y < imgA.getHeight(); y++) {
            for (int x = 0; x < imgA.getWidth(); x++) {
                if (imgA.getRGB(x, y) != imgB.getRGB(x, y)) {
                    return false;
                }
            }
        }

        return true;
    }
}
