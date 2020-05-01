import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.HashMap;

public class CardBuilder {

    private HashMap<Integer, String> numParse;
    private Conditional[] conditionals;

    private CardBuilder() {
        numParse = new HashMap<>();
        numParse.put(1, "a");
        numParse.put(2, "two");
        numParse.put(3, "three");

        conditionals = new Conditional[]{new Conditional("",0,MinionType.none),
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

    // Generates a new blank card with a randomly assigned mana cost
    private Card newCard() {
        Random rng = new Random();
        Card newCard = new Card();
        newCard.setMana(rng.nextInt(10) + 1);
        newCard.setType(randomType());
        return newCard;
    }

    // Gets the vanilla stats budget, given a mana cost. Follows heuristic of mana cost * 2 + 1 for now
    private static int getVanilla(int mana) { return mana * 2 + 1; }

    /* Randomly assigns stats between attack and health, given a mana budget, conforming to the vanilla rules (*2+1).
    This is done by repeatedly rolling a 50/50 chance for either attack or health until mana is depleted.
    Returns an array with two values. Value 0 is the attack, value 1 is the health.
    This is the last step in card generation, so we just spend all of the remaining budget and do not return cost. */
    private static int[] assignStats(int budget) {
        Random rng = new Random();
        int[] stats = {0, 0};
        int remainingStats = getVanilla(budget); // The remaining stats left to distribute to attack or health
        for (; remainingStats > 0; remainingStats--) {
            if (rng.nextBoolean()) { // Roll to determine stat - true gives to attack, false to health
                stats[0] += 1;
            } else {
                stats[1] += 1;
            }
        }

        if (stats[1] == 0) { // We can't have less than 0 health, so steal a point back
            stats[0] -= 1;
            stats[1] += 1;
        }
        return stats;
    }

    // Choose a random minion type. Can be used for both new cards and descriptions. All types are equally weighted.
    private static MinionType randomType() {
        Random rng = new Random();
        int value = rng.nextInt(MinionType.values().length);
        switch (value) {
            case 0:
                return MinionType.none;
            case 1:
                return MinionType.beast;
            case 2:
                return MinionType.murloc;
            case 3:
                return MinionType.dragon;
            case 4:
                return MinionType.mech;
            case 5:
                return MinionType.elemental;
            case 6:
                return MinionType.pirate;
        }
        return MinionType.none; // This should never be fired, but the IDE will cry
    }

    // When given a type, picks randomly between it and the neutral type. Used to make card effects actually sensible.
    private static MinionType saneType(MinionType givenType) {
        Random rng = new Random();
        if(rng.nextBoolean()){
            return MinionType.none;
        }
        else{
            return givenType;
        }
    }


    private Conditional selectConditional(){
        Random rng = new Random();
        return conditionals[rng.nextInt(conditionals.length)];
    }

    // Parse a Card object in such a way that it can be read in the console
    private static String parseToPrint(Card card) {
        return ("Mana: " + card.getMana() + " Attack: " + card.getAttack() + " Health: " + card.getHealth() +
                " Type: " + card.getType().toString().substring(0, 1).toUpperCase() +
                card.getType().toString().substring(1).toLowerCase() + " Description: " + card.getText());
    }

    /* Returns the data necessary to add "draw a card" to a card, given a budget to work with.
    Assumes drawing one card costs one mana. This is on the low side, but Blizzard are hardly above power creep.
    Returns an array with two values. Value 0 is the description, as a string. Value 1 is the cost of this effect. */
    private CardText writeCardDraw(int budget, MinionType type) {
        Random rng = new Random();
        int cost = 0;
        String text = "";
        while (cost > budget - 1 || cost == 0) { // Making sure we do this within the cost *and* leave at least one mana left
            text = "";
            int drawCards;
            drawCards = rng.nextInt(2) + 1; // Drawing no more than two cards right now

            // Pick a minion type to draw
            MinionType drawType = saneType(type);

            cost = drawCards; // Update this line to adjust the cost of drawing a card
            text = text.concat("Draw ").concat(numParse.get(drawCards)).concat(" ");

            // Append the right word to description based upon minion type
            if (drawType == MinionType.none) {
                text = text.concat("card");
            } else {
                text = text.concat(drawType.toString());
                cost += 1; // Tutoring generally has more value
            }

            // Dealing with singular and plural cards
            if (drawCards > 1) {
                text = text.concat("s.");
            } else {
                text = text.concat(".");
            }
        }
        return new CardText(text, cost);
    }

    /* CARD MAKERS */

    // Creates a card with just stats, no description
    private Card makeVanillaCard() {
        Card card = this.newCard(); // Make a new card
        int[] cardStats = assignStats(card.getMana());

        card.setAttack(cardStats[0]);
        card.setHealth(cardStats[1]);

        return card;
    }

    private Card makeBattlecryCard() {
        Card card = this.newCard(); // Make a new card
        if (card.getMana() > 1) { // If the card costs more than one
            card.addToText("Battlecry: "); // Write the boilerplate
            Conditional conditional = this.selectConditional(); // Generate an initial conditional
            String textToAdd = conditional.getText(); // Initial conditions
            int costToAdd = conditional.getCost(); // Initial conditions
            boolean validType = (conditional.getType() == card.getType() || conditional.getType() == MinionType.none);
            // Note to future self - do while is a waste of time
            if (!validType){ // If we generated a conditional incompatible with the type of the minion
                while(conditional.getType() != card.getType() && conditional.getType() != MinionType.none){
                    conditional = this.selectConditional(); // Generate a new conditional
                    textToAdd = conditional.getText();
                    costToAdd = conditional.getCost();
                }
            }
            card.addToText(textToAdd); // Add a conditional effect
            card.spendBudget(costToAdd); // Give additional budget points for the effect
            CardText cardText = this.writeCardDraw(card.getBudget(),card.getType()); // Generate the card drawing text
            card.addToText(cardText.getText()); // Add the effect to the text
            card.spendBudget(cardText.getCost()); // Spend how much that cost on the card's budget
        }
        int[] stats = assignStats(card.getBudget()); // Assign stats based on remaining budget
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
            String userInput = reader.readLine();
            switch (userInput) {
                case "1":
                    for (int i = 0; i < 11; i++) {
                        System.out.println(parseToPrint(cb.makeVanillaCard()));
                    }
                    accepted = true;
                    break;
                case "2":
                    for (int i = 0; i < 11; i++) {
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
