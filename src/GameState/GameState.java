package GameState;

import DatabaseManager.DatabaseExceptions;

public abstract class GameState {//clasa abstracta GameState

    protected GameStateManager gsm;

    public abstract void init() throws DatabaseExceptions;
    public abstract void update() throws DatabaseExceptions;
    public abstract void draw(java.awt.Graphics2D g);
    public abstract void keyPressed(int k) throws DatabaseExceptions;
    public abstract void keyReleased(int k);


}
