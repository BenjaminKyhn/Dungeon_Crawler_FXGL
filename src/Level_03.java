import com.almasb.fxgl.entity.Entity;
import javafx.util.Duration;

import java.util.ArrayList;

import static com.almasb.fxgl.dsl.FXGL.*;

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
            Entity spikes0 = getGameWorld().spawn("spikes", 576, 2944);
            Entity spikes1 = getGameWorld().spawn("spikes", 4160, 960);
            Entity spikes2 = getGameWorld().spawn("spikes", 4224, 960);
            Entity spikes3 = getGameWorld().spawn("spikes", 4288, 960);
            Entity spikes4 = getGameWorld().spawn("spikes", 4160, 1280);
            Entity spikes5 = getGameWorld().spawn("spikes", 4224, 1280);
            Entity spikes6 = getGameWorld().spawn("spikes", 4288, 1280);
            Entity spikes7 = getGameWorld().spawn("spikes", 4160, 1344);
            Entity spikes8 = getGameWorld().spawn("spikes", 4224, 1344);
            Entity spikes9 = getGameWorld().spawn("spikes", 4288, 1344);
            Entity spikes10 = getGameWorld().spawn("spikes", 4160, 1408);
            Entity spikes11 = getGameWorld().spawn("spikes", 4224, 1408);
            Entity spikes12 = getGameWorld().spawn("spikes", 4288, 1408);
            runOnce(() ->{getGameWorld().getEntitiesByType(DungeonCrawlerType.SPIKES).forEach(Entity::removeFromWorld);}, Duration.seconds(2));
            runOnce(() ->{DungeonCrawlerApp.spikesSpawned = false;}, Duration.seconds(2.75));
        }
    }

    public void spawnTrapWalls(){
        Entity rightTrapWall1 = getGameWorld().spawn("righttrapwall", 1280, 704);
        play("walldown.wav");
    }
}
