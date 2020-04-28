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
    private Card newCard(){
        Random rng = new Random();
        Card newCard = new Card();
        newCard.setMana(rng.nextInt(10) + 1);
        return newCard;
    }

    // Creates a card with just stats, no description
    private Card makeVanillaCard(Card card){
        int[] cardStats = assignStats(card.getMana());

        card.setAttack(cardStats[0]);
        card.setHealth(cardStats[1]);

        return card;
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
        return stats;
    }

    /* Returns the data necessary to add "draw a card" to a card, given a budget to work with.
    Assumes drawing one card costs one mana. This is on the low side, but Blizzard are hardly above power creep.
    Returns an array with two values. Value 0 is the description, as a string. Value 1 is the cost of this effect. */
    private Object[] writeCardDraw(int budget){
        Random rng = new Random();
        int cost = 99;
        StringBuilder text = new StringBuilder("");
        while(cost > budget - 1) { // Making sure we do this within the cost *and* leave at least one mana left
            int drawCards = 0;
            drawCards = rng.nextInt(2) + 1; // Drawing no more than three cards right now - keep some sanity
            cost = drawCards; // Update this line to adjust the cost of drawing a card
            text.append("Draw ").append(numParse.get(drawCards)).append(" card");
            if(drawCards > 1) { text.append("s."); }
            else { text.append("."); }
        }
        return new Object[]{text.toString(),cost};
    }

    /* KEYWORD MAKERS */
    public static String makeBattlecryCard(int budget){
        return "hi";
    }



    // Parse a Card object in such a way that it can be read in the console
    private static String parseToPrint(Card card){
        return("Mana: " + card.getMana() + " Attack: " + card.getAttack() + " Health: " + card.getHealth() + " Description: " + card.getText());
    }

    public static void main(String[] args){
        CardBuilder cb = new CardBuilder();
        System.out.println(parseToPrint(cb.makeVanillaCard(cb.newCard())));
    }
}
