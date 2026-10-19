
import java.util.Deque;
import java.util.ArrayDeque;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;

public class CardDeck {
    private final int deckNum;
    private final Deque<Card> cards;

    public CardDeck(int deckNum) { // constructor creating new deck
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
    public synchronized void writeDeckToFile() { // writes deck to file
        String fileName = "deck" + deckNum + "_output.txt";
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            writer.print("deck " + deckNum + " contents:");
            
            for (Card card : cards) {
                writer.print(" " + card.getValue());
            }
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    
    @Override
    public synchronized String toString() {
        StringBuilder deckContents = new StringBuilder();

        for (Card card : cards) {
            if (deckContents.length() > 0) {
                deckContents.append(" ");
            }

            deckContents.append(card.getValue());
        }

        return deckContents.toString();
    }

}
