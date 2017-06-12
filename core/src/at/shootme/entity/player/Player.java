package at.shootme.entity.player;

import at.shootme.beans.*;
import at.shootme.beans.ViewDirection;
import at.shootme.entity.EntityCategory;
import at.shootme.entity.general.Drawable;
import at.shootme.entity.general.Entity;
import at.shootme.entity.level.Platform;
import at.shootme.entity.shot.StandardShot;
import at.shootme.util.vectors.Vector2Util;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import static at.shootme.ShootMeConstants.METERS_TO_PIXELS;
import static at.shootme.ShootMeConstants.PIXELS_TO_METERS;

/**
 * Created by Alexander Dietrich on 01.05.2017.
 */
public class Player extends Entity implements Drawable {

    private static final int JUMP_SPEED = 30;

    private Sprite sprite;
    private Body body;
    private Fixture fixture;
    private HorizontalMovementState horizontalMovementState = HorizontalMovementState.STOPPED;
    private ViewDirection viewDirection = ViewDirection.LEFT;

    private String texturepath;

    private int availableJumps = 2;

    public Player() {
    }

    public void init(Vector2 position, World world) {

        Texture texture = new Texture(texturepath);
        sprite = new Sprite(texture);

        sprite.setSize(sprite.getWidth() / 2, sprite.getHeight() / 2);
        sprite.setOriginCenter();

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.linearDamping = 3f;
        bodyDef.position.set(position);

        body = world.createBody(bodyDef);
        body.setUserData(this);
        body.setFixedRotation(true);

        PolygonShape shape = new PolygonShape();

        //The minus 3 makes the polygon slightly smaller than the sprite so there are no visible gaps between the world and the player
        shape.setAsBox((sprite.getWidth() - 3) / 2 * PIXELS_TO_METERS, (sprite.getHeight() - 3) / 2 * PIXELS_TO_METERS);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.friction = 0.6f;
        fixtureDef.density = 1000f;

        fixture = body.createFixture(fixtureDef);
        shape.dispose();
    }

    public Sprite getSprite() {
        return sprite;
    }

    public Fixture getFixture() {
        return fixture;
    }

    public void setTexturepath(String texturepath) {
        this.texturepath = texturepath;
    }

    public HorizontalMovementState getHorizontalMovementState() {
        return horizontalMovementState;
    }

    public void setHorizontalMovementState(HorizontalMovementState horizontalMovementState) {
        this.horizontalMovementState = horizontalMovementState;
    }

    /**
     * This method is called during rendering to move the sprite to the new position determined by the physics engine
     */

    public void move() {
        Vector2 velocity = body.getLinearVelocity();

        //Calculating horizontal movement
        if (horizontalMovementState == HorizontalMovementState.STOPPING && Math.abs(velocity.x) < 0.001f) {
            horizontalMovementState = HorizontalMovementState.STOPPED;
        }

        float desiredHorizontalVelocity = 0;
        switch (horizontalMovementState) {
            case LEFT:
                desiredHorizontalVelocity = Math.max(velocity.x - 5f, -14f);
                if (viewDirection != ViewDirection.LEFT) {
                    sprite.flip(true, false);
                    viewDirection = viewDirection.LEFT;
                }
                break;
            case STOPPING:
                desiredHorizontalVelocity = velocity.x;
                break;
            case RIGHT:
                desiredHorizontalVelocity = Math.min(velocity.x + 5f, 14f);
                if (viewDirection != ViewDirection.RIGHT) {
                    sprite.flip(true, false);
                    viewDirection = ViewDirection.RIGHT;
                }
                break;
            case STOPPED:
                desiredHorizontalVelocity = 0;
        }

        float horizontalVelocityChange = desiredHorizontalVelocity - velocity.x;
        float horizontalForce = body.getMass() * horizontalVelocityChange;

        body.applyLinearImpulse(new Vector2(horizontalForce, 0), body.getWorldCenter(), true);
//        System.out.println(body.getLinearVelocity().x + "  " + body.getLinearVelocity().y);
    }

    public StandardShot shootAt(Vector2 clickPosition) {

        Vector2 playerPosition = body.getPosition();

        float angle = Vector2Util.getAngleFromAToB(playerPosition, clickPosition);
        Vector2 directionVector = Vector2Util.degreeToVector2(angle);

        float distance = playerPosition.dst(clickPosition);
        int initialShotSpeed = (int) (Math.min(Math.max(distance * 8.5, 18), 65.0));
        System.out.println(initialShotSpeed);
        Vector2 initialShotVelocity = directionVector.scl(initialShotSpeed);

        StandardShot shot = new StandardShot(playerPosition, initialShotVelocity, this, getWorld());
//        System.out.println("playerPosition: "+ playerPosition + " --- " + "clickPosition: "+ clickPosition + " --- " + "initialShotVelocity: "+ initialShotVelocity);
//        System.out.println("directionVector: "+ directionVector + " --- " + "angle: "+ angle + " --- ");
        return shot;
    }

    private World getWorld() {
        return body.getWorld();
    }

    public void jumpIfPossible() {
        if (availableJumps > 0) {
            jump();
        }
    }

    private void jump() {
        availableJumps--;

        body.setLinearVelocity(body.getLinearVelocity().x, JUMP_SPEED);
    }


    public void hitGround(Platform platform) {
        availableJumps = 2;
    }

    @Override
    public void draw(SpriteBatch batch) {
        sprite.setPosition(body.getPosition().x * METERS_TO_PIXELS - sprite.getWidth() / 2, body.getPosition().y * METERS_TO_PIXELS - sprite.getHeight() / 2);
        sprite.draw(batch);
    }

    @Override
    public EntityCategory getCategory() {
        return EntityCategory.PLAYER;
    }

    @Override
    public Body getBody() {
        return body;
    }
}
