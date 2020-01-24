import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.component.Required;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

@Required(HPComponent.class)
public class PlayerComponent extends Component {

    private HPComponent hp;
    private PhysicsComponent physics;
    private AnimatedTexture texture;
    private AnimationChannel animIdle, animWalk;
    private double speed = 5;
    private boolean isBeingDamaged = false;

    public PlayerComponent() {
        Image image = image("player.png");

        animIdle = new AnimationChannel(image, 8, 48, 66, Duration.seconds(1), 0, 3);
        animWalk = new AnimationChannel(image, 8, 48, 66, Duration.seconds(0.33), 4, 7);

        texture = new AnimatedTexture(animIdle);
        texture.loop();
    }

    @Override
    public void onAdded() {
        entity.getViewComponent().addChild(texture);
    }

    @Override
    public void onUpdate(double tpf) {
        if (DungeonCrawlerApp.isMoving()) {
            if (texture.getAnimationChannel() != animWalk) {
                texture.loopAnimationChannel(animWalk);
            }
        } else {
            if (texture.getAnimationChannel() != animIdle) {
                texture.loopAnimationChannel(animIdle);
            }
        }
    }

//    private boolean isMoving() {
//        return physics.isMovingX();
//    }

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

    public void onHit(int damage) {
        if (isBeingDamaged)
            return;

//        if (hp.getValue() == 0)
//            return;

        /** When mob is still alive */
        hp.setValue(hp.getValue() - damage);
        if (hp.getValue() > 0) {
            int randomHitSound = (int) (Math.random() * 3);
            switch (randomHitSound) {
                case 0:
                    play("MAN2HIT1.wav");
                    break;
                case 1:
                    play("MAN2HIT2.wav");
                    break;
                case 2:
                    play("MAN2HIT3.wav");
                    break;
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

        // Damage time 1 sec
        runOnce(() -> {
            isBeingDamaged = false;
        }, Duration.seconds(0.6));

        if (hp.getValue() <= 0) {
            FXGL.<DungeonCrawlerApp>getAppCast().onPlayerDied();
        }
    }

    public void restoreHP() {
        hp.setValue(hp.getMaxHP());
        FXGL.<DungeonCrawlerApp>getAppCast().setHeart1(texture("heart.png", 44, 40));
        FXGL.<DungeonCrawlerApp>getAppCast().setHeart2(texture("heart.png", 44, 40));
        FXGL.<DungeonCrawlerApp>getAppCast().setHeart3(texture("heart.png", 44, 40));
        addUINode(FXGL.<DungeonCrawlerApp>getAppCast().getHeart1(), 15, 15);
        addUINode(FXGL.<DungeonCrawlerApp>getAppCast().getHeart2(), 62, 15);
        addUINode(FXGL.<DungeonCrawlerApp>getAppCast().getHeart3(), 109, 15);
    }

    public int getHp() {
        return hp.getValue();
    }
}
