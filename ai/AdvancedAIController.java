package ai;

import java.util.List;
import model.Obstacle;
import model.Player;

public class AdvancedAIController extends AIController {

    private int baseJumpThreshold = 160;

    @Override
    public void update(Player player, List<Obstacle> obstacles) {
        for (Obstacle obstacle : obstacles) {
            int distance = obstacle.x - player.x;

            if (distance <= 0) continue;
            if (obstacle.height <= player.getStepOverHeight()) continue;

            int adjustedThreshold = baseJumpThreshold;

            // Take into account both height and width dynamically
            if (obstacle.width > 100 || obstacle.height > 100) {
                adjustedThreshold += 60; // massive obstacle
            } else if (obstacle.width > 60 || obstacle.height > 60) {
                adjustedThreshold += 30;
            }

            // Anticipate jump if multiple obstacles are close
            if (isObstacleClusterAhead(player, obstacles)) {
                adjustedThreshold += 20;
            }

            if (distance < adjustedThreshold && player.canJump() && !player.isJumping()) {
                player.jump();
                break;
            }
        }
    }

    private boolean isObstacleClusterAhead(Player player, List<Obstacle> obstacles) {
        int count = 0;
        for (Obstacle obs : obstacles) {
            int dist = obs.x - player.x;
            if (dist > 0 && dist < 250 && obs.height > player.getStepOverHeight()) {
                count++;
            }
        }
        return count >= 2;
    }
}
