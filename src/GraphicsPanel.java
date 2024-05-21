import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class GraphicsPanel extends JPanel implements KeyListener, MouseListener, ActionListener {
    private BufferedImage background;
    private BufferedImage background2;
    private Player player;
    private Player updPlayer;

    private Player tempPlayer;
    private boolean[] pressedKeys;
    private ArrayList<Coin> coins;
    private ArrayList<spikedBall> spikedBalls;
    private Timer timer;
    private int time;
    private boolean level2;
    private boolean isPaused;
    private boolean gameOver;
    private boolean gameWon;

    private JButton clearCoins;
    private JButton pause;

    public GraphicsPanel() {
        try {
            background = ImageIO.read(new File("src/background.png"));
            background2 = ImageIO.read(new File("src/background2.png"));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        player = new Player("src/belt.png", "src/catImages/cat2.png", null);
        updPlayer = new Player("src/mariofrogleft.png", "src/mariofrogright.png", null);
        tempPlayer = player;
        coins = new ArrayList<>();
        spikedBalls = new ArrayList<>();
        pressedKeys = new boolean[128];
        time = 0;
        timer = new Timer(1000, this); // this Timer will call the actionPerformed interface method every 1000ms = 1 second
        timer.start();

        clearCoins = new JButton("Reset");
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
        requestFocusInWindow(); // see comment above
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);  // just do this
        if (level2) {
            g.drawImage(background2, 0, 0, null);
        } else {
            g.drawImage(background, 0, 0, null);
        }// the order that things get "painted" matter; we put background down first
        g.drawImage(player.getPlayerImage(), player.getxCoord(), player.getyCoord(), null);

        // this loop does two things:  it draws each Coin that gets placed with mouse clicks,
        // and it also checks if the player has "intersected" (collided with) the Coin, and if so,
        // the score goes up and the Coin is removed from the arraylist
        for (int i = 0; i < coins.size(); i++) {
            Coin coin = coins.get(i);
            g.drawImage(coin.getImage(), coin.getxCoord(), coin.getyCoord(), null); // draw Coin
            if (player.playerRect().intersects(coin.coinRect())) { // check for collision
                player.collectCoin();
                coins.remove(i);
                i--;

                if (player.getScore() >= 10 && !level2) {
                    level2 = true;
                    // Preserve the player's score
                    updPlayer.setScore(player.getScore());
                    player = updPlayer;
                }

                if (player.getScore() >= 20) { // Winning condition
                    isPaused = true;
                    gameWon = true;
                    repaint();
                }
            }
        }

        for (spikedBall spikyBall : spikedBalls) {
            g.drawImage(spikyBall.getImage(), spikyBall.getxCoord(), spikyBall.getyCoord(), null); // draw SpikyBall
            if (player.playerRect().intersects(spikyBall.spikedBallRect())) { // check for collision
                isPaused = true;
                gameOver = true;
                repaint();
                break;
            }
        }

        // draw score
        g.setFont(new Font("Courier New", Font.BOLD, 24));
        g.drawString(player.getName() + "'s Score: " + player.getScore(), 20, 40);
        g.drawString("Time: " + time, 20, 70);
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

        if (!isPaused) {
            // player moves left (A)
            if (pressedKeys[65]) {
                player.faceLeft();
                player.moveLeft();
            }

            // player moves right (D)
            if (pressedKeys[68]) {
                player.faceRight();
                player.moveRight();
            }

            // player moves up (W)
            if (pressedKeys[87]) {
                player.moveUp();
            }

            // player moves down (S)
            if (pressedKeys[83]) {
                player.moveDown();
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

    // ----- MouseListener interface methods -----
    public void mouseClicked(MouseEvent e) {
    }  // unimplemented; if you move your mouse while clicking,
    // this method isn't called, so mouseReleased is best

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
        if (!isPaused) {
            if (e.getButton() == MouseEvent.BUTTON1) {  // left mouse click
                Point mouseClickLocation = e.getPoint();
                Random random = new Random();
                if (random.nextInt(4) < 3) { // 75% chance
                    Coin coin = new Coin(mouseClickLocation.x, mouseClickLocation.y);
                    coins.add(coin);
                } else { // 25% chance
                    spikedBall spikyBall = new spikedBall(mouseClickLocation.x, mouseClickLocation.y);
                    spikedBalls.add(spikyBall);
                }
            } else {
                Point mouseClickLocation = e.getPoint();
                if (player.playerRect().contains(mouseClickLocation)) {
                    player.turn();
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
        if (e.getSource() instanceof Timer) {
            time++;
        } else if (e.getSource() == clearCoins) {
            player = tempPlayer;
            level2 = false;
            player.resetScore();
            player.resetPosition();
            coins.clear();
            spikedBalls.clear();
            gameOver = false;
            gameWon = false;
            isPaused = false;
            repaint();
        } else if (e.getSource() == pause) {
            isPaused = !isPaused;
            if (isPaused) {
                timer.stop();
            } else {
                timer.start();
            }
            repaint();
        }
    }
}
