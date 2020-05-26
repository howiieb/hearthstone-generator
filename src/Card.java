public class Card {
    private Integer mana;
    private double budget;
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
    public int getMana(){ return mana; }
    double getBudget() { return budget; }
    public int getAttack() { return attack; }
    public int getHealth() { return health; }
    public MinionType getType() { return type; }
    public String getTypeStr() { return type.toString().substring(0, 1).toUpperCase() + type.toString().substring(1); }
    public String getText() { return text; }


    // Setter methods
    void setMana(int newMana){
        mana = newMana;
        budget = mana;
    }
    void spendBudget(double cost){ budget -= cost; }

    void setAttack(int newAttack){ attack = newAttack; }
    void setHealth(int newHealth){ health = newHealth; }
    public void setType(MinionType type) { this.type = type; }
    void setText(String newText){ text = newText; }
    void addToText(String newText) { text += newText; }
}
