import java.io.Serializable;

import static java.lang.Character.isDigit;

public class Board implements Serializable {
    static final long wSKing = 4611686018427387904L, wLKing = 288230376151711744L;
    static final long wSRook = -6917529027641081856L, wLRook = 648518346341351424L;
    static final long bSKing = 64L, bLKing = 4L;
    static final long bSRook = 160L, bLRook = 9L;

    long fPawn, fRook, fKnight, fBishop, fQueen, fKing;
    long fPawnAttackMask, fRookAttackMask, fKnightAttackMask, fBishopAttackMask, fQueenAttackMask, fKingAttackMask, fAttackMask;
    long fOccupied;

    long ePawn, eRook, eKnight, eBishop, eQueen, eKing;
    long ePawnAttackMask, eRookAttackMask, eKnightAttackMask, eBishopAttackMask, eQueenAttackMask, eKingAttackMask, eAttackMask;
    long eOccupied;

    //long[] moveLog;
    long occupied;
    long previousPawnPush; //For en passant
    long castleRights; //First digit is wShort castle, Second is wLong, and so on
    long zobristKey;
    long startSquare, endSquare;
    long moveType; //000 first digit is 0 if reversible and 1 if not, second digit is 0 if not checking move and 1 if it is, third digit is for captures
    int mateFlag; //1 for checkmate, 2 for stalemate, 0 for nothing
    long capturingPiece, victimPiece;
    boolean enemyKingInCheck;
    long castleState; //11 for both white and black castle, 01 for only black, 00 for none
    boolean wSMoved, wLMoved, bSMoved, bLMoved;
    boolean player;
    boolean endGame; //in endgame

    public Board(String fen) {
        String[][] stringBoard = fenToBoard(fen);
        arrayToBitBoard(stringBoard);
        fOccupied = fPawn | fRook | fKnight | fBishop | fQueen | fKing;
        eOccupied = ePawn | eRook | eKnight | eBishop | eQueen | eKing;
        occupied = fOccupied | eOccupied;
        fillAttackMasks();
        updateCastleRights();
        zobristKey = Zobrist.generateKey(this);
        startSquare = 0;
        endSquare = 0;
        moveType = 0;
        mateFlag = 0;
        capturingPiece = 0;
        victimPiece = 0;
        enemyKingInCheck = false;
        castleState = 0;
        endGame = false;

        //moveLog = new long[0];
    }

    public Board(Board board) {
        //copy constructor
        this.fPawn = board.ePawn;
        this.fRook = board.eRook;
        this.fKnight = board.eKnight;
        this.fBishop = board.eBishop;
        this.fQueen = board.eQueen;
        this.fKing = board.eKing;
        this.fPawnAttackMask = board.ePawnAttackMask;
        this.fRookAttackMask = board.eRookAttackMask;
        this.fKnightAttackMask = board.eKnightAttackMask;
        this.fBishopAttackMask = board.eBishopAttackMask;
        this.fQueenAttackMask = board.eQueenAttackMask;
        this.fKingAttackMask = board.eKingAttackMask;
        this.fAttackMask = board.eAttackMask;
        this.fOccupied = board.eOccupied;

        this.ePawn = board.fPawn;
        this.eRook = board.fRook;
        this.eKnight = board.fKnight;
        this.eBishop = board.fBishop;
        this.eQueen = board.fQueen;
        this.eKing = board.fKing;
        this.ePawnAttackMask = board.fPawnAttackMask;
        this.eRookAttackMask = board.fRookAttackMask;
        this.eKnightAttackMask = board.fKnightAttackMask;
        this.eBishopAttackMask = board.fBishopAttackMask;
        this.eQueenAttackMask = board.fQueenAttackMask;
        this.eKingAttackMask = board.fKingAttackMask;
        this.eAttackMask = board.fAttackMask;
        this.eOccupied = board.fOccupied;
        this.occupied = board.occupied;
        this.previousPawnPush = board.previousPawnPush;
        this.castleRights = board.castleRights;
        this.zobristKey = board.zobristKey ^ Zobrist.sideKey;
        this.moveType = 0;
        this.mateFlag = board.mateFlag;
        this.capturingPiece = board.capturingPiece;
        this.victimPiece = board.victimPiece;
        this.enemyKingInCheck = board.enemyKingInCheck;
        this.castleState = board.castleState;
        this.wSMoved = board.wSMoved;
        this.wLMoved = board.wLMoved;
        this.bSMoved = board.bSMoved;
        this.bLMoved = board.bLMoved;
        this.player = !board.player;
        this.endGame = board.endGame;

        if (previousPawnPush != 0) {
            zobristKey ^= Zobrist.enPassantKeys[BitMethods.getLS1B(previousPawnPush)];
        }

        //this.moveLog = new long[board.moveLog.length+1];
        //System.arraycopy(board.moveLog, 0, this.moveLog, 0, board.moveLog.length);
    }

    public void makeMove(long move) {
        //moveLog[moveLog.length-1] = move;

        int startSquare = MoveList.getStartSquare(move);
        int endSquare = MoveList.getEndSquare(move);
        int piece = MoveList.getPiece(move);
        int promotedPiece = MoveList.getPromotePiece(move);
        int capture = MoveList.getCaptureFlag(move);
        int doublePush = MoveList.getDoublePushFlag(move);
        int enPassant = MoveList.getEnPassantFlag(move);
        int castle = MoveList.getCastleFlag(move);
        long copy;
        int selectedSquareNum;

        long startSquareBit = 1L << startSquare;
        long endSquareBit = 1L << endSquare;
        long changingBits = startSquareBit | endSquareBit;
        long pastPreviousPawnPush = previousPawnPush;
        long inversePastPreviousPawnPush = ~previousPawnPush;
        long previousCastleRights = castleRights;
        int shiftFactor = 0;
        previousPawnPush = 0;

        boolean fUpdatedQueen = false;
        boolean fUpdatedRook = false;
        boolean fUpdatedBishop = false;
        boolean eUpdatedQueen = false;
        boolean eUpdatedRook = false;
        boolean eUpdatedBishop = false;

        if (player) { //for updating zobrist key
            shiftFactor = 6;
        }
        zobristKey ^= Zobrist.pieceKeys[piece + shiftFactor][startSquare];
        zobristKey ^= Zobrist.pieceKeys[piece + shiftFactor][endSquare];
        this.startSquare = startSquareBit;
        this.endSquare = endSquareBit;

        eOccupied &= ~startSquareBit;
        eOccupied |= endSquareBit;
        if (capture == 1) {
            moveType = 1;
            fOccupied &= ~endSquareBit;
        }
        occupied = eOccupied | fOccupied;

        if (piece == 0) {//pawn move
            ePawn &= ~startSquareBit; //enemy piece since all piece names get swapped on board copy

            //cant have a double push or promotion or enPassant at same time
            if (doublePush == 1) { //if double push for en passant purpose
                previousPawnPush = 1L << endSquare;
                ePawn |= endSquareBit;
                zobristKey ^= Zobrist.enPassantKeys[BitMethods.getLS1B(previousPawnPush)];

            } else if (promotedPiece != 0) {
                if (promotedPiece == 4) {
                    eQueen |= endSquareBit;
                    zobristKey ^= Zobrist.pieceKeys[promotedPiece + shiftFactor][endSquare];
                    eQueenAttackMask |= MoveGeneration.singleQueenAttacks(endSquare, occupied); //can just add queen since even if it blocks a dff queen it will make up for it

                } else if (promotedPiece == 2) {
                    eKnight |= endSquareBit;
                    zobristKey ^= Zobrist.pieceKeys[promotedPiece + shiftFactor][endSquare];
                    eKnightAttackMask |= MoveGeneration.singleKnightAttacks(endSquare);

                } else if (promotedPiece == 1) {
                    eRook |= endSquareBit;
                    zobristKey ^= Zobrist.pieceKeys[promotedPiece + shiftFactor][endSquare];
                    eRookAttackMask |= MoveGeneration.singleRookAttacks(endSquare, occupied);

                } else {
                    eBishop |= endSquareBit;
                    zobristKey ^= Zobrist.pieceKeys[promotedPiece + shiftFactor][endSquare];
                    eBishopAttackMask |= MoveGeneration.singleBishopAttacks(endSquare, occupied);

                }
                zobristKey ^= Zobrist.pieceKeys[piece + shiftFactor][endSquare];

            } else if (enPassant == 1) {
                fPawn &= inversePastPreviousPawnPush;
                fOccupied &= inversePastPreviousPawnPush;
                occupied &= inversePastPreviousPawnPush;

                moveType = 1; //for double check purposes
                changingBits |= pastPreviousPawnPush; //also a changing bit
                ePawn |= endSquareBit;

                zobristKey ^= Zobrist.pieceKeys[promotedPiece + (6 - shiftFactor)][BitMethods.getLS1B(pastPreviousPawnPush)]; //6-shiftFactor makes shiftFactor opposite from 6 to 0 or 0 to 6 for removing friendly piece from zobrist
                fPawnAttackMask = MoveGeneration.pawnAttackMask(fPawn, player);
            } else {
                ePawn |= endSquareBit;
            }
            ePawnAttackMask = MoveGeneration.pawnAttackMask(ePawn, !player); //update pawn mask

        } else if (piece == 1) {
            eRook &= ~startSquareBit;
            eRook |= endSquareBit;

            eUpdatedRook = true;
            copy = eRook;
            eRookAttackMask = 0;
            while (copy != 0) {
                selectedSquareNum = BitMethods.getLS1B(copy);
                copy &= ~1L << selectedSquareNum;
                eRookAttackMask |= MoveGeneration.singleRookAttacks(selectedSquareNum, occupied);
            }

            if (player) {
                if (!bSMoved && (startSquareBit & 128L) != 0) {
                    bSMoved = true;
                } else if (!bLMoved && (startSquareBit & 1L) != 0) {
                    bLMoved = true;
                }
            } else {
                if (!wSMoved && (startSquareBit & -9223372036854775808L) != 0) {
                    wSMoved = true;
                } else if (!wLMoved && (startSquareBit & 72057594037927936L) != 0) {
                    wLMoved = true;
                }
            }

        } else if (piece == 2) {
            eKnight &= ~startSquareBit;
            eKnight |= endSquareBit;

            copy = eKnight;
            eKnightAttackMask = 0;
            while (copy != 0) {
                selectedSquareNum = BitMethods.getLS1B(copy);
                copy &= ~1L << selectedSquareNum;
                eKnightAttackMask |= MoveGeneration.singleKnightAttacks(selectedSquareNum);
            }

        } else if (piece == 3) {
            eBishop &= ~startSquareBit;
            eBishop |= endSquareBit;

            eUpdatedBishop = true;
            copy = eBishop;
            eBishopAttackMask = 0;
            while (copy != 0) {
                selectedSquareNum = BitMethods.getLS1B(copy);
                copy &= ~1L << selectedSquareNum;
                eBishopAttackMask |= MoveGeneration.singleBishopAttacks(selectedSquareNum, occupied);
            }

        } else if (piece == 4) {
            eQueen &= ~startSquareBit;
            eQueen |= endSquareBit;

            eUpdatedQueen = true;
            copy = eQueen;
            eQueenAttackMask = 0;
            while (copy != 0) {
                selectedSquareNum = BitMethods.getLS1B(copy);
                copy &= ~1L << selectedSquareNum;
                eQueenAttackMask |= MoveGeneration.singleQueenAttacks(selectedSquareNum, occupied);
            }

        } else if (piece == 5) {
            eKing &= ~startSquareBit;
            eKing |= endSquareBit;
            eKingAttackMask = MoveGeneration.singleKingAttacks(endSquareBit);

            if (player) {
                bSMoved = true;
                bLMoved = true;
            } else {
                wSMoved = true;
                wLMoved = true;
            }

            if (castle == 1) {
                if (endSquareBit == wSKing) {
                    eRook ^= wSRook;
                    eOccupied ^= wSRook;
                    occupied ^= wSRook;
                    zobristKey ^= Zobrist.pieceKeys[1 + shiftFactor][63] ^ Zobrist.pieceKeys[1 + shiftFactor][61]; //1 is piece key for rook
                } else if (endSquareBit == wLKing) {
                    eRook ^= wLRook;
                    eOccupied ^= wLRook;
                    occupied ^= wLRook;
                    zobristKey ^= Zobrist.pieceKeys[1 + shiftFactor][56] ^ Zobrist.pieceKeys[1 + shiftFactor][59];
                } else if (endSquareBit == bSKing) {
                    eRook ^= bSRook;
                    eOccupied ^= bSRook;
                    occupied ^= bSRook;
                    zobristKey ^= Zobrist.pieceKeys[1 + shiftFactor][7] ^ Zobrist.pieceKeys[1 + shiftFactor][5];
                } else {
                    eRook ^= bLRook;
                    eOccupied ^= bLRook;
                    occupied ^= bLRook;
                    zobristKey ^= Zobrist.pieceKeys[1 + shiftFactor][0] ^ Zobrist.pieceKeys[1 + shiftFactor][3];
                }
            }

            copy = eRook;
            eRookAttackMask = 0;
            while (copy != 0) {
                selectedSquareNum = BitMethods.getLS1B(copy);
                copy &= ~1L << selectedSquareNum;
                eRookAttackMask |= MoveGeneration.singleRookAttacks(selectedSquareNum, occupied);
            }
        }

        shiftFactor = 6 - shiftFactor;
        if (capture == 1) {
            if ((fPawn & endSquareBit) != 0) {
                fPawn &= ~endSquareBit;
                zobristKey ^= Zobrist.pieceKeys[shiftFactor][endSquare];

                fPawnAttackMask = MoveGeneration.pawnAttackMask(fPawn, player);

            } else if ((fRook & endSquareBit) != 0) {
                fRook &= ~endSquareBit;
                zobristKey ^= Zobrist.pieceKeys[1 + shiftFactor][endSquare];

                //castle purposes
                if (player) {
                    if ((endSquareBit & -9223372036854775808L) != 0) {
                        wSMoved = true;
                    } else if ((endSquareBit & 72057594037927936L) != 0) {
                        wLMoved = true;
                    }
                } else {
                    if ((endSquareBit & 128L) != 0) {
                        bSMoved = true;
                    } else if ((endSquareBit & 1L) != 0) {
                        bLMoved = true;
                    }
                }

                copy = fRook;
                fRookAttackMask = 0;
                while (copy != 0) {
                    selectedSquareNum = BitMethods.getLS1B(copy);
                    copy &= ~1L << selectedSquareNum;
                    fRookAttackMask |= MoveGeneration.singleRookAttacks(selectedSquareNum, occupied);
                }
                fUpdatedRook = true;

            } else if ((fKnight & endSquareBit) != 0) {
                fKnight &= ~endSquareBit;
                zobristKey ^= Zobrist.pieceKeys[2 + shiftFactor][endSquare];

                copy = fKnight;
                fKnightAttackMask = 0;
                while (copy != 0) {
                    selectedSquareNum = BitMethods.getLS1B(copy);
                    copy &= ~1L << selectedSquareNum;
                    fKnightAttackMask |= MoveGeneration.singleKnightAttacks(selectedSquareNum);
                }

            } else if ((fBishop & endSquareBit) != 0) {
                fBishop &= ~endSquareBit;
                zobristKey ^= Zobrist.pieceKeys[3 + shiftFactor][endSquare];

                copy = fBishop;
                fBishopAttackMask = 0;
                while (copy != 0) {
                    selectedSquareNum = BitMethods.getLS1B(copy);
                    copy &= ~1L << selectedSquareNum;
                    fBishopAttackMask |= MoveGeneration.singleBishopAttacks(selectedSquareNum, occupied);
                }
                fUpdatedBishop = true;

            } else if ((fQueen & endSquareBit) != 0) {
                fQueen &= ~endSquareBit;
                zobristKey ^= Zobrist.pieceKeys[4 + shiftFactor][endSquare];

                copy = fQueen;
                fQueenAttackMask = 0;
                while (copy != 0) {
                    selectedSquareNum = BitMethods.getLS1B(copy);
                    copy &= ~1L << selectedSquareNum;
                    fQueenAttackMask |= MoveGeneration.singleQueenAttacks(selectedSquareNum, occupied);
                }
                fUpdatedQueen = true;
            }
        }

        if ((changingBits & (fRookAttackMask | fBishopAttackMask | fQueenAttackMask)) != 0) {
            if (!fUpdatedRook && (changingBits & fRookAttackMask) != 0) {
                copy = fRook;
                fRookAttackMask = 0;
                while (copy != 0) {
                    selectedSquareNum = BitMethods.getLS1B(copy);
                    copy &= ~1L << selectedSquareNum;
                    fRookAttackMask |= MoveGeneration.singleRookAttacks(selectedSquareNum, occupied);
                }
            }
            if (!fUpdatedBishop && (changingBits & fBishopAttackMask) != 0) {
                copy = fBishop;
                fBishopAttackMask = 0;
                while (copy != 0) {
                    selectedSquareNum = BitMethods.getLS1B(copy);
                    copy &= ~1L << selectedSquareNum;
                    fBishopAttackMask |= MoveGeneration.singleBishopAttacks(selectedSquareNum, occupied);
                }
            }
            if (!fUpdatedQueen && (changingBits & fQueenAttackMask) != 0) {
                copy = fQueen;
                fQueenAttackMask = 0;
                while (copy != 0) {
                    selectedSquareNum = BitMethods.getLS1B(copy);
                    copy &= ~1L << selectedSquareNum;
                    fQueenAttackMask |= MoveGeneration.singleQueenAttacks(selectedSquareNum, occupied);
                }
            }
        } //if the move affects friendly sliding pieces
        if ((changingBits & (eRookAttackMask | eBishopAttackMask | eQueenAttackMask)) != 0) {
            if (!eUpdatedRook && (changingBits & eRookAttackMask) != 0) {
                copy = eRook;
                eRookAttackMask = 0;
                while (copy != 0) {
                    selectedSquareNum = BitMethods.getLS1B(copy);
                    copy &= ~1L << selectedSquareNum;
                    eRookAttackMask |= MoveGeneration.singleRookAttacks(selectedSquareNum, occupied);
                }
            }
            if (!eUpdatedBishop && (changingBits & eBishopAttackMask) != 0) {
                copy = eBishop;
                eBishopAttackMask = 0;
                while (copy != 0) {
                    selectedSquareNum = BitMethods.getLS1B(copy);
                    copy &= ~1L << selectedSquareNum;
                    eBishopAttackMask |= MoveGeneration.singleBishopAttacks(selectedSquareNum, occupied);
                }
            }
            if (!eUpdatedQueen && (changingBits & eQueenAttackMask) != 0) {
                copy = eQueen;
                eQueenAttackMask = 0;
                while (copy != 0) {
                    selectedSquareNum = BitMethods.getLS1B(copy);
                    copy &= ~1L << selectedSquareNum;
                    eQueenAttackMask |= MoveGeneration.singleQueenAttacks(selectedSquareNum, occupied);
                }
            }
        } //if the move affects enemy sliding pieces
        eAttackMask = ePawnAttackMask | eRookAttackMask | eKnightAttackMask | eBishopAttackMask | eQueenAttackMask | eKingAttackMask;
        fAttackMask = fPawnAttackMask | fRookAttackMask | fKnightAttackMask | fBishopAttackMask | fQueenAttackMask | fKingAttackMask;

        updateCastleRights(); //update after all moves made
        if (previousCastleRights != castleRights) {
            zobristKey ^= Zobrist.castleKeys[(int) previousCastleRights];
            zobristKey ^= Zobrist.castleKeys[(int) castleRights];
        }

        occupied = fOccupied | eOccupied;
    }

    /*private String getMoves() {
        String output = "";
        for (int i = 0; i < moveLog.length; i++) {
            output+= String.format("%d. %s ", i+1, BitMethods.moveToStringMove(MoveList.getStartSquare(moveLog[i]))+BitMethods.moveToStringMove(MoveList.getEndSquare(moveLog[i])));
        }
        return output;
    }*/

    private void updateCastleRights() {
        long rights = 0L;
        if (!wSMoved) {
            rights |= 8;
        }
        if (!wLMoved) {
            rights |= 4;
        }
        if (!bSMoved) {
            rights |= 2;
        }
        if (!bLMoved) {
            rights |= 1;
        }
        castleRights = rights;
    }

    private void fillAttackMasks() {
        long eRook = this.eRook;
        long fRook = this.fRook;
        long eKnight = this.eKnight;
        long fKnight = this.fKnight;
        long eBishop = this.eBishop;
        long fBishop = this.fBishop;
        long eQueen = this.eQueen;
        long fQueen = this.fQueen;
        long eKing = this.eKing;
        long fKing = this.fKing;
        int currentPieceSquare;
        ePawnAttackMask = MoveGeneration.pawnAttackMask(ePawn, !player);
        fPawnAttackMask = MoveGeneration.pawnAttackMask(fPawn, player);
        eRookAttackMask = 0;
        fRookAttackMask = 0;
        eKnightAttackMask = 0;
        fKnightAttackMask = 0;
        eBishopAttackMask = 0;
        fBishopAttackMask = 0;
        eQueenAttackMask = 0;
        fQueenAttackMask = 0;
        eKingAttackMask = 0;
        fKingAttackMask = 0;


        // Loop for black rooks
        while (eRook != 0) {
            currentPieceSquare = BitMethods.getLS1B(eRook);
            eRook &= ~(1L << currentPieceSquare);
            eRookAttackMask |= MoveGeneration.singleRookAttacks(currentPieceSquare, occupied);
        }

// Loop for white rooks
        while (fRook != 0) {
            currentPieceSquare = BitMethods.getLS1B(fRook);
            fRook &= ~(1L << currentPieceSquare);
            fRookAttackMask |= MoveGeneration.singleRookAttacks(currentPieceSquare, occupied);
        }

// Loop for black knights
        while (eKnight != 0) {
            currentPieceSquare = BitMethods.getLS1B(eKnight);
            eKnight &= ~(1L << currentPieceSquare);
            eKnightAttackMask |= MoveGeneration.singleKnightAttacks(currentPieceSquare);
        }

// Loop for white knights
        while (fKnight != 0) {
            currentPieceSquare = BitMethods.getLS1B(fKnight);
            fKnight &= ~(1L << currentPieceSquare);
            fKnightAttackMask |= MoveGeneration.singleKnightAttacks(currentPieceSquare);
        }

// Loop for black bishops
        while (eBishop != 0) {
            currentPieceSquare = BitMethods.getLS1B(eBishop);
            eBishop &= ~(1L << currentPieceSquare);
            eBishopAttackMask |= MoveGeneration.singleBishopAttacks(currentPieceSquare, occupied);
        }

// Loop for white bishops
        while (fBishop != 0) {
            currentPieceSquare = BitMethods.getLS1B(fBishop);
            fBishop &= ~(1L << currentPieceSquare);
            fBishopAttackMask |= MoveGeneration.singleBishopAttacks(currentPieceSquare, occupied);
        }

// Loop for black queens
        while (eQueen != 0) {
            currentPieceSquare = BitMethods.getLS1B(eQueen);
            eQueen &= ~(1L << currentPieceSquare);
            eQueenAttackMask |= MoveGeneration.singleQueenAttacks(currentPieceSquare, occupied);
        }

// Loop for white queens
        while (fQueen != 0) {
            currentPieceSquare = BitMethods.getLS1B(fQueen);
            fQueen &= ~(1L << currentPieceSquare);
            fQueenAttackMask |= MoveGeneration.singleQueenAttacks(currentPieceSquare, occupied);
        }

// Loop for black kings
        while (eKing != 0) {
            currentPieceSquare = BitMethods.getLS1B(eKing);
            eKing &= ~(1L << currentPieceSquare);
            eKingAttackMask |= MoveGeneration.singleKingAttacks(currentPieceSquare);
        }

// Loop for white kings
        while (fKing != 0) {
            currentPieceSquare = BitMethods.getLS1B(fKing);
            fKing &= ~(1L << currentPieceSquare);
            fKingAttackMask |= MoveGeneration.singleKingAttacks(currentPieceSquare);
        }

        fAttackMask = fPawnAttackMask | fRookAttackMask | fKnightAttackMask | fBishopAttackMask | fQueenAttackMask | fKingAttackMask;
        eAttackMask = ePawnAttackMask | eRookAttackMask | eKnightAttackMask | eBishopAttackMask | eQueenAttackMask | eKingAttackMask;
    }

    public boolean checkAttackMasks() {
        long eP, eR, eN, eB, eQ, eK, eA;
        long fP, fR, fN, fB, fQ, fK, fA;

        eP = ePawnAttackMask;
        eR = eRookAttackMask;
        eN = eKnightAttackMask;
        eB = eBishopAttackMask;
        eQ = eQueenAttackMask;
        eK = eKingAttackMask;
        eA = eAttackMask;
        fP = fPawnAttackMask;
        fR = fRookAttackMask;
        fN = fKnightAttackMask;
        fB = fBishopAttackMask;
        fQ = fQueenAttackMask;
        fK = fKingAttackMask;
        fA = fAttackMask;

        fillAttackMasks();
        return eP == ePawnAttackMask && eR == eRookAttackMask &&
                eN == eKnightAttackMask && eB == eBishopAttackMask &&
                eQ == eQueenAttackMask && eK == eKingAttackMask &&
                eA == eAttackMask && fP == fPawnAttackMask &&
                fR == fRookAttackMask && fN == fKnightAttackMask &&
                fB == fBishopAttackMask && fQ == fQueenAttackMask &&
                fK == fKingAttackMask && fA == fAttackMask;
    }

    private String[][] fenToBoard(String fen) {
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
        player = fen.charAt(end + 1) != 'b';
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
            previousPawnPush = 0L;
        } else {
            pawnPush = fen.substring(fen.length() - 2);
            previousPawnPush = BitMethods.stringMoveToLong(pawnPush);
            if (player) {
                previousPawnPush <<= 8;
            } else {
                previousPawnPush >>= 8;
            }
        }
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (tempBoard[i][j] == null) {
                    tempBoard[i][j] = " ";
                }
            }
        }
        return tempBoard;
    }

    private void arrayToBitBoard(String[][] stringBoard) {
        String piece, binary;
        ePawn = 0L;
        eRook = 0L;
        eKnight = 0L;
        eBishop = 0L;
        eQueen = 0L;
        eKing = 0L;
        fPawn = 0L;
        fRook = 0L;
        fKnight = 0L;
        fBishop = 0L;
        fQueen = 0L;
        fKing = 0L;
        for (int i = 0; i < 64; i++) {
            piece = stringBoard[i / 8][i % 8];
            binary = "0000000000000000000000000000000000000000000000000000000000000000";
            binary = binary.substring(i + 1) + "1" + binary.substring(0, i);
            if (!piece.equals(" ")) {
                if (player) {
                    switch (piece) {
                        case "p" -> ePawn += BitMethods.stringToLong(binary);
                        case "r" -> eRook += BitMethods.stringToLong(binary);
                        case "n" -> eKnight += BitMethods.stringToLong(binary);
                        case "b" -> eBishop += BitMethods.stringToLong(binary);
                        case "q" -> eQueen += BitMethods.stringToLong(binary);
                        case "k" -> eKing += BitMethods.stringToLong(binary);
                        case "P" -> fPawn += BitMethods.stringToLong(binary);
                        case "R" -> fRook += BitMethods.stringToLong(binary);
                        case "N" -> fKnight += BitMethods.stringToLong(binary);
                        case "B" -> fBishop += BitMethods.stringToLong(binary);
                        case "Q" -> fQueen += BitMethods.stringToLong(binary);
                        case "K" -> fKing += BitMethods.stringToLong(binary);
                    }
                } else {
                    switch (piece) {
                        case "p" -> fPawn += BitMethods.stringToLong(binary);
                        case "r" -> fRook += BitMethods.stringToLong(binary);
                        case "n" -> fKnight += BitMethods.stringToLong(binary);
                        case "b" -> fBishop += BitMethods.stringToLong(binary);
                        case "q" -> fQueen += BitMethods.stringToLong(binary);
                        case "k" -> fKing += BitMethods.stringToLong(binary);
                        case "P" -> ePawn += BitMethods.stringToLong(binary);
                        case "R" -> eRook += BitMethods.stringToLong(binary);
                        case "N" -> eKnight += BitMethods.stringToLong(binary);
                        case "B" -> eBishop += BitMethods.stringToLong(binary);
                        case "Q" -> eQueen += BitMethods.stringToLong(binary);
                        case "K" -> eKing += BitMethods.stringToLong(binary);
                    }
                }
            }
        }
    }

    public String[][] bitsToBoard() {
        String[][] board = new String[8][8];
        for (int i = 0; i < 64; i++) {
            if (player) {
                if (((ePawn >> i) & 1) == 1) {
                    board[i / 8][i % 8] = "p";
                } else if (((eRook >> i) & 1) == 1) {
                    board[i / 8][i % 8] = "r";
                } else if (((eKnight >> i) & 1) == 1) {
                    board[i / 8][i % 8] = "n";
                } else if (((eBishop >> i) & 1) == 1) {
                    board[i / 8][i % 8] = "b";
                } else if (((eQueen >> i) & 1) == 1) {
                    board[i / 8][i % 8] = "q";
                } else if (((eKing >> i) & 1) == 1) {
                    board[i / 8][i % 8] = "k";
                } else if (((fPawn >> i) & 1) == 1) {
                    board[i / 8][i % 8] = "P";
                } else if (((fRook >> i) & 1) == 1) {
                    board[i / 8][i % 8] = "R";
                } else if (((fKnight >> i) & 1) == 1) {
                    board[i / 8][i % 8] = "N";
                } else if (((fBishop >> i) & 1) == 1) {
                    board[i / 8][i % 8] = "B";
                } else if (((fQueen >> i) & 1) == 1) {
                    board[i / 8][i % 8] = "Q";
                } else if (((fKing >> i) & 1) == 1) {
                    board[i / 8][i % 8] = "K";
                } else {
                    board[i / 8][i % 8] = " ";
                }
            } else {
                if (((fPawn >> i) & 1) == 1) {
                    board[i / 8][i % 8] = "p";
                } else if (((fRook >> i) & 1) == 1) {
                    board[i / 8][i % 8] = "r";
                } else if (((fKnight >> i) & 1) == 1) {
                    board[i / 8][i % 8] = "n";
                } else if (((fBishop >> i) & 1) == 1) {
                    board[i / 8][i % 8] = "b";
                } else if (((fQueen >> i) & 1) == 1) {
                    board[i / 8][i % 8] = "q";
                } else if (((fKing >> i) & 1) == 1) {
                    board[i / 8][i % 8] = "k";
                } else if (((ePawn >> i) & 1) == 1) {
                    board[i / 8][i % 8] = "P";
                } else if (((eRook >> i) & 1) == 1) {
                    board[i / 8][i % 8] = "R";
                } else if (((eKnight >> i) & 1) == 1) {
                    board[i / 8][i % 8] = "N";
                } else if (((eBishop >> i) & 1) == 1) {
                    board[i / 8][i % 8] = "B";
                } else if (((eQueen >> i) & 1) == 1) {
                    board[i / 8][i % 8] = "Q";
                } else if (((eKing >> i) & 1) == 1) {
                    board[i / 8][i % 8] = "K";
                } else {
                    board[i / 8][i % 8] = " ";
                }
            }
        }
        return board;
    }

    public String boardToFen() {
        String fen = "";
        String[][] stringBoard = bitsToBoard();
        int rowCount;
        String row, currentPiece, castleRights;
        for (int i = 0; i < 8; i++) {
            rowCount = 0;
            row = "";
            for (int j = 0; j < 8; j++) {
                currentPiece = stringBoard[i][j];
                if (currentPiece.equals(" ")) {
                    rowCount++;
                } else {
                    if (rowCount != 0) {
                        row += rowCount;
                    }
                    row += currentPiece;
                    rowCount = 0;
                }
            }
            if (rowCount != 0) {
                row += rowCount;
            }
            fen+=row;
            if (i != 7) {
                fen+="/";
            }
        }
        fen += " ";
        if (player) {
            fen += "w";
        } else {
            fen += "b";
        }
        fen += " ";
        castleRights = "";
        if (!wSMoved) {
            castleRights += "K";
        }
        if (!wLMoved) {
            castleRights += "Q";
        }
        if (!bSMoved) {
            castleRights += "k";
        }
        if (!bLMoved) {
            castleRights += "q";
        }
        if (castleRights.equals("")) {
            fen += "-";
        } else {
            fen += castleRights;
        }
        fen += " ";
        if (previousPawnPush == 0) {
            fen += "-";
        } else {
            if (player) {
                fen+= BitMethods.moveToStringMove(previousPawnPush>>8);
            } else {
                fen += BitMethods.moveToStringMove(previousPawnPush<<8);
            }
        }
        return fen;
    }

    public String toString() {
        String output = "";
        String[][] stringBoard = bitsToBoard();

        // Print the chess board
        output += "|";
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                output += String.format("%s|", stringBoard[i][j]);
            }
            if (i != 7) {
                output += "\n|";
            }
        }
        output += "\n\n";

        // Print other fields
        output += "previousPawnPush: " + previousPawnPush + "\n";
        output += "castleRights: " + castleRights + "\n";
        output += "zobristKey: " + zobristKey + "\n";
        output += "startSquare: " + startSquare + "\n";
        output += "endSquare: " + endSquare + "\n";
        output += "moveType: " + moveType + "\n";
        output += "mateFlag: " + mateFlag + "\n";
        output += "capturingPiece: " + capturingPiece + "\n";
        output += "victimPiece: " + victimPiece + "\n";
        output += "enemyKingInCheck: " + enemyKingInCheck + "\n";
        output += "castleState: " + castleState + "\n";
        output += "player: " + player + "\n";

        output += "\n";
        return output;
    }
}