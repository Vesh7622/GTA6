package engine;

import ai.AIController;
import ai.AdvancedAIController;
import ai.BasicAIController;
import ai.SmartAIController;
import gamejava.SoundPlayer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import model.Obstacle;
import model.Player;
import utils.Constants;

public class GamePanel extends JPanel implements ActionListener {

    private Timer timer;
    private Player player;
    private List<Obstacle> obstacles;
    private AIController aiController;
    private long startTime;
    private boolean gameOver;
    private boolean gameWin;
    private long winDisplayedTime;
    private int nextObstacleDistance;
    private Random random;
    private boolean isPlayerControlled;
    private Image backgroundImage;
    private Image backgroundImage2; // new background image
    private Image selectedBackground; // the one chosen randomly
    private Image grasssImage;
    private Clip backgroundClip;
    private boolean soundEnabled;  // New field for sound toggle
    private String selectedAIType; // Save AI type for restarts


    private void selectRandomBackground() {
        selectedBackground = random.nextBoolean() ? backgroundImage : backgroundImage2;
    }    

    // Modified constructor to accept soundEnabled parameter
    public GamePanel(boolean isPlayerControlled, boolean soundEnabled, String selectedAIType) {
        this.isPlayerControlled = isPlayerControlled;
        this.soundEnabled = soundEnabled;
        this.selectedAIType = selectedAIType; // ✅ Save for later
        
        setPreferredSize(new Dimension(Constants.FRAME_WIDTH, Constants.FRAME_HEIGHT));
        setFocusable(true);
    
        random = new Random();  // <-- MOVE THIS HERE, before using it
    
    // Load images
    try {
     backgroundImage = ImageIO.read(new File("C:\\Users\\lomes\\OneDrive\\Desktop\\gamejava\\assets\\background.png"));
     backgroundImage2 = ImageIO.read(new File("C:\\Users\\lomes\\OneDrive\\Desktop\\gamejava\\assets\\background2.png"));
        grasssImage = ImageIO.read(new File("C:\\Users\\lomes\\OneDrive\\Desktop\\gamejava\\assets\\grasss.png"));
    
      selectRandomBackground();  // ← Randomly pick background
    } catch (IOException e) {
     e.printStackTrace();
    }

    
        // rest of your constructor...  
        
        // Only load and play background music if sound is enabled
        if (soundEnabled) {
            playBackgroundMusic();
        }

        player = new Player(soundEnabled); // ✅ new
        obstacles = new ArrayList<>();
        aiController = createAIController(selectedAIType);
        timer = new Timer(20, this);
        timer.start();

        startTime = System.currentTimeMillis();
        gameOver = false;
        gameWin = false;
        random = new Random();
        nextObstacleDistance = Constants.OBSTACLE_MIN_GAP 
                + random.nextInt(Constants.OBSTACLE_MAX_GAP - Constants.OBSTACLE_MIN_GAP);

        if (isPlayerControlled) {
            addKeyListener(keyAdapter);
            requestFocusInWindow();
        }
    }

        private AIController createAIController(String type) {
        switch (type) {
            case "Smart":
                return new SmartAIController();
            case "Advanced":
                return new AdvancedAIController();
            case "Basic":
            default:
                return new BasicAIController();
        }
    }

    private final KeyAdapter keyAdapter = new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                player.jump();
            }
        }
    };

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameOver) {
            stopBackgroundMusic();
            timer.stop();
            showRestartDialog("Game Over! Do you want to restart?");
            return;
        }

        if (gameWin) {
            long now = System.currentTimeMillis();
            if (now - winDisplayedTime >= 5000) {
                stopBackgroundMusic();
                timer.stop();
                showRestartDialog("You Win! Do you want to play again?");
            }
            repaint();
            return;
        }

        player.update();

        Iterator<Obstacle> it = obstacles.iterator();
        while (it.hasNext()) {
            Obstacle obs = it.next();
            obs.update();
            if (obs.isOffScreen()) {
                it.remove();
            }
            if (obs.getBounds().intersects(player.getBounds())) {
                gameOver = true;
                if (soundEnabled) {
                    SoundPlayer.playSound("lose.wav");
                }
                stopBackgroundMusic();
                timer.stop();
                showRestartDialog("Game Over! Do you want to restart?");
                return;
            }
        }

        if (!isPlayerControlled) {
            aiController.update(player, obstacles);
        }

        int distanceCovered = (int)((System.currentTimeMillis() - startTime) / 5);
        if (distanceCovered >= nextObstacleDistance) {
            obstacles.add(new Obstacle(Constants.FRAME_WIDTH));
            nextObstacleDistance += Constants.OBSTACLE_MIN_GAP 
                    + random.nextInt(Constants.OBSTACLE_MAX_GAP - Constants.OBSTACLE_MIN_GAP);
        }

        long winTime = isPlayerControlled ? 75000 : 30000;
        if (System.currentTimeMillis() - startTime >= winTime && !gameWin) {
            gameWin = true;
            if (soundEnabled) {
                SoundPlayer.playSound("win.wav");
            }
            stopBackgroundMusic();
            winDisplayedTime = System.currentTimeMillis();
        }

        repaint();
    }

    private void playBackgroundMusic() {
        if (!soundEnabled) return;  // Don't play if sound is disabled
        
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File("C:\\Users\\lomes\\OneDrive\\Desktop\\gamejava\\assets\\song.wav"));
            backgroundClip = AudioSystem.getClip();
            backgroundClip.open(audioStream);
            backgroundClip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void stopBackgroundMusic() {
        if (backgroundClip != null && backgroundClip.isRunning()) {
            backgroundClip.stop();
            backgroundClip.close();
        }
    }
    
    private void showRestartDialog(String message) {
        int option = JOptionPane.showConfirmDialog(
            this,
            message,
            "Restart Game",
            JOptionPane.YES_NO_OPTION
        );
    
        if (option == JOptionPane.YES_OPTION) {
            restartGame();
        } else {
            javax.swing.SwingUtilities.getWindowAncestor(this).dispose();
    
            // ✅ Start looping menu music again
            Clip menuClip = soundEnabled ? SoundPlayer.playLoopingSound("menu.wav") : null;
    
            javax.swing.SwingUtilities.invokeLater(() -> {
                Image backgroundImage = null;
                try {
                    backgroundImage = ImageIO.read(new File("C:\\Users\\lomes\\OneDrive\\Desktop\\gamejava\\assets\\pbackground.png"));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
    
                Image finalBackgroundImage = backgroundImage;
    
                // Panel with background image
                javax.swing.JPanel imagePanel = new javax.swing.JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        if (finalBackgroundImage != null) {
                            g.drawImage(finalBackgroundImage, 0, 0, getWidth(), getHeight(), this);
                        }
                    }
                };
                imagePanel.setLayout(new java.awt.GridLayout(0, 1));
                imagePanel.setOpaque(false);
    
                // UI components
                javax.swing.JLabel modeLabel = new javax.swing.JLabel("Choose game mode:");
                modeLabel.setOpaque(false);
                modeLabel.setForeground(Color.WHITE);
    
                javax.swing.ButtonGroup modeGroup = new javax.swing.ButtonGroup();
                javax.swing.JRadioButton playerButton = new javax.swing.JRadioButton("Player", true);
                playerButton.setOpaque(false);
                playerButton.setForeground(Color.WHITE);
                javax.swing.JRadioButton aiButton = new javax.swing.JRadioButton("AI");
                aiButton.setOpaque(false);
                aiButton.setForeground(Color.WHITE);
                modeGroup.add(playerButton);
                modeGroup.add(aiButton);
    
                javax.swing.JCheckBox soundToggle = new javax.swing.JCheckBox("Enable Sound", soundEnabled);
                soundToggle.setOpaque(false);
                soundToggle.setForeground(Color.WHITE);

            // ✅ AI Difficulty Dropdown
            javax.swing.JLabel difficultyLabel = new javax.swing.JLabel("Select AI Difficulty:");
            difficultyLabel.setOpaque(false);
            difficultyLabel.setForeground(Color.WHITE);
            difficultyLabel.setVisible(false);

            String[] difficulties = {"Basic", "Smart", "Advanced"};
            javax.swing.JComboBox<String> difficultySelector = new javax.swing.JComboBox<>(difficulties);
            difficultySelector.setVisible(false);

            aiButton.addActionListener(e -> {
                difficultyLabel.setVisible(true);
                difficultySelector.setVisible(true);
            });
            playerButton.addActionListener(e -> {
                difficultyLabel.setVisible(false);
                difficultySelector.setVisible(false);
            });

            final Clip[] menuClipRef = new Clip[] { menuClip }; // use array to modify from inner class

            soundToggle.addItemListener(e -> {
                if (!soundToggle.isSelected()) {
                    if (menuClipRef[0] != null && menuClipRef[0].isRunning()) {
                        menuClipRef[0].stop();
                    }
                } else {
                    if (menuClipRef[0] == null) {
                        menuClipRef[0] = SoundPlayer.playLoopingSound("menu.wav");
                    } else if (!menuClipRef[0].isRunning()) {
                        menuClipRef[0].setFramePosition(0);
                        menuClipRef[0].start();
                        menuClipRef[0].loop(Clip.LOOP_CONTINUOUSLY);
                    }
                }
            });            
        
            // Add UI to panel
            imagePanel.add(modeLabel);
            imagePanel.add(playerButton);
            imagePanel.add(aiButton);
            imagePanel.add(difficultyLabel);
            imagePanel.add(difficultySelector);
            imagePanel.add(soundToggle);
    
                // Wrap in red background panel
                javax.swing.JPanel redWrapper = new javax.swing.JPanel(new java.awt.GridLayout(1, 1));
                redWrapper.setBackground(Color.RED);
                redWrapper.add(imagePanel);
    
                Object[] options = {"OK", "EXIT"};
                int newOption = javax.swing.JOptionPane.showOptionDialog(
                    null,
                    redWrapper,
                    "Main Menu",
                    javax.swing.JOptionPane.OK_CANCEL_OPTION,
                    javax.swing.JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    options[0]
                );
                   
                if (newOption == 1 || newOption == javax.swing.JOptionPane.CLOSED_OPTION) {
                    System.exit(0);
                }
                
                if (newOption == javax.swing.JOptionPane.CANCEL_OPTION ||
                    newOption == javax.swing.JOptionPane.CLOSED_OPTION) {
                    System.exit(0);
                }
                
                // 🛑 Stop menu music ONLY when starting new game
                if (menuClipRef[0] != null && menuClipRef[0].isRunning()) {
                    menuClipRef[0].stop();
                    menuClipRef[0].close();
                }
                
                boolean newIsPlayerControlled = playerButton.isSelected();
                boolean newSoundEnabled = soundToggle.isSelected();
                String selectedAIType = (String) difficultySelector.getSelectedItem();
                
                GamePanel newPanel = new GamePanel(newIsPlayerControlled, newSoundEnabled, selectedAIType);
                javax.swing.JFrame frame = new javax.swing.JFrame("Super Mario Lite");
                frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
                frame.setContentPane(newPanel);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                
            });
        }
    }    

    private void restartGame() {
        player.reset();
        obstacles.clear();
        startTime = System.currentTimeMillis();
        gameOver = false;
        gameWin = false;
        winDisplayedTime = 0;
        nextObstacleDistance = Constants.OBSTACLE_MIN_GAP 
                + random.nextInt(Constants.OBSTACLE_MAX_GAP - Constants.OBSTACLE_MIN_GAP);
        
        selectRandomBackground(); // ← Pick a new background every restart
    
        if (soundEnabled) {
            playBackgroundMusic();
        }
        timer.start();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    
        if (selectedBackground != null) {
            g.drawImage(selectedBackground, 0, 0, Constants.FRAME_WIDTH, Constants.FRAME_HEIGHT, this);
        } else {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, Constants.FRAME_WIDTH, Constants.FRAME_HEIGHT);
        }
    
        if (grasssImage != null) {
            g.drawImage(grasssImage, 0, Constants.GROUND_LEVEL, Constants.FRAME_WIDTH, Constants.FRAME_HEIGHT - Constants.GROUND_LEVEL, this);
        }
    
        player.draw(g);
        for (Obstacle obs : obstacles) {
            obs.draw(g);
        }
    
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.setColor(Color.BLACK);
        if (gameOver) {
            g.drawString("Game Over", Constants.FRAME_WIDTH / 2 - 60, Constants.FRAME_HEIGHT / 2);
        } else if (gameWin) {
            g.drawString("You Win!", Constants.FRAME_WIDTH / 2 - 60, Constants.FRAME_HEIGHT / 2);
        } else {
            long elapsed = System.currentTimeMillis() - startTime;
            g.drawString("Time: " + elapsed / 1000, 10, 20);
        }
    }
}