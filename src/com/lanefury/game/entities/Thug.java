package com.lanefury.game.entities;

import java.util.Iterator;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.lanefury.game.world.GameLevel;
import com.badlogic.gdx.utils.Timer.Task;

public class Thug extends AnimatedActor {

	private int health = 100;
	private String spriteName;
	private String charName;
	private static MapObjects wallObjects;
	private int thugSpeed = 25;
	private Player mainPlayer;
	private int distanceFromPlayer;
	private int randomFactor;
	private int randomDamageFactor;
	private int randomFactorWhenCollision;
	private boolean collisionLeft = false;
	private boolean collisionRight = false;
	private boolean collisionUp = false;
	private boolean collisionDown = false;
	private float delay = 1;
	private float delayDueToCollisionX = 3;
	private float delayDueToCollisionY = 1;
	private boolean isDamaged = false;
	private boolean isFacingRight = true;
	private boolean isAlive = true;
	private boolean shouldThugMove = false;
	private boolean facingRight = true; // determine if character is currently facing right

	private PlayerAnimations playerAnimations; // store animations for character

	public Thug(String spriteName, MapObjects wallObjects, Player mainPlayer, String charName) {
		super();
		this.spriteName = spriteName;
		this.wallObjects = wallObjects;
		this.playerAnimations = new PlayerAnimations(spriteName); // set all the animations for the character
		this.setAnimation(playerAnimations.getStationary());
		this.mainPlayer = mainPlayer;
		this.charName = charName;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {

		if (isAlive) {
			update(Gdx.graphics.getDeltaTime()); // call thug update method
			super.draw(batch, parentAlpha);
		}

	}

	public void update(float dt) {

		// check if dead

		if (health < 1) {

			Timer.schedule(new Task() {
				@Override
				public void run() {
					isAlive = false;
					setOffScreen();
				}
			}, 3);

			killThug();
		} else {

			// Just verifying if thug is colliding
//			Timer.schedule(new Task() {
//				@Override
//				public void run() {
//					System.out.println("CollisionDown: " + collisionDown);
//					System.out.println("CollisionUp: " + collisionUp);
//					System.out.println("CollisionRight: " + collisionRight);
//					System.out.println("CollisionLeft: " + collisionLeft);
//				}
//			}, 2);	

			// Get mainPlayer's coordinates

			float playerX = mainPlayer.getX();
			float playerY = mainPlayer.getY();

			float tempX = this.getX();
			float tempY = this.getY();

			if (this.velocityX < 0) { // going left
				this.setAnimation(playerAnimations.getRight());
				if (isCollision(getX(), getY())) {
					velocityX = 0;
					facingRight = false;
					this.setX(tempX + 2);
					collisionLeft = true;
//					System.out.println("thug Collision left");

					Timer.schedule(new Task() {
						@Override
						public void run() {
							collisionLeft = false;
						}
					}, delayDueToCollisionX);
				}
			}

			if (this.velocityX > 0) { // going right
				this.setAnimation(playerAnimations.getRight());
				if (isCollision(getX(), getY())) {
					velocityX = 0;
					facingRight = true;
					this.setX(tempX - 2);
					collisionRight = true;
//					System.out.println("thug Collision right");

					Timer.schedule(new Task() {
						@Override
						public void run() {
							collisionRight = false;
						}
					}, delayDueToCollisionX);
				}
			}

			if (this.velocityY < 0) { // going down
				this.setAnimation(playerAnimations.getRight());
				if (isCollision(getX(), getY())) {
					velocityY = 0;
					this.setY(tempY + 2);
					collisionDown = true;
//					System.out.println("thug Collision down");

					Timer.schedule(new Task() {
						@Override
						public void run() {
							collisionDown = false;
						}
					}, delayDueToCollisionY);
				}
			}

			if (this.velocityY > 0) { // going up
				this.setAnimation(playerAnimations.getRight());
				if (isCollision(getX(), getY())) {
					velocityY = 0;
					this.setY(tempY - 2);
					collisionUp = true;
//					System.out.println("thug Collision up");

					Timer.schedule(new Task() {
						@Override
						public void run() {
							collisionUp = false;
						}
					}, delayDueToCollisionY);
				}
			}

			// Move the thug towards the player, but add a bit of delay

			Timer.schedule(new Task() {
				@Override
				public void run() {

					// only let thug move if he isn't damaged
					if (!isDamaged) {
						thugMove(tempX, tempY, playerX, playerY);		
					}	
				}
			}, delay);

		}

	}

	// add some random movement to the thug if randomFactor is a number 1-4

	public void randomMovement(int randomFactor) {

		if (randomFactor == 1) { // Move up
			velocityY = getThugSpeed();
		} else if (randomFactor == 2) { // Move left
			velocityX = (-getThugSpeed());
		} else if (randomFactor == 3) { // Move down
			velocityY = (-getThugSpeed());
		} else { // Move right
			velocityX = getThugSpeed();
		}

	}

	// calculate thug's movement & set his animations for movement
	public void thugMove(float tempX, float tempY, float playerX, float playerY) {

		randomFactor = (int) ((Math.random() * (500)) + 1);
		// randomFactorWhenCollision = (int) ((Math.random() * (4)) + 1);

		if ((Math.abs(tempX - playerX) < 300) && (Math.abs(tempX - playerX) >= 50)) {
			shouldThugMove = true;
		} else {
			shouldThugMove = false;
			
			if ((Math.abs(tempX - playerX) < 50) && (Math.abs(tempY - playerY) < 50)) {
				
				randomDamageFactor = (int) ((Math.random() * (1000)) + 1);
				velocityX = 0;
				velocityY = 0;
				
				if (randomDamageFactor == 1) {
					System.out.println(this.getCharName() + " thug punching");
					punch();
				}
				if (randomDamageFactor == 2) {
					System.out.println(this.getCharName() + " thug kicking");
					kick();
				}
				
			}
		}
		

		if ((randomFactor < 5) && shouldThugMove) {
			// System.out.println(randomFactor);
			randomMovement(randomFactor);
		} else {

			if ((tempY < playerY && shouldThugMove) && (!collisionUp)) { // Need to go up to chase player

				this.setAnimation(playerAnimations.getRight()); // walking animation going up

//				System.out.println("THUG CAN MOVE UP");

				velocityY = getThugSpeed();
			}

			if ((tempY > playerY && shouldThugMove) && (!collisionDown)) { // Need to go down to chase player

				this.setAnimation(playerAnimations.getRight()); // walking animation going up

//				System.out.println("THUG CAN MOVE DOWN");

				velocityY = (-getThugSpeed());
			}

			if ((tempX < playerX && shouldThugMove) && (!collisionRight)) { // Need to go right to chase player

				isFacingRight = true;

				for (TextureRegion textureRegion : playerAnimations.getRight().getKeyFrames()) {
					if (textureRegion.isFlipX()) { // if walking animation is not facing right, then flip it
						textureRegion.flip(true, false);
					}
				}

				for (TextureRegion textureRegion : playerAnimations.getStationary().getKeyFrames()) {
					if (textureRegion.isFlipX()) { // if stationary animation not facing right, then flip it
						textureRegion.flip(true, false);
					}
				}

				this.setAnimation(playerAnimations.getRight());

//				System.out.println("CAN MOVE RIGHT");

				velocityX = getThugSpeed();
			}

			if ((tempX > playerX && shouldThugMove) && (!collisionLeft)) { // Need to go left to chase player

				isFacingRight = false;

				for (TextureRegion textureRegion : playerAnimations.getRight().getKeyFrames()) {
					if (!textureRegion.isFlipX()) { // if walking animation not facing left, then flip it
						textureRegion.flip(true, false);
					}
				}

				for (TextureRegion textureRegion : playerAnimations.getStationary().getKeyFrames()) {
					if (!textureRegion.isFlipX()) { // if stationary animation not facing left, then flip it
						textureRegion.flip(true, false);
					}
				}

				this.setAnimation(playerAnimations.getRight());
//				System.out.println("THUG CAN MOVE LEFT");	
				velocityX = (-getThugSpeed());
			}
		}
	}

	public PlayerAnimations getPlayerAnimations() {
		return playerAnimations;
	}

	public void setPlayerAnimations(PlayerAnimations playerAnimations) {
		this.playerAnimations = playerAnimations;
	}

	public boolean isCollision(float x, float y) {
		for (PolygonMapObject polygonObject : wallObjects.getByType(PolygonMapObject.class)) {
			Polygon polygon = polygonObject.getPolygon();

			if (isCollisionPolygon(polygon, this.getCollisionRectangle())) {
				return true;
			}
		}
		return false;
	}

	// Check if Polygon intersects Rectangle
	private boolean isCollisionPolygon(Polygon p, Rectangle r) {
		Polygon rPoly = new Polygon(new float[] { 0, 0, r.width, 0, r.width, r.height, 0, r.height });
		rPoly.setPosition(r.x, r.y);
		if (Intersector.overlapConvexPolygons(rPoly, p))
			return true;
		return false;
	}

	public void reduceHealth(int hitType) {

		if (hitType == 0) { // if hit is a punch
			health -= 10;
		} else { // if hit is a kick
			health -= 20;
		}
	}

	public int getHealth() {
		return health;
	}
	
	public void setHealth(int health) {
		this.health = health;
	}

	public void setIsDamaged(boolean val) {
		isDamaged = val;
	}

	public boolean getIsDamaged() {
		return isDamaged;
	}

	public boolean isFacingRight() {
		return isFacingRight;
	}

	public String getCharName() {
		return charName;
	}

	private void killThug() {

		// send thug flying when he dies

		if (isFacingRight) {
			velocityY = 5;
			velocityX = -20;

		} else {
			velocityY = 5;
			velocityX = 20;
		}

		if (isFacingRight()) { // thug facing right
			for (TextureRegion textureRegion : getPlayerAnimations().getDying().getKeyFrames()) {
				if (textureRegion.isFlipX()) { // if dying anim is facing left, flip it to face right
					textureRegion.flip(true, false);
				}
			}

		} else { // thug facing left
			for (TextureRegion textureRegion : getPlayerAnimations().getDying().getKeyFrames()) {
				if (!textureRegion.isFlipX()) { // if dying anim is facing right, flip it to face left
					textureRegion.flip(true, false);
				}
			}
		}

		setDyingAnim();
	}

	public void setDyingAnim() {
		this.setAnimation(playerAnimations.getDying());
	}

	public void setOffScreen() {
		this.setX(0);
		this.setY(700);
	}
	
public void punch() {
		
		// was just testing to see health deduction working
		
//		this.setHealth(this.getHealth() - 10);
		
		for (TextureRegion textureRegion : playerAnimations.getPunch().getKeyFrames()) {
			if (!facingRight && (!textureRegion.isFlipX())) { // if punching animation is not facing right, then flip it
				textureRegion.flip(true,false);
			}
			if (facingRight && (textureRegion.isFlipX())) { // if punching animation is not facing right, then flip it
				textureRegion.flip(true,false);
			}
		}
		
		this.setAnimation(playerAnimations.getPunch());
		
		// Logic for damaging player
		
		checkForDamagePlayer(0); // pass 0 for punch
		
		System.out.println("THUG IS PUNCHING");
	}
	
	// player kick function
	public void kick() {
		for (TextureRegion textureRegion : playerAnimations.getKick().getKeyFrames()) {
			if (!facingRight && (!textureRegion.isFlipX())) { // if punching animation is not facing right, then flip it
				textureRegion.flip(true,false);
			}
			if (facingRight && (textureRegion.isFlipX())) { // if punching animation is not facing right, then flip it
				textureRegion.flip(true,false);
			}
		}
		
		this.setAnimation(playerAnimations.getKick());
		
		// Logic for damaging player
		
		checkForDamagePlayer(1); // pass 1 for kick
		
		System.out.println("THUG IS KICKING");
	}
	
	private void checkForDamagePlayer(int hitType) {
		
		if (Intersector.overlaps(this.getBoundingRectangle(), mainPlayer.getBoundingRectangle())){
			
			// only damage the thug if he isn't already in a damaged state
			if (!mainPlayer.getIsDamaged()) {
				
				System.out.println("THUG HIT THE PLAYER!!!");
				System.out.println("MainPlayer IsDamaged: " + mainPlayer.getIsDamaged());
				
				damagePlayer(hitType);
			}
		}
	}

	private void damagePlayer(int hitType) {
		
		mainPlayer.reduceHealth(hitType);
		mainPlayer.setIsDamaged(true);
		mainPlayer.velocityX = 0;
		mainPlayer.velocityY = 0;
		
		if (mainPlayer.isFacingRight()) { // thug facing right
			for (TextureRegion textureRegion : mainPlayer.getPlayerAnimations().getDamaged().getKeyFrames()) {
				if (textureRegion.isFlipX()) { // if damaged anim is facing left, flip it to face right
					textureRegion.flip(true,false);
				}
			}
		} else { // thug facing left
			for (TextureRegion textureRegion : mainPlayer.getPlayerAnimations().getDamaged().getKeyFrames()) {
				if (!textureRegion.isFlipX()) { // if damaged anim is facing right, flip it to face left
					textureRegion.flip(true,false);
				}
			}
		}
		
		mainPlayer.setAnimation(mainPlayer.getPlayerAnimations().getDamaged());
			
		Timer.schedule(new Task() {
			@Override
			public void run() {
				mainPlayer.setIsDamaged(false);
			}
		}, 1);	
		
	}
	
	public boolean isAlive() {
		return isAlive;
	}
	
	public String getSpriteName() {
		return spriteName;
	}
	
	public void setSpriteName(String spriteName) {
		this.spriteName = spriteName;
	}

	public int getThugSpeed() {
		return thugSpeed;
	}

	public void setThugSpeed(int thugSpeed) {
		this.thugSpeed = thugSpeed;
	}

}
