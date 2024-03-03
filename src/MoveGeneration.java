import java.util.ArrayList;

class MoveGeneration {
    static final long[] files = {72340172838076673L, 144680345676153346L, 289360691352306692L, 578721382704613384L, 1157442765409226768L, 2314885530818453536L, 4629771061636907072L, -9187201950435737472L};
    static final long[] ranks = {255L, 65280L, 16711680L, 4278190080L, 1095216660480L, 280375465082880L, 71776119061217280L, -72057594037927936L};
    static final long knightSpan = 44272527353856L, kingSpan = 61745389371392L;

    static final long wSFinal = 6917529027641081856L, wLFinal = 1008806316530991104L, wLMoveSquares = 864691128455135232L;
    static final long bSFinal = 96L, bLFinal = 14L, bLMoveSquares = 12;

    static long[] knightAttackTable = new long[64];
    static long[] kingAttackTable = new long[64];

    static void initAttack() {
        MagicBitboards.initMagics();
        for (int i = 0; i < 64; i++) {
            knightAttackTable[i] = initKnight(1L << i);
            kingAttackTable[i] = initKing(1L << i);
        }
    }

    static long initKnight(long movingPiece) {
        long attack;
        int move = BitMethods.getLS1B(movingPiece);
        if (move < 28) {
            attack = knightSpan >> 28 - move;
        } else if (move > 28) {
            attack = knightSpan << move - 28;
        } else {
            attack = knightSpan;
        }
        if (move % 8 < 2) {
            attack = attack ^ ((attack & files[7]) | (attack & files[6]));
        } else if (move % 8 > 5) {
            attack = attack ^ ((attack & files[0]) | (attack & files[1]));
        }
        return attack;
    }

    static long initKing(long movingPiece) {
        int move = BitMethods.getLS1B(movingPiece);
        long attack;
        if (move < 36) {
            attack = kingSpan >> 36 - move;
        } else if (move > 36) {
            attack = kingSpan << move - 36;
        } else {
            attack = kingSpan;
        }
        if ((movingPiece & files[0]) == movingPiece) {
            attack &= ~files[7];
        } else if ((movingPiece & files[7]) == movingPiece) {
            attack &= ~files[0];
        }
        return attack;
    }

    static long singlePawnAttacks(long movingPiece, boolean player) {
        long attack;
        if (player) {
            attack = movingPiece >> 9 | movingPiece >> 7;
        } else {
            attack = movingPiece << 9 | movingPiece << 7;
        }
        if ((movingPiece & files[0]) == movingPiece) { //removes bits that are shifted 2 rows forward since pawn at edge
            attack &= ~files[7];
        } else if ((movingPiece & files[7]) == movingPiece) {
            attack &= ~files[0];
        }
        return attack;
    }

    static long pawnAttackMask(long pawns, boolean player) {
        long attack = 0L;
        long left = pawns & files[0], right = pawns & files[7];
        pawns = pawns & ~left & ~right;

        if (player) {
            attack |= pawns >> 7 | pawns >> 9 | left >> 7 | right >> 9;
        } else {
            attack |= pawns << 7 | pawns << 9 | left << 9 | right << 7;
        }

        return attack;
    }

    static long enPassantSquares(long previousPawnPush) {
        long squares;
        if (previousPawnPush != 0) {
            squares = previousPawnPush >> 1 | previousPawnPush << 1;
            if ((previousPawnPush & files[0]) == previousPawnPush) { //removes bits that are shifted 2 rows forward since pawn at edge
                squares &= ~files[7];
            } else if ((previousPawnPush & files[7]) == previousPawnPush) {
                squares &= ~files[0];
            }
            return squares;
        }
        return 0;
    }

    static long singlePawnForwardMoves(long movingPiece, boolean player, long occupied) {
        long moves = 0L;
        long ahead;
        if (player) {
            ahead = movingPiece >> 8;
            if ((ahead & occupied) == 0) { //checks if pawn is blocked
                moves |= ahead;

                if ((movingPiece & ranks[6]) != 0) { //double jump
                    ahead >>= 8;
                    moves |= (ahead & ~occupied);
                }
            }
        } else {
            ahead = movingPiece << 8;
            if ((ahead & occupied) == 0) {
                moves |= ahead;

                if ((movingPiece & ranks[1]) != 0) {
                    ahead <<= 8;
                    moves |= (ahead & ~occupied);
                }
            }
        }
        return moves;
    }

    static long singleKnightAttacks(long movingPiece) {
        return knightAttackTable[BitMethods.getLS1B(movingPiece)];
    }

    static long singleKnightAttacks(int movingPiece) {
        return knightAttackTable[movingPiece];
    }

    static long singleRookAttacks(long movingPiece, long occupied) {
        return MagicBitboards.getRookAttacks(BitMethods.getLS1B(movingPiece), occupied);
    }

    static long singleRookAttacks(int movingPiece, long occupied) {
        return MagicBitboards.getRookAttacks(movingPiece, occupied);
    }

    static long singleBishopAttacks(long movingPiece, long occupied) {
        return MagicBitboards.getBishopAttacks(BitMethods.getLS1B(movingPiece), occupied);
    }

    static long singleBishopAttacks(int movingPiece, long occupied) {
        return MagicBitboards.getBishopAttacks(movingPiece, occupied);
    }

    static long singleQueenAttacks(long movingPiece, long occupied) {
        return MagicBitboards.getBishopAttacks(BitMethods.getLS1B(movingPiece), occupied) | MagicBitboards.getRookAttacks(BitMethods.getLS1B(movingPiece), occupied);
    }

    static long singleQueenAttacks(int movingPiece, long occupied) {
        return MagicBitboards.getBishopAttacks(movingPiece, occupied) | MagicBitboards.getRookAttacks(movingPiece, occupied);
    }

    static long singleKingAttacks(long movingPiece) {
        return kingAttackTable[BitMethods.getLS1B(movingPiece)];
    }

    static long singleKingAttacks(int movingPiece) {
        return kingAttackTable[movingPiece];
    }

    static ArrayList<long[]> getPinnedPieces(long friendlyKing, long occupied, long enemyRook, long enemyBishop, long enemyQueen) {
        ArrayList<long[]> pinnedPieces = new ArrayList<>();
        long kingStraight, kingDiagonal;
        long piecesOnAttackRow;
        long totalPieces = 0L;
        long attackSquares;
        long currentPiece;
        int currentPieceSquare;
        enemyRook |= enemyQueen; //queen has rook sliding moves, so it can count as rook
        enemyBishop |= enemyQueen; //same as rook

        kingStraight = singleRookAttacks(friendlyKing, enemyRook);
        kingDiagonal = singleBishopAttacks(friendlyKing, enemyBishop);

        if ((kingStraight & enemyRook) != 0) { //checks if any rook rays on king
            enemyRook &= kingStraight; //remove irrelevant pieces
            while (enemyRook != 0) {
                currentPieceSquare = BitMethods.getLS1B(enemyRook);
                currentPiece = 1L << currentPieceSquare;
                enemyRook &= ~currentPiece;

                attackSquares = (singleRookAttacks(currentPieceSquare, friendlyKing) & kingStraight);
                piecesOnAttackRow = attackSquares & occupied;

                if (BitMethods.oneBitCheck(piecesOnAttackRow)) {
                    totalPieces |= piecesOnAttackRow;
                    pinnedPieces.add(new long[]{piecesOnAttackRow, attackSquares | currentPiece});
                }
            }
        }

        if ((kingDiagonal & enemyBishop) != 0) {
            enemyBishop &= kingDiagonal;
            while (enemyBishop != 0) {
                currentPieceSquare = BitMethods.getLS1B(enemyBishop);
                currentPiece = 1L << currentPieceSquare;
                enemyBishop &= ~currentPiece;

                attackSquares = (singleBishopAttacks(currentPieceSquare, friendlyKing) & kingDiagonal);
                piecesOnAttackRow = attackSquares & occupied;

                if (BitMethods.oneBitCheck(piecesOnAttackRow)) {
                    totalPieces |= piecesOnAttackRow;
                    pinnedPieces.add(new long[]{piecesOnAttackRow, attackSquares | currentPiece});
                }
            }
        }
        pinnedPieces.add(new long[]{totalPieces});
        return pinnedPieces;
    }

    static boolean enPassantPinCheck(long friendlyKing, long friendlyPawn, long enemyPawn, long occupied, long enemyRook, long enemyBishop, long enemyQueen) {
        long kingLine; //king rays
        long importantStraight; //important rays
        long ray;
        long selectedPiece;
        int selectedPieceSquare;
        boolean foundPiece = false;
        enemyRook |= enemyQueen; //queen has rook sliding moves, so it can count as rook
        enemyBishop |= enemyQueen;

        int kingNum = BitMethods.getLS1B(friendlyKing);
        kingLine = singleRookAttacks(kingNum, enemyRook);
        if ((kingLine & enemyRook) != 0 && (kingLine & friendlyPawn) != 0 && (kingLine & enemyPawn) != 0) { //checks if all participating pieces are in line with king
            enemyRook &= kingLine;
            importantStraight = (singleRookAttacks(friendlyPawn, friendlyKing)|friendlyPawn) & kingLine;

            while (enemyRook != 0 && !foundPiece) {
                selectedPieceSquare = BitMethods.getLS1B(enemyRook);
                selectedPiece = 1L << selectedPieceSquare;
                enemyRook &= ~selectedPiece;
                ray = singleRookAttacks(selectedPieceSquare, friendlyKing)|selectedPiece;

                if ((ray & importantStraight) == importantStraight) { //rook pawn and king all in same row, and only the 2 pawns in the ray
                    foundPiece = true;
                    importantStraight&=~selectedPiece;
                }
            }

            occupied &= importantStraight;
            if (foundPiece && ((occupied & ~(friendlyPawn | enemyPawn)) == 0)) {
                return true;
            }
        }

        kingLine = singleBishopAttacks(kingNum, enemyBishop);
        if ((kingLine & enemyBishop) != 0 && (kingLine & enemyPawn) != 0) { //if bishop and enemy pawn on same line then cannot enpassant
            enemyBishop &= kingLine;
            importantStraight = (singleBishopAttacks(enemyPawn, friendlyKing)|enemyPawn) & kingLine;

            while (enemyBishop != 0 && !foundPiece) {
                selectedPieceSquare = BitMethods.getLS1B(enemyBishop);
                selectedPiece = 1L << selectedPieceSquare;
                enemyBishop &= ~selectedPiece;
                ray = singleBishopAttacks(selectedPieceSquare, friendlyKing)|selectedPiece;

                if ((ray & importantStraight) == importantStraight) { //rook pawn and king all in same row, and only the 2 pawns in the ray
                    foundPiece = true;
                    importantStraight&=~selectedPiece;
                }
            }

            occupied &= importantStraight;
            return foundPiece && ((occupied & ~enemyPawn) == 0);
        }

        return false;
    }

    static long[] checkSquares(long friendlyKing, long occupied, long enemyPawn, long enemyPawnMask, long moveType, long enemyRook, long enemyKnight, long enemyKnightMask, long enemyBishop, long enemyQueen, boolean player) {
        //long[1] = checking squares
        //long[2] = amount of checking pieces
        //long[3] = king illegal squares
        long kingSquares = 0L;
        long occupiedWithoutKing = occupied & ~friendlyKing;
        long squares = 0L;
        long selectedPiece;
        int selectedPieceSquare;
        int pieceCount = 0;

        if ((friendlyKing & enemyPawnMask) != 0) {
            while (enemyPawn != 0) {
                selectedPieceSquare = BitMethods.getLS1B(enemyPawn);
                selectedPiece = 1L << selectedPieceSquare;
                enemyPawn &= ~selectedPiece;
                if ((singlePawnAttacks(selectedPiece, !player) & friendlyKing) != 0) {
                    if ((moveType&1L) == 0) {
                        return new long[]{selectedPiece, 1, -1L}; //if pawn is checking king, there cannot be double check
                    } else {
                        squares |= selectedPiece;
                        pieceCount++;
                    }
                }
            }
        }

        if ((friendlyKing & enemyKnightMask) != 0) {
            boolean found = false;
            while (!found) { //can only have 1 knight checking king at any time
                selectedPieceSquare = BitMethods.getLS1B(enemyKnight);
                selectedPiece = 1L << selectedPieceSquare;
                enemyKnight &= ~selectedPiece;
                if ((singleKnightAttacks(selectedPieceSquare) & friendlyKing) != 0) {
                    squares |= selectedPiece;
                    pieceCount++;
                    found = true;
                }
            }
        }

        enemyRook |= enemyQueen;
        enemyBishop |= enemyQueen;
        long kingStraight = singleRookAttacks(friendlyKing, occupied);
        long kingDiagonal = singleBishopAttacks(friendlyKing, occupied);

        if ((kingStraight & enemyRook) != 0) { //checks if any rook rays on king
            while (enemyRook != 0) {

                selectedPieceSquare = BitMethods.getLS1B(enemyRook);
                selectedPiece = 1L << selectedPieceSquare;
                enemyRook &= ~selectedPiece;

                if ((kingStraight & selectedPiece) != 0) {
                    squares |= (singleRookAttacks(selectedPieceSquare, occupied) & kingStraight);
                    squares |= selectedPiece;
                    kingSquares |= (singleRookAttacks(selectedPieceSquare, occupiedWithoutKing) & kingStraight);
                    pieceCount++;
                }
            }
        }

        if ((kingDiagonal & enemyBishop) != 0) { //checks if any rook rays on king
            while (enemyBishop != 0) {

                selectedPieceSquare = BitMethods.getLS1B(enemyBishop);
                selectedPiece = 1L << selectedPieceSquare;
                enemyBishop &= ~selectedPiece;

                if ((kingDiagonal & selectedPiece) != 0) {
                    squares |= (singleBishopAttacks(selectedPieceSquare, occupied) & kingDiagonal);
                    squares |= selectedPiece;
                    kingSquares |= (singleBishopAttacks(selectedPieceSquare, occupiedWithoutKing) & kingDiagonal);
                    pieceCount++;
                }
            }
        }
        return new long[]{squares, pieceCount, ~kingSquares};
    }

    static int mateCheck(Board board) { //returns 0 if not mate, 1 if checkmate, 2 if stalemate
        ArrayList<long[]> pinnedPieces;
        long amountOfCheckPieces = 0;
        long checkSquares = -1L; //squares that pieces can go to
        long pinSquares = -1L;
        long kingSquares = -1L;
        long legalSquares;
        long pinnedPiecesBits;
        boolean doubleCheck = false;

        long selectedPiece;
        long move;
        long currentBit;
        int currentBitInt;
        int selectedPieceSquare;

        pinnedPieces = getPinnedPieces(board.fKing, board.occupied, board.eRook, board.eBishop, board.eQueen);
        pinnedPiecesBits = pinnedPieces.get(pinnedPieces.size() - 1)[0];
        if ((board.fKing & board.eAttackMask) != 0) { //if king is in check
            long[] temp = checkSquares(board.fKing, board.occupied, board.ePawn, board.ePawnAttackMask, board.moveType, board.eRook, board.eKnight, board.eKnightAttackMask, board.eBishop, board.eQueen, board.player);
            amountOfCheckPieces = temp[1];
            if (amountOfCheckPieces == 2) {
                doubleCheck = true;
            }
            checkSquares = temp[0];
            kingSquares = temp[2];
        }

        if (!doubleCheck) {

            //PAWN MOVES
            long selectedBitboard = board.fPawn;
            long enPassantSquares = enPassantSquares(board.previousPawnPush);
            boolean bitboardContainsPin = (selectedBitboard & pinnedPiecesBits) != 0;

            while (selectedBitboard != 0) {
                selectedPieceSquare = BitMethods.getLS1B(selectedBitboard);
                selectedPiece = 1L << selectedPieceSquare;
                selectedBitboard &= ~(selectedPiece); //checks if next move leads to promotion

                if (bitboardContainsPin) {
                    if ((selectedPiece & pinnedPiecesBits) != 0) { //if current piece is pinned
                        for (long[] arr : pinnedPieces) {
                            if (arr[0] == selectedPiece) {
                                pinSquares = arr[1];
                                break;
                            }
                        }
                    } else {
                        pinSquares = -1L;
                    }
                }
                legalSquares = checkSquares & pinSquares;

                move = singlePawnAttacks(selectedPiece, board.player) & board.eOccupied & legalSquares;
                if (move != 0) {
                    return 0;
                }


                if ((selectedPiece & enPassantSquares) != 0) { //checks if piece can enPassant
                    long previousPush = board.previousPawnPush;
                    int endSquare;
                    if (board.player) {
                        endSquare = BitMethods.getLS1B(previousPush >> 8);
                    } else {
                        endSquare = BitMethods.getLS1B(previousPush << 8);
                    }
                    if (((1L<<endSquare | previousPush) & legalSquares) != 0) { //checks if it blocks check if in check
                        if (!enPassantPinCheck(board.fKing, selectedPiece, previousPush, board.occupied, board.eRook, board.eBishop, board.eQueen)) {
                            return 0;
                        }
                    }
                }

                move = singlePawnForwardMoves(selectedPiece, board.player, board.occupied) & legalSquares;
                if (move != 0) {
                    return 0;
                }
            }

            //ROOK MOVES
            selectedBitboard = board.fRook;
            bitboardContainsPin = (selectedBitboard & pinnedPiecesBits) != 0;
            pinSquares = -1L;

            while (selectedBitboard != 0) {
                selectedPieceSquare = BitMethods.getLS1B(selectedBitboard);
                selectedPiece = 1L << selectedPieceSquare;
                selectedBitboard &= ~selectedPiece;

                if (bitboardContainsPin) {
                    if ((selectedPiece & pinnedPiecesBits) != 0) { //if current piece is pinned
                        for (long[] arr : pinnedPieces) {
                            if (arr[0] == selectedPiece) {
                                pinSquares = arr[1];
                                break;
                            }
                        }
                    } else {
                        pinSquares = -1L;
                    }
                }
                legalSquares = checkSquares & pinSquares;

                move = singleRookAttacks(selectedPieceSquare, board.occupied) & legalSquares;
                if (move != 0) {
                    return 0;
                }
            }

            //KNIGHT MOVES
            boolean pin;
            selectedBitboard = board.fKnight;
            bitboardContainsPin = (selectedBitboard & pinnedPiecesBits) != 0;
            pinSquares = -1L;

            while (selectedBitboard != 0) {
                pin = false;
                selectedPieceSquare = BitMethods.getLS1B(selectedBitboard);
                selectedPiece = 1L << selectedPieceSquare;
                selectedBitboard &= ~selectedPiece;

                if (bitboardContainsPin) {
                    if ((selectedPiece & pinnedPiecesBits) != 0) { //if current piece is pinned
                        pin = true;
                    }
                }
                if (!pin) { //if knight is pinned it cannot move
                    legalSquares = checkSquares & pinSquares;

                    move = singleKnightAttacks(selectedPieceSquare) & legalSquares;
                    if (move != 0) {
                        return 0;
                    }
                }
            }

            //BISHOP MOVES
            selectedBitboard = board.fBishop;
            bitboardContainsPin = (selectedBitboard & pinnedPiecesBits) != 0;
            //pinSquares = -1L; pinsquares already set to -1L earlier

            while (selectedBitboard != 0) {
                selectedPieceSquare = BitMethods.getLS1B(selectedBitboard);
                selectedPiece = 1L << selectedPieceSquare;
                selectedBitboard &= ~selectedPiece;

                if (bitboardContainsPin) {
                    if ((selectedPiece & pinnedPiecesBits) != 0) { //if current piece is pinned
                        for (long[] arr : pinnedPieces) {
                            if (arr[0] == selectedPiece) {
                                pinSquares = arr[1];
                                break;
                            }
                        }
                    } else {
                        pinSquares = -1L;
                    }
                }
                legalSquares = checkSquares & pinSquares;

                move = singleBishopAttacks(selectedPieceSquare, board.occupied) & legalSquares;
                if (move != 0) {
                    return 0;
                }
            }

            //QUEEN MOVES
            selectedBitboard = board.fQueen;
            bitboardContainsPin = (selectedBitboard & pinnedPiecesBits) != 0;
            pinSquares = -1L;

            while (selectedBitboard != 0) {
                selectedPieceSquare = BitMethods.getLS1B(selectedBitboard);
                selectedPiece = 1L << selectedPieceSquare;
                selectedBitboard &= ~selectedPiece;

                if (bitboardContainsPin) {
                    if ((selectedPiece & pinnedPiecesBits) != 0) { //if current piece is pinned
                        for (long[] arr : pinnedPieces) {
                            if (arr[0] == selectedPiece) {
                                pinSquares = arr[1];
                                break;
                            }
                        }
                    } else {
                        pinSquares = -1L;
                    }
                }
                legalSquares = checkSquares & pinSquares;

                move = singleQueenAttacks(selectedPieceSquare, board.occupied) & legalSquares;
                if (move != 0) {
                    return 0;
                }
            }
        }

        selectedPiece = board.fKing; //can only have 1 king
        selectedPieceSquare = BitMethods.getLS1B(selectedPiece);
        move = singleKingAttacks(selectedPieceSquare) & ~board.eAttackMask & ~board.fOccupied & kingSquares;
        if (move != 0) {
            return 0;
        } else if (amountOfCheckPieces > 0) {
            return 1;
        } else {
            return 2;
        }
    }

    static MoveList getMoves(Board board) {
        MoveList moveList = new MoveList();
        ArrayList<long[]> pinnedPieces;
        long amountOfCheckPieces = 0;
        long checkSquares = -1L; //squares that pieces can go to
        long pinSquares = -1L;
        long kingSquares = -1L;
        long legalSquares;
        long selectedPiece;
        int selectedPieceSquare;
        long move;
        int currentBitInt;
        long currentBit;

        //variables below are for MoveList purposes;
        int endSquare;
        int doublePush = 0;

        boolean doubleCheck = false;
        long inverseFOccupied = ~board.fOccupied;
        long pinnedPiecesBits;

        pinnedPieces = getPinnedPieces(board.fKing, board.occupied, board.eRook, board.eBishop, board.eQueen);
        pinnedPiecesBits = pinnedPieces.get(pinnedPieces.size() - 1)[0];
        if ((board.fKing & board.eAttackMask) != 0) { //if king is in check
            long[] temp = checkSquares(board.fKing, board.occupied, board.ePawn, board.ePawnAttackMask, board.moveType, board.eRook, board.eKnight, board.eKnightAttackMask, board.eBishop, board.eQueen, board.player);
            amountOfCheckPieces = temp[1];
            if (amountOfCheckPieces == 2) {
                doubleCheck = true;
            }
            checkSquares = temp[0];
            kingSquares = temp[2];
        }

        if (!doubleCheck) {

            //PAWN MOVES
            long selectedBitboard = board.fPawn;
            long enPassantSquares = enPassantSquares(board.previousPawnPush);
            boolean promotion;
            boolean bitboardContainsPin = (selectedBitboard & pinnedPiecesBits) != 0;

            while (selectedBitboard != 0) {
                selectedPieceSquare = BitMethods.getLS1B(selectedBitboard);
                selectedPiece = 1L << selectedPieceSquare;
                selectedBitboard &= ~(selectedPiece);
                promotion = (board.player & (selectedPiece & ranks[1]) != 0) || (!board.player & (selectedPiece & ranks[6]) != 0); //checks if next move leads to promotion

                if (bitboardContainsPin) {
                    if ((selectedPiece & pinnedPiecesBits) != 0) { //if current piece is pinned
                        for (long[] arr : pinnedPieces) {
                            if (arr[0] == selectedPiece) {
                                pinSquares = arr[1];
                                break;
                            }
                        }
                    } else {
                        pinSquares = -1L;
                    }
                }
                legalSquares = checkSquares & pinSquares;

                move = singlePawnAttacks(selectedPiece, board.player) & board.eOccupied & legalSquares;
                while (move != 0) { //loop through attack bits
                    currentBitInt = BitMethods.getLS1B(move); //end square
                    move &= ~(1L << currentBitInt);

                    if (promotion) {
                        moveList.addPromotionMoves(selectedPieceSquare, currentBitInt, 1);
                    } else {
                        moveList.addMove(selectedPieceSquare, currentBitInt, 0, 1, 0, 0, 0);
                    }
                }


                if ((selectedPiece & enPassantSquares) != 0) { //checks if piece can enPassant
                    long previousPush = board.previousPawnPush;
                    if (board.player) {
                        endSquare = BitMethods.getLS1B(previousPush >> 8);
                    } else {
                        endSquare = BitMethods.getLS1B(previousPush << 8);
                    }
                    if (((1L<<endSquare | previousPush) & legalSquares) != 0) { //checks if it blocks check if in check
                        if (!enPassantPinCheck(board.fKing, selectedPiece, previousPush, board.occupied, board.eRook, board.eBishop, board.eQueen)) {
                            moveList.addMove(selectedPieceSquare, endSquare, 0, 0, 0, 1, 0);
                        }
                    }
                }

                move = singlePawnForwardMoves(selectedPiece, board.player, board.occupied) & legalSquares;
                while (move != 0) {
                    currentBitInt = BitMethods.getLS1B(move);
                    currentBit = 1L << currentBitInt;
                    move &= ~(currentBit);

                    if (promotion) {
                        moveList.addPromotionMoves(selectedPieceSquare, currentBitInt, 0);
                    } else {
                        if (doublePush == 1) { //if the double push already found, only single push left
                            doublePush = 0;
                        } else {
                            if ((board.player && selectedPiece >> 16 == currentBit) || (!board.player && selectedPiece << 16 == currentBit)) {
                                doublePush = 1;
                            }
                        }
                        moveList.addMove(selectedPieceSquare, currentBitInt, 0, 0, doublePush, 0, 0);
                    }
                }
            }

            //ROOK MOVES
            selectedBitboard = board.fRook;
            bitboardContainsPin = (selectedBitboard & pinnedPiecesBits) != 0;
            pinSquares = -1L;

            while (selectedBitboard != 0) {
                selectedPieceSquare = BitMethods.getLS1B(selectedBitboard);
                selectedPiece = 1L << selectedPieceSquare;
                selectedBitboard &= ~selectedPiece;

                if (bitboardContainsPin) {
                    if ((selectedPiece & pinnedPiecesBits) != 0) { //if current piece is pinned
                        for (long[] arr : pinnedPieces) {
                            if (arr[0] == selectedPiece) {
                                pinSquares = arr[1];
                                break;
                            }
                        }
                    } else {
                        pinSquares = -1L;
                    }
                }
                legalSquares = checkSquares & pinSquares;

                move = singleRookAttacks(selectedPieceSquare, board.occupied) & legalSquares & inverseFOccupied;
                while (move != 0) {
                    currentBitInt = BitMethods.getLS1B(move);
                    currentBit = 1L << currentBitInt;
                    move &= ~currentBit;

                    if ((currentBit & board.eOccupied) != 0) {
                        moveList.addMove(selectedPieceSquare, currentBitInt, 1, 1, 0, 0, 0);
                    } else {
                        moveList.addMove(selectedPieceSquare, currentBitInt, 1, 0, 0, 0, 0);
                    }
                }
            }

            //KNIGHT MOVES
            boolean pin;
            selectedBitboard = board.fKnight;
            bitboardContainsPin = (selectedBitboard & pinnedPiecesBits) != 0;
            pinSquares = -1L;

            while (selectedBitboard != 0) {
                pin = false;
                selectedPieceSquare = BitMethods.getLS1B(selectedBitboard);
                selectedPiece = 1L << selectedPieceSquare;
                selectedBitboard &= ~selectedPiece;

                if (bitboardContainsPin) {
                    if ((selectedPiece & pinnedPiecesBits) != 0) { //if current piece is pinned
                        pin = true;
                    }
                }
                if (!pin) { //if knight is pinned it cannot move
                    legalSquares = checkSquares & pinSquares;

                    move = singleKnightAttacks(selectedPieceSquare) & legalSquares & inverseFOccupied;
                    while (move != 0) {
                        currentBitInt = BitMethods.getLS1B(move);
                        currentBit = 1L << currentBitInt;
                        move &= ~currentBit;

                        if ((currentBit & board.eOccupied) != 0) {
                            moveList.addMove(selectedPieceSquare, currentBitInt, 2, 1, 0, 0, 0);
                        } else {
                            moveList.addMove(selectedPieceSquare, currentBitInt, 2, 0, 0, 0, 0);
                        }
                    }
                }
            }

            //BISHOP MOVES
            selectedBitboard = board.fBishop;
            bitboardContainsPin = (selectedBitboard & pinnedPiecesBits) != 0;
            //pinSquares = -1L; pinsquares already set to -1L earlier

            while (selectedBitboard != 0) {
                selectedPieceSquare = BitMethods.getLS1B(selectedBitboard);
                selectedPiece = 1L << selectedPieceSquare;
                selectedBitboard &= ~selectedPiece;

                if (bitboardContainsPin) {
                    if ((selectedPiece & pinnedPiecesBits) != 0) { //if current piece is pinned
                        for (long[] arr : pinnedPieces) {
                            if (arr[0] == selectedPiece) {
                                pinSquares = arr[1];
                                break;
                            }
                        }
                    } else {
                        pinSquares = -1L;
                    }
                }
                legalSquares = checkSquares & pinSquares;

                move = singleBishopAttacks(selectedPieceSquare, board.occupied) & legalSquares & inverseFOccupied;
                while (move != 0) {
                    currentBitInt = BitMethods.getLS1B(move);
                    currentBit = 1L << currentBitInt;
                    move &= ~currentBit;

                    if ((currentBit & board.eOccupied) != 0) {
                        moveList.addMove(selectedPieceSquare, currentBitInt, 3, 1, 0, 0, 0);
                    } else {
                        moveList.addMove(selectedPieceSquare, currentBitInt, 3, 0, 0, 0, 0);
                    }
                }
            }

            //QUEEN MOVES
            selectedBitboard = board.fQueen;
            bitboardContainsPin = (selectedBitboard & pinnedPiecesBits) != 0;
            pinSquares = -1L;

            while (selectedBitboard != 0) {
                selectedPieceSquare = BitMethods.getLS1B(selectedBitboard);
                selectedPiece = 1L << selectedPieceSquare;
                selectedBitboard &= ~selectedPiece;

                if (bitboardContainsPin) {
                    if ((selectedPiece & pinnedPiecesBits) != 0) { //if current piece is pinned
                        for (long[] arr : pinnedPieces) {
                            if (arr[0] == selectedPiece) {
                                pinSquares = arr[1];
                                break;
                            }
                        }
                    } else {
                        pinSquares = -1L;
                    }
                }
                legalSquares = checkSquares & pinSquares;

                move = singleQueenAttacks(selectedPieceSquare, board.occupied) & legalSquares & inverseFOccupied;
                while (move != 0) {
                    currentBitInt = BitMethods.getLS1B(move);
                    currentBit = 1L << currentBitInt;
                    move &= ~currentBit;

                    if ((currentBit & board.eOccupied) != 0) {
                        moveList.addMove(selectedPieceSquare, currentBitInt, 4, 1, 0, 0, 0);
                    } else {
                        moveList.addMove(selectedPieceSquare, currentBitInt, 4, 0, 0, 0, 0);
                    }
                }
            }
        }

        //KING MOVES
        selectedPiece = board.fKing; //can only have 1 king
        selectedPieceSquare = BitMethods.getLS1B(selectedPiece);
        move = singleKingAttacks(selectedPieceSquare) & ~board.eAttackMask & inverseFOccupied & kingSquares; //king cannot be in enemy attack or take own piece and must be in king squares
        //System.out.println(eA);
        while (move != 0) {
            currentBitInt = BitMethods.getLS1B(move);
            currentBit = 1L << currentBitInt;
            move &= ~currentBit;

            if ((currentBit & board.eOccupied) != 0) {
                moveList.addMove(selectedPieceSquare, currentBitInt, 5, 1, 0, 0, 0);
            } else {
                moveList.addMove(selectedPieceSquare, currentBitInt, 5, 0, 0, 0, 0);
            }
        }

        if (amountOfCheckPieces == 0) {
            if (board.player) { //first checks if rooks or kings moved, then if the square is occupied or under attack
                if ((board.castleRights & 8L) == 8L && (wSFinal & (board.occupied | board.eAttackMask)) == 0) { //wShort castle
                    moveList.addMove(selectedPieceSquare, 62, 5, 0, 0, 0, 1);
                }
                if ((board.castleRights & 4L) == 4L && (wLFinal & board.occupied) == 0 && (wLMoveSquares & board.eAttackMask) == 0) { //wLong castle
                    moveList.addMove(selectedPieceSquare, 58, 5, 0, 0, 0, 1);
                }
            } else {
                if ((board.castleRights & 2L) == 2L && (bSFinal & (board.occupied | board.eAttackMask)) == 0) { //bShort castle
                    moveList.addMove(selectedPieceSquare, 6, 5, 0, 0, 0, 1);
                }
                if ((board.castleRights & 1L) == 1L && (bLFinal & board.occupied) == 0 && (bLMoveSquares & board.eAttackMask) == 0) { //bLong castle
                    moveList.addMove(selectedPieceSquare, 2, 5, 0, 0, 0, 1);
                }
            }
        }

        return moveList;
    }
}