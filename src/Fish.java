import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.Random;

public class Fish {
    private int xCoord;
    private int yCoord;
    private BufferedImage image;
    private static final String[] FISH_IMAGE_PATHS = {
            "src/fishImages/fish1.png",
            "src/fishImages/fish2.png",
            "src/fishImages/fish3.png",
            "src/fishImages/fish4.png",
            "src/fishImages/fish5.png",
            "src/fishImages/fish6.png"
    };
    private static Random rand = new Random();

    public Fish(int x, int y) {
        xCoord = x;
        yCoord = y;
        try {
            String imagePath = FISH_IMAGE_PATHS[rand.nextInt(FISH_IMAGE_PATHS.length)];
            image = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
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

    public boolean contains(Point p) {
        int imageHeight = getImage().getHeight();
        int imageWidth = getImage().getWidth();
        return p.x >= xCoord && p.x <= xCoord + imageWidth &&
                p.y >= yCoord && p.y <= yCoord + imageHeight;
    }

    public Rectangle getBounds() {
        int imageHeight = getImage().getHeight();
        int imageWidth = getImage().getWidth();
        return new Rectangle(xCoord, yCoord, imageWidth, imageHeight);
    }
}
