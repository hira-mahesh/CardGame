public class Player {
    private int playerNum;
    private int[] hand;
    private String name;
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
}