public class Player {
    int playerNum;
    private String name;
    public Player(int playerNum){
        this.playerNum = playerNum;
        this.name = "Player " + playerNum;
    }
    public String getName() {
        return name;
    }
}