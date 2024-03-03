import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class OpeningFinder {
    public static final long[] startPosition = {71776119061217280L, -9151314442816847872L, 4755801206503243776L, 2594073385365405696L, 576460752303423488L, 1152921504606846976L, 65280L, 129L, 66L, 36L, 8L, 16L};
    public static ArrayList<ArrayList<String>> formattedOpenings = new ArrayList<>();

    public static void initializeOpenings() {
        String[] line;
        String fileName = "src/Games/Games.txt", lineIn, move, squareToMove, startSquare = "", formattedMove;
        char pieceType, indicator, promotion = 0;
        boolean player;
        ArrayList<String> openings = new ArrayList<>(), formattedLine = new ArrayList<>();
        ArrayList<long[]> currentPositions = new ArrayList<>();
        long[][] currentPositionsCopy;
        long[] currentPosition;
        long currentPiece, eliminator, occupied;
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            lineIn = in.readLine();
            while (lineIn != null) {
                openings.add(lineIn);
                lineIn = in.readLine();
            }
        } catch (IOException e) {
            System.out.println(e);
        }
        for (String currentLine : openings) {
            currentPosition = startPosition.clone();
            line = currentLine.split("\\s+");
            formattedLine.clear();
            for (int i = 0; i < line.length; i++) {
                occupied = HelpfulMethods.getOccupied(currentPosition);
                move = line[i];
                if (move.equals("1-0") || move.equals("0-1") || move.equals("1/2-1/2")) break;
                move = move.replaceAll("x", "");
                move = move.replaceAll("\\+", "");
                move = move.replaceAll("#", "");
                squareToMove = move.substring(move.length() - 2);
                player = i % 2 == 0;
                if (move.equals("O-O")) {
                    if (player) {
                        formattedMove = "e1g1";
                    } else {
                        formattedMove = "e8g8";
                    }
                } else if (move.equals("O-O-O")) {
                    if (player) {
                        formattedMove = "e1c1";
                    } else {
                        formattedMove = "e8c8";
                    }
                } else if (Character.isUpperCase(move.charAt(0))) {
                    if (move.charAt(0) == 'K') {
                        startSquare = MoveGeneration.moveToStringMove(pieceTypeBoard('K', player, currentPosition));
                    } else {
                        pieceType = move.charAt(0);
                        currentPiece = pieceTypeBoard(pieceType, player, currentPosition);
                        eliminator = -1;
                        if (move.length() == 4) {
                            indicator = move.charAt(1);
                            if (Character.isDigit(indicator)) {
                                eliminator = MoveGeneration.ranks[8 - Character.getNumericValue(indicator)];
                            } else {
                                eliminator = letterToFile(indicator);
                            }
                        } else if (move.length() == 5) {
                            eliminator = letterToFile(move.charAt(1)) & MoveGeneration.ranks[8 - Character.getNumericValue(move.charAt(2))];
                        }
                        currentPiece &= eliminator;
                        startSquare = getStartSquare(currentPiece, squareToMove, pieceType, occupied, player, currentPosition);
                    }
                    formattedMove = startSquare + squareToMove;
                } else {
                    if (player) {
                        currentPiece = currentPosition[0];
                    } else {
                        currentPiece = currentPosition[6];
                    }
                    if (move.charAt(move.length() - 2) == '=') {
                        promotion = move.charAt(move.length() - 1);
                    }
                    if (move.length() == 2) {
                        squareToMove = move;
                        startSquare = getPawnStartSquare(currentPiece, move, player, currentPosition);
                    } else if (move.length() == 3 || move.length() == 5) {
                        squareToMove = move.substring(1, 3);
                        eliminator = letterToFile(move.charAt(0));
                        currentPiece &= eliminator;
                        startSquare = getPawnStartSquare(currentPiece, squareToMove, player, currentPosition);
                    } else if (move.length() == 4) {
                        squareToMove = move.substring(0, 2);
                        startSquare = getPawnStartSquare(currentPiece, squareToMove, player, currentPosition);
                    }
                    if (promotion != 0) {
                        formattedMove = startSquare + squareToMove + promotion;
                    } else {
                        formattedMove = startSquare + squareToMove;
                    }
                    promotion = 0;
                }
                formattedLine.add(formattedMove);
                //System.out.println(formattedMove);
                Main.makeMove(formattedLine.get(i), currentPosition);
                /*Main.longBoardUpdate(currentPosition);
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException ignored) {
                }*/
            }
            formattedOpenings.add((ArrayList<String>) formattedLine.clone());
        }
    }

    public static ArrayList<String> openingMove(ArrayList<String> moves) {
        ArrayList<String> options = new ArrayList<>();
        for (ArrayList<String> line : formattedOpenings) {
            if (moves.size() == 0) {
                options.add(line.get(0));
            } else {
                for (int i = 0; i < moves.size(); i++) {
                    if (moves.get(i).equals(line.get(i))) {
                        if (i == moves.size() - 1) {
                            options.add(line.get(i + 1));
                        }
                    } else {
                        break;
                    }
                }
            }
        }
        HashSet<String> set = new HashSet<>(options);
        return new ArrayList<>(set);
    }

    public static long letterToFile(char letter) {
        int index = letter - 97;
        return MoveGeneration.files[index];
    }

    public static long pieceTypeBoard(char pieceType, boolean player, long[] board) {
        if (player) {
            switch (pieceType) {
                case 'R' -> {
                    return board[1];
                }
                case 'N' -> {
                    return board[2];
                }
                case 'B' -> {
                    return board[3];
                }
                case 'Q' -> {
                    return board[4];
                }
                case 'K' -> {
                    return board[5];
                }
            }
        } else {
            switch (pieceType) {
                case 'R' -> {
                    return board[7];
                }
                case 'N' -> {
                    return board[8];
                }
                case 'B' -> {
                    return board[9];
                }
                case 'Q' -> {
                    return board[10];
                }
                case 'K' -> {
                    return board[11];
                }
            }
        }
        return -1;
    }

    public static String getStartSquare(long pieces, String moveSquare, char pieceType, long occupied, boolean player, long[] board) {
        long attackSquares = 0, moveSquareBit = Main.stringMoveToLong(moveSquare), pp, pr, pn, pb, pq, pk, op, or, on, ob, oq, ok;
        int pieceNum;
        if (player) {
            pp = board[0];
            pr = board[1];
            pn = board[2];
            pb = board[3];
            pq = board[4];
            pk = board[5];
            op = board[6];
            or = board[7];
            on = board[8];
            ob = board[9];
            oq = board[10];
            ok = board[11];
        } else {
            op = board[0];
            or = board[1];
            on = board[2];
            ob = board[3];
            oq = board[4];
            ok = board[5];
            pp = board[6];
            pr = board[7];
            pn = board[8];
            pb = board[9];
            pq = board[10];
            pk = board[11];
        }
        long enemyPieces = op | or | on | ob | oq | ok, playerPieces = pp | pr | pn | pb | pq | pk, legalSquares;
        ArrayList<Long>[] pinnedSquares = MoveGeneration.pinnedSquares(pk, or, ob, oq, enemyPieces, playerPieces);
        ArrayList<Long> individualPiece = MoveGeneration.separateBits(pieces);
        for (long piece : individualPiece) {
            if (pinnedSquares[0].contains(piece)) {
                legalSquares = pinnedSquares[1].get(pinnedSquares[0].indexOf(piece));
            } else {
                legalSquares = -1;
            }
            pieceNum = MoveGeneration.bitsToMove(piece);
            switch (pieceType) {
                case 'R' -> attackSquares = MagicBitboards.getRookAttacks(pieceNum, occupied);
                case 'N' -> attackSquares = MoveGeneration.knightMoves(piece, 0L);
                case 'B' -> attackSquares = MagicBitboards.getBishopAttacks(pieceNum, occupied);
                case 'Q' ->
                        attackSquares = MagicBitboards.getRookAttacks(pieceNum, occupied) | MagicBitboards.getBishopAttacks(pieceNum, occupied);
            }
            attackSquares &= legalSquares;
            if ((attackSquares & moveSquareBit) == moveSquareBit) {
                return MoveGeneration.moveToStringMove(piece);
            }
        }
        return "";
    }

    public static String getPawnStartSquare(long pawns, String moveSquare, boolean player, long[] board) {
        long attackSquares, moveSquareBit = Main.stringMoveToLong(moveSquare), pp, pr, pn, pb, pq, pk, op, or, on, ob, oq, ok;
        if (player) {
            pp = board[0];
            pr = board[1];
            pn = board[2];
            pb = board[3];
            pq = board[4];
            pk = board[5];
            op = board[6];
            or = board[7];
            on = board[8];
            ob = board[9];
            oq = board[10];
            ok = board[11];
        } else {
            op = board[0];
            or = board[1];
            on = board[2];
            ob = board[3];
            oq = board[4];
            ok = board[5];
            pp = board[6];
            pr = board[7];
            pn = board[8];
            pb = board[9];
            pq = board[10];
            pk = board[11];
        }
        ArrayList<Long> enPassant, individualPiece = MoveGeneration.separateBits(pawns);
        long enemyPieces = op | or | on | ob | oq | ok, playerPieces = pp | pr | pn | pb | pq | pk, legalSquares;
        ArrayList<Long>[] pinnedSquares = MoveGeneration.pinnedSquares(pk, or, ob, oq, enemyPieces, playerPieces);
        if (player) {
            enPassant = MoveGeneration.separateBits(MoveGeneration.ranks[3]);
        } else {
            enPassant = MoveGeneration.separateBits(MoveGeneration.ranks[4]);
        }
        for (long pawn : individualPiece) {
            if (pinnedSquares[0].contains(pawn)) {
                legalSquares = pinnedSquares[1].get(pinnedSquares[0].indexOf(pawn));
            } else {
                legalSquares = -1;
            }
            for (long pawnPush : enPassant) {
                attackSquares = MoveGeneration.pawnMoves(pawn, playerPieces, enemyPieces, player, pawnPush & ~playerPieces);
                attackSquares &= legalSquares;
                if ((attackSquares & moveSquareBit) == moveSquareBit) {
                    return MoveGeneration.moveToStringMove(pawn);
                }
            }
        }
        return "";
    }
}
