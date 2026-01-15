public class Output {
    // trida pro vypis do konzole
    private static final String SHIP_HIT = "██";
    private static final String WATER_MISS = "░░";
    private static final String UNREVEALED = "  "; 
    private static final String SHIP_SAFE = "[]"; 

    // barvy textu
    private static final String BOLD = "\033[1m";
    private static final String RESET = "\033[0m";

    // casti ramecku
    private static final String HORIZ = "══"; 
    private static final String VERT = "║";
    private static final String TL = "╔", TR = "╗", BL = "╚", BR = "╝"; 
    private static final String T_DOWN = "╦", T_UP = "╩";

    // vypise hlavni menu
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
        System.out.printf((fmt + "%n"), "1. Nova hra");
        System.out.printf((fmt + "%n"), "2. Nastaveni bota");
        System.out.printf((fmt + "%n"), "3. Statistiky");
        System.out.printf((fmt + "%n"), "4. Konec");
        System.out.println(BL + "════════════════════════════" + BR);
    }

    // vykresli herni plochu
    public void printGameFrame(int[][] botBoard, int[][] playerBoard, String logMessage) {
        int size = botBoard.length;

        System.out.println(); 

        System.out.print("    "); 
        for (int i = 0; i < size; i++) System.out.print(" " + (char)('A' + i));
        System.out.print(" "); 
        for (int i = 0; i < size; i++) System.out.print(" " + (char)('A' + i));
        System.out.println();

        System.out.print("   " + TL);
        for (int i = 0; i < size; i++) System.out.print(HORIZ);
        System.out.print(T_DOWN);
        for (int i = 0; i < size; i++) System.out.print(HORIZ);
        System.out.println(TR);

        for (int i = 0; i < size; i++) {
            System.out.printf("%2d %s", i + 1, VERT);
            for (int cell : botBoard[i]) System.out.print(getSymbol(cell, false));
            System.out.print(VERT);
            for (int cell : playerBoard[i]) System.out.print(getSymbol(cell, true));
            System.out.printf("%s %-2d\n", VERT, i + 1);
        }

        System.out.print("   " + BL);
        for (int i = 0; i < size; i++) System.out.print(HORIZ);
        System.out.print(T_UP);
        for (int i = 0; i < size; i++) System.out.print(HORIZ);
        System.out.println(BR);

        System.out.print("    "); 
        System.out.print("         " + BOLD + "BOT" + RESET + "        ");
        System.out.print(" ");
        System.out.println("       " + BOLD + "HRAC" + RESET);
        
        if (logMessage != null && !logMessage.isEmpty()) {
            System.out.println("\n" + logMessage);
        }
    }

    // vrati symbol pro vykresleni
    private String getSymbol(int value, boolean isPlayer) {
        if (value == -1) return SHIP_HIT;
        if (value == -2) return WATER_MISS;
        if (value == 1) return isPlayer ? SHIP_SAFE : UNREVEALED;
        return UNREVEALED;
    }
    
    // zobrazi urovne obtiznosti
    public void printBotDifficulties() {
        System.out.println("\n--- OBTIZNOST BOTA ---");
        System.out.println("1. Lehky   : Nahodna strelba.");
        System.out.println("2. Stredni : Nahodna, neopakuje se.");
        System.out.println("3. Tezky   : Po zasahu hleda v okoli.");
    }
}