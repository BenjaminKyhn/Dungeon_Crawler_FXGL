import com.almasb.fxgl.entity.components.IntegerComponent;

public class HPComponent extends IntegerComponent {

    private final int startingHP;

    public HPComponent(int hp) {
        super(hp);
        startingHP = hp;
    }

    public int getStartingHP() {
        return startingHP;
    }
}
