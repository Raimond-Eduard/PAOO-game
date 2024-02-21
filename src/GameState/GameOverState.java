package GameState;

import DatabaseManager.DatabaseExceptions;
import TileMap.Background;

import java.awt.*;
import java.awt.event.KeyEvent;

public class GameOverState extends GameState{
    private Background bg;
    private Color titleColor;
    private Color promptColor;
    private Font font;
    private Font promptFont;
    public GameOverState(GameStateManager gsm){
        this.gsm = gsm;
        try{
            bg = new Background("/Backgrounds/gameover.png", 1);
            bg.setVector(0,0);

            titleColor = new Color(242, 245, 243);
            font = new Font("Times New Roman", Font.PLAIN, 48);
            promptColor = new Color(197,119,188);
            promptFont = new Font("Times New Roman", Font.PLAIN, 24);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void init() {}
    public void update(){
        bg.update();
    }
    public void draw(Graphics2D g){
        bg.draw(g);

        g.setColor(titleColor);
        g.setFont(font);
        g.drawString("GAME OVER", 490, 140);

        g.setColor(promptColor);
        g.setFont(promptFont);
        g.drawString("PRESS \"SPACEBAR\" TO RETURN TO MAIN MENU", 340, 600);
    }

    @Override
    public void keyPressed(int k) throws DatabaseExceptions {
        if(k == KeyEvent.VK_SPACE) gsm.setState(GameStateManager.MENUSTATE);
    }

    @Override
    public void keyReleased(int k) {

    }
}
