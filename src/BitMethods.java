public class BitMethods {
    public static long stringMoveToLong(String move) {
        char letter = move.charAt(0);
        int row = letter - 'a', column = 8 - Integer.parseInt(move.substring(1)), moveNum;
        moveNum = column * 8 + row;
        return 1L << moveNum;
    }

    public static int stringMoveToInt(String move) {
        char letter = move.charAt(0);
        int row = letter - 'a', column = 8 - Integer.parseInt(move.substring(1)), moveNum;
        moveNum = column * 8 + row;
        return moveNum;
    }

    public static long stringToLong(String binary) {
        if (binary.charAt(0) == '0') {
            return Long.parseLong(binary, 2);
        } else {
            return Long.parseLong("1" + binary.substring(2), 2) * 2;
        }
    }

    public static int countBits(long bits) {
        int counter = 0;
        while (bits != 0) {
            counter++;
            bits &= bits - 1;
        }
        return counter;
    } //Brian Kernighan's method

    public static String moveToStringMove(long pieceBit) {
        int piece = getLS1B(pieceBit);
        piece = 63 - piece;
        String start = "";
        int pieceRow = piece / 8;
        int pieceColumn = piece % 8;
        switch (pieceColumn) {
            case 0 -> start = "h";
            case 1 -> start = "g";
            case 2 -> start = "f";
            case 3 -> start = "e";
            case 4 -> start = "d";
            case 5 -> start = "c";
            case 6 -> start = "b";
            case 7 -> start = "a";
        }
        return start + (pieceRow + 1);
    }

    public static String moveToStringMove(long start, long end) {
        return moveToStringMove(start) + moveToStringMove(end);
    }

    public static String moveToStringMove(int piece) {
        piece = 63 - piece;
        String start = "";
        int pieceRow = piece / 8;
        int pieceColumn = piece % 8;
        switch (pieceColumn) {
            case 0 -> start = "h";
            case 1 -> start = "g";
            case 2 -> start = "f";
            case 3 -> start = "e";
            case 4 -> start = "d";
            case 5 -> start = "c";
            case 6 -> start = "b";
            case 7 -> start = "a";
        }
        return start + (pieceRow + 1);
    }

    public static void printBits(long bits) {
        int num;
        for (int i = 0; i < 64; i++) {
            num = (int) ((bits >> i) & 1);
            System.out.print(num);
            if ((i + 1) % 8 == 0) {
                System.out.println();
            }
        }
        System.out.println();
    }

    public static boolean oneBitCheck(long bits) {
        return (bits != 0 && ((bits & (bits - 1)) == 0));
    }

    public static int getLS1B(long bits) {
        if (bits != 0) {
            return countBits((bits & -bits)-1);
        }
        return -1;
    }
}
