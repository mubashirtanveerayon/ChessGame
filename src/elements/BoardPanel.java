package elements;

import path.Paths;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import server.util.Constants;

public class BoardPanel extends JPanel {

    public JButton[][] buttons ;

    public BoardPanel(){
        super(new GridLayout(8,8));
        buttons = new JButton[8][8];
        for(int i=0;i<8;i++){
            for(int j=0;j<8;j++){
                buttons[i][j] = new JButton();
                if((i + j)%2 == 0){
                    buttons[i][j].setBackground(new Color(255,210,143));
                }else{
                    buttons[i][j].setBackground(new Color(180,89,23));
                }
                buttons[i][j].setFocusable(false);
                add(buttons[i][j]);
            }
        }
        setPreferredSize(new Dimension(550,450));
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
                buttons[j][i].setIcon(null);
                if(flip){
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
