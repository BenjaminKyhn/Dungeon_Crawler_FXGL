import com.almasb.fxgl.entity.Entity;

import java.util.ArrayList;

import static com.almasb.fxgl.dsl.FXGL.getGameWorld;

public abstract class DungeonLevel {
    private int playerX;
    private int playerY;
    private String levelName;
    private ArrayList<String> enemyNames = new ArrayList<String>();
    private ArrayList<Integer> enemyX = new ArrayList<Integer>();
    private ArrayList<Integer> enemyY = new ArrayList<Integer>();

    private ArrayList<Integer> chestLoot = new ArrayList<>();
    private boolean trapActivated;

    private ArrayList<Entity> chests = new ArrayList();
    private ArrayList<Entity> trollTrapEnemies = new ArrayList();
    private ArrayList<Entity> dragonTrapEnemies = new ArrayList();
    private boolean doorOpened;
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

    public boolean isTrapActivated() {
        return trapActivated;
    }

    public void setTrapActivated(boolean trapActivated) {
        this.trapActivated = trapActivated;
    }

    public ArrayList<Entity> getChests() {
        return chests;
    }

    public ArrayList<Entity> getTrollTrapEnemies() {
        return trollTrapEnemies;
    }

    public ArrayList<Entity> getDragonTrapEnemies() {
        return dragonTrapEnemies;
    }

    public void spawnEnemies(){
    }

    public void spawnDragons(){
    }

    public void spawnBoss(){
    }

    public void spawnSpikes(){
    }

    public void spawnTrapSpikes(){
    }

    public void spawnTrapWalls(){
    }

    public void openDoor(){
    }

    public void spawnChests(){}

    public ArrayList<Integer> getChestLoot() {
        return chestLoot;
    }
}
