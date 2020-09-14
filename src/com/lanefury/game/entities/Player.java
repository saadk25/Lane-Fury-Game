package com.lanefury.game.entities;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
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
import com.badlogic.gdx.utils.Timer.Task;
import com.lanefury.game.world.GameLevel;

public class Player extends AnimatedActor implements InputProcessor {

	private int health = 100; // initialize character's health
	private String name; // name of the sprite character that will be used
	private String charName; // name that will appear on playerLabel
	private static MapObjects wallObjects;
	private static int playerSpeed; // playerSpeed variable. Need to modify all speeds to use this instead of hardcoding
	private boolean facingRight = true; // determine if character is currently facing right
	private boolean canPunch = true; // determine if character can currently punch
	private boolean canKick = true; // determine if character can currently kick
	private boolean punchHelper = true; 
	private boolean kickHelper = true;
	private boolean isDamaged = false;
	private HashMap<String, Float> playerCenter = new HashMap<String, Float>(); 
	private HashMap<String, Float> playerMiddle = new HashMap<String, Float>();
	
	private PlayerAnimations playerAnimations; // store animations for character
	
	private String thugLastHit;
	
	public Player(String name, MapObjects wallObjects, String charName) {
		super();
		this.name = name;
		this.wallObjects = wallObjects;
		this.charName = charName;
		this.playerAnimations = new PlayerAnimations(name); // set all the animations for the character
		this.setAnimation(playerAnimations.getStationary());
//		this.listOfEnemies = listOfEnemies;
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		update(Gdx.graphics.getDeltaTime()); // call Player update method
		super.draw(batch, parentAlpha);
	}
	
	public void update(float dt) {
			
		// Set player's coordinates into playerCenter hashmap
		// This will not shift map when the player punches
//		if (canPunch) {	
//			this.playerCenter.put("x", this.getX() + this.getOriginX());
//			this.playerCenter.put("y", this.getY() + this.getOriginY());	
//			this.playerMiddle.put("x", this.getX() + this.getWidth() / 2);
//			this.playerMiddle.put("y", this.getY() + this.getHeight() / 2);
//		}
		
		float tempX = this.getX();
		float tempY = this.getY();
		
		if (this.velocityX < 0) { // going left
			
			// Get rid of any other animations if moving
			this.setAnimation(playerAnimations.getRight());
			
			// set position while moving in case attacks pressed at same time as movement
			this.setPlayerCameraPosition();
			
			if (isCollision(getX(), getY())) {
				velocityX = 0;
				this.setX(tempX + 2);
				System.out.println("Collision left");
			}
			
			if (isTeleportDoor(getX(), getY())) {
				setBossPosition();
			}
		}
		
		if (this.velocityX > 0) { // going right
			this.setAnimation(playerAnimations.getRight());
			
			// set position while moving in case attacks pressed at same time as movement
			this.setPlayerCameraPosition();
			
			if (isCollision(getX(), getY())) {
				velocityX = 0;
				this.setX(tempX - 2);
				System.out.println("Collision right");
			}
			
			if (isTeleportDoor(getX(), getY())) {
				setBossPosition();
			}
		}
		
		if (this.velocityY < 0) { // going down
			this.setAnimation(playerAnimations.getRight());
			
			// set position while moving in case attacks pressed at same time as movement
			this.setPlayerCameraPosition();
			
			if (isCollision(getX(), getY())) {
				velocityY = 0;
				this.setY(tempY + 2);
				System.out.println("Collision down");
			}
			
			if (isTeleportDoor(getX(), getY())) {
				setBossPosition();
			}
		}
		
		if (this.velocityY > 0) { // going up
			this.setAnimation(playerAnimations.getRight());

			// set position while moving in case attacks pressed at same time as movement
			this.setPlayerCameraPosition();
			
			if (isCollision(getX(), getY())) {
				velocityY = 0;
				this.setY(tempY - 2);
				System.out.println("Collision up");
			}
			
			if (isTeleportDoor(getX(), getY())) {
				setBossPosition();
			}
		}
		
	}
	
	@Override
	public boolean keyDown(int keycode) {
		
		switch (keycode) {
		case Keys.W:
			
			if ((!Gdx.input.isKeyPressed(Input.Keys.J)) || (!Gdx.input.isKeyJustPressed(Input.Keys.J)) || (!Gdx.input.isKeyPressed(Input.Keys.K)) || (!Gdx.input.isKeyJustPressed(Input.Keys.K))) {
				
				this.setAnimation(playerAnimations.getRight()); // walking animation going up
				
				System.out.println("CAN MOVE UP");
				velocityY = 60;
				
			}
				
			break;
		case Keys.S:
			
			if ((!Gdx.input.isKeyPressed(Input.Keys.J)) || (!Gdx.input.isKeyJustPressed(Input.Keys.J)) || (!Gdx.input.isKeyPressed(Input.Keys.K)) || (!Gdx.input.isKeyJustPressed(Input.Keys.K))) {
				
				this.setAnimation(playerAnimations.getRight()); // walking animation going down
				
				System.out.println("CAN MOVE DOWN");
				velocityY = -60;
				
			}
			
			break;
		case Keys.A:
			if ((!Gdx.input.isKeyPressed(Input.Keys.J)) || (!Gdx.input.isKeyJustPressed(Input.Keys.J)) || (!Gdx.input.isKeyPressed(Input.Keys.K)) || (!Gdx.input.isKeyJustPressed(Input.Keys.K))) {
				
				facingRight = false;
				
				for (TextureRegion textureRegion : playerAnimations.getRight().getKeyFrames()) {
					if (!textureRegion.isFlipX()) { // if walking animation not facing left, then flip it
						textureRegion.flip(true,false);
					}
				}
				
				for (TextureRegion textureRegion : playerAnimations.getStationary().getKeyFrames()) {
					if (!textureRegion.isFlipX()) { // if stationary animation not facing left, then flip it 
						textureRegion.flip(true,false);
					}
				}
				
				this.setAnimation(playerAnimations.getRight());
				
				System.out.println("CAN MOVE LEFT");
				velocityX = -60;
				
				facingRight = false;
				
			}
			
			
			break;
		case Keys.D:
			
			if ((!Gdx.input.isKeyPressed(Input.Keys.J)) || (!Gdx.input.isKeyJustPressed(Input.Keys.J)) || (!Gdx.input.isKeyPressed(Input.Keys.K)) || (!Gdx.input.isKeyJustPressed(Input.Keys.K))) {
				
				facingRight = true;
				
				for (TextureRegion textureRegion : playerAnimations.getRight().getKeyFrames()) {
					if (textureRegion.isFlipX()) { // if walking animation is not facing right, then flip it
						textureRegion.flip(true,false);
					}
				}
				
				for (TextureRegion textureRegion : playerAnimations.getStationary().getKeyFrames()) {
					if (textureRegion.isFlipX()) { // if stationary animation not facing right, then flip it
						textureRegion.flip(true,false);
					}
				}
				
				this.setAnimation(playerAnimations.getRight());
				
				System.out.println("CAN MOVE RIGHT");
				velocityX = 60;
				facingRight = true;
				
			}
			
			break;
			
		case Keys.J:
					
			if (canPunch) {
				canPunch = false;
				
				Timer.schedule(new Task() {
					@Override
					public void run() {
						punch();
					}
				}, 0.1f);
				
				System.out.println("Not paused");
				// canPunch = false;
			}		

			break;
		
		case Keys.K:
			
			if (canKick) {
				canKick = false;
				
				Timer.schedule(new Task() {
					@Override
					public void run() {
						kick();
					}
				}, 0.1f);
				System.out.println("Not paused");
			}		

			break;
			
		default:
			return false;
		}	
		
		return true;
	}
	
	public void punch() {
		
		// was just testing to see health deduction working
		
	//	this.setHealth(this.getHealth() - 10);
		
		for (TextureRegion textureRegion : playerAnimations.getPunch().getKeyFrames()) {
			if (!facingRight && (!textureRegion.isFlipX())) { // if punching animation is not facing right, then flip it
				textureRegion.flip(true,false);
			}
			if (facingRight && (textureRegion.isFlipX())) { // if punching animation is not facing right, then flip it
				textureRegion.flip(true,false);
			}
		}
		
		this.setAnimation(playerAnimations.getPunch());
		
		// Logic for damaging thugs
		
		System.out.println(GameLevel.listOfEnemies);
		
		checkForDamageThugs(0); // pass 0 for punch
		
		System.out.println("PUNCHING");
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
		
		// Logic for damaging thugs
		
		System.out.println(GameLevel.listOfEnemies);
		
		checkForDamageThugs(1); // pass 1 for kick
		
		System.out.println("KICKING");
	}
	
	private void checkForDamageThugs(int hitType) {
		
		Iterator enemyIterator = GameLevel.listOfEnemies.entrySet().iterator();
		
		boolean thugNotDamaged = true; // set true so we can enter while loop and hit only 1 thug
		
		while (enemyIterator.hasNext() && thugNotDamaged) {
			Map.Entry mapElement = (Map.Entry)enemyIterator.next();
			Thug thug = (Thug) mapElement.getValue();
			
			System.out.println(thug.getCharName() + "'s HEALTH: " + thug.getHealth());
			if (Intersector.overlaps(this.getBoundingRectangle(), thug.getBoundingRectangle())){
				
				// only damage the thug if he isn't already in a damaged state
				if (!thug.getIsDamaged()) {
					
					System.out.println("IT'S A HIT!!!!!!");
					System.out.println("IsDamaged: " + thug.getIsDamaged());
					
					damageThug(thug, hitType);	
					thugNotDamaged = false;
					thugLastHit = thug.getCharName();
				}
			}
			System.out.println(mapElement.getKey() + ": " + thug);		
		}
	}

	private void damageThug(Thug thug, int hitType) {
		
		thug.reduceHealth(hitType);
		thug.setIsDamaged(true);
		thug.velocityX = 0;
		thug.velocityY = 0;
		
		if (thug.isFacingRight()) { // thug facing right
			for (TextureRegion textureRegion : thug.getPlayerAnimations().getDamaged().getKeyFrames()) {
				if (textureRegion.isFlipX()) { // if damaged anim is facing left, flip it to face right
					textureRegion.flip(true,false);
				}
			}
		} else { // thug facing left
			for (TextureRegion textureRegion : thug.getPlayerAnimations().getDamaged().getKeyFrames()) {
				if (!textureRegion.isFlipX()) { // if damaged anim is facing right, flip it to face left
					textureRegion.flip(true,false);
				}
			}
		}
		
		thug.setAnimation(thug.getPlayerAnimations().getDamaged());
			
		Timer.schedule(new Task() {
			@Override
			public void run() {
				thug.setIsDamaged(false);
			}
		}, 1);	
		System.out.println(thug.getCharName() + "'s HEALTH: " + thug.getHealth());
		
	}

	public void setStationary() {
		this.setAnimation(playerAnimations.getStationary());
	}
	
	public boolean keyUp(int keycode) {
		
		switch (keycode) {
		case Keys.W:
			setStationary();
			velocityY = 0;
			break;
		case Keys.S:
			setStationary();
			velocityY = 0;
			break;
		case Keys.A:
			setStationary();
			velocityX = 0;
			break;
		case Keys.D:
			setStationary();
			velocityX = 0;
			break;
		case Keys.J:
			Timer.schedule(new Task() {
				@Override
				public void run() {
					setStationary();
				}
			}, 0.5f);
			
			Timer.schedule(new Task() {
				@Override
				public void run() {
					canPunch = true;
				}
			}, 1);
			
			velocityX = 0;
			break;
		case Keys.K:
			Timer.schedule(new Task() {
				@Override
				public void run() {
					setStationary();
				}
			}, 0.8f);
			
			Timer.schedule(new Task() {
				@Override
				public void run() {
					canKick = true;
				}
			}, 1);
			
			velocityX = 0;
			break;
		default:
			return false;
		}
		
		System.out.println("Key pressed: " + Input.Keys.toString(keycode));
		
		return true;
	}
	
	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
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
	
	// check if colliding with the Teleport Door object in the tiled map
	public boolean isTeleportDoor(float x, float y) {
		for (PolygonMapObject polygonObject : GameLevel.teleportObject.getByType(PolygonMapObject.class)) {
			Polygon polygon = polygonObject.getPolygon();		
			
			if (isCollisionPolygon(polygon, this.getCollisionRectangle())) {
				return true;
			}
		}
		return false;
	}
	
	// Check if Polygon intersects the rectangle
	private boolean isCollisionPolygon(Polygon p, Rectangle r) {
	    Polygon rPoly = new Polygon(new float[] { 0, 0, r.width, 0, r.width,
	            r.height, 0, r.height });
	    rPoly.setPosition(r.x, r.y);
	    if (Intersector.overlapConvexPolygons(rPoly, p))
	        return true;
	    return false;
	}
	
	public Float getPlayerCenterX() {
		return playerCenter.get("x");
	}
	
	public Float getPlayerCenterY() {
		return playerCenter.get("y");
	}
	
	public Float getPlayerMiddleX() {
		return playerMiddle.get("x");
	}
	
	public Float getPlayerMiddleY() {
		return playerMiddle.get("y");
	}
	
	public void setPlayerCenterX() {
		this.playerCenter.put("x", this.getX() + this.getOriginX());
	}
	
	public void setPlayerCenterY() {
		this.playerCenter.put("y", this.getY() + this.getOriginY());
	}
	
	public void setPlayerMiddleX() {
		this.playerMiddle.put("x", this.getX() + this.getWidth() / 2);
	}
	
	public void setPlayerMiddleY() {
		this.playerMiddle.put("y", this.getY() + this.getHeight() / 2);
	}
	
	public void setPlayerCameraPosition() {
		setPlayerCenterX();
		setPlayerCenterY();
		setPlayerMiddleX();
		setPlayerMiddleY();
	}
	
	public void reduceHealth(int hitType) {

		if (hitType == 0) { // if hit is a punch
			health -= 10;
		} else { // if hit is a kick
			health -= 20;
		}
	}
	
	public boolean isFacingRight() {
		return facingRight;
	}
	
	public int getHealth() {
		return health;
	}
	
	public void setHealth(int health) {
		this.health= health;
	}
	
	public String getCharName() {
		return charName;
	}
	
	public void setCharName(String charName) {
		this.charName = charName;
	}
	
	public String getThugLastHit() {
		return thugLastHit;
	}
	
	public void setThugLastHit(String thugCharName) {
		this.thugLastHit = thugCharName;
	}

	public void setIsDamaged(boolean val) {
		isDamaged = val;
	}

	public boolean getIsDamaged() {
		return isDamaged;
	}
	
	public boolean allThugsDead() {
		
		Iterator enemyIterator = GameLevel.listOfEnemies.entrySet().iterator();
		
		boolean thugNotDamaged = true; // set true so we can enter while loop and hit only 1 thug
		
		int aliveThugCount = 0; // count to track the number of thugs still alive
		
		while (enemyIterator.hasNext() && thugNotDamaged) { // go through each thug in hashmap
			Map.Entry mapElement = (Map.Entry)enemyIterator.next();
			Thug thug = (Thug) mapElement.getValue(); 

			if (thug.isAlive()) {
				aliveThugCount++;
			}
		}
		
		if (aliveThugCount == 0) { // if all the thugs are dead, return true
			return true;
		} else {
			return false;
		}
		
		
	}
	
	public void setBossPosition() {
		this.setPosition(4500, 20);
		
	}

	
	
}
