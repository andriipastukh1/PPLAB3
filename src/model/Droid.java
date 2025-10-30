package model;

import java.util.Random;
import java.io.Serializable;
import model.Weapon;


public abstract class Droid implements Serializable {
    protected String name;
    protected int health;
    protected int maxHealth;
    protected int damage;
    protected int initiative; // новий параметр
    protected int enegry; // новий параметр

    protected int energy; // Current energy
    protected int maxEnergy; // Maximum energy
    protected int energyRegenPerRound; // Energy regeneration rate
    protected Weapon weapon;


    public int getMaxHealth() {
        return maxHealth;
    }

    public Droid(String name, int health, int damage, int initiative) {
        this.name = name;
        this.maxHealth = health;
        this.health = health;
        this.damage = damage;
        this.initiative = initiative;


        this.maxEnergy = 100; // Example max energy
        this.energy = maxEnergy; // Start with full energy
        this.energyRegenPerRound = 15; // Example regeneration rate



    }
    public int getInitiative() {
        return initiative;
    }

    public Droid(String name, int health, int damage) {
        this(name, health, damage, 50);
    }

    public String getName() { return name; }
    public int getHealth() { return health; }
    public int getDamage() { return damage; }
    public int getEnergy() { return energy; }
    public Weapon getWeapon() { return weapon; }

    public void setWeapon(Weapon w) { this.weapon = w; }

    public boolean isAlive() { return health > 0; }

    public int attack(Droid target) {
        int base = damage + (weapon != null ? weapon.getBonusDamage() : 0);
        target.takeDamage(base);
        return base;
    }


    public int specialAttack(Droid target) {
        int cost = 25;

        if (weapon != null && energy >= weapon.getEnergyCost()) {
            cost = weapon.getEnergyCost();
            energy -= cost;
            int dealt = (int)Math.round((damage + weapon.getBonusDamage()) * 1.8);
            target.takeDamage(dealt);
            return dealt;
        }

        if (energy >= cost) {
            energy -= cost;
            int dealt = (int)Math.round(damage * 1.5);
            target.takeDamage(dealt);
            return dealt;
        } else {
            return attack(target);
        }
    }

    public void takeDamage(int amount) {
        health = Math.max(0, health - amount);
    }

    public void heal(int amount) {
        if (isAlive()) {
            health = Math.min(maxHealth, health + amount);
        }
    }

    public void regenEnergy() {
        energy = Math.min(maxEnergy, energy + energyRegenPerRound);
    }

    public String describe() {
        String w = (weapon != null) ? (" | W:" + weapon.getName()) : "";
        return String.format("%s (HP:%d/%d, DMG:%d, E:%d/%d%s)",
                name, health, maxHealth, damage, energy, maxEnergy, w);
    }

    public void endOfRound() {
        regenEnergy();
    }

    @Override
    public String toString() {
        return describe();
    }
}
