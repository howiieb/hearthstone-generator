public class Conditional {
    /* This object stores text of a conditional, its cost (usually negative), and the minion types it specifically
    concerns (if any). */
    private String text;
    private Integer cost;
    private MinionType type;
    private Conditional[] conditionals;

    public Conditional(String text, Integer cost, MinionType type) {
        this.text = text;
        this.cost = cost;
        this.type = type;

        this.conditionals = new Conditional[]{new Conditional("",0,MinionType.none),
                new Conditional("If you are holding a dragon, ",-2,MinionType.dragon),
                new Conditional("If you played an elemental last turn, ",-3,MinionType.elemental),
                new Conditional("If you have another mech, ",-1,MinionType.mech),
                new Conditional("If you have another beast, ",-2,MinionType.mech),
                new Conditional("If you have another pirate, ",-1,MinionType.pirate),
                new Conditional("If you control no other minions, ",-2,MinionType.none),
                new Conditional("If you control at least two other minions, ",-1,MinionType.none),
                new Conditional("If you control at least four other minions, ",-2,MinionType.none),
                new Conditional("If your opponent has at least three minions, ",-2,MinionType.none),
                new Conditional("If you have ten mana crystals, ",-1,MinionType.none),
                new Conditional("If you control a secret, ",-1,MinionType.none),
                new Conditional("If you played a spell this turn, ",-1,MinionType.none), // MAGE
                new Conditional("If you have a weapon equipped, ",-1,MinionType.none), // WARRIOR
                new Conditional("If you're holding a card from another class, ",-1,MinionType.none), // ROGUE
                new Conditional("If you control a Treant, ",-1,MinionType.none), // DRUID
                new Conditional("If you discard this minion, ",-2,MinionType.none), // WARLOCK
                new Conditional("If your deck has no neutral cards, ",-1,MinionType.none), // PALADIN
                new Conditional("If you have overloaded mana crystals, ",-2,MinionType.none), // SHAMAN
                new Conditional("If your hand is empty, ",-1,MinionType.none), // HUNTER
                new Conditional("Outcast: ",-2,MinionType.none), // DEMON HUNTER
                // CAN'T WORK OUT A PRIEST ONE! GOOD CLASS IDENTITY, BLIZZARD
        };
    }

    public String getText() { return text; }
    public Integer getCost() { return cost; }
    public MinionType getType() { return type; }
    public Conditional[] getConditionals() { return conditionals; }
}
