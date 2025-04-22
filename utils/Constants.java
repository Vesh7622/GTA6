package utils;

/**
 * Contains application-wide constants.
 */
public class Constants {
    public static final int FRAME_WIDTH = 800;
    public static final int FRAME_HEIGHT = 400;
    public static final int GROUND_LEVEL = FRAME_HEIGHT - 100;
    
    public static final int PLAYER_WIDTH = 50;
    public static final int PLAYER_HEIGHT = 100;
    
    public static final int OBSTACLE_WIDTH = 30;
    public static final int OBSTACLE_HEIGHT = 50;
    
    public static final int JUMP_VELOCITY = -20;
    public static final int GRAVITY = 1;
    
    // Distance between obstacles will vary between these gap values
    public static final int OBSTACLE_MIN_GAP = 200;
    public static final int OBSTACLE_MAX_GAP = 400;
    
    // The win condition: survive for 30 seconds.
    public static final int WIN_TIME = 30000; 
}
