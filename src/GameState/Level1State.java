package GameState;

import DatabaseManager.DatabaseManager;
import Entity.*;
import Enemies.OrangeGoblin;
import Main.GamePanel;
import TileMap.*;
import DatabaseManager.DatabaseExceptions;


import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Level1State extends GameState{

    private TileMap tileMap;
    private Background bg;
    private DatabaseManager databaseManager;
    private Player player;
    private ArrayList<Enemy> enemies;
    private ArrayList<Explosion> explosions;
    private HUD hud;
    private ArrayList<HealthPickup> healthPickups;
    public Level1State(GameStateManager gsm) throws DatabaseExceptions {
        this.gsm = gsm;
        init();
    }

    public void init() throws DatabaseExceptions {
        //incarcare harta cu texturi si pozitie
        tileMap = new TileMap(30);
        tileMap.loadTiles("Resources/Tilesets/grasstileset.gif");
        tileMap.loadMap("Resources/Maps/level1-1.map");
        tileMap.setPosition(0, 0);
        tileMap.setTween(1);

        //incarcare fundal
        bg = new Background("/Backgrounds/gameBackground.jpg",0.1);

        //declarare observer
        databaseManager = new DatabaseManager();

        //initializare  jucator
        player = new Player(tileMap);
        player.setPosition(100,300);
        //adaugam observator
        player.addObserver(databaseManager);

        populateEnemies();//se plaseaza inamicii

        explosions = new ArrayList<Explosion>();//se adauga exploziile
        placeHearts();//se plaseaza inimile


        hud = new HUD(player);//plasarea HUD-ului



    }
    private void placeHearts(){//plasarea strategica a inimilor
        healthPickups = new ArrayList<HealthPickup>();
        Point[] points = new Point[] {
                new Point(2360,405),
                new Point(990,495)
        };
        for(int i = 0 ; i < points.length; i++){
            HealthPickup hp;
            hp = new HealthPickup(tileMap);
            hp.setPosition(points[i].x,points[i].y);
            healthPickups.add(hp);
        }
    }
    private void populateEnemies(){//plasarea strategica a inamicilor
        enemies = new ArrayList<Enemy>();
        OrangeGoblin og;
        Point[] points = new Point[] {
                new Point(860,450),
                new Point(1525, 450),
                new Point(1680, 450),
                new Point(1800,450),
        };
        for(int i = 0 ; i < points.length; i++){
            og = new OrangeGoblin(tileMap);
            og.setPosition(points[i].x, points[i].y);
            enemies.add(og);
        }




    }
    public void update() throws DatabaseExceptions {
        //update la jucator
        player.update();
        tileMap.setPosition(
                GamePanel.WIDTH / 2 - player.getx(),
                GamePanel.HEIGHT / 2 - player.gety()
        );
        //Setare fundal
        bg.setPosition(tileMap.getx(), tileMap.gety());

        //atac catre inamici
        player.checkAttack(enemies);

        //update la toti inamici
        for(int i = 0 ; i < enemies.size(); i++ ){
            Enemy e = enemies.get(i);
            e.update();
            if(e.isDead()){//daca e mort se elimina de pe harta
                player.setScore(e.getScoreIncrement());
                enemies.remove(i);
                i--;
                explosions.add(new Explosion(e.getx(), e.gety()));

            }
        }
       //update inimioare
        for(int i = 0 ; i < healthPickups.size();i++){
            HealthPickup hp = healthPickups.get(i);
            hp.update();
            if(hp.isPicked()){
                healthPickups.remove(i);
                i--;
            }
            if(hp.intersectPlayer(player) && !hp.isPicked()){//daca viata nu e plina, adauga viata, altfel adauga scor
                if(player.getHealth() < player.getMaxHealth()){
                    player.heal(hp.getHealthValue());
                }else{
                    player.setScore(hp.getScoreValue());
                }
                hp.pick();
            }
        }
        //update taote exploziile
        for(int i = 0 ; i < explosions.size(); i++){
            explosions.get(i).update();
            if(explosions.get(i).shouldRemove()){
                explosions.remove(i);
                i--;
            }
        }

        if(player.gety() >= 700){
            player.kill();
        }
        //La x > 2900 ajunge la capatul hartii
        if(player.getx() >=2900){
            player.removeObserver(databaseManager);
            databaseManager.storeState(2,player.getHealth(),player.getScore());
            gsm.setState(GameStateManager.LEVEL2State);
        }

        //aici verificare pentru incarcare din baza de date, sa nu incarce un jucator cu 0 viata
        if(player.isDead() || player.getHealth() <= 0){
            gsm.setState(GameStateManager.GAMEOVERSTATE);
            player.removeObserver(databaseManager);
        }




    }

    public void draw(Graphics2D g){
        //desenare fundal
        bg.draw(g);


        //desenare harta
        tileMap.draw(g);

        //desenare jucator
        player.draw(g);

        //ddesenare inim
        for(int i = 0 ; i < healthPickups.size(); i++){
            healthPickups.get(i).draw(g);
        }

        //desenare inamici
        for(int i = 0 ; i < enemies.size(); i++ ){
            enemies.get(i).draw(g);
        }
        //desenare explozii
        for(int i = 0; i < explosions.size();i++){
            explosions.get(i).setMapPosition((int)tileMap.getx(), (int)tileMap.gety());
            explosions.get(i).draw(g);
        }

        //deseneare HUD
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
