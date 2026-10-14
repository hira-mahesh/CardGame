
import java.util.Deque;
import java.util.ArrayDeque;

public class cardDeck {
    private final int deckNum;
    private final Deque<Card> cards;

    public cardDeck(int deckNum) { // constructor creating new deck
        this.deckNum = deckNum;
        this.cards = new ArrayDeque<>();
    }

    public int getdeckNum() { // method to get deck number
        return deckNum;
    }

    public synchronized Card drawTop() { // gets card from top of deck
        Card top = cards.poll();
        return top;
    }

    public synchronized void addBottom(Card a) { // method to put card at bottom of deck
        cards.addLast(a);
    }

    public synchronized int getSize() { // checks and returns size of deck
        return cards.size();
    }

    public synchronized boolean isEmpty() { // checks if deck is empty
        return cards.isEmpty();
    }
}
