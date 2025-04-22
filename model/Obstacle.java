package model;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;
import utils.Constants;

/**
 * Represents an obstacle (a cactus) in the game.
 */
public class Obstacle {
    public int x, y;
    public int width, height;
    public int speed;
    private Image cactusImage;
    private static final Random random = new Random();
    private static final String[] OBSTACLE_IMAGES = {
        "assets/beetle.png",
        "assets/turtle.png",
        "assets/spike.png",
        "assets/Goomba.png"
    };
    private static final Image[] loadedImages = new Image[OBSTACLE_IMAGES.length];

    // Static block to load images once
    static {
        for (int i = 0; i < OBSTACLE_IMAGES.length; i++) {
            try {
                loadedImages[i] = ImageIO.read(new File(OBSTACLE_IMAGES[i]));
            } catch (IOException e) {
                System.err.println("Failed to load " + OBSTACLE_IMAGES[i] + ". Using null.");
                loadedImages[i] = null;
            }
        }
    }

    /**
     * Creates an obstacle starting at the specified x coordinate.
     * The obstacle is placed on the ground with a random speed.
     */
    public Obstacle(int startX) {
        // Default size from Constants
        this.width = Constants.OBSTACLE_WIDTH;
        this.height = Constants.OBSTACLE_HEIGHT;
        this.x = startX;
        this.speed = 5 + random.nextInt(6);

        // Randomly pick one image
        int index = random.nextInt(loadedImages.length);
        this.cactusImage = loadedImages[index];

        // Apply individual scaling per obstacle type
        switch (index) {
            case 0: // beetle.png — smaller height
                this.width = (int)(Constants.OBSTACLE_WIDTH * 1.0);
                this.height = (int)(Constants.OBSTACLE_HEIGHT * 1.0);
                break;
            case 1: // turtle.png — taller
                this.width = (int)(Constants.OBSTACLE_WIDTH * 1.3);
                this.height = (int)(Constants.OBSTACLE_HEIGHT * 1.3);
                break;
            case 2: // spike.png — smaller and wider
                this.width = (int)(Constants.OBSTACLE_WIDTH * 1.3);
                this.height = (int)(Constants.OBSTACLE_HEIGHT * 1.3);
                break;
            case 3: // Goomba.png — slightly larger overall
                this.width = (int)(Constants.OBSTACLE_WIDTH * 1.1);
                this.height = (int)(Constants.OBSTACLE_HEIGHT * 1.1);
                break;
        }

        // Recalculate Y after height adjustment to keep obstacle on ground
        this.y = Constants.GROUND_LEVEL - this.height;
    }

    /**
     * Moves the obstacle left by its speed.
     */
    public void update() {
        x -= speed;
    }

    /**
     * Draws the obstacle using its image or fallback.
     */
    public void draw(Graphics g) {
        if (cactusImage != null) {
            g.drawImage(cactusImage, x, y, width, height, null);
        } else {
            g.setColor(Color.RED);
            g.fillRect(x, y, width, height);
        }
    }

    /**
     * Determines if the obstacle has moved off screen.
     */
    public boolean isOffScreen() {
        return (x + width < 0);
    }

    /**
     * Gets the bounding rectangle for collision detection.
     */
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}
