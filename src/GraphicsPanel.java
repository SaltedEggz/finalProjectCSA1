import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class GraphicsPanel extends JPanel implements KeyListener, MouseListener, ActionListener, MouseMotionListener {
    private BufferedImage background;
    private BufferedImage belt;
    private BufferedImage cat1Image;
    private BufferedImage cat2Image;
    private BufferedImage cat3Image;

    private BufferedImage fishComb1;

    private boolean[] pressedKeys;
    private ArrayList<Coin> coins;
    private ArrayList<Cat> cats;

    private Coin draggedCoin;
    private int dragOffsetX;
    private int dragOffsetY;

    private boolean isPaused;
    private boolean gameOver;
    private boolean gameWon;

    private JButton clearCoins;
    private JButton pause;

    private boolean isDragging; // Flag to track if a coin is being dragged
    private int score;

    public GraphicsPanel() {
        try {
            background = ImageIO.read(new File("src/background.png"));
            belt = ImageIO.read(new File("src/belt.png"));
            cat1Image = ImageIO.read(new File("src/catImages/cat1.png"));
            cat2Image = ImageIO.read(new File("src/catImages/cat2.png"));
            cat3Image = ImageIO.read(new File("src/catImages/cat3.png"));
            fishComb1 = ImageIO.read(new File("src/fishComb/fishComb3.png"));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        coins = new ArrayList<>();
        cats = new ArrayList<>();
        pressedKeys = new boolean[128];

        cats.add(new Cat(20, 400, cat1Image));
        cats.add(new Cat(720, 400, cat2Image));
        cats.add(new Cat(1390, 400, cat3Image));

        clearCoins = new JButton("Clear");
        clearCoins.setFocusable(false);
        add(clearCoins);
        clearCoins.addActionListener(this);

        pause = new JButton("Pause");
        pause.setFocusable(false);
        add(pause);
        pause.addActionListener(this);

        addKeyListener(this);
        addMouseListener(this);
        setFocusable(true); // this line of code + one below makes this panel active for keylistener events
        addMouseMotionListener(this);
        requestFocusInWindow(); // see comment above
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);  // just do this
        g.drawImage(background, 0, 0, null);
        g.drawImage(belt, 540, 810, null);
        g.drawImage(belt, -10, 810, null);
        g.drawImage(belt, 1672, 810, null);
        g.drawImage(fishComb1,200, 280, null );

        // Draw cats
        for (Cat cat : cats) {
            g.drawImage(cat.getImage(), cat.getxCoord(), cat.getyCoord(), null);
        }

        // Draw coins
        for (Coin coin : coins) {
            g.drawImage(coin.getImage(), coin.getxCoord(), coin.getyCoord(), null);
        }

        // Draw score
        g.setFont(new Font("Courier New", Font.BOLD, 36));
        g.setColor(Color.WHITE);
        g.drawString("Score: " + score, 20, 50);

        clearCoins.setLocation(20, 80);
        pause.setLocation(20, 110);

        if (isPaused) {
            g.setFont(new Font("Courier New", Font.BOLD, 48));
            g.setColor(Color.RED);
            if (gameOver) {
                g.drawString("GAME OVER", getWidth() / 2 - 150, getHeight() / 2);
            } else if (gameWon) {
                g.drawString("YOU WIN!", getWidth() / 2 - 150, getHeight() / 2);
            } else {
                g.drawString("PAUSED", getWidth() / 2 - 100, getHeight() / 2);
            }
        }
    }

    // ----- KeyListener interface methods -----
    public void keyTyped(KeyEvent e) {
    } // unimplemented

    public void keyPressed(KeyEvent e) {
        // see this for all keycodes: https://stackoverflow.com/questions/15313469/java-keyboard-keycodes-list
        // A = 65, D = 68, S = 83, W = 87, left = 37, up = 38, right = 39, down = 40, space = 32, enter = 10
        int key = e.getKeyCode();
        pressedKeys[key] = true;
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        pressedKeys[key] = false;
    }

    // ----- MouseMotionListener interface methods -----
    public void mouseDragged(MouseEvent e) {
        if (isPaused || draggedCoin == null) {
            return;
        }
        // Update the position of the dragged coin
        draggedCoin.setxCoord(e.getX() - dragOffsetX);
        draggedCoin.setyCoord(e.getY() - dragOffsetY);
        checkCollisions(); // Check for collisions during dragging
        repaint();
    }

    public void mouseMoved(MouseEvent e) {
    }

    // ----- MouseListener interface methods -----

    public void mousePressed(MouseEvent e) {
        if (isPaused) {
            return;
        }
        // Check if the mouse press is on a coin
        for (Coin coin : coins) {
            if (coin.contains(e.getPoint())) {
                isDragging = true;
                draggedCoin = coin;
                dragOffsetX = e.getX() - coin.getxCoord();
                dragOffsetY = e.getY() - coin.getyCoord();
                break;
            }
        }
    }

    public void mouseClicked(MouseEvent e) {
    }  // unimplemented; if you move your mouse while clicking,
    // this method isn't called, so mouseReleased is best

    public void mouseReleased(MouseEvent e) {
        isDragging = false;
        draggedCoin = null;
        if (isPaused) {
            return; // If paused, clicks shouldn't do anything
        }
        if (e.getButton() == MouseEvent.BUTTON1) {  // left mouse click
            Point mouseClickLocation = e.getPoint();
            int clickX = mouseClickLocation.x;
            int clickY = mouseClickLocation.y;

            // Define the boundaries
            int boundaryX = 0;
            int boundaryY = 780;
            int boundaryWidth = 1980;
            int boundaryHeight = 175;

            // Check if the click is within the boundaries
            if (clickX >= boundaryX && clickX <= (boundaryX + boundaryWidth) && clickY >= boundaryY && clickY <= (boundaryY + boundaryHeight)) {
                Random random = new Random();
                if (random.nextInt(4) < 3) { // 75% chance
                    Coin coin = new Coin(clickX, clickY);
                    coins.add(coin);
                }
            }
        }
    }

    public void mouseEntered(MouseEvent e) {
    } // unimplemented

    public void mouseExited(MouseEvent e) {
    } // unimplemented

    // ACTIONLISTENER INTERFACE METHODS: used for buttons AND timers!
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == clearCoins) {
            coins.clear();
            gameOver = false;
            gameWon = false;
            isPaused = false;
            score = 0;
            repaint();
        } else if (e.getSource() == pause) {
            isPaused = !isPaused;
            repaint();
        }
    }

    // Check for collisions between coins and cats
    private void checkCollisions() {
        ArrayList<Coin> coinsToRemove = new ArrayList<>();
        for (Coin coin : coins) {
            for (Cat cat : cats) {
                if (cat.getBounds().intersects(coin.coinRect())) {
                    coinsToRemove.add(coin);
                    score++;
                }
            }
        }
        coins.removeAll(coinsToRemove);
    }
}
