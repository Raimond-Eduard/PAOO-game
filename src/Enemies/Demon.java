package Enemies;

import Entity.*;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.util.ArrayList;
import javax.imageio.*;
import TileMap.*;

public class Demon extends Enemy{
    private ArrayList<BufferedImage[]> sprites;
    private int xStart;
    private int xEnd;

    //animatiile inamicului
    private static final int WALKING = 0;
    private static final int CHARGE = 1;
    //nuamrul de cadre per animatie
    private final int numFrames = 5;

    //schimbarea dintre animatii
    private boolean charging;

    public Demon(TileMap tm){
        super(tm);

        moveSpeed = 0.4;
        maxSpeed = 1;
        fallSpeed = 0.2;
        maxFallSpeed = 10.0;

        width = 35;
        height = 40;
        cwidth = 25;
        cheight = 30;

        health = maxHealth = 2;
        damage = 2;
        scoreIncrement = 50;


        charging = false;

        //incarcare sprite-uri si animatii
        try{
            sprites = new ArrayList<BufferedImage[]>();
            for(int i = 0 ; i < 2; i++){
                BufferedImage[] bi = new BufferedImage[numFrames];
                switch(i){
                    case 0:
                        for(int j = 0; j < numFrames; j++){
                            bi[j] = ImageIO.read(new FileInputStream("Resources/Sprites/Enemies/Cacodaemon/Walk/w"+j+".png"));

                        }
                        sprites.add(bi);
                        break;
                    case 1:
                        for(int j = 0; j < numFrames; j++){
                            bi[j] = ImageIO.read(new FileInputStream("Resources/Sprites/Enemies/Cacodaemon/Charge/c"+j+".png"));
                        }
                        sprites.add(bi);
                        break;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        animation = new Animation();
        currentAction = WALKING;
        animation.setFrames(sprites.get(WALKING));
        animation.setDelay(50);

        left = true;
        facingRight = false;
    }
    private void getNextPosition() {

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
        //falling
        if(falling) {
            dy += fallSpeed;
        }

    }
    public void update(){
        //update pozitie
        getNextPosition();
        checkTileMapCollision();
        setPosition(xtemp, ytemp);

        //verificare flinch
        if(flinching){
            long elapsed = (System.nanoTime() - flinchingTimer) / 1000000;
            if(elapsed > 500){
                flinching = false;
            }
        }

        //daca loveste perete schimba directia
        if(right && dx == 0){
            right = false;
            left = true;
            facingRight = false;

        }else if(left && dx == 0){
            right = true;
            left = false;
            facingRight = true;
        }
        if(charging){
            if(currentAction != CHARGE){
                currentAction = CHARGE;
                animation.setFrames(sprites.get(CHARGE));
                animation.setDelay(100);
            }
        }else{
            if(currentAction != WALKING){
                currentAction = WALKING;
                animation.setFrames(sprites.get(WALKING));
                animation.setDelay(100);
            }
        }
//
        //update animatie
        animation.update();
    }

    public void draw(Graphics2D g){

        setMapPosition();


        super.draw(g);

    }
    public void setBoundries(int x1, int x2){
        xStart = x1;
        xEnd = x2;
    }

    public int getxStart(){return xStart;}
    public int getxEnd(){return xEnd;}

    public void enrage(){
        charging = true;
        maxSpeed = 2;
    }

    public void calm(){charging = false;
    maxSpeed = 1;}

}
