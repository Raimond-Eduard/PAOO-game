package GameState;

import DatabaseManager.DatabaseManager;
import Enemies.BetterOrangeGoblin;
import Enemies.GiantSpider;
import Entity.Enemy;
import Main.GamePanel;
import TileMap.*;
import Entity.*;
import  DatabaseManager.*;
import Enemies.Demon;


import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Level3State extends GameState{
    private TileMap tileMap;
    private Background bg;
    private Player player;
    private ArrayList<Enemy> enemies;
    private ArrayList<Explosion> explosions;
    private ArrayList<HealthPickup> healthPickups;
    private HUD hud;
    private DatabaseManager databaseManager = new DatabaseManager();

    public Level3State(GameStateManager gsm) throws DatabaseExceptions {
        this.gsm = gsm;
        init();
    }

    public void init(){
        tileMap = new TileMap(30);
        tileMap.loadTiles("Resources/Tilesets/grasstileset.gif");
        tileMap.loadMap("Resources/Maps/level3.map");
        tileMap.setPosition(0,0);
        tileMap.setTween(1);

        bg = new Background("/Backgrounds/level3Background.png", 0.1);

        player = new Player(tileMap);
        player.loadScore(databaseManager.selectScore());
        player.loadHealth(databaseManager.selectHealth());
        player.setPosition(100,200);
        player.addObserver(databaseManager);

        populateEnemies();

        explosions = new ArrayList<Explosion>();

        placeHearts();

        hud = new HUD(player);
    }

    private void placeHearts(){
        healthPickups = new ArrayList<HealthPickup>();

        Point[] points = new Point[] {
                new Point(800,435),
                new Point(2350,435),
                new Point(3280, 375),
                new Point(3200,465)
        };
        for(int i = 0 ; i < points.length; i++){
            HealthPickup hp;
            hp = new HealthPickup(tileMap);
            hp.setPosition(points[i].x,points[i].y);
            healthPickups.add(hp);
        }
    }
    private void populateEnemies(){
        enemies = new ArrayList<Enemy>();
        BetterOrangeGoblin bog;
        GiantSpider giantSpider;
        Demon demon;

        Point[] spiders = new Point[]{
                new Point(1020, 320),
                new Point(1110,320),
                new Point(1800,400),
                new Point(2550,330),
                new Point(2730,275),
                new Point(3150, 240)

        };
        Point[] goblins = new Point[]{
                new Point(1850,460),
                new Point(2920, 370)
        };

        Point[] demons = new Point[]{
              new Point(480,490),
              new Point(1325,520),
              new Point(1510,520),
                new Point(2380,550),
              new Point(2920, 370)
        };
        Point[] triggers = new Point[]{
                new Point(350,570),
                new Point(1180,1660),
                new Point(1180,1660),
                new Point(2110,2720),
                new Point(2840,3000)
        };
        //initializarea paianjenilor
        for(int i = 0 ; i < spiders.length;i++){
            giantSpider = new GiantSpider(tileMap);
            giantSpider.setPosition(spiders[i].x,spiders[i].y);
            enemies.add(giantSpider);
        }
        //initializarea goblinilor
        for(int i = 0 ; i < goblins.length; i++){
            bog = new BetterOrangeGoblin(tileMap);
            bog.setPosition(goblins[i].x,goblins[i].y);
            enemies.add(bog);
        }
        //initializem demonii
        for(int i = 0 ; i < demons.length; i++){
            demon = new Demon(tileMap);
            demon.setPosition(demons[i].x,demons[i].y);
            demon.setBoundries(triggers[i].x,triggers[i].y);
            enemies.add(demon);
        }
    }
    public void update() throws DatabaseExceptions{
        //din nou update player
        player.update();


        tileMap.setPosition(GamePanel.WIDTH/2 - player.getx(),
                GamePanel.HEIGHT / 2 - player.gety());

        //set background
        bg.setPosition(tileMap.getx(), tileMap.gety());

        //atac inamici
        player.checkAttack(enemies);

        //update la inimaici
        for(int i = 0 ; i < enemies.size(); i++){
            Enemy e = enemies.get(i);
            e.update();
            if(e.isDead()){
                player.setScore(e.getScoreIncrement());
                enemies.remove(i);
                i--;
                explosions.add(new Explosion(e.getx(),e.gety()));
            }
            if(e instanceof Demon){
                if(player.getx()>((Demon) e).getxStart() && player.getx() < ((Demon) e).getxEnd()){
                    ((Demon) e).enrage();
                }else{
                    ((Demon) e).calm();
                }
            }
        }



        //update la explozii
        for(int i = 0 ; i < explosions.size(); i++){
            explosions.get(i).update();
            if(explosions.get(i).shouldRemove()){
                explosions.remove(i);
                i--;
            }
        }
        //update la inimi
        for(int i = 0 ; i < healthPickups.size();i++){
            HealthPickup hp = healthPickups.get(i);
            hp.update();
            if(hp.isPicked()){
                healthPickups.remove(i);
                i--;
            }
            if(hp.intersectPlayer(player) && !hp.isPicked()){
                if(player.getHealth() < player.getMaxHealth()){
                    player.heal(hp.getHealthValue());
                }else{
                    player.setScore(hp.getScoreValue());
                }
                hp.pick();
            }
        }
        //Mai mult de 3600 endgame
        if(player.getx()>=3600){
            player.removeObserver(databaseManager);
            databaseManager.storeState(4, player.getHealth(), player.getScore());
            gsm.setState(GameStateManager.WINSTATE);
        }
        //Out of bound il omoara
        if(player.gety() >= 700){
            player.kill();
        }
        if(player.isDead() || player.getHealth() <= 0){
            gsm.setState(GameStateManager.GAMEOVERSTATE);
            player.removeObserver(databaseManager);
        }
    }
    public void draw(Graphics2D g){
        bg.draw(g);

        //desenarea tilemap
        tileMap.draw(g);

        //desenare jucator
        player.draw(g);

        //inimioare
        for(int i = 0 ; i < healthPickups.size(); i++){
            healthPickups.get(i).draw(g);
        }

        //desenarea inamici
        for(int i = 0; i < enemies.size(); i++){
            enemies.get(i).draw(g);
        }

        //deseneare explozii
        for(int i = 0; i < explosions.size(); i++){
            explosions.get(i).setMapPosition((int)tileMap.getx(), (int)tileMap.gety());
            explosions.get(i).draw(g);
        }

        //HUD
        hud.draw(g);

    }
    public void keyPressed(int k) throws DatabaseExceptions {
        if(k == KeyEvent.VK_LEFT) player.setLeft(true);
        if(k == KeyEvent.VK_RIGHT) player.setRight(true);
        if(k == KeyEvent.VK_UP) player.setUp(true);
        if(k == KeyEvent.VK_DOWN) player.setDown(true);
        if(k == KeyEvent.VK_W) player.setJumping(true);
        if(k == KeyEvent.VK_R) player.setAttack();
        if(k == KeyEvent.VK_ESCAPE) gsm.setState(GameStateManager.MENUSTATE);
    }
    public void keyReleased(int k){
        if(k == KeyEvent.VK_LEFT) player.setLeft(false);
        if(k == KeyEvent.VK_RIGHT) player.setRight(false);
        if(k == KeyEvent.VK_UP) player.setUp(false);
        if(k == KeyEvent.VK_DOWN) player.setDown(false);
        if(k == KeyEvent.VK_W) player.setJumping(false);
    }

}
