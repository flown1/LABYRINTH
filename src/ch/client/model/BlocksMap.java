package ch.client.model;


public class BlocksMap {
    private Boolean map[][];
    private int size;

    public BlocksMap(int size){
        this.size = size;
        initMap();
        map[1][1] = Boolean.TRUE;
    }
    private void initMap(){
        map = new Boolean[size][size];
        for(int i = 0; i < size; i++)
            for(int j = 0; j < size; j++)
                this.map[i][j] = Boolean.FALSE;
    }
    public void fillBlock(int x, int y, Boolean val){
        map[x][y] = val;
    }
    public Boolean isBlock(int x, int y){
        if(map[x][y] == Boolean.TRUE)
            return Boolean.TRUE;
        else return Boolean.FALSE;
    }
}
