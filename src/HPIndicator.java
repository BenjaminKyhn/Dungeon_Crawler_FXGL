import com.almasb.fxgl.animation.Interpolators;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class HPIndicator extends StackPane {

    private static final int BARLENGTH = 200;
    private static final int BARHEIGHT = 40;

    private HPComponent playerHP;

    private Rectangle inner;

    public HPIndicator(HPComponent playerHP) {
        this.playerHP = playerHP;

        inner = new Rectangle(BARLENGTH,BARHEIGHT,Color.GREEN);

        inner.fillProperty().bind(
                Bindings.when(playerHP.valueProperty().divide(playerHP.getMaxHP() * 1.0).greaterThan(0.25)).then(Color.GREEN.brighter()).otherwise(Color.RED.brighter())
        );

        playerHP.valueProperty().addListener((o, old, hp) -> {
            hpChanged(hp.intValue());
        });

        getChildren().addAll(inner);
    }

    private void hpChanged(int hp) {
        var timeline = new Timeline();
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.seconds(0.66), new KeyValue(inner.widthProperty(), hp * 1.0 / playerHP.getMaxHP() * BARLENGTH))
        );
        timeline.play();
    }
}
