package Enemies;

import Entity.Animation;
import TileMap.TileMap;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;

public class BetterOrangeGoblin extends OrangeGoblin{

    private BufferedImage[] sprites;
    public BetterOrangeGoblin(TileMap tm){
        super(tm);

        moveSpeed = 0.6;
        maxSpeed = 1.5;
        fallSpeed = 0.2;
        maxFallSpeed = 10.0;

        width = 20;
        height = 20;
        cwidth = 15;
        cheight = 15;

        health = maxHealth = 2;
        damage = 2;
        scoreIncrement = 30;

        //load sprites/animation;
        try{
            sprites = new BufferedImage[43];
            for(int i = 0 ; i < sprites.length; i++ ){
                sprites[i] = ImageIO.read(new FileInputStream("Resources/Sprites/Enemies/OrangeGoblin/og_"+i+".png"));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
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
        //update position
        getNextPosition();
        checkTileMapCollision();
        setPosition(xtemp, ytemp);

        //check flinching
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
        //update animation
        animation.update();
    }

    public void draw(Graphics2D g){

        setMapPosition();

        super.draw(g);

    }

}