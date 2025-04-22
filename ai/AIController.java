package ai;

import java.util.List;
import model.Obstacle;
import model.Player;

public abstract class AIController {
    public abstract void update(Player player, List<Obstacle> obstacles);
}
