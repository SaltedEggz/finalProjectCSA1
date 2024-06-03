import java.awt.*;
import java.awt.image.BufferedImage;

public class Cat {
    private int xCoord;
    private int yCoord;
    private BufferedImage image;

    public Cat(int x, int y, BufferedImage img) {
        xCoord = x;
        yCoord = y;
        image = img;
    }

    public int getxCoord() {
        return xCoord;
    }

    public void setxCoord(int xCoord) {
        this.xCoord = xCoord;
    }

    public int getyCoord() {
        return yCoord;
    }

    public void setyCoord(int yCoord) {
        this.yCoord = yCoord;
    }

    public BufferedImage getImage() {
        return image;
    }

    public Rectangle getBounds() {
        int imageWidth = image.getWidth() - 20; // 10 pixels smaller on each side
        int imageHeight = image.getHeight() - 20; // 10 pixels smaller on each side
        return new Rectangle(xCoord + 10, yCoord + 10, imageWidth, imageHeight);
    }
}
