package at.shootme.beans;

import at.shootme.ShootMeVariables;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import static at.shootme.beans.HorizontalMovementState.*;
import static at.shootme.beans.VerticalMovementState.*;

/**
 * Created by Alexander Dietrich on 01.05.2017.
 */
public class Player implements ShootMeVariables{

    private Sprite sprite;
    private Body body;
    private Fixture fixture;
    private HorizontalMovementState horizontalMovementState = STOPPED;
    private VerticalMovementState verticalMovementState = STANDING;

    private static final String TEXTUREPATH = "assets/badlogic.jpg";

    public Player() {
    }

    public void init(Vector2 position, World world) {

        Texture texture = new Texture(TEXTUREPATH);
        sprite = new Sprite(texture);

       /* sprite.setSize(35f, 175f); resizing sprite
        sprite.setOriginCenter();*/


        sprite.setPosition(position.x, position.y);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        bodyDef.position.set((sprite.getX() + sprite.getWidth() / 2) * PIXELS_TO_METERS,
                (sprite.getY() + sprite.getHeight() / 2) * PIXELS_TO_METERS);

        body = world.createBody(bodyDef);

        body.setFixedRotation(true);

        PolygonShape shape = new PolygonShape();

        shape.setAsBox(sprite.getWidth() / 2*PIXELS_TO_METERS, sprite.getHeight() / 2 * PIXELS_TO_METERS);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.1f;

        fixture = body.createFixture(fixtureDef);
        shape.dispose();
    }

    public Sprite getSprite() {
        return sprite;
    }

    public Body getBody() {
        return body;
    }

    public Fixture getFixture() {
        return fixture;
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
        float desiredHorizontalVelocity = 0;
        if (horizontalMovementState == HorizontalMovementState.STOPPING && Math.abs(velocity.x) < 0.001f) {
            horizontalMovementState = STOPPED;
        }

        switch (horizontalMovementState) {
            case LEFT:
                desiredHorizontalVelocity = Math.max(velocity.x - 1f, -5f);
                break;
            case STOPPING:
                desiredHorizontalVelocity = velocity.x;
                break;
            case RIGHT:
                desiredHorizontalVelocity = Math.min(velocity.x + 1f, 5f);
                break;
            case STOPPED:
                desiredHorizontalVelocity = 0;
        }

        float horizontalVelocityChange = desiredHorizontalVelocity - velocity.x;
        float horizontalForce = body.getMass() * horizontalVelocityChange;

        //Calculating vertical movement
        float verticalForce = 0;
        switch (verticalMovementState) {
            case AIRBORN:
            case STANDING:
                verticalForce = 0 * body.getMass();
                break;
            case JUMPING:
                verticalForce = 20* body.getMass();
                verticalMovementState = STANDING;
                break;
        }


        body.applyLinearImpulse(new Vector2(horizontalForce, verticalForce), body.getWorldCenter(), true);
        System.out.println(""+body.getLinearVelocity().x +"  "+ body.getLinearVelocity().y);
    }

    public void jump() {
        if(verticalMovementState != AIRBORN) verticalMovementState = JUMPING;
    }

    public void drawSprite(SpriteBatch batch) {
        sprite.setPosition(body.getPosition().x * METERS_TO_PIXELS - sprite.getWidth() / 2, body.getPosition().y * METERS_TO_PIXELS - sprite.getHeight() / 2);
        sprite.draw(batch);
    }
}