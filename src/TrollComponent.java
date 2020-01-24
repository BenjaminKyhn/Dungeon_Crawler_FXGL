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

public class TrollComponent extends Component{
    private HPComponent hp;
    private PhysicsComponent physics;
    private AnimatedTexture texture;
    private AnimationChannel animIdle, animWalk;
    private boolean bottomWallTouched;
    private boolean topWallTouched;
    private boolean rightWallTouched;
    private boolean leftWallTouched;
    private boolean rightTrapWallTouched;
    private boolean doorTouched;
    private boolean isBeingDamaged = false;
    private boolean dead = false;
    private LocalTimer moveTimer;
    private double speed = 2;
    private Entity player = FXGL.getGameWorld().getSingleton(DungeonCrawlerType.PLAYER);

    public TrollComponent() {
        Image image = image("troll3times.png");

        animIdle = new AnimationChannel(image, 8, 48, 51, Duration.seconds(1), 0, 3);
        animWalk = new AnimationChannel(image, 8, 48, 51, Duration.seconds(1), 4, 7);

        texture = new AnimatedTexture(animIdle);
        texture.loop();
    }

    @Override
    public void onAdded(){
        moveTimer = FXGL.newLocalTimer();
        moveTimer.capture();
        entity.getViewComponent().addChild(texture);
    }

    @Override
    public void onUpdate(double tpf) {
        /**
         * Target the player
         */
        if (moveTimer.elapsed(Duration.seconds(1)) && !rightWallTouched && !rightTrapWallTouched && (player.getX() - entity.getX() > 0) && (player.getX() - entity.getX() < 640) && (player.getX() - entity.getX() > player.getY() - entity.getY()) && !DungeonCrawlerApp.freezeInput) {
            for (int i = 0; i < 32; i++) {
                runOnce(() -> {
                    if (!dead && !rightWallTouched && !rightTrapWallTouched && !DungeonCrawlerApp.freezeInput && !(entity == null)) right();
                }, Duration.seconds(i / 32.0));
            }
            texture.loopAnimationChannel(animWalk);
            moveTimer.capture();
            runOnce(() -> {
                texture.loopAnimationChannel(animIdle);
            }, Duration.seconds(1));
        }

        if (moveTimer.elapsed(Duration.seconds(1)) && !leftWallTouched && (player.getX() - entity.getX() < 0) && (player.getX() - entity.getX() > -640) && (player.getX() - entity.getX() < player.getY() - entity.getY()) && !DungeonCrawlerApp.freezeInput) {
            for (int i = 0; i < 32; i++) {
                runOnce(() -> {
                    if (!dead && !leftWallTouched && !DungeonCrawlerApp.freezeInput && !(entity == null)) left();
                }, Duration.seconds(i / 32.0));
            }
            texture.loopAnimationChannel(animWalk);
            moveTimer.capture();
            runOnce(() -> {
                texture.loopAnimationChannel(animIdle);
            }, Duration.seconds(1));
        }

        if (moveTimer.elapsed(Duration.seconds(1)) && !topWallTouched && (player.getY() - entity.getY() < 0) && (player.getY() - entity.getY() > -640) && (player.getY() - entity.getY() < player.getX() - entity.getX()) && !DungeonCrawlerApp.freezeInput) {
            for (int i = 0; i < 32; i++) {
                runOnce(() -> {
                    if (!dead && !topWallTouched && !DungeonCrawlerApp.freezeInput && !(entity == null)) up();
                }, Duration.seconds(i / 32.0));
            }
            texture.loopAnimationChannel(animWalk);
            moveTimer.capture();
            runOnce(() -> {
                texture.loopAnimationChannel(animIdle);
            }, Duration.seconds(1));
        }

        if (moveTimer.elapsed(Duration.seconds(1)) && !bottomWallTouched && (player.getY() - entity.getY() > 0) && (player.getY() - entity.getY() < 640) && (player.getY() - entity.getY() > player.getX() - entity.getX()) && !DungeonCrawlerApp.freezeInput) {
            for (int i = 0; i < 32; i++) {
                runOnce(() -> {
                    if (!dead && !bottomWallTouched && !DungeonCrawlerApp.freezeInput && !(entity == null)) down();
                }, Duration.seconds(i / 32.0));
            }
            texture.loopAnimationChannel(animWalk);
            moveTimer.capture();
            runOnce(() -> {
                texture.loopAnimationChannel(animIdle);
            }, Duration.seconds(1));
        }

        /** Random movement when outside of aggro range */
        if ((player.getX() - entity.getX() > 640) && (player.getX() - entity.getX() < -640) && (player.getY() - entity.getY() > 640) && (player.getY() - entity.getY() < -640) && !DungeonCrawlerApp.freezeInput){
            int randomMovement = (int) (Math.random() * 4);
            switch (randomMovement) {
                case 0:
                    if (moveTimer.elapsed(Duration.seconds(1)) && !topWallTouched) {
                        for (int i = 0; i < 32; i++) {
                            runOnce(() -> {
                                if (!dead && !topWallTouched && !doorTouched && !DungeonCrawlerApp.freezeInput && !(entity == null)) up();
                            }, Duration.seconds(i / 32.0));
                        }
                        texture.loopAnimationChannel(animWalk);
                        moveTimer.capture();
                        runOnce(() -> {
                            texture.loopAnimationChannel(animIdle);
                        }, Duration.seconds(2));
                        break;
                    }
                case 1:
                    if (moveTimer.elapsed(Duration.seconds(1)) && !bottomWallTouched) {
                        for (int i = 0; i < 32; i++) {
                            runOnce(() -> {
                                if (!dead && !bottomWallTouched  && !DungeonCrawlerApp.freezeInput && !(entity == null)) down();
                            }, Duration.seconds(i / 32.0));
                        }
                        texture.loopAnimationChannel(animWalk);
                        moveTimer.capture();
                        runOnce(() -> {
                            texture.loopAnimationChannel(animIdle);
                        }, Duration.seconds(2));
                        break;
                    }
                case 2:
                    if (moveTimer.elapsed(Duration.seconds(1)) && !rightWallTouched) {
                        for (int i = 0; i < 32; i++) {
                            runOnce(() -> {
                                if (!dead && !rightWallTouched && !rightTrapWallTouched && !DungeonCrawlerApp.freezeInput && !(entity == null)) right();
                            }, Duration.seconds(i / 32.0));
                        }
                        texture.loopAnimationChannel(animWalk);
                        moveTimer.capture();
                        runOnce(() -> {
                            texture.loopAnimationChannel(animIdle);
                        }, Duration.seconds(2));
                        break;
                    }
                case 3:
                    if (moveTimer.elapsed(Duration.seconds(1)) && !leftWallTouched) {
                        for (int i = 0; i < 32; i++) {
                            runOnce(() -> {
                                if (!dead && !leftWallTouched  && !DungeonCrawlerApp.freezeInput && !(entity == null)) left();
                            }, Duration.seconds(i / 32.0));
                        }
                        texture.loopAnimationChannel(animWalk);
                        moveTimer.capture();
                        runOnce(() -> {
                            texture.loopAnimationChannel(animIdle);
                        }, Duration.seconds(2));
                        break;
                    }
            }
        }

    }
    public void left() {
        getEntity().setScaleX(-1); //Changes the direction of the sprite
        entity.translateX(-speed);
    }

    public void right() {
        getEntity().setScaleX(1); //Changes the direction of the sprite
        entity.translateX(speed);
    }

    public void up() {
        entity.translateY(-speed);
    }

    public void down() {
        entity.translateY(speed);
    }

    public void setDead(boolean dead) {
        this.dead = dead;
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
                case 0: play("trollhit1.wav"); break;
                case 1: play("trollhit2.wav"); break;
                case 2: play("trollhit3.wav"); break;
            }

            double zeroOpacity = 0.2;
            double fullOpacity = 0.0;
            for (int i = 0; i < 2; i++) {
                runOnce(() -> {
                    entity.getViewComponent().setOpacity(0.0);
                }, Duration.seconds(fullOpacity));
                runOnce(() -> {
                    entity.getViewComponent().setOpacity(1.0);
                }, Duration.seconds(zeroOpacity));
                zeroOpacity += 0.4;
                fullOpacity += 0.4;
            }
        }

        isBeingDamaged = true;

        /** Damage time 1 sec */
        runOnce(() -> {
            isBeingDamaged = false;
        }, Duration.seconds(0.6));

        /** When mob is dead */
        if (hp.getValue() == 0) {
            setDead(true);
            entity.removeFromWorld();
            FXGL.<DungeonCrawlerApp>getAppCast().getCurrentLevel().getTrapEnemies().remove(entity);
            int randomDeathSound = (int)(Math.random()*2);
            switch (randomDeathSound){
                case 0: play("trolldie1.wav"); break;
                case 1: play("trolldie2.wav"); break;
            }
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

    public void setRightTrapWallTouched(boolean rightTrapWallTouched) {
        this.rightTrapWallTouched = rightTrapWallTouched;
    }

    public void setDoorTouched(boolean doorTouched) {
        this.doorTouched = doorTouched;
    }
}
