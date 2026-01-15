import java.util.Scanner;

public class Main {
    // hlavni trida, ktera spousti hru
    private static final Logic logic = new Logic();
    private static final Output output = new Output();
    private static final Scanner scanner = new Scanner(System.in);
    
    // nastaveni hry a bota
    private static Bot bot;
    private static int difficulty = 2;
    private static final String[] BOT_NAMES = {"Neznamy", "Lehky", "Stredni", "Tezky"};
    
    // statistiky
    private static int totalWins = 0;
    private static int totalLosses = 0;
    private static int gamesPlayed = 0;
    private static int totalShipsSunk = 0;

    // vstupni bod programu
    public static void main(String[] args) {
        boolean running = true;
        while (running) {
            clearConsole();
            output.printMenu();
            System.out.print("\nZvolte moznost: ");
            String choice = scanner.nextLine().trim();

            if (choice.equals("1")) {
                runGameLoop();
            } else if (choice.equals("2")) {
                changeSettings();
            } else if (choice.equals("3")) {
                showStats();
            } else if (choice.equals("4")) {
                running = false;
                System.out.println("Na shledanou!");
            } else {
                System.out.println("Neplatna volba.");
                promptEnterKey();
            }
        }
        scanner.close();
    }
    
    // ridici smycka jedne hry
    private static void runGameLoop() {
        logic.initGame();
        bot = createBot(difficulty);
        
        long startTime = System.currentTimeMillis();
        boolean gameRunning = true;
        String turnLog = "Hra zacala. Cekam na rozkazy.";

        while (gameRunning) {
            clearConsole();
            output.printGameFrame(logic.getBotBoard(), logic.getPlayerBoard(), turnLog);

            // tah hrace
            int[] pMove = getValidPlayerInput(); 
            int pRes = logic.processShot(logic.getBotBoard(), pMove[0], pMove[1], true);
            
            if (pRes == 2) {
                totalShipsSunk++;
            }
            
            if (logic.checkWin(logic.getBotBoard())) {
                endGame(true, startTime);
                break;
            }

            // tah bota
            int[] bMove = bot.shoot(logic.getPlayerBoard());
            int bRes = logic.processShot(logic.getPlayerBoard(), bMove[0], bMove[1], false);
            
            String pStr = formatMove("Vy", pMove, pRes);
            String bStr = formatMove("Bot", bMove, bRes);
            turnLog = pStr + "\n" + bStr;

            if (logic.checkWin(logic.getPlayerBoard())) {
                endGame(false, startTime);
                break;
            }
        }
    }

    // ziska od uzivatele souradnice a osetri chyby
    private static int[] getValidPlayerInput() {
        while (true) {
            System.out.print("\nZadejte cil (napr. A1 nebo 1A): ");
            String input = scanner.nextLine().toUpperCase().trim();
            
            try {
                if (input.length() < 2) {
                    throw new Exception();
                }

                int r, c;
                char first = input.charAt(0);
                char last = input.charAt(input.length() - 1);

                // Logika pro rozpoznani formatu
                if (Character.isLetter(first)) {
                    // Format A1 (Pismeno na zacatku)
                    c = first - 'A';
                    // zbytek retezce musi byt cislo
                    r = Integer.parseInt(input.substring(1)) - 1;
                } 
                else if (Character.isLetter(last)) {
                    // Format 1A (Pismeno na konci)
                    c = last - 'A';
                    // zacatek retezce musi byt cislo
                    r = Integer.parseInt(input.substring(0, input.length() - 1)) - 1;
                } 
                else {
                    throw new Exception();
                }
                
                if (!Utils.isValid(r, c)) {
                    System.out.println("Mimo herni plochu.");
                    continue;
                }
                
                // ODSTRANENO: Kontrola jiz zasazeneho policka (povolena opakovana strelba)
                
                return new int[]{r, c};

            } catch (NumberFormatException e) {
                System.out.println("Spatny format cisla.");
            } catch (Exception e) {
                System.out.println("Neplatny vstup. Pouzijte napr. A5 nebo 5A.");
            }
        }
    }

    // naformatuje textovy vystup tahu
    private static String formatMove(String who, int[] move, int res) {
        String coord = "" + (char)('A' + move[1]) + (move[0] + 1);
        String resultStr;
        if (res == 1) {
            resultStr = "ZASAH lode!";
        } else if (res == 2) {
            resultStr = "POTOPENA lod!";
        } else if (res == 3) {
            resultStr = "opakovana strelba."; // uzitecne pro debug
        } else {
            resultStr = "vedle.";
        }
        return String.format("%-4s strelba na %-3s -> %s", who, coord, resultStr);
    }

    // vytvori instanci bota podle obtiznosti
    private static Bot createBot(int diff) {
        if (diff == 1) return new Simple();
        if (diff == 3) return new Advanced();
        return new Standard();
    }

    // ukonci hru a zobrazi vysledky
    private static void endGame(boolean playerWon, long startTime) {
        clearConsole();
        output.printGameFrame(logic.getBotBoard(), logic.getPlayerBoard(), "KONEC HRY"); 
        double duration = (System.currentTimeMillis() - startTime) / 1000.0;
        
        System.out.println("\n" + (playerWon ? "VITEZSTVI!" : "PROHRA!"));
        System.out.printf("Cas: %.1fs\n", duration);
        
        if (playerWon) totalWins++; else totalLosses++;
        gamesPlayed++;
        promptEnterKey();
    }
    
    // zmeni nastaveni obtiznosti
    private static void changeSettings() {
        clearConsole();
        output.printBotDifficulties(); 
        System.out.println("Aktualni obtiznost: " + BOT_NAMES[difficulty]);
        System.out.print("Vyberte obtiznost (1-3): ");
        try {
            String line = scanner.nextLine();
            int val = Integer.parseInt(line);
            if (val >= 1 && val <= 3) {
                difficulty = val;
            }
        } catch (NumberFormatException e) {
            // ignorujeme chybny vstup
        }
    }
    
    // zobrazi statistiky
    private static void showStats() {
        clearConsole();
        System.out.println("--- STATISTIKA ---");
        System.out.println("Odehrano: " + gamesPlayed + " | Vyhry: " + totalWins + " | Prohry: " + totalLosses);
        System.out.println("Potopene lode nepritele: " + totalShipsSunk);
        
        int shots = logic.getPlayerShots();
        double acc = 0;
        if (shots > 0) {
            acc = (double)logic.getPlayerHits() / shots * 100;
        }
        System.out.printf("Presnost posledni hry: %.1f%%\n", acc);
        promptEnterKey();
    }

    // pocka na stisk enteru
    private static void promptEnterKey() {
        System.out.println("\nStisknete Enter...");
        scanner.nextLine();
    }

    // vymaze konzoli
    public static void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}