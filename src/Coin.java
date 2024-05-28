import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Coin {
    private int xCoord;
    private int yCoord;
    private BufferedImage image;
    private BufferedImage image2;
    private BufferedImage image3;
    private BufferedImage image4;
    private BufferedImage image5;
    private BufferedImage image6;
    Random rand = new Random();
    int randomNum = rand.nextInt(6) + 1;

    public Coin(int x, int y) {
        xCoord = x;
        yCoord = y;
        try {
            image = ImageIO.read(new File("src/fishImages/fish1.png"));
            image2 = ImageIO.read(new File("src/fishImages/fish2.png"));
            image3 = ImageIO.read(new File("src/fishImages/fish3.png"));
            image4 = ImageIO.read(new File("src/fishImages/fish4.png"));
            image5 = ImageIO.read(new File("src/fishImages/fish5.png"));
            image6 = ImageIO.read(new File("src/fishImages/fish6.png"));

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
        if (randomNum == 1){
            return image;
        }
        if (randomNum == 2){
            return image2;
        }
        if (randomNum == 3){
            return image3;
        }
        if (randomNum == 4){
            return image4;
        }
        if (randomNum == 5){
            return image5;
        }
        return image6;
    }

    public boolean contains(Point p) {
        int imageHeight = getImage().getHeight();
        int imageWidth = getImage().getWidth();
        return p.x >= xCoord && p.x <= xCoord + imageWidth &&
                p.y >= yCoord && p.y <= yCoord + imageHeight;
    }

    // we use a "bounding Rectangle" for detecting collision
    public Rectangle coinRect() {
        int imageHeight = getImage().getHeight();
        int imageWidth = getImage().getWidth();
        Rectangle rect = new Rectangle(xCoord, yCoord, imageWidth, imageHeight);
        return rect;
    }
}
