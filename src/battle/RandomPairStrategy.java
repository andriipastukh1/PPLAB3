package battle;

import model.Droid;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Optional;

public class RandomPairStrategy implements BattleStrategy {
    private Random rnd = new Random();

    @Override
    public Optional<int[]> nextPair(List<Droid> teamA, List<Droid> teamB) {
        List<Integer> aAlive = new ArrayList<>();
        List<Integer> bAlive = new ArrayList<>();
        for (int i = 0; i < teamA.size(); i++) if (teamA.get(i).isAlive()) aAlive.add(i);
        for (int j = 0; j < teamB.size(); j++) if (teamB.get(j).isAlive()) bAlive.add(j);
        if (aAlive.isEmpty() || bAlive.isEmpty()) return Optional.empty();
        int ai = aAlive.get(rnd.nextInt(aAlive.size()));
        int bi = bAlive.get(rnd.nextInt(bAlive.size()));
        return Optional.of(new int[]{ai, bi});
    }
}
