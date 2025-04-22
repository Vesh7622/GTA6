package ai;

import java.util.List;
import model.Obstacle;
import model.Player;

public class SmartAIController extends AIController {

    private int baseJumpThreshold = 150;

    @Override
    public void update(Player player, List<Obstacle> obstacles) {
        for (Obstacle obstacle : obstacles) {
            int distance = obstacle.x - player.x;

            if (distance <= 0) continue;
            if (obstacle.height <= player.getStepOverHeight()) continue;

            int adjustedThreshold = baseJumpThreshold;

            if (obstacle.width > 60) {
                adjustedThreshold += 30;
            }
            if (obstacle.width > 100) {
                adjustedThreshold += 50;
            }

            if (distance < adjustedThreshold && player.canJump() && !player.isJumping()) {
                player.jump();
                break;
            }
        }
    }

    public void setBaseJumpThreshold(int threshold) {
        this.baseJumpThreshold = threshold;
    }
}
