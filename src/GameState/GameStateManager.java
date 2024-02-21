package GameState;


import DatabaseManager.DatabaseExceptions;

import java.sql.SQLException;
import java.util.ArrayList;


public class GameStateManager {//Aici s-a utilizat state pattern, diferit de cel dat la curs
    private ArrayList<GameState> gameStates;
    private int currentState;
    //definirea celor 6 state-uri
    public static final int MENUSTATE = 0;

    public static final int LEVEL1State = 1;
    public static final int GAMEOVERSTATE = 2;

    public static final int LEVEL2State = 3;
    public static final int LEVEL3State = 4;
    public static final int WINSTATE = 5;


    public GameStateManager() throws DatabaseExceptions, SQLException {//Se incarca in constructor taote cele 6 state-uri
        gameStates = new ArrayList<GameState>();
        currentState = MENUSTATE;//Se seteaza starea initiala ca fiind MENUSTATE
        gameStates.add(new MenuState(this));
        gameStates.add(new Level1State(this));
        gameStates.add(new GameOverState(this));
        gameStates.add(new Level2State(this));
        gameStates.add(new Level3State(this));
        gameStates.add(new WinState(this));


    }
    public void setState(int state) throws DatabaseExceptions {//pentru setarea de state-uri sau trecerea de la un state la altul
        currentState = state;
        gameStates.get(currentState).init();
    }

    public void update() throws DatabaseExceptions {
        gameStates.get(currentState).update();
    }
    public void draw(java.awt.Graphics2D g){
        gameStates.get(currentState).draw(g);
    }
    public void keyPressed(int k) throws DatabaseExceptions {
        gameStates.get(currentState).keyPressed(k);
    }
    public void keyReleased(int k){
        gameStates.get(currentState).keyReleased(k);
    }
    public int getState(){return this.currentState;}
}
