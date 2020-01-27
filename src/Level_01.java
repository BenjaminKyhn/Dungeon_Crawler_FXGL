import java.util.ArrayList;

import static com.almasb.fxgl.dsl.FXGL.getGameWorld;

public class Level_01 extends DungeonLevel {
    private int playerX = 640;
    private int playerY = 192;
    private String levelName = "dungeon1.tmx";
    private ArrayList<String> enemyNames = new ArrayList<String>();
    private ArrayList<Integer> enemyX = new ArrayList<Integer>();
    private ArrayList<Integer> enemyY = new ArrayList<Integer>();
    private int chest1 = 10;

    public ArrayList<String> getEnemyNames(){
        return enemyNames;
    }

    public ArrayList<Integer> getEnemyX(){
        return enemyX;
    }

    public ArrayList<Integer> getEnemyY(){
        return enemyY;
    }

    public void addEnemies(){
    }

    public int getPlayerX() {
        return playerX;
    }

    public int getPlayerY() {
        return playerY;
    }

    public String getLevelName() {
        return levelName;
    }

    public int getNumberOfEnemies(){
        return enemyNames.size();
    }

    public String getEnemyname(int index){
        return enemyNames.get(index);
    }

    public int getEnemyX(int index){
        return enemyX.get(index);
    }

    public int getEnemyY(int index){
        return enemyY.get(index);
    }

    public void spawnEnemies(){
        getGameWorld().spawn("demon", 1088, 448);
        getGameWorld().spawn("goblin", 704, 448);
        getGameWorld().spawn("demon", 1152, 896);
        getGameWorld().spawn("goblin", 832, 1024);
        getGameWorld().spawn("demon", 1600, 960);
        getGameWorld().spawn("dragon", 192, 1216);
    }
}
