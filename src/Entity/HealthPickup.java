package Entity;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;

import TileMap.*;

import javax.imageio.ImageIO;

public class HealthPickup extends MapObject{
    //Cata viata poate da inapoi
    private int healthValue;
    //Pentru cand nu da viata, aceasta da scor
    private int scoreValue;
    //Un flag pentru a confirma disparitia inimii de pe harta
    private boolean isPicked;
    //imaginea pe care o va lua
    private BufferedImage[] image;

    public HealthPickup(TileMap tm){
        super(tm);
        healthValue = 2;
        scoreValue = 20;
        width = 30;
        height = 30;
        cwidth = 25;
        cheight = 25;
        try{
            image = new BufferedImage[1];
            image[0] = ImageIO.read(new FileInputStream("Resources/Pickups/heartPickup.png"));
        }catch(Exception e){
            e.printStackTrace();
        }
        animation = new Animation();
        animation.setFrames(image);
        animation.setDelay(0);
    }

    public int getHealthValue(){return healthValue;}
    public int getScoreValue(){return scoreValue;}
    public boolean isPicked(){return isPicked;}
    public void update(){
        //update la actiuni
        checkTileMapCollision();
        setPosition(xtemp,ytemp);


    }
    public boolean intersectPlayer(Player other){
        return this.interescts(other);
    }
    public void pick(){
        isPicked = true;
    }
    public void draw(Graphics2D g){
        setMapPosition();

        super.draw(g);
    }
}
