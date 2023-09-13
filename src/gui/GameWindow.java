package gui;

import controller.GameController;
import elements.BoardPanel;
import elements.SidePanel;

import javax.swing.*;
import java.awt.*;

public class GameWindow extends JFrame {


    BoardPanel boardPanel;

    SidePanel sidePanel;

    GameController controller;

    JMenuBar menuBar;


    public GameWindow() {
        configure();
        initComponents();
        controller = new GameController(boardPanel,sidePanel,menuBar);
    }


    private void initComponents() {
        boardPanel = new BoardPanel();
        sidePanel = new SidePanel();
        menuBar = new JMenuBar();

        setJMenuBar(menuBar);
        add(boardPanel);
        add(sidePanel, BorderLayout.EAST);
        pack();
    }

    private void configure() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


}