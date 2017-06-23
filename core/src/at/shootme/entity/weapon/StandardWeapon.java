package at.shootme.entity.weapon;

import at.shootme.entity.player.Player;
import at.shootme.entity.shot.Shot;
import at.shootme.entity.shot.StandardShot;
import at.shootme.util.vectors.Vector2Util;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by Sebi on 23/06/2017.
 */
public class StandardWeapon extends AbstractWeapon {

    public StandardWeapon(World world) {
        super(world);
    }

    public StandardWeapon(Player owner, World world) {
        super(owner, world);
        shootSpeed = 0;
    }

    @Override
    protected Shot createBullet(Vector2 position, Vector2 clickPosition) {
        float angle = Vector2Util.getAngleFromAToB(position, clickPosition);
        Vector2 directionVector = Vector2Util.degreeToVector2(angle);

        float distance = position.dst(clickPosition);
        int initialShotSpeed = (int) (Math.min(Math.max(distance * 8.5, 18), 65.0));
        System.out.println(initialShotSpeed);
        Vector2 initialShotVelocity = directionVector.scl(initialShotSpeed);

        return new StandardShot(position, initialShotVelocity, owner, world);
    }
}
