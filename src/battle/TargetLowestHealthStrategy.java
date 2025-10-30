package battle;

import model.Droid;
import java.util.List;
import java.util.Optional;

public class TargetLowestHealthStrategy implements BattleStrategy {

    @Override
    public Optional<int[]> nextPair(List<Droid> teamA, List<Droid> teamB) {
        for (int ai = 0; ai < teamA.size(); ai++) {
            Droid attacker = teamA.get(ai);
            if (!attacker.isAlive()) continue;

            int minHp = Integer.MAX_VALUE;
            int targetIndex = -1;
            for (int bi = 0; bi < teamB.size(); bi++) {
                Droid defender = teamB.get(bi);
                if (defender.isAlive() && defender.getHealth() < minHp) {
                    minHp = defender.getHealth();
                    targetIndex = bi;
                }
            }

            if (targetIndex != -1) {
                return Optional.of(new int[]{ai, targetIndex});
            }
        }

        return Optional.empty();
    }
}
