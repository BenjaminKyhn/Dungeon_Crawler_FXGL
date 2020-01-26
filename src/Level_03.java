import com.almasb.fxgl.entity.Entity;
import javafx.util.Duration;

import java.util.ArrayList;

import static com.almasb.fxgl.dsl.FXGL.*;

public class Level_03 extends DungeonLevel {
//    private int playerX = 320;
//    private int playerY = 3008;
    private int playerX = 1344;
    private int playerY = 832;
//    private int playerX = 4032;
//    private int playerY = 1728;
//    private int playerX = 1792;
//    private int playerY = 2368;
    private String levelName = "dungeon3.tmx";
    private ArrayList<String> enemyNames = new ArrayList<String>();
    private ArrayList<Integer> enemyX = new ArrayList<Integer>();
    private ArrayList<Integer> enemyY = new ArrayList<Integer>();
    private ArrayList<Entity> trollTrapEnemies = new ArrayList();
    private ArrayList<Entity> dragonTrapEnemies = new ArrayList();
    Entity boss;
    private boolean doorOpened;

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

    public ArrayList<Entity> getTrollTrapEnemies() {
        return trollTrapEnemies;
    }

    public ArrayList<Entity> getDragonTrapEnemies() {
        return dragonTrapEnemies;
    }

    public void spawnEnemies(){
        getGameWorld().spawn("demon", 3328, 2240);
        getGameWorld().spawn("demon", 3584, 1600);
        getGameWorld().spawn("demon", 3776, 2048);
        getGameWorld().spawn("demon", 4224, 1728);
        getGameWorld().spawn("ogre", 4480, 3008);
        Entity dragon1 = getGameWorld().spawn("dragon", 1280, 2304);
        Entity dragon2 = getGameWorld().spawn("dragon", 1344, 2752);
        Entity dragon3 = getGameWorld().spawn("dragon", 1472, 2496);
        Entity dragon4 = getGameWorld().spawn("dragon", 1792, 2368);
        Entity dragon5 = getGameWorld().spawn("dragon", 1792, 2688);
        dragonTrapEnemies.add(dragon1);
        dragonTrapEnemies.add(dragon2);
        dragonTrapEnemies.add(dragon3);
        dragonTrapEnemies.add(dragon4);
        dragonTrapEnemies.add(dragon5);

        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                int finalJ = j;
                int finalI = i;
                runOnce(() ->{getGameWorld().spawn("imp", 64*(54+(2* finalJ)), 64*(23+(2* finalI)));}, Duration.seconds(i/(j+1)));
            }
        }
    }

    public void spawnSpikes(){
        if(!DungeonCrawlerApp.spikesSpawned){
            DungeonCrawlerApp.spikesSpawned = true;
            Entity spikes0 = getGameWorld().spawn("spikes", 576, 2944);
            Entity spikes1 = getGameWorld().spawn("spikes", 4160, 960);
            Entity spikes2 = getGameWorld().spawn("spikes", 4224, 960);
            Entity spikes3 = getGameWorld().spawn("spikes", 4288, 960);
            runOnce(() ->{getGameWorld().getEntitiesByType(DungeonCrawlerType.SPIKES).forEach(Entity::removeFromWorld);}, Duration.seconds(2));
            runOnce(() ->{DungeonCrawlerApp.spikesSpawned = false;}, Duration.seconds(2.75));
        }
    }

    public void spawnTrapSpikes(){
        if(!DungeonCrawlerApp.trapSpikesSpawned){
            DungeonCrawlerApp.trapSpikesSpawned = true;
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
            runOnce(() ->{DungeonCrawlerApp.trapSpikesSpawned = false;}, Duration.seconds(2.75));
        }
    }

    public void spawnTrapWalls(){
        Entity rightTrapWall1 = getGameWorld().spawn("righttrapwall", 1280, 704);
        play("walldown.wav");
        DungeonCrawlerApp.freezeInput = true;
        runOnce(() ->{DungeonCrawlerApp.freezeInput = false;}, Duration.seconds(2));
        spawnTrolls();
    }

    public void openDoor(){
        if (!(boss == null) && boss.getComponent(BossComponent.class).isDead() && !doorOpened){
            getGameWorld().getEntitiesByType(DungeonCrawlerType.DOOR).forEach(Entity::removeFromWorld);
            play("dooropen2.wav");
            doorOpened = true;
        }
    }

    public void spawnTrolls(){
        runOnce(() ->{Entity troll1 = getGameWorld().spawn("troll", 1088, 640); play("trollspawn.wav"); trollTrapEnemies.add(troll1);}, Duration.seconds(0.5));
        runOnce(() ->{Entity troll2 = getGameWorld().spawn("troll", 896, 704); play("trollspawn.wav"); trollTrapEnemies.add(troll2);}, Duration.seconds(1.0));
        runOnce(() ->{Entity troll3 = getGameWorld().spawn("troll", 896, 960); play("trollspawn.wav"); trollTrapEnemies.add(troll3);}, Duration.seconds(1.5));
        runOnce(() ->{Entity troll4 = getGameWorld().spawn("troll", 640, 1024); play("trollspawn.wav"); trollTrapEnemies.add(troll4);}, Duration.seconds(2.0));
        runOnce(() ->{Entity troll5 = getGameWorld().spawn("troll", 640, 788); play("trollspawn.wav"); trollTrapEnemies.add(troll5);}, Duration.seconds(2.5));
        runOnce(() ->{Entity troll6 = getGameWorld().spawn("troll", 576, 640); play("trollspawn.wav"); trollTrapEnemies.add(troll6);}, Duration.seconds(3.0));
    }

    public void spawnDragons(){
    }

    public void spawnBoss(){
        DungeonCrawlerApp.freezeInput = true;
        runOnce(() ->{DungeonCrawlerApp.freezeInput = false;}, Duration.seconds(2));
        boss = getGameWorld().spawn("boss", 2250, 500);
        play("hornhapy.wav");
        getGameScene().getViewport().shakeRotational(1.0);
    }
}
