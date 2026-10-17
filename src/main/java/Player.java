
public class Player extends Thread{
    private int playerNum;
    private Card[] hand;
    private int cardsInHand;
    private cardDeck deckToDrawFrom;
    private cardDeck deckToAddTo;

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

    public void setDecks(cardDeck deckToDrawFrom, cardDeck deckToAddTo){//tells player which deck to use
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
    private synchronized Card replaceCard(Card newCard){
        for (int i = 0; i < hand.length; i++){
            //discards card that isnt preferred value
            if (hand[i].getValue() != playerNum){
                Card discardedCard = hand[i]; // saves reference to old card
                hand[i] = newCard; //adds new card in position
                return discardedCard; 
            }
        }
        //no card to be replaced found
        return newCard();
    }
    @Override
    public void run() {
        //checks for a starting win
        if (hasWon()) {
            declareWin();
            return;
        }

        // keeps playing until a player has won
        while (!gameOver) {
            //draws a card from the left
            Card drawnCard = deckToDrawFrom.drawTop();

            // if deck is empty try again
            if (drawnCard == null) {
                Thread.yield();
                continue;
            }
            //print move 
            System.out.println( "player " + playerNum + " draws a "+ drawnCard.getValue()+ " from deck " + deckToDrawFrom.getdeckNum());
            
            // replace card in hand
            Card discardedCard = replaceCard(drawnCard);

            // put discarded card in bottom of right pile
            deckToAddTo.addBottom(discardedCard);
            //print discarding move
            System.out.println( "player " + playerNum + " discards a " + discardedCard.getValue() + " to deck " + deckToAddTo.getdeckNum());
            //prints hand
            System.out.println("player " + playerNum + " current hand is " + getHandString());
        // pauses  after each complete turn
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
            // check if player has won

            if (hasWon()) {
                declareWin();
                return;
            }
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



