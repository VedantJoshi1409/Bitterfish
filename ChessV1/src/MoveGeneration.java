import java.util.ArrayList;

public class MoveGeneration {
    public static final long[] files = {72340172838076673L, 144680345676153346L, 289360691352306692L, 578721382704613384L, 1157442765409226768L, 2314885530818453536L, 4629771061636907072L, -9187201950435737472L};
    public static final long[] ranks = {255L, 65280L, 16711680L, 4278190080L, 1095216660480L, 280375465082880L, 71776119061217280L, -72057594037927936L};
    public static final long knightSpan = 44272527353856L, kingSpan = 61745389371392L;
    public static final long wSCastleBit = 4611686018427387904L, wLCastleBit = 288230376151711744L, wLCastleAttackSquares = 864691128455135232L, bSCastleBit = 64L, bLCastleBit = 4L, bLCastleAttackSquares = 12, bSCastleSquares = 96L, bLCastleSquares = 14L, wSCastleSquares = 6917529027641081856L, wLCastleSquares = 1008806316530991104L, bSCastleRook = 160L, bLCastleRook = 9L, wSCastleRook = -6917529027641081856L, wLCastleRook = 648518346341351424L;

    public static ArrayList<Long> separateBits(long bits) {
        ArrayList<Long> separatedBits = new ArrayList<>();
        for (int i = 0; i < 64; i++) {
            if ((bits >> i & 1) == 1) {
                separatedBits.add(1L << i);
            }
        }
        return separatedBits;
    }

    public static boolean oneValueCheck(long bits) {
        return (bits != 0 && ((bits & (bits - 1)) == 0));
    }

    public static int bitsToMove(long bits) {
        for (int i = 0; i < 64; i++) {
            if (((bits >> i) & 1) == 1) {
                return i;
            }
        }
        return -1;
    }

    public static String moveToStringMove(long pieceBit) {
        int piece = bitsToMove(pieceBit);
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

    public static long singlePawnAttacks(long movingPiece, boolean player) {
        long attack;
        if (player) {
            attack = movingPiece >> 9 | movingPiece >> 7;
        } else {
            attack = movingPiece << 9 | movingPiece << 7;
        }
        if ((movingPiece & files[0]) == movingPiece) {
            attack &= ~files[7];
        } else if ((movingPiece & files[7]) == movingPiece) {
            attack &= ~files[0];
        }
        return attack;
    }

    public static long pawnAttacks(long movingPiece, boolean player) {
        long attack = 0L;
        long currentAttack, currentMove;
        int move = bitsToMove(movingPiece);
        while (movingPiece != 0) {
            currentMove = 1L << move;
            if (player) {
                currentAttack = currentMove >> 9 | currentMove >> 7;
            } else {
                currentAttack = currentMove << 9 | currentMove << 7;
            }
            if ((currentMove & files[0]) == currentMove) {
                currentAttack &= ~files[7];
            } else if ((currentMove & files[7]) == currentMove) {
                currentAttack &= ~files[0];
            }
            attack |= currentAttack;
            movingPiece ^= 1L << move;
            move = bitsToMove(movingPiece);
        }
        return attack;
    }

    public static boolean bSCastlePossible(long occupied, long wp, long wr, long wn, long wb, long wq, long wk, long br) {
        if ((128L & br) != 128L) {
            return false;
        }
        if ((occupied & bSCastleSquares) != 0) {
            return false;
        }
        return (illegalSquares(occupied, 16L, wp, wr, wn, wb, wq, wk, false) & bSCastleSquares) == 0;
    }

    public static boolean bLCastlePossible(long occupied, long wp, long wr, long wn, long wb, long wq, long wk, long br) {
        if ((1L & br) != 1L) {
            return false;
        }
        if ((occupied & bLCastleSquares) != 0) {
            return false;
        }
        return (illegalSquares(occupied, 16L, wp, wr, wn, wb, wq, wk, false) & bLCastleAttackSquares) == 0;
    }

    public static boolean wSCastlePossible(long occupied, long bp, long br, long bn, long bb, long bq, long bk, long wr) {
        if ((-9223372036854775808L & wr) != -9223372036854775808L) {
            return false;
        }
        if ((occupied & wSCastleSquares) != 0) {
            return false;
        }
        return (illegalSquares(occupied, 1152921504606846976L, bp, br, bn, bb, bq, bk, true) & wSCastleSquares) == 0;
    }

    public static boolean wLCastlePossible(long occupied, long bp, long br, long bn, long bb, long bq, long bk, long wr) {
        if ((72057594037927936L & wr) != 72057594037927936L) {
            return false;
        }
        ;
        if ((occupied & wLCastleSquares) != 0) {
            return false;
        }
        return (illegalSquares(occupied, 1152921504606846976L, bp, br, bn, bb, bq, bk, true) & wLCastleAttackSquares) == 0;
    }

    public static long pawnMoves(long movingPiece, long playerPieces, long enemyPieces, boolean player, long doublePawnPush) {
        boolean temp;
        long occupied = enemyPieces | playerPieces;
        long leftPawn, rightPawn, upPawn, oneUp, enPassant = 0L;
        int doublePawnPushMove, file;
        if (doublePawnPush != 0L) {
            doublePawnPushMove = bitsToMove(doublePawnPush);
            file = doublePawnPushMove % 8;
            if (file == 0) {
                temp = ((movingPiece & files[1]) == movingPiece);
            } else if (file == 7) {
                temp = ((movingPiece & files[6]) == movingPiece);
            } else {
                temp = ((movingPiece & files[file - 1]) == movingPiece || (movingPiece & files[file + 1]) == movingPiece);
            }
            if (temp) {
                if (player) {
                    if ((movingPiece & ranks[3]) == movingPiece) {
                        enPassant = doublePawnPush >> 8;
                    }
                } else {
                    if ((movingPiece & ranks[4]) == movingPiece) {
                        enPassant = doublePawnPush << 8;
                    }
                }
            }
        }
        if (player) {
            leftPawn = movingPiece >> 9;
            rightPawn = movingPiece >> 7;
            upPawn = movingPiece >> 8;
            oneUp = (leftPawn & enemyPieces) | (rightPawn & enemyPieces) | (upPawn & ~occupied);
            if ((movingPiece & ranks[6]) == movingPiece) {
                if ((oneUp & upPawn) != upPawn) {
                    return oneUp;
                } else {
                    oneUp |= ((movingPiece >> 16) & (~occupied));
                }
            }
        } else {
            leftPawn = movingPiece << 9;
            rightPawn = movingPiece << 7;
            upPawn = movingPiece << 8;
            oneUp = (leftPawn & enemyPieces) | (rightPawn & enemyPieces) | (upPawn & ~occupied);
            if ((movingPiece & ranks[1]) == movingPiece) {
                if ((oneUp & upPawn) != upPawn) {
                    return oneUp;
                } else {
                    oneUp |= ((movingPiece << 16) & (~occupied));
                }
            }
        }
        if ((movingPiece & files[7]) == movingPiece) {
            oneUp &= ~files[0];
        } else if ((movingPiece & files[0]) == movingPiece) {
            oneUp &= ~files[7];
        }
        return oneUp | enPassant;
    }

    public static long knightMoves(long movingPiece, long pieces) {
        long currentKnightMoves, knightMoves = 0;
        ArrayList<Long> knights = separateBits(movingPiece);
        for (Long knight : knights) {
            int move = bitsToMove(knight);
            if (move < 28) {
                currentKnightMoves = knightSpan >> 28 - move;
            } else if (move > 28) {
                currentKnightMoves = knightSpan << move - 28;
            } else {
                currentKnightMoves = knightSpan;
            }
            if (move % 8 < 2) {
                currentKnightMoves = currentKnightMoves ^ ((currentKnightMoves & files[7]) | (currentKnightMoves & files[6]));
            } else if (move % 8 > 5) {
                currentKnightMoves = currentKnightMoves ^ ((currentKnightMoves & files[0]) | (currentKnightMoves & files[1]));
            }
            knightMoves |= currentKnightMoves;
        }

        return knightMoves & ~pieces;
    }

    public static long rookMoves(long movingPiece, long occupied, long playerPieces) {
        int move = bitsToMove(movingPiece);
        long attack = 0L;
        if (movingPiece >> move == 1) {
            return MagicBitboards.getRookAttacks(move, occupied) & ~playerPieces;
        } else {
            while (movingPiece != 0) {
                attack |= MagicBitboards.getRookAttacks(move, occupied) & ~playerPieces;
                movingPiece ^= 1L << move;
                move = bitsToMove(movingPiece);
            }
            return attack;
        }
    }

    public static long bishopMoves(long movingPiece, long occupied, long playerPieces) {
        int move = bitsToMove(movingPiece);
        long attack = 0L;
        if (movingPiece >> move == 1) {
            return MagicBitboards.getBishopAttacks(move, occupied) & ~playerPieces;
        } else {
            while (movingPiece != 0) {
                attack |= MagicBitboards.getBishopAttacks(move, occupied) & ~playerPieces;
                movingPiece ^= 1L << move;
                move = bitsToMove(movingPiece);
            }
            return attack;
        }
    }

    public static long queenMoves(long movingPiece, long occupied, long playerPieces) {
        int move = bitsToMove(movingPiece);
        long attack = 0L;
        if (movingPiece >> move == 1) {
            return (MagicBitboards.getBishopAttacks(move, occupied) | MagicBitboards.getRookAttacks(move, occupied)) & ~playerPieces;
        } else {
            while (movingPiece != 0) {
                attack |= (MagicBitboards.getBishopAttacks(move, occupied) | MagicBitboards.getRookAttacks(move, occupied)) & ~playerPieces;
                movingPiece ^= 1L << move;
                move = bitsToMove(movingPiece);
            }
            return attack;
        }
    }

    public static long kingMoves(long movingPiece, long playerPieces, long op, long or, long on, long ob, long oq, long ok, long pr, boolean shortMoved, boolean longMoved, boolean player) {
        long enemyPieces = op | or | on | ob | oq | ok;
        long occupied = playerPieces | enemyPieces;
        long castleMoves = 0L;
        long illegalSquares = illegalSquares(occupied, movingPiece, op, or, on, ob, oq, ok, player);
        long kingMoves;
        int move = bitsToMove(movingPiece);
        if (move < 36) {
            kingMoves = kingSpan >> 36 - move;
        } else if (move > 36) {
            kingMoves = kingSpan << move - 36;
        } else {
            kingMoves = kingSpan;
        }
        if ((movingPiece & files[0]) == movingPiece) {
            kingMoves &= ~files[7];
        } else if ((movingPiece & files[7]) == movingPiece) {
            kingMoves &= ~files[0];
        }
        if (player) {
            if (!shortMoved) {
                if (wSCastlePossible(occupied, op, or, on, ob, oq, ok, pr)) {
                    castleMoves |= wSCastleBit;
                }
            }
            if (!longMoved) {
                if (wLCastlePossible(occupied, op, or, on, ob, oq, ok, pr)) {
                    castleMoves |= wLCastleBit;
                }
            }
        } else {
            if (!shortMoved) {
                if (bSCastlePossible(occupied, op, or, on, ob, oq, ok, pr)) {
                    castleMoves |= bSCastleBit;
                }
            }
            if (!longMoved) {
                if (bLCastlePossible(occupied, op, or, on, ob, oq, ok, pr)) {
                    castleMoves |= bLCastleBit;
                }
            }
        }
        return (kingMoves & ~playerPieces) & ~illegalSquares | castleMoves;
    }

    public static long illegalSquares(long occupied, long king, long enemyPawn, long enemyRook, long enemyKnight, long enemyBishop, long enemyQueen, long enemyKing, boolean player) {
        long noKing = occupied & ~king, file = -1, fileNum;
        enemyKing = bitsToMove(enemyKing);
        fileNum = enemyKing % 8;
        if (fileNum == 0) {
            file = files[0] | files[1];
        } else if (fileNum == 7) {
            file = files[6] | files[7];
        }
        if (enemyKing < 36) {
            enemyKing = kingSpan >> 36 - enemyKing;
        } else if (enemyKing > 36) {
            enemyKing = kingSpan << enemyKing - 36;
        } else {
            enemyKing = kingSpan;
        }
        enemyKing &= file;
        return pawnAttacks(enemyPawn, !player) | queenMoves(enemyQueen, noKing, 0) | bishopMoves(enemyBishop, noKing, 0) | rookMoves(enemyRook, noKing, 0) | knightMoves(enemyKnight, 0) | enemyKing;
    }

    public static ArrayList<Long> attackedSquares(long king, long enemyRook, long enemyBishop, long enemyQueen, long enemyPieces) {
        ArrayList<Long> moves = new ArrayList<>(), rooks = separateBits(enemyRook), bishops = separateBits(enemyBishop), queens = separateBits(enemyQueen);
        int queenSize = queens.size(), rookSize = rooks.size(), bishopSize = bishops.size();
        long rookMoves, bishopMoves, queenMoves, tempPiece, kingOccupied = enemyPieces | king;
        long kingStraightRays = rookMoves(king, enemyPieces, 0L);
        long kingDiagonalRays = bishopMoves(king, enemyPieces, 0L);
        for (int i = 0, j = 0, h = 0; i < rookSize || j < bishopSize || h < queenSize; i++, j++, h++) {
            if (i < rookSize) {
                tempPiece = rooks.get(i);
                if ((tempPiece & kingStraightRays) != 0L) {
                    rookMoves = rookMoves(tempPiece, kingOccupied, enemyPieces);
                    moves.add((rookMoves | tempPiece) & kingStraightRays);
                }
            }
            if (j < bishopSize) {
                tempPiece = bishops.get(j);
                if ((tempPiece & kingDiagonalRays) != 0L) {
                    bishopMoves = bishopMoves(tempPiece, kingOccupied, enemyPieces);
                    moves.add((bishopMoves | tempPiece) & kingDiagonalRays);
                }
            }
            if (h < queenSize) {
                tempPiece = queens.get(h);
                if ((tempPiece & kingStraightRays) != 0L) {
                    queenMoves = rookMoves(tempPiece, kingOccupied, enemyPieces);
                    moves.add((queenMoves | tempPiece) & kingStraightRays);
                }
                if ((tempPiece & kingDiagonalRays) != 0L) {
                    queenMoves = bishopMoves(tempPiece, kingOccupied, enemyPieces);
                    moves.add((queenMoves | tempPiece) & kingDiagonalRays);
                }
            }

        }
        return moves;
    }

    public static ArrayList<Long>[] pinnedSquares(long king, long enemyRook, long enemyBishop, long enemyQueen, long enemyPieces, long playerPieces) {
        ArrayList<Long> attackedSquares = attackedSquares(king, enemyRook, enemyBishop, enemyQueen, enemyPieces);
        ArrayList<Long> pinnedPieces = new ArrayList<>(), pinnedAttack = new ArrayList<>();
        long tempPieces, tempAttacks;
        for (Long attackedSquare : attackedSquares) {
            tempAttacks = attackedSquare;
            tempPieces = tempAttacks & playerPieces;
            if (oneValueCheck(tempPieces)) {
                pinnedPieces.add(tempPieces);
                pinnedAttack.add(tempAttacks);
            }
        }
        return new ArrayList[]{pinnedPieces, pinnedAttack};
    }

    public static ArrayList<Long> checkSquares(long king, long occupied, long playerPieces, long enemyPawn, long enemyRook, long enemyKnight, long enemyBishop, long enemyQueen, long enemyPieces, boolean player) {
        ArrayList<Long> moves = new ArrayList<>(), rooks = separateBits(enemyRook), bishops = separateBits(enemyBishop), queens = separateBits(enemyQueen), pawns = separateBits(enemyPawn), knights = separateBits(enemyKnight);
        int queenSize = queens.size(), rookSize = rooks.size(), bishopSize = bishops.size(), knightSize = knights.size(), pawnSize = pawns.size();
        long rookMoves, bishopMoves, queenMoves, knightMoves, pawnMoves, tempPiece;
        long kingStraightRays = rookMoves(king, occupied, playerPieces);
        long kingDiagonalRays = bishopMoves(king, occupied, playerPieces);
        for (int r = 0, b = 0, q = 0, k = 0, p = 0; r < rookSize || b < bishopSize || q < queenSize || k < knightSize || p < pawnSize; r++, b++, q++, k++, p++) {
            if (p < pawnSize) {
                tempPiece = pawns.get(p);
                pawnMoves = singlePawnAttacks(tempPiece, !player);
                if ((pawnMoves & king) == king) {
                    moves.add(tempPiece);
                    return moves;
                }
            }
            if (r < rookSize) {
                tempPiece = rooks.get(r);
                if ((tempPiece & kingStraightRays) != 0L) {
                    rookMoves = rookMoves(tempPiece, occupied, enemyPieces);
                    moves.add((rookMoves | tempPiece) & kingStraightRays);
                }
            }
            if (b < bishopSize) {
                tempPiece = bishops.get(b);
                if ((tempPiece & kingDiagonalRays) != 0L) {
                    bishopMoves = bishopMoves(tempPiece, occupied, enemyPieces);
                    moves.add((bishopMoves | tempPiece) & kingDiagonalRays);
                }
            }
            if (q < queenSize) {
                tempPiece = queens.get(q);
                if ((tempPiece & kingStraightRays) != 0L) {
                    queenMoves = rookMoves(tempPiece, occupied, enemyPieces);
                    moves.add((queenMoves | tempPiece) & kingStraightRays);
                }
                if ((tempPiece & kingDiagonalRays) != 0L) {
                    queenMoves = bishopMoves(tempPiece, occupied, enemyPieces);
                    moves.add((queenMoves | tempPiece) & kingDiagonalRays);
                }
            }
            if (k < knightSize) {
                tempPiece = knights.get(k);
                knightMoves = knightMoves(tempPiece, enemyPieces);
                if ((knightMoves & king) == king) {
                    moves.add(tempPiece);
                }
            }
        }
        return moves;
    }

    public static ArrayList<Long>[] pawnMovesList(long movingPiece, long inverseOccupied, long enemyPieces, boolean player, long illegalSquares) {
        ArrayList<Long> moves = new ArrayList<>(), type = new ArrayList<>();
        long leftPawn = 0L, rightPawn = 0L, upPawn, doubleUp, oneUp, theType = 0;
        if (player) {
            if ((movingPiece & files[7]) == 0) {
                rightPawn = movingPiece >> 7 & enemyPieces & illegalSquares;
            }
            if ((movingPiece & files[0]) == 0) {
                leftPawn = movingPiece >> 9 & enemyPieces & illegalSquares;
            }
            upPawn = movingPiece >> 8;
            oneUp = upPawn & inverseOccupied;
            if ((movingPiece & ranks[6]) == movingPiece) {
                if (oneUp == upPawn) {
                    doubleUp = movingPiece >> 16 & inverseOccupied & illegalSquares;
                    if (doubleUp != 0) {
                        moves.add(doubleUp);
                        type.add(1L);
                    }
                }
            } else if ((movingPiece & ranks[1]) == movingPiece) {
                theType = 2;
            }
        } else {
            if ((movingPiece & files[7]) == 0) {
                rightPawn = movingPiece << 9 & enemyPieces & illegalSquares;
            }
            if ((movingPiece & files[0]) == 0) {
                leftPawn = movingPiece << 7 & enemyPieces & illegalSquares;
            }
            upPawn = movingPiece << 8;
            oneUp = upPawn & inverseOccupied;
            if ((movingPiece & ranks[1]) == movingPiece) {
                if (oneUp == upPawn) {
                    doubleUp = movingPiece << 16 & inverseOccupied & illegalSquares;
                    if (doubleUp != 0) {
                        moves.add(doubleUp);
                        type.add(1L);
                    }
                }
            } else if ((movingPiece & ranks[6]) == movingPiece) {
                theType = 2;
            }
        }
        oneUp &= illegalSquares;
        if (oneUp != 0L) {
            moves.add(oneUp);
            type.add(theType);
        }
        if (rightPawn != 0L) {
            moves.add(rightPawn);
            type.add(theType);
        }
        if (leftPawn != 0L) {
            moves.add(leftPawn);
            type.add(theType);
        }
        return new ArrayList[]{moves, type};
    }

    public static ArrayList<Long> knightMovesList(long movingPiece, long inversePieces, long illegalSquares) {
        ArrayList<Long> moves;
        int move = bitsToMove(movingPiece);
        long knightMoves;
        if (move < 28) {
            knightMoves = knightSpan >> 28 - move;
        } else if (move > 28) {
            knightMoves = knightSpan << move - 28;
        } else {
            knightMoves = knightSpan;
        }
        knightMoves &= inversePieces & illegalSquares;
        if (move % 8 < 2) {
            knightMoves = knightMoves & ~(files[6] | files[7]);
        } else if (move % 8 > 5) {
            knightMoves = knightMoves & ~(files[0] | files[1]);
        }
        moves = separateBits(knightMoves);
        return moves;
    }

    public static ArrayList<Long> rookMovesList(int piece, long occupied, long inversePlayerPieces, long illegalSquares) {
        long attacks = MagicBitboards.getRookAttacks(piece, occupied);
        attacks &= inversePlayerPieces & illegalSquares;
        return separateBits(attacks);
    }

    public static ArrayList<Long> bishopMovesList(int piece, long occupied, long inversePlayerPieces, long illegalSquares) {
        long attacks = MagicBitboards.getBishopAttacks(piece, occupied);
        attacks &= inversePlayerPieces & illegalSquares;
        return separateBits(attacks);
    }

    public static ArrayList<Long> queenMovesList(int piece, long occupied, long inversePlayerPieces, long illegalSquares) {
        ArrayList<Long> rook = rookMovesList(piece, occupied, inversePlayerPieces, illegalSquares), bishop = bishopMovesList(piece, occupied, inversePlayerPieces, illegalSquares);
        rook.addAll(bishop);
        return rook;
    }

    public static ArrayList<Long> kingMovesList(long movingPiece, long inversePlayerPieces, long op, long or, long on, long ob, long oq, long ok, long occupied, boolean player) {
        ArrayList<Long> moves = new ArrayList<>();
        long illegalSquares = ~illegalSquares(occupied, movingPiece, op, or, on, ob, oq, ok, player), kingMoves;
        int move = bitsToMove(movingPiece);
        if (move < 36) {
            kingMoves = kingSpan >> 36 - move;
        } else if (move > 36) {
            kingMoves = kingSpan << move - 36;
        } else {
            kingMoves = kingSpan;
        }
        kingMoves &= illegalSquares & inversePlayerPieces;
        if ((movingPiece & files[0]) == movingPiece) {
            kingMoves &= ~files[7];
        } else if ((movingPiece & files[7]) == movingPiece) {
            kingMoves &= ~files[0];
        }
        moves = separateBits(kingMoves);
        return moves;
    }

    public static boolean inCheck(long king, long occupied, long enemyPawn, long enemyRook, long enemyKnight, long enemyBishop, long enemyQueen, long enemyKing, boolean player) {
        return (king & illegalSquares(occupied, king, enemyPawn, enemyRook, enemyKnight, enemyBishop, enemyQueen, enemyKing, player)) == king;
    }

    public static long enPassant(long movingPiece, boolean player, long doublePawnPush, long illegalSquares) {
        int doublePawnPushMove, file;
        long enPassant = 0L;
        doublePawnPushMove = bitsToMove(doublePawnPush);
        file = doublePawnPushMove % 8;
        if (file == 7) {
            if ((movingPiece & files[file - 1]) == movingPiece) {
                if (player) {
                    if ((movingPiece & ranks[3]) == movingPiece) {
                        enPassant = doublePawnPush >> 8;
                    }
                } else {
                    if ((movingPiece & ranks[4]) == movingPiece) {
                        enPassant = doublePawnPush << 8;
                    }
                }
            }
        } else if (file == 0) {
            if ((movingPiece & files[file + 1]) == movingPiece) {
                if (player) {
                    if ((movingPiece & ranks[3]) == movingPiece) {
                        enPassant = doublePawnPush >> 8;
                    }
                } else {
                    if ((movingPiece & ranks[4]) == movingPiece) {
                        enPassant = doublePawnPush << 8;
                    }
                }
            }
        } else {
            if ((movingPiece & files[file - 1]) == movingPiece || (movingPiece & files[file + 1]) == movingPiece) {
                if (player) {
                    if ((movingPiece & ranks[3]) == movingPiece) {
                        enPassant = doublePawnPush >> 8;
                    }
                } else {
                    if ((movingPiece & ranks[4]) == movingPiece) {
                        enPassant = doublePawnPush << 8;
                    }
                }
            }
        }
        if ((illegalSquares & doublePawnPush) == illegalSquares) {
            illegalSquares = -1;
        }
        return enPassant & illegalSquares;
    }

    public static ArrayList<Long[]> possibleMoves(boolean player, Long[] board) {
        ArrayList<Long[]> moves = new ArrayList<>();
        Long[] individualMoves = new Long[23];
        System.arraycopy(board, 0, individualMoves, 0, 23);
        //1: wPawn
        //2: wRook
        //3: wKnight
        //4: wBishop
        //5: wQueen
        //6: wKing
        //7: bPawn
        //8: bRook
        //9: bKnight
        //10: bBishop
        //11: bQueen
        //12: bKing
        //13: enPassant Square
        //14: Castle rights
        //15: Position key
        //16: Moving piece
        //17: Move square
        //18: Move type: 00 for reversible and no capture/check 11 for non-reversible and capture/check
        //19: Mate flag
        //20: Capturing piece
        //21: Victim piece;
        //22: Enemy king in check
        //23: Castle state
        long pp, pr, pn, pb, pq, pk, op, or, on, ob, oq, ok, castle = board[13], key = board[14] ^ Zobrist.sideKey, baseKey, captureKey, tempOp, tempOr, tempOn, tempOb, tempOq, tempCastle, nonReversible = 2, reversible = 0, special = 1, base = 0, castleStates = board[22];
        int[] indexes = new int[12];
        individualMoves[12] = 0L;
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
        boolean shortMoved, longMoved;
        if (player) {
            shortMoved = (castle >> 3 & 1) == 1;
            longMoved = (castle >> 2 & 1) == 1;
        } else {
            shortMoved = (castle >> 1 & 1) == 1;
            longMoved = (castle & 1) == 1;
        }

        ArrayList<Long> checkSquares;
        long playerPieces = pp | pr | pn | pb | pq | pk, enemyPieces = op | or | on | ob | oq | ok, inversePlayerPieces = ~playerPieces, occupied = enemyPieces | playerPieces, inverseOccupied = ~occupied, currentMove, inverseTempMove, pawnMoveType, illegalSquares, checkingSquares = -1, doublePawnPush = board[12];
        int tempPieceMove, tempValue;
        boolean inCheck = false, doubleCheck = false;
        ArrayList<Long>[] pinnedSquares = pinnedSquares(pk, or, ob, oq, enemyPieces, playerPieces), pawnTemp;
        if (inCheck(pk, occupied, op, or, on, ob, oq, ok, player)) {
            inCheck = true;
            checkSquares = checkSquares(pk, occupied, playerPieces, op, or, on, ob, oq, enemyPieces, player);
            if (checkSquares.size() > 1) {
                doubleCheck = true;
            } else {
                checkingSquares = checkSquares.get(0);
            }
        }
        ArrayList<Long> temp, pPawn = separateBits(pp), pRook = separateBits(pr), pKnight = separateBits(pn), pBishop = separateBits(pb), pQueen = separateBits(pq);
        if (!doubleCheck) {
            base = nonReversible;
            individualMoves[19] = (long) indexes[0];
            for (long tempPiece : pPawn) {
                baseKey = key ^ Zobrist.pieceKeys[indexes[0]][bitsToMove(tempPiece)];
                individualMoves[15] = tempPiece;
                illegalSquares = 0;
                illegalSquares |= checkingSquares;
                if (pinnedSquares[0].contains(tempPiece)) {
                    illegalSquares &= pinnedSquares[1].get(pinnedSquares[0].indexOf(tempPiece));
                } else if (illegalSquares == 0) {
                    illegalSquares = -1;
                }
                pawnTemp = pawnMovesList(tempPiece, inverseOccupied, enemyPieces, player, illegalSquares);
                if (doublePawnPush != 0) {
                    long possiblePassant = enPassant(tempPiece, player, doublePawnPush, illegalSquares);
                    if (possiblePassant != 0) {
                        ArrayList<Long>[] passantPin = pinnedSquares(pk, or, ob, oq, enemyPieces & ~doublePawnPush, playerPieces);
                        if (!passantPin[0].contains(tempPiece)) {
                            individualMoves[17] = base | special;
                            individualMoves[indexes[0]] = (pp & ~tempPiece) | possiblePassant;
                            individualMoves[indexes[6]] = op & ~doublePawnPush;
                            individualMoves[indexes[7]] = or;
                            individualMoves[indexes[8]] = on;
                            individualMoves[indexes[9]] = ob;
                            individualMoves[indexes[10]] = oq;
                            individualMoves[14] = baseKey ^ Zobrist.pieceKeys[indexes[0]][bitsToMove(possiblePassant)] ^ Zobrist.pieceKeys[indexes[6]][bitsToMove(doublePawnPush)];
                            individualMoves[16] = possiblePassant;
                            individualMoves[20] = (long) indexes[6];
                            moves.add(individualMoves.clone());
                        }
                    }
                }
                for (int j = 0; j < pawnTemp[0].size(); j++) {
                    currentMove = pawnTemp[0].get(j);
                    tempValue = bitsToMove(currentMove);
                    inverseTempMove = ~currentMove;
                    pawnMoveType = pawnTemp[1].get((j));
                    tempOp = op & inverseTempMove;
                    tempOr = or & inverseTempMove;
                    tempOn = on & inverseTempMove;
                    tempOb = ob & inverseTempMove;
                    tempOq = oq & inverseTempMove;
                    if ((enemyPieces & currentMove) != 0) {
                        individualMoves[17] = base | special;
                        if (tempOp != op) {
                            individualMoves[20] = (long) indexes[6];
                            captureKey = baseKey ^ Zobrist.pieceKeys[indexes[6]][tempValue];
                        } else if (tempOr != or) {
                            individualMoves[20] = (long) indexes[7];
                            captureKey = baseKey ^ Zobrist.pieceKeys[indexes[7]][tempValue];
                        } else if (tempOn != on) {
                            individualMoves[20] = (long) indexes[8];
                            captureKey = baseKey ^ Zobrist.pieceKeys[indexes[8]][tempValue];
                        } else if (tempOb != ob) {
                            individualMoves[20] = (long) indexes[9];
                            captureKey = baseKey ^ Zobrist.pieceKeys[indexes[9]][tempValue];
                        } else {
                            individualMoves[20] = (long) indexes[10];
                            captureKey = baseKey ^ Zobrist.pieceKeys[indexes[10]][tempValue];
                        }
                    } else {
                        individualMoves[17] = base;
                        captureKey = baseKey;
                    }
                    if ((pawnAttacks(currentMove, player) & ok) == ok) {
                        individualMoves[17] = base|special;
                        individualMoves[21] = 1L;
                    } else {
                        individualMoves[21] = 0L;
                    }
                    if (pawnMoveType == 1) {
                        individualMoves[12] = currentMove;
                        captureKey ^= Zobrist.enPassantKeys[tempValue];
                    } else if (pawnMoveType == 2) {
                        individualMoves[indexes[0]] = pp & ~tempPiece;
                        individualMoves[indexes[1]] = pr | currentMove;
                        individualMoves[indexes[6]] = tempOp;
                        individualMoves[indexes[7]] = tempOr;
                        individualMoves[indexes[8]] = tempOn;
                        individualMoves[indexes[9]] = tempOb;
                        individualMoves[indexes[10]] = tempOq;
                        individualMoves[14] = captureKey ^ Zobrist.pieceKeys[indexes[1]][tempValue];
                        individualMoves[16] = currentMove;
                        moves.add(individualMoves.clone());
                        individualMoves[indexes[1]] = pr;
                        individualMoves[indexes[2]] = pn | currentMove;
                        individualMoves[14] = captureKey ^ Zobrist.pieceKeys[indexes[2]][tempValue];
                        moves.add(individualMoves.clone());
                        individualMoves[indexes[2]] = pn;
                        individualMoves[indexes[3]] = pb | currentMove;
                        individualMoves[14] = captureKey ^ Zobrist.pieceKeys[indexes[3]][tempValue];
                        moves.add(individualMoves.clone());
                        individualMoves[indexes[3]] = pb;
                        individualMoves[indexes[4]] = pq | currentMove;
                        individualMoves[14] = captureKey ^ Zobrist.pieceKeys[indexes[4]][tempValue];
                        moves.add(individualMoves.clone());
                        individualMoves[indexes[4]] = pq;
                    }
                    if (pawnMoveType != 2) {
                        captureKey ^= Zobrist.pieceKeys[indexes[0]][tempValue];
                        individualMoves[indexes[0]] = (pp & ~tempPiece) | currentMove;
                        individualMoves[indexes[6]] = tempOp;
                        individualMoves[indexes[7]] = tempOr;
                        individualMoves[indexes[8]] = tempOn;
                        individualMoves[indexes[9]] = tempOb;
                        individualMoves[indexes[10]] = tempOq;
                        individualMoves[14] = captureKey;
                        individualMoves[16] = currentMove;
                        moves.add(individualMoves.clone());
                        individualMoves[12] = 0L;
                    }
                }
            }
            individualMoves[indexes[0]] = pp;
            individualMoves[19] = (long) indexes[1];
            base = reversible;
            for (long tempPiece : pRook) {
                individualMoves[15] = tempPiece;
                tempPieceMove = bitsToMove(tempPiece);
                baseKey = key ^ Zobrist.pieceKeys[indexes[1]][tempPieceMove];
                illegalSquares = 0;
                illegalSquares |= checkingSquares;
                if (pinnedSquares[0].contains(tempPiece)) {
                    illegalSquares &= pinnedSquares[1].get(pinnedSquares[0].indexOf(tempPiece));
                } else if (illegalSquares == 0) {
                    illegalSquares = -1;
                }
                temp = rookMovesList(tempPieceMove, occupied, inversePlayerPieces, illegalSquares);
                for (long value : temp) {
                    tempValue = bitsToMove(value);
                    inverseTempMove = ~value;
                    tempOp = op & inverseTempMove;
                    tempOr = or & inverseTempMove;
                    tempOn = on & inverseTempMove;
                    tempOb = ob & inverseTempMove;
                    tempOq = oq & inverseTempMove;
                    if ((enemyPieces & value) != 0) {
                        individualMoves[17] = base | special;
                        if (tempOp != op) {
                            individualMoves[20] = (long) indexes[6];
                            captureKey = baseKey ^ Zobrist.pieceKeys[indexes[6]][tempValue];
                        } else if (tempOr != or) {
                            individualMoves[20] = (long) indexes[7];
                            captureKey = baseKey ^ Zobrist.pieceKeys[indexes[7]][tempValue];
                        } else if (tempOn != on) {
                            individualMoves[20] = (long) indexes[8];
                            captureKey = baseKey ^ Zobrist.pieceKeys[indexes[8]][tempValue];
                        } else if (tempOb != ob) {
                            individualMoves[20] = (long) indexes[9];
                            captureKey = baseKey ^ Zobrist.pieceKeys[indexes[9]][tempValue];
                        } else {
                            individualMoves[20] = (long) indexes[10];
                            captureKey = baseKey ^ Zobrist.pieceKeys[indexes[10]][tempValue];
                        }
                    } else {
                        individualMoves[17] = base;
                        captureKey = baseKey;
                    }
                    if ((MagicBitboards.getRookAttacks(tempValue, occupied) & ok) == ok) {
                        individualMoves[17] = base|special;
                        individualMoves[21] = 1L;
                    } else {
                        individualMoves[21] = 0L;
                    }
                    if (!(shortMoved && longMoved)) {
                        captureKey ^= Zobrist.castleKeys[(int) castle];
                        if (player) {
                            if (tempPiece == 72057594037927936L) {
                                individualMoves[17] |= nonReversible;
                                tempCastle = castle | 4;
                                individualMoves[13] = tempCastle;
                                captureKey ^= Zobrist.castleKeys[(int) tempCastle];
                            } else if (tempPiece == -9223372036854775808L) {
                                individualMoves[17] |= nonReversible;
                                tempCastle = castle | 8;
                                individualMoves[13] = tempCastle;
                                captureKey ^= Zobrist.castleKeys[(int) tempCastle];
                            }
                        } else {
                            if (tempPiece == 128) {
                                individualMoves[17] |= nonReversible;
                                tempCastle = castle | 2;
                                individualMoves[13] = tempCastle;
                                captureKey ^= Zobrist.castleKeys[(int) tempCastle];
                            } else if (tempPiece == 1) {
                                individualMoves[17] |= nonReversible;
                                tempCastle = castle | 1;
                                individualMoves[13] = tempCastle;
                                captureKey ^= Zobrist.castleKeys[(int) tempCastle];
                            }
                        }
                    }
                    captureKey ^= Zobrist.pieceKeys[indexes[1]][tempValue];
                    individualMoves[indexes[1]] = (pr & ~tempPiece) | value;
                    individualMoves[indexes[6]] = tempOp;
                    individualMoves[indexes[7]] = tempOr;
                    individualMoves[indexes[8]] = tempOn;
                    individualMoves[indexes[9]] = tempOb;
                    individualMoves[indexes[10]] = tempOq;
                    individualMoves[14] = captureKey;
                    individualMoves[16] = value;
                    moves.add(individualMoves.clone());
                    individualMoves[13] = castle;
                }
            }
            individualMoves[indexes[1]] = pr;
            individualMoves[19] = (long) indexes[2];
            for (long tempPiece : pKnight) {
                baseKey = key ^ Zobrist.pieceKeys[indexes[2]][bitsToMove(tempPiece)];
                individualMoves[15] = tempPiece;
                illegalSquares = 0;
                illegalSquares |= checkingSquares;
                if (pinnedSquares[0].contains(tempPiece)) {
                    illegalSquares &= pinnedSquares[1].get(pinnedSquares[0].indexOf(tempPiece));
                } else if (illegalSquares == 0) {
                    illegalSquares = -1;
                }
                temp = knightMovesList(tempPiece, inversePlayerPieces, illegalSquares);
                for (long tempMove : temp) {
                    tempValue = bitsToMove(tempMove);
                    inverseTempMove = ~tempMove;
                    tempOp = op & inverseTempMove;
                    tempOr = or & inverseTempMove;
                    tempOn = on & inverseTempMove;
                    tempOb = ob & inverseTempMove;
                    tempOq = oq & inverseTempMove;
                    if ((enemyPieces & tempMove) != 0) {
                        individualMoves[17] = base | special;
                        if (tempOp != op) {
                            individualMoves[20] = (long) indexes[6];
                            captureKey = baseKey ^ Zobrist.pieceKeys[indexes[6]][tempValue];
                        } else if (tempOr != or) {
                            individualMoves[20] = (long) indexes[7];
                            captureKey = baseKey ^ Zobrist.pieceKeys[indexes[7]][tempValue];
                        } else if (tempOn != on) {
                            individualMoves[20] = (long) indexes[8];
                            captureKey = baseKey ^ Zobrist.pieceKeys[indexes[8]][tempValue];
                        } else if (tempOb != ob) {
                            individualMoves[20] = (long) indexes[9];
                            captureKey = baseKey ^ Zobrist.pieceKeys[indexes[9]][tempValue];
                        } else {
                            individualMoves[20] = (long) indexes[10];
                            captureKey = baseKey ^ Zobrist.pieceKeys[indexes[10]][tempValue];
                        }
                    } else {
                        individualMoves[17] = base;
                        captureKey = baseKey;
                    }
                    if ((knightMoves(tempMove, playerPieces) & ok) == ok) {
                        individualMoves[17] = base|special;
                        individualMoves[21] = 1L;
                    } else {
                        individualMoves[21] = 0L;
                    }
                    captureKey ^= Zobrist.pieceKeys[indexes[2]][tempValue];
                    individualMoves[indexes[2]] = (pn & ~tempPiece) | tempMove;
                    individualMoves[indexes[6]] = tempOp;
                    individualMoves[indexes[7]] = tempOr;
                    individualMoves[indexes[8]] = tempOn;
                    individualMoves[indexes[9]] = tempOb;
                    individualMoves[indexes[10]] = tempOq;
                    individualMoves[14] = captureKey;
                    individualMoves[16] = tempMove;
                    moves.add(individualMoves.clone());
                }
            }
            individualMoves[indexes[2]] = pn;
            individualMoves[19] = (long) indexes[3];
            for (long tempPiece : pBishop) {
                baseKey = key ^ Zobrist.pieceKeys[indexes[3]][bitsToMove(tempPiece)];
                individualMoves[15] = tempPiece;
                tempPieceMove = bitsToMove(tempPiece);
                illegalSquares = 0;
                illegalSquares |= checkingSquares;
                if (pinnedSquares[0].contains(tempPiece)) {
                    illegalSquares &= pinnedSquares[1].get(pinnedSquares[0].indexOf(tempPiece));
                } else if (illegalSquares == 0) {
                    illegalSquares = -1;
                }
                temp = bishopMovesList(tempPieceMove, occupied, inversePlayerPieces, illegalSquares);
                for (long tempMove : temp) {
                    tempValue = bitsToMove(tempMove);
                    inverseTempMove = ~tempMove;
                    tempOp = op & inverseTempMove;
                    tempOr = or & inverseTempMove;
                    tempOn = on & inverseTempMove;
                    tempOb = ob & inverseTempMove;
                    tempOq = oq & inverseTempMove;
                    if ((enemyPieces & tempMove) != 0) {
                        individualMoves[17] = base | special;
                        if (tempOp != op) {
                            individualMoves[20] = (long) indexes[6];
                            captureKey = baseKey ^ Zobrist.pieceKeys[indexes[6]][tempValue];
                        } else if (tempOr != or) {
                            individualMoves[20] = (long) indexes[7];
                            captureKey = baseKey ^ Zobrist.pieceKeys[indexes[7]][tempValue];
                        } else if (tempOn != on) {
                            individualMoves[20] = (long) indexes[8];
                            captureKey = baseKey ^ Zobrist.pieceKeys[indexes[8]][tempValue];
                        } else if (tempOb != ob) {
                            individualMoves[20] = (long) indexes[9];
                            captureKey = baseKey ^ Zobrist.pieceKeys[indexes[9]][tempValue];
                        } else {
                            individualMoves[20] = (long) indexes[10];
                            captureKey = baseKey ^ Zobrist.pieceKeys[indexes[10]][tempValue];
                        }
                    } else {
                        individualMoves[17] = base;
                        captureKey = baseKey;
                    }
                    if ((MagicBitboards.getBishopAttacks(tempValue, occupied) & ok) == ok) {
                        individualMoves[17] = base|special;
                        individualMoves[21] = 1L;
                    } else {
                        individualMoves[21] = 0L;
                    }
                    captureKey ^= Zobrist.pieceKeys[indexes[3]][tempValue];
                    individualMoves[indexes[3]] = (pb & ~tempPiece) | tempMove;
                    individualMoves[indexes[6]] = tempOp;
                    individualMoves[indexes[7]] = tempOr;
                    individualMoves[indexes[8]] = tempOn;
                    individualMoves[indexes[9]] = tempOb;
                    individualMoves[indexes[10]] = tempOq;
                    individualMoves[14] = captureKey;
                    individualMoves[16] = tempMove;
                    moves.add(individualMoves.clone());
                }
            }
            individualMoves[indexes[3]] = pb;
            individualMoves[19] = (long) indexes[4];
            for (long tempPiece : pQueen) {
                baseKey = key ^ Zobrist.pieceKeys[indexes[4]][bitsToMove(tempPiece)];
                individualMoves[15] = tempPiece;
                tempPieceMove = bitsToMove(tempPiece);
                illegalSquares = 0;
                illegalSquares |= checkingSquares;
                if (pinnedSquares[0].contains(tempPiece)) {
                    illegalSquares &= pinnedSquares[1].get(pinnedSquares[0].indexOf(tempPiece));
                } else if (illegalSquares == 0) {
                    illegalSquares = -1;
                }
                temp = queenMovesList(tempPieceMove, occupied, inversePlayerPieces, illegalSquares);
                for (long tempMove : temp) {
                    tempValue = bitsToMove(tempMove);
                    inverseTempMove = ~tempMove;
                    tempOp = op & inverseTempMove;
                    tempOr = or & inverseTempMove;
                    tempOn = on & inverseTempMove;
                    tempOb = ob & inverseTempMove;
                    tempOq = oq & inverseTempMove;
                    if ((enemyPieces & tempMove) != 0) {
                        individualMoves[17] = base | special;
                        if (tempOp != op) {
                            individualMoves[20] = (long) indexes[6];
                            captureKey = baseKey ^ Zobrist.pieceKeys[indexes[6]][tempValue];
                        } else if (tempOr != or) {
                            individualMoves[20] = (long) indexes[7];
                            captureKey = baseKey ^ Zobrist.pieceKeys[indexes[7]][tempValue];
                        } else if (tempOn != on) {
                            individualMoves[20] = (long) indexes[8];
                            captureKey = baseKey ^ Zobrist.pieceKeys[indexes[8]][tempValue];
                        } else if (tempOb != ob) {
                            individualMoves[20] = (long) indexes[9];
                            captureKey = baseKey ^ Zobrist.pieceKeys[indexes[9]][tempValue];
                        } else {
                            individualMoves[20] = (long) indexes[10];
                            captureKey = baseKey ^ Zobrist.pieceKeys[indexes[10]][tempValue];
                        }
                    } else {
                        individualMoves[17] = base;
                        captureKey = baseKey;
                    }
                    if (((MagicBitboards.getRookAttacks(tempValue, occupied) | MagicBitboards.getBishopAttacks(tempValue, occupied)) & ok) == ok) {
                        individualMoves[17] = base|special;
                        individualMoves[21] = 1L;
                    } else {
                        individualMoves[21] = 0L;
                    }
                    captureKey ^= Zobrist.pieceKeys[indexes[4]][tempValue];
                    individualMoves[indexes[4]] = (pq & ~tempPiece) | tempMove;
                    individualMoves[indexes[6]] = tempOp;
                    individualMoves[indexes[7]] = tempOr;
                    individualMoves[indexes[8]] = tempOn;
                    individualMoves[indexes[9]] = tempOb;
                    individualMoves[indexes[10]] = tempOq;
                    individualMoves[14] = captureKey;
                    individualMoves[16] = tempMove;
                    moves.add(individualMoves.clone());
                }
            }
        }
        individualMoves[indexes[4]] = pq;
        individualMoves[19] = (long) indexes[5];
        temp = kingMovesList(pk, inversePlayerPieces, op, or, on, ob, oq, ok, occupied, player);
        individualMoves[15] = pk;
        baseKey = key ^ Zobrist.pieceKeys[indexes[5]][bitsToMove(pk)];
        if (!(shortMoved && longMoved)) {
            base = nonReversible;
            baseKey ^= Zobrist.castleKeys[(int) castle];
            if (player) {
                tempCastle = castle | 12;
            } else {
                tempCastle = castle | 3;
            }
            individualMoves[13] = tempCastle;
            baseKey ^= Zobrist.castleKeys[(int) tempCastle];
        }
        for (long tempMove : temp) {
            tempValue = bitsToMove(tempMove);
            inverseTempMove = ~tempMove;
            tempOp = op & inverseTempMove;
            tempOr = or & inverseTempMove;
            tempOn = on & inverseTempMove;
            tempOb = ob & inverseTempMove;
            tempOq = oq & inverseTempMove;
            if ((enemyPieces & tempMove) != 0) {
                individualMoves[17] = base | special;
                if (tempOp != op) {
                    individualMoves[20] = (long) indexes[6];
                    captureKey = baseKey ^ Zobrist.pieceKeys[indexes[6]][tempValue];
                } else if (tempOr != or) {
                    individualMoves[20] = (long) indexes[7];
                    captureKey = baseKey ^ Zobrist.pieceKeys[indexes[7]][tempValue];
                } else if (tempOn != on) {
                    individualMoves[20] = (long) indexes[8];
                    captureKey = baseKey ^ Zobrist.pieceKeys[indexes[8]][tempValue];
                } else if (tempOb != ob) {
                    individualMoves[20] = (long) indexes[9];
                    captureKey = baseKey ^ Zobrist.pieceKeys[indexes[9]][tempValue];
                } else {
                    individualMoves[20] = (long) indexes[10];
                    captureKey = baseKey ^ Zobrist.pieceKeys[indexes[10]][tempValue];
                }
            } else {
                individualMoves[17] = base;
                captureKey = baseKey;
            }
            captureKey ^= Zobrist.pieceKeys[indexes[5]][tempValue];
            individualMoves[indexes[5]] = tempMove;
            individualMoves[indexes[6]] = tempOp;
            individualMoves[indexes[7]] = tempOr;
            individualMoves[indexes[8]] = tempOn;
            individualMoves[indexes[9]] = tempOb;
            individualMoves[indexes[10]] = tempOq;
            individualMoves[14] = captureKey;
            individualMoves[16] = tempMove;
            moves.add(individualMoves.clone());
        }
        individualMoves[indexes[6]] = op;
        individualMoves[indexes[7]] = or;
        individualMoves[indexes[8]] = on;
        individualMoves[indexes[9]] = ob;
        individualMoves[indexes[10]] = oq;
        individualMoves[17] = nonReversible | special;
        if (!inCheck) {
            individualMoves[17] = nonReversible|special;
            if (player) {
                individualMoves[17] = castle|12;
                individualMoves[22] = castleStates|2;
                if (!shortMoved) {
                    if (wSCastlePossible(occupied, op, or, on, ob, oq, ok, pr)) {
                        individualMoves[indexes[1]] = pr ^ wSCastleRook;
                        individualMoves[indexes[5]] = wSCastleBit;
                        individualMoves[14] = baseKey ^ Zobrist.pieceKeys[indexes[1]][63] ^ Zobrist.pieceKeys[indexes[1]][61] ^ Zobrist.pieceKeys[indexes[5]][62];
                        individualMoves[16] = wSCastleBit;
                        moves.add(individualMoves.clone());
                    }
                }
                if (!longMoved) {
                    if (wLCastlePossible(occupied, op, or, on, ob, oq, ok, pr)) {
                        individualMoves[indexes[1]] = pr ^ wLCastleRook;
                        individualMoves[indexes[5]] = wLCastleBit;
                        individualMoves[14] = baseKey ^ Zobrist.pieceKeys[indexes[1]][56] ^ Zobrist.pieceKeys[indexes[1]][59] ^ Zobrist.pieceKeys[indexes[5]][58];
                        individualMoves[16] = wLCastleBit;
                        moves.add(individualMoves.clone());
                    }
                }
            } else {
                individualMoves[17] = castle|3;
                individualMoves[22] = castleStates|1;
                if (!shortMoved) {
                    if (bSCastlePossible(occupied, op, or, on, ob, oq, ok, pr)) {
                        individualMoves[indexes[1]] = pr ^ bSCastleRook;
                        individualMoves[indexes[5]] = bSCastleBit;
                        individualMoves[14] = baseKey ^ Zobrist.pieceKeys[indexes[1]][7] ^ Zobrist.pieceKeys[indexes[1]][5] ^ Zobrist.pieceKeys[indexes[5]][6];
                        individualMoves[16] = bSCastleBit;
                        moves.add(individualMoves.clone());
                    }
                }
                if (!longMoved) {
                    if (bLCastlePossible(occupied, op, or, on, ob, oq, ok, pr)) {
                        individualMoves[indexes[1]] = pr ^ bLCastleRook;
                        individualMoves[indexes[5]] = bLCastleBit;
                        individualMoves[14] = baseKey ^ Zobrist.pieceKeys[indexes[1]][0] ^ Zobrist.pieceKeys[indexes[1]][3] ^ Zobrist.pieceKeys[indexes[5]][2];
                        individualMoves[16] = bLCastleBit;
                        moves.add(individualMoves.clone());
                    }
                }
            }
        }
        if (moves.size() == 0) {
            individualMoves[14] = 0L;
            if (inCheck) {
                individualMoves[18] = 1L;
            } else {
                individualMoves[18] = 2L;
            }
            moves.add(individualMoves.clone());
        }
        return moves;
    }

    public static long moveGenerationCheck(int depth, int originalDepth, boolean player, Long[] board) {
        ArrayList<Long[]> possibleMoves = possibleMoves(player, board);
        long counter = 0;
        long temp;
        if (depth == 0) {
            counter += possibleMoves.size();
            return counter;
        }
        for (Long[] currentBoard : possibleMoves) {
            temp = moveGenerationCheck(depth - 1, originalDepth, !player, currentBoard);
            counter += temp;
            /*if (depth == originalDepth) {
                System.out.printf("%s%s: %d\n", moveToStringMove(bitsToMove(currentBoard[15])), moveToStringMove(bitsToMove(currentBoard[16])), temp);
                if (moveToStringMove(bitsToMove(currentBoard[15])).equals("a2") && moveToStringMove(bitsToMove(currentBoard[16])).equals("a4")) {
                    System.out.printf("%b %d %d %d %d %d %d %d %d %d %d %d %d %d %d\n", !player, currentBoard[0], currentBoard[1], currentBoard[2], currentBoard[3], currentBoard[4], currentBoard[5], currentBoard[6], currentBoard[7], currentBoard[8], currentBoard[9], currentBoard[10], currentBoard[11], currentBoard[12], currentBoard[13]);
                    //System.out.printf("%b %d %d %d %d %d %d %d %d %d %d %d %d %d %b %b\n", player, pp, pr, pn, pb, pq, pk, op, or, on, ob, oq, ok, doublePawnPush, shortMoved, longMoved);
                }
            }*/
        }
        return counter;
    }
}
