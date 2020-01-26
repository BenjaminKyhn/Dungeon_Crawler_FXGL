import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.LiftComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.entity.components.IrremovableComponent;
import com.almasb.fxgl.input.view.KeyView;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

public class DungeonCrawlerFactory implements EntityFactory {

    @Spawns("player")
    public Entity newPlayer(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);

        return entityBuilder()
                .type(DungeonCrawlerType.PLAYER)
                .from(data)
                //TODO: Fix hitboxes for players, weapon and enemies to match the sprites
//                .bbox(new HitBox(BoundingShape.box(48,66)))
                .bbox(new HitBox("main", new Point2D(9, 18), BoundingShape.box(30, 48)))
                .with(new CollidableComponent(true))
                .with(new IrremovableComponent())
                .with(new HPComponent(30))
                .with(new PlayerComponent())
                .build();
    }

    @Spawns("weapon")
    public Entity newWeapon(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);

        return entityBuilder()
                .type(DungeonCrawlerType.WEAPON)
                .from(data)
                .viewWithBBox(texture("weapon.png"))
                .bbox(new HitBox(BoundingShape.box(48, 66)))
                .with(new CollidableComponent(true))
                .with(new IrremovableComponent())
                .with(new WeaponComponent())
                .build();
    }

    @Spawns("hpfountain")
    public Entity newHPFountain(SpawnData data) {
        return entityBuilder()
                .type(DungeonCrawlerType.HPFOUNTAIN)
                .from(data)
                .viewWithBBox(texture("hpfountain.png").toAnimatedTexture(3, Duration.seconds(0.8)).loop())
                .with(new CollidableComponent(true))
                .build();
    }

    @Spawns("mpfountain")
    public Entity newMPFountain(SpawnData data) {
        return entityBuilder()
                .type(DungeonCrawlerType.MPFOUNTAIN)
                .from(data)
                .viewWithBBox(texture("mpfountain.png").toAnimatedTexture(3, Duration.seconds(0.8)).loop())
                .with(new CollidableComponent(true))
                .build();
    }

    @Spawns("keyCode")
    public Entity newKeyCode(SpawnData data) {
        String key = data.get("key");

        KeyCode keyCode = KeyCode.getKeyCode(key);

        var lift = new LiftComponent();
        lift.setGoingUp(true);
        lift.yAxisDistanceDuration(6, Duration.seconds(0.76));

        return entityBuilder()
                .from(data)
                .view(new KeyView(keyCode, Color.YELLOW, 24))
                .with(lift)
                .zIndex(100)
                .build();
    }

    @Spawns("button")
    public Entity newButton(SpawnData data) {
        var keyEntity = FXGL.getGameWorld().create("keyCode", new SpawnData(data.getX() + 20, data.getY() + 20).put("key", "E"));
        keyEntity.getViewComponent().opacityProperty().setValue(0);

        return entityBuilder()
                .type(DungeonCrawlerType.BUTTON)
                .from(data)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new CollidableComponent(true))
                .with("keyEntity", keyEntity)
                .build();
    }

    @Spawns("wall")
    public Entity newWall(SpawnData data) {
        return entityBuilder()
                .type(DungeonCrawlerType.WALL)
                .from(data)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new PhysicsComponent())
                .with(new CollidableComponent(true))
                .build();
    }

    @Spawns("rightwall")
    public Entity newRightWall(SpawnData data) {
        return entityBuilder()
                .type(DungeonCrawlerType.RIGHTWALL)
                .from(data)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new PhysicsComponent())
                .with(new CollidableComponent(true))
                .build();
    }

    @Spawns("leftwall")
    public Entity newLeftWall(SpawnData data) {
        return entityBuilder()
                .type(DungeonCrawlerType.LEFTWALL)
                .from(data)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new PhysicsComponent())
                .with(new CollidableComponent(true))
                .build();
    }

    @Spawns("topwall")
    public Entity newTopWall(SpawnData data) {
        return entityBuilder()
                .type(DungeonCrawlerType.TOPWALL)
                .from(data)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new PhysicsComponent())
                .with(new CollidableComponent(true))
                .build();
    }

    @Spawns("bottomwall")
    public Entity neBottomwWall(SpawnData data) {
        return entityBuilder()
                .type(DungeonCrawlerType.BOTTOMWALL)
                .from(data)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new PhysicsComponent())
                .with(new CollidableComponent(true))
                .build();
    }

    @Spawns("trap")
    public Entity newTrap(SpawnData data) {
        return entityBuilder()
                .type(DungeonCrawlerType.TRAP)
                .from(data)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new PhysicsComponent())
                .with(new CollidableComponent(true))
                .build();
    }

    @Spawns("righttrapwall")
    public Entity newRightTrapWall(SpawnData data) {
        return entityBuilder()
                .type(DungeonCrawlerType.RIGHTTRAPWALL)
                .from(data)
                .viewWithBBox(texture("right_trap_wall.png"))
                .with(new PhysicsComponent())
                .with(new CollidableComponent(true))
                .build();
    }

    @Spawns("stairs")
    public Entity newStairs(SpawnData data) {
        return entityBuilder()
                .type(DungeonCrawlerType.STAIRS)
                .from(data)
                .viewWithBBox(texture("stairs.png"))
//                .bbox(new HitBox(BoundingShape.box(64,64)))
//                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new CollidableComponent(true))
                .build();
    }

    @Spawns("redswitch")
    public Entity newRedSwitch(SpawnData data) {
        return entityBuilder()
                .type(DungeonCrawlerType.REDSWITCH)
                .from(data)
                .viewWithBBox(texture("red_switch.png"))
                .with(new CollidableComponent(true))
                .build();
    }

    @Spawns("blueswitch")
    public Entity newBlueSwitch(SpawnData data) {
        return entityBuilder()
                .type(DungeonCrawlerType.BLUESWITCH)
                .from(data)
                .viewWithBBox(texture("blue_switch.png"))
                .with(new CollidableComponent(true))
                .build();
    }

    @Spawns("hole")
    public Entity newHole(SpawnData data) {
        return entityBuilder()
                .type(DungeonCrawlerType.HOLE)
                .from(data)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new CollidableComponent(true))
                .build();
    }

    @Spawns("spikes")
    public Entity newSpikes(SpawnData data) {
        return entityBuilder()
                .type(DungeonCrawlerType.SPIKES)
                .from(data)
                .viewWithBBox(texture("spikes.png").toAnimatedTexture(5, Duration.seconds(2)).loop())
                .with(new CollidableComponent(true))
                .build();
    }

    @Spawns("door")
    public Entity newDoor(SpawnData data) {
        return entityBuilder()
                .type(DungeonCrawlerType.DOOR)
                .from(data)
                .viewWithBBox(texture("door.png"))
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new CollidableComponent(true))
                .build();
    }

    @Spawns("demon")
    public Entity newDemon(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);

        return entityBuilder()
                .type(DungeonCrawlerType.ENEMY)
                .from(data)
                .bbox(new HitBox("main", new Point2D(21, 36), BoundingShape.box(54, 87)))
                .with(new CollidableComponent(true))
                .with(new HPComponent(100))
                .with(new DemonComponent())
                .build();
    }

    @Spawns("goblin")
    public Entity newGoblin(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);

        return entityBuilder()
                .type(DungeonCrawlerType.ENEMY)
                .from(data)
                .bbox(new HitBox("main", new Point2D(15, 23), BoundingShape.box(30, 41)))
                .with(new CollidableComponent(true))
                .with(new HPComponent(50))
                .with(new GoblinComponent())
                .build();
    }

    @Spawns("ogre")
    public Entity newOgre(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);

        return entityBuilder()
                .type(DungeonCrawlerType.ENEMY)
                .from(data)
                .bbox(new HitBox("main", new Point2D(24, 6), BoundingShape.box(54, 78)))
                .with(new CollidableComponent(true))
                .with(new HPComponent(150))
                .with(new OgreComponent())
                .build();
    }

    @Spawns("troll")
    public Entity newTroll(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);

        return entityBuilder()
                .type(DungeonCrawlerType.ENEMY)
                .from(data)
                .bbox(new HitBox("main", new Point2D(7, 6), BoundingShape.box(30, 45)))
                .with(new CollidableComponent(true))
                .with(new HPComponent(75))
                .with(new TrollComponent())
                .build();
    }

    @Spawns("imp")
    public Entity newImp(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);

        return entityBuilder()
                .type(DungeonCrawlerType.ENEMY)
                .from(data)
                .bbox(new HitBox("main", new Point2D(9, 12), BoundingShape.box(25, 30)))
                .with(new CollidableComponent(true))
                .with(new HPComponent(25))
                .with(new ImpComponent())
                .build();
    }

    @Spawns("dragon")
    public Entity newDragon(SpawnData data) {

        return entityBuilder()
                .type(DungeonCrawlerType.ENEMY)
                .from(data)
                .bbox(new HitBox("main", new Point2D(4, 15), BoundingShape.box(42, 48)))
                .with(new CollidableComponent(true))
                .with(new HPComponent(100))
                .with(new DragonComponent())
                .build();
    }

    @Spawns("boss")
    public Entity newBoss(SpawnData data) {
        return entityBuilder()
                .type(DungeonCrawlerType.ENEMY)
                .from(data)
                .bbox(new HitBox("main", new Point2D(136, 111), BoundingShape.box(416, 210))) //TODO fix hitbox to be oval/circle or something - trapezoid maybe
                .bbox(new HitBox("leftwing", new Point2D(50, 150), BoundingShape.box(100, 170)))
                .bbox(new HitBox("rightwing", new Point2D(550, 150), BoundingShape.box(100, 170)))
                .bbox(new HitBox("legs", new Point2D(260, 290), BoundingShape.box(180, 100)))
                .bbox(new HitBox("feet", new Point2D(330, 380), BoundingShape.box(50, 50)))
//                .bbox(new HitBox("body", new Point2D(210, 119), BoundingShape.circle(150)))
//                .bbox(new HitBox("body", new Point2D(210, 119), BoundingShape.polygon(-150.0,30, 0,-30, 100,0, 160,55, 200,55, 300,0, 360,-30, 470,30, 470,70, 350,210, 250,200, 160,310, 60,270, 0,180, -150,150)))
//                .bbox(new HitBox("main", new Point2D(0, 0), BoundingShape.polygon(-60.0, 119.0, 210.0, 89.0, 310.0, 119.0, 370.0, 174.0, 410.0, 174.0, 510.0, 119.0, 570.0, 89.0, 680.0, 149.0, 680.0, 189.0, 560.0,329.0, 460.0, 319.0, 370.0, 429.0, 270.0, 389.0, 210.0, 299.0, 60.0, 269.0)))
                .with(new CollidableComponent(true))
                .with(new HPComponent(500))
                .with(new BossComponent())
                .build();
    }
}
