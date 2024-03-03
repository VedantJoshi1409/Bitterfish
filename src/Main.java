import java.util.Scanner;

public class Main {
    static boolean player = true;

    public static void main(String[] args) {
        init();
        //Board board = new Board(PosConstants.startPos);
        //Gui gui = new Gui(board, 1.2, true);
        //engineTest(board, gui, 500);
        //play(board, gui, 1000, player);
        Client client = new Client(1409);
        client.initMatch();
    }

    static void play(Board board, Gui gui, int timeLimit, boolean player) {
        while (MoveGeneration.getMoves(board).count > 0 && Repetition.getRepetitionAmount(board.zobristKey, Repetition.historyFlag) < 3) {
            System.out.println(board.boardToFen());
            if (board.player == player) {
                board = PlayerGame.playerMove(board, gui);
                System.out.println();

            } else {
                board = Engine.engineMove(board, timeLimit);
            }

            Repetition.addToHistory(board.zobristKey, Repetition.historyFlag);

            gui.panel.board = board;
            gui.repaint();
        }
        if (board.player) {
            System.out.printf("%.2f\n", Evaluation.evaluation(board)/100);
        } else {
            System.out.printf("%.2f\n", -Evaluation.evaluation(board)/100);
        }
        if (Repetition.getRepetitionAmount(board.zobristKey, Repetition.historyFlag) >= 3) {
            System.out.println("Draw by repetition!");
        } else {
            if ((board.fKing & board.eAttackMask) != 0) {
                System.out.println("Checkmate!");
            } else{
                System.out.println("Stalemate!");
            }
        }
    }

    static void engineTest(Board board, Gui gui, int timeLimit) {
        //Scanner sc = new Scanner(System.in);
        while (MoveGeneration.getMoves(board).count > 0 && Repetition.getRepetitionAmount(board.zobristKey, Repetition.historyFlag) < 3) {
            System.out.println(board.boardToFen());
            board = Engine.engineMove(board, timeLimit);
            Repetition.addToHistory(board.zobristKey, Repetition.historyFlag);

            gui.panel.board = board;
            gui.repaint();
            System.out.println();
            //sc.nextLine();
        }
        if (board.player) {
            System.out.printf("%.2f\n", Evaluation.evaluation(board)/100);
        } else {
            System.out.printf("%.2f\n", -Evaluation.evaluation(board)/100);
        }
        if (Repetition.getRepetitionAmount(board.zobristKey, Repetition.historyFlag) >= 3) {
            System.out.println("Draw by repetition!");
        } else {
            if ((board.fKing & board.eAttackMask) != 0) {
                System.out.println("Checkmate!");
            } else{
                System.out.println("Stalemate!");
            }
        }
    }

    static void init() {
        MoveGeneration.initAttack();
        Zobrist.initKeys();
    }

    private static void speedTest(Board board, int repetitions) {
        long start = System.currentTimeMillis();
        for (int i = 0; i < repetitions; i++) {
            MoveGeneration.getPinnedPieces(board.fKing, board.occupied, board.eRook, board.eBishop, board.eQueen);
        }
        long end = System.currentTimeMillis();
        System.out.println(end-start);
    }
}
