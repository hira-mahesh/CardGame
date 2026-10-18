import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static org.junit.Assert.*;

public class CardTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();   // fresh directory for every test

    @Before
    public void resetSharedState() {
        Player.resetGame();   // gameOver and winnerNum are static, so every test must start clean
    }
    private Path directory() { 
        return folder.getRoot().toPath(); 
    }

 private Path writePack(int... values) throws IOException {
    StringBuilder sb = new StringBuilder();
    for (int v : values){
         sb.append(v).append('\n');
    }
    Path p = Files.createTempFile(directory(), "pack", ".txt");
    Files.writeString(p, sb);
    return p;
}

    private Path writeRaw(String content) throws IOException {
        Path p = Files.createTempFile(directory(), "raw", ".txt");
        Files.writeString(p, content);
        return p;
    }

    private static  List<Integer> drain(CardDeck d)
    {
        List<Integer> out = new ArrayList<>();
        Card c;
        while((c= d.drawTop())!= null) {
        out.add(c.getValue());
        }
        return out;
    }

   
    private int[] distinctPack(int n) {
        int[] pack = new int[8 * n];
        for (int i = 0; i < pack.length; i++) pack[i] = 100 + i;
        return pack;
    }
    private int[] winnablePack(int n) {
        int[] pack = distinctPack(n);
        pack[0] = 1; pack[n] = 1; pack[2 * n] = 1;
        pack[4 * n] = 1;
        return pack;
    }
    private void runAll(Player[] players, long timeoutMs) throws InterruptedException {
        Player.resetGame();
        Thread[] threads = new Thread[players.length];
        for (int i = 0; i < players.length; i++) {
            threads[i] = new Thread(players[i]);
            threads[i].start();
        }
        for (Thread t : threads) {
            t.join(timeoutMs);
            assertFalse("a player thread did not terminate (deadlock or no clean shutdown)", t.isAlive());
        }
    }

    private static List<Integer> handValues(Player p) {
        List<Integer> v = new ArrayList<>();
        for (Card c : p.getHand()) v.add(c.getValue());
        return v;
    }
    private List<String> readLines(String fileName) throws IOException {
        return Files.readAllLines(directory().resolve(fileName));
    }
    private static int countMatches(String text, String regex) {
        Matcher m = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(text);
        int c = 0;
        while (m.find()) c++;
        return c;
    }
    private static int totalInDecks(CardDeck[] ds, int i) {
        if (i == ds.length) return 0;
        synchronized (ds[i]) { return ds[i].getSize() + totalInDecks(ds, i + 1); }
    }


    //tests for CardDeck class
    @Test 
    public void deckIsFifo(){
        //adds cards of value 1,2,3 to the bottom
        CardDeck deck = new CardDeck(1);
        deck.addBottom(new Card(1));
        deck.addBottom(new Card(2));
        deck.addBottom(new Card(3)); 
        assertEquals(Arrays.asList(1,2,3), drain(deck)); //checks if the array is in right order
     }

     @Test 
     public void drawEmptyReturnsNull(){
        CardDeck deck = new CardDeck(1);
        assertNull(deck.drawTop());
        assertTrue(deck.isEmpty());
        assertEquals(0, deck.getSize());
     }

     @Test
    public void checkOutputTxt() {
        CardDeck d = new CardDeck(2);
        for (int v : new int[]{1, 3, 3, 7}) d.addBottom(new Card(v));
        assertEquals("1 3 3 7", d.toString());
        assertEquals(4, d.getSize());
        assertEquals("", new CardDeck(3).toString());
    }

    @Test
    public void addBottomWorks() throws Exception {
        final CardDeck deck = new CardDeck(1);
        int threads = 8;
        int perThread = 1000;
        Thread[] ts = new Thread[threads];
        for (int t = 0; t < threads; t++) {
            ts[t] = new Thread(() -> {
                for (int i = 0; i < perThread; i++) {
                    deck.addBottom(new Card(i));
                }
            });
            ts[t].start();
        }
        for (Thread t : ts) {
            t.join();
        }
        assertEquals(threads * perThread, deck.getSize());
    }
    @Test
    public void cardsDrawnCorrectly() throws Exception {
        final CardDeck deck = new CardDeck(1);
        int total = 4000;
        for (int i = 0; i < total; i++) {
            deck.addBottom(new Card(i));
        }

        final int[] counts = new int[total];
        Thread[] ts = new Thread[8];
        for (int t = 0; t < ts.length; t++) {
            ts[t] = new Thread(() -> {
                Card c;
                while ((c = deck.drawTop()) != null) {
                    synchronized (counts) {
                        counts[c.getValue()]++;
                    }
                }
            });
            ts[t].start();
        }
        for (Thread t : ts) {
            t.join();
        }

        for (int i = 0; i < total; i++) {
            assertEquals("card " + i + " was lost or drawn twice", 1, counts[i]);
        }
        assertTrue(deck.isEmpty());
    }


}
