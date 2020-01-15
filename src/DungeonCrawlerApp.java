import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.CollisionHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.util.Duration;

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

    @Override
    protected void initSettings(GameSettings gameSettings) {
        gameSettings.setWidth(15 * 64);
        gameSettings.setHeight(10 * 64);
        gameSettings.setTitle("Dungeon Crawler");
        gameSettings.setVersion("0.1");
//        gameSettings.setFullScreenAllowed(true);
//        gameSettings.setFullScreenFromStart(true);
    }

    @Override
    protected void onPreInit() {
        getSettings().setGlobalSoundVolume(0.2);
        getSettings().setGlobalMusicVolume(0.2);

        loopBGM("06 Russell Shaw - Storm the Dungeon.mp3");
    }

    @Override
    protected void initGame() {
        FXGL.getGameWorld().addEntityFactory(new DungeonCrawlerFactory());
        getGameScene().setBackgroundColor(Color.BLACK);
        FXGL.setLevelFromMap("dungeon1.tmx");

        player = getGameWorld().spawn("player", 150, 150);
        weapon = getGameWorld().spawn("weapon", 198, 150);

        getGameWorld().spawn("enemy", 448, 384);
        getGameWorld().spawn("goblin", 192, 384);
        getGameWorld().spawn("enemy", 320, 896);
        getGameWorld().spawn("goblin", 640, 1088);
        getGameWorld().spawn("goblin", 1152, 1024);

//        getGameScene().getViewport().setBounds(-250, 0, 3000, getAppHeight()); //sets a boundary for the camera
        getGameScene().getViewport().bindToEntity(player, getAppWidth() / 2, getAppHeight() / 2); //adds a camera and binds it to the player

    }

    @Override
    protected void initInput() {
        FXGL.getInput().addAction(new UserAction("Left") {
            @Override
            protected void onActionBegin() {
//                if (isAttacking && weaponFacingRight){
//                    weapon.getComponent(WeaponComponent.class).undoAttackRight();
//                    weapon.getComponent(WeaponComponent.class).attackLeft();
//                }
                if (weaponFacingRight) {
                    weapon.getComponent(WeaponComponent.class).faceLeft();
                    weaponFacingRight = false;
                }
            }

            protected void onAction() {
                if (leftWallTouched) //If player unit collides with right wall,"Move Right" function stops until false.
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
//                if (isAttacking && !weaponFacingRight){
//                    weapon.getComponent(WeaponComponent.class).undoAttackLeft();
//                    weapon.getComponent(WeaponComponent.class).attackRight();
//                }
                if (!weaponFacingRight) {
                    weapon.getComponent(WeaponComponent.class).faceRight();
                    weaponFacingRight = true;
                }
            }

            protected void onAction() {
                if (rightWallTouched) //If player unit collides with right wall,"Move Right" function stops until false.
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
                if (topWallTouched) //If player unit collides with right wall,"Move Right" function stops until false.
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
                if (bottomWallTouched) //If player unit collides with right wall,"Move Right" function stops until false.
                    return;
                player.getComponent(PlayerComponent.class).down();
                weapon.getComponent(WeaponComponent.class).down();
                isMoving = true;
            }

            protected void onActionEnd() {
                isMoving = false;
            }
        }, KeyCode.S);

//        FXGL.getInput().addAction(new UserAction("Attack") {
//            @Override
//            protected void onActionBegin() {
//                if (weaponFacingRight && !isAttacking) {
//                    weapon.getComponent(WeaponComponent.class).attackRight();
//                    isAttacking = true;
//                }
//                if (!weaponFacingRight && !isAttacking) {
//                    weapon.getComponent(WeaponComponent.class).attackLeft();
//                    isAttacking = true;
//                }
//            }
//
//            protected void onActionEnd() {
//                if (weaponFacingRight) {
//                    weapon.getComponent(WeaponComponent.class).undoAttackRight();
//                    isAttacking = false;
//
//                }
//                if (!weaponFacingRight) {
//                    weapon.getComponent(WeaponComponent.class).undoAttackLeft();
//                    isAttacking = false;
//                }
//            }
//        }, KeyCode.SPACE);
//    }

            FXGL.getInput().addAction(new UserAction("Attack") {
        @Override
        protected void onActionBegin() {
            /** Switch for handling random swing sounds */
            int randomSwingSound = (int)(Math.random()*3);
            switch (randomSwingSound){
                case 0: play("swing.wav"); break;
                case 1: play("swing2.wav"); break;
                case 2: play("swing3.wav"); break;
            }

            /** Directional attacking */
            if (weaponFacingRight && !isAttacking) {
                weapon.getComponent(WeaponComponent.class).attackRight();
                isAttacking = true;
                runOnce(()->{
                    weapon.getComponent(WeaponComponent.class).undoAttackRight();
                    isAttacking = false;
                }, Duration.seconds(0.1));
            }
            if (!weaponFacingRight && !isAttacking) {
                weapon.getComponent(WeaponComponent.class).attackLeft();
                isAttacking = true;
                runOnce(()->{
                    weapon.getComponent(WeaponComponent.class).undoAttackLeft();
                    isAttacking = false;
                }, Duration.seconds(0.1));
            }
        }
    }, MouseButton.PRIMARY);

//        FXGL.getInput().addAction(new UserAction("Juggle") {
//            @Override
//            protected void onAction() {
//                if (weaponFacingRight && !isAttacking) {
//                    weapon.getComponent(WeaponComponent.class).juggle();
//                }
//                if (!weaponFacingRight && !isAttacking) {
//                }
//            }
//        }, KeyCode.F);
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

        /** Adds unitCollision to top wall and enemy unit*/
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(DungeonCrawlerType.ENEMY, DungeonCrawlerType.TOPWALL) {
            @Override
            protected void onCollisionBegin(Entity enemy, Entity wall) {
                if (enemy.hasComponent(GoblinComponent.class)) {
                    enemy.getComponent(GoblinComponent.class).setTopWallTouched(true);
                }
                if (enemy.hasComponent(EnemyComponent.class)) {
                    enemy.getComponent(EnemyComponent.class).setTopWallTouched(true);
                }
            }

            @Override
            protected void onCollisionEnd(Entity enemy, Entity wall) {
                if (enemy.hasComponent(GoblinComponent.class)) {
                    enemy.getComponent(GoblinComponent.class).setTopWallTouched(false);
                }
                if (enemy.hasComponent(EnemyComponent.class)) {
                    enemy.getComponent(EnemyComponent.class).setTopWallTouched(false);
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
                if (enemy.hasComponent(EnemyComponent.class)) {
                    enemy.getComponent(EnemyComponent.class).setBottomWallTouched(true);
                }
            }

            @Override
            protected void onCollisionEnd(Entity enemy, Entity wall) {
                if (enemy.hasComponent(GoblinComponent.class)) {
                    enemy.getComponent(GoblinComponent.class).setBottomWallTouched(false);
                }
                if (enemy.hasComponent(EnemyComponent.class)) {
                    enemy.getComponent(EnemyComponent.class).setBottomWallTouched(false);
                }
            }
        });

        /** Adds unitCollision to bottom wall and enemy unit*/
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(DungeonCrawlerType.ENEMY, DungeonCrawlerType.RIGHTWALL) {
            @Override
            protected void onCollisionBegin(Entity enemy, Entity wall) {
                if (enemy.hasComponent(GoblinComponent.class)) {
                    enemy.getComponent(GoblinComponent.class).setRightWallTouched(true);
                }
                if (enemy.hasComponent(EnemyComponent.class)) {
                    enemy.getComponent(EnemyComponent.class).setRightWallTouched(true);
                }
            }

            @Override
            protected void onCollisionEnd(Entity enemy, Entity wall) {
                if (enemy.hasComponent(GoblinComponent.class)) {
                    enemy.getComponent(GoblinComponent.class).setRightWallTouched(false);
                }
                if (enemy.hasComponent(EnemyComponent.class)) {
                    enemy.getComponent(EnemyComponent.class).setRightWallTouched(false);
                }
            }
        });

        /** Adds unitCollision to bottom wall and enemy unit*/
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(DungeonCrawlerType.ENEMY, DungeonCrawlerType.LEFTWALL) {
            @Override
            protected void onCollisionBegin(Entity enemy, Entity wall) {
                if (enemy.hasComponent(GoblinComponent.class)) {
                    enemy.getComponent(GoblinComponent.class).setLeftWallTouched(true);
                }
                if (enemy.hasComponent(EnemyComponent.class)) {
                    enemy.getComponent(EnemyComponent.class).setLeftWallTouched(true);
                }
            }

            @Override
            protected void onCollisionEnd(Entity enemy, Entity wall) {
                if (enemy.hasComponent(GoblinComponent.class)) {
                    enemy.getComponent(GoblinComponent.class).setLeftWallTouched(false);
                }
                if (enemy.hasComponent(EnemyComponent.class)) {
                    enemy.getComponent(EnemyComponent.class).setLeftWallTouched(false);
                }
            }
        });

        /** Adds unitCollision to player and enemy unit*/
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(DungeonCrawlerType.PLAYER, DungeonCrawlerType.ENEMY) {
            @Override
            protected void onCollision(Entity player, Entity enemy) {
                player.getComponent(PlayerComponent.class).onHit(enemy);
            }
        });

        /** Adds unitCollision to weapon and enemy unit*/
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(DungeonCrawlerType.WEAPON, DungeonCrawlerType.ENEMY) {
            @Override
            protected void onCollision(Entity weapon, Entity enemy) {
                if (isAttacking) {
                    if (enemy.hasComponent(EnemyComponent.class)){
                        enemy.getComponent(EnemyComponent.class).onHit();
                    }

                    if (enemy.hasComponent(GoblinComponent.class)){
                        enemy.getComponent(GoblinComponent.class).onHit();

                    }
                }
            }
        });

        /** Adds unitCollision to player and stairs unit*/
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(DungeonCrawlerType.PLAYER, DungeonCrawlerType.STAIRS) {
            @Override
            protected void onCollision(Entity player, Entity stairs) {
                nextLevel();
            }
        });
    }

    @Override
    protected void initUI() {
        var hp = new HPIndicator(player.getComponent(HPComponent.class));
        addUINode(hp);
    }

    public void onPlayerDied() {
        getDisplay().showMessageBox("YOU DIED", () -> {
            respawn();
        });
        play("death.wav");
    }

    private void respawn() {
        if (player != null) {
            player.setPosition(150, 150);
            weapon.setPosition(198, 150);
            player.getComponent(PlayerComponent.class).restoreHP();
        }
        if (!weaponFacingRight) {
            player.setScaleX(1);
            weapon.setScaleX(1);
            weaponFacingRight = true;
        }
    }

    private void nextLevel() {
        player.setZ(Integer.MAX_VALUE);
        weapon.setZ(Integer.MAX_VALUE);
        FXGL.setLevelFromMap("dungeon2.tmx");
        player.setPosition(1600,1600);
        weapon.setPosition(1648,1600);
        getGameWorld().spawn("enemy", 256, 256);
        getGameWorld().spawn("goblin", 320, 320);
        getGameWorld().spawn("enemy", 1472, 256);
        getGameWorld().spawn("goblin", 1600, 256);
        getGameWorld().spawn("enemy", 256, 1600);
        getGameWorld().spawn("goblin", 256, 1728);
        getGameWorld().spawn("enemy", 1472, 1600);
        getGameWorld().spawn("goblin", 1600, 1728);

    }

    public static void main(String[] args) {
        launch(args);
    }
}
