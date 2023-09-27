package gui;

import controller.GameController;
import elements.BoardPanel;
import elements.EvalBar;
import elements.SidePanel;

import javax.swing.*;
import java.awt.*;

public class GameWindow extends JFrame {


    BoardPanel boardPanel;

    SidePanel sidePanel;

    GameController controller;

    JMenuBar menuBar;

    EvalBar evalBar;


    public GameWindow() {
        configure();
        initComponents();
        controller = new GameController(boardPanel,sidePanel,evalBar,menuBar);
    }


    private void initComponents() {
        boardPanel = new BoardPanel();
        sidePanel = new SidePanel();
        menuBar = new JMenuBar();

        evalBar = new EvalBar();

        setJMenuBar(menuBar);
        add(boardPanel);
        add(sidePanel, BorderLayout.EAST);
        add(evalBar,BorderLayout.WEST);
        pack();
    }

    private void configure() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, you can set the GUI to another look and feel.
        }
    }


}