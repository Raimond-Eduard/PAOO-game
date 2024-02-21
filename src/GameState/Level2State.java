package GameState;
import DatabaseManager.DatabaseExceptions;
import DatabaseManager.DatabaseManager;
import Enemies.BetterOrangeGoblin;
import Enemies.GiantSpider;
import Entity.Enemy;
import Entity.Explosion;
import Entity.*;
import Main.Game;
import Main.GamePanel;
import TileMap.*;


import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Level2State extends GameState{
    private TileMap tileMap;
    private Background bg;
    private Player player;
    private ArrayList<Enemy> enemies;
    private ArrayList<Explosion> explosions;
    private ArrayList<HealthPickup> healthPickups;
    private HUD hud;
    private DatabaseManager databaseManager = new DatabaseManager();

    public Level2State(GameStateManager gsm) throws DatabaseExceptions{
        this.gsm = gsm;
        init();
    }

    public void init() throws DatabaseExceptions{
        tileMap = new TileMap(30);
        tileMap.loadTiles("Resources/Tilesets/grasstileset.gif");
        tileMap.loadMap("Resources/Maps/level2.map");
        tileMap.setPosition(0,0);
        tileMap.setTween(1);



        bg = new Background("/Backgrounds/level2background.jpg", 0.1);
        databaseManager = new DatabaseManager();


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
                new Point(1480,435),
                new Point(2410,285)
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


        Point[] spiders = new Point[]{
            new Point(480,260),
                new Point(990,480),
                new Point(1080,480),
                new Point(1200,480),
                new Point(1890,260),
                new Point(2040, 260),
                new Point(2160, 260)
        };
        Point[] goblins = new Point[]{
                new Point(630,400),
                new Point(2530, 285)
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
        //Mai mult de 3000 nextlevel
        if(player.getx() >= 3000){
            databaseManager.storeState(3, player.getHealth(), player.getScore());
            gsm.setState(GameStateManager.LEVEL3State);

            player.removeObserver(databaseManager);
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
    //desenarea propriu-zisa

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
