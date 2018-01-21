package ch.client.model;

import javafx.scene.paint.Paint;

public class OtherPlayer {

    private int x, y;
    private Paint color = Paint.valueOf("white");
    public OtherPlayer(){}
    public OtherPlayer(int x, int y){
        this.x = x;
        this.y = y;
    }

    public void setX(int val){
        x = val;
    }
    public void setY(int val){
        y = val;
    }
    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
    public Paint getColor(){return color;}
}
