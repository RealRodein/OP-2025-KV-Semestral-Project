import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    private static final Logic logic = new Logic();
    private static final Output output = new Output();
    private static final Scanner scanner = new Scanner(System.in);
    
    // Game State
    private static Bot bot;
    private static int difficulty = 2;
    private static final String[] BOT_NAMES = {"Unknown", "Simple", "Standard", "Advanced", "Expert"};
    
    // Stats
    private static int totalWins = 0, totalLosses = 0, gamesPlayed = 0;
    private static int totalShipsSunk = 0;

    public static void main(String[] args) {
        boolean running = true;
        while (running) {
            clearConsole();
            output.printMenu();
            System.out.print("\nChoose option: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> runGameLoop();
                case "2" -> changeSettings();
                case "3" -> showStats();
                case "4" -> {
                    running = false;
                    System.out.println("Goodbye!");
                }
                default -> {
                    System.out.println("Invalid option.");
                    promptEnterKey();
                }
            }
        }
        scanner.close();
    }
    
    private static void runGameLoop() {
        logic.initGame();
        bot = createBot(difficulty);
        
        long startTime = System.currentTimeMillis();
        boolean gameRunning = true;
        String turnLog = "Game Started. Awaiting orders, Commander.";

        while (gameRunning) {
            clearConsole();
            // Pass the log to be printed along with the frame
            output.printGameFrame(logic.getBotBoard(), logic.getPlayerBoard(), turnLog);

            // --- PLAYER TURN ---
            int[] pMove = getValidPlayerInput(); // Loops until valid coordinate
            int pRes = logic.processShot(logic.getBotBoard(), pMove[0], pMove[1], true);
            
            if (pRes == 2) totalShipsSunk++; // 2 = Sunk
            
            // Check Win immediately after player shot
            if (logic.checkWin(logic.getBotBoard())) {
                endGame(true, startTime);
                break;
            }

            // --- BOT TURN ---
            int[] bMove = bot.shoot(logic.getPlayerBoard());
            int bRes = logic.processShot(logic.getPlayerBoard(), bMove[0], bMove[1], false);
            
            // --- BUILD LOG FOR NEXT FRAME ---
            // Since we don't sleep, we save this text to show in the next loop iteration
            String pStr = formatMove("You", pMove, pRes);
            String bStr = formatMove("Bot", bMove, bRes);
            turnLog = pStr + "\n" + bStr;

            if (logic.checkWin(logic.getPlayerBoard())) {
                endGame(false, startTime);
                break;
            }
            
            // NO SLEEP: The loop restarts instantly, clearing console and printing the new state + log.
        }
    }

    private static int[] getValidPlayerInput() {
        while (true) {
            System.out.print("\nEnter target (e.g. A1): ");
            String input = scanner.nextLine().toUpperCase().trim();
            
            Matcher numM = Pattern.compile("(\\d+)").matcher(input);
            Matcher colM = Pattern.compile("([A-J])").matcher(input);

            if (numM.find() && colM.find()) {
                int r = Integer.parseInt(numM.group(1)) - 1;
                int c = colM.group(1).charAt(0) - 'A';
                
                // Check if valid coord
                if (!Utils.isValid(r, c)) {
                    System.out.println("Out of bounds.");
                    continue;
                }
                
                // Check if already shot
                int cell = logic.getBotBoard()[r][c];
                if (cell == Utils.HIT || cell == Utils.MISS) {
                    System.out.println("Already shot there! Check your map.");
                    continue;
                }
                
                return new int[]{r, c};
            } else {
                System.out.println("Invalid format. Use Letter+Number (e.g. A5).");
            }
        }
    }

    private static String formatMove(String who, int[] move, int res) {
        String coord = "" + (char)('A' + move[1]) + (move[0] + 1);
        String resultStr = switch(res) {
            case 1 -> "HIT a ship!";
            case 2 -> "SUNK a ship!";
            default -> "missed.";
        };
        return String.format("%-4s fired at %-3s -> %s", who, coord, resultStr);
    }

    private static Bot createBot(int diff) {
        return switch (diff) {
            case 1 -> new Simple();
            case 3 -> new Advanced();
            case 4 -> new Expert();
            default -> new Standard();
        };
    }

    private static void endGame(boolean playerWon, long startTime) {
        clearConsole();
        // Show final state
        output.printGameFrame(logic.getBotBoard(), logic.getPlayerBoard(), "GAME OVER"); 
        double duration = (System.currentTimeMillis() - startTime) / 1000.0;
        
        System.out.println("\n" + (playerWon ? "VICTORY!" : "DEFEAT!"));
        System.out.printf("Time: %.1fs\n", duration);
        
        if (playerWon) totalWins++; else totalLosses++;
        gamesPlayed++;
        promptEnterKey();
    }
    
    private static void changeSettings() {
        clearConsole();
        output.printBotDifficulties(); 
        System.out.println("Current Difficulty: " + BOT_NAMES[difficulty]);
        System.out.print("Select Difficulty (1-4): ");
        if (scanner.hasNextInt()) {
            int val = scanner.nextInt();
            if (val >= 1 && val <= 4) difficulty = val;
        }
        scanner.nextLine(); 
    }
    
    private static void showStats() {
        clearConsole();
        System.out.println("--- STATISTICS ---");
        System.out.println("Played: " + gamesPlayed + " | Wins: " + totalWins + " | Losses: " + totalLosses);
        System.out.println("Enemy Ships Sunk: " + totalShipsSunk);
        
        int shots = logic.getPlayerShots();
        double acc = shots > 0 ? (double)logic.getPlayerHits() / shots * 100 : 0;
        System.out.printf("Last Game Accuracy: %.1f%%\n", acc);
        promptEnterKey();
    }

    private static void promptEnterKey() {
        System.out.println("\nPress Enter...");
        scanner.nextLine();
    }

    public static void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}