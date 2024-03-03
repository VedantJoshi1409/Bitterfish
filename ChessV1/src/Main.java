import javax.swing.*;

import java.util.ArrayList;

import static java.lang.Character.isDigit;

public class Main {
    public static ArrayList<String> movesSoFar = new ArrayList<>();
    public static Long[] currentBoard = new Long[23];
    public static long bPawn, bRook, bKnight, bBishop, bQueen, bKing, wPawn, wRook, wKnight, wBishop, wQueen, wKing;
    public static long previousDoublePawnPush, promotion = 0L;
    public static boolean wSMoved, wLMoved, bSMoved, bLMoved, player;
    public static boolean flippedBoard = false, inTheory, endGame = false;
    public static JFrame frame;
    public static String[][] board;

    public static void main(String[] args) {
        MagicBitboards.initializeArrays();
        Zobrist.initializeRandomKeys();
        OpeningFinder.initializeOpenings();

        //Start position: rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq -
        //Tricky endgame: 8/k7/3p4/p2P1p2/P2P1P2/8/8/K7 w - -
        fenToBoard("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq -");
        arrayToBitBoard();
        pieceBoardUpdate();
        inTheory = isStart(currentBoard);
        //frame = new GameFrame();
        Client.returnMove();
        //computerGame(true, 3000);
        //engineTest(100, 5);
    }

    public static void computerGame(boolean computerFirst, int thinkTime) {
        while (currentBoard[18] == 0) {
            if (!endGame) {
                endGame = checkEndGame();
            }
            if (RepetitionTable.addToHistory(currentBoard[14], RepetitionTable.historyFlag)) {
                System.out.println("Draw By Repetition");
                break;
            }
            if (currentBoard[15] != 0 && currentBoard[16] != 0) {
                movesSoFar.add(findMove(currentBoard[15], currentBoard[16]));
            }
            if (computerFirst) {
                currentBoard = Engine.computerMove(thinkTime, player, currentBoard);
                longBoardUpdate(true, currentBoard);
            } else {
                currentBoard = PlayerGame.playerMove(currentBoard, player, board);
                longBoardUpdate(true, currentBoard);
            }
            board = bitsToBoard();
            frame.repaint();
            player = !player;
            computerFirst = !computerFirst;
        }
        if (currentBoard[18] == 1) {
            System.out.println("\n\n\nCheckmate");
        } else if (currentBoard[18] == 2){
            System.out.println("\n\n\nStalemate");
        }
    }

    public static void engineTest(int thinkTime, int theoryLimit) {
        if (theoryLimit == 0) {
            inTheory = false;
        }
        int counter = 0;
        while (currentBoard[18] == 0) {
            if (!endGame) {
                endGame = checkEndGame();
            }
            if (RepetitionTable.addToHistory(currentBoard[14], RepetitionTable.historyFlag)) {
                System.out.println("Draw By Repetition");
                break;
            }
            if (counter < theoryLimit) {
                counter++;
                try {
                    Thread.sleep(thinkTime);
                } catch (InterruptedException ignored) {
                }
                if (counter >= theoryLimit) {
                    inTheory = false;
                }
            }
            if (currentBoard[15] != 0 && currentBoard[16] != 0) {
                movesSoFar.add(findMove(currentBoard[15], currentBoard[16]));
            }
            currentBoard = Engine.computerMove(thinkTime, player, currentBoard);
            longBoardUpdate(true, currentBoard);
            board = bitsToBoard();
            frame.repaint();
            player = !player;
        }
        if (currentBoard[18] == 1) {
            System.out.println("\n\n\nCheckmate");
        } else if (currentBoard[18] == 2){
            System.out.println("\n\n\nStalemate");
        }
    }

    public static void pieceBoardUpdate() {
        long castle = 0;
        currentBoard[0] = wPawn;
        currentBoard[1] = wRook;
        currentBoard[2] = wKnight;
        currentBoard[3] = wBishop;
        currentBoard[4] = wQueen;
        currentBoard[5] = wKing;
        currentBoard[6] = bPawn;
        currentBoard[7] = bRook;
        currentBoard[8] = bKnight;
        currentBoard[9] = bBishop;
        currentBoard[10] = bQueen;
        currentBoard[11] = bKing;
        currentBoard[12] = previousDoublePawnPush;
        if (wSMoved) {
            castle |= 8;
        }
        if (wLMoved) {
            castle |= 4;
        }
        if (bSMoved) {
            castle |= 2;
        }
        if (bLMoved) {
            castle |= 1;
        }
        currentBoard[13] = castle;
        currentBoard[14] = Zobrist.generateKey(currentBoard, player);
        currentBoard[15] = 0L;
        currentBoard[16] = 0L;
        currentBoard[17] = 0L;
        currentBoard[18] = 0L;
        currentBoard[19] = 0L;
        currentBoard[20] = 0L;
        currentBoard[21] = 0L;
        currentBoard[22] = 0L;
    }

    public static void longBoardUpdate(boolean player, Long[] board) {
        long castle = board[13];
        if (player) {
            wPawn = board[0];
            wRook = board[1];
            wKnight = board[2];
            wBishop = board[3];
            wQueen = board[4];
            wKing = board[5];
            bPawn = board[6];
            bRook = board[7];
            bKnight = board[8];
            bBishop = board[9];
            bQueen = board[10];
            bKing = board[11];
        } else {
            bPawn = board[0];
            bRook = board[1];
            bKnight = board[2];
            bBishop = board[3];
            bQueen = board[4];
            bKing = board[5];
            wPawn = board[6];
            wRook = board[7];
            wKnight = board[8];
            wBishop = board[9];
            wQueen = board[10];
            wKing = board[11];
        }
        wSMoved = (castle >> 3 & 1) == 1;
        wLMoved = (castle >> 2 & 1) == 1;
        bSMoved = (castle >> 1 & 1) == 1;
        bLMoved = (castle & 1) == 1;
        previousDoublePawnPush = board[12];
    }

    public static void longBoardUpdate(long[] board) {
        wPawn = board[0];
        wRook = board[1];
        wKnight = board[2];
        wBishop = board[3];
        wQueen = board[4];
        wKing = board[5];
        bPawn = board[6];
        bRook = board[7];
        bKnight = board[8];
        bBishop = board[9];
        bQueen = board[10];
        bKing = board[11];
        Main.board = bitsToBoard();
        frame.repaint();
    }

    public static Long[] longBoardSwitch(Long[] board) {
        return new Long[]{board[6], board[7], board[8], board[9], board[10], board[11], board[0], board[1], board[2], board[3], board[4], board[5], board[12], board[13], board[14], board[15]};
    }

    public static void arrayToBitBoard() {
        String piece, binary;
        bPawn = 0L;
        bRook = 0L;
        bKnight = 0L;
        bBishop = 0L;
        bQueen = 0L;
        bKing = 0L;
        wPawn = 0L;
        wRook = 0L;
        wKnight = 0L;
        wBishop = 0L;
        wQueen = 0L;
        wKing = 0L;
        for (int i = 0; i < 64; i++) {
            piece = board[i / 8][i % 8];
            binary = "0000000000000000000000000000000000000000000000000000000000000000";
            binary = binary.substring(i + 1) + "1" + binary.substring(0, i);
            if (!piece.equals(" ")) {
                switch (piece) {
                    case "p" -> bPawn += stringToLong(binary);
                    case "r" -> bRook += stringToLong(binary);
                    case "n" -> bKnight += stringToLong(binary);
                    case "b" -> bBishop += stringToLong(binary);
                    case "q" -> bQueen += stringToLong(binary);
                    case "k" -> bKing += stringToLong(binary);
                    case "P" -> wPawn += stringToLong(binary);
                    case "R" -> wRook += stringToLong(binary);
                    case "N" -> wKnight += stringToLong(binary);
                    case "B" -> wBishop += stringToLong(binary);
                    case "Q" -> wQueen += stringToLong(binary);
                    case "K" -> wKing += stringToLong(binary);
                }
            }
        }
    }

    public static long stringToLong(String binary) {
        if (binary.charAt(0) == '0') {
            return Long.parseLong(binary, 2);
        } else {
            return Long.parseLong("1" + binary.substring(2), 2) * 2;
        }
    }

    public static void fenToBoard(String fen) {
        wSMoved = true;
        wLMoved = true;
        bSMoved = true;
        bLMoved = true;
        String[][] tempBoard = new String[8][8];
        String castle, pawnPush;
        int row = 0, column = 0, end = -1;
        for (int i = 0; i < fen.length(); i++) {
            if (fen.charAt(i) == ' ') {
                end = i;
                break;
            }
            if (fen.charAt(i) != '/') {
                if (!isDigit(fen.charAt(i))) {
                    tempBoard[column][row] = fen.substring(i, i + 1);
                    row++;
                } else {
                    row += Integer.parseInt(fen.substring(i, i + 1));
                    if (row >= 8) {
                        row = 0;
                    }
                }
            } else {
                column++;
                row = 0;
            }
        }
        if (fen.charAt(end + 1) == 'b') {
            player = false;
        } else {
            player = true;
        }
        castle = fen.substring(end + 3);
        for (int i = 0; i < castle.length(); i++) {
            if (castle.charAt(i) == 'K') {
                wSMoved = false;
            } else if (castle.charAt(i) == 'Q') {
                wLMoved = false;
            } else if (castle.charAt(i) == 'k') {
                bSMoved = false;
            } else if (castle.charAt(i) == 'q') {
                bLMoved = false;
            }
        }
        if (fen.charAt(fen.length() - 1) == '-') {
            previousDoublePawnPush = 0L;
        } else {
            pawnPush = fen.substring(fen.length() - 2);
            previousDoublePawnPush = stringMoveToLong(pawnPush);
            if (player) {
                previousDoublePawnPush <<= 8;
            } else {
                previousDoublePawnPush >>= 8;
            }
        }
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (tempBoard[i][j] == null) {
                    tempBoard[i][j] = " ";
                }
            }
        }
        board = tempBoard;
    }

    public static String[][] bitsToBoard() {
        String[][] board = new String[8][8];
        for (int i = 0; i < 64; i++) {
            if (((bPawn >> i) & 1) == 1) {
                board[i / 8][i % 8] = "p";
            } else if (((bRook >> i) & 1) == 1) {
                board[i / 8][i % 8] = "r";
            } else if (((bKnight >> i) & 1) == 1) {
                board[i / 8][i % 8] = "n";
            } else if (((bBishop >> i) & 1) == 1) {
                board[i / 8][i % 8] = "b";
            } else if (((bQueen >> i) & 1) == 1) {
                board[i / 8][i % 8] = "q";
            } else if (((bKing >> i) & 1) == 1) {
                board[i / 8][i % 8] = "k";
            } else if (((wPawn >> i) & 1) == 1) {
                board[i / 8][i % 8] = "P";
            } else if (((wRook >> i) & 1) == 1) {
                board[i / 8][i % 8] = "R";
            } else if (((wKnight >> i) & 1) == 1) {
                board[i / 8][i % 8] = "N";
            } else if (((wBishop >> i) & 1) == 1) {
                board[i / 8][i % 8] = "B";
            } else if (((wQueen >> i) & 1) == 1) {
                board[i / 8][i % 8] = "Q";
            } else if (((wKing >> i) & 1) == 1) {
                board[i / 8][i % 8] = "K";
            } else {
                board[i / 8][i % 8] = " ";
            }
        }
        return board;
    }

    public static long stringMoveToLong(String move) {
        char letter = move.charAt(0);
        int row = -1, column = 8 - Integer.parseInt(move.substring(1)), moveNum;
        switch (letter) {
            case 'a' -> row = 0;
            case 'b' -> row = 1;
            case 'c' -> row = 2;
            case 'd' -> row = 3;
            case 'e' -> row = 4;
            case 'f' -> row = 5;
            case 'g' -> row = 6;
            case 'h' -> row = 7;
        }
        moveNum = column * 8 + row;
        return 1L << moveNum;
    }

    public static void makeMove(String move, long[] board) {
        long[] previousBoard = board.clone();
        long movingPiece = stringMoveToLong(move.substring(0, 2));
        long moveSquare = stringMoveToLong(move.substring(2, 4));
        long occupied = HelpfulMethods.getOccupied(board);
        int piece = -1;
        boolean wKingCastle = (board[5] & stringMoveToLong("e1")) != 0, bKingCastle = (board[11] & stringMoveToLong("e8")) != 0;
        if (move.equals("e1g1") && wKingCastle) {
            board[5] = MoveGeneration.wSCastleBit;
            board[1] ^= MoveGeneration.wSCastleRook;
        } else if (move.equals("e1c1") && wKingCastle) {
            board[5] = MoveGeneration.wLCastleBit;
            board[1] ^= MoveGeneration.wLCastleRook;
        } else if (move.equals("e8g8") && bKingCastle) {
            board[11] = MoveGeneration.bSCastleBit;
            board[7] ^= MoveGeneration.bSCastleRook;
        } else if (move.equals("e8c8") && bKingCastle) {
            board[11] = MoveGeneration.bLCastleBit;
            board[7] ^= MoveGeneration.bLCastleRook;
        } else if (move.length() == 5) {
            char promotionType = move.charAt(4), side = move.charAt(3);
            if (side == '8') {
                board[0] &= ~movingPiece;
                switch (promotionType) {
                    case 'R' -> board[1] |= moveSquare;
                    case 'N' -> board[2] |= moveSquare;
                    case 'B' -> board[3] |= moveSquare;
                    case 'Q' -> board[4] |= moveSquare;
                }
                for (int i = 6; i < 12; i++) {
                    board[i] &= ~moveSquare;
                }
            } else {
                board[6] &= ~movingPiece;
                switch (promotionType) {
                    case 'R' -> board[7] |= moveSquare;
                    case 'N' -> board[8] |= moveSquare;
                    case 'B' -> board[9] |= moveSquare;
                    case 'Q' -> board[10] |= moveSquare;
                }
                for (int i = 0; i < 6; i++) {
                    board[i] &= ~moveSquare;
                }
            }
        } else {
            for (int i = 0; i < 12; i++) {
                board[i] &= ~moveSquare;
                if ((board[i] & movingPiece) == movingPiece) {
                    piece = i;
                    board[i] = board[i] & ~movingPiece | moveSquare;
                }
            }
            if (piece == 0) {
                if ((occupied & moveSquare) == 0) {
                    board[6] &= ~(moveSquare << 8);
                }
            } else if (piece == 6) {
                if ((occupied & moveSquare) == 0) {
                    board[0] &= ~(moveSquare >> 8);
                }
            }
        }
    }

    public static void makeMove(String move, Long[] board) {
        Long[] previousBoard = board.clone();
        long movingPiece = stringMoveToLong(move.substring(0, 2));
        long moveSquare = stringMoveToLong(move.substring(2, 4));
        long occupied = HelpfulMethods.getOccupied(board);
        int piece = -1;
        boolean wKingCastle = (board[5] & stringMoveToLong("e1")) != 0, bKingCastle = (board[11] & stringMoveToLong("e8")) != 0;
        if (move.equals("e1g1") && wKingCastle) {
            board[5] = MoveGeneration.wSCastleBit;
            board[1] ^= MoveGeneration.wSCastleRook;
        } else if (move.equals("e1c1") && wKingCastle) {
            board[5] = MoveGeneration.wLCastleBit;
            board[1] ^= MoveGeneration.wLCastleRook;
        } else if (move.equals("e8g8") && bKingCastle) {
            board[11] = MoveGeneration.bSCastleBit;
            board[7] ^= MoveGeneration.bSCastleRook;
        } else if (move.equals("e8c8") && bKingCastle) {
            board[11] = MoveGeneration.bLCastleBit;
            board[7] ^= MoveGeneration.bLCastleRook;
        } else if (move.length() == 5) {
            char promotionType = move.charAt(4), side = move.charAt(3);
            if (side == '8') {
                board[0] &= ~movingPiece;
                switch (promotionType) {
                    case 'R' -> board[1] |= moveSquare;
                    case 'N' -> board[2] |= moveSquare;
                    case 'B' -> board[3] |= moveSquare;
                    case 'Q' -> board[4] |= moveSquare;
                }
                for (int i = 6; i < 12; i++) {
                    board[i] &= ~moveSquare;
                }
            } else {
                board[6] &= ~movingPiece;
                switch (promotionType) {
                    case 'R' -> board[7] |= moveSquare;
                    case 'N' -> board[8] |= moveSquare;
                    case 'B' -> board[9] |= moveSquare;
                    case 'Q' -> board[10] |= moveSquare;
                }
                for (int i = 0; i < 6; i++) {
                    board[i] &= ~moveSquare;
                }
            }
        } else {
            for (int i = 0; i < 12; i++) {
                board[i] &= ~moveSquare;
                if ((board[i] & movingPiece) == movingPiece) {
                    piece = i;
                    board[i] = board[i] & ~movingPiece | moveSquare;
                }
            }
            if (piece == 0) {
                if ((occupied & moveSquare) == 0) {
                    board[6] &= ~(moveSquare << 8);
                }
            } else if (piece == 6) {
                if ((occupied & moveSquare) == 0) {
                    board[0] &= ~(moveSquare >> 8);
                }
            }
        }
    }

    public static void possibleMoveTest(boolean player, Long[] board) {
        ArrayList<Long[]> moves = MoveGeneration.possibleMoves(player, board);
        for (Long[] value : moves) {
            longBoardUpdate(true, value);
            Main.board = bitsToBoard();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
            frame.repaint();
        }
    }

    public static String findMove(Long[] previousBoard, Long[] currentBoard) {
        long previous = HelpfulMethods.getOccupied(previousBoard), current = HelpfulMethods.getOccupied(currentBoard), start, end;
        start = previous & ~current;
        end = current & ~previous;
        return MoveGeneration.moveToStringMove(start) + MoveGeneration.moveToStringMove(end);
    }

    public static String findMove(long start, long end) {
        return MoveGeneration.moveToStringMove(start) + MoveGeneration.moveToStringMove(end);
    }

    public static boolean isStart(Long[] currentBoard) {
        for (int i = 0; i < 12; i++) {
            if (currentBoard[i] != OpeningFinder.startPosition[i]) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkEndGame() {
        return ((Evaluation.countBits(wRook) + Evaluation.countBits(wKnight) + Evaluation.countBits(wBishop) + (Evaluation.countBits(wQueen)*2)) < 3) && ((Evaluation.countBits(bRook) + Evaluation.countBits(bKnight) + Evaluation.countBits(bBishop) + (Evaluation.countBits(bQueen)*2))) < 3;
    }
}