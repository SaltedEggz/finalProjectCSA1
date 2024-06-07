import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;



public class GraphicsPanel extends JPanel implements KeyListener, MouseListener, ActionListener, MouseMotionListener, Runnable {
    private BufferedImage background;
    private BufferedImage belt;
    private BufferedImage cat1Image;
    private BufferedImage cat2Image;
    private BufferedImage cat3Image;

    private FishComb[] fishCombs;

    private boolean[] pressedKeys;
    private ArrayList<Fish> fish;
    private ArrayList<Cat> cats;

    private Fish draggedFish;
    private int dragOffsetX;
    private int dragOffsetY;


    private boolean gameOver;
    private boolean gameWon;

    private JButton clearCoins;

    private boolean isDragging;
    private int score;

    private int beltOffsetX; //x-coordinate of the conveyor belt
    private int beltSpeed = 2; //speed of the conveyor belt

    private Clip songClip;
    private boolean gameOverMusicPlayed = false;


    public GraphicsPanel() {
        playBackground();
        try {
            background = ImageIO.read(new File("src/background.png"));
            belt = ImageIO.read(new File("src/belt.png"));
            cat1Image = ImageIO.read(new File("src/catImages/cat1.png"));
            cat2Image = ImageIO.read(new File("src/catImages/cat2.png"));
            cat3Image = ImageIO.read(new File("src/catImages/cat3.png"));

            // fish combinations
            fishCombs = new FishComb[]{
                    new FishComb("src/fishComb/fishComb1.png", new String[]{"src/fishImages/fish1.png", "src/fishImages/fish2.png"}),
                    new FishComb("src/fishComb/fishComb2.png", new String[]{"src/fishImages/fish3.png", "src/fishImages/fish4.png"}),
                    new FishComb("src/fishComb/fishComb3.png", new String[]{"src/fishImages/fish5.png", "src/fishImages/fish6.png"}),
                    new FishComb("src/fishComb/fishComb4.png", new String[]{"src/fishImages/fish1.png", "src/fishImages/fish3.png"}),
                    new FishComb("src/fishComb/fishComb5.png", new String[]{"src/fishImages/fish2.png", "src/fishImages/fish4.png"}),
                    new FishComb("src/fishComb/fishComb6.png", new String[]{"src/fishImages/fish5.png", "src/fishImages/fish1.png"})
            };
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        fish = new ArrayList<>();
        cats = new ArrayList<>();
        pressedKeys = new boolean[128];

        // Initialize cats with random fish combinations
        cats.add(new Cat(20, 350, cat1Image, getRandomFishComb()));
        cats.add(new Cat(800, 350, cat2Image, getRandomFishComb()));
        cats.add(new Cat(1600, 350, cat3Image, getRandomFishComb()));

        clearCoins = new JButton("Clear");
        clearCoins.setFocusable(false);
        add(clearCoins);
        clearCoins.addActionListener(this);


        addKeyListener(this);
        addMouseListener(this);
        setFocusable(true);
        addMouseMotionListener(this);
        requestFocusInWindow();

        beltOffsetX = 0; // Initialize the belt offset
        new Thread(this).start(); // Start the thread for continuous belt movement
        Thread animationThread = new Thread(this);
        animationThread.start();
    }

    private FishComb getRandomFishComb() {
        Random random = new Random();
        return fishCombs[random.nextInt(fishCombs.length)];
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(background, 0, 0, null);


        // Draw the conveyor belt with the current offset
        g.drawImage(belt, beltOffsetX, 810, null);
        g.drawImage(belt, beltOffsetX + belt.getWidth(), 810, null);
        g.drawImage(belt, beltOffsetX + 2 * belt.getWidth(), 810, null);


        // Draw cats and their fishComb
        for (Cat cat : cats) {
            g.drawImage(cat.getImage(), cat.getxCoord(), cat.getyCoord(), null);
            g.drawImage(cat.getFishComb().getCombImage(), cat.getxCoord(), cat.getyCoord() - 100, null);
        }

        // Draw fish
        if (fish != null) {
            for (Fish fish : this.fish) {
                g.drawImage(fish.getImage(), fish.getxCoord(), fish.getyCoord(), null);
            }
        }

        // Draw score
        g.setFont(new Font("Courier New", Font.BOLD, 36));
        g.setColor(Color.WHITE);
        g.drawString("Score: " + score, 20, 50);

        clearCoins.setLocation(20, 80);

        g.setFont(new Font("SansSerif", Font.BOLD, 180));
        g.setColor(Color.RED);
        if (gameOver) {
            g.drawString("GAME OVER", getWidth() / 2 - 590, getHeight() / 2);
            g.setFont(new Font("SansSerif", Font.BOLD, 182));
            g.setColor(Color.BLACK);
            g.drawString("GAME OVER", getWidth() / 2 - 590, getHeight() / 2);
        } else if (gameWon) {
            g.drawString("YOU WIN!", getWidth() / 2 - 150, getHeight() / 2);
        }
    }


    //sound


    private void playBackground() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("src/sounds/background.wav").getAbsoluteFile());
            songClip = AudioSystem.getClip();
            songClip.open(audioInputStream);
            songClip.loop(Clip.LOOP_CONTINUOUSLY);  // song repeats when finished
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void startBackgroundMusic() {
        if (!songClip.isRunning()) {
            songClip.start();
        }
    }

    private void playGameOver() {
        if (!gameOverMusicPlayed) {
            // Stop background music if it's playing
            if (songClip != null && songClip.isRunning()) {
                songClip.stop();
            }

            try {
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("src/sounds/gameOver.wav").getAbsoluteFile());
                songClip = AudioSystem.getClip();
                songClip.open(audioInputStream);
                songClip.start();
                gameOverMusicPlayed = true; // Set the flag to true indicating that the music has been played
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void playSplash() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("src/sounds/splash1.wav").getAbsoluteFile());
            songClip = AudioSystem.getClip();
            songClip.open(audioInputStream);
            songClip.start();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void playMeow() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("src/sounds/Meow.wav").getAbsoluteFile());
            songClip = AudioSystem.getClip();
            songClip.open(audioInputStream);
            songClip.start();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void playGrowl() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("src/sounds/growl.wav").getAbsoluteFile());
            songClip = AudioSystem.getClip();
            songClip.open(audioInputStream);
            songClip.start();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    // Implementing the run method for continuous belt movement
    public void run() {
        while (true) {
            try {
                Thread.sleep(20); // Adjust the delay for animation
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            beltOffsetX -= beltSpeed; // Move the belt to the left
            if (beltOffsetX <= -belt.getWidth()) {
                beltOffsetX = 0; // Reset the belt offset when it reaches the end
            }

            // Update the position of clicked spawned coins with the belt movement
            for (Fish fish : this.fish) {
                fish.setxCoord(fish.getxCoord() - beltSpeed);
                // Remove coins that go beyond the screen width
                if (fish.getxCoord() + fish.getImage().getWidth() < 0) {
                    this.fish.remove(fish);
                    break;
                }
            }

            repaint(); // Repaint the panel to update the belt position and coin positions
        }
    }

    // KeyListener interface methods
    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        pressedKeys[key] = true;
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        pressedKeys[key] = false;
    }

    // MouseMotionListener interface methods
    public void mouseDragged(MouseEvent e) {
        if (!gameOver) {
            draggedFish.setxCoord(e.getX() - dragOffsetX);
            draggedFish.setyCoord(e.getY() - dragOffsetY);
            checkCollisions();
            repaint();
        }
    }

    public void mouseMoved(MouseEvent e) {
    }

    // MouseListener interface methods
    public void mousePressed(MouseEvent e) {
        if (!gameOver) {
            for (Fish fish : this.fish) {
                if (fish.contains(e.getPoint())) {
                    isDragging = true;
                    draggedFish = fish;
                    dragOffsetX = e.getX() - fish.getxCoord();
                    dragOffsetY = e.getY() - fish.getyCoord();
                    break;
                }
            }
        }
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
        if (!gameOver) {
            isDragging = false;
            draggedFish = null;
            if (e.getButton() == MouseEvent.BUTTON1) {
                Point mouseClickLocation = e.getPoint();
                int clickX = mouseClickLocation.x;
                int clickY = mouseClickLocation.y;

                int boundaryX = 0;
                int boundaryY = 780;
                int boundaryWidth = 1980;
                int boundaryHeight = 175;

                if (clickX >= boundaryX && clickX <= (boundaryX + boundaryWidth) && clickY >= boundaryY && clickY <= (boundaryY + boundaryHeight)) {
                    Random random = new Random();
                    if (random.nextInt(4) < 3) {
                        Fish fish = new Fish(clickX, clickY);
                        this.fish.add(fish);
                        playSplash();
                    }
                }
            }
        }
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            if (e.getSource() == clearCoins) {
                fish.clear();
                repaint();
            }
        }
    }

    private void checkCollisions() {
        ArrayList<Fish> coinsToRemove = new ArrayList<>();
        for (Cat cat : cats) {
            Rectangle catBounds = cat.getBounds();
            for (Fish fish : this.fish) {
                if (catBounds.intersects(fish.getBounds())) {
                    if (cat.getFishComb().matchesFish(fish.getImage())) {
                        score += 10;
                        playMeow();
                        coinsToRemove.add(fish);
                        cat.setFishComb(getRandomFishComb());
                        break;
                    } else {
                        playGrowl();
                        gameOver = true;
                        if (songClip != null && songClip.isRunning()) {
                            songClip.stop();
                        }
                        playGameOver();
                        return; // Exit the method after playing the game over music
                    }
                }
            }
        }
        fish.removeAll(coinsToRemove);
    }
}