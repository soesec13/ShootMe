package at.shootme.entity.weapon;


import at.shootme.entity.player.Player;
import at.shootme.entity.shot.Shot;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by Sebi on 23/06/2017.
 */
public abstract class AbstractWeapon {
    protected Player owner;
    protected float shootSpeed;
    protected float currentDelay;
    protected final World world;

    public AbstractWeapon(Player owner,World world) {
        this.owner = owner;
        this.world = world;
    }

    public AbstractWeapon(World world) {
        this.world = world;
    }

    protected abstract Shot createBullet(Vector2 position, Vector2 clickPosition);

    public Shot fire(Vector2 clickPosition)
    {
        if(!canFire())
            return null;
        Shot shot = createBullet(owner.getBody().getPosition(),clickPosition);
        currentDelay = shootSpeed;
        return shot;
    }

    public void update(float delta)
    {
        currentDelay -= delta;
    }
    public void render()
    {

    }
    private boolean canFire()
    {
        return currentDelay <= 0;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }
}
