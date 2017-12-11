package nyc.mok.game.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import nyc.mok.game.components.BattleBehaviorComponent;
import nyc.mok.game.components.MoveTargetsComponent;
import nyc.mok.game.components.PhysicsBody;

/**
 * Created by taco on 12/11/17.
 */

public class MovementSystem extends EntityProcessingSystem {
	private ComponentMapper<PhysicsBody> physicsBodyMapper;
	private ComponentMapper<BattleBehaviorComponent> battleBehaviorMapper;
	private ComponentMapper<MoveTargetsComponent> moveTargetsMapper;

	Vector2 acc = new Vector2();
	Vector2 acc2 = new Vector2();

	public MovementSystem() {
		super(Aspect.all(PhysicsBody.class, BattleBehaviorComponent.class, MoveTargetsComponent.class));
	}

	@Override
	protected void process(Entity e) {
		PhysicsBody physicsBody = physicsBodyMapper.get(e);
		MoveTargetsComponent moveTargets = moveTargetsMapper.get(e);
		BattleBehaviorComponent battleBehavior = battleBehaviorMapper.get(e);

		if (moveTargets.entityToMoveTowards != -1) {
			PhysicsBody targetPhysicsBody = physicsBodyMapper.get(moveTargets.entityToMoveTowards);

			// Move towards target
			//		physicsBody.body.setLinearVelocity(targetPhysicsBody.body.getPosition().x - physicsBody.body.getPosition().x,
			//				targetPhysicsBody.body.getPosition().y - physicsBody.body.getPosition().y);
			//
			//		physicsBody.body.getLinearVelocity().nor().scl(moveTargetsMapper.get(e).maxSpeed);

			acc.set(targetPhysicsBody.body.getPosition().x - physicsBody.body.getPosition().x,
					targetPhysicsBody.body.getPosition().y - physicsBody.body.getPosition().y);

			float maxSpeed = moveTargetsMapper.get(e).maxSpeed;

			acc.nor().scl(maxSpeed / 4);

			physicsBody.body.applyLinearImpulse(acc.x, acc.y,
					physicsBody.body.getPosition().x,
					physicsBody.body.getPosition().y, true);

			physicsBody.body.getLinearVelocity().clamp(0, maxSpeed);


//			float desiredAngularVelocity = angleDiff * 1;
//
//			physicsBody.body.setAngularDamping(0.5f);
//			physicsBody.body.applyAngularImpulse(angleDiff , true);
		}

		if (battleBehavior.target != -1) {
			PhysicsBody targetPhysicsBody = physicsBodyMapper.get(battleBehavior.target);

			physicsBody.body.setAngularVelocity(0);

			Vector2 direction = acc;
			direction.set(targetPhysicsBody.body.getPosition().x - physicsBody.body.getPosition().x,
					targetPhysicsBody.body.getPosition().y - physicsBody.body.getPosition().y);


			float desiredAngle = direction.angle();
			float currentAngle = physicsBody.body.getAngle();
			currentAngle = (float) Math.toDegrees(currentAngle);

			float angleDiff = desiredAngle - currentAngle;

			if (angleDiff > 2 || angleDiff < -2) {
				float directionToSpin = angleDiff > 0 ? 1 : -1;

				if (angleDiff < -180) {
					angleDiff += 360;
					directionToSpin = 1;
				}
				if (angleDiff > 180) {
					angleDiff -= 360;
					directionToSpin = -1;
				}

				physicsBody.body.applyTorque(MathUtils.radDeg * angleDiff / moveTargets.torqueFactor, true);
				//physicsBody.body.setAngularVelocity((float) Math.toRadians(angleDiff));
			}
		} else {
			physicsBody.body.setAngularVelocity(0);
		}
	}
}
