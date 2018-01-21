package ch.client.view;

import ch.client.model.BlocksMap;
import ch.client.model.OtherPlayer;
import ch.client.model.Player;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class BoardController{

    private GridPane gameGrid;
    private Scene scene;
    private String username;
    private Player player;
    private BlocksMap blocksMap;
    private int fieldSize = 20;
    private Boolean isJoinBtnClicked = false;
    private Paint fieldColor = Paint.valueOf("#0f0f0f");
    private Paint blockColor = Paint.valueOf("gray");
    private Boolean gameEnded;
    private int size;
    private int portNumber = 4444;
    private ArrayList<OtherPlayer> otherPlayers;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    @FXML
    private Text infoLabel;
    @FXML
    private TextField usernameInput;
    @FXML
    private BorderPane gameSceneBorderPane;
    @FXML
    private Button menuBtn1;
    @FXML
    private void initialize() {
        otherPlayers = new ArrayList<OtherPlayer>();
        gameEnded = false;

        startGameLogic();
    }
    @FXML
    public void handleActionUsernameInput(ActionEvent event){

    }
    @FXML
    public void handleMenuBtn1Click(ActionEvent event) throws IOException {
        menuBtn1.setDisable(Boolean.TRUE);
        usernameInput.setDisable(Boolean.TRUE);
        username = usernameInput.getText();
        player.setName(usernameInput.getText());
        player.setStartLocation(size/2,size/2);
        paintPlayer(player, player.getX(), player.getY());
        isJoinBtnClicked = Boolean.TRUE;

        scene = menuBtn1.getScene();
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {


                        switch (event.getCode()) {
                            case UP:
                                if(!gameEnded)
                                    moveUp(player);
                                break;
                            case DOWN:
                                if(!gameEnded)
                                    moveDown(player);
                                break;
                            case LEFT:
                                if(!gameEnded)
                                    moveLeft(player);
                                break;
                            case RIGHT:
                                if(!gameEnded)
                                    moveRight(player);
                                break;
                            default:
                                break;
                        }

                }
            });

    }
    public Node getNodeByRowColumnIndex (final int row, final int column, GridPane gridPane) {
        Node result = null;
        ObservableList<Node> childrens = gridPane.getChildren();

        for (Node node : childrens) {
            if(gridPane.getRowIndex(node) == row && gridPane.getColumnIndex(node) == column) {
                result = node;
                break;
            }
        }
        return result;
    }
    private void getBoardSize(){
        try{
            String tmp = in.readLine();
            size = Integer.parseInt(tmp);
            System.out.format("SIZE: %d\n",size);
        } catch (Exception e){
            System.out.println("[EXC] In getBoardSize");
            System.err.println(e);
            System.exit(1);
        }

    }
    private void fillMap(){
        try{
            out.println("GIMME_MAP");
            String inInfo = in.readLine();
            if(inInfo.equals("MAP_INCOMMING")){
//                System.out.format("____PRINT_MAP____size: %d\n", size);
                for(int i = 0; i < size; i++){
                    for(int j = 0; j < size; j++){
                        inInfo = in.readLine();
                        if(Integer.parseInt(inInfo) == 0) {
                            blocksMap.fillBlock(i, j, Boolean.FALSE);
//                            System.out.println("FIELD: 0");
                        }else if(Integer.parseInt(inInfo) == 1){
                            blocksMap.fillBlock(i, j, Boolean.TRUE);
//                            System.out.println("FIELD: 1");
                        }else{
                            System.out.println("INVALID FIELD READ");
                        }
                    }
                }
                System.out.println(in.readLine());
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    private void getMap(){
        getBoardSize();
        blocksMap = new BlocksMap(size);
        fillMap();
    }
    private void generateBoard(){
        gameGrid = new GridPane();
        gameGrid.setHgap(0);
        gameGrid.setVgap(0);

        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                Rectangle rec = new Rectangle();
                rec.setHeight(fieldSize);
                rec.setWidth(fieldSize);
                if(blocksMap.isBlock(j,i))          //tutaj isBlock(j,i) z jakiegos powodu nie na odwrot WTF?f
                    rec.setFill(blockColor);
                else
                    rec.setFill(fieldColor);
                gameGrid.add(rec,i,j);
            }
        }

        gameSceneBorderPane.setCenter(gameGrid);
    }
    private void paintPlayer(Player p, final int x, final int y){
        final Paint color = p.getColor();
        Rectangle rec = (Rectangle) getNodeByRowColumnIndex(x,y,gameGrid);
        rec.setFill(color);
    }
    private void paintOtherPlayer(OtherPlayer op){
        if(op.getX() == player.getX() && op.getY() == player.getY()){
            paintPlayer(player,op.getX(), op.getY());
        }
        else{
            final Paint color = op.getColor();
            Rectangle rec = (Rectangle) getNodeByRowColumnIndex(op.getX(), op.getY(), gameGrid);
            rec.setFill(color);
        }
    }
    private void eraseBlock(final int x, final int y){
        Rectangle rec = (Rectangle) getNodeByRowColumnIndex(x,y,gameGrid);
        rec.setFill(fieldColor);
    }
    private void erasePlayer(Player p){
        final int x = p.getX(), y = p.getY();

        Rectangle rec = (Rectangle) getNodeByRowColumnIndex(x,y,gameGrid);
        rec.setFill(fieldColor);
    }
    private Boolean collisionWithBlock(int x, int y){
        if(blocksMap.isBlock(x,y))
            return Boolean.TRUE;
        else return Boolean.FALSE;
    }
    private void sendInfoWin(String p){
        try{
            out.println("SOMEONE_WON");
            out.println(p);
        } catch (Exception e){
            System.err.println(e);
        }
    }
    private void sendNewPositionOf(Player p){
        try{
            out.println("PLAYER_NEW_POSITION");
            out.println(p.getX());
            out.println(p.getY());
        } catch (Exception e){
            System.err.println(e);
            System.exit(1);
        }
    }
    private void endGame(){
        System.out.println("Setting gameEnded to TRUE");
        this.gameEnded = true;
    }
    private void eraseOtherPlayers(){
        /*te funkcje poprawić, żeby przemalowywac tylko bloki na ktoych stoi OtherPlayer - da sie na
        podstawie otherplayers;)*/
        for( OtherPlayer op : otherPlayers ) {
            System.out.format("%d %d\n", op.getX(),op.getY());
            eraseBlock(op.getX(), op.getY());
        }
    }
    private void redrawOtherPlayers(){
        try{
            eraseOtherPlayers();
            otherPlayers.clear();
            System.out.println("Fetching players info");
            out.println("SEND_PLAYERS_POSITIONS");
            String inInfo = in.readLine();

            System.out.format("Response: %s\n",inInfo);
            if (inInfo.equals("DATA_INCOMMING")) {
                try {
                    inInfo = in.readLine();
                    while (inInfo.equals("PLAYER_INFO_COMMING")) {
                        int x = Integer.parseInt(in.readLine());
                        int y = Integer.parseInt(in.readLine());
                        System.out.format("OTHER_PLAYER: %d %d\n", x,y);
                        if(player.getX() == x && player.getY() == y){

                        }else{
                            otherPlayers.add(new OtherPlayer(x,y));
                        }

                        for( OtherPlayer op : otherPlayers )
                            paintOtherPlayer(op);
                        inInfo = in.readLine();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }catch(Exception e){
            System.out.println("[EXC] Exception while getting players info");
            e.printStackTrace();
        }
    }
    private void checkForGameData(){
        try{
            out.println("GIMME_GAME_DATA");
            String inInfo = "";
            if((inInfo = in.readLine()) != null) {
                System.out.format("Processing %s: \n",inInfo);
                switch (inInfo) {
                    case "OTHER_PLAYER_WON":
                        String winnerName = in.readLine();
                        infoLabel.setText(winnerName.concat(" escaped!"));
                        System.out.format("[INFO]%s won\n", winnerName);
                        endGame();
                        break;
                    case "GAME_ENDED":
                        endGame();
                        break;
                    case "GAME_NOT_ENDED":
                        break;
                    default:
                        System.out.format("[IN-ERROR] Got some useless info: %s\n", inInfo);
                        break;
                }
            }
        }catch(Exception e){
            System.out.println("[EXC] Exception while fetching new data");
            System.out.println(e);
        }
    }
    private void playerWon(Player p){
        System.out.format("You've won. Player %s \n", p.getName());
        endGame();
        erasePlayer(p);
        sendInfoWin(p.getName());
    }
    private void moveUp(Player p){
        if(p.getX() - 1 == -1){
            playerWon(p);
        }else if(!collisionWithBlock(p.getX() - 1, p.getY()) && !gameEnded){
            erasePlayer(p);
            paintPlayer(p, p.getX() - 1, p.getY());
            p.setX(p.getX() - 1);
            sendNewPositionOf(player);
        }
    }
    private void moveDown(Player p){
        if(p.getX() + 1 == size){
            playerWon(p);
        }else if(!collisionWithBlock(p.getX() + 1, p.getY()) && !gameEnded) {
            erasePlayer(p);
            paintPlayer(p, p.getX() + 1, p.getY());
            p.setX(p.getX() + 1);
            sendNewPositionOf(player);
        }
    }
    private void moveLeft(Player p){
        if(p.getY() - 1 == -1){
            playerWon(p);
        }else if(!collisionWithBlock(p.getX(), p.getY() - 1) && !gameEnded) {
            erasePlayer(p);
            paintPlayer(p, p.getX(), p.getY() - 1);
            p.setY(p.getY() - 1);
            sendNewPositionOf(player);
        }
    }
    private void moveRight(Player p){
        if(p.getY() + 1 == size){
            playerWon(p);
        }else if(!collisionWithBlock(p.getX(), p.getY() + 1) && !gameEnded) {
            erasePlayer(p);
            paintPlayer(p, p.getX(), p.getY() + 1);
            p.setY(p.getY() + 1);
            sendNewPositionOf(player);
        }
    }
    public void startGameLogic() {

        try{
            Socket socket = new Socket("localhost",portNumber);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            getMap();
            generateBoard();
            player = new Player("default");

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    System.out.println("*tick*");
                    if(gameEnded){
                        try{
                            menuBtn1.setDisable(Boolean.TRUE);
                            usernameInput.setDisable(Boolean.TRUE);
                            infoLabel.setText("The Game Has Ended");

                            in.close();
                            out.close();
                            socket.close();
                            timer.cancel();

                        }catch(Exception e){
                            System.out.format("[EXC] Couldnt close streams\n");
                            e.printStackTrace();
                        }
                    }else {
                        checkForGameData();
                        redrawOtherPlayers();
                    }
                }
            }, 0, 100);
        }catch(ConnectException e){
            System.out.println("NO CONNECTION");
        }catch(Exception e){
            System.out.println("[EXC] Exception while checking for new data");
            System.out.println(e);
            e.printStackTrace();
        }
}
    public BoardController(){}
}
