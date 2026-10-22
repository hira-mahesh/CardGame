
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class CardGame {
    public static void main(String[] args) { //defines the main class
        Scanner scanner = new Scanner(System.in); //imports the scanner to read userinput and files
        int numberOfPlayers = checkValidPlayerNum(scanner); //asks user for number of players and validates input
        scanner.nextLine(); 
        System.out.print("Enter the location of pack to load: "); //asks user to enter location of pack location
        String packLocation = scanner.nextLine().trim();//reads file lcoation and removes the whitespace
        while (!checkValidFile(packLocation, numberOfPlayers)) { //validates pack file name
            System.out.println("Invalid file."); 
            System.out.print("Enter the location of pack to load: ");
            packLocation = scanner.nextLine().trim();
        }
        
        Player[] players = makePlayers(numberOfPlayers); //create players
        CardDeck[] decks = createDecks(numberOfPlayers); //create decks
        distributeCards(players, packLocation); //distribute cards
        for (Player player : players) { //writes the starting hand to the file
            player.writeInitialHand();
        }
        distributeDecks(decks, packLocation); //distribute decks
        assignDecks(players, decks); //assign right and left deck to player

        // Print each player's hand
        for (Player player : players) {
            System.out.println(player.getName() + "'s hand:"); // prints name

            for (Card card : player.getHand()) {
                if (card != null) {
                    System.out.println(card.getValue()); //prints card value
                }
            }
            System.out.println();
        }
        // Print each deck
        // for (CardDeck deck : decks) {
        //     System.out.println("Deck " + deck.getdeckNum() + ":");

        //     while (!deck.isEmpty()) {
        //         Card card = deck.drawTop();
        //         System.out.println(card.getValue());
        //     }
        //     System.out.println();
        // }

        //reset game status shared flag
        Player.resetGame();
        for (Player player : players) {  // loops
            player.start(); //starts player thread 
        }  

        
        // wait for player threads to finish
        for (Player player : players) {
            try {
                player.join(); //waits until player thread has finished
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); //restores interupted status of thread
                break; //stops waiting for any player threads
            }
        }
        for(CardDeck deck : decks) { //saves each decks final contents
            deck.writeDeckToFile();//writes final state of the deck to output file
        }
        scanner.close(); //close the scanner
    }

    //method to validate the number of players
    public static int checkValidPlayerNum(Scanner scanner) {
        while (true) { 
            System.out.print("Enter the number of players: ");

            if (scanner.hasNextInt()) {
                int numberOfPlayers = scanner.nextInt();

                if (numberOfPlayers > 0 && numberOfPlayers <= 15) { //checks number of players is between 1 and 15
                    return numberOfPlayers; //returns valid number of players to main
                }
            } else {
                scanner.next(); // Removes invalid input
            }

            System.out.println("Invalid number of players. Please try again.");
        }
    }

    //method that checks pack file is valid
    public static boolean checkValidFile( String packLocation, int numberOfPlayers) {
        if (numberOfPlayers <= 0 || numberOfPlayers > 15) { //rejects player counts if its outside the range 
            return false;// returns false
        }
        File pack = new File(packLocation); //creates file objects that represents the pack files

        try (Scanner myReader = new Scanner(pack)) {//opens pack file using scanner
            int lineCount = 0; //stores number of lines in file
            while (myReader.hasNextLine()) {//reads file one line at a time until no lines are left
                String data = myReader.nextLine().trim(); //removes whitespace from line

                // Each line must have one non-negative integer
                if (!data.matches("\\d+")) {
                    return false;
                }
                // attempts to convert line into integer
                try {
                    Integer.parseInt(data);
                } catch (NumberFormatException e) { //rejects numbers that are too large to fit in an int.
                    return false;
                }
                lineCount++; //increments count after validating line

                
            }
            // checks file has exactly 8 cards per player
            return lineCount == 8 * numberOfPlayers;
        } catch (FileNotFoundException e) { //returns false where file cannot be found 
            return false;
        }
    }

    //method that creates the players
    public static Player[] makePlayers(int numberOfPlayers) {
        Player[] players = new Player[numberOfPlayers]; // creates array with one position for each player objects
        for (int i = 0; i < numberOfPlayers; i++) {//loops through each position in players array.
            players[i] = new Player(i + 1);//creates a player with player number starting at 1
        }
        return players; //returns array with all players
    }

    // method that makes the decks
    public static CardDeck[] createDecks(int numberOfPlayers) {
        CardDeck[] decks = new CardDeck[numberOfPlayers];// makes array for the decks
        for (int i = 0; i < numberOfPlayers; i++) {
            decks[i] = new CardDeck(i + 1); //creates deck with each deck number starting at 1
        }
        return decks; //returns array containing all deck
    }

    //method that deals four cards
    public static void distributeCards(
        Player[] players, String packLocation) {
        File pack = new File(packLocation); //creates file objecr for the pack files
        try (Scanner packScanner = new Scanner(pack)) { //opens pack file and closes scanner automatically after
            // deal four cards to each player
            for (int round = 0; round < 4; round++) {
                for (int i = 0; i < players.length; i++) {
                    int cardValue =Integer.parseInt(packScanner.nextLine().trim());//reads line and converts to int
                    Card card = new Card(cardValue);//creates card objects with the value from file
                    players[i].addCard(card);//adds card to current players hand
                }
            }

        } catch (FileNotFoundException e) { //handles if the pack file cannot be found.
            System.out.println("Pack not found."); //error message if pack cant be opened
        }
    }

    //method that distributes the remaining cards among the decks
    public static void distributeDecks( CardDeck[] decks, String packLocation) {
        File pack = new File(packLocation); //file object for the pack file
        try (Scanner packScanner = new Scanner(pack)) { //opens the pack file and closes the scanner after
            int numberOfPlayers = decks.length;//stores number of players and decks
            // Skip the cards already dealt to the players
            for (int i = 0; i < numberOfPlayers * 4; i++) { 
                packScanner.nextLine();//reads and discards one line from the pack file.
            }
            int deckIndex = 0;
            // Distribute remaining cards among the decks until no cards left
            while (packScanner.hasNextLine()) {
                int cardValue = Integer.parseInt(packScanner.nextLine().trim()); //reads card value and converts to int
                Card card = new Card(cardValue); //creates card object
                decks[deckIndex].addBottom(card);//adds to bottom of deck
                deckIndex++;//moves to next deck
                if (deckIndex == decks.length) { //check if end of deck array has been reached
                    deckIndex = 0; //comes back to first deck for round robin
                }
            }
        } catch (FileNotFoundException e) { //if pack file cannot be found
            System.out.println("Pack not found.");
        }
    }
    //method to assign decks to player
    public static void assignDecks(Player[] players, CardDeck[] decks) { 
        for (int i = 0; i < players.length; i++) { //loops through every player
            // deck to players left
            int leftDeck = i;
            // deck to players right
            int rightDeck = (i + 1) % decks.length; //modulo operator wraps back to final player
            //assign decks to player
            players[i].setDecks(decks[leftDeck], decks[rightDeck]); 
        }
    }
}
