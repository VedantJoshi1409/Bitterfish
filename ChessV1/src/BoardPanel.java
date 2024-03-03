import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class BoardPanel extends JPanel implements MouseListener {
    Image bP, bR, bN, bB, bQ, bK, wP, wR, wN, wB, wQ, wK;
    JLabel clickLabel = new JLabel();
    public static long possibleMoves;
    public static int clickedPiece = -1;
    public static int x,y;

    BoardPanel() {
        this.setPreferredSize(new Dimension(800, 800));
        this.setLayout(null);
        this.add(clickLabel);
        clickLabel.setBounds(0, 0, 800, 800);
        clickLabel.setOpaque(false);
        clickLabel.addMouseListener(this);
        bP = new ImageIcon("src/Images/bPawn.png").getImage();
        bR = new ImageIcon("src/Images/bRook.png").getImage();
        bN = new ImageIcon("src/Images/bKnight.png").getImage();
        bB = new ImageIcon("src/Images/bBishop.png").getImage();
        bQ = new ImageIcon("src/Images/bQueen.png").getImage();
        bK = new ImageIcon("src/Images/bKing.png").getImage();
        wP = new ImageIcon("src/Images/wPawn.png").getImage();
        wR = new ImageIcon("src/Images/wRook.png").getImage();
        wN = new ImageIcon("src/Images/wKnight.png").getImage();
        wB = new ImageIcon("src/Images/wBishop.png").getImage();
        wQ = new ImageIcon("src/Images/wQueen.png").getImage();
        wK = new ImageIcon("src/Images/wKing.png").getImage();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2D = (Graphics2D) g;
        boolean color;
        Color light = new Color(240, 218, 181);
        Color dark = new Color(181, 135, 99);
        Color lightRed = new Color(251, 103, 103);
        color = true;
        if (Main.flippedBoard) {
            for (int i = 0; i < 800; i += 100) {
                for (int j = 0; j < 800; j += 100) {
                    if (color) {
                        g2D.setPaint(light);
                    } else {
                        g2D.setPaint(dark);
                    }
                    color = !color;
                    g2D.fillRect(i, j, 100, 100);
                }
                color = !color;
            }
            for (int i = 0; i < 64; i++) {
                if (((possibleMoves >> i) & 1) == 1) {
                    g2D.setPaint(lightRed);
                    g2D.fillRect(700-(i % 8) * 100, 700-(i / 8) * 100, 100, 100);
                }
            }
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (!Main.board[j][i].equals(" ")) {
                        switch (Main.board[j][i]) {
                            case "p" -> g2D.drawImage(bP, 700-i * 100, 700-j * 100, 100, 100, null);
                            case "r" -> g2D.drawImage(bR, 700-i * 100, 700-j * 100, 100, 100, null);
                            case "n" -> g2D.drawImage(bN, 700-i * 100, 700-j * 100, 100, 100, null);
                            case "b" -> g2D.drawImage(bB, 700-i * 100, 700-j * 100, 100, 100, null);
                            case "q" -> g2D.drawImage(bQ, 700-i * 100, 700-j * 100, 100, 100, null);
                            case "k" -> g2D.drawImage(bK, 700-i * 100, 700-j * 100, 100, 100, null);
                            case "P" -> g2D.drawImage(wP, 700-i * 100, 700-j * 100, 100, 100, null);
                            case "R" -> g2D.drawImage(wR, 700-i * 100, 700-j * 100, 100, 100, null);
                            case "N" -> g2D.drawImage(wN, 700-i * 100, 700-j * 100, 100, 100, null);
                            case "B" -> g2D.drawImage(wB, 700-i * 100, 700-j * 100, 100, 100, null);
                            case "Q" -> g2D.drawImage(wQ, 700-i * 100, 700-j * 100, 100, 100, null);
                            case "K" -> g2D.drawImage(wK, 700-i * 100, 700-j * 100, 100, 100, null);
                        }
                    }
                }
            }
            if (Main.promotion != 0) {
                for (int i = 0; i < 64; i++) {
                    if (((Main.promotion>>i) & 1) == 1) {
                        x = 700-(i % 8) * 100;
                        y = 700-(i / 8) * 100;
                        g2D.setPaint(lightRed);
                        g2D.fillRect(x, y, 100, 100);
                        if (Main.player) {
                            g2D.drawImage(wQ, x,y,50,50,null);
                            g2D.drawImage(wR, x+50,y,50,50,null);
                            g2D.drawImage(wB, x,y+50,50,50,null);
                            g2D.drawImage(wN, x+50,y+50,50,50,null);
                        } else {
                            g2D.drawImage(bQ, x,y,50, 50,null);
                            g2D.drawImage(bR, x+50,y,50,50,null);
                            g2D.drawImage(bB, x,y+50,50,50,null);
                            g2D.drawImage(bN, x+50,y+50,50,50,null);
                        }
                        break;
                    }
                }
            }
        } else {
            for (int i = 0; i < 800; i += 100) {
                for (int j = 0; j < 800; j += 100) {
                    if (color) {
                        g2D.setPaint(light);
                    } else {
                        g2D.setPaint(dark);
                    }
                    color = !color;
                    g2D.fillRect(i, j, 100, 100);
                }
                color = !color;
            }
            for (int i = 0; i < 64; i++) {
                if (((possibleMoves >> i) & 1) == 1) {
                    g2D.setPaint(lightRed);
                    g2D.fillRect((i % 8) * 100, (i / 8) * 100, 100, 100);
                }
            }
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (!Main.board[j][i].equals(" ")) {
                        switch (Main.board[j][i]) {
                            case "p" -> g2D.drawImage(bP, i * 100, j * 100, 100, 100, null);
                            case "r" -> g2D.drawImage(bR, i * 100, j * 100, 100, 100, null);
                            case "n" -> g2D.drawImage(bN, i * 100, j * 100, 100, 100, null);
                            case "b" -> g2D.drawImage(bB, i * 100, j * 100, 100, 100, null);
                            case "q" -> g2D.drawImage(bQ, i * 100, j * 100, 100, 100, null);
                            case "k" -> g2D.drawImage(bK, i * 100, j * 100, 100, 100, null);
                            case "P" -> g2D.drawImage(wP, i * 100, j * 100, 100, 100, null);
                            case "R" -> g2D.drawImage(wR, i * 100, j * 100, 100, 100, null);
                            case "N" -> g2D.drawImage(wN, i * 100, j * 100, 100, 100, null);
                            case "B" -> g2D.drawImage(wB, i * 100, j * 100, 100, 100, null);
                            case "Q" -> g2D.drawImage(wQ, i * 100, j * 100, 100, 100, null);
                            case "K" -> g2D.drawImage(wK, i * 100, j * 100, 100, 100, null);
                        }
                    }
                }
            }
            if (Main.promotion != 0) {
                for (int i = 0; i < 64; i++) {
                    if (((Main.promotion>>i) & 1) == 1) {
                        x = (i % 8) * 100;
                        y = (i / 8) * 100;
                        g2D.setPaint(lightRed);
                        g2D.fillRect(x, y, 100, 100);
                        if (Main.player) {
                            g2D.drawImage(wQ, x,y,50,50,null);
                            g2D.drawImage(wR, x+50,y,50,50,null);
                            g2D.drawImage(wB, x,y+50,50,50,null);
                            g2D.drawImage(wN, x+50,y+50,50,50,null);
                        } else {
                            g2D.drawImage(bQ, x,y,50, 50,null);
                            g2D.drawImage(bR, x+50,y,50,50,null);
                            g2D.drawImage(bB, x,y+50,50,50,null);
                            g2D.drawImage(bN, x+50,y+50,50,50,null);
                        }
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        String piece;
        boolean player = Main.player;
        if (Main.flippedBoard) {
            y = 800-e.getY();
            x = 800-e.getX();
        } else {
            y = e.getY();
            x = e.getX();
        }
        if (Main.promotion != 0) {
            y = y%100/50;
            x = x%100/50;
            if (Main.flippedBoard) {
                if (y == 0 & x ==0) {
                    Main.promotion = 3;
                } else if (y == 0 && x == 1) {
                    Main.promotion = 1;
                } else if (y == 1 && x == 0) {
                    Main.promotion = 2;
                } else if (y == 1 && x == 1) {
                    Main.promotion = 0;
                }
            } else {
                if (y == 0 & x ==0) {
                    Main.promotion = 0;
                } else if (y == 0 && x == 1) {
                    Main.promotion = 2;
                } else if (y == 1 && x == 0) {
                    Main.promotion = 1;
                } else if (y == 1 && x == 1) {
                    Main.promotion = 3;
                }
            }
        } else {
            clickedPiece = y / 100 * 10 + x / 100;
            piece = Main.board[y / 100][x / 100];
            long blackPieces = Main.bPawn | Main.bRook | Main.bKnight | Main.bBishop | Main.bQueen | Main.bKing;
            long whitePieces = Main.wPawn | Main.wRook | Main.wKnight | Main.wBishop | Main.wQueen | Main.wKing;
            long pp, pr, pn, pb, pq, pk, op, or, on, ob, oq, ok, playerPieces, enemyPieces, checkSquares = -1L, movingPiece = 1L<<(y/100*8 + x/100%8), pin = -1;
            boolean shortMoved, longMoved;
            if (player) {
                shortMoved = Main.wSMoved;
                longMoved = Main.wLMoved;
                playerPieces = whitePieces;
                enemyPieces = blackPieces;
                pp = Main.wPawn;
                pr = Main.wRook;
                pn = Main.wKnight;
                pb = Main.wBishop;
                pq = Main.wQueen;
                pk = Main.wKing;
                op = Main.bPawn;
                or = Main.bRook;
                on = Main.bKnight;
                ob = Main.bBishop;
                oq = Main.bQueen;
                ok = Main.bKing;
            } else {
                shortMoved = Main.bSMoved;
                longMoved = Main.bLMoved;
                playerPieces = blackPieces;
                enemyPieces = whitePieces;
                pp = Main.bPawn;
                pr = Main.bRook;
                pn = Main.bKnight;
                pb = Main.bBishop;
                pq = Main.bQueen;
                pk = Main.bKing;
                op = Main.wPawn;
                or = Main.wRook;
                on = Main.wKnight;
                ob = Main.wBishop;
                oq = Main.wQueen;
                ok = Main.wKing;
            }
            long occupied = whitePieces | blackPieces;
            ArrayList<Long>[] pinned = MoveGeneration.pinnedSquares(pk, or, ob, oq, enemyPieces, playerPieces);
            ArrayList<Long> checkingSquares;
            if (MoveGeneration.inCheck(pk, occupied, op, or, on, ob, oq, ok, player)) {
                checkingSquares = MoveGeneration.checkSquares(pk, occupied, playerPieces, op, or, on, ob, oq, enemyPieces, player);
                if (checkingSquares.size() > 1) {
                    checkSquares = 0;
                } else {
                    checkSquares = checkingSquares.get(0);
                }
            }
            if (pinned[0].contains(movingPiece)) {
                pin = pinned[1].get(pinned[0].indexOf(movingPiece));
            }
            checkSquares &= pin;
            switch (piece.toLowerCase()) {
                case "p" ->
                        possibleMoves = MoveGeneration.pawnMoves(movingPiece, playerPieces, enemyPieces, player, Main.previousDoublePawnPush) & checkSquares;
                case "n" ->
                        possibleMoves = MoveGeneration.knightMoves(movingPiece, playerPieces) & checkSquares;
                case "r" ->
                        possibleMoves = MoveGeneration.rookMoves(movingPiece, occupied, playerPieces) & checkSquares;
                case "b" ->
                        possibleMoves = MoveGeneration.bishopMoves(movingPiece, occupied, playerPieces) & checkSquares;
                case "q" ->
                        possibleMoves = MoveGeneration.queenMoves(movingPiece, occupied, playerPieces) & checkSquares;
                case "k" ->
                        possibleMoves = MoveGeneration.kingMoves(movingPiece, playerPieces, op, or, on, ob, oq, ok, pr, shortMoved, longMoved, player);
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

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