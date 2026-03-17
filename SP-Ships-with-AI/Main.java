package kin.op.kupec.vojtech;

import java.util.Scanner;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * POZNAMKA:
 * Pro spravne zobrazeni herni plochy a ASCII artu v NetBeans doporucuji
 * nastavit v Output font na JuliaMono nebo jine neproporcionalni pismo.
 */

public class Main {
    private static final Logic logic = new Logic();
    private static final Output output = new Output();
    private static final Scanner scanner = new Scanner(System.in);

    private static Bot bot;
    private static int difficulty = 2;
    private static final String[] BOT_NAMES = {"neznamy", "lehky", "stredni", "tezky"};

    // bod 2: konstanta pro pocatecni pismeno souradnic
    public static final char STARTING_COORDINATE_CHARACTER = 'A';

    // bod 6: specialni vstup pro navrat do hlavniho menu
    private static final String MENU_RETURN_INPUT = "M";

    private static int totalWins = 0;
    private static int totalLosses = 0;
    private static int gamesPlayed = 0;
    private static int totalShipsSunk = 0;

    public static void main(String[] args) {
        // nastaveni utf8 kodovani pro spravne zobrazeni znaku v konzoli
        try {
            System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        } catch (Exception e) {
            System.err.println("Nepodarilo se nastavit UTF-8 kodovani: " + e.getMessage());
        }

        boolean running = true;
        while (running) {
            clearConsole();
            output.printMenu();
            // bod 5: choice je nyni int, nacitany metodou readInt()
            int choice = readInt("\nZvolte moznost: ");

            switch (choice) {
                case 1 -> runGameLoop();
                case 2 -> changeSettings();
                case 3 -> showStats();
                case 4 -> {
                    running = false;
                    System.out.println("Na shledanou!");
                }
                default -> {
                    System.out.println("Neplatna volba.");
                    promptEnterKey();
                }
            }
        }
        scanner.close();
    }

    /**
     * Bod 3: Metoda pro nacteni celeho cisla s cyklem a zachytavanim vyjimky.
     * Opakuje dotaz dokud uzivatel nezada platne cele cislo.
     *
     * @param prompt Zobrazena vyzva pro uzivatele
     * @return Nactene cele cislo
     */
    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                String line = scanner.nextLine().trim();
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Chyba: Zadejte cele cislo.");
            }
        }
    }

    private static void runGameLoop() {
        logic.initGame();
        bot = createBot(difficulty);

        long startTime = System.currentTimeMillis();
        boolean gameRunning = true;
        String turnLog = "Hra zacala. Cekam na rozkazy.";

        while (gameRunning) {
            clearConsole();
            output.printGameFrame(logic.getBotBoard(), logic.getPlayerBoard(), turnLog);

            // bod 6: getValidPlayerInput muze vratit null pri zadani M (navrat do menu)
            Coordinates pMove = getValidPlayerInput();
            if (pMove == null) {
                return; // navrat do hlavniho menu
            }

            int pRes = logic.processShot(logic.getBotBoard(), pMove, true);

            if (pRes == 2) {
                totalShipsSunk++;
            }

            if (logic.checkWin(logic.getBotBoard())) {
                endGame(true, startTime);
                break;
            }

            Coordinates bMove = bot.shoot(logic.getPlayerBoard());
            int bRes = logic.processShot(logic.getPlayerBoard(), bMove, false);

            String pStr = formatMove("Vy", pMove, pRes);
            String bStr = formatMove("Bot", bMove, bRes);
            turnLog = pStr + "\n" + bStr;

            if (logic.checkWin(logic.getPlayerBoard())) {
                endGame(false, startTime);
                break;
            }
        }
    }

    /**
     * Bod 6: Nacte platny vstup od hrace.
     * Zadani hodnoty MENU_RETURN_INPUT ("M") vraci null = navrat do menu.
     *
     * @return Souradnice cilovaneho policka, nebo null pri navratu do menu
     */
    private static Coordinates getValidPlayerInput() {
        while (true) {
            System.out.print("\nZadejte cil (napr. A1 nebo 1A), nebo '" + MENU_RETURN_INPUT + "' pro navrat do menu: ");
            String input = scanner.nextLine().toUpperCase().trim();

            // bod 6: kontrola specialniho vstupu pro navrat do menu
            if (input.equals(MENU_RETURN_INPUT)) {
                return null;
            }

            try {
                if (input.length() < 2) {
                    throw new Exception();
                }

                int r, c;
                char first = input.charAt(0);
                char last = input.charAt(input.length() - 1);

                if (Character.isLetter(first)) {
                    // bod 2: odkaz na konstantu STARTING_COORDINATE_CHARACTER (radek 115)
                    c = first - STARTING_COORDINATE_CHARACTER;
                    r = Integer.parseInt(input.substring(1)) - 1;
                } else if (Character.isLetter(last)) {
                    // bod 2: odkaz na konstantu STARTING_COORDINATE_CHARACTER (radek 119)
                    c = last - STARTING_COORDINATE_CHARACTER;
                    r = Integer.parseInt(input.substring(0, input.length() - 1)) - 1;
                } else {
                    throw new Exception();
                }

                if (!Utils.isValid(r, c)) {
                    System.out.println("Mimo herni plochu.");
                    continue;
                }

                return new Coordinates(r, c);

            } catch (NumberFormatException e) {
                System.out.println("Spatny format cisla.");
            } catch (Exception e) {
                System.out.println("Neplatny vstup. Pouzijte napr. A5 nebo 5A.");
            }
        }
    }

    private static String formatMove(String who, Coordinates move, int res) {
        // bod 2: odkaz na konstantu STARTING_COORDINATE_CHARACTER (radek 142)
        String coord = "" + (char)(STARTING_COORDINATE_CHARACTER + move.y) + (move.x + 1);
        String resultStr;
        resultStr = switch (res) {
            case 1 -> "ZASAH lode!";
            case 2 -> "POTOPENA lod!";
            case 3 -> "opakovana strelba.";
            default -> "vedle.";
        };
        return String.format("%-4s strelba na %-3s -> %s", who, coord, resultStr);
    }

    private static Bot createBot(int diff) {
        if (diff == 1) return new Simple();
        if (diff == 3) return new Advanced();
        return new Standard();
    }

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

    /**
     * Bod 3 + 4: Pouziva readInt() pro nacteni obtiznosti.
     * Vypise chybove hlaseni kdyz uzivatel zada cislo mimo rozsah.
     */
    private static void changeSettings() {
        clearConsole();
        output.printBotDifficulties();
        System.out.println("Aktualni obtiznost: " + BOT_NAMES[difficulty]);

        // bod 3: pouziti metody readInt() misto inline parsovani
        int val = readInt("Vyberte obtiznost (1-3): ");
        if (val >= 1 && val <= 3) {
            difficulty = val;
        } else {
            // bod 4: chybove hlaseni pro cislo mimo rozsah
            System.out.println("Chyba: Obtiznost musi byt v rozsahu 1 az 3. Hodnota nebyla zmenena.");
            promptEnterKey();
        }
    }

    private static void showStats() {
        clearConsole();
        System.out.println("--- STATISTIKA ---");
        System.out.println("Odehrano: " + gamesPlayed + " | Vyhry: " + totalWins + " | Prohry: " + totalLosses);
        System.out.println("Potopene lode nepritele: " + totalShipsSunk);

        int shots = logic.getPlayerShots();
        double acc = 0;
        if (shots > 0) {
            acc = (double) logic.getPlayerHits() / shots * 100;
        }
        System.out.printf("Presnost posledni hry: %.1f%%\n", acc);
        promptEnterKey();
    }

    private static void promptEnterKey() {
        System.out.println("\nStisknete Enter...");
        scanner.nextLine();
    }

    public static void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}