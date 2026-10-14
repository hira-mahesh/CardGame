public class Card{
    private final int value;
    public Card(int value){ // constructor for card
        this.value = value;
    }
    public synchronized int getValue(){ // method to assign cards value (syncronised helps with thread safety)
        return value;
    }
}