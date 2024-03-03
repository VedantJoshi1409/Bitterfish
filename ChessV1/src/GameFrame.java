import javax.swing.*;

public class GameFrame extends JFrame {
    JPanel boardPanel = new BoardPanel();
    GameFrame() {
        this.add(boardPanel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setLayout(null);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
}