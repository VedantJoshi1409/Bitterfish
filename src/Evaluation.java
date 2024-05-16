public class Evaluation {
    static final int PawnValue = 100;
    static final int RookValue = 500;
    static final int KnightValue = 310;
    static final int BishopValue = 320;
    static final int QueenValue = 900;

    static final int RookMobilityValue = 4; //tested values
    static final int KnightMobilityValue = 2;
    static final int BishopMobilityValue = 6;
    static final int QueenMobilityValue = 3;
    static final int MGKingMobilityValue = -4;

    static final int[] kingAttackerScores = {0, 15, 20, 40, 50, 60, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79};
    static final int[] kingPawnShield = {-15, 0, 10, 40, 60, 70, 80, 90};
    static final int[] passedPawnBonus = {0, 90, 60, 40, 25, 15, 15};

    //PeSTO piece square tables
    static int[][] mgPawnTable = {{ //0 is for white, 1 is for black
            0, 0, 0, 0, 0, 0, 0, 0,
            98, 134, 61, 95, 68, 126, 34, -11,
            -6, 7, 26, 31, 65, 56, 25, -20,
            -14, 13, 6, 21, 23, 12, 17, -23,
            -27, -2, -5, 12, 17, 6, 10, -25,
            -26, -4, -4, -10, 3, 3, 33, -12,
            -35, -1, -20, -23, -15, 24, 38, -22,
            0, 0, 0, 0, 0, 0, 0, 0,
    }, {0, 0, 0, 0, 0, 0, 0, 0,
            -35, -1, -20, -23, -15, 24, 38, -22,
            -26, -4, -4, -10, 3, 3, 33, -12,
            -27, -2, -5, 12, 17, 6, 10, -25,
            -14, 13, 6, 21, 23, 12, 17, -23,
            -6, 7, 26, 31, 65, 56, 25, -20,
            98, 134, 61, 95, 68, 126, 34, -11,
            0, 0, 0, 0, 0, 0, 0, 0,
    }};

    static int[][] egPawnTable = {{
            0, 0, 0, 0, 0, 0, 0, 0,
            178, 173, 158, 134, 147, 132, 165, 187,
            94, 100, 85, 67, 56, 53, 82, 84,
            32, 24, 13, 5, -2, 4, 17, 17,
            13, 9, -3, -7, -7, -8, 3, -1,
            4, 7, -6, 1, 0, -5, -1, -8,
            13, 8, 8, 10, 13, 0, 2, -7,
            0, 0, 0, 0, 0, 0, 0, 0,
    }, {0, 0, 0, 0, 0, 0, 0, 0,
            13, 8, 8, 10, 13, 0, 2, -7,
            4, 7, -6, 1, 0, -5, -1, -8,
            13, 9, -3, -7, -7, -8, 3, -1,
            32, 24, 13, 5, -2, 4, 17, 17,
            94, 100, 85, 67, 56, 53, 82, 84,
            178, 173, 158, 134, 147, 132, 165, 187,
            0, 0, 0, 0, 0, 0, 0, 0,
    }};

    static int[][] mgRookTable = {{
            32, 42, 32, 51, 63, 9, 31, 43,
            27, 32, 58, 62, 80, 67, 26, 44,
            -5, 19, 26, 36, 17, 45, 61, 16,
            -24, -11, 7, 26, 24, 35, -8, -20,
            -36, -26, -12, -1, 9, -7, 6, -23,
            -45, -25, -16, -17, 3, 0, -5, -33,
            -44, -16, -20, -9, -1, 11, -6, -71,
            -19, -13, 1, 17, 16, 7, -37, -26,
    }, {-19, -13, 1, 17, 16, 7, -37, -26,
            -44, -16, -20, -9, -1, 11, -6, -71,
            -45, -25, -16, -17, 3, 0, -5, -33,
            -36, -26, -12, -1, 9, -7, 6, -23,
            -24, -11, 7, 26, 24, 35, -8, -20,
            -5, 19, 26, 36, 17, 45, 61, 16,
            27, 32, 58, 62, 80, 67, 26, 44,
            32, 42, 32, 51, 63, 9, 31, 43,}};

    static int[][] egRookTable = {{
            13, 10, 18, 15, 12, 12, 8, 5,
            11, 13, 13, 11, -3, 3, 8, 3,
            7, 7, 7, 5, 4, -3, -5, -3,
            4, 3, 13, 1, 2, 1, -1, 2,
            3, 5, 8, 4, -5, -6, -8, -11,
            -4, 0, -5, -1, -7, -12, -8, -16,
            -6, -6, 0, 2, -9, -9, -11, -3,
            -9, 2, 3, -1, -5, -13, 4, -20,
    }, {-9, 2, 3, -1, -5, -13, 4, -20,
            -6, -6, 0, 2, -9, -9, -11, -3,
            -4, 0, -5, -1, -7, -12, -8, -16,
            3, 5, 8, 4, -5, -6, -8, -11,
            4, 3, 13, 1, 2, 1, -1, 2,
            7, 7, 7, 5, 4, -3, -5, -3,
            11, 13, 13, 11, -3, 3, 8, 3,
            13, 10, 18, 15, 12, 12, 8, 5,}};

    static int[][] mgKnightTable = {{
            -167, -89, -34, -49, 61, -97, -15, -107,
            -73, -41, 72, 36, 23, 62, 7, -17,
            -47, 60, 37, 65, 84, 129, 73, 44,
            -9, 17, 19, 53, 37, 69, 18, 22,
            -13, 4, 16, 13, 28, 19, 21, -8,
            -23, -9, 12, 10, 19, 17, 25, -16,
            -29, -53, -12, -3, -1, 18, -14, -19,
            -105, -21, -58, -33, -17, -28, -19, -23
    }, {-105, -21, -58, -33, -17, -28, -19, -23,
            -29, -53, -12, -3, -1, 18, -14, -19,
            -23, -9, 12, 10, 19, 17, 25, -16,
            -13, 4, 16, 13, 28, 19, 21, -8,
            -9, 17, 19, 53, 37, 69, 18, 22,
            -47, 60, 37, 65, 84, 129, 73, 44,
            -73, -41, 72, 36, 23, 62, 7, -17,
            -167, -89, -34, -49, 61, -97, -15, -107,}};

    static int[][] egKnightTable = {{
            -58, -38, -13, -28, -31, -27, -63, -99,
            -25, -8, -25, -2, -9, -25, -24, -52,
            -24, -20, 10, 9, -1, -9, -19, -41,
            -17, 3, 22, 22, 22, 11, 8, -18,
            -18, -6, 16, 25, 16, 17, 4, -18,
            -23, -3, -1, 15, 10, -3, -20, -22,
            -42, -20, -10, -5, -2, -20, -23, -44,
            -29, -51, -23, -15, -22, -18, -50, -64,
    }, {-29, -51, -23, -15, -22, -18, -50, -64,
            -42, -20, -10, -5, -2, -20, -23, -44,
            -23, -3, -1, 15, 10, -3, -20, -22,
            -18, -6, 16, 25, 16, 17, 4, -18,
            -17, 3, 22, 22, 22, 11, 8, -18,
            -24, -20, 10, 9, -1, -9, -19, -41,
            -25, -8, -25, -2, -9, -25, -24, -52,
            -58, -38, -13, -28, -31, -27, -63, -99,}};

    static int[][] mgBishopTable = {{
            -29, 4, -82, -37, -25, -42, 7, -8,
            -26, 16, -18, -13, 30, 59, 18, -47,
            -16, 37, 43, 40, 35, 50, 37, -2,
            -4, 5, 19, 50, 37, 37, 7, -2,
            -6, 13, 13, 26, 34, 12, 10, 4,
            0, 15, 15, 15, 14, 27, 18, 10,
            4, 15, 16, 0, 7, 21, 33, 1,
            -33, -3, -14, -21, -13, -12, -39, -21,
    }, {-33, -3, -14, -21, -13, -12, -39, -21,
            4, 15, 16, 0, 7, 21, 33, 1,
            0, 15, 15, 15, 14, 27, 18, 10,
            -6, 13, 13, 26, 34, 12, 10, 4,
            -4, 5, 19, 50, 37, 37, 7, -2,
            -16, 37, 43, 40, 35, 50, 37, -2,
            -26, 16, -18, -13, 30, 59, 18, -47,
            -29, 4, -82, -37, -25, -42, 7, -8,}};

    static int[][] egBishopTable = {{
            -14, -21, -11, -8, -7, -9, -17, -24,
            -8, -4, 7, -12, -3, -13, -4, -14,
            2, -8, 0, -1, -2, 6, 0, 4,
            -3, 9, 12, 9, 14, 10, 3, 2,
            -6, 3, 13, 19, 7, 10, -3, -9,
            -12, -3, 8, 10, 13, 3, -7, -15,
            -14, -18, -7, -1, 4, -9, -15, -27,
            -23, -9, -23, -5, -9, -16, -5, -17,
    }, {-23, -9, -23, -5, -9, -16, -5, -17,
            -14, -18, -7, -1, 4, -9, -15, -27,
            -12, -3, 8, 10, 13, 3, -7, -15,
            -6, 3, 13, 19, 7, 10, -3, -9,
            -3, 9, 12, 9, 14, 10, 3, 2,
            2, -8, 0, -1, -2, 6, 0, 4,
            -8, -4, 7, -12, -3, -13, -4, -14,
            -14, -21, -11, -8, -7, -9, -17, -24,}};

    static int[][] mgQueenTable = {{
            -28, 0, 29, 12, 59, 44, 43, 45,
            -24, -39, -5, 1, -16, 57, 28, 54,
            -13, -17, 7, 8, 29, 56, 47, 57,
            -27, -27, -16, -16, -1, 17, -2, 1,
            -9, -26, -9, -10, -2, -4, 3, -3,
            -14, 2, -11, -2, -5, 2, 14, 5,
            -35, -8, 11, 2, 8, 15, -3, 1,
            -1, -18, -9, 10, -15, -25, -31, -50,
    }, {-1, -18, -9, 10, -15, -25, -31, -50,
            -35, -8, 11, 2, 8, 15, -3, 1,
            -14, 2, -11, -2, -5, 2, 14, 5,
            -9, -26, -9, -10, -2, -4, 3, -3,
            -27, -27, -16, -16, -1, 17, -2, 1,
            -13, -17, 7, 8, 29, 56, 47, 57,
            -24, -39, -5, 1, -16, 57, 28, 54,
            -28, 0, 29, 12, 59, 44, 43, 45,}};

    static int[][] egQueenTable = {{
            -9, 22, 22, 27, 27, 19, 10, 20,
            -17, 20, 32, 41, 58, 25, 30, 0,
            -20, 6, 9, 49, 47, 35, 19, 9,
            3, 22, 24, 45, 57, 40, 57, 36,
            -18, 28, 19, 47, 31, 34, 39, 23,
            -16, -27, 15, 6, 9, 17, 10, 5,
            -22, -23, -30, -16, -16, -23, -36, -32,
            -33, -28, -22, -43, -5, -32, -20, -41,
    }, {-33, -28, -22, -43, -5, -32, -20, -41,
            -22, -23, -30, -16, -16, -23, -36, -32,
            -16, -27, 15, 6, 9, 17, 10, 5,
            -18, 28, 19, 47, 31, 34, 39, 23,
            3, 22, 24, 45, 57, 40, 57, 36,
            -20, 6, 9, 49, 47, 35, 19, 9,
            -17, 20, 32, 41, 58, 25, 30, 0,
            -9, 22, 22, 27, 27, 19, 10, 20,}};

    static int[][] mgKingTable = {{
            -65, 23, 16, -15, -56, -34, 2, 13,
            29, -1, -20, -7, -8, -4, -38, -29,
            -9, 24, 2, -16, -20, 6, 22, -22,
            -17, -20, -12, -27, -30, -25, -14, -36,
            -49, -1, -27, -39, -46, -44, -33, -51,
            -14, -14, -22, -46, -44, -30, -15, -27,
            1, 7, -8, -64, -43, -16, 9, 8,
            -15, 36, 12, -54, 8, -28, 24, 14,
    }, {-15, 36, 12, -54, 8, -28, 24, 14,
            1, 7, -8, -64, -43, -16, 9, 8,
            -14, -14, -22, -46, -44, -30, -15, -27,
            -49, -1, -27, -39, -46, -44, -33, -51,
            -17, -20, -12, -27, -30, -25, -14, -36,
            -9, 24, 2, -16, -20, 6, 22, -22,
            29, -1, -20, -7, -8, -4, -38, -29,
            -65, 23, 16, -15, -56, -34, 2, 13,}};

    static int[][] egKingTable = {{
            -74, -35, -18, -18, -11, 15, 4, -17,
            -12, 17, 14, 17, 17, 38, 23, 11,
            10, 17, 23, 15, 20, 45, 44, 13,
            -8, 22, 24, 27, 26, 33, 26, 3,
            -18, -4, 21, 24, 27, 23, 9, -11,
            -19, -3, 11, 21, 23, 16, 7, -9,
            -27, -11, 4, 13, 14, 4, -5, -17,
            -53, -34, -21, -11, -28, -14, -24, -43
    }, {-53, -34, -21, -11, -28, -14, -24, -43,
            -27, -11, 4, 13, 14, 4, -5, -17,
            -19, -3, 11, 21, 23, 16, 7, -9,
            -18, -4, 21, 24, 27, 23, 9, -11,
            -8, 22, 24, 27, 26, 33, 26, 3,
            10, 17, 23, 15, 20, 45, 44, 13,
            -12, 17, 14, 17, 17, 38, 23, 11,
            -74, -35, -18, -18, -11, 15, 4, -17,}};

    public static double evaluation(Board board) {
        int materialDiff;
        double positionalScore = 0;
        double mobilityScore = 0;
        double defenseScore = 0;
        double pawnBonuses = 0;
        double endGameKingCorner;

        long currentBitBoard;
        long currentAttack;
        long selectedPieceBit;
        int selectedPiece;

        int fPCount = 0;
        int fNCount = BitMethods.countBits(board.fKnight);
        int fRCount = BitMethods.countBits(board.fRook);
        int fBCount = BitMethods.countBits(board.fBishop);
        int fQCount = BitMethods.countBits(board.fQueen);
        int ePCount = 0;
        int eNCount = BitMethods.countBits(board.eKnight);
        int eRCount = BitMethods.countBits(board.eRook);
        int eBCount = BitMethods.countBits(board.eBishop);
        int eQCount = BitMethods.countBits(board.eQueen);

        int fKSquare = BitMethods.getLS1B(board.fKing);
        int eKSquare = BitMethods.getLS1B(board.eKing);

        long fKingBubble = MoveGeneration.singleKingAttacks(fKSquare);
        long eKingBubble = MoveGeneration.singleKingAttacks(eKSquare);
        int fAttackersOnKing = 0;
        int eAttackersOnKing = 0;

        int fIndex = 1, eIndex = 0;
        if (board.player) {
            fIndex = 0;
            eIndex = 1;
        }

        double endGameFactor = 24 - (fQCount + eQCount) * 4 - (fRCount + eRCount) * 2 - (fNCount + eNCount + fBCount + eBCount); //at the start of the game this will equal to 0
        endGameFactor = (endGameFactor * 256 + 12) / 24; //y = (x*256+12)/24
        //starts at 0.5 and ends at 256.5
        //Tapered eval from chess programming website

        pawnBonuses-=doubledPawnPenalty(board.fPawn, board.ePawn);
        currentBitBoard = board.fPawn;
        while (currentBitBoard != 0) {
            selectedPiece = BitMethods.getLS1B(currentBitBoard);
            currentBitBoard &= ~(1L << selectedPiece);
            pawnBonuses += passedPawnBonus(selectedPiece, board.ePawn, board.player);

            fPCount++;
            positionalScore += (mgPawnTable[fIndex][selectedPiece] * (256 - endGameFactor) + egPawnTable[fIndex][selectedPiece] * endGameFactor) / 256;

            if ((MoveGeneration.singlePawnAttacks(selectedPiece, board.player) & eKingBubble) != 0) {
                fAttackersOnKing++;
            }
        }

        currentBitBoard = board.ePawn;
        while (currentBitBoard != 0) {
            selectedPiece = BitMethods.getLS1B(currentBitBoard);
            currentBitBoard &= ~(1L << selectedPiece);
            pawnBonuses -= passedPawnBonus(selectedPiece, board.fPawn, !board.player);

            ePCount++;
            positionalScore -= (mgPawnTable[eIndex][selectedPiece] * (256 - endGameFactor) + egPawnTable[eIndex][selectedPiece] * endGameFactor) / 256;
            if ((MoveGeneration.singlePawnAttacks(selectedPiece, !board.player) & fKingBubble) != 0) {
                eAttackersOnKing++;
            }
        }

        long inverseFOccupied = ~board.fOccupied;
        long inverseEOccupied = ~board.eOccupied;

        currentBitBoard = board.fRook;
        while (currentBitBoard != 0) {
            selectedPiece = BitMethods.getLS1B(currentBitBoard);
            currentBitBoard &= ~(1L << selectedPiece);

            currentAttack = MoveGeneration.singleRookAttacks(selectedPiece, board.occupied);
            if ((currentAttack & eKingBubble) != 0) {
                fAttackersOnKing++;
            }
            currentAttack &= inverseFOccupied;

            positionalScore += (mgRookTable[fIndex][selectedPiece] * (256 - endGameFactor) + egRookTable[fIndex][selectedPiece] * endGameFactor) / 256;
            mobilityScore += BitMethods.countBits(currentAttack) * RookMobilityValue;
        }

        currentBitBoard = board.eRook;
        while (currentBitBoard != 0) {
            selectedPiece = BitMethods.getLS1B(currentBitBoard);
            currentBitBoard &= ~(1L << selectedPiece);

            currentAttack = MoveGeneration.singleRookAttacks(selectedPiece, board.occupied);
            if ((currentAttack & fKingBubble) != 0) {
                eAttackersOnKing++;
            }
            currentAttack &= inverseEOccupied;

            positionalScore -= (mgRookTable[eIndex][selectedPiece] * (256 - endGameFactor) + egRookTable[eIndex][selectedPiece] * endGameFactor) / 256;
            mobilityScore -= BitMethods.countBits(currentAttack) * RookMobilityValue;
        }

        currentBitBoard = board.fKnight;
        while (currentBitBoard != 0) {
            selectedPiece = BitMethods.getLS1B(currentBitBoard);
            currentBitBoard &= ~(1L << selectedPiece);

            currentAttack = MoveGeneration.singleKnightAttacks(selectedPiece);
            if ((currentAttack & eKingBubble) != 0) {
                fAttackersOnKing++;
            }
            currentAttack &= inverseFOccupied;

            positionalScore += (mgKnightTable[fIndex][selectedPiece] * (256 - endGameFactor) + egKnightTable[fIndex][selectedPiece] * endGameFactor) / 256;
            mobilityScore += BitMethods.countBits(currentAttack) * KnightMobilityValue;
        }

        currentBitBoard = board.eKnight;
        while (currentBitBoard != 0) {
            selectedPiece = BitMethods.getLS1B(currentBitBoard);
            currentBitBoard &= ~(1L << selectedPiece);

            currentAttack = MoveGeneration.singleKnightAttacks(selectedPiece);
            if ((currentAttack & fKingBubble) != 0) {
                eAttackersOnKing++;
            }
            currentAttack &= inverseEOccupied;

            positionalScore -= (mgKnightTable[eIndex][selectedPiece] * (256 - endGameFactor) + egKnightTable[eIndex][selectedPiece] * endGameFactor) / 256;
            mobilityScore -= BitMethods.countBits(currentAttack) * KnightMobilityValue;
        }

        currentBitBoard = board.fBishop;
        while (currentBitBoard != 0) {
            selectedPiece = BitMethods.getLS1B(currentBitBoard);
            currentBitBoard &= ~(1L << selectedPiece);

            currentAttack = MoveGeneration.singleBishopAttacks(selectedPiece, board.occupied);
            if ((currentAttack & eKingBubble) != 0) {
                fAttackersOnKing++;
            }
            currentAttack &= inverseFOccupied;

            positionalScore += (mgBishopTable[fIndex][selectedPiece] * (256 - endGameFactor) + egBishopTable[fIndex][selectedPiece] * endGameFactor) / 256;
            mobilityScore += BitMethods.countBits(currentAttack) * BishopMobilityValue;
        }

        currentBitBoard = board.eBishop;
        while (currentBitBoard != 0) {
            selectedPiece = BitMethods.getLS1B(currentBitBoard);
            currentBitBoard &= ~(1L << selectedPiece);

            currentAttack = MoveGeneration.singleBishopAttacks(selectedPiece, board.occupied);
            if ((currentAttack & fKingBubble) != 0) {
                eAttackersOnKing++;
            }
            currentAttack &= inverseEOccupied;

            positionalScore -= (mgBishopTable[eIndex][selectedPiece] * (256 - endGameFactor) + egBishopTable[eIndex][selectedPiece] * endGameFactor) / 256;
            mobilityScore -= BitMethods.countBits(currentAttack) * BishopMobilityValue;
        }

        currentBitBoard = board.fQueen;
        while (currentBitBoard != 0) {
            selectedPiece = BitMethods.getLS1B(currentBitBoard);
            currentBitBoard &= ~(1L << selectedPiece);

            currentAttack = MoveGeneration.singleQueenAttacks(selectedPiece, board.occupied);
            if ((currentAttack & eKingBubble) != 0) {
                fAttackersOnKing++;
            }
            currentAttack &= inverseFOccupied;

            positionalScore += (mgQueenTable[fIndex][selectedPiece] * (256 - endGameFactor) + egQueenTable[fIndex][selectedPiece] * endGameFactor) / 256;
            mobilityScore += BitMethods.countBits(currentAttack) * QueenMobilityValue;
        }

        currentBitBoard = board.eQueen;
        while (currentBitBoard != 0) {
            selectedPiece = BitMethods.getLS1B(currentBitBoard);
            currentBitBoard &= ~(1L << selectedPiece);

            currentAttack = MoveGeneration.singleQueenAttacks(selectedPiece, board.occupied);
            if ((currentAttack & fKingBubble) != 0) {
                eAttackersOnKing++;
            }
            currentAttack &= inverseEOccupied;

            positionalScore -= (mgQueenTable[eIndex][selectedPiece] * (256 - endGameFactor) + egQueenTable[eIndex][selectedPiece] * endGameFactor) / 256;
            mobilityScore -= BitMethods.countBits(currentAttack) * QueenMobilityValue;
        }
        defenseScore += kingAttackerScores[fAttackersOnKing] - kingAttackerScores[eAttackersOnKing];

        positionalScore += (mgKingTable[fIndex][fKSquare] * Math.max((256 - endGameFactor - 32), 0) + egKingTable[fIndex][fKSquare] * Math.max(128 - endGameFactor, 0)) / 256;
        positionalScore -= (mgKingTable[eIndex][eKSquare] * Math.max((256 - endGameFactor - 32), 0) + egKingTable[eIndex][eKSquare] * Math.max(128 - endGameFactor, 0)) / 256;

        int kingMobility = BitMethods.countBits(MoveGeneration.singleQueenAttacks(fKSquare, board.occupied) & inverseFOccupied) * 5;
        mobilityScore += kingMobility * MGKingMobilityValue * Math.max((256 - endGameFactor - 32), 0) / 256; //+ since MGKing is negative
        defenseScore += kingPawnShield[BitMethods.countBits(fKingBubble & board.fPawn)] * (256 - endGameFactor) / 256;

        kingMobility = BitMethods.countBits(MoveGeneration.singleQueenAttacks(eKSquare, board.occupied) & inverseEOccupied) * 5;
        mobilityScore -= kingMobility * MGKingMobilityValue * Math.max((256 - endGameFactor - 32), 0) / 256;
        defenseScore -= kingPawnShield[BitMethods.countBits(eKingBubble & board.ePawn)] * (256 - endGameFactor) / 256;

        materialDiff = (fPCount - ePCount) * PawnValue + (fRCount - eRCount) * RookValue + (fNCount - eNCount) * KnightValue + (fBCount - eBCount) * BishopValue + (fQCount - eQCount) * QueenValue;

        endGameKingCorner = endKingCornerEval(fKSquare, eKSquare) * endGameFactor / 256;
        if ((eRCount + eQCount) > (fRCount + fQCount)) {
            endGameKingCorner *= -1;
        }

        return materialDiff + positionalScore + endGameKingCorner + pawnBonuses + mobilityScore + defenseScore;
    }

    private static int endKingCornerEval(int fKing, int eKing) {
        int fRank, fFile, eRank, eFile, eval = 0, distBetweenKing;

        fRank = fKing / 8;
        fFile = fKing % 8;

        eRank = eKing / 8;
        eFile = eKing % 8;

        eval += (Math.max(3 - eRank, eRank - 4));
        eval += (Math.max(3 - eFile, eFile - 4));

        distBetweenKing = Math.abs(fRank - eRank) + Math.abs(fFile - eFile);
        eval -= distBetweenKing * 2;

        return eval * 3;
    }

    private static int passedPawnBonus(int fPawnSquare, long ePawns, boolean player) {
        long forwardMask = -1L;
        long files = 0L;
        int rank = fPawnSquare / 8;
        int file = fPawnSquare % 8;
        int bonus;

        if (player) {
            forwardMask <<= rank * 8;
            forwardMask = ~forwardMask;
            bonus = passedPawnBonus[rank];
        } else {
            forwardMask <<= (rank + 1) * 8;
            bonus = passedPawnBonus[7 - rank];
        }
        files |= MoveGeneration.files[file];
        if (file != 0) {
            files |= MoveGeneration.files[file - 1];
        }
        if (file != 7) {
            files |= MoveGeneration.files[file + 1];
        }
        forwardMask &= files;

        if ((forwardMask & ePawns) == 0) {
            return bonus; //random value
        }
        return 0;
    }

    private static int doubledPawnPenalty(long fPawns, long ePawns) {
        int penalty = 0;
        for (int i = 0; i < 8; i++) {
            if (BitMethods.countBits(fPawns&MoveGeneration.files[i])>1) {
                penalty += 10;
            }
            if (BitMethods.countBits(ePawns&MoveGeneration.files[i])>1) {
                penalty -= 10;
            }
        }
        return penalty;
    }
}