public class PlayerGame {
    public static Board playerMove(Board board, Gui gui) {
        long move = getMove(board, gui);
        Board nextBoard = new Board(board);
        nextBoard.makeMove(move);
        return nextBoard;
    }

    private static long getMove(Board board, Gui gui) {
        MoveList moveList = MoveGeneration.getMoves(board);
        int startSquare = -1, endSquare;

        while (true) {
            gui.panel.selectedPieceSquare = -1;
            while (gui.panel.selectedPieceSquare == -1) { //get click
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                }
            }

            if (startSquare == -1) {
                if ((1L<<gui.panel.selectedPieceSquare & board.fOccupied) != 0) {
                    startSquare = gui.panel.selectedPieceSquare;
                }
            } else {
                endSquare = gui.panel.selectedPieceSquare;

                for (int i = 0; i < moveList.count; i++) {
                    if (startSquare == MoveList.getStartSquare(moveList.moves[i]) && endSquare == MoveList.getEndSquare(moveList.moves[i])) {
                        int promotion = MoveList.getPromotePiece(moveList.moves[i]);
                        if (promotion == 0) {
                            return moveList.moves[i];
                        } else {
                            gui.panel.promotion();
                            while (gui.panel.promotionFlag == 0) { //get click
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException ignored) {
                                }
                            }
                            int promotionFlag = gui.panel.promotionFlag;
                            gui.panel.promotionFlag = -1;
                            return moveList.moves[i+promotionFlag-1];
                        }
                    }
                }
                startSquare = endSquare;
            }
        }
    }
}
