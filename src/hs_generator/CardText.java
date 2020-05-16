package hs_generator;

public class CardText {
    // This object stores both the text of an effect and its cost. It is used instead of storing these as an array.
    private String text;
    private double cost;

    public CardText(String text, double cost) {
        this.text = text;
        this.cost = cost;
    }

    /* You don't KNOW how much I just want to make this object with public fields and leave it as that. It wouldn't be
    *that bad*. I'm just afraid that my Java 101 teacher will somehow find this and steal my degree away from me. */
    public String getText() { return text; }

    public double getCost() { return cost; }
}
