package Entity;

import Main.GamePanel;
import TileMap.TileMap;
import TileMap.Tile;

import java.awt.*;

public abstract class MapObject {
    //lucrurile cu tile-uri
    protected TileMap tileMap;
    protected int tileSize;
    protected double xmap;
    protected double ymap;

    //pozitie si vector
    protected double x;
    protected double y;
    protected double dx;
    protected double dy;

    //dimensiuni;
    protected int width;
    protected int height;

    //cutia de coliziune
    protected int cwidth;
    protected int cheight;

    //coliziuni
    protected int currRow;
    protected int currCol;
    protected double xdest;
    protected double ydest;
    protected double xtemp;
    protected double ytemp;
    protected boolean topLeft;
    protected boolean topRight;
    protected boolean bottomLeft;
    protected boolean bottomRight;

    //animatii

    protected Animation animation;
    protected int currentAction;
    protected int previousAction;
    protected boolean facingRight;

    //miscare

    protected boolean left;
    protected boolean right;
    protected boolean up;
    protected boolean down;
    protected boolean jumping;
    protected boolean falling;

    //atributele miscarilor
    protected double moveSpeed;
    protected double maxSpeed;
    protected double stopSpeed;
    protected double fallSpeed;
    protected double maxFallSpeed;
    protected double jumpStart;
    protected double stopJumpSpeed;

    //constructor

    public MapObject(TileMap tm){
        tileMap = tm;
        tileSize = tm.getTileSize();
    }
    //O parte din tratarea coliziunilor
    public boolean interescts(MapObject other){
        Rectangle r1 = getRectagnle();
        Rectangle r2 = other.getRectagnle();
        return r1.intersects(r2);
    }
    public Rectangle getRectagnle(){
        return new Rectangle((int)x - cwidth,(int)y - cheight, cwidth, cheight);
    }
    //seteaza fiecare tile daca e blocat sau nu pentru coliziuni
    public void calculateCorners(double x, double y){

        int leftTile = (int)(x - cwidth/2)/tileSize;
        int rightTile = (int)(x + cwidth/2 - 1)/tileSize;
        int topTile = (int)(y-cheight/2)/tileSize;
        int bottomTile = (int)(y + cheight/2 - 1)/tileSize;

        int tl = tileMap.getType(topTile,leftTile);
        int tr = tileMap.getType(topTile, rightTile);
        int bl = tileMap.getType(bottomTile, leftTile);
        int br = tileMap.getType(bottomTile, rightTile);


        topLeft = tl == Tile.BLOCKED;
        topRight = tr == Tile.BLOCKED;
        bottomLeft = bl == Tile.BLOCKED;
        bottomRight = br == Tile.BLOCKED;

    }
    //verificare propriu-zisa a coliziunilor
    public void checkTileMapCollision(){
        currCol = (int)x/tileSize;
        currRow = (int)y/tileSize;

        xdest = x + dx;
        ydest = y + dy;

        xtemp = x;
        ytemp = y;

        calculateCorners(x, ydest);
        if(dy < 0){
            if(topLeft || topRight) {
                dy = 0;
                ytemp = currRow * tileSize + cheight / 2;

            }else {
                ytemp += dy;
            }
        }
        if(dy > 0){
            if(bottomLeft || bottomRight){
                dy = 0;
                falling = false;
                ytemp = (currRow + 1) * tileSize - cheight / 2;

            }else{
                ytemp += dy;
            }
        }
        calculateCorners(xdest, y);
        if(dx < 0){
            if(topLeft || bottomLeft){
                dx = 0 ;
                xtemp = currCol * tileSize + cwidth / 2;

            }else{
                xtemp += dx;
            }
        }
        if(dx > 0){
            if(topRight || bottomRight){
                dx = 0 ;
                xtemp = (currCol + 1) * tileSize - cwidth /2;

            }else{
                xtemp += dx;
            }
        }
        if(!falling){
            calculateCorners(x, ydest + 1);
            if(!bottomLeft && !bottomRight){
                falling = true;
            }
        }
    }
    public int getx(){return (int)x; }
    public int gety(){return (int)y; }
    public int getHeight(){return height; }
    public int getWidth(){return width; }
    public int getCWidth(){return cwidth; }
    public int getCheight(){return cheight;}

    public void setPosition(double x, double y){
        this.x = x;
        this.y = y;

    }

    public void setVector(double dx, double dy){
        this.dx = dx;
        this.dy = dy;
    }

    public void setMapPosition(){
        xmap = tileMap.getx();
        ymap = tileMap.gety();
    }

    public void setLeft(boolean b) { left = b; }
    public void setRight(boolean b) { right = b; }
    public void setUp(boolean b) { up = b; }
    public void setDown(boolean b){down = b;}
    public void setJumping(boolean b){ jumping = b; }

    public boolean notOnScreen(){
        return x + xmap + width < 0 ||
                x + xmap - width > GamePanel.WIDTH ||
                y + ymap +height < 0 ||
                y + ymap - height > GamePanel.HEIGHT;
    }

    public void draw(Graphics2D g){
        if(facingRight){
            g.drawImage(animation.getImage(),
                    (int)(x + xmap - width / 2),
                    (int)(y + ymap - height / 2),
                    null);

        }else{
            g.drawImage(animation.getImage(),
                    (int)(x + xmap - width / 2 + width),
                    (int)(y + ymap - height / 2),
                    -width,
                    height,
                    null);
        }
    }


}
