
public class Player extends Thread{
    private int playerNum;
    private Card[] hand;
    private int cardsInHand;
    private CardDeck deckToDrawFrom;
    private CardDeck deckToAddTo;

    // shared flag ; alerts other threads when one player wins
    private static volatile boolean gameOver = false;

    public Player(int playerNum) {
        super("Player "+ playerNum); // super calls thread class
        this.playerNum = playerNum;
        this.hand = new Card[4]; // Makes hand array of 4
        this.cardsInHand = 0; //player starts with 0 cards before dealt
    }

    public int getPlayerNum() { //getter method
        return playerNum;
    }
    public synchronized Card[] getHand() { //getter method, synchronised makes sure only one thread can access at a time
        return hand.clone(); // retruns copy so callers cannot change the original array
    }
    
    public synchronized void addCard(Card card) {//check if hand is full
        if (cardsInHand >= hand.length) {
            throw new IllegalStateException("Player's hand is full.");
        }
                hand[cardsInHand] = card; // stores card in next position
        cardsInHand++; // adds number of cards in hand
    }

         // Returns the player's cards as a string of numbers
    private synchronized String handToString() {
        StringBuilder handString = new StringBuilder();

        for (int i = 0; i < cardsInHand; i++) {
            if (i > 0) {
                handString.append(" ");
            }
            handString.append(hand[i].getValue());
        }
        return handString.toString();
    }
    //getter method
    public synchronized String getHandString() {
        return handToString();
    }

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
        for (int i = 0; i < hand.length; i++) {
            // discards card thats not preferred value
            if (hand[i].getValue() != playerNum) {
                Card discardedCard = hand[i];//saves reference to old card to return 
                hand[i] = newCard; //adds new card in position
                return discardedCard; //returns discarded card to add to deck
            }
        }
        //no card to be replaced found
        return newCard;
    }

        public void turn() {
            Card drawnCard = deckToDrawFrom.drawTop();

            if (drawnCard == null) {
            Thread.yield();
            return;
        }

        Card discardedCard = replaceCard(drawnCard);
        deckToAddTo.addBottom(discardedCard);

        System.out.println("player " + playerNum
                + " draws a " + drawnCard.getValue()
                + " from deck " + deckToDrawFrom.getdeckNum()
                + " and discards a " + discardedCard.getValue()
                + " to deck " + deckToAddTo.getdeckNum());

        System.out.println("player " + playerNum
                + " current hand is " + getHandString());

        if (hasWon()) {
            declareWin();
            return;
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    @Override
    public void run() {
        //checks for a starting win
        if (hasWon()) {
            declareWin();
            return;
        }

        // keeps playing until a player has won
        while (!gameOver && !Thread.currentThread().isInterrupted()) {
        turn();
        }

    }

    // announces winner and stops game
    private void declareWin() {
        synchronized (Player.class) {
            if (!gameOver) {
                gameOver = true;
                System.out.println(getName() + " wins");
            }
        }
    }

    // resters shared flag before making new game
    public static void resetGame() {
        gameOver = false;
    }
}



