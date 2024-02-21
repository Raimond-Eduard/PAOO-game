package Main;

import DatabaseManager.DatabaseExceptions;
import DatabaseManager.DatabaseManager;
import GameState.GameStateManager;

import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.sql.SQLException;


public class GamePanel extends JPanel
    implements Runnable, KeyListener {

    //dimensiunile ecranului sunt date in aceasta clasa

    public static int WIDTH;
    public static int HEIGHT;
    public static final int SCALE = 1;//Modificarea scale-ului rezulta marirea ecranului de tip window nu cel pe care se intampla propriu-zis jocul

    //De aici incepe game thread-ul
    private Thread thread;
    private boolean running;
    private int FPS = 60;
    private long targetTime = 1000 / FPS;

    //incarcare imagini
    private BufferedImage image;
    private Graphics2D g;

    //game state manager, implementat folosind state pattern
    private GameStateManager gsm;

    public GamePanel() throws DatabaseExceptions {
        super();
        DatabaseManager dbm = new DatabaseManager();//Se incarca dimensiunile ecranului de joc
        WIDTH = dbm.getWidth();
        HEIGHT = dbm.getHeight();
        setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
        setFocusable(true);
        requestFocus();
    }
    public void addNotify() {
        super.addNotify();//In conditiile in care thread-ul pica, se va crea altul care sa preia mai departe
        if(thread == null){
            thread = new Thread(this);
            addKeyListener(this);
            thread.start();
        }
    }
    private void init() throws DatabaseExceptions, SQLException {
        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        g = (Graphics2D) image.getGraphics();
        running =  true;

        gsm = new GameStateManager();
    }
    public void run() {

        try {
            init();
        } catch (DatabaseExceptions | SQLException e) {
            throw new RuntimeException(e);
        }

        long start;
        long elapsed;
        long wait;
        //main loop
        while(running){

            start = System.nanoTime();

            try {
                update();
            } catch (DatabaseExceptions e) {
                throw new RuntimeException(e);
            }
            draw();
            drawToScreen();

            elapsed =  System.nanoTime() - start;

            wait = targetTime - elapsed / 1000000000;
            try{
                Thread.sleep(wait);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public void update() throws DatabaseExceptions {//Functia de update care reia fiecare verificare necesara la fiecare loop
        gsm.update();
    }
    public void draw(){
        gsm.draw(g);
    }//Functia care deseneaza texturile pe ecran
    public void drawToScreen(){
        Graphics g2 = getGraphics();
        g2.drawImage(image, 0,0,null);
        g2.dispose();
    }

//Mai jos sunt functiile care vor citi input-urile de la tastatura
    public void keyTyped(KeyEvent key){

    }


    public void keyPressed(KeyEvent key){
        try {
            gsm.keyPressed(key.getKeyCode());
        } catch (DatabaseExceptions e) {
            throw new RuntimeException(e);
        }
    }

    public void keyReleased(KeyEvent key){
        gsm.keyReleased(key.getKeyCode());
    }
}
