package DatabaseManager;
import Entity.Player;

import javax.swing.plaf.nimbus.State;
import java.sql.*;
public class DatabaseManager implements Observer{
    private ResultSet resultSet;
    private String query;
    public DatabaseManager() throws DatabaseExceptions {

    }

    public void updateScoreHealth(int score, int health){//scrierea dureaza mai mult asa ca se intampla asincron

        try(Connection connection = DriverManager.getConnection("jdbc:sqlite:DBforClaurentiu.db");
                Statement statement = connection.createStatement()){
            query = "UPDATE Player SET Score = "+score+", Health = " + health+";";
            statement.executeUpdate(query);


        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    public void storeState(int state, int health, int score){//in functia asta am separat fiecare comanda pe thread-uri deoarece sunt 2 operatii de scriere care au rist de a se intampla simultan.


        try(Connection connection = DriverManager.getConnection("jdbc:sqlite:DBforClaurentiu.db");
            Statement statement = connection.createStatement()){
            query = "UPDATE Levels SET LevelToLoad = "+state+";";
            statement.executeUpdate(query);

            query = "Update Player SET ScoreForLevelStart = "+score+", HealthForLevelStart = "+health+";";
            statement.executeUpdate(query);



        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    public void storeState(int state) {//in functia asta am separat fiecare comanda pe thread-uri deoarece sunt 2 operatii de scriere care au rist de a se intampla simultan.


        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:DBforClaurentiu.db");
             Statement statement = connection.createStatement()) {
            query = "UPDATE Levels SET LevelToLoad = " + state + ";";
            statement.executeUpdate(query);


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public int loadState(){//incarcare state din baza de date
        int state = 0;
        try{
            Connection connection = DriverManager.getConnection("jdbc:sqlite:DBforClaurentiu.db");
            query = "SELECT LevelToLoad FROM Levels";
            Statement stmt = connection.createStatement();
            resultSet = stmt.executeQuery(query);
            if(resultSet.next()){
                state = resultSet.getInt("LevelToLoad");
            }
            connection.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
        return state;
    }
    public int getHeight(){//incarcare inaltime ecran din baza de date
        int height = 0;
        try{
            Connection connection = DriverManager.getConnection("jdbc:sqlite:DBforClaurentiu.db");
            query = "SELECT Height FROM GameSettings";
            Statement stmt = connection.createStatement();
            resultSet = stmt.executeQuery(query);
            if(resultSet.next()){
                height = resultSet.getInt("Height");
            }
            connection.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return height;
    }
    public int getWidth(){//incarcare latime ecran din baza de date
        int width = 0;
        try{
            Connection connection = DriverManager.getConnection("jdbc:sqlite:DBforClaurentiu.db");
            query = "SELECT Width FROM GameSettings";
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            if(resultSet.next()){
                width = resultSet.getInt("Width");
            }
            connection.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
        return width;
    }
    public int selectScore(){//selectarea scorului pentru inceputul fiecarui nivel
        int score = 0;
        try{
            Connection connection = DriverManager.getConnection("jdbc:sqlite:DBforClaurentiu.db");
            query = "SELECT ScoreForLevelStart FROM Player";
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            if(resultSet.next()){
                score = resultSet.getInt("ScoreForLevelStart");
            }
            connection.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
        return score;
    }
    public int selectHealth(){//selectarea nivelului de viata pentru incaputul fiecarui nivel
        int health = 0;
        try{
            Connection connection = DriverManager.getConnection("jdbc:sqlite:DBforClaurentiu.db");
            query = "SELECT HealthForLevelStart FROM Player";
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            if(resultSet.next()){
                health = resultSet.getInt("HealthForLevelStart");
            }
            connection.close();
        }catch(SQLException e){
            e.printStackTrace();
        }

        return health;
    }

    public String finalScore(){//Returnarea scorului de la sfarsitul jocului intr-un string pentru a fi afisat in WINSTATE
        String finalScore = null;
        try{
            Connection connection = DriverManager.getConnection("jdbc:sqlite:DBforClaurentiu.db");
            query = "SELECT Score FROM Player";
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            if(resultSet.next()){
                finalScore = Integer.toString(resultSet.getInt("Score"));
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return finalScore;
    }
    @Override
    public void update(Subject subject){//functia update folosita de catre observer pattern
        if(subject instanceof Player){
            Player player = (Player) subject;
            int score = player.getScore();
            int health = player.getHealth();
            updateScoreHealth(score, health);
        }
    }


}
