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

import static com.almasb.fxgl.dsl.FXGL.image;
import static com.almasb.fxgl.dsl.FXGL.runOnce;

public class WeaponComponent extends Component {

    private double speed = 5;
    private boolean isBeingDamaged = false;

    public WeaponComponent() {
    }

    @Override
    public void onAdded() {
    }

    @Override
    public void onUpdate(double tpf) {
    }

    public void left() {
        entity.translateX(-speed);
    }

    public void right() {
        entity.translateX(speed);
    }

    public void up() {
        entity.translateY(-speed);
    }

    public void down() {
        entity.translateY(speed);
    }

    public void faceLeft(){
        getEntity().setScaleX(-1); //Changes the direction of the sprite
        entity.translateX(-114);
    }

    public void faceRight(){
        getEntity().setScaleX(1); //Changes the direction of the sprite
        entity.translateX(114);
    }

    public void attackRight(){
        if (FXGL.<DungeonCrawlerApp>getAppCast().getWeapons().contains("greatsword")){
            entity.rotateBy(90);
            entity.translateX(20);
            entity.translateY(40);
        }
        else {
            entity.rotateBy(45);
            entity.translateY(20);
        }
    }

    public void undoAttackRight(){
        if (FXGL.<DungeonCrawlerApp>getAppCast().getWeapons().contains("greatsword")){
            entity.rotateBy(-90);
            entity.translateX(-20);
            entity.translateY(-40);
        }
        else {
            entity.rotateBy(-45);
            entity.translateY(-20);
        }
    }

    public void attackLeft(){
        if (FXGL.<DungeonCrawlerApp>getAppCast().getWeapons().contains("greatsword")){
            entity.rotateBy(-90);
            entity.translateX(20);
            entity.translateY(40);
        }
        else {
            entity.rotateBy(-45);
            entity.translateY(20);
        }
    }

    public void undoAttackLeft(){
        if (FXGL.<DungeonCrawlerApp>getAppCast().getWeapons().contains("greatsword")){
            entity.rotateBy(90);
            entity.translateX(-20);
            entity.translateY(-40);
        }
        else {
            entity.rotateBy(45);
            entity.translateY(-20);
        }
    }

    public void juggle(){
        entity.rotateBy(45);
    }
}
