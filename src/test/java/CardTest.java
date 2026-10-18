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
    public TemporaryFolder folder = new TemporaryFolder();   // creates a temp  directory for every test

    @Before //execute the method before each test method
    public void resetSharedState() {
        Player.resetGame();   // gameOver and winnerNum are static, so every test must start clean
    }
    private Path directory() { //returns  path of the temp directory
        return folder.getRoot().toPath(); 
    }

 private Path writePack(int... values) throws IOException { //creates a temp pack file with one integer on each line
    StringBuilder sb = new StringBuilder();
    for (int v : values){
         sb.append(v).append('\n');
    }
    Path p = Files.createTempFile(directory(), "pack", ".txt");
    Files.writeString(p, sb);
    return p;
}

    private Path writeRaw(String content) throws IOException { //takes string instead
        Path p = Files.createTempFile(directory(), "raw", ".txt");
        Files.writeString(p, content);
        return p;
    }

    private static  List<Integer> drain(CardDeck d) //gets every card from  deck and adds values in list
    {
        List<Integer> out = new ArrayList<>();
        Card c;
        while((c= d.drawTop())!= null) {
        out.add(c.getValue());
        }
        return out;
    }

   
    private int[] distinctPack(int n) { //makes  array which represnts a pack where every card has a different value
        int[] pack = new int[8 * n];
        for (int i = 0; i < pack.length; i++) pack[i] = 100 + i;
        return pack;
    }
    private int[] winnablePack(int n) { //create a pack containing four cards with the same value ;helps test if a player can win
        int[] pack = distinctPack(n);
        pack[0] = 1; pack[n] = 1; pack[2 * n] = 1;
        pack[4 * n] = 1;
        return pack;
    }
    private void runAll(Player[] players, long timeoutMs) throws InterruptedException { //starts all the players at the same time and waits for them to finish, then checks that no threads still awake after waiting period
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

    private static List<Integer> handValues(Player p) { //takes players hand and returns list of values of the cards in hand.
        List<Integer> v = new ArrayList<>();
        for (Card c : p.getHand()) v.add(c.getValue());
        return v;
    }
    private List<String> readLines(String fileName) throws IOException { //reads every line from a file in the temp directory and returns as strings
        return Files.readAllLines(directory().resolve(fileName));
    }
    private static int countMatches(String text, String regex) { //counts how many times  regex matches  string
        Matcher m = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(text);
        int c = 0;
        while (m.find()) c++;
        return c;
    }
    private static int totalInDecks(CardDeck[] ds, int i) { //the total number of cards across all the decks in CardDeck array.
        if (i == ds.length) return 0;
        synchronized (ds[i]) { return ds[i].getSize() + totalInDecks(ds, i + 1); }
    }


    //tests for CardDeck class
    @Test 
    public void deckIsFifo(){ //checks if deck follows First In, First Out order
        //adds cards of value 1,2,3 to the bottom
        CardDeck deck = new CardDeck(1);
        deck.addBottom(new Card(1));
        deck.addBottom(new Card(2));
        deck.addBottom(new Card(3)); 
        assertEquals(Arrays.asList(1,2,3), drain(deck)); //checks if the array is in right order
     }

     @Test 
     public void drawEmptyReturnsNull(){ //checks what happens when you try drawing from empty deck
        CardDeck deck = new CardDeck(1);
        assertNull(deck.drawTop());
        assertTrue(deck.isEmpty());
        assertEquals(0, deck.getSize());
     }

     @Test
    public void checkOutputTxt() { //checks the text representation of a deck and the size
        CardDeck d = new CardDeck(2);
        for (int v : new int[]{1, 3, 3, 7}) d.addBottom(new Card(v));
        assertEquals("1 3 3 7", d.toString());
        assertEquals(4, d.getSize());
        assertEquals("", new CardDeck(3).toString());
    }

    @Test
    public void addBottomWorks() throws Exception { //checks if multiple threads can add cards to the same deck without losing added dards
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
    public void cardsDrawnCorrectly() throws Exception { //check if CardDeck works if multiple threads draw deck at the same time
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

    //player tests
    @Test
    public void handRejectsAFifthCard() {
        final Player p = new Player(1);
        for (int i = 0; i < 4; i++) p.addCard(new Card(i));
        assertThrows(IllegalStateException.class, () -> p.addCard(new Card(9)));
    }
    @Test
    public void getHandReturnsACopy() {
        Player p = new Player(1);
        for (int i = 0; i < 4; i++) p.addCard(new Card(i));
        p.getHand()[0] = null;                         // tamper with the copy
        assertEquals("0 1 2 3", p.getHandString());    // original untouched
    }
    @Test
    public void checkWinningHand() {
        for (int value : new int[]{1, 7}) {            // 1 is player 1's preferred value, 7 is not
            Player p = new Player(1);
            for (int i = 0; i < 4; i++) p.addCard(new Card(value));
            assertEquals(value + " " + value + " " + value + " " + value, p.getHandString());
        }
    }
    @Test
    public void testBadHands() {
        Player p = new Player(1);
        for (int v : new int[]{1, 1, 1}) p.addCard(new Card(v));
        assertEquals("1 1 1", p.getHandString());
        p.addCard(new Card(2));
        assertEquals("1 1 1 2", p.getHandString());
    }

    // input validation tests
    @Test
    public void acceptsAValidPack() throws IOException {
        assertTrue(CardGame.checkValidFile(writePack(distinctPack(2)).toString(), 2));
    }
    @Test
    public void rejectsNot8n() throws IOException {
        assertFalse(CardGame.checkValidFile(writePack(distinctPack(2)).toString(), 3));
        assertFalse(CardGame.checkValidFile(writePack(distinctPack(3)).toString(), 2));
    }
    @Test
    public void checkInvalidLines() throws IOException {
        String line = "1\n";
        StringBuilder base = new StringBuilder();
        for (int i = 0; i < 15; i++) base.append(line);
        assertFalse(CardGame.checkValidFile(writeRaw(base + "abc\n").toString(), 2));
        assertFalse(CardGame.checkValidFile(writeRaw(base + "-3\n").toString(), 2));
        assertFalse(CardGame.checkValidFile(writeRaw(base + "\n").toString(), 2));
        assertFalse(CardGame.checkValidFile(writeRaw(base + "1.5\n").toString(), 2));
        assertFalse(CardGame.checkValidFile(writeRaw(base + "99999999999\n").toString(), 2));
    }
    @Test
    public void checksFileAndPlayerCount() throws IOException {
        assertFalse(CardGame.checkValidFile(directory().resolve("impossible.txt").toString(), 2));
        Path ok = writePack(distinctPack(1));
        assertFalse(CardGame.checkValidFile(ok.toString(), 0));
        assertFalse(CardGame.checkValidFile(ok.toString(), -1));
    }
    @Test
    public void playerCountValidation() {
        assertEquals(4, CardGame.checkValidPlayerNum(new Scanner("4")));
        assertEquals(4, CardGame.checkValidPlayerNum(new Scanner(" 4 ")));
        assertEquals(3, CardGame.checkValidPlayerNum(new Scanner("0\n3")));
        assertEquals(3, CardGame.checkValidPlayerNum(new Scanner("-2\n3")));
        assertEquals(3, CardGame.checkValidPlayerNum(new Scanner("four\n3")));
    }
    

}
