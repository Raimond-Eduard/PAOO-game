package GameState;

import DatabaseManager.*;
import TileMap.Background;

import java.awt.*;
import java.awt.event.KeyEvent;

public class WinState extends GameState{
    private Background bg;
    private Color titleColor, promptColor, scoreColor;
    private Font titleFont,promptFont,scoreFont;
    private DatabaseManager dbm;

    public WinState(GameStateManager gsm){
        this.gsm = gsm;
        try{
            dbm = new DatabaseManager();
            bg = new Background("/Backgrounds/youWin.png", 1);
            bg.setVector(0,0);

            titleColor = new Color(213,242,227);
            titleFont = new Font("Times New Roman", Font.PLAIN, 48);

            scoreColor = new Color(213,242,227);
            scoreFont = new Font("Arial", Font.PLAIN, 32);

            promptColor = new Color(213,242,227);
            promptFont = new Font("Helvetica", Font.PLAIN, 32);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void init(){}
    public void update(){
        bg.update();
    }

    public void draw(Graphics2D g){
        bg.draw(g);


        g.setColor(titleColor);
        g.setFont(titleFont);
        g.drawString("YOU WIN", 490,140);

        g.setColor(scoreColor);
        g.setFont(scoreFont);
        g.drawString("Your score: "+dbm.finalScore(),490,500);

        g.setColor(promptColor);
        g.setFont(promptFont);
        g.drawString("Press \"SPACEBAR\" to return to main menu",340,600);
    }

    @Override
    public void keyPressed(int k) throws DatabaseExceptions {
        if(k == KeyEvent.VK_SPACE) gsm.setState((GameStateManager.MENUSTATE));
    }
    @Override
    public void keyReleased(int k){}

}