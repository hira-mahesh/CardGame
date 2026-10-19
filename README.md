# CardGame
Software Dev coursework

## Directory structure:
└── hira-mahesh-cardgame/
    ├── README.md
    ├── deck1.txt
    ├── deck2.txt
    ├── deck3.txt
    ├── invalidPack.txt
    ├── packExample.3playerstxt
    ├── player1_output.txt
    ├── player2_output.txt
    ├── player3_output.txt
    ├── player4_output.txt
    ├── pom.xml
    ├── winningPack3players.txt
    └── src/
        ├── main/
        │   └── java/
        │       ├── Card.java
        │       ├── CardDeck.java
        │       ├── CardGame.java
        │       └── Player.java
        └── test/
            └── java/
                └── CardTest.java

## Requirements
+ **JDK 17 or later**. The project is configured to compile against Java 17 using maven.compiler.release
+ **Apache Maven** to compile the source code and run the test suite.
+ **JUnit 4.13.2** for unit tests.
+ **Maven Surefire Plugin** 3.5.2 to run the tests

## Compiling the program
Open a terminal in the project root directory (the directory containing pom.xml) and run **mvn clean compile** . Maven removes the previous build output and compiles the main Java source files into target/classes.

## Running the test suite
Run all the tests with **mvn clean test** to the run the testing suite.And to run the tests without cleaning the previous build output, use **mvn test**. The tests use JUnit 4.13.2, listed in pom.xml. Test reports are saved in **target/surefire-reports/**.
Maven shows BUILD SUCCESS if the build and tests pass. If they fail, check the terminal and Surefire reports for compilation errors or failed tests.

## Running the game
Compile the program first **mvn compile**
Then run the main class from the project root **java -cp target/classes CardGame**
Enter the number of players when prompted, followed by the path to the pack file. The code will accept any positive integer but we have provided packs for a demonstration with three players as well as an invalid one. 

To start a three-player game using the supplied winning pack:
Enter number of players :3
Enter the location of pack to load : winningPack3players.txt
Enter the pack path when prompted. The two lines above are example inputs, not one command.

## Pack file format
For n players, the input pack must contain exactly ***8 × n lines.*** Each line must contain one ***non-negative*** integer, with no additional text. The first 4 × n cards are dealt to players in round-robin order; the remaining cards are distributed among the decks in round-robin order until there are no remianing cards in the pack.
For three players, the pack must therefore contain 24 lines. Use a pack file such as winningPack3players.txt or packExample.3playerstxt from the project root.

## Output Files
The program writes each player's initial hand and turn log to files named player1_output.txt, player2_output.txt, and so on. It writes the final contents of each deck to deck1.txt, deck2.txt, etc  in the project root directory.

