package TileMap;

import GameState.GameStateManager;
import Main.GamePanel;

import java.awt.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
import java.io.*;



public class TileMap {
    //Gameover if out of bounds
    GameStateManager gsm;
    //position
    private double x;
    private double y;

    //bounds

    private int xmin;
    private int ymin;
    private int xmax;
    private int ymax;

    private double tween;

    //map
    private int[][] map;
    private int tileSize;
    private int numRows;
    private int numCols;
    private int width;
    private int height;

    //tilest

    private BufferedImage tileset;
    private int numTilesAcross;
    private Tile[][] tiles;

    //drawing

    private int rowOffset;
    private int colOffset;
    private int numRowsToDraw;
    private int numColsToDraw;

    public TileMap(int tileSize){
        this.tileSize = tileSize;
        numRowsToDraw = GamePanel.HEIGHT / tileSize + 2;
        numColsToDraw = GamePanel.WIDTH / tileSize + 2;
        tween = 0.07;

    }

    public void loadTiles(String s){

        try {

            tileset = ImageIO.read(new FileInputStream(s));
            numTilesAcross = tileset.getWidth() / tileSize;
            tiles = new Tile[2][numTilesAcross];

            BufferedImage subimage;
            for(int col = 0 ; col < numTilesAcross; col++){
                subimage = tileset.getSubimage(col * tileSize, 0, tileSize, tileSize);
                tiles[0][col] = new Tile(subimage, Tile.NORMAL);
                subimage = tileset.getSubimage(col * tileSize, tileSize, tileSize, tileSize);
                tiles[1][col] = new Tile(subimage, Tile.BLOCKED);
            }

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void loadMap(String s){


        try{
            BufferedReader fr = new BufferedReader(new FileReader(s));
            numCols = Integer.parseInt(fr.readLine());
            numRows = Integer.parseInt(fr.readLine());

            width = numCols * tileSize;
            height = numRows * tileSize;
            map = new int[numRows][numCols];

            xmin = GamePanel.WIDTH - width;
            xmax = 0;
            ymin = GamePanel.HEIGHT - height;
            ymax = 0;

            String delims = "\\s+";
            for(int row = 0; row < numRows; row++){
                String line = fr.readLine();
                String[] tokens = line.split(delims);
                for(int col = 0 ; col < numCols; col++){
                    map[row][col] = Integer.parseInt(tokens[col]);
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }
    public int getTileSize() {return tileSize;}
    public double getx() { return x;}
    public double gety() { return y;}
    public int getWidth() { return width;}
    public int getHeight() { return height;}
    public int getType(int row, int col){

        int rc = map[row][col];
        int r = rc / numTilesAcross;
        int c = rc % numTilesAcross;
        return tiles[r][c].getType();

    }
    public void setTween(double d){
        this.tween = d;
    }

    public void setPosition(double x, double y){
        this.x += (x - this.x) * tween;
        this.y += (y - this.y) * tween;

        fixBounds();

        colOffset = (int)-this.x / tileSize;
        rowOffset = (int)-this.y / tileSize;
    }
    private void fixBounds(){
        if(x < xmin) x = xmin;
        if(y < ymin) y = ymin;
        if(x > xmax) x = xmax;
        if(y > ymax) y = ymax;
    }

    public void draw(Graphics2D g){
        for(int row = rowOffset; row < rowOffset + numRowsToDraw; row++){
            if(row >= numRows) break;
            for(int col = colOffset; col < colOffset + numColsToDraw; col++){
                if(col >=numCols) break;

                if (map[row][col] == 0) continue;

                int rc = map[row][col];
                int r = rc / numTilesAcross;
                int c = rc % numTilesAcross;

                g.drawImage(tiles[r][c].getImage(), (int)x  + col * tileSize, (int)y + row * tileSize, null);

            }

        }
    }
}
