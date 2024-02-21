package Entity;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;

public class HUD {
    private Player player;
    private BufferedImage image;
    private Font font;
    private Font scoreFont;
    private Color scoreColor;
    public HUD(Player p) {
        player = p;

        try{
            image = ImageIO.read(new FileInputStream("Resources/HUD/hud.png"));
            font = new Font("Arial", Font.PLAIN, 14);
            scoreFont = new Font("Arial", Font.PLAIN, 14);
            scoreColor = new Color(0,0,0);
        }
        catch (Exception e){
            e.printStackTrace();
        }


    }

    public void draw(Graphics2D g){

        g.drawImage(image, 0, 20, null);
        g.setFont(font);
        g.setColor(Color.RED);
        g.drawString(player.getHealth()+"/"+player.getMaxHealth(),
                30, 35);
        g.setFont(scoreFont);
        g.setColor(scoreColor);
        g.drawString("Score: "+player.getScoreString(),25, 55);
    }
}
