package ai;

import java.util.List;
import model.Obstacle;
import model.Player;

public class BasicAIController extends AIController {

    private int jumpThreshold = 120; // Slightly less cautious

    @Override
    public void update(Player player, List<Obstacle> obstacles) {
        // Jump way too late or not at all
        if (!obstacles.isEmpty()) {
            Obstacle obstacle = obstacles.get(0); // Always reacts to the first one
            int distance = obstacle.x - player.x;
    
            if (distance < 72 && player.canJump() && !player.isJumping()) {
                player.jump(); // Reacts too late to ever succeed
            }
        }
    }
}
    
