import java.util.Deque;

public class cardDeck{
    private final int deckNum;
    private final Deque<Card> cards;
    private final Reentrant lock;

    public int CardDeck(int deckNum){ 
        this.deckNum = deckNum;
    }

    public int getdeckNum(){ //constructor for deckNum
        return deckNum;
    }
    void lock(){

    }
    void unlock(){

    }

    Card drawTop(){

    }
    void addBottom(Card a){

    }

}