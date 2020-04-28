import java.util.Random;

public class CardBuilder {

    private Card makeVanillaCard(){
        Card newCard = new Card();
        int cardMana = generateMana();
        int[] cardStats = assignStats(cardMana);

        newCard.setMana(cardMana);
        newCard.setAttack(cardStats[0]);
        newCard.setHealth(cardStats[1]);

        return newCard;
    }

    // Static methods

    // Returns a random mana cost, from 1 to 10
    private static int generateMana(){
        Random rng = new Random();
        return rng.nextInt(10) + 1;
    }

    // Gets the vanilla stats budget, given a mana cost. Follows heuristic of mana cost * 2 + 1 for now
    private static int getVanilla(int mana){
        return mana * 2 + 1;
    }

    /* Randomly assigns stats between attack and health, given a mana budget, conforming to the vanilla rules (*2+1).
    This is done by repeatedly rolling a 50/50 chance for either attack or health until mana is depleted. */
    private static int[] assignStats(Integer mana){
        Random rng = new Random();
        int[] stats = {0,0};
        int remainingStats = getVanilla(mana); // The remaining stats left to distribute to attack or health
        for (; remainingStats > 0; remainingStats--) {
            if(rng.nextBoolean()){ // Roll to determine stat - true gives to attack, false to health
                stats[0] += 1;
            }
            else {
                stats[1] += 1;
            }
        }
        return stats;
    }

    // Parse a Card object in such a way that it can be read in the console
    private static String parseToPrint(Card card){
        return("Mana: " + card.getMana() + " Attack: " + card.getAttack() + " Health: " + card.getHealth());
    }

    public static void main(String[] args){
        CardBuilder cb = new CardBuilder();
        Card card = cb.makeVanillaCard();
        System.out.println(parseToPrint(card));

    }
}
