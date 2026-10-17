
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
public class CardGame {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the number of players: ");
        int numberOfPlayers = scanner.nextInt();
        scanner.nextLine(); 
        System.out.print("Enter the location of pack to load: ");
        String packLocation = scanner.nextLine().trim();
        while (!checkValid(packLocation, numberOfPlayers)) {
            System.out.println("Invalid file.");
            System.out.print("Enter the location of pack to load: ");
            packLocation = scanner.nextLine().trim();
        }
        scanner.close();
        Player[] players = makePlayers(numberOfPlayers);
        CardDeck[] decks = createDecks(numberOfPlayers);
        distributeCards(players, packLocation);
        distributeDecks(decks, packLocation);
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
        for (CardDeck deck : decks) {
            System.out.println("Deck " + deck.getdeckNum() + ":");

            while (!deck.isEmpty()) {
                Card card = deck.drawTop();
                System.out.println(card.getValue());
            }
            System.out.println();
        }
    }
    public static boolean checkValid(
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

    public static void distributeDecks(
        CardDeck[] decks, String packLocation) {
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
}
