package com.lucafacchini.stats;

public class HP {
    public int currentHP;
    public int maxHP;

    public HP(int maxHP) {
        this.maxHP = maxHP;
        this.currentHP = maxHP;
    }

    public void addHP(int hp) {
        currentHP += hp;
        if(currentHP > maxHP) {
            currentHP = maxHP;
        }
    }

    public void removeHP(int hp) {
        currentHP -= hp;
        if(currentHP < 0) {
            currentHP = 0;
        }
    }

    public void setHP(int hp) {
        currentHP = hp;
        if(currentHP > maxHP) {
            currentHP = maxHP;
        }
        if(currentHP < 0) {
            currentHP = 0;
        }
    }

    public void setMaxHP(int maxHP) {
        this.maxHP = Math.max(maxHP, 0);
    }
}
