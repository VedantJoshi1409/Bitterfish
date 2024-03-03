import java.util.ArrayList;
import java.util.Arrays;

public class PlayerGame {
    public static Long[] playerMove(Long[] board, boolean player, String[][] stringBoard) {

        long pp, pr, pn, pb, pq, pk, op, or, on, ob, oq, ok, tempPp, tempPr, tempPn, tempPb, tempPq, tempPk, playerPieces, enemyPieces, pieceBit = 0, moveBit, castle = board[13], tempEnemyPieces;
        char pieceType = 0;
        boolean arrayPresent = false, reversible;
        int tempClick, piece = -1;
        Long[] position = new Long[board.length], tempPosition;
        int[] indexes = new int[12];
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
            for (int i = 0; i < 12; i++) {
                indexes[i] = i;
            }
        } else {
            pp = board[6];
            pr = board[7];
            pn = board[8];
            pb = board[9];
            pq = board[10];
            pk = board[11];
            op = board[0];
            or = board[1];
            on = board[2];
            ob = board[3];
            oq = board[4];
            ok = board[5];
            indexes[0] = 6;
            indexes[1] = 7;
            indexes[2] = 8;
            indexes[3] = 9;
            indexes[4] = 10;
            indexes[5] = 11;
            indexes[6] = 0;
            indexes[7] = 1;
            indexes[8] = 2;
            indexes[9] = 3;
            indexes[10] = 4;
            indexes[11] = 5;
        }
        playerPieces = pp | pr | pn | pb | pq | pk;
        enemyPieces = op | or | on | ob | oq | ok;
        ArrayList<Long[]> possibleMoves = MoveGeneration.possibleMoves(player, board);
        if (possibleMoves.size() == 1 && possibleMoves.get(0)[18] != 0) {
            BoardPanel.clickedPiece = -1;
            return possibleMoves.get(0);
        }
        while (true) {
            reversible = true;
            tempEnemyPieces = 0;
            tempPp = pp;
            tempPr = pr;
            tempPn = pn;
            tempPb = pb;
            tempPq = pq;
            tempPk = pk;
            BoardPanel.clickedPiece = -1;
            while (BoardPanel.clickedPiece == -1) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                }
            }
            tempClick = (BoardPanel.clickedPiece / 10) * 8 + BoardPanel.clickedPiece % 10;
            if ((1L << tempClick & playerPieces) == 1L << tempClick) {
                piece = tempClick;
                pieceBit = 1L << piece;
                Main.frame.repaint();
                pieceType = stringBoard[piece / 8][piece % 8].toLowerCase().charAt(0);
            } else if (piece != -1) {
                moveBit = 1L << tempClick;
                switch (pieceType) {
                    case 'p' -> tempPp = (pp & ~pieceBit) | moveBit;
                    case 'r' -> tempPr = (pr & ~pieceBit) | moveBit;
                    case 'n' -> tempPn = (pn & ~pieceBit) | moveBit;
                    case 'b' -> tempPb = (pb & ~pieceBit) | moveBit;
                    case 'q' -> tempPq = (pq & ~pieceBit) | moveBit;
                    case 'k' -> tempPk = moveBit;
                }
                position[indexes[0]] = tempPp;
                position[indexes[1]] = tempPr;
                position[indexes[2]] = tempPn;
                position[indexes[3]] = tempPb;
                position[indexes[4]] = tempPq;
                position[indexes[5]] = tempPk;
                position[indexes[6]] = op & ~moveBit;
                position[indexes[7]] = or & ~moveBit;
                position[indexes[8]] = on & ~moveBit;
                position[indexes[9]] = ob & ~moveBit;
                position[indexes[10]] = oq & ~moveBit;
                position[indexes[11]] = ok & ~moveBit;
                if (pieceType == 'p') {
                    reversible = false;
                    if (doublePushCheck(pieceBit, moveBit, player)) {
                        position[12] = moveBit;
                    } else if (enPassentCheck(pieceBit, moveBit, enemyPieces)) {
                        position[indexes[6]] &= ~Main.previousDoublePawnPush;
                    } else if (promotionCheck(moveBit, player)) {
                        Main.promotion = moveBit;
                        Main.frame.repaint();
                        while (Main.promotion == moveBit) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException ignored) {
                            }
                        }
                        position[indexes[0]] &= ~moveBit;
                        if (Main.promotion == 0) {
                            position[indexes[4]] |= moveBit;
                        } else if (Main.promotion == 1) {
                            position[indexes[3]] |= moveBit;
                        } else if (Main.promotion == 2) {
                            position[indexes[1]] |= moveBit;
                        } else if (Main.promotion == 3) {
                            position[indexes[2]] |= moveBit;
                        }
                        Main.promotion = 0;
                    }
                } else if (pieceType == 'k') {
                    if (player) {
                        if (shortCastle(pieceBit, moveBit, true)) {
                            reversible = false;
                            position[indexes[5]] = MoveGeneration.wSCastleBit;
                            position[indexes[1]] ^= MoveGeneration.wSCastleRook;
                        } else if (longCastle(pieceBit, moveBit, true)) {
                            reversible = false;
                            position[indexes[5]] = MoveGeneration.wLCastleBit;
                            position[indexes[1]] ^= MoveGeneration.wLCastleRook;
                            position[16] = MoveGeneration.wLCastleBit;
                            position[17] = 13L;
                        }
                    } else {
                        if (shortCastle(pieceBit, moveBit, false)) {
                            reversible = false;
                            position[indexes[5]] = MoveGeneration.bSCastleBit;
                            position[indexes[1]] ^= MoveGeneration.bSCastleRook;
                        } else if (longCastle(pieceBit, moveBit, false)) {
                            reversible = false;
                            position[indexes[5]] = MoveGeneration.bLCastleBit;
                            position[indexes[1]] ^= MoveGeneration.bLCastleRook;
                        }
                    }
                }
                for (int i = 6; i < 12; i++) {
                    tempEnemyPieces |= position[indexes[i]];
                }
                if ((tempEnemyPieces != enemyPieces) || !reversible) {
                    TranspositionTable.transpositionTable.clear();
                }
                for (Long[] possibleMove : possibleMoves) {
                    tempPosition = possibleMove.clone();
                    for (int i = 12; i < position.length; i++) {
                        position[i] = 0L;
                        tempPosition[i] = 0L;
                    }
                    if (Arrays.equals(tempPosition, position)) {
                        position = possibleMove;
                        arrayPresent = true;
                        break;
                    }
                }
                if (arrayPresent) {
                    break;
                }
            }
        }
        BoardPanel.possibleMoves = 0;
        return position;
    }

    public static boolean doublePushCheck(long piece, long move, boolean player) {
        long startRank, targetRank;
        if (player) {
            targetRank = piece >> 16;
            startRank = MoveGeneration.ranks[6];
        } else {
            targetRank = piece << 16;
            startRank = MoveGeneration.ranks[1];
        }
        if ((piece & startRank) == piece && move == targetRank) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean shortCastle(long piece, long move, boolean player) {
        if (player) {
            return (piece == 1152921504606846976L) && (move == MoveGeneration.wSCastleBit);
        } else {
            return (piece == 16) && (move == MoveGeneration.bSCastleBit);
        }
    }

    public static boolean longCastle(long piece, long move, boolean player) {
        if (player) {
            return (piece == 1152921504606846976L) && (move == MoveGeneration.wLCastleBit);
        } else {
            return (piece == 16) && (move == MoveGeneration.bLCastleBit);
        }
    }

    public static boolean enPassentCheck(long piece, long move, long enemyPieces) {
        int pieceNum = MoveGeneration.bitsToMove(piece);
        int moveNum = MoveGeneration.bitsToMove(move);
        if (pieceNum % 8 == moveNum % 8) {
            return false;
        }
        return (move & enemyPieces) == 0;
    }

    public static boolean promotionCheck(long move, boolean player) {
        if (player) {
            return ((move & MoveGeneration.ranks[0]) == move);
        } else {
            return ((move & MoveGeneration.ranks[7]) == move);
        }
    }
}
