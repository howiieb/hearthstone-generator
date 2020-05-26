package hs_generator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

public class CardBuilder {

    private final Conditional[] conditionals;
    private final Random rng;

    public CardBuilder() {
        rng = new Random();
        conditionals = new Conditional[]{new Conditional("",0,MinionType.none,false),
                new Conditional("If you are holding a dragon, ",-2,MinionType.dragon,false),
                new Conditional("If you played an elemental last turn, ",-3,MinionType.elemental,false),
                new Conditional("If you have another mech, ",-1,MinionType.mech,false),
                new Conditional("If you have another beast, ",-2,MinionType.beast,false),
                new Conditional("If you have another pirate, ",-1,MinionType.pirate,false),
                new Conditional("If you control no other minions, ",-2,MinionType.none,false),
                new Conditional("If you control at least two other minions, ",-1,MinionType.none,false),
                new Conditional("If you control at least four other minions, ",-2,MinionType.none,false),
                new Conditional("If your opponent has at least three minions, ",-2,MinionType.none,false),
                new Conditional("If you have ten mana crystals, ",-1,MinionType.none,false),
                new Conditional("If you control a secret, ",-1,MinionType.none,false),
                new Conditional("If you played a spell this turn, ",-1,MinionType.none,true), // MAGE
                new Conditional("If you played a secret this turn, ",-2,MinionType.none,true),
                new Conditional("If you have a weapon equipped, ",-1,MinionType.none,false), // WARRIOR
                new Conditional("If you have a damaged minion, ",-1,MinionType.none,false),
                new Conditional("If you're holding a card from another class, ",-1,MinionType.none,false), // ROGUE
                new Conditional("Combo: ",-2,MinionType.none,true),
                new Conditional("If you control a Treant, ",-1,MinionType.none,false), // DRUID
                new Conditional("If you have unspent mana, ",-1,MinionType.none,true),
                new Conditional("If you discard this minion, ",-3,MinionType.none,true), // WARLOCK
                new Conditional("If you have a demon, ",-2,MinionType.demon,false),
                new Conditional("If your deck has no neutral cards, ",-2,MinionType.none,false), // PALADIN
                new Conditional("If you have a Silver Hand Recruit, ",-1,MinionType.none,false),
                new Conditional("If you have overloaded mana crystals, ",-2,MinionType.none,true), // SHAMAN
                new Conditional("If you cast a spell last turn, ",-2,MinionType.none,true),
                new Conditional("If your hand is empty, ",-2,MinionType.none,false), // HUNTER
                new Conditional("If you have used your hero power this turn, ",-1,MinionType.none,true),
                new Conditional("Outcast: ",-2,MinionType.none,true), // DEMON HUNTER
                new Conditional("If you attacked this turn, ",-2,MinionType.none,true),
                new Conditional("If you restored health this turn, ",-2,MinionType.none,true), // PRIEST
                new Conditional("If you have cast a spell on a friendly minion this turn, ",-2,MinionType.none,true),
        };

    }

    // Generates a new blank card with a randomly assigned mana cost
    public Card newCard() {
        Card newCard = new Card();
        newCard.setMana(rng.nextInt(10) + 1);
        newCard.setType(this.randomType());
        return newCard;
    }

    // Gets the vanilla stats budget, given a mana cost. Follows heuristic of mana cost * 2 + 1 for now
    private static double getVanilla(double mana) { return mana * 2 + 1; }

    /* Randomly assigns stats between attack and health, given a mana budget, conforming to the vanilla rules (*2+1).
    This is done by repeatedly rolling a 50/50 chance for either attack or health until mana is depleted.
    Returns an array with two values. Value 0 is the attack, value 1 is the health.
    This is the last step in card generation, so we just spend all of the remaining budget and do not return cost. */
    private int[] assignStats(double budget) {
        int[] stats = {0, 0};
        double remainingStats = getVanilla(budget); // The remaining stats left to distribute to attack or health
        if(budget == 1 && remainingStats > 4) remainingStats = 4; // Nerf 1-drops
        if(budget % 1 != 0) { remainingStats += 1; } // If we have half a stat, give one more stat
        for (; remainingStats > 0; remainingStats--) {
            if(rng.nextBoolean()) { // Roll to determine stat - true gives to attack, false to health
                stats[0] += 1;
            } else stats[1] += 1;
        }

        if(stats[1] == 0) { // We can't have less than 1 health, so steal a point back
            stats[0] -= 1;
            stats[1] += 1;
        }
        return stats;
    }

    // Choose a random minion type. Can be used for both new cards and descriptions. All types are equally weighted.
    private MinionType randomType() {
        return MinionType.values()[rng.nextInt(MinionType.values().length)];
    }

    // When given a type, picks randomly between it and the neutral type. Used to make card effects actually sensible.
    private MinionType saneType(MinionType givenType) {
        if(rng.nextBoolean()) return MinionType.none;
        return givenType;
    }

    // Pick a random conditional
    private Conditional selectConditional(){
        return conditionals[rng.nextInt(conditionals.length)];
    }

    // Parse a hs_generator.Card object in such a way that it can be read in the console
    public String toString(Card card) {
        return ("Mana: " + card.getMana() + " Attack: " + card.getAttack() + " Health: " + card.getHealth() +
                " Type: " + card.getType().toString().substring(0, 1).toUpperCase() +
                card.getType().toString().substring(1).toLowerCase() + " Description: " + card.getText());
    }

    // EFFECT WRITERS

    /* Returns the data necessary to add "draw a card" to a card, given a budget to work with and the type of the minion.
    Min cost cost of drawing one card is 1.5 mana.
    Returns an object with two values - the description, as a string, and the cost of this effect. */
    private CardText writeCardDraw(double budget, MinionType type) {
        double effectCost = 0;
        String effectText = "";
        while (effectCost > budget - 0.5 || effectCost == 0) { // Making sure we do this within the cost *and* leave at least one mana left
            int drawCards = rng.nextInt(2) + 1; // Drawing no more than two cards right now

            // Pick a minion type to draw (50/50 split between neutral and the minion type specifically)
            MinionType drawType = this.saneType(type);

            effectCost = drawCards * 1.5; // Update this line to adjust the cost of drawing a card
            String numText = Integer.toString(drawCards);
            if(numText.equals("1")){ // Convert "one" to "a"
                numText = "a";
            }
            effectText = "draw ".concat(numText).concat(" ");

            // Append the right word to description based upon minion type
            if(drawType == MinionType.none) {
                effectText = effectText.concat("card");
            } else {
                effectText = effectText.concat(drawType.toString());
                effectCost += 1; // Tutoring generally has more value
            }

            // Dealing with singular and plural cards
            if(drawCards > 1) {
                effectText = effectText.concat("s.");
            } else {
                effectText = effectText.concat(".");
            }
        }
        return new CardText(effectText, effectCost);
    }

    private CardText writeDealDamage(double budget, boolean alwaysRandom){
        double effectCost = 0;
        String effectText = "";
        while(effectCost > budget - 0.5 || effectCost == 0) { // Making sure we do this within the cost *and* leave at least one mana left
            int damage = rng.nextInt(8) + 2;
            effectText = "deal ".concat(Integer.toString(damage)).concat(" damage");
            if(rng.nextBoolean() || alwaysRandom) { // Roll to decide if we attack random targets or a fixed target
                switch (rng.nextInt(3)){ // 0 = enemy, 1 = enemy minion, 2 = enemy hero
                    case 0:
                        effectCost = damage;
                        effectText = effectText.concat(" to a random enemy.");
                        break;
                    case 1:
                        effectCost = damage;
                        effectText = effectText.concat(" to a random enemy minion.");
                        break;
                    case 2:
                        effectCost = damage;
                        effectText = effectText.concat(" randomly split among enemies.");
                        break;
                }
            } else {
                if(rng.nextBoolean()) { // If we allow for face damage
                    effectCost = damage * 2;
                    effectText = effectText.concat(".");
                }
                else {
                    effectCost = damage * 1.5;
                    effectText = effectText.concat(" to an enemy minion.");
                }
            }
        }
        return new CardText(effectText,effectCost);
    }

    private CardText writeDealAOE(double budget){
        double effectCost = 0;
        String effectText = "";
        while(effectCost > budget - 0.5 || effectCost == 0) { // Making sure we do this within the cost *and* leave at least one mana left
            int damage = rng.nextInt(8) + 1;
            effectText = "deal ".concat(Integer.toString(damage)).concat(" damage");
            if(rng.nextBoolean()) {
                effectCost = damage * 2;
                effectText = effectText.concat(" to all minions.");
            }
            else {
                effectCost = damage * 2 + 1;
                effectText = effectText.concat(" to all enemy minions.");
            }
        }
        return new CardText(effectText,effectCost);
    }

    private CardText writeRestoreHealth(double budget, boolean alwaysRandom) {
        double effectCost = 0;
        String effectText = "";
        while (effectCost > budget - 0.5 || effectCost == 0) { // Making sure we do this within the cost *and* leave at least one mana left
            int health = rng.nextInt(8) + 2;
            effectText = "restore ".concat(Integer.toString(health)).concat(" health");
            if(rng.nextBoolean() || alwaysRandom) { // Roll to decide if this is targetable
                switch (rng.nextInt(2)) { // 0 = your hero, 1 = all allies
                    case 0:
                        effectCost = health * 0.5;
                        effectText = effectText.concat(" to your hero.");
                        break;
                    case 1:
                        effectCost = health * 1.5;
                        effectText = effectText.concat(" to all friendly characters.");
                        break;
                }
            } else {
                effectCost = health;
                effectText = effectText.concat(".");
            }
        }
        return new CardText(effectText,effectCost);
    }

    private CardText writeDiscover(MinionType type){
        if(type != MinionType.none) return new CardText("discover a ".concat(type.toString()).concat("."), 1);
        else {
            switch(rng.nextInt(3)) {
                case 0:
                    return new CardText("discover a spell.", 1);
                case 1:
                    return new CardText("discover a battlecry minion.", 1);
                case 2:
                    return new CardText("discover a deathrattle minion.", 1);
                default:
                    throw new ArithmeticException();
            }
        }
    }


    // Pick a random effect from the list
    private CardText writeRandomEffect(double budget, MinionType type, boolean alwaysRandom){
        int bound = 4;
        if(!alwaysRandom) bound += 1; // Only do discover for battlecry cards
        switch(rng.nextInt(bound)){
            case 0:
                if(budget > 1.5) return this.writeCardDraw(budget, type);
            case 1:
                if (budget > 2) return this.writeDealDamage(budget, alwaysRandom);
            case 2:
                if (budget > 0.5) return this.writeRestoreHealth(budget, alwaysRandom);
            case 3:
                if (budget > 2) return this.writeDealAOE(budget);
            case 4:
                if (budget > 1) return this.writeDiscover(type);
            default:
                return(new CardText("",0));
        }
    }

    private void addKeyword(Card card){
        if(rng.nextBoolean()){ // 50/50 chance of adding a new keyword
            CardText[] keywords = {new CardText("Taunt. ",0.5),
                    new CardText("Divine Shield. ",1.5),
                    new CardText("Windfury. ",2),
                    new CardText("Rush. ",1),
                    new CardText("Poisonous. ",1.5),};
            CardText toAdd = keywords[rng.nextInt(keywords.length)];
            card.addToText(toAdd.getText());
            card.spendBudget(toAdd.getCost());
        }
    }

    // CARD MAKERS

    private Card makeVanillaCard() {
        Card card = this.newCard(); // Make a new card
        int[] cardStats = this.assignStats(card.getMana());

        card.setAttack(cardStats[0]);
        card.setHealth(cardStats[1]);

        return card;
    }

    private void addConditional(Card card, boolean battlecry) {
        Conditional conditional = this.selectConditional(); // Generate an initial conditional
        while(!battlecry && conditional.isBattlecryOnly()){
            conditional = this.selectConditional();
        }
        String condText = conditional.getText(); // Initial conditions
        int costToAdd = conditional.getCost(); // Initial conditions
        boolean validType = (conditional.getType() == card.getType() || conditional.getType() == MinionType.none);
        if(!validType){ // If we generated a conditional incompatible with the type of the minion
            card.setType(this.saneType(conditional.getType())); // Change the type of the minion to be valid
        }
        card.addToText(condText); // Add a conditional to the text
        card.spendBudget(costToAdd); // Give additional budget points for the effect
    }


    public Card makeBattlecryCard() {
        Card card = this.newCard(); // Make a new card
        this.addKeyword(card);
        card.addToText("Battlecry: "); // Write the boilerplate
        addConditional(card, true);
        CardText cardText = this.writeRandomEffect(card.getBudget(), card.getType(), false);
        card.addToText(cardText.getText()); // Add the effect to the text
        card.spendBudget(cardText.getCost()); // Spend how much that cost on the card's budget
        int[] stats = this.assignStats(card.getBudget()); // Assign stats based on remaining budget
        card.setAttack(stats[0]); // Give those stats to the card itself
        card.setHealth(stats[1]);
        return card;
    }

    public Card makeDeathrattleCard() {
        Card card = this.newCard(); // Make a new card
        card.spendBudget(-1); // Deathrattles generally seem to be weaker
        this.addKeyword(card);
        card.addToText("Deathrattle: "); // Write the boilerplate
        addConditional(card, false);
        return addEffectAlwaysRandom(card);
    }

    private Card addEffectAlwaysRandom(Card card) {
        CardText cardText = this.writeRandomEffect(card.getBudget(), card.getType(), true);
        card.addToText(cardText.getText()); // Add the effect to the text
        card.spendBudget(cardText.getCost()); // Spend how much that cost on the card's budget
        int[] stats = this.assignStats(card.getBudget()); // Assign stats based on remaining budget
        card.setAttack(stats[0]); // Give those stats to the card itself
        card.setHealth(stats[1]);
        return card;
    }

    private Card makeEndTurnCard() {
        Card card = this.newCard();
        card.spendBudget(1.5); // End of turn is fairly strong
        this.addKeyword(card);
        card.addToText("At the end of your turn, "); // Write the boilerplate
        return addEffectAlwaysRandom(card);
    }


    public static void main(String[] args) throws IOException {
        CardBuilder cb = new CardBuilder();
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(System.in));
        boolean accepted = false;
        System.out.println("Enter a value to generate a card\n1 - Vanilla\n2 - Battlecry\n3 - Deathrattle\n4 - End turn");
        while (!accepted) {
            String cardType = reader.readLine();
            System.out.println("How many?");
            String cardCount = reader.readLine();
            switch (cardType) {
                case "1":
                    for (int i = 0; i < Integer.parseInt(cardCount); i++) {
                        System.out.println(cb.toString(cb.makeVanillaCard()));
                    }
                    accepted = true;
                    break;
                case "2":
                    for (int i = 0; i < Integer.parseInt(cardCount); i++) {
                        System.out.println(cb.toString(cb.makeBattlecryCard()));
                    }
                    accepted = true;
                    break;
                case "3":
                    for (int i = 0; i < Integer.parseInt(cardCount); i++) {
                        System.out.println(cb.toString(cb.makeDeathrattleCard()));
                    }
                    accepted = true;
                    break;
                case "4":
                    for (int i = 0; i < Integer.parseInt(cardCount); i++) {
                        System.out.println(cb.toString(cb.makeEndTurnCard()));
                    }
                    accepted = true;
                    break;
                default:
                    System.out.println("Invalid input");
                    break;
            }

        }
    }
}
