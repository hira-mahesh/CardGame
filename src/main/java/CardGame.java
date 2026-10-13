
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
public class CardGame {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in); //scanner class
        System.out.print("Enter the number of players: "); 
        int numberOfPlayers = scanner.nextInt();//inputting players4
        makePlayers(numberOfPlayers);
        System.out.print("Enter the location of pack to load: ");
        String packLocation = scanner.next();
        
        while (!checkValid(packLocation)) {
            System.out.println("Invalid file ");
            System.out.println("Enter the location of pack to load: ");
            packLocation = scanner.next();
        }
        scanner.close();
        Player[] players = makePlayers(numberOfPlayers);
        distributeCards(players, packLocation);
        for (Player player : players) {
    System.out.println(player.getName() + "'s hand:");

    for (int card : player.getHand()) {
        System.out.println(card);
    }
}
    }

    public static boolean checkValid(String packLocation){
        File pack = new File(packLocation);
        
        // scanner closed automatically
        try (Scanner myReader = new Scanner(pack)) {
            int lineCount = 0;
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine().trim();
                //regex to make sure there only a single number on the line and theres no negatives
                if (!data.matches("\\d+")) {
                    return false;
                 }
                lineCount++; //updates count of lines
            }

            if (lineCount % 8 != 0) { // makes sure line count is 8n
            return false;
         }
        } 
        catch (FileNotFoundException e) {
            return false;
        }

    
        return true;
    }

    public static Player[] makePlayers(int numberOfPlayers)
    {
        Player[] players = new Player[numberOfPlayers];
        for (int i = 0; i < numberOfPlayers; i++) {
            players[i] = new Player(i + 1);
        }
        return players;
    }

    public static void distributeCards(Player[] players, String packLocation){
        File pack = new File(packLocation);
        try (Scanner packScanner = new Scanner(pack)) {
             for (int round = 0; round < 4; round++) { // outer loop deals 4 rounds of cards
                for (int i = 0; i < players.length; i++) {// inner loop gives one card to each player per round
                    int card = Integer.parseInt(packScanner.nextLine().trim()); // reads  next line from the file
                    players[i].addCard(card); // removes the spaces around and changes to integer
                }
             }
            
        } catch (FileNotFoundException e) {
             System.out.println("Pack not found.");
        }

    }
}

