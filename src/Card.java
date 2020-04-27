import java.util.HashMap;

public class Card {
    private Integer mana;
    private Integer attack;
    private Integer health;
    private String text;
    private HashMap<Integer,Integer> vanilla;

    public Card(){
        mana = 0;
        attack = 0;
        health = 0;
        text = "";
        HashMap<Integer, Integer> vanilla = new HashMap<>();
        vanilla.put(1,3);
        vanilla.put(2,5);
        vanilla.put(3,7);
        vanilla.put(4,9);
        vanilla.put(5,11);
        vanilla.put(6,13);
        vanilla.put(7,14);
        vanilla.put(8,16);
        vanilla.put(9,18);
    }

    // Accessor methods
    int getMana(){
        return mana;
    }
    int getAttack(){
        return attack;
    }
    int getHealth(){
        return health;
    }
    String getText(){
        return text;
    }
    HashMap getVanilla(){
        return vanilla;
    }

    // Setter methods
    void setMana(int newMana){
        mana = newMana;
    }
    void setAttack(int newAttack){
        mana = newAttack;
    }
    void setHealth(int newHealth){
        mana = newHealth;
    }
    void setDescription(int newDescription){
        mana = newDescription;
    }

}
