import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.time.LocalTimer;
import javafx.scene.image.Image;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

public class    BossComponent extends Component {
    private HPComponent hp;
    private PhysicsComponent physics;
    private AnimatedTexture texture;
    private AnimationChannel animIdle, animWalk, animDead;
    private boolean bottomWallTouched;
    private boolean topWallTouched;
    private boolean rightWallTouched;
    private boolean leftWallTouched;
    private boolean isBeingDamaged = false;
    private boolean dead = false;
    private boolean facingRight = true;
    private LocalTimer moveTimer;
    private double speed = 2;
    private Entity player = FXGL.getGameWorld().getSingleton(DungeonCrawlerType.PLAYER);

    public BossComponent() {
        Image image = image("boss4times.png");
        Image image2 = image("boss_death.png");

        animIdle = new AnimationChannel(image, 4, 696, 432, Duration.seconds(1), 0, 3);
        animWalk = new AnimationChannel(image, 4, 696, 432, Duration.seconds(1), 0, 3);
        animDead = new AnimationChannel(image2, 4, 653, 432, Duration.seconds(1), 0, 3);

        texture = new AnimatedTexture(animIdle);
        texture.loop();
    }

    @Override
    public void onAdded() {
        moveTimer = FXGL.newLocalTimer();
        moveTimer.capture();
        entity.getViewComponent().addChild(texture);
    }

    @Override
    public void onUpdate(double tpf) {
        /**
         * Target the player
         */
        if (moveTimer.elapsed(Duration.seconds(3)) && !rightWallTouched && (player.getX() - entity.getX() > 0) && (player.getX() - entity.getX() < 640) && (player.getX() - entity.getX() > player.getY() - entity.getY()) && !DungeonCrawlerApp.freezeInput && !dead) {
            for (int i = 0; i < 64; i++) {
                runOnce(() -> {
                    if (!dead && !rightWallTouched && !DungeonCrawlerApp.freezeInput && !(entity == null)) right();
                }, Duration.seconds(i / 32.0));
                texture.loopAnimationChannel(animWalk);
            }
            moveTimer.capture();
        }

        if (moveTimer.elapsed(Duration.seconds(3)) && !leftWallTouched && (player.getX() - entity.getX() < 0) && (player.getX() - entity.getX() > -640) && (player.getX() - entity.getX() < player.getY() - entity.getY()) && !DungeonCrawlerApp.freezeInput && !dead) {
            for (int i = 0; i < 64; i++) {
                runOnce(() -> {
                    if (!dead && !leftWallTouched  && !DungeonCrawlerApp.freezeInput && !(entity == null)) left();
                }, Duration.seconds(i / 32.0));
                texture.loopAnimationChannel(animWalk);
            }
            moveTimer.capture();
        }

        if (moveTimer.elapsed(Duration.seconds(3)) && !topWallTouched && (player.getY() - entity.getY() < 0) && (player.getY() - entity.getY() > -640) && (player.getY() - entity.getY() < player.getX() - entity.getX()) && !DungeonCrawlerApp.freezeInput && !dead) {
            for (int i = 0; i < 64; i++) {
                runOnce(() -> {
                    if (!dead && !topWallTouched  && !DungeonCrawlerApp.freezeInput && !(entity == null)) up();
                }, Duration.seconds(i / 32.0));
                texture.loopAnimationChannel(animWalk);
            }
            moveTimer.capture();
        }

        if (moveTimer.elapsed(Duration.seconds(3)) && !bottomWallTouched && (player.getY() - entity.getY() > 0) && (player.getY() - entity.getY() < 640) && (player.getY() - entity.getY() > player.getX() - entity.getX()) && !DungeonCrawlerApp.freezeInput && !dead) {
            for (int i = 0; i < 64; i++) {
                runOnce(() -> {
                    if (!dead && !bottomWallTouched  && !DungeonCrawlerApp.freezeInput && !(entity == null)) down();
                }, Duration.seconds(i / 32.0));
                texture.loopAnimationChannel(animWalk);
            }
            moveTimer.capture();
        }

        /** Random movement when outside of aggro range */
        if ((player.getX() - entity.getX() > 640) && (player.getX() - entity.getX() < -640) && (player.getY() - entity.getY() > 640) && (player.getY() - entity.getY() < -640) && !DungeonCrawlerApp.freezeInput  && !dead){
            int randomMovement = (int) (Math.random() * 4);
            switch (randomMovement) {
                case 0:
                    if (moveTimer.elapsed(Duration.seconds(3)) && !topWallTouched  && !dead) {
                        for (int i = 0; i < 64; i++) {
                            runOnce(() -> {
                                if (!dead && !topWallTouched  && !DungeonCrawlerApp.freezeInput && !(entity == null)) up();
                            }, Duration.seconds(i / 32.0));
                            texture.loopAnimationChannel(animWalk);
                        }
                        moveTimer.capture();
                        break;
                    }
                case 1:
                    if (moveTimer.elapsed(Duration.seconds(3)) && !bottomWallTouched  && !dead) {
                        for (int i = 0; i < 64; i++) {
                            runOnce(() -> {
                                if (!dead && !bottomWallTouched  && !DungeonCrawlerApp.freezeInput && !(entity == null)) down();
                            }, Duration.seconds(i / 32.0));
                            texture.loopAnimationChannel(animWalk);
                        }
                        moveTimer.capture();
                        break;
                    }
                case 2:
                    if (moveTimer.elapsed(Duration.seconds(3)) && !rightWallTouched && !dead) {
                        for (int i = 0; i < 64; i++) {
                            runOnce(() -> {
                                if (!dead && !rightWallTouched  && !DungeonCrawlerApp.freezeInput && !(entity == null)) right();
                            }, Duration.seconds(i / 32.0));
                            texture.loopAnimationChannel(animWalk);
                        }
                        moveTimer.capture();
                        break;
                    }
                case 3:
                    if (moveTimer.elapsed(Duration.seconds(3)) && !leftWallTouched  && !dead) {
                        for (int i = 0; i < 64; i++) {
                            runOnce(() -> {
                                if (!dead && !leftWallTouched && !DungeonCrawlerApp.freezeInput && !(entity == null)) left();
                            }, Duration.seconds(i / 32.0));
                            texture.loopAnimationChannel(animWalk);
                        }
                        moveTimer.capture();
                        break;
                    }
            }
        }

    }

    public void left() {
        if (facingRight){
            entity.translateX(350);
            facingRight = false;
        }
        getEntity().setScaleX(-1); //Changes the direction of the sprite
        entity.translateX(-speed);
    }

    public void right() {
        if (!facingRight){
            entity.translateX(-350);
            facingRight = true;
        }
        getEntity().setScaleX(1); //Changes the direction of the sprite
        entity.translateX(speed);
    }

    public void up() {
        entity.translateY(-speed);
    }

    public void down() {
        entity.translateY(speed);
    }

    public void onHit() {
        if (isBeingDamaged)
            return;

        if (hp.getValue() == 0)
            return;

        /** When mob is still alive */
        hp.setValue(hp.getValue() - 25);
        if (hp.getValue() > 0){
            int randomHitSound = (int)(Math.random()*3);
            switch (randomHitSound){
                case 0: play("HORNHIT1.wav"); break;
                case 1: play("HORNHIT2.wav"); break;
                case 2: play("HORNHIT3.wav"); break;
            }

            double zeroOpacity = 0.1;
            double fullOpacity = 0.0;
            for (int i = 0; i < 4; i++) {
                runOnce(() -> {
                    entity.getViewComponent().setOpacity(0.0);
                }, Duration.seconds(fullOpacity));
                runOnce(() -> {
                    entity.getViewComponent().setOpacity(1.0);
                }, Duration.seconds(zeroOpacity));
                zeroOpacity += 0.175;
                fullOpacity += 0.175;
            }
        }

        isBeingDamaged = true;

        /** Damage time 0.6 sec */
        runOnce(() -> {
            isBeingDamaged = false;
        }, Duration.seconds(0.7));

        /** When mob is dead */
        if (hp.getValue() <= 0) {
            dead = true;
            play("HORNDIE2.wav");
            texture.playAnimationChannel(animDead);
        }
    }

    public void setBottomWallTouched(boolean bottomWallTouched) {
        this.bottomWallTouched = bottomWallTouched;
    }

    public void setTopWallTouched(boolean topWallTouched) {
        this.topWallTouched = topWallTouched;
    }

    public void setRightWallTouched(boolean rightWallTouched) {
        this.rightWallTouched = rightWallTouched;
    }

    public void setLeftWallTouched(boolean leftWallTouched) {
        this.leftWallTouched = leftWallTouched;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public boolean isDead() {
        return this.dead;
    }
}
