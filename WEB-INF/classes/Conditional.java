package hs_generator;

public class Conditional {
    /* This object stores text of a conditional, its cost (usually negative), and the minion types it specifically
    concerns (if any). */
    private String text;
    private Integer cost;
    private MinionType type;
    private boolean battlecryOnly;

    public Conditional(String text, Integer cost, MinionType type, boolean battlecry) {
        this.text = text;
        this.cost = cost;
        this.type = type;
        this.battlecryOnly = battlecry;

    }

    public String getText() { return text; }
    public Integer getCost() { return cost; }
    public MinionType getType() { return type; }
    public boolean isBattlecryOnly() { return battlecryOnly; }
}
