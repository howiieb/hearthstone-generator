public class Card {
    private Integer mana;
    private Integer budget;
    private Integer attack;
    private Integer health;
    private MinionType type;
    private String text;

    public Card(){
        mana = 0;
        budget = 0;
        attack = 0;
        health = 0;
        text = "";
        type = MinionType.none;

    }

    // Accessor methods
    int getMana(){ return mana; }
    int getBudget() { return budget; }
    int getAttack() { return attack; }
    int getHealth() { return health; }
    public MinionType getType() { return type; }
    String getText() { return text; }


    // Setter methods
    void setMana(int newMana){
        mana = newMana;
        budget = mana;
    }
    void spendBudget(int cost){ budget -= cost; }

    void setAttack(int newAttack){ attack = newAttack; }
    void setHealth(int newHealth){ health = newHealth; }
    public void setType(MinionType type) { this.type = type; }
    void setText(String newText){ text = newText; }
    void addToText(String newText) { text += newText; }

}
