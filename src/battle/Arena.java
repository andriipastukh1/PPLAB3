package battle;

import model.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class Arena {
    private List<String> log = new ArrayList<>();
    private DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final int MAX_ROUNDS = 1000;

    public List<String> getLog() {
        return log;
    }

    private void write(String s) {
        String ts = LocalDateTime.now().format(df);
        String line = "[" + ts + "] " + s;
        log.add(line);
        System.out.println(line);
    }

    private void writeHP(Droid a, Droid b) {
        String aStatus = a.isAlive() ? String.format("HP:%d/%d", a.getHealth(), a.getMaxHealth()) : ";( МЕРТВИЙ";
        String bStatus = b.isAlive() ? String.format("HP:%d/%d", b.getHealth(), b.getMaxHealth()) : ";( МЕРТВИЙ";
        write(String.format("%-20s %s | %-20s %s",
                a.getName(), aStatus, b.getName(), bStatus));
    }

    private void writeTeamsHP(List<Droid> teamA, List<Droid> teamB) {
        write("--- СТАН КОМАНД ---");
        write(String.format("%-35s | %-35s", "КОМАНДА A", "КОМАНДА B"));
        int max = Math.max(teamA.size(), teamB.size());
        for (int i = 0; i < max; i++) {
            String left = i < teamA.size() ? formatDroid(teamA.get(i)) : "";
            String right = i < teamB.size() ? formatDroid(teamB.get(i)) : "";
            write(String.format("%-35s | %-35s", left, right));
        }
    }

    private String formatDroid(Droid d) {
        if (!d.isAlive()) return "МЕРТВИЙ :(" + d.getName();
        return String.format("%s (%d/%d HP)", d.getName(), d.getHealth(), d.getMaxHealth());
    }


    private String getStatusString(List<Droid> team, int i) {
        if (i >= team.size()) return "";
        Droid d = team.get(i);
        if (!d.isAlive()) {
            return "☠️ " + d.getName();
        }
        return d.getName() + String.format(" (%d/%d HP)", d.getHealth(), d.getMaxHealth());
    }

    public void oneOnOne(Droid a, Droid b) {
        log.clear();
        write("=== БІЙ 1vs1 (Чергування Ходів) ===");
        write(a.describe() + " VS " + b.describe());
        writeHP(a, b);

        int round = 1;
        Droid[] fighters = {a, b};

        if (fighters[0].getInitiative() < fighters[1].getInitiative()) {
            Droid tmp = fighters[0];
            fighters[0] = fighters[1];
            fighters[1] = tmp;
        }

        boolean startWithFighter0 = true;

        while (a.isAlive() && b.isAlive() && round <= MAX_ROUNDS) {
            write("\n!!!!*** РАУНД " + round + " !!!!***");

            Droid att, def;

            if (startWithFighter0) {
                att = fighters[0];
                def = fighters[1];
            } else {
                att = fighters[1];
                def = fighters[0];
            }

            startWithFighter0 = !startWithFighter0;


            int dmg = att.attack(def);
            write(att.getName() + " ->>>" + def.getName() + ": " + dmg + " DMG");
            writeHP(a, b);

            if (!def.isAlive()) {
                write(":( " + def.getName() + " ЗНИЩЕНО!");
                break;
            }

            int cntDmg = def.attack(att);
            write(def.getName() + " ->>> " + att.getName() + ": " + cntDmg + " DMG");
            writeHP(a, b);

            att.endOfRound();
            def.endOfRound();

            round++;
        }

        if (round > MAX_ROUNDS) {
            write("//// НІЧИЯ! Бій затягнувся...");
        } else {
            Droid winner = a.isAlive() ? a : b;
            write("ПЕРЕМОЖЕЦЬ: " + winner.getName().toUpperCase());
        }
        write("==================");
    }

    public void teamBattle(List<Droid> teamA, List<Droid> teamB, BattleStrategy strategy) {
        log.clear();
        write("=== БІЙ КОМАНДА vs КОМАНДА ===");
        write("Команда A (" + teamA.size() + " дроїдів) VS Команда B (" + teamB.size() + ")");
        writeTeamsHP(teamA, teamB);

        int round = 1;
        while (aliveCount(teamA) > 0 && aliveCount(teamB) > 0 && round <= MAX_ROUNDS) {
            write("\n========== РАУНД " + round + " ==========");

            Optional<int[]> pairOpt = strategy.nextPair(teamA, teamB);
            if (!pairOpt.isPresent()) {
                write("---Немає цілей для атаки!");
                break;
            }

            int[] pair = pairOpt.get();
            Droid attacker = teamA.get(pair[0]);
            Droid defender = teamB.get(pair[1]);

            int dmg = attacker.attack(defender);
            write(attacker.getName() + " (A) → " + defender.getName() + " (B): " + dmg + " DMG");
            write(defender.getName() + " HP: " + defender.getHealth() + "/" + defender.getMaxHealth());

            if (!defender.isAlive()) {
                write(":(" + defender.getName() + " ЗНИЩЕНО!");

                if (aliveCount(teamA) == 0 || aliveCount(teamB) == 0) {
                    write("!!!!!Кінець бою: команду знищено!");
                    break;
                }
            } else {
                int cntDmg = defender.attack(attacker);
                write(defender.getName() + " (B) → " + attacker.getName() + " (A): " + cntDmg + " DMG");
                write(attacker.getName() + " HP: " + attacker.getHealth() + "/" + attacker.getMaxHealth());
            }

            runEndOfRound(teamA);
            runEndOfRound(teamB);

            writeTeamsHP(teamA, teamB);

            round++;
        }

        write("\nРЕЗУЛЬТАТ:");
        if (round > MAX_ROUNDS) {
            write("++++++++НІЧИЯ! Затягнутий бій (>" + MAX_ROUNDS + " раундів)");
        } else if (aliveCount(teamA) > 0) {
            write("ПЕРЕМОГА КОМАНДИ A!");
        } else if (aliveCount(teamB) > 0) {
            write("ПЕРЕМОГА КОМАНДИ B!");
        } else {
            write("НІЧИЯ!");
        }
        write("=====================================");
    }

    private void runEndOfRound(List<Droid> team) {
        List<MedicDroid> medics = new ArrayList<>();
        for (Droid d : team) {
            if (d instanceof MedicDroid && d.isAlive()) {
                medics.add((MedicDroid) d);
            }
        }

        for (MedicDroid medic : medics) {
            Droid toHeal = findLowestHealthAlly(team, medic);
            if (toHeal != null) {
                int healed = medic.healAlly(toHeal);
                write("Медик " + medic.getName() + " → " + toHeal.getName() +
                        ": " + (healed > 0 ? "+" + healed : "0 (немає енергії)") + " HP");
            } else {
                write("Медик " + medic.getName() + " → немає кого лікувати");
            }
        }

        for (Droid d : team) {
            d.endOfRound();
        }
    }

    private Droid findLowestHealthAlly(List<Droid> team, MedicDroid medic) {
        Droid lowest = null;
        int minHp = Integer.MAX_VALUE;
        for (Droid ally : team) {
            if (ally.isAlive() && ally != medic && ally.getHealth() < minHp) {
                minHp = ally.getHealth();
                lowest = ally;
            }
        }
        return lowest;
    }

    private int aliveCount(List<Droid> team) {
        int count = 0;
        for (Droid d : team) {
            if (d.isAlive()) count++;
        }
        return count;
    }
}