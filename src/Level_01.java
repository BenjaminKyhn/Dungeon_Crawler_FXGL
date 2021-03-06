import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;

import java.util.ArrayList;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGL.play;

public class Level_01 extends DungeonLevel {
    private int playerX = 640;
    private int playerY = 192;
    private String levelName = "dungeon1.tmx";
    private ArrayList<String> enemyNames = new ArrayList<String>();
    private ArrayList<Integer> enemyX = new ArrayList<Integer>();
    private ArrayList<Integer> enemyY = new ArrayList<Integer>();
    private ArrayList<Entity> chests = new ArrayList();
    private ArrayList<Integer> chestLoot = new ArrayList<>();
    private Entity chest1;
    private Entity chest2;
    private Entity chest3;

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
        getGameWorld().spawn("demon", 1216, 320);
        getGameWorld().spawn("goblin", 704, 448);
        getGameWorld().spawn("demon", 1152, 896);
        getGameWorld().spawn("goblin", 832, 1024);
        getGameWorld().spawn("demon", 1856, 768);
        getGameWorld().spawn("dragon", 192, 1216);
        getGameWorld().spawn("goblin", 1856, 1280);
        getGameWorld().spawn("dragon", 2432, 1664);
        getGameWorld().spawn("demon", 448, 1600);
        getGameWorld().spawn("goblin", 768, 1408);
        getGameWorld().spawn("demon", 1556, 1600);
        getGameWorld().spawn("dragon", 1792, 256);
        getGameWorld().spawn("goblin", 2112, 192);
        getGameWorld().spawn("demon", 2368, 896);
    }

    public void spawnChests(){
        chest1 = getGameWorld().spawn("chest", 128, 768);
        chest2 = getGameWorld().spawn("chest", 2048, 1472);
        chest3 = getGameWorld().spawn("chest", 1984, 192);
        chestLoot.add(5);
    }

    public ArrayList<Integer> getChestLoot() {
        return chestLoot;
    }
}
