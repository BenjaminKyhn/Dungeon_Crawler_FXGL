import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.texture.Texture;
import javafx.geometry.Rectangle2D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;

public class DungeonCrawlerApp extends GameApplication {

    private Entity player;
    private Entity weapon;
    private static boolean isMoving;
    private static boolean isAttacking = false;
    private static boolean weaponFacingRight = true;
    public static boolean freezeInput = false;
    public static boolean spikesSpawned;
    public static boolean trapSpikesSpawned;
    private boolean bottomWallTouched;
    private boolean topWallTouched;
    private boolean rightWallTouched;
    private boolean leftWallTouched;
    private boolean rightTrapWallTouched;
    private boolean topDoorTouched;
    private boolean bottomDoorTouched;
    private boolean doorOpened;
    private boolean stairsDiscovered;
    private boolean redSwitchActivated;
    private boolean blueSwitchActivated;
    private boolean healing;
    private boolean bossActivated;
    private boolean chestCollision;
    private boolean shopActive = false;
    private ArrayList<String> weapons = new ArrayList<>();
    private List<DungeonLevel> levels = new ArrayList<>();
    private int levelNumber = 1;
    private int greatswordPrice = 100;
    private int healthPrice = 50;
    private int startingGold = 0;
    private int maxHP = 30;
    private int currentHP;
    private Texture heart1;
    private Texture heart2;
    private Texture heart3;
    private Texture heart4;
    private Texture heart5;
    private Texture shopUI;

    @Override
    protected void initSettings(GameSettings gameSettings) {
        gameSettings.setWidth(1280);
        gameSettings.setHeight(720);
//        gameSettings.setWidth(15 * 64);
//        gameSettings.setHeight(10 * 64);
        gameSettings.setTitle("Dungeon Crawler");
        gameSettings.setVersion("0.1");
//        gameSettings.setFullScreenAllowed(true);
//        gameSettings.setFullScreenFromStart(true);
//        gameSettings.setMenuEnabled(true);
//        gameSettings.setDeveloperMenuEnabled(true);
    }

    @Override
    protected void onPreInit() {
        getSettings().setGlobalSoundVolume(0.2);
        getSettings().setGlobalMusicVolume(0.2);

        loopBGM("enchanted dungeon.mp3");
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("gold", startingGold);
    }

    @Override
    protected void initGame() {
        /** Add all the levels to the Dungeon Level ArrayList */
        levels.add(new Level_01());
        levels.add(new Level_02());
        levels.add(new Level_03());

        /** Load map */
        FXGL.getGameWorld().addEntityFactory(new DungeonCrawlerFactory());
        getGameScene().setBackgroundColor(Color.rgb(34,34,34));
        FXGL.setLevelFromMap(getCurrentLevel().getLevelName());

        /** Spawn the player */
        player = getGameWorld().spawn("player", getCurrentLevel().getPlayerX(), getCurrentLevel().getPlayerY());
        weapon = getGameWorld().spawn("sword", getCurrentLevel().getPlayerX() + 48, getCurrentLevel().getPlayerY());
//        weapon = getGameWorld().spawn("greatsword", getCurrentLevel().getPlayerX() + 48, getCurrentLevel().getPlayerY()-25);
//        weapons.add("greatsword");
        player.setZ(1);
        weapon.setZ(1);

        /** Spawn initial enemies */
        getCurrentLevel().spawnEnemies();
        getCurrentLevel().spawnChests();

        /** Add a camera that follows the player */
//        getGameScene().getViewport().setBounds(-250, 0, 3000, getAppHeight()); //sets a boundary for the camera
        getGameScene().getViewport().bindToEntity(player, getAppWidth() / 2.0, getAppHeight() / 2.0); //adds a camera and binds it to the player
    }

    @Override
    protected void initUI() {
        Texture coin = texture("coin.png").toAnimatedTexture(4, Duration.seconds(1)).loop();
        heart1 = texture("heart.png", 44, 40);
        heart2 = texture("heart.png", 44, 40);
        heart3 = texture("heart.png", 44, 40);

        var scoreText = getUIFactory().newText("", Color.GOLD, 38.0);
        scoreText.setStrokeWidth(2.5);
        scoreText.setStroke(Color.color(0.0, 0.0, 0.0, 0.56));
        scoreText.textProperty().bind(getip("gold").asString());

        addUINode(scoreText, 295, 49);
        addUINode(coin, 260, 21);
        addUINode(heart1, 15, 15);
        addUINode(heart2, 62, 15);
        addUINode(heart3, 109, 15);
    }

    @Override
    public void onUpdate(double tpf) {
        /** onUpdate methods that are always relevant*/
        updateUI();

        /** onUpdate methods specific for Level_02*/
        if (getCurrentLevel().equals((levels.get(1)))) {
            openDoor();
            showStairs();
        }

        /** onUpdate methods specific for Level_03*/
        if (getCurrentLevel().equals(levels.get(2))) {
            getCurrentLevel().spawnSpikes();
            spawnBoss();
            getCurrentLevel().openDoor();

            if (getCurrentLevel().isTrapActivated()) {
                removeTrapWall();
            }
            if (!redSwitchActivated) {
                getCurrentLevel().spawnTrapSpikes();
            }
            if (blueSwitchActivated) {
                getGameWorld().getEntitiesInRange(new Rectangle2D(896, 1152, 1, 1)).forEach(Entity::removeFromWorld);
                runOnce(() -> {
                    play("dooropen2.wav");
                }, Duration.seconds(1));
                bossActivated = true;
                blueSwitchActivated = false;
            }
        }
    }

    private void updateUI() {
        if (player.getComponent(PlayerComponent.class).getHp() <= 0) {
            removeUINode(heart1);
            removeUINode(heart2);
            removeUINode(heart3);
            addUINode(heart2, 62, 15);
            addUINode(heart3, 109, 15);
            heart1 = texture("heart_empty.png", 44, 40);
            heart2 = texture("heart_empty.png", 44, 40);
            heart3 = texture("heart_empty.png", 44, 40);
            addUINode(heart1, 15, 15);
            addUINode(heart2, 62, 15);
            addUINode(heart3, 109, 15);
            if (heart4 != null) {
                heart4 = texture("heart_empty.png", 44, 40);
                addUINode(heart4, 156, 15);
            }
            if (heart5 != null) {
                heart5 = texture("heart_empty.png", 44, 40);
                addUINode(heart5, 203, 15);
            }
        } else if ((player.getComponent(PlayerComponent.class).getHp() > 0) && (player.getComponent(PlayerComponent.class).getHp() < 10)) {
            removeUINode(heart1);
            removeUINode(heart2);
            removeUINode(heart3);
            heart1 = texture("heart_half_full.png", 44, 40);
            heart2 = texture("heart_empty.png", 44, 40);
            heart3 = texture("heart_empty.png", 44, 40);
            addUINode(heart1, 15, 15);
            addUINode(heart2, 62, 15);
            addUINode(heart3, 109, 15);
            if (heart4 != null) {
                heart4 = texture("heart_empty.png", 44, 40);
                addUINode(heart4, 156, 15);
            }
            if (heart5 != null) {
                heart5 = texture("heart_empty.png", 44, 40);
                addUINode(heart5, 203, 15);
            }
        } else if ((player.getComponent(PlayerComponent.class).getHp() > 9) && (player.getComponent(PlayerComponent.class).getHp() < 15)) {
            removeUINode(heart1);
            removeUINode(heart2);
            removeUINode(heart3);
            heart1 = texture("heart.png", 44, 40);
            heart2 = texture("heart_empty.png", 44, 40);
            heart3 = texture("heart_empty.png", 44, 40);
            addUINode(heart1, 15, 15);
            addUINode(heart2, 62, 15);
            addUINode(heart3, 109, 15);
            if (heart4 != null) {
                heart4 = texture("heart_empty.png", 44, 40);
                addUINode(heart4, 156, 15);
            }
            if (heart5 != null) {
                heart5 = texture("heart_empty.png", 44, 40);
                addUINode(heart5, 203, 15);
            }
        } else if ((player.getComponent(PlayerComponent.class).getHp() > 14) && (player.getComponent(PlayerComponent.class).getHp() < 20)) {
            removeUINode(heart1);
            removeUINode(heart2);
            removeUINode(heart3);
            heart1 = texture("heart.png", 44, 40);
            heart2 = texture("heart_half_full.png", 44, 40);
            heart3 = texture("heart_empty.png", 44, 40);
            addUINode(heart1, 15, 15);
            addUINode(heart2, 62, 15);
            addUINode(heart3, 109, 15);
            if (heart4 != null) {
                heart4 = texture("heart_empty.png", 44, 40);
                addUINode(heart4, 156, 15);
            }
            if (heart5 != null) {
                heart5 = texture("heart_empty.png", 44, 40);
                addUINode(heart5, 203, 15);
            }
        } else if ((player.getComponent(PlayerComponent.class).getHp() > 19) && (player.getComponent(PlayerComponent.class).getHp() < 25)) {
            removeUINode(heart1);
            removeUINode(heart2);
            removeUINode(heart3);
            heart1 = texture("heart.png", 44, 40);
            heart2 = texture("heart.png", 44, 40);
            heart3 = texture("heart_empty.png", 44, 40);
            addUINode(heart1, 15, 15);
            addUINode(heart2, 62, 15);
            addUINode(heart3, 109, 15);
            if (heart4 != null) {
                heart4 = texture("heart_empty.png", 44, 40);
                addUINode(heart4, 156, 15);
            }
            if (heart5 != null) {
                heart5 = texture("heart_empty.png", 44, 40);
                addUINode(heart5, 203, 15);
            }
        } else if ((player.getComponent(PlayerComponent.class).getHp() > 24) && (player.getComponent(PlayerComponent.class).getHp() < 30)) {
            removeUINode(heart1);
            removeUINode(heart2);
            removeUINode(heart3);
            heart1 = texture("heart.png", 44, 40);
            heart2 = texture("heart.png", 44, 40);
            heart3 = texture("heart_half_full.png", 44, 40);
            addUINode(heart1, 15, 15);
            addUINode(heart2, 62, 15);
            addUINode(heart3, 109, 15);
            if (heart4 != null) {
                heart4 = texture("heart_empty.png", 44, 40);
                addUINode(heart4, 156, 15);
            }
            if (heart5 != null) {
                heart5 = texture("heart_empty.png", 44, 40);
                addUINode(heart5, 203, 15);
            }
        } else if ((player.getComponent(PlayerComponent.class).getHp() > 29) && (player.getComponent(PlayerComponent.class).getHp() < 35)) {
            removeUINode(heart1);
            removeUINode(heart2);
            removeUINode(heart3);
            heart1 = texture("heart.png", 44, 40);
            heart2 = texture("heart.png", 44, 40);
            heart3 = texture("heart.png", 44, 40);
            addUINode(heart1, 15, 15);
            addUINode(heart2, 62, 15);
            addUINode(heart3, 109, 15);
            if (heart4 != null) {
                heart4 = texture("heart_empty.png", 44, 40);
                addUINode(heart4, 156, 15);
            }
            if (heart5 != null) {
                heart5 = texture("heart_empty.png", 44, 40);
                addUINode(heart5, 203, 15);
            }
        } else if ((player.getComponent(PlayerComponent.class).getHp() > 34) && (player.getComponent(PlayerComponent.class).getHp() < 40)) {
            removeUINode(heart1);
            removeUINode(heart2);
            removeUINode(heart3);
            removeUINode(heart4);
            heart1 = texture("heart.png", 44, 40);
            heart2 = texture("heart.png", 44, 40);
            heart3 = texture("heart.png", 44, 40);
            heart4 = texture("heart_half_full.png", 44, 40);
            addUINode(heart1, 15, 15);
            addUINode(heart2, 62, 15);
            addUINode(heart3, 109, 15);
            addUINode(heart4, 156, 15);
            if (heart5 != null) {
                heart5 = texture("heart_empty.png", 44, 40);
                addUINode(heart5, 203, 15);
            }
        } else if ((player.getComponent(PlayerComponent.class).getHp() > 39) && (player.getComponent(PlayerComponent.class).getHp() < 45)) {
            removeUINode(heart1);
            removeUINode(heart2);
            removeUINode(heart3);
            removeUINode(heart4);
            heart1 = texture("heart.png", 44, 40);
            heart2 = texture("heart.png", 44, 40);
            heart3 = texture("heart.png", 44, 40);
            heart4 = texture("heart.png", 44, 40);
            addUINode(heart1, 15, 15);
            addUINode(heart2, 62, 15);
            addUINode(heart3, 109, 15);
            addUINode(heart4, 156, 15);
            if (heart5 != null) {
                heart5 = texture("heart_empty.png", 44, 40);
                addUINode(heart5, 203, 15);
            }
        } else if ((player.getComponent(PlayerComponent.class).getHp() > 44) && (player.getComponent(PlayerComponent.class).getHp() < 50)) {
            removeUINode(heart1);
            removeUINode(heart2);
            removeUINode(heart3);
            removeUINode(heart4);
            removeUINode(heart5);
            heart1 = texture("heart.png", 44, 40);
            heart2 = texture("heart.png", 44, 40);
            heart3 = texture("heart.png", 44, 40);
            heart4 = texture("heart.png", 44, 40);
            heart5 = texture("heart_half_full.png", 44, 40);
            addUINode(heart1, 15, 15);
            addUINode(heart2, 62, 15);
            addUINode(heart3, 109, 15);
            addUINode(heart4, 156, 15);
            addUINode(heart5, 203, 15);
        } else if ((player.getComponent(PlayerComponent.class).getHp() > 49)) {
            removeUINode(heart1);
            removeUINode(heart2);
            removeUINode(heart3);
            removeUINode(heart4);
            removeUINode(heart5);
            heart1 = texture("heart.png", 44, 40);
            heart2 = texture("heart.png", 44, 40);
            heart3 = texture("heart.png", 44, 40);
            heart4 = texture("heart.png", 44, 40);
            heart5 = texture("heart.png", 44, 40);
            addUINode(heart1, 15, 15);
            addUINode(heart2, 62, 15);
            addUINode(heart3, 109, 15);
            addUINode(heart4, 156, 15);
            addUINode(heart5, 203, 15);
        }
    }

    @Override
    protected void initInput() {
        FXGL.getInput().addAction(new UserAction("Use") {
            @Override
            protected void onActionBegin() {
                lootChest();

                getGameWorld().getEntitiesByType(DungeonCrawlerType.BUTTON)
                        .stream()
                        .filter(btn -> player.isColliding(btn))
                        .forEach(btn -> {
                            if (!healing) {
                                healing = true;
                                player.getComponent(PlayerComponent.class).restoreHP();
                                play("sp_heal.wav");
                                runOnce(() -> {
                                    healing = false;
                                }, Duration.seconds(1));
                            }
                        });

                getGameWorld().getEntitiesByType(DungeonCrawlerType.SHOP)
                        .stream()
                        .filter(btn -> player.isColliding(btn))
                        .forEach(btn -> {
                            if (!shopActive) {
                                play("femhapy.wav");
                                shopUI = texture("shop.png");
                                shopUI.setTranslateX(getAppWidth() - shopUI.getWidth());
                                getGameScene().addUINode(shopUI);
                                shopActive = true;
                            } else {
                                getGameScene().removeUINode(shopUI);
                                shopActive = false;
                            }
                        });
            }
        }, KeyCode.E);

        FXGL.getInput().addAction(new UserAction("Left") {
            @Override
            protected void onActionBegin() {
                if (weaponFacingRight && !freezeInput) {
                    weapon.getComponent(WeaponComponent.class).faceLeft();
                    weaponFacingRight = false;
                }
            }

            protected void onAction() {
                if (leftWallTouched || freezeInput) //If player unit collides with right wall,"Move Right" function stops until false.
                    return;
                player.getComponent(PlayerComponent.class).left();
                weapon.getComponent(WeaponComponent.class).left();
                isMoving = true;
            }

            protected void onActionEnd() {
                isMoving = false;
            }
        }, KeyCode.A);

        FXGL.getInput().addAction(new UserAction("Right") {
            @Override
            protected void onActionBegin() {
                if (!weaponFacingRight && !freezeInput) {
                    weapon.getComponent(WeaponComponent.class).faceRight();
                    weaponFacingRight = true;
                }
            }

            protected void onAction() {
                if (rightWallTouched || freezeInput || rightTrapWallTouched) //If player unit collides with right wall,"Move Right" function stops until false.
                    return;

                player.getComponent(PlayerComponent.class).right();
                weapon.getComponent(WeaponComponent.class).right();
                isMoving = true;
            }

            protected void onActionEnd() {
                isMoving = false;
            }
        }, KeyCode.D);

        FXGL.getInput().addAction(new UserAction("Up") {
            @Override
            protected void onAction() {
                if (topWallTouched || topDoorTouched || freezeInput) //If player unit collides with right wall,"Move Right" function stops until false.
                    return;
                player.getComponent(PlayerComponent.class).up();
                weapon.getComponent(WeaponComponent.class).up();
                isMoving = true;
            }

            protected void onActionEnd() {
                isMoving = false;
            }
        }, KeyCode.W);

        FXGL.getInput().addAction(new UserAction("Down") {
            @Override
            protected void onAction() {
                if (bottomWallTouched || bottomDoorTouched || freezeInput) //If player unit collides with right wall,"Move Right" function stops until false.
                    return;
                player.getComponent(PlayerComponent.class).down();
                weapon.getComponent(WeaponComponent.class).down();
                isMoving = true;
            }

            protected void onActionEnd() {
                isMoving = false;
            }
        }, KeyCode.S);

        FXGL.getInput().addAction(new UserAction("Attack") {
            @Override
            protected void onActionBegin() {
                if (shopActive && (getInput().getMouseXUI() > getAppWidth() - shopUI.getWidth()) && (getInput().getMouseXUI() < getAppWidth() - (shopUI.getWidth() / 2)) && (getInput().getMouseYUI() < shopUI.getHeight())) {
                    if (!weapons.contains("greatsword") && getGameState().getInt("gold") >= greatswordPrice) {
                        weapons.add("greatsword");
                        weapon.removeFromWorld();
                        weapon = spawn("greatsword", player.getX() + 48, player.getY() - 25);
                        inc("gold", -greatswordPrice);
                        play("coins.wav");
                    } else {
                        return;
                    }
                }
                if (shopActive && (getInput().getMouseXUI() > getAppWidth() - shopUI.getWidth() / 2) && (getInput().getMouseYUI() < shopUI.getHeight())) {
                    if (heart5 == null && heart4 != null && getGameState().getInt("gold") >= healthPrice) {
                        heart5 = texture("heart_empty.png", 44, 40);
                        addUINode(heart5, 203, 15);
                        inc("gold", -healthPrice);
                        play("coins.wav");
                        maxHP = 50;
                        player.getComponent(PlayerComponent.class).gainMoreHealth(10);
                    }
                    if (heart4 == null && getGameState().getInt("gold") >= healthPrice) {
                        heart4 = texture("heart_empty.png", 44, 40);
                        addUINode(heart4, 156, 15);
                        inc("gold", -healthPrice);
                        play("coins.wav");
                        maxHP = 40;
                        player.getComponent(PlayerComponent.class).gainMoreHealth(10);
                    } else {
                        return;
                    }
                }

                if (!freezeInput) {
                    /** Switch for handling random swing sounds */
                    if (weapons.contains("greatsword")) {
                        int randomSwingSound = (int) (Math.random() * 2);
                        switch (randomSwingSound) {
                            case 0:
                                play("swipe1.wav");
                                break;
                            case 1:
                                play("swipe2.wav");
                                break;
                        }
                    } else {
                        int randomSwingSound = (int) (Math.random() * 3);
                        switch (randomSwingSound) {
                            case 0:
                                play("swing.wav");
                                break;
                            case 1:
                                play("swing2.wav");
                                break;
                            case 2:
                                play("swing3.wav");
                                break;
                        }
                    }


                    /** Directional attacking */
                    if (weaponFacingRight && !isAttacking) {
                        weapon.getComponent(WeaponComponent.class).attackRight();
                        isAttacking = true;
                        runOnce(() -> {
                            weapon.getComponent(WeaponComponent.class).undoAttackRight();
                            isAttacking = false;
                        }, Duration.seconds(0.1));
                    }
                    if (!weaponFacingRight && !isAttacking) {
                        weapon.getComponent(WeaponComponent.class).attackLeft();
                        isAttacking = true;
                        runOnce(() -> {
                            weapon.getComponent(WeaponComponent.class).undoAttackLeft();
                            isAttacking = false;
                        }, Duration.seconds(0.1));
                    }
                }
            }
        }, MouseButton.PRIMARY);
    }

    public static boolean isMoving() {
        return isMoving;
    }

    @Override
    protected void initPhysics() {
        /** Adds unitCollision to right wall and player unit*/
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(DungeonCrawlerType.PLAYER, DungeonCrawlerType.RIGHTWALL) {
            @Override
            protected void onCollisionBegin(Entity player, Entity wall) {
                rightWallTouched = true;
            }

            @Override
            protected void onCollisionEnd(Entity player, Entity wall) {
                rightWallTouched = false;
            }
        });

        /** Adds unitCollision to left wall and player unit*/
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(DungeonCrawlerType.PLAYER, DungeonCrawlerType.LEFTWALL) {
            @Override
            protected void onCollisionBegin(Entity player, Entity wall) {
                leftWallTouched = true;
            }

            @Override
            protected void onCollisionEnd(Entity player, Entity wall) {
                leftWallTouched = false;
            }
        });


        /** Adds unitCollision to top wall and player unit*/
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(DungeonCrawlerType.PLAYER, DungeonCrawlerType.TOPWALL) {
            @Override
            protected void onCollisionBegin(Entity player, Entity wall) {
                topWallTouched = true;
            }

            @Override
            protected void onCollisionEnd(Entity player, Entity wall) {
                topWallTouched = false;
            }
        });

        /** Adds unitCollision to bottom wall and player unit*/
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(DungeonCrawlerType.PLAYER, DungeonCrawlerType.BOTTOMWALL) {
            @Override
            protected void onCollisionBegin(Entity player, Entity wall) {
                bottomWallTouched = true;
            }

            @Override
            protected void onCollisionEnd(Entity player, Entity wall) {
                bottomWallTouched = false;
            }
        });

        /** Adds unitCollision to right void and player unit*/
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(DungeonCrawlerType.PLAYER, DungeonCrawlerType.RIGHTVOID) {
            @Override
            protected void onCollisionBegin(Entity player, Entity wall) {
                rightWallTouched = true;
            }

            @Override
            protected void onCollisionEnd(Entity player, Entity wall) {
                rightWallTouched = false;
            }
        });

        /** Adds unitCollision to left void and player unit*/
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(DungeonCrawlerType.PLAYER, DungeonCrawlerType.LEFTVOID) {
            @Override
            protected void onCollisionBegin(Entity player, Entity wall) {
                leftWallTouched = true;
            }

            @Override
            protected void onCollisionEnd(Entity player, Entity wall) {
                leftWallTouched = false;
            }
        });


        /** Adds unitCollision to top void and player unit*/
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(DungeonCrawlerType.PLAYER, DungeonCrawlerType.TOPVOID) {
            @Override
            protected void onCollisionBegin(Entity player, Entity wall) {
                topWallTouched = true;
            }

            @Override
            protected void onCollisionEnd(Entity player, Entity wall) {
                topWallTouched = false;
            }
        });

        /** Adds unitCollision to bottom void and player unit*/
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(DungeonCrawlerType.PLAYER, DungeonCrawlerType.BOTTOMVOID) {
            @Override
            protected void onCollisionBegin(Entity player, Entity wall) {
                bottomWallTouched = true;
            }

            @Override
            protected void onCollisionEnd(Entity player, Entity wall) {
                bottomWallTouched = false;
            }
        });

        /** Adds unitCollision to door and player unit*/
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(DungeonCrawlerType.PLAYER, DungeonCrawlerType.DOOR) {
            @Override
            protected void onCollisionBegin(Entity player, Entity door) {
                if (door.getY() < player.getY()) {
                    topDoorTouched = true;
                }
                if (door.getY() > player.getY()) {
                    bottomDoorTouched = true;
                }
            }

            @Override
            protected void onCollisionEnd(Entity player, Entity door) {
                topDoorTouched = false;
                bottomDoorTouched = false;
            }
        });

        /** Adds unitCollision to trapwall and player unit*/
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(DungeonCrawlerType.PLAYER, DungeonCrawlerType.RIGHTTRAPWALL) {
            @Override
            protected void onCollisionBegin(Entity player, Entity righttrapwall) {
                rightTrapWallTouched = true;
            }

            @Override
            protected void onCollisionEnd(Entity player, Entity righttrapwall) {
                rightTrapWallTouched = false;
            }
        });

        /** Adds unitCollision to top wall and enemy unit*/
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(DungeonCrawlerType.ENEMY, DungeonCrawlerType.TOPWALL) {
            @Override
            protected void onCollisionBegin(Entity enemy, Entity wall) {
                if (enemy.hasComponent(GoblinComponent.class)) {
                    enemy.getComponent(GoblinComponent.class).setTopWallTouched(true);
                }
                if (enemy.hasComponent(DemonComponent.class)) {
                    enemy.getComponent(DemonComponent.class).setTopWallTouched(true);
                }
                if (enemy.hasComponent(OgreComponent.class)) {
                    enemy.getComponent(OgreComponent.class).setTopWallTouched(true);
                }
                if (enemy.hasComponent(TrollComponent.class)) {
                    enemy.getComponent(TrollComponent.class).setTopWallTouched(true);
                }
                if (enemy.hasComponent(ImpComponent.class)) {
                    enemy.getComponent(ImpComponent.class).setTopWallTouched(true);
                }
                if (enemy.hasComponent(DragonComponent.class)) {
                    enemy.getComponent(DragonComponent.class).setTopWallTouched(true);
                }
                if (enemy.hasComponent(ProjectileComponent.class)) {
                    Entity flame = spawn("flame", enemy.getX(), enemy.getY() - 40);
                    enemy.removeFromWorld();
                    runOnce(flame::removeFromWorld, Duration.seconds(1.5));
                }
            }

            @Override
            protected void onCollisionEnd(Entity enemy, Entity wall) {
                if (enemy.hasComponent(GoblinComponent.class)) {
                    enemy.getComponent(GoblinComponent.class).setTopWallTouched(false);
                }
                if (enemy.hasComponent(DemonComponent.class)) {
                    enemy.getComponent(DemonComponent.class).setTopWallTouched(false);
                }
                if (enemy.hasComponent(OgreComponent.class)) {
                    enemy.getComponent(OgreComponent.class).setTopWallTouched(false);
                }
                if (enemy.hasComponent(TrollComponent.class)) {
                    enemy.getComponent(TrollComponent.class).setTopWallTouched(false);
                }
                if (enemy.hasComponent(ImpComponent.class)) {
                    enemy.getComponent(ImpComponent.class).setTopWallTouched(false);
                }
                if (enemy.hasComponent(DragonComponent.class)) {
                    enemy.getComponent(DragonComponent.class).setTopWallTouched(false);
                }
            }
        });

        /** Adds unitCollision to bottom wall and enemy unit*/
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(DungeonCrawlerType.ENEMY, DungeonCrawlerType.BOTTOMWALL) {
            @Override
            protected void onCollisionBegin(Entity enemy, Entity wall) {
                if (enemy.hasComponent(GoblinComponent.class)) {
                    enemy.getComponent(GoblinComponent.class).setBottomWallTouched(true);
                }
                if (enemy.hasComponent(DemonComponent.class)) {
                    enemy.getComponent(DemonComponent.class).setBottomWallTouched(true);
                }
                if (enemy.hasComponent(OgreComponent.class)) {
                    enemy.getComponent(OgreComponent.class).setBottomWallTouched(true);
                }
                if (enemy.hasComponent(TrollComponent.class)) {
                    enemy.getComponent(TrollComponent.class).setBottomWallTouched(true);
                }
                if (enemy.hasComponent(ImpComponent.class)) {
                    enemy.getComponent(ImpComponent.class).setBottomWallTouched(true);
                }
                if (enemy.hasComponent(DragonComponent.class)) {
                    enemy.getComponent(DragonComponent.class).setBottomWallTouched(true);
                }
                if (enemy.hasComponent(ProjectileComponent.class)) {
                    Entity flame = spawn("flame", enemy.getX(), enemy.getY() + 40);
                    enemy.removeFromWorld();
                    runOnce(flame::removeFromWorld, Duration.seconds(1.5));
                }
            }

            @Override
            protected void onCollisionEnd(Entity enemy, Entity wall) {
                if (enemy.hasComponent(GoblinComponent.class)) {
                    enemy.getComponent(GoblinComponent.class).setBottomWallTouched(false);
                }
                if (enemy.hasComponent(DemonComponent.class)) {
                    enemy.getComponent(DemonComponent.class).setBottomWallTouched(false);
                }
                if (enemy.hasComponent(OgreComponent.class)) {
                    enemy.getComponent(OgreComponent.class).setBottomWallTouched(false);
                }
                if (enemy.hasComponent(TrollComponent.class)) {
                    enemy.getComponent(TrollComponent.class).setBottomWallTouched(true);
                }
                if (enemy.hasComponent(ImpComponent.class)) {
                    enemy.getComponent(ImpComponent.class).setBottomWallTouched(true);
                }
                if (enemy.hasComponent(DragonComponent.class)) {
                    enemy.getComponent(DragonComponent.class).setBottomWallTouched(true);
                }
            }
        });

        /** Adds unitCollision to right wall and enemy unit*/
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(DungeonCrawlerType.ENEMY, DungeonCrawlerType.RIGHTWALL) {
            @Override
            protected void onCollisionBegin(Entity enemy, Entity wall) {
                if (enemy.hasComponent(GoblinComponent.class)) {
                    enemy.getComponent(GoblinComponent.class).setRightWallTouched(true);
                }
                if (enemy.hasComponent(DemonComponent.class)) {
                    enemy.getComponent(DemonComponent.class).setRightWallTouched(true);
                }
                if (enemy.hasComponent(OgreComponent.class)) {
                    enemy.getComponent(OgreComponent.class).setRightWallTouched(true);
                }
                if (enemy.hasComponent(TrollComponent.class)) {
                    enemy.getComponent(TrollComponent.class).setRightWallTouched(true);
                }
                if (enemy.hasComponent(ImpComponent.class)) {
                    enemy.getComponent(ImpComponent.class).setRightWallTouched(true);
                }
                if (enemy.hasComponent(DragonComponent.class)) {
                    enemy.getComponent(DragonComponent.class).setRightWallTouched(true);
                }
                if (enemy.hasComponent(ProjectileComponent.class)) {
                    Entity flame = spawn("flame", enemy.getX() + 40, enemy.getY());
                    enemy.removeFromWorld();
                    runOnce(flame::removeFromWorld, Duration.seconds(1.5));
                }
            }

            @Override
            protected void onCollisionEnd(Entity enemy, Entity wall) {
                if (enemy.hasComponent(GoblinComponent.class)) {
                    enemy.getComponent(GoblinComponent.class).setRightWallTouched(false);
                }
                if (enemy.hasComponent(DemonComponent.class)) {
                    enemy.getComponent(DemonComponent.class).setRightWallTouched(false);
                }
                if (enemy.hasComponent(OgreComponent.class)) {
                    enemy.getComponent(OgreComponent.class).setRightWallTouched(false);
                }
                if (enemy.hasComponent(TrollComponent.class)) {
                    enemy.getComponent(TrollComponent.class).setRightWallTouched(false);
                }
                if (enemy.hasComponent(ImpComponent.class)) {
                    enemy.getComponent(ImpComponent.class).setRightWallTouched(false);
                }
                if (enemy.hasComponent(DragonComponent.class)) {
                    enemy.getComponent(DragonComponent.class).setRightWallTouched(false);
                }
            }
        });

        /** Adds unitCollision to left wall and enemy unit*/
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(DungeonCrawlerType.ENEMY, DungeonCrawlerType.LEFTWALL) {
            @Override
            protected void onCollisionBegin(Entity enemy, Entity wall) {
                if (enemy.hasComponent(GoblinComponent.class)) {
                    enemy.getComponent(GoblinComponent.class).setLeftWallTouched(true);
                }
                if (enemy.hasComponent(DemonComponent.class)) {
                    enemy.getComponent(DemonComponent.class).setLeftWallTouched(true);
                }
                if (enemy.hasComponent(OgreComponent.class)) {
                    enemy.getComponent(OgreComponent.class).setLeftWallTouched(true);
                }
                if (enemy.hasComponent(TrollComponent.class)) {
                    enemy.getComponent(TrollComponent.class).setLeftWallTouched(true);
                }
                if (enemy.hasComponent(ImpComponent.class)) {
                    enemy.getComponent(ImpComponent.class).setLeftWallTouched(true);
                }
                if (enemy.hasComponent(DragonComponent.class)) {
                    enemy.getComponent(DragonComponent.class).setLeftWallTouched(true);
                }
                if (enemy.hasComponent(ProjectileComponent.class)) {
                    Entity flame = spawn("flame", enemy.getX() - 20, enemy.getY());
                    enemy.removeFromWorld();
                    runOnce(flame::removeFromWorld, Duration.seconds(1.5));
                }
            }

            @Override
            protected void onCollisionEnd(Entity enemy, Entity wall) {
                if (enemy.hasComponent(GoblinComponent.class)) {
                    enemy.getComponent(GoblinComponent.class).setLeftWallTouched(false);
                }
                if (enemy.hasComponent(DemonComponent.class)) {
                    enemy.getComponent(DemonComponent.class).setLeftWallTouched(false);
                }
                if (enemy.hasComponent(OgreComponent.class)) {
                    enemy.getComponent(OgreComponent.class).setLeftWallTouched(false);
                }
                if (enemy.hasComponent(TrollComponent.class)) {
                    enemy.getComponent(TrollComponent.class).setLeftWallTouched(false);
                }
                if (enemy.hasComponent(ImpComponent.class)) {
                    enemy.getComponent(ImpComponent.class).setLeftWallTouched(false);
                }
                if (enemy.hasComponent(DragonComponent.class)) {
                    enemy.getComponent(DragonComponent.class).setLeftWallTouched(false);
                }
            }
        });

        /** Adds unitCollision to top void and enemy unit*/
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(DungeonCrawlerType.ENEMY, DungeonCrawlerType.TOPVOID) {
            @Override
            protected void onCollisionBegin(Entity enemy, Entity wall) {
                if (enemy.hasComponent(GoblinComponent.class)) {
                    enemy.getComponent(GoblinComponent.class).setTopWallTouched(true);
                }
                if (enemy.hasComponent(DemonComponent.class)) {
                    enemy.getComponent(DemonComponent.class).setTopWallTouched(true);
                }
                if (enemy.hasComponent(OgreComponent.class)) {
                    enemy.getComponent(OgreComponent.class).setTopWallTouched(true);
                }
                if (enemy.hasComponent(TrollComponent.class)) {
                    enemy.getComponent(TrollComponent.class).setTopWallTouched(true);
                }
                if (enemy.hasComponent(ImpComponent.class)) {
                    enemy.getComponent(ImpComponent.class).setTopWallTouched(true);
                }
                if (enemy.hasComponent(DragonComponent.class)) {
                    enemy.getComponent(DragonComponent.class).setTopWallTouched(true);
                }
            }

            @Override
            protected void onCollisionEnd(Entity enemy, Entity wall) {
                if (enemy.hasComponent(GoblinComponent.class)) {
                    enemy.getComponent(GoblinComponent.class).setTopWallTouched(false);
                }
                if (enemy.hasComponent(DemonComponent.class)) {
                    enemy.getComponent(DemonComponent.class).setTopWallTouched(false);
                }
                if (enemy.hasComponent(OgreComponent.class)) {
                    enemy.getComponent(OgreComponent.class).setTopWallTouched(false);
                }
                if (enemy.hasComponent(TrollComponent.class)) {
                    enemy.getComponent(TrollComponent.class).setTopWallTouched(false);
                }
                if (enemy.hasComponent(ImpComponent.class)) {
                    enemy.getComponent(ImpComponent.class).setTopWallTouched(false);
                }
                if (enemy.hasComponent(DragonComponent.class)) {
                    enemy.getComponent(DragonComponent.class).setTopWallTouched(false);
                }
            }
        });

        /** Adds unitCollision to bottom void and enemy unit*/
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(DungeonCrawlerType.ENEMY, DungeonCrawlerType.BOTTOMVOID) {
            @Override
            protected void onCollisionBegin(Entity enemy, Entity wall) {
                if (enemy.hasComponent(GoblinComponent.class)) {
                    enemy.getComponent(GoblinComponent.class).setBottomWallTouched(true);
                }
                if (enemy.hasComponent(DemonComponent.class)) {
                    enemy.getComponent(DemonComponent.class).setBottomWallTouched(true);
                }
                if (enemy.hasComponent(OgreComponent.class)) {
                    enemy.getComponent(OgreComponent.class).setBottomWallTouched(true);
                }
                if (enemy.hasComponent(TrollComponent.class)) {
                    enemy.getComponent(TrollComponent.class).setBottomWallTouched(true);
                }
                if (enemy.hasComponent(ImpComponent.class)) {
                    enemy.getComponent(ImpComponent.class).setBottomWallTouched(true);
                }
                if (enemy.hasComponent(DragonComponent.class)) {
                    enemy.getComponent(DragonComponent.class).setBottomWallTouched(true);
                }
            }

            @Override
            protected void onCollisionEnd(Entity enemy, Entity wall) {
                if (enemy.hasComponent(GoblinComponent.class)) {
                    enemy.getComponent(GoblinComponent.class).setBottomWallTouched(false);
                }
                if (enemy.hasComponent(DemonComponent.class)) {
                    enemy.getComponent(DemonComponent.class).setBottomWallTouched(false);
                }
                if (enemy.hasComponent(OgreComponent.class)) {
                    enemy.getComponent(OgreComponent.class).setBottomWallTouched(false);
                }
                if (enemy.hasComponent(TrollComponent.class)) {
                    enemy.getComponent(TrollComponent.class).setBottomWallTouched(true);
                }
                if (enemy.hasComponent(ImpComponent.class)) {
                    enemy.getComponent(ImpComponent.class).setBottomWallTouched(true);
                }
                if (enemy.hasComponent(DragonComponent.class)) {
                    enemy.getComponent(DragonComponent.class).setBottomWallTouched(true);
                }
            }
        });

        /** Adds unitCollision to right void and enemy unit*/
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(DungeonCrawlerType.ENEMY, DungeonCrawlerType.RIGHTVOID) {
            @Override
            protected void onCollisionBegin(Entity enemy, Entity wall) {
                if (enemy.hasComponent(GoblinComponent.class)) {
                    enemy.getComponent(GoblinComponent.class).setRightWallTouched(true);
                }
                if (enemy.hasComponent(DemonComponent.class)) {
                    enemy.getComponent(DemonComponent.class).setRightWallTouched(true);
                }
                if (enemy.hasComponent(OgreComponent.class)) {
                    enemy.getComponent(OgreComponent.class).setRightWallTouched(true);
                }
                if (enemy.hasComponent(TrollComponent.class)) {
                    enemy.getComponent(TrollComponent.class).setRightWallTouched(true);
                }
                if (enemy.hasComponent(ImpComponent.class)) {
                    enemy.getComponent(ImpComponent.class).setRightWallTouched(true);
                }
                if (enemy.hasComponent(DragonComponent.class)) {
                    enemy.getComponent(DragonComponent.class).setRightWallTouched(true);
                }
            }

            @Override
            protected void onCollisionEnd(Entity enemy, Entity wall) {
                if (enemy.hasComponent(GoblinComponent.class)) {
                    enemy.getComponent(GoblinComponent.class).setRightWallTouched(false);
                }
                if (enemy.hasComponent(DemonComponent.class)) {
                    enemy.getComponent(DemonComponent.class).setRightWallTouched(false);
                }
                if (enemy.hasComponent(OgreComponent.class)) {
                    enemy.getComponent(OgreComponent.class).setRightWallTouched(false);
                }
                if (enemy.hasComponent(TrollComponent.class)) {
                    enemy.getComponent(TrollComponent.class).setRightWallTouched(false);
                }
                if (enemy.hasComponent(ImpComponent.class)) {
                    enemy.getComponent(ImpComponent.class).setRightWallTouched(false);
                }
                if (enemy.hasComponent(DragonComponent.class)) {
                    enemy.getComponent(DragonComponent.class).setRightWallTouched(false);
                }
            }
        });

        /** Adds unitCollision to left void and enemy unit*/
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(DungeonCrawlerType.ENEMY, DungeonCrawlerType.LEFTVOID) {
            @Override
            protected void onCollisionBegin(Entity enemy, Entity wall) {
                if (enemy.hasComponent(GoblinComponent.class)) {
                    enemy.getComponent(GoblinComponent.class).setLeftWallTouched(true);
                }
                if (enemy.hasComponent(DemonComponent.class)) {
                    enemy.getComponent(DemonComponent.class).setLeftWallTouched(true);
                }
                if (enemy.hasComponent(OgreComponent.class)) {
                    enemy.getComponent(OgreComponent.class).setLeftWallTouched(true);
                }
                if (enemy.hasComponent(TrollComponent.class)) {
                    enemy.getComponent(TrollComponent.class).setLeftWallTouched(true);
                }
                if (enemy.hasComponent(ImpComponent.class)) {
                    enemy.getComponent(ImpComponent.class).setLeftWallTouched(true);
                }
                if (enemy.hasComponent(DragonComponent.class)) {
                    enemy.getComponent(DragonComponent.class).setLeftWallTouched(true);
                }
            }

            @Override
            protected void onCollisionEnd(Entity enemy, Entity wall) {
                if (enemy.hasComponent(GoblinComponent.class)) {
                    enemy.getComponent(GoblinComponent.class).setLeftWallTouched(false);
                }
                if (enemy.hasComponent(DemonComponent.class)) {
                    enemy.getComponent(DemonComponent.class).setLeftWallTouched(false);
                }
                if (enemy.hasComponent(OgreComponent.class)) {
                    enemy.getComponent(OgreComponent.class).setLeftWallTouched(false);
                }
                if (enemy.hasComponent(TrollComponent.class)) {
                    enemy.getComponent(TrollComponent.class).setLeftWallTouched(false);
                }
                if (enemy.hasComponent(ImpComponent.class)) {
                    enemy.getComponent(ImpComponent.class).setLeftWallTouched(false);
                }
                if (enemy.hasComponent(DragonComponent.class)) {
                    enemy.getComponent(DragonComponent.class).setLeftWallTouched(false);
                }
            }
        });

        /** Adds unitCollision to right trap wall and enemy unit*/
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(DungeonCrawlerType.ENEMY, DungeonCrawlerType.RIGHTTRAPWALL) {
            @Override
            protected void onCollisionBegin(Entity enemy, Entity righttrapwall) {
                if (enemy.hasComponent(TrollComponent.class)) {
                    enemy.getComponent(TrollComponent.class).setRightTrapWallTouched(true);
                }
                if (enemy.hasComponent(OgreComponent.class)) {
                    enemy.getComponent(OgreComponent.class).setLeftWallTouched(true);
                }
                if (enemy.hasComponent(GoblinComponent.class)) {
                    enemy.getComponent(GoblinComponent.class).setLeftWallTouched(true);
                }
            }

            @Override
            protected void onCollisionEnd(Entity enemy, Entity righttrapwall) {
                if (enemy.hasComponent(TrollComponent.class)) {
                    enemy.getComponent(TrollComponent.class).setRightTrapWallTouched(false);
                }
                if (enemy.hasComponent(OgreComponent.class)) {
                    enemy.getComponent(OgreComponent.class).setLeftWallTouched(false);
                }
                if (enemy.hasComponent(GoblinComponent.class)) {
                    enemy.getComponent(GoblinComponent.class).setLeftWallTouched(false);
                }
            }
        });

        /** Adds unitCollision to door and enemy unit*/
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(DungeonCrawlerType.ENEMY, DungeonCrawlerType.DOOR) {
            @Override
            protected void onCollisionBegin(Entity enemy, Entity door) {
                if (door.getY() < enemy.getY()) {
                    if (enemy.hasComponent(TrollComponent.class)) {
                        enemy.getComponent(TrollComponent.class).setTopDoorTouched(true);
                    }
                    if (enemy.hasComponent(DragonComponent.class)) {
                        enemy.getComponent(DragonComponent.class).setTopDoorTouched(true);
                    }
                    if (enemy.hasComponent(OgreComponent.class)) {
                        enemy.getComponent(OgreComponent.class).setTopDoorTouched(true);
                    }
                    if (enemy.hasComponent(GoblinComponent.class)) {
                        enemy.getComponent(GoblinComponent.class).setTopDoorTouched(true);
                    }
                }

                if (door.getY() > enemy.getY()) {
                    if (enemy.hasComponent(TrollComponent.class)) {
                        enemy.getComponent(TrollComponent.class).setBottomDoorTouched(true);
                    }
                    if (enemy.hasComponent(DragonComponent.class)) {
                        enemy.getComponent(DragonComponent.class).setBottomDoorTouched(true);
                    }
                    if (enemy.hasComponent(OgreComponent.class)) {
                        enemy.getComponent(OgreComponent.class).setBottomDoorTouched(true);
                    }
                    if (enemy.hasComponent(GoblinComponent.class)) {
                        enemy.getComponent(GoblinComponent.class).setBottomDoorTouched(true);
                    }
                }
            }

            @Override
            protected void onCollisionEnd(Entity enemy, Entity door) {
                if (enemy.hasComponent(TrollComponent.class)) {
                    enemy.getComponent(TrollComponent.class).setTopDoorTouched(false);
                    enemy.getComponent(TrollComponent.class).setBottomDoorTouched(false);
                }
                if (enemy.hasComponent(DragonComponent.class)) {
                    enemy.getComponent(DragonComponent.class).setTopDoorTouched(false);
                    enemy.getComponent(DragonComponent.class).setBottomDoorTouched(false);
                }
            }
        });

        /** Adds unitCollision to player and enemy unit*/
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(DungeonCrawlerType.PLAYER, DungeonCrawlerType.ENEMY) {
            @Override
            protected void onCollision(Entity player, Entity enemy) {
                if (enemy.getProperties().exists("isProjectile")) {
                    player.getComponent(PlayerComponent.class).onHit(10);
                    enemy.removeFromWorld();
                }
                if (enemy.hasComponent(ImpComponent.class)) {
                    player.getComponent(PlayerComponent.class).onHit(5); //imps deal little damage
                }
                if (enemy.hasComponent(OgreComponent.class)) {
                    player.getComponent(PlayerComponent.class).onHit(20); //ogres deal high damage
                }
                if (enemy.hasComponent(BossComponent.class)) {
                    if (!enemy.getComponent(BossComponent.class).isDead()) { //boss corpse lingers (boss is never removed from world)
                        player.getComponent(PlayerComponent.class).onHit(10); //boss deals normal damage
                    }
                } else {
                    player.getComponent(PlayerComponent.class).onHit(10); //standard hit
                }
            }
        });

        /** Adds unitCollision to weapon and enemy unit*/
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(DungeonCrawlerType.WEAPON, DungeonCrawlerType.ENEMY) {
            @Override
            protected void onCollision(Entity weapon, Entity enemy) {
                if (isAttacking) {
                    if (enemy.hasComponent(DemonComponent.class)) {
                        enemy.getComponent(DemonComponent.class).onHit();
                    }
                    if (enemy.hasComponent(GoblinComponent.class)) {
                        enemy.getComponent(GoblinComponent.class).onHit();
                    }
                    if (enemy.hasComponent(OgreComponent.class)) {
                        enemy.getComponent(OgreComponent.class).onHit();
                    }
                    if (enemy.hasComponent(TrollComponent.class)) {
                        enemy.getComponent(TrollComponent.class).onHit();
                    }
                    if (enemy.hasComponent(ImpComponent.class)) {
                        enemy.getComponent(ImpComponent.class).onHit();
                    }
                    if (enemy.hasComponent(BossComponent.class)) {
                        enemy.getComponent(BossComponent.class).onHit();
                    }
                    if (enemy.hasComponent(DragonComponent.class)) {
                        enemy.getComponent(DragonComponent.class).onHit();
                    }
                }
            }
        });

        /** Adds unitCollision to player and trap unit*/
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(DungeonCrawlerType.PLAYER, DungeonCrawlerType.TRAP) {
            @Override
            protected void onCollision(Entity player, Entity trap) {
                getCurrentLevel().setTrapActivated(true);
                getCurrentLevel().spawnTrapWalls();
                getGameWorld().getEntitiesByType(DungeonCrawlerType.TRAP).forEach(Entity::removeFromWorld); //Game slows down if the traps are not removed from world upon activation
            }
        });

        /** Adds unitCollision to player and stairs unit*/
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(DungeonCrawlerType.PLAYER, DungeonCrawlerType.STAIRS) {
            @Override
            protected void onCollision(Entity player, Entity stairs) {
                if (getCurrentLevel().equals(levels.get(2))) {
                    getDisplay().showMessageBox("You've completed the game. You can continue to play around in Level 03 if you'd like.", () -> {
                        player.setPosition(getCurrentLevel().getPlayerX(), getCurrentLevel().getPlayerY());
                        if (weapons.contains("greatsword")) {
                            weapon.setPosition(getCurrentLevel().getPlayerX() + 48, getCurrentLevel().getPlayerY() - 25);
                        } else {
                            weapon.setPosition(getCurrentLevel().getPlayerX() + 48, getCurrentLevel().getPlayerY());
                        }
                        if (!weaponFacingRight) {
                            player.setScaleX(1);
                            weapon.setScaleX(1);
                            weaponFacingRight = true;
                        }
                    });
                }

                //TODO fix the weapon disappearing and crashing the game when loading level 02
                if (!getCurrentLevel().equals(levels.get(2))) {
                    freezeInput = true;
                    getGameScene().getViewport().fade(() -> {
                        cleanupLevel();
                        nextLevel();
                    });
                }
            }
        });

        /** Adds unitCollision to player and red switch unit*/
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(DungeonCrawlerType.PLAYER, DungeonCrawlerType.REDSWITCH) {
            @Override
            protected void onCollision(Entity player, Entity redswitch) {
                redswitch.removeFromWorld();
                play("switch.wav");
                redSwitchActivated = true;
            }
        });

        /** Adds unitCollision to player and red switch unit*/
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(DungeonCrawlerType.PLAYER, DungeonCrawlerType.BLUESWITCH) {
            @Override
            protected void onCollision(Entity player, Entity blueswitch) {
                getCurrentLevel().spawnDragons();
                blueswitch.removeFromWorld();
                play("switch.wav");
                blueSwitchActivated = true;
            }
        });

        /** Adds unitCollision to player and hole unit*/
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(DungeonCrawlerType.PLAYER, DungeonCrawlerType.HOLE) {
            @Override
            protected void onCollision(Entity player, Entity hole) {
                freezeInput = true;
                getGameScene().getViewport().fade(() -> {
                    if (weapons.contains("greatsword")) {
                        weapon.setPosition(2560 + 48, 3008 - 25);
                    } else {
                        weapon.setPosition(2560 + 48, 3008);
                    }
                    player.setPosition(2560, 3008);
                    if (!weaponFacingRight) {
                        player.setScaleX(1);
                        weaponFacingRight = true;
                    }
                    freezeInput = false;
                });
            }
        });

        /** Adds unitCollision to player and hole unit*/
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(DungeonCrawlerType.PLAYER, DungeonCrawlerType.LADDER) {
            @Override
            protected void onCollision(Entity player, Entity ladder) {
                freezeInput = true;
                if (getCurrentLevel().equals(levels.get(1))) {
                    getGameScene().getViewport().fade(() -> {
                        if (weapons.contains("greatsword")) {
                            weapon.setPosition(getCurrentLevel().getPlayerX() + 48, getCurrentLevel().getPlayerY() - 25);
                        } else {
                            weapon.setPosition(getCurrentLevel().getPlayerX() + 48, getCurrentLevel().getPlayerY());
                        }
                        player.setPosition(getCurrentLevel().getPlayerX(), getCurrentLevel().getPlayerY());
                    });
                }

                if (getCurrentLevel().equals(levels.get(2))) {
                    getGameScene().getViewport().fade(() -> {
                        if (weapons.contains("greatsword")) {
                            weapon.setPosition(2560 + 48, 1280 - 25);
                        } else {
                            weapon.setPosition(2560 + 48, 1280);
                        }
                        player.setPosition(2560, 1280);
                        if (!weaponFacingRight) {
                            player.setScaleX(1);
                            weaponFacingRight = true;
                        }
                    });
                }

                if (!weaponFacingRight) {
                    player.setScaleX(1);
                    weaponFacingRight = true;
                }
                freezeInput = false;
            }
        });

        /** Adds unitCollision to player and spikes unit*/
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(DungeonCrawlerType.PLAYER, DungeonCrawlerType.SPIKES) {
            @Override
            protected void onCollision(Entity player, Entity spikes) {
                player.getComponent(PlayerComponent.class).onHit(30);
            }
        });

        /** Adds unitCollision to player and button unit*/
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(DungeonCrawlerType.PLAYER, DungeonCrawlerType.BUTTON) {
            @Override
            protected void onCollisionBegin(Entity player, Entity btn) {
                Entity keyEntity = btn.getObject("keyEntity");

                if (!keyEntity.isActive()) {
                    getGameWorld().addEntity(keyEntity);
                }

                keyEntity.getViewComponent().opacityProperty().setValue(1);
            }

            @Override
            protected void onCollisionEnd(Entity player, Entity btn) {
                Entity keyEntity = btn.getObject("keyEntity");

                keyEntity.getViewComponent().opacityProperty().setValue(0);
            }
        });

        /** Adds unitCollision to player and shop unit*/
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(DungeonCrawlerType.PLAYER, DungeonCrawlerType.SHOP) {
            @Override
            protected void onCollisionBegin(Entity player, Entity btn) {
                Entity keyEntity = btn.getObject("keyEntity");

                if (!keyEntity.isActive()) {
                    getGameWorld().addEntity(keyEntity);
                }

                keyEntity.getViewComponent().opacityProperty().setValue(1);
            }

            @Override
            protected void onCollisionEnd(Entity player, Entity btn) {
                Entity keyEntity = btn.getObject("keyEntity");
                keyEntity.getViewComponent().opacityProperty().setValue(0);
                if (shopActive) {
                    getGameScene().removeUINode(shopUI);
                    shopActive = false;
                }
            }
        });

        /** Adds unitCollision to player and chest unit*/
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(DungeonCrawlerType.PLAYER, DungeonCrawlerType.CHEST) {
            @Override
            protected void onCollisionBegin(Entity player, Entity btn) {
                Entity keyEntity = btn.getObject("keyEntity");
                if (!keyEntity.isActive()) {
                    getGameWorld().addEntity(keyEntity);
                }
                keyEntity.getViewComponent().opacityProperty().setValue(1);
                chestCollision = true;
            }

            @Override
            protected void onCollisionEnd(Entity player, Entity btn) {
                if (chestCollision) {
                    Entity keyEntity = btn.getObject("keyEntity");
                    keyEntity.getViewComponent().opacityProperty().setValue(0);
                    chestCollision = false;
                }
            }
        });

        /** Adds unitCollision to player and coin unit*/
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(DungeonCrawlerType.PLAYER, DungeonCrawlerType.COIN) {
            @Override
            protected void onCollision(Entity player, Entity coin) {
                inc("gold", +1);
                play("coins.wav");
                coin.removeFromWorld();
            }
        });

        /** Adds unitCollision to player and health potion unit*/
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(DungeonCrawlerType.PLAYER, DungeonCrawlerType.HEALTHPOTION) {
            @Override
            protected void onCollision(Entity player, Entity healthpotion) {
                if ((player.getComponent(PlayerComponent.class).getHp() < maxHP)) {
                    if ((player.getComponent(PlayerComponent.class).getHp() + 10) > maxHP) {
                        player.getComponent(PlayerComponent.class).setHp(maxHP);
                        play("sp_heal.wav");
                        healthpotion.removeFromWorld();
                    } else {
                        player.getComponent(PlayerComponent.class).setHp(player.getComponent(PlayerComponent.class).getHp() + 10);
                        play("sp_heal.wav");
                        healthpotion.removeFromWorld();
                    }
                }
            }
        });
    }

    public void onPlayerDied() {
        getDisplay().showMessageBox("YOU DIED", () -> {
            respawn();
        });
        play("death.wav");
    }

    private void respawn() {
        if (player != null) {
            player.setPosition(getCurrentLevel().getPlayerX(), getCurrentLevel().getPlayerY());
            if (weapons.contains("greatsword")) {
                weapon.setPosition(getCurrentLevel().getPlayerX() + 48, getCurrentLevel().getPlayerY() - 25);
            } else {
                weapon.setPosition(getCurrentLevel().getPlayerX() + 48, getCurrentLevel().getPlayerY());
            }
            player.getComponent(PlayerComponent.class).restoreHP();
        }
        if (!weaponFacingRight) {
            player.setScaleX(1);
            weapon.setScaleX(1);
            weaponFacingRight = true;
        }
    }

    protected DungeonLevel getCurrentLevel() {
        return levels.get(levelNumber - 1); //right now we don't have level 1
    }

    private void lootChest() {
        getGameWorld().getEntitiesByType(DungeonCrawlerType.CHEST)
                .stream()
                .filter(btn -> player.isColliding(btn))
                .forEach(btn -> {
                    getDisplay().showMessageBox("You find " + getCurrentLevel().getChestLoot().get(0) + " gold.", () -> {
                        inc("gold", +getCurrentLevel().getChestLoot().get(0));
                        play("coins.wav");
                        Entity keyEntity = btn.getObject("keyEntity");
                        keyEntity.getViewComponent().opacityProperty().setValue(0);
                        btn.removeFromWorld();
                        chestCollision = false;
                    });
                });
    }

    private void showStairs() {
        if (getGameWorld().getEntitiesByType(DungeonCrawlerType.ENEMY).isEmpty() && doorOpened && !stairsDiscovered) {
            getGameWorld().spawn("stairs", 1216, 704);
            stairsDiscovered = true;
        }
    }

    //TODO: make openDoor() work for every level and not just level 2
    private void openDoor() {
        if (getGameWorld().getEntitiesByType(DungeonCrawlerType.ENEMY).isEmpty() && !doorOpened && getCurrentLevel().equals(levels.get(1))) {
            getGameWorld().getEntitiesByType(DungeonCrawlerType.DOOR).forEach(Entity::removeFromWorld);
//            getGameWorld().getEntitiesByType(DungeonCrawlerType.DOOR).get(0).removeFromWorld();
            play("dooropen2.wav");
            doorOpened = true;
            Level_02 level_02 = new Level_02();
            level_02.spawnGoblins();
            runOnce(() -> {
                play("imphapy.wav");
            }, Duration.seconds(0.5));
            runOnce(() -> {
                play("imphapy.wav");
            }, Duration.seconds(1));
            runOnce(() -> {
                play("imphapy.wav");
            }, Duration.seconds(1.5));
            runOnce(() -> {
                play("imphapy.wav");
            }, Duration.seconds(2));
            runOnce(() -> {
                play("imphapy.wav");
            }, Duration.seconds(2.5));
        }
    }

    private void removeTrapWall() {
        if (getCurrentLevel().getTrollTrapEnemies().isEmpty() && !freezeInput) {
            getGameWorld().getEntitiesByType(DungeonCrawlerType.RIGHTTRAPWALL).forEach(Entity::removeFromWorld);
            play("wallup.wav");
            getCurrentLevel().setTrapActivated(false);
        }
    }

    private void spawnBoss() {
        if (bossActivated && getCurrentLevel().getDragonTrapEnemies().isEmpty()) {
            getCurrentLevel().spawnBoss();
            bossActivated = false;
        }
    }

    private void cleanupLevel() {
        getGameWorld().getEntitiesByType(
                DungeonCrawlerType.ENEMY,
                DungeonCrawlerType.STAIRS,
                DungeonCrawlerType.DOOR,
                DungeonCrawlerType.HOLE,
                DungeonCrawlerType.SPIKES,
                DungeonCrawlerType.RIGHTTRAPWALL,
                DungeonCrawlerType.RIGHTWALL,
                DungeonCrawlerType.LEFTWALL,
                DungeonCrawlerType.TOPWALL,
                DungeonCrawlerType.BOTTOMWALL,
                DungeonCrawlerType.RIGHTVOID,
                DungeonCrawlerType.LEFTVOID,
                DungeonCrawlerType.TOPVOID,
                DungeonCrawlerType.BOTTOMWALL,
                DungeonCrawlerType.REDSWITCH,
                DungeonCrawlerType.BLUESWITCH,
                DungeonCrawlerType.HPFOUNTAIN,
                DungeonCrawlerType.MPFOUNTAIN,
                DungeonCrawlerType.SHOP,
                DungeonCrawlerType.COIN,
                DungeonCrawlerType.BUTTON,
                DungeonCrawlerType.TRAP,
                DungeonCrawlerType.CHEST)
                .forEach(Entity::removeFromWorld);

        doorOpened = false;
        stairsDiscovered = false;
        freezeInput = false;
    }

    public void nextLevel() {
        /** Clean up the previous level and increment level number */
        currentHP = player.getComponent(PlayerComponent.class).getHp();
        doorOpened = false;
        stairsDiscovered = false;
        freezeInput = false;
        player.removeFromWorld();
        weapon.removeFromWorld();
        levelNumber += 1;

        /** Load new .tmx file */
        FXGL.setLevelFromMap(getCurrentLevel().getLevelName());

        /** Spawn the player and give him the correct HP amount*/
        player = getGameWorld().spawn("player", getCurrentLevel().getPlayerX(), getCurrentLevel().getPlayerY());
        getGameScene().getViewport().bindToEntity(player, getAppWidth() / 2.0, getAppHeight() / 2.0);
        player.getComponent(PlayerComponent.class).setHp(currentHP);

        /** Spawn the weapon */
        if (weapons.contains("greatsword")) {
            weapon = getGameWorld().spawn("greatsword", getCurrentLevel().getPlayerX() + 48, getCurrentLevel().getPlayerY() - 25);
        } else {
            weapon = getGameWorld().spawn("sword", getCurrentLevel().getPlayerX() + 48, getCurrentLevel().getPlayerY());
        }

        /** Set player and weapon correct on the Z-axis */
        player.setZ(1);
        weapon.setZ(1);

        /** Spawn other entities in the map */
        getCurrentLevel().spawnEnemies();
        getCurrentLevel().spawnChests();
    }

    public Texture getHeart1() {
        return heart1;
    }

    public Texture getHeart2() {
        return heart2;
    }

    public Texture getHeart3() {
        return heart3;
    }

    public Texture getHeart4() {
        return heart4;
    }

    public Texture getHeart5() {
        return heart5;
    }

    public void setHeart1(Texture heart1) {
        this.heart1 = heart1;
    }

    public void setHeart2(Texture heart2) {
        this.heart2 = heart2;
    }

    public void setHeart3(Texture heart3) {
        this.heart3 = heart3;
    }

    public void setHeart4(Texture heart4) {
        this.heart4 = heart4;
    }

    public void setHeart5(Texture heart5) {
        this.heart5 = heart5;
    }

    public ArrayList<String> getWeapons() {
        return weapons;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
