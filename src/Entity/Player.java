package Entity;

import DatabaseManager.Subject;
import DatabaseManager.Observer;
import TileMap.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectStreamClass;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;



public class Player extends MapObject implements Subject {
    //chestii de jucator

    private int health;
    private int maxHealth;
    private int score;
    //observer-ul
    private ArrayList<Observer> observers;
    //starile jucatorului
    private boolean dead;
    private boolean flinching;
    private long flinchTimer;



    //attack
    private boolean attack;
    private int attackDamage;
    private int attackRange;


    //animatii
    private ArrayList<BufferedImage[]> sprites;
    private final int[] numFrames = {
            21, 21, 11, 11, 13//, 31
    };


    //actiunile animatiilor
    private static final int IDLE = 0 ;
    private static final int WALKING = 1;
    private static final int JUMPING = 2;
    private static final int FALLING = 3;


    private static final int ATTACK = 4;


    public Player(TileMap tm){
        super(tm);
        //initializarea observatorilor
        observers = new ArrayList<>();

        //inaltimea si latimea jucatorului
        width = 35;
        height = 40;
        cwidth = 25;
        cheight = 30;

        //alte statusuri ale jucatorului
        moveSpeed = 0.5;
        maxSpeed = 1.6;
        stopSpeed = 0.4;
        fallSpeed = 0.15;
        maxFallSpeed = 4.0;
        jumpStart = -4.8;
        stopJumpSpeed = 0.3;
        facingRight = true;

        health = maxHealth = 5;
        score = 0;

        attackDamage = 2;
        attackRange = 50;

        //incarcare sprite-uri
        try{
            sprites = new ArrayList<BufferedImage[]>();
            for(int i = 0; i < 5; i++){
                BufferedImage[] bi = new BufferedImage[numFrames[i]];
                switch(i) {
                    case 0:
                        for(int j = 0 ; j < numFrames[i]; j++ ){
                            bi[j] = ImageIO.read(new FileInputStream("Resources/Sprites/Player/Idle/i"+j+".png"));
                        }
                        sprites.add(bi);
                        break;
                    case 1:
                        for(int j = 0 ; j < numFrames[i]; j++){
                            bi[j] = ImageIO.read(new FileInputStream("Resources/Sprites/Player/Run/r"+j+".png"));
                        }
                        sprites.add(bi);
                        break;
                    case 2:
                        for(int j = 0 ; j < numFrames[i]; j++){
                            bi[j] = ImageIO.read(new FileInputStream("Resources/Sprites/Player/Jump/j"+j+".png"));
                        }
                        sprites.add(bi);
                        break;
                    case 3:
                        for(int j = 0 ; j < numFrames[i]; j++){
                            bi[j] = ImageIO.read(new FileInputStream("Resources/Sprites/Player/Fall/f"+j+".png"));
                        }
                        sprites.add(bi);
                        break;
                    case 4:
                        for(int j = 0 ; j < numFrames[i]; j++){
                            bi[j] = ImageIO.read(new FileInputStream("Resources/Sprites/Player/Attack/a"+j+".png"));
                        }
                        sprites.add(bi);
                        break;
                }
            }
        }catch (Exception e){
            e.printStackTrace();

        }
        //setare animatii
        animation = new Animation();
        currentAction = IDLE;
        animation.setFrames(sprites.get(IDLE));
        animation.setDelay(400);
    }
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }

    public boolean isDead(){ return dead;}

    public void setAttack() {
        attack = true;
    }

    public void checkAttack(ArrayList<Enemy> enemies) {
        //loop prin vectorul de inamici
        for (int i = 0; i < enemies.size(); i++) {
            Enemy e = enemies.get(i);
            //verifica atacul
            if (attack) {
                if (facingRight) {
                    if (e.getx() > x &&
                            e.getx() < x + attackRange &&
                            e.gety() > y - height / 2 &&
                            e.gety() < y + height / 2)
                        e.hit(attackDamage);
                } else {
                    if (e.getx() < x &&
                            e.getx() > x - attackRange &&
                            e.gety() > y - height / 2 &&
                            e.gety() < y + height / 2) {
                        e.hit(attackDamage);
                    }
                }
            }
            //verifica coliziunea cu inamicii
            if(interescts(e)){
                hit(e.getDamage());
            }
        }
    }

    //invincibil pentru vreo cateva secunde dupa ce este lovit
    public void hit(int damage){
        if(flinching) return;
        health -= damage;
        if(health < 0) health = 0;
        if(health == 0) dead = true;
        flinching = true;
        flinchTimer = System.nanoTime();
    }

    private void getNextPosition(){
        //miscare intr-un sistem cartezian obisnuit
        if(left){
            dx -= moveSpeed;
            if(dx < -maxSpeed){
                dx = -maxSpeed;
            }
        }
        else if(right) {
            dx += moveSpeed;
            if(dx > maxSpeed){
                dx = maxSpeed;
            }
        }
        else{
            if(dx > 0){
                dx -= stopSpeed;
                if(dx < 0){
                    dx = 0;
                }
            }
            else if(dx < 0){
                dx += stopSpeed;
                if(dx > 0){
                    dx = 0;
                }
            }
        }
        //nu poate ataca cat se misca, mai putin in aer
        if((currentAction == ATTACK) && !(jumping || falling)) {
            dx = 0;
        }

        //seteaza incrementul vertical pentru saritura si pregateste animatia de cadere
        if(jumping && !falling){
            dy = jumpStart;
            falling = true;
        }
        //in cazul caderii caderea pe axa y este incrementata pana la viteza maxima de cadere
        if(falling){
            dy += fallSpeed;
        }
        if(dy > 0 ) jumping = false;
        if(dy < 0 && !jumping) dy += stopJumpSpeed;

        if(dy > maxFallSpeed ) dy = maxFallSpeed;
    }
    public void update() {
        //update la pozitia jucatorului
        getNextPosition();
        checkTileMapCollision();
        setPosition(xtemp, ytemp);

        //verifica terminarea atacului, neefectuarea acestei verificari aduce repetarea infinita a atacului
        if(currentAction == ATTACK){
            if(animation.hasPlayedOnce())
                attack = false;
        }
        //verificare final de "flinch"
        if(flinching){
            long elapsed = (System.nanoTime() - flinchTimer)/1000000;
            if(elapsed > 1000){
                flinching = false;
            }
        }


        //setarea animatiilor in functie de actiune
        if(attack){
            if(currentAction != ATTACK){
                currentAction = ATTACK;
                animation.setFrames(sprites.get(ATTACK));
                animation.setDelay(10);
                width = 60;
            }
        }else if(dy > 0 ) {
            if (currentAction != FALLING){
                currentAction = FALLING;
                animation.setFrames(sprites.get(FALLING));
                animation.setDelay(100);
                width = 35;
            }
        }
        else if(dy < 0){
            if(currentAction != JUMPING){
                currentAction = JUMPING;
                animation.setFrames(sprites.get(JUMPING));
                animation.setDelay(-1);
                width = 35;
            }
        }
        else if(left || right){
            if(currentAction != WALKING) {
                currentAction = WALKING;
                animation.setFrames(sprites.get(WALKING));
                animation.setDelay(40);
                width = 35;
            }
        }else{
            if(currentAction != IDLE){
                currentAction = IDLE;
                animation.setFrames(sprites.get(IDLE));
                animation.setDelay(10);
                width = 35;
            }
        }


        animation.update();

        //setare directie
        if(currentAction != ATTACK){
            if(right) facingRight = true;
            if(left) facingRight = false;
        }

        //Verificare status viata
        if(health > maxHealth){
            health = maxHealth;
        }

    }
    public void draw(Graphics2D g){
        setMapPosition();
        //desenare jucator
        if(flinching){
            long elapsed = (System.nanoTime() - flinchTimer)/1000000;
            if(elapsed / 100 % 2 ==0){
                return;
            }
        }
       super.draw(g);
    }
    public void kill(){
        dead = true;
    }
    public int getScore(){
        return score;
    }
    public String getScoreString(){
        String scoreString = Integer.toString(getScore());
        return scoreString;
    }
    public void setScore(int increment){
        this.score += increment;
        notifyObservers();
    }
    public void heal(int value){
        this.health +=value;
    }

    public void loadHealth(int health){
        this.health = health;
    }

    public void loadScore(int score){
        this.score = score;
    }

    //functiile de observator
    @Override
    public void addObserver(Observer observer){
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer){
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(){
        for(Observer observer : observers){
            observer.update(this);
        }
    }




}
