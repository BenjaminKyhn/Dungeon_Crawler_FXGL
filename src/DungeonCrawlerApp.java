import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.texture.Texture;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;

public class DungeonCrawlerApp extends GameApplication {

    private static boolean isMoving;
    private static boolean isAttacking = false;
    private static boolean weaponFacingRight = true;
    private Entity player;
    private Entity weapon;
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
    private boolean blueSwitchActivated = true;
    private boolean healing;
    private boolean bossActivated;
    public static boolean spikesSpawned;
    public static boolean trapSpikesSpawned;
    private int levelNumber = 3;
    private List<DungeonLevel> levels = new ArrayList<>();
    public static boolean freezeInput = false;
    private Texture heart1;
    private Texture heart2;
    private Texture heart3;

    @Override
    protected void initSettings(GameSettings gameSettings) {
        gameSettings.setWidth(1280);
        gameSettings.setHeight(720);
        gameSettings.setWidth(15*64);
        gameSettings.setHeight(10*64);
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

        loopBGM("06 Russell Shaw - Storm the Dungeon.mp3");
    }

    @Override
    protected void initGame() {
        /** Add all the levels to the Dungeon Level ArrayList */
        levels.add(new Level_01());
        levels.add(new Level_02());
        levels.add(new Level_03());

        /** Load map */
        FXGL.getGameWorld().addEntityFactory(new DungeonCrawlerFactory());
        getGameScene().setBackgroundColor(Color.BLACK);
        FXGL.setLevelFromMap(getCurrentLevel().getLevelName());

        /** Spawn the player */
        player = getGameWorld().spawn("player", getCurrentLevel().getPlayerX(), getCurrentLevel().getPlayerY());
        weapon = getGameWorld().spawn("weapon", getCurrentLevel().getPlayerX() + 48, getCurrentLevel().getPlayerY());
        player.setZ(1);
        weapon.setZ(1);

        /** Spawn initial enemies */
        getCurrentLevel().spawnEnemies();

        /** Add a camera that follows the player */
//        getGameScene().getViewport().setBounds(-250, 0, 3000, getAppHeight()); //sets a boundary for the camera
        getGameScene().getViewport().bindToEntity(player, getAppWidth() / 2.0, getAppHeight() / 2.0); //adds a camera and binds it to the player
    }

    @Override
    protected void initUI() {
//        HPIndicator hp = new HPIndicator(player.getComponent(HPComponent.class));
//        addUINode(hp);

        heart1 = texture("heart.png", 44, 40);
        heart2 = texture("heart.png", 44, 40);
        heart3 = texture("heart.png", 44, 40);

        addUINode(heart1, 15, 15);
        addUINode(heart2, 62, 15);
        addUINode(heart3, 109, 15);
    }

    @Override
    public void onUpdate(double tpf) {
        /** onUpdate methods that are always relevant*/
        updateUI();

        /** onUpdate methods specific for Level_02*/
        if (getCurrentLevel().equals((levels.get(1)))){
            openDoor();
            showStairs();
        }

        /** onUpdate methods specific for Level_03*/
        if (getCurrentLevel().equals(levels.get(2))){
            getCurrentLevel().spawnSpikes();
            spawnBoss();
            getCurrentLevel().openDoor();

            if (getCurrentLevel().isTrapActivated()){
                removeTrapWall();
            }
            if (!redSwitchActivated){
                getCurrentLevel().spawnTrapSpikes();
            }
            if (blueSwitchActivated){
                getGameWorld().getEntitiesInRange(new Rectangle2D(896,1152,1,1)).forEach(Entity::removeFromWorld);
                runOnce(() ->{play("dooropen2.wav");}, Duration.seconds(1));
                bossActivated = true;
                blueSwitchActivated = false;
            }
        }
    }

    private void updateUI() {
        if (player.getComponent(PlayerComponent.class).getHp() <= 0) {
            heart1 = texture("heart_empty.png", 44, 40);
            heart2 = texture("heart_empty.png", 44, 40);
            heart3 = texture("heart_empty.png", 44, 40);
            addUINode(heart1, 15, 15);
            addUINode(heart2, 62, 15);
            addUINode(heart3, 109, 15);
        } else if ((player.getComponent(PlayerComponent.class).getHp() > 0) && (player.getComponent(PlayerComponent.class).getHp() < 10)) {
            heart1 = texture("heart_half_full.png", 44, 40);
            heart2 = texture("heart_empty.png", 44, 40);
            heart3 = texture("heart_empty.png", 44, 40);
            addUINode(heart1, 15, 15);
            addUINode(heart2, 62, 15);
            addUINode(heart3, 109, 15);
        } else if ((player.getComponent(PlayerComponent.class).getHp() > 9) && (player.getComponent(PlayerComponent.class).getHp() < 15)) {
            heart2 = texture("heart_empty.png", 44, 40);
            heart3 = texture("heart_empty.png", 44, 40);
            addUINode(heart2, 62, 15);
            addUINode(heart3, 109, 15);
        } else if ((player.getComponent(PlayerComponent.class).getHp() > 14) && (player.getComponent(PlayerComponent.class).getHp() < 20)) {
            heart2 = texture("heart_half_full.png", 44, 40);
            heart3 = texture("heart_empty.png", 44, 40);
            addUINode(heart2, 62, 15);
            addUINode(heart3, 109, 15);
        } else if ((player.getComponent(PlayerComponent.class).getHp() > 19) && (player.getComponent(PlayerComponent.class).getHp() < 25)) {
            heart3 = texture("heart_empty.png", 44, 40);
            addUINode(heart3, 109, 15);
        } else if ((player.getComponent(PlayerComponent.class).getHp() > 24) && (player.getComponent(PlayerComponent.class).getHp() < 30)) {
            heart3 = texture("heart_half_full.png", 44, 40);
            addUINode(heart3, 109, 15);
        }
//        else if (player.getComponent(PlayerComponent.class).getHp() > 29) {
//            heart1 = texture("heart.png", 44, 40);
//            heart2 = texture("heart.png", 44, 40);
//            heart3 = texture("heart.png", 44, 40);
//            addUINode(heart1, 15, 15);
//            addUINode(heart3, 62, 15);
//            addUINode(heart3, 109, 15);
//        }
    }

    @Override
    protected void initInput() {
        FXGL.getInput().addAction(new UserAction("Use") {
            @Override
            protected void onActionBegin() {
                getGameWorld().getEntitiesByType(DungeonCrawlerType.BUTTON)
                        .stream()
                        .filter(btn -> player.isColliding(btn))
                        .forEach(btn -> {
                            if (!healing) {
                                healing = true;
                                player.getComponent(PlayerComponent.class).restoreHP();
                                play("SP_HEAL.wav");
                                runOnce(() -> {
                                    healing = false;
                                }, Duration.seconds(1));
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
                if (!freezeInput) {
                    /** Switch for handling random swing sounds */
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

        /** Adds unitCollision to door and player unit*/
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(DungeonCrawlerType.PLAYER, DungeonCrawlerType.DOOR) {
            @Override
            protected void onCollisionBegin(Entity player, Entity door) {
                if (door.getY() < player.getY()){
                    topDoorTouched = true;
                }
                if (door.getY() > player.getY()){
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
                if (enemy.hasComponent(BossComponent.class)) {
                    enemy.getComponent(BossComponent.class).setTopWallTouched(true);
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
                if (enemy.hasComponent(BossComponent.class)) {
                    enemy.getComponent(BossComponent.class).setTopWallTouched(false);
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
                if (enemy.hasComponent(BossComponent.class)) {
                    enemy.getComponent(BossComponent.class).setBottomWallTouched(true);
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
                if (enemy.hasComponent(BossComponent.class)) {
                    enemy.getComponent(BossComponent.class).setBottomWallTouched(true);
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
                if (enemy.hasComponent(BossComponent.class)) {
                    enemy.getComponent(BossComponent.class).setRightWallTouched(true);
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
                if (enemy.hasComponent(BossComponent.class)) {
                    enemy.getComponent(BossComponent.class).setRightWallTouched(false);
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
                if (enemy.hasComponent(BossComponent.class)) {
                    enemy.getComponent(BossComponent.class).setLeftWallTouched(true);
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
                if (enemy.hasComponent(BossComponent.class)) {
                    enemy.getComponent(BossComponent.class).setLeftWallTouched(false);
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
            }
            @Override
            protected void onCollisionEnd(Entity enemy, Entity righttrapwall) {
                if (enemy.hasComponent(TrollComponent.class)) {
                    enemy.getComponent(TrollComponent.class).setRightTrapWallTouched(false);
                }
            }
        });

        /** Adds unitCollision to door and enemy unit*/
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(DungeonCrawlerType.ENEMY, DungeonCrawlerType.DOOR) {
            @Override
            protected void onCollisionBegin(Entity enemy, Entity door) {
                if (door.getY() < enemy.getY()){
                    if (enemy.hasComponent(TrollComponent.class)){
                        enemy.getComponent(TrollComponent.class).setTopDoorTouched(true);
                    }
                    if (enemy.hasComponent(DragonComponent.class)){
                        enemy.getComponent(DragonComponent.class).setTopDoorTouched(true);
                    }
                }

                if (door.getY() > enemy.getY()){
                    if (enemy.hasComponent(TrollComponent.class)){
                        enemy.getComponent(TrollComponent.class).setBottomDoorTouched(true);
                    }
                    if (enemy.hasComponent(DragonComponent.class)){
                        enemy.getComponent(DragonComponent.class).setBottomDoorTouched(true);
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
                if (enemy.hasComponent(ImpComponent.class)){
                    player.getComponent(PlayerComponent.class).onHit(5); //imps deal little damage
                }
                if (enemy.hasComponent(OgreComponent.class)){
                    player.getComponent(PlayerComponent.class).onHit(20); //ogres deal high damage
                }
                if (enemy.hasComponent(BossComponent.class)){
                    if (!enemy.getComponent(BossComponent.class).isDead()){ //boss corpse lingers (boss is never removed from world)
                        player.getComponent(PlayerComponent.class).onHit(10); //boss deals normal damage
                    }
                }
                else{
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
                if (!freezeInput) {
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
                    player.setPosition(2560, 3008);
                    weapon.setPosition(2560 + 48, 3008);
                    if (!weaponFacingRight) {
                        player.setScaleX(1);
                        weaponFacingRight = true;
                    }
                    freezeInput = false;
                });
            }
        });

        /** Adds unitCollision to player and spikes unit*/
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(DungeonCrawlerType.PLAYER, DungeonCrawlerType.SPIKES) {
            @Override
            protected void onCollision(Entity player, Entity spikes) {
                player.getComponent(PlayerComponent.class).onHit(10);
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
            weapon.setPosition(getCurrentLevel().getPlayerX() + 48, getCurrentLevel().getPlayerY());
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
        }
    }

    private void removeTrapWall() {
        if (getCurrentLevel().getTrollTrapEnemies().isEmpty() && !freezeInput){
            getGameWorld().getEntitiesByType(DungeonCrawlerType.RIGHTTRAPWALL).forEach(Entity::removeFromWorld);
            play("wallup.wav");
            getCurrentLevel().setTrapActivated(false);
        }
    }

    private void spawnBoss() {
        if (bossActivated && getCurrentLevel().getDragonTrapEnemies().isEmpty()){
            getCurrentLevel().spawnBoss();
            bossActivated = false;
        }
    }

    private void cleanupLevel() {
        getGameWorld().getEntitiesByType(
                DungeonCrawlerType.ENEMY,
                DungeonCrawlerType.STAIRS,
                DungeonCrawlerType.DOOR,
                DungeonCrawlerType.RIGHTWALL,
                DungeonCrawlerType.LEFTWALL,
                DungeonCrawlerType.TOPWALL,
                DungeonCrawlerType.BOTTOMWALL,
                DungeonCrawlerType.HPFOUNTAIN,
                DungeonCrawlerType.MPFOUNTAIN)
                .forEach(Entity::removeFromWorld);

        doorOpened = false;
        stairsDiscovered = false;
        freezeInput = false;
    }

    public void nextLevel() {
        levelNumber += 1;
        player.setZ(1);
        weapon.setZ(1);
        player.setPosition(getCurrentLevel().getPlayerX(), getCurrentLevel().getPlayerY());
        weapon.setPosition(getCurrentLevel().getPlayerX() + 48, getCurrentLevel().getPlayerY());
        FXGL.setLevelFromMap(getCurrentLevel().getLevelName());
        getCurrentLevel().spawnEnemies();
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

    public void setHeart1(Texture heart1) {
        this.heart1 = heart1;
    }

    public void setHeart2(Texture heart2) {
        this.heart2 = heart2;
    }

    public void setHeart3(Texture heart3) {
        this.heart3 = heart3;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
