package elements;

import controller.GameController;
import path.Paths;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class SidePanel extends JPanel {


    public JTextArea moveListArea;
    JPanel bottomPanel;
    public JButton nextButton, prevButton,startButton,endButton;

    public JRadioButton five,two,ten,inf,playAsW,playAsB;
    public JRadioButton[] depthButtons;

    public SidePanel(){
        initComponents();
        configure();
    }

    private void configure() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(350, 450));
    }
    private void initComponents() {
        moveListArea = new JTextArea();
        moveListArea.setEditable(false);
        moveListArea.setLineWrap(true);
        moveListArea.setToolTipText("Move list");

        JScrollPane scroll = new JScrollPane(moveListArea,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setPreferredSize(new Dimension(250,200));

        JPanel topPanel = new JPanel();
        topPanel.setPreferredSize(new Dimension(250,70));
        nextButton = new JButton(new ImageIcon(Paths.ASSET_PATH+"next.png"));
        prevButton = new JButton(new ImageIcon(Paths.ASSET_PATH+"previous.png"));
        startButton = new JButton(new ImageIcon(Paths.ASSET_PATH+"start.png"));
        endButton = new JButton(new ImageIcon(Paths.ASSET_PATH+"end.png"));

        nextButton.setToolTipText("Next move");
        prevButton.setToolTipText("Previous move");
        startButton.setToolTipText("First move");
        endButton.setToolTipText("Last move");

        nextButton.setFocusable(false);
        prevButton.setFocusable(false);
        startButton.setFocusable(false);
        endButton.setFocusable(false);


        playAsW = new JRadioButton();
        playAsW.setText("Play as white");
        playAsW.setSelected(true);
        playAsB = new JRadioButton();
        playAsB.setText("Play as black");
        ButtonGroup allianceBtnGroup = new ButtonGroup();
        allianceBtnGroup.add(playAsW);

        allianceBtnGroup.add(playAsB);

        topPanel.add(new JLabel("          "));
        topPanel.add(startButton);
        topPanel.add(prevButton);
        topPanel.add(nextButton);
        topPanel.add(endButton);
        topPanel.add(new JLabel("           "));
        topPanel.add(playAsW);
        topPanel.add(playAsB);

        bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(topPanel,BorderLayout.NORTH);
        bottomPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));

        JPanel depthPanel = new JPanel();
        depthPanel.setLayout(new BoxLayout(depthPanel,BoxLayout.Y_AXIS));
        JLabel depthTitle = new JLabel("                                       ");
        depthTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        depthPanel.setBorder(BorderFactory.createTitledBorder("Level"));
        depthPanel.setToolTipText("Increasing level/depth value enables the engine to make more accurate moves.However this is achieved at the cost of greatly increased thinking time.");

        depthButtons = new JRadioButton[5];
        ButtonGroup depthButtonGroup = new ButtonGroup();


        depthPanel.add(Box.createVerticalGlue());
        depthPanel.add(depthTitle);
        for(int i=0;i<5;i++){
            depthButtons[i] = new JRadioButton();
            depthButtons[i].setText(String.valueOf(i+1));
            depthButtons[i].setAlignmentX(Component.CENTER_ALIGNMENT);
            depthButtonGroup.add(depthButtons[i]);
            depthPanel.add(depthButtons[i]);
            depthButtons[i].setEnabled(false);
        }
        depthPanel.add(Box.createVerticalGlue());

        bottomPanel.add(depthPanel,BorderLayout.WEST);

        JPanel timePanel = new JPanel();
        timePanel.setLayout(new BoxLayout(timePanel,BoxLayout.Y_AXIS));
        timePanel.setBorder(BorderFactory.createRaisedBevelBorder());
        timePanel.setBorder(BorderFactory.createTitledBorder("Time"));
        timePanel.setToolTipText("The engine will make its move within the specified time, even if the search depth is not reached.");

        two = new JRadioButton("2 seconds");
        five = new JRadioButton("5 seconds");
        ten = new JRadioButton("10 seconds");
        inf = new JRadioButton("Full depth search");

//        five.setAlignmentX(Component.CENTER_ALIGNMENT);
//        seven.setAlignmentX(Component.CENTER_ALIGNMENT);
//        ten.setAlignmentX(Component.CENTER_ALIGNMENT);
//        inf.setAlignmentX(Component.CENTER_ALIGNMENT);

        ButtonGroup timeButtonGroup = new ButtonGroup();
        timeButtonGroup.add(two);
        timeButtonGroup.add(five);

        timeButtonGroup.add(ten);
        timeButtonGroup.add(inf);

        two.setSelected(true);

        timePanel.add(Box.createVerticalGlue());
        //timePanel.add(timeLabel);
        timePanel.add(two);
        timePanel.add(five);

        timePanel.add(ten);
        timePanel.add(inf);
        timePanel.add(Box.createVerticalGlue());

        bottomPanel.add(timePanel,BorderLayout.CENTER);


        add(Box.createVerticalGlue());
        add(scroll);

        add(bottomPanel);
        add(Box.createVerticalGlue());

    }

    public void toggleControl(boolean on){
        nextButton.setEnabled(on);
        prevButton.setEnabled(on);
        startButton.setEnabled(on);
        endButton.setEnabled(on);
        playAsW.setEnabled(on);
        playAsB.setEnabled(on);
        if(inf.isSelected()) {
            for (int i = 0; i < 5; i++) {
                depthButtons[i].setEnabled(on);
            }
        }
        five.setEnabled(on);
        two.setEnabled(on);
        ten.setEnabled(on);
        inf.setEnabled(on);
    }

    public void registerListener(ActionListener listener) {
        nextButton.addActionListener(listener);
        prevButton.addActionListener(listener);
        startButton.addActionListener(listener);
        endButton.addActionListener(listener);
        playAsW.addActionListener(listener);
        playAsB.addActionListener(listener);

        two.addActionListener(listener);
        five.addActionListener(listener);
        ten.addActionListener(listener);
        inf.addActionListener(listener);

    }
}
