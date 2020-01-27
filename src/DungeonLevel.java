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
    private boolean trapActivated;
    private ArrayList<Entity> trollTrapEnemies = new ArrayList();
    private ArrayList<Entity> dragonTrapEnemies = new ArrayList();
    private boolean doorOpened;
    private int chest1;

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

    public boolean isTrapActivated() {
        return trapActivated;
    }

    public void setTrapActivated(boolean trapActivated) {
        this.trapActivated = trapActivated;
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
}
