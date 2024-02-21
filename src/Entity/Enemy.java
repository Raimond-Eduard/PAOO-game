package Entity;

import TileMap.TileMap;
public class Enemy extends MapObject{
    //statusuri de inamici - setate protected pentru ca le folosesc toti inamicii
    protected int health;
    protected int maxHealth;
    protected boolean dead;
    protected int damage;
    protected int scoreIncrement;
    protected boolean flinching;
    protected long flinchingTimer;
    //constructor
    public Enemy(TileMap tm){
        super(tm);
    }

    public boolean isDead() {return dead;}
    public int getDamage() {return damage;}

    public void hit(int damage){
        if(dead || flinching) return;
        health -= damage;
        if(health < 0) health = 0;
        if(health == 0) dead = true;
        flinching = true;
        flinchingTimer = System.nanoTime();
    }
    public int getScoreIncrement(){
        return scoreIncrement;
    }
    public void update(){}

}
