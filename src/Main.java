import NNUEBridge.NNUEBridge;
import TB.Tablebase;

import java.util.Scanner;

public class Main {
    static String bigNet = "nn-b1a57edbea57.nnue";
    static String smallNet = "nn-baff1ede1f90.nnue";
    static String tbPath = "dependencies/tablebase";
    static boolean flip;
    static boolean player;
    static boolean tablebase;
    static boolean nnue;
    static int timeLimit;
    static boolean uci = false;

    public static void main(String[] args) {
//        nnue = true;
//        preInit();
//        postInit();
//
//        Board b = new Board("6k1/1p3rp1/p1n1Q2p/8/4p3/P4qP1/1PP2P2/1K6 w - - 0 36");
//        Engine.engineMove(8, b);

//        Board board = new Board("4K3/4P3/3q4/8/8/8/6k1/8 b - -");
//        Board board = new Board("rnbqkbnr/pppppppp/8/8/2P5/8/PP1PPPPP/RNBQKBNR b KQkq c3 0 1");
//        Engine.engineMove(board, 1000);
//        board = new Board("r1bqkbnr/pppppppp/2n5/8/2PP4/8/PP2PPPP/RNBQKBNR b KQkq d3 0 2");
//        SearchNode node = Engine.getSearchNodes(board, 3);
//        node.flag = SearchNode.StartNode;
//        TreeGUI.displayTree(node);

        Scanner sc = new Scanner(System.in);
        boolean firstLoop = true;

        while (true) {
            String lineIn = sc.nextLine();

            if (lineIn.equals("uci")) {
                uci = true;
                nnue = true;
                if (firstLoop) {
                    preInit();
                    firstLoop = false;
                }
                System.out.println("id name Bitterfish");
                System.out.println("id author Vedant Joshi");
                System.out.println();
                System.out.println("option name NNUE Evaluation type check default true");
                System.out.println("option name Clear Tables type button");
                System.out.println("option name Tablebase Path type string default dependencies/tablebase");
                System.out.println();
                System.out.println("uciok");
                UCI uci = new UCI();
                uci.loop();
            } else if (lineIn.equals("gui")) {
                if (firstLoop) {
                    preInit();
                    firstLoop = false;
                }

                Menu menu = new Menu();
                while (!menu.startGame) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                    }
                }
                menu.dispose();
                nnue = menu.nnueEnabled;
                player = menu.player;
                flip = menu.flip;
                timeLimit = menu.thinkTimeAmount;


                preInit();
                Board board = new Board(PosConstants.startPos);
                Gui gui = new Gui(board, menu.scale, flip);
                play(board, gui, timeLimit, player);
                gui.dispose();
                Repetition.clearTables();
                TTable.clearTables();

            } else if (lineIn.equals("test")) {
                nnue=true;
                preInit();
                Board board = new Board(PosConstants.startPos);
                Gui gui = new Gui(board, 1, false);

                while (true) {
                    engineTest(board, gui, 3000);
                    board = new Board(PosConstants.startPos);
                    System.out.println(TTable.hashfull());
                }
            }
        }
        /*Board board = new Board(PosConstants.startPos);
        Gui gui = new Gui(board, 1, false);

        while (true) {
            board = new Board(PosConstants.startPos);

            engineTest(board, gui, 50);
//            Engine.engineMove(6, board);

        }*/

//        Client client = new Client(1409);
//        client.initMatch();
    }

    static void play(Board board, Gui gui, int timeLimit, boolean player) {
        while (MoveGeneration.getMoves(board).count > 0 && Repetition.getRepetitionAmount(board.zobristKey, Repetition.historyFlag) < 3 && board.halfMoveClock < 100) {
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

        System.out.println(board.boardToFen());
        if (Repetition.getRepetitionAmount(board.zobristKey, Repetition.historyFlag) >= 3) {
            System.out.println("Draw by repetition!");

        } else if (board.halfMoveClock >= 100) {
            MoveList moveList = MoveGeneration.getMoves(board);
            if ((board.fKing & board.eAttackMask) != 0 && moveList.count == 0) {
                System.out.println("Checkmate!");
            } else {
                System.out.println("Draw by 50 move rule!");
            }

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
//            System.out.println(board.boardToFen());
            board = Engine.engineMove(board, timeLimit);
            Repetition.addToHistory(board.zobristKey, Repetition.historyFlag);

            gui.panel.board = board;
            gui.repaint();
//            System.out.println();
            //sc.nextInt();
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

    static void preInit() {
        if (nnue) {
            NNUEBridge.init(bigNet, smallNet);
        }
        MoveGeneration.initAttack();
        Zobrist.initKeys();
        TTable.init(64000000);
    }

    static void postInit() {
        Tablebase.init(tbPath);
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
