import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.HashMap;

public class CardBuilder {

    private Conditional[] conditionals;
    private Random rng;

    private CardBuilder() {
        rng = new Random();
        conditionals = new Conditional[]{new Conditional("",0,MinionType.none),
                new Conditional("If you are holding a dragon, ",-2,MinionType.dragon),
                new Conditional("If you played an elemental last turn, ",-3,MinionType.elemental),
                new Conditional("If you have another mech, ",-1,MinionType.mech),
                new Conditional("If you have another beast, ",-2,MinionType.beast),
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
                new Conditional("If you discard this minion, ",-3,MinionType.none), // WARLOCK
                new Conditional("If your deck has no neutral cards, ",-1,MinionType.none), // PALADIN
                new Conditional("If you have overloaded mana crystals, ",-2,MinionType.none), // SHAMAN
                new Conditional("If your hand is empty, ",-1,MinionType.none), // HUNTER
                new Conditional("Outcast: ",-2,MinionType.none), // DEMON HUNTER
                // CAN'T WORK OUT A PRIEST ONE! GOOD CLASS IDENTITY, BLIZZARD
        };

    }

    // Generates a new blank card with a randomly assigned mana cost
    private Card newCard() {
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
        if(budget % 1 != 0) { remainingStats += 1; } // If we have half a stat, give one more stat
        for (; remainingStats > 0; remainingStats--) {
            if (rng.nextBoolean()) { // Roll to determine stat - true gives to attack, false to health
                stats[0] += 1;
            } else {
                stats[1] += 1;
            }
        }

        if (stats[1] == 0) { // We can't have less than 1 health, so steal a point back
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
        if(rng.nextBoolean()){
            return MinionType.none;
        }
        else{
            return givenType;
        }
    }

    // Pick a random conditional
    private Conditional selectConditional(){
        return conditionals[rng.nextInt(conditionals.length)];
    }

    // Parse a Card object in such a way that it can be read in the console
    private static String parseToPrint(Card card) {
        return ("Mana: " + card.getMana() + " Attack: " + card.getAttack() + " Health: " + card.getHealth() +
                " Type: " + card.getType().toString().substring(0, 1).toUpperCase() +
                card.getType().toString().substring(1).toLowerCase() + " Description: " + card.getText());
    }

    /* Returns the data necessary to add "draw a card" to a card, given a budget to work with and the type of the minion.
    Current cost of drawing one card is 1.5 mana.
    Returns an object with two values - the description, as a string, and the cost of this effect. */
    private CardText writeCardDraw(double budget, MinionType type) {
        double effectCost = 0;
        String effectText = "";
        while (effectCost > budget - 1 || effectCost == 0) { // Making sure we do this within the cost *and* leave at least one mana left
            effectText = "";
            int drawCards = rng.nextInt(2) + 1; // Drawing no more than two cards right now

            // Pick a minion type to draw (50/50 split between neutral and the minion type specifically)
            MinionType drawType = this.saneType(type);

            effectCost = drawCards * 1.5; // Update this line to adjust the cost of drawing a card
            String numText = Integer.toString(drawCards);
            if (numText.equals("1")){ // Convert "one" to "a"
                numText = "a";
            }
            effectText = effectText.concat("draw ").concat(numText).concat(" ");

            // Append the right word to description based upon minion type
            if (drawType == MinionType.none) {
                effectText = effectText.concat("card");
            } else {
                effectText = effectText.concat(drawType.toString());
                effectCost += 1; // Tutoring generally has more value
            }

            // Dealing with singular and plural cards
            if (drawCards > 1) {
                effectText = effectText.concat("s.");
            } else {
                effectText = effectText.concat(".");
            }
        }
        return new CardText(effectText, effectCost);
    }

    private CardText writeDealDamage(double budget, MinionType type){
        double effectCost = 0;
        String effectText = "";
        while(effectCost > budget - 1 || effectCost == 0) { // Making sure we do this within the cost *and* leave at least one mana left
            effectText = "";
            int damage = rng.nextInt(8) + 1;
            effectText = effectText.concat("deal ").concat(Integer.toString(damage)).concat(" damage ");
            if (rng.nextBoolean()) { // Roll to decide if we attack random targets or a fixed target
                switch (rng.nextInt(3)){ // 0 = enemy, 1 = enemy minion, 2 = enemy hero
                    case 0:
                        effectCost = damage;
                        effectText = effectText.concat("to a random enemy.");
                        break;
                    case 1:
                        effectCost = damage;
                        effectText = effectText.concat("to a random enemy minion.");
                        break;
                    case 2:
                        effectCost = damage * 2;
                        effectText = effectText.concat("to the enemy hero.");
                        break;
                }
            }
            else {
                if(rng.nextBoolean()) { // If we allow for face damage
                    effectCost = damage * 2;
                    effectText = effectText.concat("to an enemy.");
                }
                else{
                    effectCost = damage * 1.5;
                    effectText = effectText.concat("to an enemy minion.");
                }
            }
        }
        return new CardText(effectText,effectCost);
    }

    /* CARD MAKERS */

    // Creates a card with just stats, no description
    private Card makeVanillaCard() {
        Card card = this.newCard(); // Make a new card
        int[] cardStats = this.assignStats(card.getMana());

        card.setAttack(cardStats[0]);
        card.setHealth(cardStats[1]);

        return card;
    }

    private Card makeBattlecryCard() {
        Card card = this.newCard(); // Make a new card
        if (card.getMana() > 1) { // If the card costs more than one
            card.addToText("Battlecry: "); // Write the boilerplate
            Conditional conditional = this.selectConditional(); // Generate an initial conditional
            String condText = conditional.getText(); // Initial conditions
            int costToAdd = conditional.getCost(); // Initial conditions
            boolean validType = (conditional.getType() == card.getType() || conditional.getType() == MinionType.none);
            if (!validType){ // If we generated a conditional incompatible with the type of the minion
                card.setType(this.saneType(conditional.getType())); // Change the type of the minion to be valid
            }
            card.setConditional(conditional); // Add a conditional effect
            card.addToText(condText); // Add it to the text
            card.spendBudget(costToAdd); // Give additional budget points for the effect

            CardText cardText = this.writeDealDamage(card.getBudget(),this.saneType(card.getType())); // Generate the card drawing text (50/50 chance of drawing the conditional type)
            card.addToText(cardText.getText()); // Add the effect to the text
            card.spendBudget(cardText.getCost()); // Spend how much that cost on the card's budget
        }
        int[] stats = this.assignStats(card.getBudget()); // Assign stats based on remaining budget
        card.setAttack(stats[0]); // Give those stats to the card itself
        card.setHealth(stats[1]);
        return card;
    }

    public static void main(String[] args) throws IOException {
        CardBuilder cb = new CardBuilder();
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(System.in));
        boolean accepted = false;
        System.out.println("Card generator. Enter a value to generate a card\n 1 - Vanilla\n 2 - Battlecry\n");
        while (!accepted) {
            String cardType = reader.readLine();
            System.out.println("How many?");
            String cardCount = reader.readLine();
            switch (cardType) {
                case "1":
                    for (int i = 0; i < Integer.parseInt(cardCount); i++) {
                        System.out.println(parseToPrint(cb.makeVanillaCard()));
                    }
                    accepted = true;
                    break;
                case "2":
                    for (int i = 0; i < Integer.parseInt(cardCount); i++) {
                        System.out.println(parseToPrint(cb.makeBattlecryCard()));
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
