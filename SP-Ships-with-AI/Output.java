public class Output {
    // --- Visual Assets ---
    private static final String SHIP_HIT = "██";
    private static final String WATER_MISS = "░░";
    private static final String UNREVEALED = "  "; 
    private static final String SHIP_SAFE = "[]"; 

    // ANSI Text Styles
    private static final String BOLD = "\033[1m";
    private static final String RESET = "\033[0m";

    // Frame Parts
    private static final String HORIZ = "══"; 
    private static final String VERT = "║";
    private static final String TL = "╔", TR = "╗", BL = "╚", BR = "╝"; 
    private static final String T_DOWN = "╦", T_UP = "╩";

    public void printMenu() {
        System.out.println("by KV");
        System.out.println("""
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣸⣇⣀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣀⣸⣇⣀⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠉⢹⡏⠉⠉⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠸⢾⡟⠛⠛⠛⠛⢻⡷⠇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣾⣤⡴⠟⣿⣦⣤⣷⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⢰⡟⠋⢿⡷⠀⣿⡇⠈⣿⣿⡆⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⣿⠀⠀⠀⠀⣿⣿⣿⣿⣿⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢻⡀⠀⠀⠀⣿⣿⣿⣿⡟⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⣀⣀⣀⣀⣠⣼⣧⣤⣤⣤⣿⣿⣿⣿⣧⣤⣤⣤⣀⣀⣀⠀⠀⠀
""".stripTrailing());

        
        System.out.println(TL + "════════════════════════════" + TR);
        String fmt = VERT + " %-26s " + VERT;
        System.out.printf((fmt + "%n"), "1. Start Game");
        System.out.printf((fmt + "%n"), "2. Bot Settings");
        System.out.printf((fmt + "%n"), "3. Statistics");
        System.out.printf((fmt + "%n"), "4. Exit");
        System.out.println(BL + "════════════════════════════" + BR);
    }

    public void printGameFrame(int[][] botBoard, int[][] playerBoard, String logMessage) {
        int size = botBoard.length;

        System.out.println(); // Top spacing

        // 1. Column Headers
        System.out.print("    "); 
        for (int i = 0; i < size; i++) System.out.print(" " + (char)('A' + i));
        System.out.print(" "); 
        for (int i = 0; i < size; i++) System.out.print(" " + (char)('A' + i));
        System.out.println();

        // 2. Top Borders
        System.out.print("   " + TL);
        for (int i = 0; i < size; i++) System.out.print(HORIZ);
        System.out.print(T_DOWN);
        for (int i = 0; i < size; i++) System.out.print(HORIZ);
        System.out.println(TR);

        // 3. Board Rows
        for (int i = 0; i < size; i++) {
            System.out.printf("%2d %s", i + 1, VERT);
            for (int cell : botBoard[i]) System.out.print(getSymbol(cell, false));
            System.out.print(VERT);
            for (int cell : playerBoard[i]) System.out.print(getSymbol(cell, true));
            System.out.printf("%s %-2d\n", VERT, i + 1);
        }

        // 4. Bottom Borders
        System.out.print("   " + BL);
        for (int i = 0; i < size; i++) System.out.print(HORIZ);
        System.out.print(T_UP);
        for (int i = 0; i < size; i++) System.out.print(HORIZ);
        System.out.println(BR);

        // 5. Names
        System.out.print("    "); 
        System.out.print("         " + BOLD + "BOT" + RESET + "        ");
        System.out.print(" ");
        System.out.println("       " + BOLD + "PLAYER" + RESET);
        
        // 6. Log Message (The necessary info)
        if (logMessage != null && !logMessage.isEmpty()) {
            System.out.println("\n" + logMessage);
        }
    }

    private String getSymbol(int value, boolean isPlayer) {
        return switch (value) {
            case -1 -> SHIP_HIT;
            case -2 -> WATER_MISS;
            case 1 -> isPlayer ? SHIP_SAFE : UNREVEALED;
            default -> UNREVEALED;
        };
    }
    
    public void printBotDifficulties() {
        System.out.println("\n--- DIFFICULTY LEVELS ---");
        System.out.println("1. Simple   : Random fire.");
        System.out.println("2. Standard : Random, no duplicates.");
        System.out.println("3. Advanced : Hunts adjacent tiles after hit.");
        System.out.println("4. Expert   : Probability heatmaps & parity.");
    }
}