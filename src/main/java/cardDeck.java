import java.util.Deque;

public class cardDeck{
    private final int deckNum;
    private final Deque<Card> cards;

    public int CardDeck(int deckNum){ // constructor creating new deck
        this.deckNum = deckNum;
    }

    public int getdeckNum(){ //method to get deck number
        return deckNum;
    }

    public synchronized Card drawTop(){ // gets card from top of deck (syncronised to stop deadlocking)
        top = cards.poll();
        return top;
    }
    public synchronized void addBottom(Card a){ // method to put card down into next deck
        cards.addLast(a);
    }
    public synchronized int getSize(){ // checks if deck is full and returns size of deck
        return cards.size();
    }

    public synchronized boolean isEmpty() { return cards.isEmpty(); } // checks if deck is empty

}