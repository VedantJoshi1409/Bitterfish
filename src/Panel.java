import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class Panel extends JPanel implements MouseListener {
    Image bP, bR, bN, bB, bQ, bK, wP, wR, wN, wB, wQ, wK;
    Color light = new Color(240, 218, 181);
    Color dark = new Color(181, 135, 99);
    Color lightRed = new Color(252, 70, 70, 127);
    Color background = new Color(22, 21, 18);
    Color lightYellow = new Color(255,255,51, 63);
    JLabel label = new JLabel();
    Graphics2D g2D;

    int selectedPieceSquare = -1;
    int promotionFlag = -1;
    long moveSquares = 0;

    final int squareLength;
    int inverseWidth;
    int width;
    int height;
    boolean flip;
    Board board;

    Panel(Board board, double scale, boolean flip) {
        squareLength = (int) (scale * 100);
        width = squareLength * 8;
        height = squareLength * 8;
        this.flip = flip;
        inverseWidth = squareLength * 7;

        this.board = board;
        this.setPreferredSize(new Dimension(width, height));
        this.setLayout(null);
        this.add(label);
        label.setBounds(0, 0, height, height);
        label.setOpaque(false);
        label.addMouseListener(this);

        bP = new ImageIcon("Images/bPawn.png").getImage();
        bR = new ImageIcon("Images/bRook.png").getImage();
        bN = new ImageIcon("Images/bKnight.png").getImage();
        bB = new ImageIcon("Images/bBishop.png").getImage();
        bQ = new ImageIcon("Images/bQueen.png").getImage();
        bK = new ImageIcon("Images/bKing.png").getImage();
        wP = new ImageIcon("Images/wPawn.png").getImage();
        wR = new ImageIcon("Images/wRook.png").getImage();
        wN = new ImageIcon("Images/wKnight.png").getImage();
        wB = new ImageIcon("Images/wBishop.png").getImage();
        wQ = new ImageIcon("Images/wQueen.png").getImage();
        wK = new ImageIcon("Images/wKing.png").getImage();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g2D = (Graphics2D) g;
        createBoard();
        populateBoard();
        highlightSquares();
        if (promotionFlag == 0) {
            promotion();
        }
    }

    public void promotion() {
        int x, y;
        if (flip) {
            x = inverseWidth - selectedPieceSquare % 8 * squareLength;
            y = inverseWidth - selectedPieceSquare / 8 * squareLength;
        } else {
            x = selectedPieceSquare % 8 * squareLength;
            y = selectedPieceSquare / 8 * squareLength;
        }
        g2D.setPaint(lightRed);
        g2D.fillRect(x, y, squareLength, squareLength);
        if (board.player) {
            g2D.drawImage(wQ, x, y, squareLength / 2, squareLength / 2, null);
            g2D.drawImage(wR, x + squareLength / 2, y, squareLength / 2, squareLength / 2, null);
            g2D.drawImage(wB, x, y + squareLength / 2, squareLength / 2, squareLength / 2, null);
            g2D.drawImage(wN, x + squareLength / 2, y + squareLength / 2, squareLength / 2, squareLength / 2, null);
        } else {
            g2D.drawImage(bQ, x, y, squareLength / 2, squareLength / 2, null);
            g2D.drawImage(bR, x + squareLength / 2, y, squareLength / 2, squareLength / 2, null);
            g2D.drawImage(bB, x, y + squareLength / 2, squareLength / 2, squareLength / 2, null);
            g2D.drawImage(bN, x + squareLength / 2, y + squareLength / 2, squareLength / 2, squareLength / 2, null);
        }
        promotionFlag = 0;
        this.repaint();
    }

    private void createBoard() {
        boolean color = false;
        for (int i = 0; i < height; i += squareLength) {
            for (int j = 0; j < height; j += squareLength) {
                if (color) {
                    g2D.setPaint(light);
                } else {
                    g2D.setPaint(dark);
                }
                color = !color;
                g2D.fillRect(i, j, squareLength, squareLength);
            }
            color = !color;
        }
    }

    private void populateBoard() {
        String[][] stringBoard = board.bitsToBoard();
        int x, y;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (!stringBoard[j][i].equals(" ")) {
                    if (flip) {
                        x = inverseWidth - i * squareLength;
                        y = inverseWidth - j * squareLength;
                    } else {
                        x = i * squareLength;
                        y = j * squareLength;
                    }
                    switch (stringBoard[j][i]) {
                        case "p" -> g2D.drawImage(bP, x, y, squareLength, squareLength, null);
                        case "r" -> g2D.drawImage(bR, x, y, squareLength, squareLength, null);
                        case "n" -> g2D.drawImage(bN, x, y, squareLength, squareLength, null);
                        case "b" -> g2D.drawImage(bB, x, y, squareLength, squareLength, null);
                        case "q" -> g2D.drawImage(bQ, x, y, squareLength, squareLength, null);
                        case "k" -> g2D.drawImage(bK, x, y, squareLength, squareLength, null);
                        case "P" -> g2D.drawImage(wP, x, y, squareLength, squareLength, null);
                        case "R" -> g2D.drawImage(wR, x, y, squareLength, squareLength, null);
                        case "N" -> g2D.drawImage(wN, x, y, squareLength, squareLength, null);
                        case "B" -> g2D.drawImage(wB, x, y, squareLength, squareLength, null);
                        case "Q" -> g2D.drawImage(wQ, x, y, squareLength, squareLength, null);
                        case "K" -> g2D.drawImage(wK, x, y, squareLength, squareLength, null);
                    }
                }
            }
        }
    }

    private void highlightSquares() {
        int x, y;
        g2D.setPaint(lightYellow);
        if (flip) {
            g2D.fillRect(inverseWidth-BitMethods.getLS1B(board.startSquare)%8*squareLength, inverseWidth-BitMethods.getLS1B(board.startSquare)/8*squareLength, squareLength, squareLength);
            g2D.fillRect(inverseWidth-BitMethods.getLS1B(board.endSquare)%8*squareLength, inverseWidth-BitMethods.getLS1B(board.endSquare)/8*squareLength, squareLength, squareLength);
            x = inverseWidth - selectedPieceSquare % 8 * squareLength;
            y = inverseWidth - selectedPieceSquare / 8 * squareLength;
        } else {
            g2D.fillRect(BitMethods.getLS1B(board.startSquare)%8*squareLength, BitMethods.getLS1B(board.startSquare)/8*squareLength, squareLength, squareLength);
            g2D.fillRect(BitMethods.getLS1B(board.endSquare)%8*squareLength, BitMethods.getLS1B(board.endSquare)/8*squareLength, squareLength, squareLength);
            x = selectedPieceSquare % 8 * squareLength;
            y = selectedPieceSquare / 8 * squareLength;
        }
        if (selectedPieceSquare != -1 && moveSquares != 0) {
            g2D.setPaint(lightRed);
            g2D.fillRect(x, y, squareLength, squareLength);
            int selectedPiece;
            while (moveSquares != 0) {
                selectedPiece = BitMethods.getLS1B(moveSquares);
                moveSquares &= ~1L << selectedPiece;
                if (flip) {
                    x = inverseWidth - selectedPiece % 8 * squareLength;
                    y = inverseWidth - selectedPiece / 8 * squareLength;
                } else {
                    x = selectedPiece % 8 * squareLength;
                    y = selectedPiece / 8 * squareLength;
                }
                g2D.fillRect(x, y, squareLength, squareLength);
            }
        }
    }

    private void selectPiece() {
        char pieceType = board.bitsToBoard()[selectedPieceSquare / 8][selectedPieceSquare % 8].toLowerCase().charAt(0);

        long[] checkInfo = MoveGeneration.checkSquares(board.fKing, board.occupied, board.ePawn, board.ePawnAttackMask, board.moveType, board.eRook, board.eKnight, board.eKnightAttackMask, board.eBishop, board.eQueen, board.player);
        boolean doubleCheck = false;
        long checkSquares = -1L;
        if (checkInfo[1] != 0) {
            if (checkInfo[1] > 1) {
                doubleCheck = true;
            }
            checkSquares = checkInfo[0];
        }
        if (pieceType != ' ') {
            if (!doubleCheck) {
                if (pieceType == 'p') {
                    moveSquares |= MoveGeneration.singlePawnForwardMoves(1L << selectedPieceSquare, board.player, board.occupied);
                    moveSquares |= MoveGeneration.singlePawnAttacks(1L << selectedPieceSquare, board.player) & board.eOccupied;
                    if ((MoveGeneration.enPassantSquares(board.previousPawnPush) & 1L << selectedPieceSquare) != 0) {
                        if (!MoveGeneration.enPassantPinCheck(board.fKing, board.fPawn, board.ePawn, board.occupied, board.eRook, board.eBishop, board.eQueen)) {
                            if (board.player) {
                                moveSquares |= board.previousPawnPush >> 8;
                            } else {
                                moveSquares |= board.previousPawnPush << 8;
                            }
                        }
                    }
                    moveSquares &= checkSquares;
                } else if (pieceType == 'r') {
                    moveSquares |= MoveGeneration.singleRookAttacks(selectedPieceSquare, board.occupied) & ~board.fOccupied & checkSquares;
                } else if (pieceType == 'n') {
                    moveSquares |= MoveGeneration.singleKnightAttacks(selectedPieceSquare) & ~board.fOccupied & checkSquares;
                } else if (pieceType == 'b') {
                    moveSquares |= MoveGeneration.singleBishopAttacks(selectedPieceSquare, board.occupied) & ~board.fOccupied & checkSquares;
                } else if (pieceType == 'q') {
                    moveSquares |= MoveGeneration.singleQueenAttacks(selectedPieceSquare, board.occupied) & ~board.fOccupied & checkSquares;
                }
            }
            if (pieceType == 'k') {
                moveSquares |= MoveGeneration.singleKingAttacks(selectedPieceSquare) & ~board.fOccupied & ~board.eAttackMask & checkInfo[2];
                if (checkInfo[1] == 0) {
                    if (board.player) { //first checks if rooks or kings moved, then if the square is occupied or under attack
                        if ((board.castleRights & 8L) == 8L && (MoveGeneration.wSFinal & (board.occupied | board.eAttackMask)) == 0) { //wShort castle
                            moveSquares |= 1L << 62;
                        }
                        if ((board.castleRights & 4L) == 4L && (MoveGeneration.wLFinal & board.occupied) == 0 && (MoveGeneration.wLMoveSquares & board.eAttackMask) == 0) { //wLong castle
                            moveSquares |= 1L << 58;
                        }
                    } else {
                        if ((board.castleRights & 2L) == 2L && (MoveGeneration.bSFinal & (board.occupied | board.eAttackMask)) == 0) { //bShort castle
                            moveSquares |= 1L << 6;
                        }
                        if ((board.castleRights & 1L) == 1L && (MoveGeneration.bLFinal & board.occupied) == 0 && (MoveGeneration.bLMoveSquares & board.eAttackMask) == 0) { //bLong castle
                            moveSquares |= 1L << 2;
                        }
                    }
                }
            }
            ArrayList<long[]> pin = MoveGeneration.getPinnedPieces(board.fKing, board.occupied, board.eRook, board.eBishop, board.eQueen);
            if ((1L << selectedPieceSquare & pin.get(pin.size() - 1)[0]) != 0) {
                for (long[] currentPin : pin) {
                    if ((currentPin[0] & selectedPieceSquare) != 0) {
                        moveSquares &= currentPin[1];
                        break;
                    }
                }
            }
        }
        this.repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        int x, y;
        if (flip) {
            y = height - e.getY();
            x = height - e.getX();
        } else {
            y = e.getY();
            x = e.getX();
        }
        if (promotionFlag != -1) {


            y = y % squareLength / (squareLength / 2);
            x = x % squareLength / (squareLength / 2);
            if (flip) {
                if (y == 0 & x == 0) {
                    promotionFlag = 2;
                } else if (y == 0 && x == 1) {
                    promotionFlag = 3;
                } else if (y == 1 && x == 0) {
                    promotionFlag = 1;
                } else if (y == 1 && x == 1) {
                    promotionFlag = 4;
                }
            } else {
                if (y == 0 & x == 0) {
                    promotionFlag = 4;
                } else if (y == 0 && x == 1) {
                    promotionFlag = 1;
                } else if (y == 1 && x == 0) {
                    promotionFlag = 3;
                } else if (y == 1 && x == 1) {
                    promotionFlag = 2;
                }
            }
        } else {
            selectedPieceSquare = y / squareLength * 8 + x / squareLength;
            if (board.player == Main.player && (1L<<selectedPieceSquare & board.fOccupied) != 0) {
                selectPiece();
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
