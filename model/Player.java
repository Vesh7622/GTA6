package model;

import gamejava.SoundPlayer;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Player {
    public int x = 100;
    public int y;
    public int width = 50;
    public int height = 100;
    public int yVelocity;
    private boolean isJumping;
    private Image mariooImage;
    private boolean soundEnabled;

    // Added initial position for reset support
    private final int initialX = 100;
    private final int initialY = 300 - height;

    public Player(boolean soundEnabled) {
        this.soundEnabled = soundEnabled;
        this.y = initialY;
        this.yVelocity = 0;
        this.isJumping = false;
    
        try {
            this.mariooImage = ImageIO.read(new File("assets/marioo.png"));
        } catch (IOException var2) {
            System.err.println("Error loading marioo.png image. Using fallback rectangle.");
            this.mariooImage = null;
        }
    }
    

    public void jump() {
        if (!this.isJumping) {
            this.yVelocity = -20;
            this.isJumping = true;
            if (soundEnabled) {
                SoundPlayer.playSound("jump.wav");
            }
        }
    }
    public void update() {
        this.y += this.yVelocity;
        if (this.y < 300 - this.height) {
            ++this.yVelocity;
        } else {
            this.y = 300 - this.height;
            this.yVelocity = 0;
            this.isJumping = false;
        }
    }

    public void draw(Graphics var1) {
        if (this.mariooImage != null) {
            var1.drawImage(this.mariooImage, this.x, this.y, this.width, this.height, (ImageObserver)null);
        } else {
            var1.setColor(Color.BLUE);
            var1.fillRect(this.x, this.y, this.width, this.height);
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(this.x, this.y, this.width, this.height);
    }

    public void reset() {
        x = initialX;
        y = initialY;
        yVelocity = 0;
        isJumping = false;
    }

    // ✅ New Methods for AIController

    // Height the player can step over without jumping
    public int getStepOverHeight() {
        return 20; // adjust based on your game design
    }

    // Can the player currently jump?
    public boolean canJump() {
        return !isJumping;
    }

    // Is the player mid-jump?
    public boolean isJumping() {
        return isJumping;
    }
}
