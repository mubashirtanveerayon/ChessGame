package elements;

import path.Paths;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PromotionChooser extends JFrame{
    JPanel mainPanel;

    public JButton qButton,rButton,nButton,bButton;
    public PromotionChooser(){
        FlowLayout layout = new FlowLayout();
        layout.setHgap(20);
        mainPanel = new JPanel();
        mainPanel.setLayout(layout);
        mainPanel.setPreferredSize(new Dimension(500,100));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        initComponents();
    }

    public void registerListener(ActionListener listener) {
        qButton.addActionListener(listener);
        nButton.addActionListener(listener);
        bButton.addActionListener(listener);
        rButton.addActionListener(listener);
    }

    public void show(boolean playerIsWhite){
        if(playerIsWhite){
            qButton.setIcon(new ImageIcon(Paths.ASSET_PATH+"Q.png"));
            nButton.setIcon(new ImageIcon(Paths.ASSET_PATH+"N.png"));
            bButton.setIcon(new ImageIcon(Paths.ASSET_PATH+"B.png"));
            rButton.setIcon(new ImageIcon(Paths.ASSET_PATH+"R.png"));
        }else{
            qButton.setIcon(new ImageIcon(Paths.ASSET_PATH+"q.png"));
            nButton.setIcon(new ImageIcon(Paths.ASSET_PATH+"n.png"));
            bButton.setIcon(new ImageIcon(Paths.ASSET_PATH+"b.png"));
            rButton.setIcon(new ImageIcon(Paths.ASSET_PATH+"r.png"));
        }

        super.show();

    }

    private void initComponents() {
        qButton = new JButton();
        nButton = new JButton();
        bButton = new JButton();
        rButton = new JButton();

        qButton.setFocusable(false);
        nButton.setFocusable(false);
        bButton.setFocusable(false);
        rButton.setFocusable(false);

        mainPanel.add(qButton);
        mainPanel.add(rButton);
        mainPanel.add(nButton);
        mainPanel.add(bButton);

        add(mainPanel);
        pack();
    }


}
