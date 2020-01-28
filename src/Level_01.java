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


    public Entity getChest1() {
        return chest1;
    }

    public Entity getChest2() {
        return chest2;
    }

    public ArrayList<Entity> getChests() {
        return chests;
    }

    public void spawnEnemies(){
        getGameWorld().spawn("demon", 1088, 448);
        getGameWorld().spawn("goblin", 704, 448);
        getGameWorld().spawn("demon", 1152, 896);
        getGameWorld().spawn("goblin", 832, 1024);
        getGameWorld().spawn("demon", 1600, 960);
        getGameWorld().spawn("dragon", 192, 1216);
    }

    public void spawnChests(){
        chest1 = getGameWorld().spawn("chest", 128, 768);
        chests.add(chest1);
        chestLoot.add(5);
        chest2 = getGameWorld().spawn("chest", 250, 768);
        chests.add(chest2);
        chestLoot.add(10);
    }

    public ArrayList<Integer> getChestLoot() {
        return chestLoot;
    }
}
