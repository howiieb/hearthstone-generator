import java.util.Random;
import java.util.HashMap;

public class CardBuilder {

    private HashMap<Integer,String> numParse;

    private CardBuilder(){
        numParse = new HashMap<>();
        numParse.put(1,"a");
        numParse.put(2,"two");
        numParse.put(3,"three");
    }

    // Generates a new blank card with a randomly assigned mana cost
    private static Card newCard(){
        Random rng = new Random();
        Card newCard = new Card();
        newCard.setMana(rng.nextInt(10) + 1);
        return newCard;
    }

    /* STATIC METHODS */

    // Gets the vanilla stats budget, given a mana cost. Follows heuristic of mana cost * 2 + 1 for now
    private static int getVanilla(int mana){
        return mana * 2 + 1;
    }

    /* Randomly assigns stats between attack and health, given a mana budget, conforming to the vanilla rules (*2+1).
    This is done by repeatedly rolling a 50/50 chance for either attack or health until mana is depleted.
    Returns an array with two values. Value 0 is the attack, value 1 is the health.
    This is the last step in card generation, so we just spend all of the remaining budget and do not return cost. */
    private static int[] assignStats(int budget){
        Random rng = new Random();
        int[] stats = {0,0};
        int remainingStats = getVanilla(budget); // The remaining stats left to distribute to attack or health
        for (; remainingStats > 0; remainingStats--) {
            if(rng.nextBoolean()){ // Roll to determine stat - true gives to attack, false to health
                stats[0] += 1;
            }
            else{
                stats[1] += 1;
            }
        }

        if(stats[1] == 0){ // We can't have less than 0 health, so steal a point back
            stats[0] -= 1;
            stats[1] += 1;
        }
        return stats;
    }

    // Parse a Card object in such a way that it can be read in the console
    private static String parseToPrint(Card card){
        return("Mana: " + card.getMana() + " Attack: " + card.getAttack() + " Health: " + card.getHealth() + " Description: " + card.getText());
    }

    /* Returns the data necessary to add "draw a card" to a card, given a budget to work with.
    Assumes drawing one card costs one mana. This is on the low side, but Blizzard are hardly above power creep.
    Returns an array with two values. Value 0 is the description, as a string. Value 1 is the cost of this effect. */
    private CardText writeCardDraw(int budget){
        Random rng = new Random();
        int cost = 0;
        String text = "";
        if (budget > 1){ // Not even going to try and give this effect to a 1 cost - although watch blizzard do this in 2020
            while(cost > budget - 1 || cost == 0) { // Making sure we do this within the cost *and* leave at least one mana left
                text = "";
                int drawCards;
                drawCards = rng.nextInt(3) + 1; // Drawing no more than three cards right now - keep some sanity
                cost = drawCards; // Update this line to adjust the cost of drawing a card
                text = text.concat("Draw ").concat(numParse.get(drawCards)).concat(" card");
                if (drawCards > 1) {
                    text = text.concat("s.");
                } else {
                    text = text.concat(".");
                }
            }
        }
        return new CardText(text,cost);
    }

    /* CARD MAKERS */

    // Creates a card with just stats, no description
    private Card makeVanillaCard(Card card){
        int[] cardStats = assignStats(card.getMana());

        card.setAttack(cardStats[0]);
        card.setHealth(cardStats[1]);

        return card;
    }

    private Card makeBattlecryCard(){
        Card card = newCard(); // Make a new card
        CardText cardText = this.writeCardDraw(card.getMana()); // Generate the card drawing text (using the card's cost)
        if (!(cardText.getText().equals(""))) {
            card.addToText("Battlecry: "); // Write the boilerplate
            card.addToText(cardText.getText()); // Add it to the text
        }

        card.spendBudget(cardText.getCost()); // Spend how much that cost on the card's budget
        int[] stats = assignStats(card.getBudget()); // Assign stats based on remaining budget
        card.setAttack(stats[0]); // Give those stats to the card itself
        card.setHealth(stats[1]);
        return card;
    }

    public static void main(String[] args){
        CardBuilder cb = new CardBuilder();
        for (int i = 0; i < 11; i++) {
            System.out.println(parseToPrint(cb.makeBattlecryCard()));
        }

    }
}
