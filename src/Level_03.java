import com.almasb.fxgl.entity.Entity;
import javafx.util.Duration;

import java.util.ArrayList;

import static com.almasb.fxgl.dsl.FXGL.getGameWorld;
import static com.almasb.fxgl.dsl.FXGL.runOnce;

public class Level_03 extends DungeonLevel {
    private int playerX = 320;
    private int playerY = 3008;
    private String levelName = "dungeon3.tmx";
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
        getGameWorld().spawn("ogre", 2560, 1000);
    }

    public void spawnSpikes(){
        if(!DungeonCrawlerApp.spikesSpawned){
            DungeonCrawlerApp.spikesSpawned = true;
            Entity spikes1 = getGameWorld().spawn("spikes", 576, 2944);
            runOnce(spikes1::removeFromWorld, Duration.seconds(2));
            runOnce(() ->{DungeonCrawlerApp.spikesSpawned = false;}, Duration.seconds(3));
        }
    }
}
