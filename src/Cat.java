import java.awt.*;
import java.awt.image.BufferedImage;

class Cat {
    private int xCoord;
    private int yCoord;
    private BufferedImage image;
    private FishComb fishComb;

    public Cat(int xCoord, int yCoord, BufferedImage image, FishComb fishComb) {
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.image = image;
        this.fishComb = fishComb;
    }

    public int getxCoord() {
        return xCoord;
    }

    public int getyCoord() {
        return yCoord;
    }

    public BufferedImage getImage() {
        return image;
    }

    public FishComb getFishComb() {
        return fishComb;
    }

    public void setFishComb(FishComb fishComb) {
        this.fishComb = fishComb;
    }

    public Rectangle getBounds() {
        return new Rectangle(xCoord, yCoord, image.getWidth(), image.getHeight());
    }
}
