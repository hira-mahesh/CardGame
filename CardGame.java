import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
public class CardGame {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in); //scanner class
        System.out.print("Enter the number of players: "); 
        int numberOfPlayers = scanner.nextInt();//inputting players4
    
        System.out.print("Enter the location of pack to load: ");
        String packLocation = scanner.next();
        
        while (!checkValid(packLocation)) {
            System.out.println("Invalid file ");
            System.out.println("Enter the location of pack to load: ");
            packLocation = scanner.next();
        }
        scanner.close();
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

}
