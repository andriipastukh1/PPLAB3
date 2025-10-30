    package battle;

    import model.Droid;
    import java.util.List;
    import java.util.Optional;
    import java.util.ArrayList;
    import java.util.Random;

    public class WeakestTargetStrategy implements BattleStrategy {
        private Random rnd = new Random();

        @Override
        public Optional<int[]> nextPair(List<Droid> teamA, List<Droid> teamB) {
            List<Integer> aAlive = new ArrayList<>();
            for (int i = 0; i < teamA.size(); i++) if (teamA.get(i).isAlive()) aAlive.add(i);
            List<Integer> bAlive = new ArrayList<>();
            for (int j = 0; j < teamB.size(); j++) if (teamB.get(j).isAlive()) bAlive.add(j);
            if (aAlive.isEmpty() || bAlive.isEmpty()) return Optional.empty();

            int ai = aAlive.get(rnd.nextInt(aAlive.size()));

            int minHp = Integer.MAX_VALUE; int bi = bAlive.get(0);
            for (Integer idx : bAlive) {
                int hp = teamB.get(idx).getHealth();
                if (hp < minHp) { minHp = hp; bi = idx; }
            }
            return Optional.of(new int[]{ai, bi});
        }
    }
