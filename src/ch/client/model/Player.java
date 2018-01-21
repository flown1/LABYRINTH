package ch.client.model;

import javafx.scene.paint.Paint;

public class Player {
    private String name;
    private int x, y;
    private Paint color = Paint.valueOf("red");

    public Player(String name){
        this.name = name;
    }
    public void setStartLocation(int x,int y){
        setX(x);
        setY(y);
    }
    public void setX(int val){
        x = val;
    }
    public void setY(int val){
        y = val;
    }
    public void setName(String val){ name = val; }
    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
    public String getName(){System.out.println(name); return name;}
    public Paint getColor(){return color;}
}
