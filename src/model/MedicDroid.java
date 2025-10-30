package model;

public class MedicDroid extends Droid {
    private int healPower;
    private int regenPerRound;
    private final int HEAL_ENERGY_COST = 12;


    public MedicDroid(String name) {
        super(name, 100, 10, 70);
        this.healPower = 20;
        this.regenPerRound = 6;
        this.energyRegenPerRound = 8;
    }

    @Override
    public int attack(Droid target) {
        int dealt = damage;
        target.takeDamage(dealt);
        return dealt;
    }

    public int healAlly(Droid ally) {
        if (energy >= HEAL_ENERGY_COST) {
            energy -= HEAL_ENERGY_COST;
            ally.heal(healPower);
            return healPower;
        }
        return 0;
    }

    @Override
    public void endOfRound() {
        if(isAlive()){
            this.heal(regenPerRound);
        }
        super.endOfRound();
    }
}
