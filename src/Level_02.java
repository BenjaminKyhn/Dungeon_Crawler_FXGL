import java.util.ArrayList;

import static com.almasb.fxgl.dsl.FXGL.getGameWorld;

public class Level_02 extends DungeonLevel {
    private int playerX = 1600;
    private int playerY = 1600;
    private String levelName = "dungeon2.tmx";
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
        getGameWorld().spawn("enemy", 256, 256);
        getGameWorld().spawn("goblin", 320, 320);
        getGameWorld().spawn("enemy", 1472, 256);
        getGameWorld().spawn("goblin", 1600, 256);
        getGameWorld().spawn("enemy", 256, 1600);
        getGameWorld().spawn("goblin", 256, 1728);
        getGameWorld().spawn("enemy", 1472, 1600);
        getGameWorld().spawn("goblin", 1600, 1728);
    }
}
