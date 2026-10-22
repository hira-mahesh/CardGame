
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Player extends Thread{ //defines player as thread which allows each player to tun at the same time
    private int playerNum;
    private Card[] hand;
    private int cardsInHand;
    private CardDeck deckToDrawFrom;//stores deck from player draws cards
    private CardDeck deckToAddTo;//stores deck from player discards cards
    private PrintWriter outputFile; //writer to write the output file

    // shared flag ; alerts other threads when one player wins
    //volatile makes sure the changes are visible to the other threads
    private static volatile boolean gameOver = false;

    public Player(int playerNum) { //constructor to make player with specified player number
        super("Player "+ playerNum); // super calls  thread constructor to give thread a name.
        this.playerNum = playerNum; //stores payer num in object
        this.hand = new Card[4]; // Makes hand array of 4
        this.cardsInHand = 0; //player starts with 0 cards before dealt

        try { //creates player output file
            outputFile = new PrintWriter( new FileWriter("player" + playerNum + "_output.txt"));// makes a file named playerN_output.txt
        } catch (IOException e) { //handles errors when making output file
            throw new RuntimeException("Could not create player output file.", e);
        }
    }

    public int getPlayerNum() { //getter method
        return playerNum;// returns the value stored in playerNum.
    }
    public synchronized Card[] getHand() { //getter method, synchronised makes sure only one thread can access at a time
        return hand.clone(); // retruns copy so callers cannot change the original array
    }
    
    public synchronized void addCard(Card card) {//check if hand is full synchronised the hand while the method is running
        if (cardsInHand >= hand.length) {
            throw new IllegalStateException("Player's hand is full.");
        }
                hand[cardsInHand] = card; // stores card in next position
        cardsInHand++; // adds number of cards in hand
    }

    // Returns the player's cards as a string of numbers
    //  method is private because it is only used inside this class
    // synchronized protects hand while contents are read
    private synchronized String handToString() {
        StringBuilder handString = new StringBuilder(); // creates StringBuilder to build the hand string

        for (int i = 0; i < cardsInHand; i++) { //loops through cards
            if (i > 0) { //checks if this is not the first card in the string
                handString.append(" ");// adds space between card values
            }
            handString.append(hand[i].getValue());// adds the current card value to the string
        }
        return handString.toString();// retruns string of card values
    }
    //getter method that retruns players hand as string
    public synchronized String getHandString() {
        return handToString();
    }
    // assigns decks that  player will use during the game
    public void setDecks(CardDeck deckToDrawFrom, CardDeck deckToAddTo){//tells player which deck to use
        this.deckToDrawFrom = deckToDrawFrom; // stores draw deck in player field
        this.deckToAddTo = deckToAddTo; // stores add deck in player field
    }

     // checking if  player has won
    private synchronized boolean hasWon() {
        if (cardsInHand != 4) { //checks if player has 4 cards in deck
            return false;
        }

        for (Card card : hand) { //checks if each card is equal to the player number
            if (card.getValue() != playerNum) {
                return false;
            }
        }

        return true;
    }

    //replacing a card in a hand
     private synchronized Card replaceCard(Card newCard) {
        List<Integer> options = new ArrayList<>(); //array of all the non preferred cards
        for (int i = 0; i < hand.length; i++) {//loops through hands in hand
            // discards card thats not preferred value
            if (hand[i].getValue() != playerNum) { //checks if cards value is differnt to players number
                options.add(i);//adds to list of non preferred card
            }

           
        }
        if(options.isEmpty()) //checks if every card matches player card
                {
                    return newCard; //doesnt change hand and returns the new card
                }
        int i = options.get(ThreadLocalRandom.current().nextInt(options.size())); //ranodmly chooses from the positions with non prefferred card
        Card discardedCard = hand[i];//saves reference to old card to return 
        hand[i] = newCard; //adds new card in position
        return discardedCard; //returns discarded card to add to deck
     
    }
    //one turn for the player
    public void turn() {

        Card drawnCard = deckToDrawFrom.drawTop(); //draws top card from players assigned deck

        if (drawnCard == null) { //checks if deck was empty and no card could be drawn
            Thread.yield(); //gives another thread opportunity to run
            return; //ends turn becuase no card drawn
        }

        Card discardedCard = replaceCard(drawnCard); //replces non preferred card and gets discarded card
        deckToAddTo.addBottom(discardedCard); //adds discarded card to bottom of the add deck
        // writes the draw , discard and current hand to the file
        outputFile.println("player " + playerNum + " draws a " + drawnCard.getValue()  + " from deck " + deckToDrawFrom.getdeckNum());
        outputFile.println("player " + playerNum + " discards a " + discardedCard.getValue() + " to deck " + deckToAddTo.getdeckNum());
        outputFile.println("player " + playerNum + " current hand is " + getHandString());
        outputFile.flush(); // writes to file immediatly
        System.out.println("player " + playerNum + " draws a " + drawnCard.getValue()  + " from deck " + deckToDrawFrom.getdeckNum()  + " and discards a " + discardedCard.getValue()
                + " to deck " + deckToAddTo.getdeckNum()); //prints to terminal

        System.out.println("player " + playerNum  + " current hand is " + getHandString());

        if (hasWon()) {// checks if  player wins after finsihing the turn.
            declareWin();//announces winner and marks game as over
            return; //ends turn as player has won
        }

        try { //pauses player thread for one second
            Thread.sleep(1000);
        } catch (InterruptedException e) { //handles interruption whilst thread is asleep
            Thread.currentThread().interrupt(); //restores status of current thread
        }
    }
    
    //writes player initial hand to output file
    public void writeInitialHand() {// writes player number and start hand
        outputFile.println("player " + playerNum + " initial hand " + getHandString());
        outputFile.flush();//flushes writer so inital hand is saved
        }
    @Override //  actions when the player thread starts.
    public void run() {
        //checks for a starting win
        if (hasWon()) {  // announces  winner and ends the game.
            declareWin();
            return; //ends thread as player has won
        }

        // keeps playing until a player has won and the current thread has not been interrupted
        while (!gameOver && !Thread.currentThread().isInterrupted()) {
        turn(); //performs a turn for the player
        }

    }

    // announces winner and stops game
    private void declareWin() {
        synchronized (Player.class) {//locks the player class so only one thread can declare win
            if (!gameOver) {//checks if another player has already ended the game
                gameOver = true; //sets shared flag to show game has ended
                System.out.println(getName() + " wins"); //prints nme of winning player
            }
        }
    }

    // resters shared flag before making new game
    public static void resetGame() {
        gameOver = false; //sets sared flag to flase so players can start playing again 
    }
}



