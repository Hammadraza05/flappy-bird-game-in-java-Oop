import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel implements ActionListener, KeyListener {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int GROUND_HEIGHT = 50;
    private static final int BIRD_SIZE = 30;
    private static final int PIPE_WIDTH = 80;
    private static final int PIPE_GAP = 200;
    private static final int PIPE_SPEED = 4;

    private Timer gameTimer;
    private List<Rectangle> topPipes;
    private List<Rectangle> bottomPipes;
    private int birdY;
    private int birdVelocity;
    private boolean isGameOver;
    private int score;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.CYAN);
        setFocusable(true);
        addKeyListener(this);

        startNewGame();
        gameTimer = new Timer(20, this);
        gameTimer.start();
    }

    private void startNewGame() {
        topPipes = new ArrayList<>();
        bottomPipes = new ArrayList<>();
        birdY = HEIGHT / 2;
        birdVelocity = 0;
        isGameOver = false;
        score = 0;

        // Initialize the first set of pipes
        topPipes.add(generateTopPipe(WIDTH));
        topPipes.add(generateTopPipe(WIDTH + (WIDTH / 2)));
        bottomPipes.add(generateBottomPipe(WIDTH));
        bottomPipes.add(generateBottomPipe(WIDTH + (WIDTH / 2)));
    }

    private Rectangle generateTopPipe(int xPosition) {
        int gapPosition = GROUND_HEIGHT + (int) (Math.random() * (HEIGHT - GROUND_HEIGHT - PIPE_GAP));
        return new Rectangle(xPosition, 0, PIPE_WIDTH, gapPosition);
    }

    private Rectangle generateBottomPipe(int xPosition) {
        int gapPosition = GROUND_HEIGHT + (int) (Math.random() * (HEIGHT - GROUND_HEIGHT - PIPE_GAP));
        return new Rectangle(xPosition, gapPosition + PIPE_GAP, PIPE_WIDTH, HEIGHT - gapPosition - PIPE_GAP - GROUND_HEIGHT);
    }

    private void movePipes() {
        for (Rectangle pipe : topPipes) {
            pipe.setLocation(pipe.x - PIPE_SPEED, pipe.y);

            if (pipe.x + PIPE_WIDTH < 0) {
                pipe.setLocation(WIDTH, 0);
                pipe.height = GROUND_HEIGHT + (int) (Math.random() * (HEIGHT - GROUND_HEIGHT - PIPE_GAP));
            }
        }

        for (Rectangle pipe : bottomPipes) {
            pipe.setLocation(pipe.x - PIPE_SPEED, pipe.y);

            if (pipe.x + PIPE_WIDTH < 0) {
                int gapPosition = GROUND_HEIGHT + (int) (Math.random() * (HEIGHT - GROUND_HEIGHT - PIPE_GAP));
                pipe.setLocation(WIDTH, gapPosition + PIPE_GAP);
                pipe.height = HEIGHT - gapPosition - PIPE_GAP - GROUND_HEIGHT;
            }
        }
    }

    private void moveBird() {
        birdVelocity += 1; // Gravity effect
        birdY += birdVelocity;

        if (birdY > HEIGHT - GROUND_HEIGHT - BIRD_SIZE) {
            birdY = HEIGHT - GROUND_HEIGHT - BIRD_SIZE;
            triggerGameOver();
        }
    }

    private void checkCollisions() {
        Rectangle birdRect = new Rectangle(WIDTH / 2, birdY, BIRD_SIZE, BIRD_SIZE);

        for (Rectangle pipe : topPipes) {
            if (birdRect.intersects(pipe)) {
                triggerGameOver();
                return;
            }
        }

        for (Rectangle pipe : bottomPipes) {
            if (birdRect.intersects(pipe)) {
                triggerGameOver();
                return;
            }
        }

        if (birdY <= 0) {
            birdY = 0;
            triggerGameOver();
        }
    }

    private void triggerGameOver() {
        isGameOver = true;
        gameTimer.stop();

        // Display game over message and score
        JOptionPane.showMessageDialog(this, "Game Over! Your Score: " + score, "Game Over", JOptionPane.PLAIN_MESSAGE);

        // Ask if the player wants to restart
        int choice = JOptionPane.showConfirmDialog(this, "Restart Game?", "Restart", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            startNewGame();
            isGameOver = false;
            gameTimer.start();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!isGameOver) {
            movePipes();
            moveBird();
            checkCollisions();
            score++;
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw background
        g.setColor(Color.CYAN);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // Draw bird
        g.setColor(Color.RED);
        g.fillRect(WIDTH / 2, birdY, BIRD_SIZE, BIRD_SIZE);

        // Draw bird face
        g.setColor(Color.BLACK);
        g.fillOval(WIDTH / 2 + 5, birdY + 5, 10, 10); // Left eye
        g.fillOval(WIDTH / 2 + 15, birdY + 5, 10, 10); // Right eye
        g.drawArc(WIDTH / 2 + 7, birdY + 15, 15, 10, 0, -180); // Smile

        // Draw top pipes
        g.setColor(Color.GREEN);
        for (Rectangle pipe : topPipes) {
            g.fillRect(pipe.x, pipe.y, PIPE_WIDTH, pipe.height);
        }

        // Draw bottom pipes
        for (Rectangle pipe : bottomPipes) {
            g.fillRect(pipe.x, pipe.y, PIPE_WIDTH, pipe.height);
        }

        // Draw ground
        g.setColor(Color.ORANGE);
        g.fillRect(0, HEIGHT - GROUND_HEIGHT, WIDTH, GROUND_HEIGHT);

        // Draw score
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + score, 20, 30);

        // Draw game over message
        if (isGameOver) {
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("Game Over", WIDTH / 2 - 120, HEIGHT / 2 - 20);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE && !isGameOver) {
            birdVelocity = -12; // Bird flap
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // No action needed on key release
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // No action needed on key type
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Flappy Bird");
        GamePanel gamePanel = new GamePanel();
        frame.add(gamePanel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
  