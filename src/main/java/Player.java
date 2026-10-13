public class Player {
    private int playerNum;
    private int[] hand;
    private String name;
    private int cardsInHand = 0;
    public Player(int playerNum) {
        this.playerNum = playerNum;
        this.name = "Player " + playerNum;
        this.hand = new int[4];
    }
    public String getName() {
        return name;
    }
    public int getPlayerNum() {
        return playerNum;
    }
    public int[] getHand() {
        return hand;
    }
    public void addCard(int card) {
        hand[cardsInHand] = card;
        cardsInHand++;
    }
}