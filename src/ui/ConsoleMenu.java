package ui;

import model.*;
import battle.*;
import io.BattleRecorder;
import io.Serializer;
import java.util.*;
import java.util.stream.Collectors;
import java.io.IOException;
import java.nio.file.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

import java.io.File;


public class ConsoleMenu {
    private Scanner sc = new Scanner(System.in);
    private List<Droid> droids = new ArrayList<>();
    private Arena arena = new Arena();

    public void run() {
        boolean running = true;
        while (running) {
            printMainMenu();
            String cmd = sc.nextLine().trim();
            switch (cmd) {
                case "1": createDroid(); break;
                case "2": listDroids(); break;
                case "3": fightOneOnOne(); break;
                case "4": fightTeamVsTeam(); break;
                case "5": showRecentLogs(); break;
                case "6": replayBattleFromFile(); break;
                case "7": saveDroids(); break;
                case "8": loadDroids(); break;
                case "0": running = false; break;
                default: System.out.println("Невідома команда.");
            }
        }
        System.out.println("Вихід. Дякую за гру!");
    }

    private void printMainMenu() {
        System.out.println("\n=== БИТВА ДРОЇДІВ ===");
        System.out.println("1 — Створити дроїда");
        System.out.println("2 — Список дроїдів");
        System.out.println("3 — Бій 1vs1");
        System.out.println("4 — Бій команда vs команда");
        System.out.println("5 — Останні логи боїв");
        System.out.println("6 — Відтворити бій");
        System.out.println("7 — Зберегти дроїдів");
        System.out.println("8 — Завантажити дроїди");
        System.out.println("0 — Вийти");
        System.out.print("Введіть команду: ");
    }

    private void createDroid() {
        String type;
        do {
            System.out.println("Оберіть вид дроїда: 1) Assault 2) Medic 3) Sniper 4) Tank");
            type = sc.nextLine().trim();
            if (!type.matches("[1-4]")) {
                System.out.println("Невірний вибір. Введіть 1-4.");
            }
        } while (!type.matches("[1-4]"));

        String name = "";
        if (!type.equals("10")) { // хард код !! тут просто заглушка ЗАБРАТИ зАглушку
            name = readNonEmptyString("Введіть ім'я дроїда: ");
        }

        Droid droid = switch (type) {
            case "1" -> new AssaultDroid(name);
            case "2" -> new MedicDroid(name);
            case "3" -> new SniperDroid(name);
            case "4" -> new TankDroid(name);
            default -> throw new IllegalStateException("Невідомий тип");
        };

        droids.add(droid);
        System.out.println("Створено: " + droid.describe());
    }

    private String readNonEmptyString(String prompt) {
        String input;
        do {
            System.out.print(prompt);
            input = sc.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Ім'я не може бути порожнім!");
            }
        } while (input.isEmpty());
        return input;
    }

    private void listDroids() {
        if (droids.isEmpty()) {
            System.out.println("Поки нема створених дроїдів.");
            return;
        }
        System.out.println("Список створених дроїдв:");
        for (int i = 0; i < droids.size(); i++) {
            System.out.println(i + ": " + droids.get(i).describe());
        }
    }

    private Droid pickDroid() {
        listDroids();
        if (droids.isEmpty()) return null;

        System.out.print("Введіть індекс дроїда: ");
        try {
            int idx = Integer.parseInt(sc.nextLine().trim());
            if (idx >= 0 && idx < droids.size()) {
                return cloneForBattle(droids.get(idx));
            }
        } catch (NumberFormatException e) {
            // ignore
        }
        System.out.println("Невірний індекс.");
        return null;
    }

    private Droid cloneForBattle(Droid src) {
        return switch (src) {
            case AssaultDroid ignored -> new AssaultDroid(src.getName());
            case MedicDroid ignored   -> new MedicDroid(src.getName());
            case SniperDroid ignored  -> new SniperDroid(src.getName());
            case TankDroid ignored    -> new TankDroid(src.getName());
            default -> throw new IllegalStateException("Невідомий тип дроїда");
        };
    }

    private void fightOneOnOne() {
        System.out.println("Виберіть 1 дроїда:");
        Droid a = pickDroid();
        if (a == null) return;

        System.out.println("Виберіть 2 дроїда:");
        Droid b = pickDroid();
        if (b == null) return;

        arena.oneOnOne(a, b);
        saveLogAuto();
    }

    private void fightTeamVsTeam() {
        if (droids.size() < 2) {
            System.out.println("Потрібно мінімум 2 дроїди для командного бою.");
            return;
        }

        int teamASize = readPositiveInt("Дроїди в команді A? ");
        int teamBSize = readPositiveInt("Дроїди в команді B? ");

        List<Droid> teamA = selectTeam("A", teamASize);
        List<Droid> teamB = selectTeam("B", teamBSize);
        if (teamA.isEmpty() || teamB.isEmpty()) return;

        BattleStrategy strategy = chooseStrategy();
        arena.teamBattle(teamA, teamB, strategy);
        saveLogAuto();
    }

    private List<Droid> selectTeam(String teamName, int size) {
        List<Droid> team = new ArrayList<>();
        System.out.println("Виберіть дроїдів для команди " + teamName + ":");
        for (int i = 0; i < size; i++) {
            System.out.println("Дроїд " + teamName + " #" + (i + 1) + ":");
            Droid d = pickDroid();
            if (d != null) {
                team.add(d);
            } else {
                i--; // повтор
            }
        }
        return team;
    }

    private BattleStrategy chooseStrategy() {
        System.out.println("Оберіть стратегію бою:");
        System.out.println("1 — Випадкова пара");
        System.out.println("2 — Атакувати найслабшого");
        System.out.println("3 — Перший → найслабший");

        String choice;
        do {
            choice = sc.nextLine().trim();
            if (!choice.matches("[1-3]")) {
                System.out.println("Введіть 1, 2 або 3.");
            }
        } while (!choice.matches("[1-3]"));

        return switch (choice) {
            case "1" -> new RandomPairStrategy();
            case "2" -> new WeakestTargetStrategy();
            case "3" -> new TargetLowestHealthStrategy();
            default -> new RandomPairStrategy();
        };
    }

    private int readPositiveInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int val = Integer.parseInt(sc.nextLine().trim());
                if (val > 0) return val;
                System.out.println("Введіть число більше 0.");
            } catch (NumberFormatException e) {
                System.out.println("Невірне число.");
            }
        }
    }

    private void saveLogAuto() {
        try {
            String path = BattleRecorder.saveBattleLog(arena.getLog());
            System.out.println("Логи бою збережено: " + path);
        } catch (IOException e) {
            System.err.println("Помилка збереження логу: " + e.getMessage());
        }
    }

    private void showRecentLogs() {
        Path logsDir = Paths.get("logs");
        if (!Files.exists(logsDir)) {
            System.out.println("Ще нема збережених боїв");
            return;
        }

        try (var stream = Files.list(logsDir)) {
            List<Path> files = stream
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".txt"))
                    .sorted((a, b) -> Long.compare(b.toFile().lastModified(), a.toFile().lastModified()))
                    .limit(10)
                    .collect(Collectors.toList());

            if (files.isEmpty()) {
                System.out.println("Логів не знайдено.");
                return;
            }

            System.out.println("Останні 10 боїв:");
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
            for (Path p : files) {
                File f = p.toFile();
                LocalDateTime time = LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(f.lastModified()), ZoneId.systemDefault());
                System.out.printf("• %s (%s)%n", f.getName(), time.format(dtf));
            }
        } catch (IOException e) {
            System.out.println("Помилка читання логів: " + e.getMessage());
        }
    }

    private void replayBattleFromFile() {
        System.out.print("Введіть шлях до логфайлу або частину назви: ");
        String input = sc.nextLine().trim();

        Path logsDir = Paths.get("logs");
        if (!Files.exists(logsDir)) {
            System.out.println("Папки logs немає.");
            return;
        }

        try (var stream = Files.list(logsDir)) {
            List<Path> matches = stream
                    .filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().contains(input))
                    .collect(Collectors.toList());

            if (matches.isEmpty()) {
                System.out.println("Файла НЕМА :( або десь є але ти неправильно написав :))))");
                return;
            }

            Path file = matches.get(0);
            if (matches.size() > 1) {
                System.out.println("Знайдено кілька файлів. Відтворюється перший: " + file.getFileName());
            }

            List<String> lines = Files.readAllLines(file);
            System.out.println("===! ВІДТВОРЕННЯ БОЮ: " + file.getFileName() + " ===");
            for (String line : lines) {
                System.out.println(line);
                try { Thread.sleep(150); } catch (InterruptedException ignored) {}
            }
            System.out.println("=== КІНЕЦЬ ===");
        } catch (IOException e) {
            System.out.println("Помилка: " + e.getMessage());
        }
    }

    private void saveDroids() {
        try {
            Serializer.saveDroids(droids, "droids.dat");
            System.out.println("Дроїди збережено в droids.dat");
        } catch (Exception e) {
            System.out.println("Помилка збереження: " + e.getMessage());
        }
    }

    private void loadDroids() {
        List<Droid> loaded = Serializer.loadDroids("droids.dat");
        if (loaded == null || loaded.isEmpty()) {
            System.out.println("Не вдалося завантажити дроїди, шось з файлом:) .");
            return;
        }
        droids.clear();
        droids.addAll(loaded);
        System.out.println("Завантажено " + loaded.size() + " дроїдів.");
    }
}