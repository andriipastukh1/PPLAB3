package model;

public class AssaultDroid extends Droid {
    private double critChance; // шанс криту
    private double critMultiplier;

    public AssaultDroid(String name) {
        super(name, 120, 18, 60);
        this.critChance = 0.18;
        this.critMultiplier = 3;
    }

    @Override
    public int attack(Droid target) {
        boolean crit = Math.random() < critChance;
        int base = damage + (weapon != null ? weapon.getBonusDamage() : 0);
        int dealt = crit ? (int)Math.round(base * critMultiplier) : base;
        target.takeDamage(dealt);
        return dealt;
    }

    @Override
    public int specialAttack(Droid target) {
        int cost = 15;

        if (energy >= cost) {
            energy -= cost;
            int base = damage + (weapon != null ? weapon.getBonusDamage() : 0);

            int dealt = (int)Math.round(base * (critMultiplier + 0.5));

            target.takeDamage(dealt);
            return dealt;
        }

        return attack(target);
    }

}
