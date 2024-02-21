package GameState;

import java.awt.*;

import java.awt.event.KeyEvent;
import java.sql.SQLException;

import DatabaseManager.DatabaseExceptions;
import DatabaseManager.DatabaseManager;
import TileMap.Background;
public class MenuState extends GameState {

    private DatabaseManager dbm;
    private Background bg;
    private int currentChoice = 0;

    private String[] options = {
            "New Game",
            "Load Game",
            "Quit"
    };

    private Color titleColor;
    private Font titleFont;
    private Font font;
    private int state;
    public  MenuState(GameStateManager gsm) throws SQLException {
        this.gsm = gsm;

        try {

            bg = new Background("/Backgrounds/menubg.png",1);
            bg.setVector(0,0);

            titleColor = new Color(166,28,60);
            titleFont = new Font("Times New Roman", Font.PLAIN, 48);

            font = new Font("Arial", Font.PLAIN, 36);
            dbm = new DatabaseManager();
            state = dbm.loadState();

        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

    public void init() {}
    public void update() {
        bg.update();
    }
    public void draw(Graphics2D g){

        //desenare fundal
        bg.draw(g);

        //desenare titlu joc
        g.setColor(titleColor);
        g.setFont(titleFont);
        g.drawString("Claurentiu - Aventura", 490, 140);

        //desenare optiuni meniu

        g.setFont(font);
        for(int i = 0 ; i < options.length; i++){
            if(i == currentChoice){
                g.setColor(new Color(93,183,222));
            }else{
                g.setColor(new Color(42,45,52));
            }
            g.drawString(options[i], 540, 240 + i * 30);
        }
    }
    private void select() throws DatabaseExceptions {
        if (currentChoice == 0) {
            //New Game
            gsm.setState(GameStateManager.LEVEL1State);

        }
        if(currentChoice == 1){
            //Load Game - verifica ce state se afla in baza de date
            switch(state){//Fiecarui state care nu este meniul, i s-a atribuit un numar, fiecare numar gasit va incarca un state
                case 0:
                    gsm.setState(GameStateManager.GAMEOVERSTATE);
                    break;
                case 1:
                    gsm.setState(GameStateManager.LEVEL1State);
                    break;
                case 2:
                    gsm.setState(GameStateManager.LEVEL2State);
                    break;
                case 3:
                    gsm.setState(GameStateManager.LEVEL3State);
                    break;
                case 4:
                    gsm.setState(GameStateManager.WINSTATE);
                    break;
            }


        }
        if(currentChoice == 2){
            //exit
            System.exit(0);//ultima optiune inchide jocul
        }
    }
    public void keyPressed(int k) throws DatabaseExceptions {//Aici se creeaza efectul de selectie meniu
        if(k == KeyEvent.VK_ENTER){
            select();
        }
        if(k == KeyEvent.VK_UP){
            currentChoice--;
            if(currentChoice == -1){
                currentChoice = options.length - 1;
            }

        }
        if(k == KeyEvent.VK_DOWN){
            currentChoice++;
            if(currentChoice == options.length){
                currentChoice = 0;
            }
        }
    }
    public void keyReleased(int k){}

}
