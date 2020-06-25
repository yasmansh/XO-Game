import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Game {
    private static ArrayList<Game> games = new ArrayList<>();
    private static int numOfRows = 3;
    private static int numOfColumns = 3;
    private ArrayList<StringBuilder> table = new ArrayList<>();
    private ArrayList<Integer> rowInStep = new ArrayList<>();
    private ArrayList<Integer> columnInStep = new ArrayList<>();
    private Person[] players = new Person[2];
    private boolean isUndoOfPlayer1;
    private boolean isUndoOfPlayer2;
    public boolean isEnd;
    private int numberOfRows;
    private int numberOfColumns;
    private int numberOfPlayer;
    private int step;

    Game() {
        setNumberOfRows(numOfRows);
        setNumberOfColumns(numOfColumns);
        setTable();
    }

    public ArrayList<StringBuilder> getTable() {
        return table;
    }

    public Person[] getPlayers() {
        return players;
    }

    public static ArrayList<Game> getGames() {
        return games;
    }

    public ArrayList<Integer> getColumnInStep() {
        return columnInStep;
    }

    public ArrayList<Integer> getRowInStep() {
        return rowInStep;
    }

    public void setNumberOfRows(int numberOfRows) {
        this.numberOfRows = numberOfRows;
    }

    public void setNumberOfColumns(int numberOfColumns) {
        this.numberOfColumns = numberOfColumns;
    }

    public int getNumberOfRows() {
        return numberOfRows;
    }

    public int getNumberOfColumns() {
        return numberOfColumns;
    }

    public void setTable() {
        for (int row = 0; row < getNumberOfRows(); row++) {
            StringBuilder sample = new StringBuilder();
            for (int column = 0; column < getNumberOfColumns(); column++) {
                sample.append('_');
            }
            getTable().add(sample);
        }
    }

    public static void addNewGame(Game game) {
        game.isEnd = false;
        getGames().add(game);
    }


    public void setPlayers(Person player1, Person player2) {
        this.getPlayers()[0] = player1;
        this.getPlayers()[1] = player2;
        printTable();
    }

    public static void setSizeOfTable(int numberOfRows, int numberOfColumns) {
        numOfRows = numberOfRows;
        numOfColumns = numberOfColumns;
    }

    public boolean isInvalidCoordination(int row, int column) {
        if (row < 0 || row >= getNumberOfRows() || column < 0 || column >= getNumberOfColumns()) {
            System.out.println("Invalid coordination");
            return true;
        }
        if (getTable().get(row).charAt(column) != '_') {
            System.out.println("Invalid coordination");
            return true;
        }
        return false;
    }

    public void undo() {
        if (numberOfPlayer == 0 && !isUndoOfPlayer2 && step > 1) {
            isUndoOfPlayer2 = true;
            numberOfPlayer = 1;
            changeTableInUndo();
        } else if (numberOfPlayer == 1 && !isUndoOfPlayer1 && step > 1) {
            isUndoOfPlayer1 = true;
            numberOfPlayer = 0;
            changeTableInUndo();
        } else {
            System.out.println("Invalid undo");
            printTable();
        }
    }

    private void changeTableInUndo() {
        step--;
        getTable().get(getRowInStep().get(step)).setCharAt(getColumnInStep().get(step), '_');
        getRowInStep().remove(step);
        getColumnInStep().remove(step);
        printTable();
    }

    public void put(int row, int column, Scanner scanner) {
        if (isInvalidCoordination(row, column)) {
            return;
        }
        char character;
        if (numberOfPlayer == 0) {
            character = 'X';
            numberOfPlayer = 1;
        } else {
            character = 'O';
            numberOfPlayer = 0;
        }
        this.getTable().get(row).setCharAt(column, character);
        getRowInStep().add(row);
        getColumnInStep().add(column);
        step++;
        if (isWin() || isEqual()) {
            Main.playing(scanner);
        }
    }

    private void printWinner(int number) {
        this.isEnd = true;
        this.getPlayers()[number].numberOfWins++;
        System.out.println("Player " + getPlayers()[number].getName() + " won");
        if (number == 1) {
            this.getPlayers()[0].numberOfLost++;
        } else {
            this.getPlayers()[1].numberOfLost++;
        }
    }

    public boolean isWin() {
        String findX;
        String findO;
        int numberOfKey;

        if (getNumberOfRows() == 3 || getNumberOfColumns() == 3) {
            findX = "XXX";
            findO = "OOO";
            numberOfKey = 3;
        } else {
            findX = "XXXX";
            findO = "OOOO";
            numberOfKey = 4;
        }
        if (checkHorizontal(findX, findO)) return true;
        if (checkVertical(numberOfKey)) return true;
        if (checkLeftDiameter(numberOfKey, 0, 0)) return true;
        if (checkRightDiameter(numberOfKey, 0, 0)) return true;
        return false;
    }

    private boolean checkRightDiameter(int numberOfKey, int numberOfX, int numberOfO) {
        for (int row = 0; row <= getNumberOfRows() - numberOfKey; row++) {
            for (int column = getNumberOfColumns() - 1; column >= numberOfKey - 1; column--) {
                for (int t = 0; t < numberOfKey; t++) {
                    if (getTable().get(row + t).charAt(column - t) == 'X') {
                        numberOfX++;
                        numberOfO = 0;
                    } else if (getTable().get(row + t).charAt(column - t) == 'O') {
                        numberOfO++;
                        numberOfX = 0;
                    } else {
                        numberOfO = 0;
                        numberOfX = 0;
                    }
                    if (checkNumberOfKeyFind(numberOfKey, numberOfX, numberOfO)) return true;
                }
            }
        }
        return false;
    }

    private boolean checkLeftDiameter(int numberOfKey, int numberOfXFound, int numberOfOFound) {
        for (int row = 0; row <= getNumberOfRows() - numberOfKey; row++) {
            for (int column = 0; column <= getNumberOfColumns() - numberOfKey; column++) {
                for (int t = 0; t < numberOfKey; t++) {
                    if (getTable().get(row + t).charAt(column + t) == 'X') {
                        numberOfXFound++;
                        numberOfOFound = 0;
                    } else if (getTable().get(row + t).charAt(column + t) == 'O') {
                        numberOfOFound++;
                        numberOfXFound = 0;
                    } else {
                        numberOfOFound = 0;
                        numberOfXFound = 0;
                    }
                    if (checkNumberOfKeyFind(numberOfKey, numberOfXFound, numberOfOFound)) return true;
                }
            }
        }
        return false;
    }

    private boolean checkVertical(int numberOfKey) {
        int numberOfX;
        int numberOfO;
        for (int column = 0; column < getNumberOfColumns(); column++) {
            numberOfX = 0;
            numberOfO = 0;
            for (int row = 0; row < getNumberOfRows(); row++) {
                if (getTable().get(row).charAt(column) == 'X') {
                    numberOfO = 0;
                    numberOfX++;
                } else if (getTable().get(row).charAt(column) == 'O') {
                    numberOfO++;
                    numberOfX = 0;
                } else {
                    numberOfO = 0;
                    numberOfX = 0;
                }
                if (checkNumberOfKeyFind(numberOfKey, numberOfX, numberOfO)) return true;
            }
        }
        return false;
    }

    private boolean checkHorizontal(String findX, String findO) {
        for (int row = 0; row < getNumberOfRows(); row++) {
            if (getTable().get(row).indexOf(findX) != -1) {
                printWinner(0);
                return true;
            }
            if (getTable().get(row).indexOf(findO) != -1) {
                printWinner(1);
                return true;
            }
        }
        return false;
    }

    private boolean checkNumberOfKeyFind(int numberOfKey, int numberOfX, int numberOfO) {
        if (numberOfX == numberOfKey) {
            printWinner(0);
            return true;
        }
        if (numberOfO == numberOfKey) {
            printWinner(1);
            return true;
        }
        return false;
    }

    public boolean isEqual() {
        if (step == getNumberOfRows() * getNumberOfColumns()) {
            this.getPlayers()[0].numberOfEqual++;
            this.getPlayers()[1].numberOfEqual++;
            this.isEnd = true;
            System.out.println("Draw");
            return true;
        }
        return false;
    }

    public void printTable() {
        if (Main.end)
            return;
        for (int row = 0; row < getNumberOfRows(); row++) {
            for (int column = 0; column < getNumberOfColumns() - 1; column++) {
                System.out.printf("%c|", getTable().get(row).charAt(column));
            }
            System.out.println(getTable().get(row).charAt(getNumberOfColumns() - 1));
        }
        System.out.println(getPlayers()[numberOfPlayer].getName());
    }
}

class Person {
    private static ArrayList<Person> people = new ArrayList<>();
    private static ArrayList<String> names = new ArrayList<>();
    private String name;

    public int numberOfWins;
    public int numberOfLost;
    public int numberOfEqual;

    Person(String name) {
        this.name = name;
        if (!getNames().contains(name)) {
            getNames().add(name);
            people.add(this);
        }
    }

    public String getName() {
        return name;
    }

    public static ArrayList<String> getNames() {
        return names;
    }

    public static ArrayList<Person> getPeople() {
        return people;
    }

    public static Person findPersonFromName(String name) {
        for (int i = 0; i < getPeople().size(); i++) {
            if (getPeople().get(i).getName().equals(name)) {
                return getPeople().get(i);
            }
        }
        return new Person(name);
    }
}

public class Main {
    static boolean end = false;

    public static void scoreboard(Scanner scanner) {
        String input = scanner.nextLine();
        if (input.equals("quit")) {
            end = true;
            return;
        } else if (input.equals("back")) {
            playing(scanner);
            return;
        }

        printInvalidCommand();
        scoreboard(scanner);

    }

    private static void printInvalidCommand() {
        System.out.println("Invalid command");
    }

    public static void bubbleSortPeople(ArrayList<Person> people) {
        int size = people.size();
        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < size - i - 1; j++) {

                if ((people.get(j + 1).numberOfWins > people.get(j).numberOfWins) || (people.get(j + 1).numberOfWins ==
                        people.get(j).numberOfWins && people.get(j + 1).numberOfLost < people.get(j).numberOfLost) ||
                        (people.get(j + 1).numberOfWins == people.get(j).numberOfWins && people.get(j + 1).numberOfLost
                                == people.get(j).numberOfLost && people.get(j + 1).numberOfEqual < people.get(j).
                                numberOfEqual) || (people.get(j + 1).numberOfWins == people.get(j).numberOfWins &&
                        people.get(j + 1).numberOfLost == people.get(j).numberOfLost && people.get(j + 1).numberOfEqual
                        == people.get(j).numberOfEqual && (people.get(j + 1).getName().compareTo(people.get(j).getName()) < 0))) {

                    Collections.swap(people, j, j + 1);

                }
            }

        }
    }

    public static void showScoreboard() {

        ArrayList<Person> players = new ArrayList<>();
        for (int i = 0; i < Person.getPeople().size(); i++) {
            Person newPerson = new Person(Person.getPeople().get(i).getName());
            players.add(newPerson);
            players.get(i).numberOfWins = Person.getPeople().get(i).numberOfWins;
            players.get(i).numberOfEqual = Person.getPeople().get(i).numberOfEqual;
            players.get(i).numberOfLost = Person.getPeople().get(i).numberOfLost;
        }
        bubbleSortPeople(players);
        for (int i = 0; i < players.size(); i++) {
            System.out.println(players.get(i).getName() + " " + players.get(i).numberOfWins + " " + players.get(i).
                    numberOfLost + " " + players.get(i).numberOfEqual);
        }

    }

    public static void playGame(Scanner scanner, Game game) {

        String input = scanner.nextLine().trim();
        String[] partsOfInput = input.split("\\s+");

        if (partsOfInput[0].contains("put")) {
            Pattern pattern = Pattern.compile("(put\\s*\\(\\s*)(\\d+)(\\s*\\,\\s*)(\\d+)(\\s*\\))");
            Matcher matcher = pattern.matcher(input);
            if (matcher.find()) {
                game.put(Integer.parseInt(matcher.group(2)) - 1, Integer.parseInt(matcher.group(4)) - 1, scanner);
            } else {
                printInvalidCommand();
            }
            game.printTable();
        } else if (input.equals("undo")) {
            game.undo();
        } else if (input.equals("stop")) {
            game.isEnd = true;
            return;
        } else if (input.equals("pause")) {
            return;
        } else {
            printInvalidCommand();
            game.printTable();
        }
        if (!game.isEnd)
            playGame(scanner, game);
    }

    public static int showSavedGamesAndReturnNumberOfSavedGames() {

        int num = 0;
        for (int i = Game.getGames().size() - 1; i >= 0; i--) {
            if (!Game.getGames().get(i).isEnd) {
                System.out.println(++num + ". " + Game.getGames().get(i).getPlayers()[0].getName() + " " + Game.getGames()
                        .get(i).getPlayers()[1].getName());
            }
        }
        return num;
    }

    public static void resume(Scanner scanner, int numbersOfSavedGames) {

        String input = scanner.nextLine().trim();
        if (input.equals("back")) {
            return;
        }
        if (numbersOfSavedGames > 0 && isInt(input)) {
            int number = Integer.parseInt(input), item = 0;
            if (input.charAt(0) == '-' || number <= 0 || number > numbersOfSavedGames) {
                System.out.println("Invalid number");
            } else {
                if (playSaveGame(scanner, number, item)) return;
            }
        } else {
            printInvalidCommand();
        }
        resume(scanner, numbersOfSavedGames);
    }

    private static boolean playSaveGame(Scanner scanner, int number, int item) {
        for (int i = Game.getGames().size() - 1; i >= 0; i--) {
            if (!Game.getGames().get(i).isEnd) {
                item++;
            }
            if (item == number) {
                Game.getGames().get(i).printTable();
                Game.getGames().add(Game.getGames().get(i));
                Game.getGames().remove(i);
                playGame(scanner, Game.getGames().get(Game.getGames().size() - 1));
                return true;
            }
        }
        return false;
    }

    static boolean isInt(String s) {
        for (int a = 0; a < s.length(); a++) {
            if (!Character.isDigit(s.charAt(a))) return false;
        }
        return true;
    }

    public static void playing(Scanner scanner) {
        String input = scanner.nextLine().trim();
        if (input.equals("quit")) {
            end = true;
            return;
        }

        String[] partsOfInput = input.split("\\s+");
        if (partsOfInput.length > 0 && partsOfInput[0].equals("new")) {
            inputContainsNew(scanner, partsOfInput);
        } else if (input.equals("resume")) {
            int numberOfSavedGames = showSavedGamesAndReturnNumberOfSavedGames();
            resume(scanner, numberOfSavedGames);
        } else if (input.equals("scoreboard")) {
            showScoreboard();
            scoreboard(scanner);
            return;
        } else if (partsOfInput[0].equals("set")) {
            inputContainsSet(input);
        } else {
            printInvalidCommand();
        }
        if (!end) {
            playing(scanner);
        }
    }

    private static void inputContainsNew(Scanner scanner, String[] partsOfInput) {
        if (partsOfInput.length == 4 && partsOfInput[1].equals("game")) {

            Game newGame = new Game();
            Game.addNewGame(newGame);
            newGame.setPlayers(Person.findPersonFromName(partsOfInput[2]), Person.findPersonFromName(partsOfInput[3]));
            playGame(scanner, newGame);
        } else if ((partsOfInput.length == 3 || partsOfInput.length == 2) && partsOfInput[1].equals("game")) {
            System.out.println("Invalid players");
        } else {
            printInvalidCommand();
        }
    }

    private static void inputContainsSet(String input) {
        Pattern pattern = Pattern.compile("(set)(\\s)+(table)(\\s)+(\\d+)(\\s)*(\\*)(\\s)*(\\d+)");
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            Game.setSizeOfTable(Integer.parseInt(matcher.group(5)), Integer.parseInt(matcher.group(9)));
        } else if (input.equals("set table")) {
            Game.setSizeOfTable(3, 3);
        } else {
            printInvalidCommand();
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        playing(scanner);
    }
}
