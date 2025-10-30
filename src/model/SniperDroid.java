package model;



public class SniperDroid extends Droid {
    private double accuracy;
    private static final double CRIT_MULTIPLIER = 2.5;


    public SniperDroid(String name) {
        super(name, 80, 30, 40);
        this.accuracy = 0.78;
    }

    @Override
    public int attack(Droid target) {
        if (Math.random() <= accuracy) {
            int dealt = damage + (weapon != null ? weapon.getBonusDamage() : 0);
            target.takeDamage(dealt);
            return dealt;
        } else {
            return 0;
        }
    }

    @Override
    public int specialAttack(Droid target) {
        int energyCost = 30;


        if (energy >= energyCost) {

            energy -= energyCost;

            int baseDamage = damage + (weapon != null ? weapon.getBonusDamage() : 0);

            int dealtDamage = (int)Math.round(baseDamage * CRIT_MULTIPLIER);

            target.takeDamage(dealtDamage);

            return dealtDamage;
        }

        return attack(target);
    }



}
