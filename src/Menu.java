import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Menu extends JFrame {
    private JPanel base;
    private JSlider scaleSlider;
    private JRadioButton NNUE;
    private JRadioButton CustomEval;
    private JRadioButton White;
    private JRadioButton Black;
    private JRadioButton WhitePerspective;
    private JRadioButton BlackPerspective;
    private JButton Start;
    private JLabel timeLabel;
    private JLabel scaleLabel;
    private JTextField thinkTime;

    public boolean startGame;
    public boolean nnueEnabled;
    public boolean player;
    public boolean flip;
    public int thinkTimeAmount;
    public double scale;

    public Menu() {
        base = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // NNUE and CustomEval RadioButtons
        NNUE = new JRadioButton("NNUE");
        NNUE.setSelected(true);
        CustomEval = new JRadioButton("Custom Evaluation");
        ButtonGroup evalGroup = new ButtonGroup();
        evalGroup.add(NNUE);
        evalGroup.add(CustomEval);

        gbc.gridx = 0;
        gbc.gridy = 0;
        base.add(NNUE, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        base.add(CustomEval, gbc);

        // White and Black RadioButtons
        White = new JRadioButton("White");
        White.setSelected(true);
        Black = new JRadioButton("Black");
        ButtonGroup sideGroup = new ButtonGroup();
        sideGroup.add(White);
        sideGroup.add(Black);

        gbc.gridx = 0;
        gbc.gridy = 2;
        base.add(White, gbc);

        gbc.gridx = 2;
        gbc.gridy = 2;
        base.add(Black, gbc);

        // WhitePerspective and BlackPerspective RadioButtons
        WhitePerspective = new JRadioButton("White's Perspective");
        WhitePerspective.setSelected(true);
        BlackPerspective = new JRadioButton("Black's Perspective");
        ButtonGroup flipGroup = new ButtonGroup();
        flipGroup.add(WhitePerspective);
        flipGroup.add(BlackPerspective);

        gbc.gridx = 0;
        gbc.gridy = 4;
        base.add(WhitePerspective, gbc);

        gbc.gridx = 2;
        gbc.gridy = 4;
        base.add(BlackPerspective, gbc);

        // Think Time Label and TextField
        timeLabel = new JLabel("Think Time (s)");
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 3;
        base.add(timeLabel, gbc);

        thinkTime = new JTextField("1", 10);
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 3;
        base.add(thinkTime, gbc);

        // Scale Label and Slider
        scaleLabel = new JLabel("GUI Scale");
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.gridwidth = 3;
        base.add(scaleLabel, gbc);

        scaleSlider = new JSlider(1, 5, 3);
        scaleSlider.setPaintTicks(true);
        scaleSlider.setPaintLabels(true);
        scaleSlider.setSnapToTicks(true);
        gbc.gridx = 0;
        gbc.gridy = 12;
        gbc.gridwidth = 3;
        base.add(scaleSlider, gbc);

        // Start Button
        Start = new JButton("Start Engine");
        gbc.gridx = 0;
        gbc.gridy = 14;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        base.add(Start, gbc);

        setContentPane(base);
        setTitle("Menu");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 400);
        setLocationRelativeTo(null);
        setVisible(true);

        // Initialize Fields
        startGame = false;
        nnueEnabled = true;
        player = true;
        flip = false;
        thinkTimeAmount = 1000;
        scale = 1;


        // Action Listeners
        Start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String time = thinkTime.getText();
                try {
                    thinkTimeAmount = (int) (Double.parseDouble(time) * 1000);
                    startGame = true;
                } catch (NumberFormatException ignored) {
                }
            }
        });

        NNUE.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nnueEnabled = true;
            }
        });

        CustomEval.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nnueEnabled = false;
            }
        });

        White.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                player = true;
            }
        });

        Black.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                player = false;
            }
        });

        WhitePerspective.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                flip = false;
            }
        });

        BlackPerspective.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                flip = true;
            }
        });

        scaleSlider.addChangeListener(e -> {
            int value = scaleSlider.getValue();
            switch (value) {
                case 1 -> scale = 0.6;
                case 2 -> scale = 0.8;
                case 3 -> scale = 1;
                case 4 -> scale = 1.2;
                case 5 -> scale = 1.4;
            }
        });
    }
}