
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class CardGame {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int numberOfPlayers = checkValidPlayerNum(scanner);
        scanner.nextLine(); 
        System.out.print("Enter the location of pack to load: ");
        String packLocation = scanner.nextLine().trim();
        while (!checkValidFile(packLocation, numberOfPlayers)) {
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
            System.out.println(player.getName() + "'s hand:");

            for (Card card : player.getHand()) {
                if (card != null) {
                    System.out.println(card.getValue());
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
        for (Player player : players) {  // start all the player threads
            player.start();
        }  

        
        // wait for player threads to finish
        for (Player player : players) {
            try {
                player.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        for(CardDeck deck : decks) { //writes the final state of the decks to files
            deck.writeDeckToFile();
        }
        scanner.close(); //close the scanner
    }
    public static int checkValidPlayerNum(Scanner scanner) {
        while (true) {
            System.out.print("Enter the number of players: ");

            if (scanner.hasNextInt()) {
                int numberOfPlayers = scanner.nextInt();

                if (numberOfPlayers > 0) {
                    return numberOfPlayers;
                }
            } else {
                scanner.next(); // Removes invalid input
            }

            System.out.println("Invalid number of players. Please try again.");
        }
    }
    public static boolean checkValidFile(
            String packLocation, int numberOfPlayers) {
        if (numberOfPlayers <= 0) {
            return false;
        }
        File pack = new File(packLocation);

        try (Scanner myReader = new Scanner(pack)) {
            int lineCount = 0;
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine().trim();

                // Each line must contain one non-negative integer
                if (!data.matches("\\d+")) {
                    return false;
                }
                lineCount++;
            }
            // Exactly eight cards per player
            return lineCount == 8 * numberOfPlayers;
        } catch (FileNotFoundException e) {
            return false;
        }
    }
    public static Player[] makePlayers(int numberOfPlayers) {
        Player[] players = new Player[numberOfPlayers];
        for (int i = 0; i < numberOfPlayers; i++) {
            players[i] = new Player(i + 1);
        }
        return players;
    }
    public static CardDeck[] createDecks(int numberOfPlayers) {
        CardDeck[] decks = new CardDeck[numberOfPlayers];
        for (int i = 0; i < numberOfPlayers; i++) {
            decks[i] = new CardDeck(i + 1);
        }
        return decks;
    }
    public static void distributeCards(
            Player[] players, String packLocation) {
        File pack = new File(packLocation);
        try (Scanner packScanner = new Scanner(pack)) {
            // Deal four cards to each player
            for (int round = 0; round < 4; round++) {
                for (int i = 0; i < players.length; i++) {
                    int cardValue =
                            Integer.parseInt(packScanner.nextLine().trim());
                    Card card = new Card(cardValue);
                    players[i].addCard(card);
                }
            }

        } catch (FileNotFoundException e) {
            System.out.println("Pack not found.");
        }
    }

    public static void distributeDecks( CardDeck[] decks, String packLocation) {
        File pack = new File(packLocation);
        try (Scanner packScanner = new Scanner(pack)) {
            int numberOfPlayers = decks.length;
            // Skip the cards already dealt to the players
            for (int i = 0; i < numberOfPlayers * 4; i++) {
                packScanner.nextLine();
            }
            int deckIndex = 0;
            // Distribute remaining cards among the decks
            while (packScanner.hasNextLine()) {
                int cardValue = Integer.parseInt(packScanner.nextLine().trim());
                Card card = new Card(cardValue);
                decks[deckIndex].addBottom(card);
                deckIndex++;
                if (deckIndex == decks.length) {
                    deckIndex = 0;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Pack not found.");
        }
    }

    public static void assignDecks(Player[] players, CardDeck[] decks) {

        for (int i = 0; i < players.length; i++) {
            // deck to players left
            int leftDeck = i;
            // deck to players right
            int rightDeck = (i + 1) % decks.length;
            //assign deck to player
            players[i].setDecks(decks[leftDeck], decks[rightDeck]);
        }
    }
}
