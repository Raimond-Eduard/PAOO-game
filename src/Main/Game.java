package Main;


import DatabaseManager.*;

import javax.swing.JFrame;
public class Game {
    public static void main(String[] args) throws DatabaseExceptions {
        JFrame window = new JFrame("Claurentiu - Aventura");
        window.setContentPane(new GamePanel());//Aici se intampla main loop
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.pack();
        window.setVisible(true);


    }
}
