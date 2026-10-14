public class Player {
    private int playerNum;
    private int[] hand;
    private String name;
    private int cardsInHand = 0;
    public Player(int playerNum) {
        this.playerNum = playerNum;
        this.name = "Player " + playerNum;
        this.hand = new Card[4]; //makes hand array of 4
        this.cardsInHand = 0;
    }
    public String getName() {
        return name;
    }
    public int getPlayerNum() {
        return playerNum;
    }
     public synchronized Card[] getHand() {
        return hand.clone();// retruns copy so callers cannot change the original array
    }
     public synchronized void addCard(Card card) {
        if (cardsInHand >= hand.length) {
            throw new IllegalStateException("Player's hand is full."); //check if hand is full
        hand[cardsInHand] = card;// stores card in the next  position
        cardsInHand++; //increases cards in hand by one
        }
    }
}