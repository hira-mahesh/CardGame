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
    private static  List<Integer> drain(CardDeck d)
    {
        List<Integer> out = new ArrayList<>();
        Card c;
        while((c= d.drawTop())!= null) {
        out.add(c.getValue());
        }
        return out;
    }
     @Test 
     public void deckIsFifo(){
        //adds cards of value 1,2,3 to the bottom
        CardDeck deck = new CardDeck(1);
        deck.addBottom(new Card(1));
        deck.addBottom(new Card(2));
        deck.addBottom(new Card(3)); 
        assertEquals(Arrays.asList(1,2,3), drain(deck));
     }
}
