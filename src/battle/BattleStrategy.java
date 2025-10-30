package battle;

import model.Droid;
import java.util.List;
import java.util.Optional;

public interface BattleStrategy {
    Optional<int[]> nextPair(List<Droid> teamA, List<Droid> teamB);
}
