package model;

public class TestDroid extends Droid {
    private final double damageReduction;
    private final int regenPerRound;

    public TestDroid(String name) {
        super(name, 180, 15);
        this.damageReduction = 0.15;
        this.regenPerRound = 0;
    }

    @Override
    public void takeDamage(int amount) {
        int reduced = (int) Math.round(amount * (1 - damageReduction));
        super.takeDamage(reduced);
    }

    @Override
    public void endOfRound() {
        if (isAlive()) {
            health += regenPerRound;
            if (health > maxHealth) health = maxHealth;
        }
        super.endOfRound();
    }

    @Override
    public String toString() {
        return "TankDroid{" +
                "name='" + name + '\'' +
                ", health=" + health +
                ", damage=" + damage +
                ", damageReduction=" + (int)(damageReduction * 100) + "%" +
                ", regen=" + regenPerRound +
                '}';
    }
}
