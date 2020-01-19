import java.util.ArrayList;

import static com.almasb.fxgl.dsl.FXGL.getGameWorld;

public class Level_01 extends DungeonLevel {
    private int playerX = 150;
    private int playerY = 150;
    private String levelName = "dungeon1.tmx";
    private ArrayList<String> enemyNames = new ArrayList<String>();
    private ArrayList<Integer> enemyX = new ArrayList<Integer>();
    private ArrayList<Integer> enemyY = new ArrayList<Integer>();

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
        getGameWorld().spawn("enemy", 448, 384);
        getGameWorld().spawn("goblin", 192, 384);
        getGameWorld().spawn("enemy", 320, 896);
        getGameWorld().spawn("goblin", 640, 1088);
        getGameWorld().spawn("enemy", 1152, 1024);
    }
}
