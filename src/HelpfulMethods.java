public class HelpfulMethods {
    public static void bitsToDisplay(long bits) {
        String binary = Long.toBinaryString(bits);
        int length = binary.length();
        for (int i = 0; i < 64 - length; i++) binary = "0" + binary;
        for (int i = 63; i >= 0; i--) {
            if ((i + 1) % 8 == 0 && i != 63) {
                System.out.println("|");
            }
            System.out.printf("|%c", binary.charAt(i));
        }
        System.out.print("|\n\n\n");
    }

    public static void boardToDisplay(String[][] board) {
        System.out.print("|");
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                System.out.printf("%s|", board[i][j]);
            }
            if (i != 7) {
                System.out.print("\n|");
            }
        }
        System.out.print("\n\n\n");
    }

    public static void boardLongToDisplay(long[] board) {
        bitsToDisplay(board[0] | board[1] | board[2] | board[3] | board[4] | board[5] | board[6] | board[7] | board[8] | board[9] | board[10] | board[11]);
    }

    public static void boardLongToDisplay(Long[] board) {
        bitsToDisplay(board[0] | board[1] | board[2] | board[3] | board[4] | board[5] | board[6] | board[7] | board[8] | board[9] | board[10] | board[11]);
    }

    public static void printBoardBits(Long[] board) {
        System.out.printf("%d %d %d %d %d %d %d %d %d %d %d %d %d %d\n", board[0], board[1], board[2], board[3], board[4], board[5], board[6], board[7], board[8], board[9], board[10], board[11], board[12], board[13]);
    }

    public static long getOccupied(long[] board) {
        long occupied = 0;
        for (int i = 0; i < 12; i++) {
            occupied |= board[i];
        }
        return  occupied;
    }

    public static long getOccupied(Long[] board) {
        long occupied = 0;
        for (int i = 0; i < 12; i++) {
            occupied |= board[i];
        }
        return  occupied;
    }
}
