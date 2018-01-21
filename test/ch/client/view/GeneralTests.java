package ch.client.view;

import ch.client.model.BlocksMap;
import ch.client.model.Player;
import org.junit.jupiter.api.Test;
import org.testng.Assert;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

class GeneralTests {

    @Test
    void isServerOnline() {
        try(
                Socket socket = new Socket("localhost", 4444);
        ){
            socket.close();
        }catch(ConnectException e) {
            assertEquals(false, true);
        }catch(Exception e){
            assertEquals(false, true);
        }
    }
    @Test
    void isMapSizeReturnedProper(){
        try(
                Socket socket = new Socket("localhost", 4444);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ) {
            int size = Integer.parseInt(in.readLine());
            System.out.println(size);
            assertEquals(true, size > 1);
            socket.close();
        }catch(ConnectException e){
            assertEquals(false, true);
        }catch(Exception e){
            assertEquals(false, true);
            e.printStackTrace();
        }
    }
    @Test
    void getNameReturnsName(){
        Player player = new Player("TEST");
        assertEquals(true, player.getName().equals("TEST"));
    }

    @Test
    void isBlockDetectsBlock(){
        BlocksMap bm = new BlocksMap(10);
        bm.fillBlock(1,1,Boolean.TRUE);
        assertEquals(true, bm.isBlock(1,1));
    }
}