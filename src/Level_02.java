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

    public void spawnGoblins(){
//        getGameWorld().spawn("goblin", 768, 768);
//        getGameWorld().spawn("goblin", 896, 758);
//        getGameWorld().spawn("goblin", 1024, 768);
//        getGameWorld().spawn("goblin", 1152, 768);
//        getGameWorld().spawn("goblin", 768, 896);
//        getGameWorld().spawn("goblin", 896, 896);
//        getGameWorld().spawn("goblin", 1024, 896);
//        getGameWorld().spawn("goblin", 1152, 896);
//        getGameWorld().spawn("goblin", 768, 1024);
//        getGameWorld().spawn("goblin", 896, 1024);
//        getGameWorld().spawn("goblin", 1024, 1024);
//        getGameWorld().spawn("goblin", 1152, 1024);
//        getGameWorld().spawn("goblin", 768, 1152);
//        getGameWorld().spawn("goblin", 896, 1152);
//        getGameWorld().spawn("goblin", 1024, 1152);
//        getGameWorld().spawn("goblin", 1152, 1152);
//
//        getGameWorld().spawn("goblin", 832, 832);
//        getGameWorld().spawn("goblin", 964, 832);
//        getGameWorld().spawn("goblin", 1088, 832);
//        getGameWorld().spawn("goblin", 1216, 832);
//        getGameWorld().spawn("goblin", 832, 964);
//        getGameWorld().spawn("goblin", 964, 964);
//        getGameWorld().spawn("goblin", 1088, 964);
//        getGameWorld().spawn("goblin", 1216, 964);
//        getGameWorld().spawn("goblin", 832, 1088);
//        getGameWorld().spawn("goblin", 964, 1088);
//        getGameWorld().spawn("goblin", 1088, 1088);
        getGameWorld().spawn("goblin", 1216, 1088);
    }
}
