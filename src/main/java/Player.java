
public class Player {
    private int playerNum;
    private Card[] hand;
    private String name;
    private int cardsInHand = 0;

    public Player(int playerNum) {
        this.playerNum = playerNum;
        this.name = "Player " + playerNum;
        this.hand = new Card[4]; // Makes hand array of 4
        this.cardsInHand = 0;
    }
    public String getName() {
        return name;
    }
    public int getPlayerNum() {
        return playerNum;
    }
    public synchronized Card[] getHand() {
        return hand.clone(); // retruns copy so callers cannot change the original array
    }
    public synchronized void addCard(Card card) {//check if hand is full
        if (cardsInHand >= hand.length) {
            throw new IllegalStateException("Player's hand is full.");
        }

        hand[cardsInHand] = card; // stores card in next position
        cardsInHand++; // adds number of cards in hand
    }
}
