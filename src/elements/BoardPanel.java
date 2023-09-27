package elements;

import path.Paths;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import server.util.Constants;

public class BoardPanel extends JPanel {

    public JButton[][] buttons ;

//    JPanel filePanel,rankPanel;

    public BoardPanel(){
        super(new BorderLayout());
        configure();
    }

    private void configure() {
        buttons = new JButton[8][8];
        JPanel mainPanel = new JPanel(new GridLayout(8,8));
        for(int i=0;i<8;i++){
            for(int j=0;j<8;j++){
                buttons[i][j] = new JButton();
                if((i + j)%2 == 0){
                    buttons[i][j].setBackground(new Color(255,210,143));
                }else{
                    buttons[i][j].setBackground(new Color(180,89,23));
                }
                buttons[i][j].setFocusable(false);

                buttons[i][j].setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
                mainPanel.add(buttons[i][j]);
            }
        }
        mainPanel.setPreferredSize(new Dimension(550,450));
        add(mainPanel);
//
//
//        filePanel = new JPanel();
//        rankPanel = new JPanel();
//
//        FlowLayout rankLayout = new FlowLayout();
//        rankLayout.setVgap(35);
//
//        FlowLayout fileLayout = new FlowLayout();
//        fileLayout.setHgap(10);
//
//        rankPanel.setLayout(rankLayout);
//        filePanel.setLayout(fileLayout);
//
//        filePanel.setPreferredSize(new Dimension(getSize().width,20));
//        rankPanel.setPreferredSize(new Dimension(20,getSize().height));
//
////        filePanel.setBackground(Color.BLUE);
////        rankPanel.setBackground(Color.RED);
//
//
//        rankPanel.add(new JLabel("8"));
//        rankLayout.setVgap(35);
//        rankPanel.add(new JLabel("7"));
//        rankPanel.add(new JLabel("6"));
//        rankPanel.add(new JLabel("5"));
//        rankPanel.add(new JLabel("4"));
//        rankPanel.add(new JLabel("3"));
//        rankPanel.add(new JLabel("2"));
//        rankPanel.add(new JLabel("1"));
//
//        add(filePanel,BorderLayout.SOUTH);
//        add(rankPanel,BorderLayout.WEST);
//

    }

    public void registerListener(ActionListener listener){
        for(int i=0;i<8;i++){
            for(int j=0;j<8;j++){
                buttons[i][j].addActionListener(listener);
            }
        }
    }

    public void render(char[][] board,boolean flip){
        for(int i=0;i<8;i++){
            for(int j=0;j<8;j++){
                if((i + j)%2 == 0){
                    buttons[i][j].setBackground(new Color(255,210,143));
                }else{
                    buttons[i][j].setBackground(new Color(180,89,23));
                }
                buttons[j][i].setIcon(null);
                if(flip){
                    if((j+i)%2 == 0){
                        buttons[i][j].setBackground(new Color(255,210,143));
                    }else{
                        buttons[i][j].setBackground(new Color(180,89,23));
                    }
                    if(board[7-j][7-i] != Constants.EMPTY_SQUARE){
                        buttons[j][i].setIcon(new ImageIcon(Paths.getImagePath(board[7-j][7-i])));
                    }
                }else{
                    if(board[j][i] != Constants.EMPTY_SQUARE){
                        buttons[j][i].setIcon(new ImageIcon(Paths.getImagePath(board[j][i])));
                    }
                }
            }
        }
    }



}
