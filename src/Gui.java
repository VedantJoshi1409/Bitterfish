import javax.swing.*;

public class Gui extends JFrame {
    Panel panel;

    Gui(Board board, double scale, boolean flip) {
        panel = new Panel(board, scale, flip);
        this.add(panel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setLayout(null);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
}
