package game.engine;

import engine.GamePanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.io.File;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;


public class Game {

    private static Clip playSoundLoop(String filePath) {
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(filePath));
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
            return clip;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        // 🔊 Play looping menu sound
        Clip menuClip = playSoundLoop("C:\\Users\\lomes\\OneDrive\\Desktop\\gamejava\\assets\\menu.wav");

        // Load background image
        Image backgroundImage = null;
        try {
            backgroundImage = ImageIO.read(new File("C:\\Users\\lomes\\OneDrive\\Desktop\\gamejava\\assets\\pbackground.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create panel with background
        Image finalBackgroundImage = backgroundImage;
        JPanel imagePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (finalBackgroundImage != null) {
                    g.drawImage(finalBackgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        imagePanel.setLayout(new GridLayout(0, 1));
        imagePanel.setOpaque(false);

        // Components
        JLabel modeLabel = new JLabel("Choose game mode:");
        modeLabel.setOpaque(false);
        modeLabel.setForeground(Color.WHITE);

        ButtonGroup modeGroup = new ButtonGroup();
        JRadioButton playerButton = new JRadioButton("Player", true);
        playerButton.setOpaque(false);
        playerButton.setForeground(Color.WHITE);

        JRadioButton aiButton = new JRadioButton("AI");
        aiButton.setOpaque(false);
        aiButton.setForeground(Color.WHITE);

        modeGroup.add(playerButton);
        modeGroup.add(aiButton);

        JCheckBox soundToggle = new JCheckBox("Enable Sound", true);
        soundToggle.setOpaque(false);
        soundToggle.setForeground(Color.WHITE);       

        // 🧠 AI Difficulty Selector
        JLabel difficultyLabel = new JLabel("Select AI Difficulty:");
        difficultyLabel.setOpaque(false);
        difficultyLabel.setForeground(Color.WHITE);
        difficultyLabel.setVisible(false);

        String[] difficulties = {"Basic", "Smart", "Advanced"};
        JComboBox<String> difficultySelector = new JComboBox<>(difficulties);
        difficultySelector.setVisible(false);

        // Toggle difficulty dropdown when AI is selected
        aiButton.addActionListener(e -> {
            difficultyLabel.setVisible(true);
            difficultySelector.setVisible(true);
        });
        playerButton.addActionListener(e -> {
            difficultyLabel.setVisible(false);
            difficultySelector.setVisible(false);
        });

        // ✅ Toggle music when checkbox is changed
        soundToggle.addItemListener(e -> {
            if (!soundToggle.isSelected()) {
                if (menuClip != null && menuClip.isRunning()) {
                    menuClip.stop();
                }
            } else {
                if (menuClip != null && !menuClip.isRunning()) {
                    menuClip.setFramePosition(0);
                    menuClip.start();
                    menuClip.loop(Clip.LOOP_CONTINUOUSLY);
                }
            }
        });

        // Add all components to imagePanel
        imagePanel.add(modeLabel);
        imagePanel.add(playerButton);
        imagePanel.add(aiButton);
        imagePanel.add(difficultyLabel);
        imagePanel.add(difficultySelector);
        imagePanel.add(soundToggle);

        JPanel redWrapper = new JPanel(new GridLayout(1, 1));
        redWrapper.setBackground(Color.RED);
        redWrapper.add(imagePanel);

        Object[] options = {"OK", "EXIT"};
        int option = JOptionPane.showOptionDialog(
                null,
                redWrapper,
                "Main Menu",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );

        // ⛔ Stop menu sound
        if (menuClip != null && menuClip.isRunning()) {
            menuClip.stop();
            menuClip.close();
        }

        // Handle EXIT button or dialog close
        if (option == 1 || option == JOptionPane.CLOSED_OPTION) {
            System.exit(0);
        }       

        boolean isPlayerControlled = playerButton.isSelected();
        boolean soundEnabled = soundToggle.isSelected();
        String selectedAIDifficulty = (String) difficultySelector.getSelectedItem(); // "Basic", "Smart", or "Advanced"

        // 👇 Pass this to GamePanel
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Super Mario Lite");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);

            GamePanel gamePanel = new GamePanel(isPlayerControlled, soundEnabled, selectedAIDifficulty);
            frame.add(gamePanel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}