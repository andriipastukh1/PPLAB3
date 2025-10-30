package model;





// Нагад !!глянути чи юзаю клас якшо не то видалаит
public class Weapon {
    private String name;
    private int bonusDamage;
    private int energyCost;

    public Weapon(String name, int bonusDamage, int energyCost) {
        this.name = name;
        this.bonusDamage = bonusDamage;
        this.energyCost = energyCost;
    }

    public String getName() { return name; }
    public int getBonusDamage() { return bonusDamage; }
    public int getEnergyCost() { return energyCost; }

    @Override
    public String toString() {
        return String.format("%s (+%d dmg, cost %dE)", name, bonusDamage, energyCost);
    }
}
