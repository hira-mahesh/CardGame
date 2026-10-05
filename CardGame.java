import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
public class CardGame {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in); //scanner class
        System.out.print("Enter the number of players: "); 
        int n = scanner.nextInt();//inputting players4
    
        System.out.print("Enter the location of pack to load: ");
        String packLocation = scanner.next();
        
        while (!checkValid(packLocation)) {
            System.out.print("Invalid file ");
            System.out.print("Enter the location of pack to load: ");
            packLocation = scanner.next();
        }
        scanner.close();
    }

    public static boolean checkValid(String packLocation){
        File pack = new File(packLocation);
        
        // scannerclosed automatically
        try (Scanner myReader = new Scanner(pack)) {
            int lineCount = 0;
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                            
                if (!data.matches("\\d+")) {
                myReader.close();
                return false;
            }
                int number = Integer.parseInt(data);
                if (number < 0) {
                    return false;
                }
                lineCount++;
            }

            if (lineCount % 8 != 0) {
            return false;
         }
        } 
        catch (FileNotFoundException e) {
            return false;
        }

    
        return true;
    }

}
