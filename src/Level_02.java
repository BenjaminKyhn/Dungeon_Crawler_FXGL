import javafx.util.Duration;

import java.util.ArrayList;

import static com.almasb.fxgl.dsl.FXGL.getGameWorld;
import static com.almasb.fxgl.dsl.FXGL.runOnce;

public class Level_02 extends DungeonLevel {
    private int playerX = 2358;
    private int playerY = 2944;
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
        getGameWorld().spawn("demon", 2240, 2688);
        getGameWorld().spawn("goblin", 2112, 2752);
        getGameWorld().spawn("demon", 960, 2880);
        getGameWorld().spawn("goblin", 768, 2816);
        getGameWorld().spawn("demon", 896, 1600);
        getGameWorld().spawn("goblin", 768, 1472);
        getGameWorld().spawn("demon", 2112, 1600);
        getGameWorld().spawn("goblin", 2304, 1472);

        getGameWorld().spawn("troll", 2112, 448);
        getGameWorld().spawn("ogre", 1536, 704);
        getGameWorld().spawn("goblin", 192, 192);
        getGameWorld().spawn("dragon", 192, 576);
        getGameWorld().spawn("dragon", 896, 960);
        getGameWorld().spawn("dragon", 768, 704);
    }

    public void spawnGoblins(){
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                getGameWorld().spawn("goblin", 1344 + (2*j), 1984 + (2*i));
            }
        }
        runOnce(() ->{getGameWorld().spawn("goblin", 1216, 2176);}, Duration.seconds(0.5));
        runOnce(() ->{getGameWorld().spawn("goblin", 1984, 2112);}, Duration.seconds(1));
        runOnce(() ->{getGameWorld().spawn("goblin", 1280, 2544);}, Duration.seconds(1.5));
    }
}
