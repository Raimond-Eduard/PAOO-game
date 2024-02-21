package Enemies;

import Entity.Animation;
import Entity.Enemy;

import TileMap.TileMap;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;

public class GiantSpider extends Enemy {
    private BufferedImage[] sprite;

    public GiantSpider(TileMap tm){
        super(tm);

        width = 30;
        height = 30;
        cwidth = 25;
        cheight = 25;


        health = maxHealth = 2;
        damage = 1;
        scoreIncrement = 20;


        //incarcam sprite-urile
        try{
            sprite = new BufferedImage[1];
            sprite[0] = ImageIO.read(new FileInputStream("Resources/Sprites/Enemies/arachnik.gif"));
        }catch(Exception e){
            e.printStackTrace();
        }
        animation = new Animation();
        animation.setFrames(sprite);
        animation.setDelay(10);

        left = true;
        facingRight = false;
    }

    public void update(){
        //update la stare
        checkTileMapCollision();
        setPosition(xtemp, ytemp);

        //verificare mecanica flinch
        if(flinching){
            long elapsed = (System.nanoTime() - flinchingTimer) / 1000000;
            if(elapsed > 400){
                flinching = false;
            }
        }
    }
    public void draw(Graphics2D g){
        setMapPosition();


        super.draw(g);
    }
}
