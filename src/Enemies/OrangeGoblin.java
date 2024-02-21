package Enemies;

import Entity.Animation;
import Entity.Enemy;
import TileMap.TileMap;

import javax.imageio.ImageIO;
import java.awt.image.*;
import java.io.FileInputStream;
import java.awt.Graphics2D;

public class OrangeGoblin extends Enemy {
    private BufferedImage[] sprites;
    public OrangeGoblin(TileMap tm){
        super(tm);
        //setare atribute
        moveSpeed = 0.3;
        maxSpeed = 0.3;
        fallSpeed = 0.2;
        maxFallSpeed = 10.0;

        //setare inaltime si cutia de coliziune
        width = 20;
        height = 20;
        cwidth = 15;
        cheight = 15;

        health = maxHealth = 2;
        damage = 1;
        scoreIncrement = 10;

        //incarcarea sprite-urilor si a animatiilor
        try{
            sprites = new BufferedImage[43];
            for(int i = 0 ; i < sprites.length; i++ ){
                sprites[i] = ImageIO.read(new FileInputStream("Resources/Sprites/Enemies/OrangeGoblin/og_"+i+".png"));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        //setarea animatiei
        animation = new Animation();
        animation.setFrames(sprites);
        animation.setDelay(10);

        left = true;
        facingRight = true;

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
        //update la pozitie
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
            facingRight = true;

        }else if(left && dx == 0){
            right = true;
            left = false;
            facingRight = false;
        }
        //update animatie
        animation.update();
    }

    public void draw(Graphics2D g){

        setMapPosition();

        super.draw(g);

    }

}
