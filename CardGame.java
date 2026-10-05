import java.util.Scanner;
public class CardGame {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in); //scanner class
        System.out.print("Enter the number of players: "); 
        int n = scanner.nextInt();//inputting players4
        System.out.println("Number of players: " + n);
        scanner.close();
    }

}
