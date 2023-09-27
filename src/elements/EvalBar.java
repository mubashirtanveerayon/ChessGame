package elements;

import engine.Engine;

import javax.swing.*;
import java.awt.*;

public class EvalBar extends JPanel {


    Engine engine;

    public float eval;

    public boolean playerIsWhite;

    public void configure(){
        setPreferredSize(new Dimension(20,getHeight()));
    }

    public void setEngine(Engine engine){
        this.engine = engine;
    }

    @Override
    public void paint(Graphics g){
        Graphics2D g2D = (Graphics2D)g;
        if(!engine.isSearching()) {
            eval = engine.evaluate();
        }
        float barLength = map(eval,-1,1,0,getHeight());
        if(playerIsWhite){
            if(engine.cb.whiteToMove){
                int blackBarLength = getHeight()-(int)barLength;
                g2D.setColor(Color.BLACK);
                g2D.fillRect(0,0,20,blackBarLength);
                g2D.setColor(Color.WHITE);
                g2D.fillRect(0,blackBarLength,20,getHeight());
            }else{
                g2D.setColor(Color.BLACK);
                g2D.fillRect(0,0,20,(int)barLength);
                g2D.setColor(Color.WHITE);
                g2D.fillRect(0,(int)barLength,20,getHeight());
            }
        }else{
            if(engine.cb.whiteToMove){
                g2D.setColor(Color.WHITE);
                g2D.fillRect(0,0,20,(int)barLength);
                g2D.setColor(Color.BLACK);
                g2D.fillRect(0,(int)barLength,20,getHeight());
            }else{
                g2D.setColor(Color.WHITE);
                int whiteBarLength = getHeight()-(int)barLength;
                g2D.fillRect(0,0,20,whiteBarLength);
                g2D.setColor(Color.BLACK);
                g2D.fillRect(0,whiteBarLength,20,getHeight());
            }
        }


    }

    public static float map(float x, float in_min, float in_max, float out_min, float out_max)
    {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }



}
