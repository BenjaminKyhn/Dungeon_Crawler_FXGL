import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.entity.components.IrremovableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

public class DungeonCrawlerFactory implements EntityFactory {

    @Spawns("player")
    public Entity newPlayer(SpawnData data){
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);

        return entityBuilder()
                .type(DungeonCrawlerType.PLAYER)
                .from(data)
                //TODO: Fix hitboxes for players, weapon and enemies to match the sprites
                .bbox(new HitBox(BoundingShape.box(48,66)))
                .with(new CollidableComponent(true))
                .with(new IrremovableComponent())
                .with(new HPComponent(30))
                .with(new PlayerComponent())
                .build();
    }

    @Spawns("weapon")
    public Entity newWeapon(SpawnData data){
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);

        return entityBuilder()
                .type(DungeonCrawlerType.WEAPON)
                .from(data)
                .viewWithBBox(texture("weapon.png"))
                .bbox(new HitBox(BoundingShape.box(48,66)))
                .with(new CollidableComponent(true))
                .with(new IrremovableComponent())
                .with(new WeaponComponent())
                .build();
    }

    @Spawns("hpfountain")
    public Entity newHPFountain(SpawnData data){
        return entityBuilder()
                .type(DungeonCrawlerType.HPFOUNTAIN)
                .from(data)
                .viewWithBBox(texture("hpfountain.png").toAnimatedTexture(3, Duration.seconds(0.8)).loop())
                .with(new CollidableComponent(true))
                .build();
    }

    @Spawns("mpfountain")
    public Entity newMPFountain(SpawnData data){
        return entityBuilder()
                .type(DungeonCrawlerType.MPFOUNTAIN)
                .from(data)
                .viewWithBBox(texture("mpfountain.png").toAnimatedTexture(3, Duration.seconds(0.8)).loop())
                .with(new CollidableComponent(true))
                .build();
    }

    @Spawns("wall")
    public Entity newWall(SpawnData data){
        return entityBuilder()
                .type(DungeonCrawlerType.WALL)
                .from(data)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new PhysicsComponent())
                .with(new CollidableComponent(true))
                .build();
    }

    @Spawns("rightwall")
    public Entity newRightWall(SpawnData data){
        return entityBuilder()
                .type(DungeonCrawlerType.RIGHTWALL)
                .from(data)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new PhysicsComponent())
                .with(new CollidableComponent(true))
                .build();
    }

    @Spawns("leftwall")
    public Entity newLeftWall(SpawnData data){
        return entityBuilder()
                .type(DungeonCrawlerType.LEFTWALL)
                .from(data)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new PhysicsComponent())
                .with(new CollidableComponent(true))
                .build();
    }

    @Spawns("topwall")
    public Entity newTopWall(SpawnData data){
        return entityBuilder()
                .type(DungeonCrawlerType.TOPWALL)
                .from(data)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new PhysicsComponent())
                .with(new CollidableComponent(true))
                .build();
    }

    @Spawns("bottomwall")
    public Entity neBottomwWall(SpawnData data){
        return entityBuilder()
                .type(DungeonCrawlerType.BOTTOMWALL)
                .from(data)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new PhysicsComponent())
                .with(new CollidableComponent(true))
                .build();
    }

    @Spawns("pillar")
    public Entity newPillar(SpawnData data){
        return entityBuilder()
                .type(DungeonCrawlerType.PILLAR)
                .from(data)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new PhysicsComponent())
                .with(new CollidableComponent(true))
                .build();
    }

    @Spawns("stairs")
    public Entity newStairs(SpawnData data){
        return entityBuilder()
                .type(DungeonCrawlerType.STAIRS)
                .from(data)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new CollidableComponent(true))
                .build();
    }

    @Spawns("door")
    public Entity newDoor(SpawnData data){
        return entityBuilder()
                .type(DungeonCrawlerType.DOOR)
                .from(data)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new CollidableComponent(true))
                .build();
    }

    @Spawns("enemy")
    public Entity newEnemy(SpawnData data){
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);

        return entityBuilder()
                .type(DungeonCrawlerType.ENEMY)
                .from(data)
                .bbox(new HitBox(BoundingShape.box(64,64)))
                .with(new CollidableComponent(true))
                .with(new HPComponent(100))
                .with(new EnemyComponent())
                .build();
    }

    @Spawns("goblin")
    public Entity newGoblin(SpawnData data){
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);

        return entityBuilder()
                .type(DungeonCrawlerType.ENEMY)
                .from(data)
                .bbox(new HitBox(BoundingShape.box(64,64)))
                .with(new CollidableComponent(true))
                .with(new HPComponent(50))
                .with(new GoblinComponent())
                .build();
    }
}
