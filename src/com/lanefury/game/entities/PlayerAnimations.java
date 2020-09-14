package com.lanefury.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class PlayerAnimations {

	private Animation stationary, right, punch, kick, damaged, dying;
	private int stationaryNum, rightNum, punchNum, kickNum, damageNum, dyingNum;
	
	public PlayerAnimations(String playerName) {
		
		if (playerName == "kyo") {
			animationNums(9, 9, 3, 5, 6, 6);
		}
		else if (playerName == "thug") {
			animationNums(2, 6, 5, 5, 2, 2);
		}
		else if (playerName == "boss") {
			animationNums(3, 6, 3, 3, 2, 2);
		}
		
		// Set the stationary animations for the character
		TextureRegion[] stationaryFrames = new TextureRegion[stationaryNum];
	    for (int n = 0; n < stationaryNum; n++)
	    {   
	        String fileName = "assets/sprites/" + playerName + "/" + playerName + "_stationary_" + n + ".png";
	        Texture tex = new Texture(Gdx.files.internal(fileName));
	        tex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
	        TextureRegion textureRegion = new TextureRegion ( tex );
	        
	        stationaryFrames[n] = textureRegion; // if not thug, assign normally
	        
	    }
	    Array<TextureRegion> stationaryFramesArray = new Array<TextureRegion>(stationaryFrames);

	    stationary = new Animation(0.12f, stationaryFramesArray, Animation.PlayMode.LOOP_PINGPONG);
	      
	    // Set the right animations for the character
	    TextureRegion[] rightFrames = new TextureRegion[rightNum];
	    for (int n = 0; n < rightNum; n++)
	    {   
	        String fileName = "assets/sprites/" + playerName + "/" + playerName + "_right_" + n + ".png";
	        Texture tex = new Texture(Gdx.files.internal(fileName));
	        tex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
	        TextureRegion textureRegion = new TextureRegion ( tex );

	        rightFrames[n] = textureRegion; // if not thug, assign normally
	       
	    }
	    Array<TextureRegion> rightFramesArray = new Array<TextureRegion>(rightFrames);

	    right = new Animation(0.12f, rightFramesArray, Animation.PlayMode.LOOP_PINGPONG);
	    
	 // Set the punch animations for the character
	    TextureRegion[] punchFrames = new TextureRegion[punchNum];
	    for (int n = 0; n < punchNum; n++)
	    {   
	        String fileName = "assets/sprites/" + playerName + "/" + playerName + "_punch_" + n + ".png";
	        Texture tex = new Texture(Gdx.files.internal(fileName));
	        tex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
	        TextureRegion textureRegion = new TextureRegion ( tex );
	        
	        punchFrames[n] = textureRegion; // if not thug, assign normally
	    }
	    Array<TextureRegion> punchFramesArray = new Array<TextureRegion>(punchFrames);

	    punch = new Animation(0.3f, punchFramesArray, Animation.PlayMode.LOOP);
	    
	 // Set the kick animations for the character
	    TextureRegion[] kickFrames = new TextureRegion[kickNum];
	    for (int n = 0; n < kickNum; n++)
	    {   
	        String fileName = "assets/sprites/" + playerName + "/" + playerName + "_kick_" + n + ".png";
	        Texture tex = new Texture(Gdx.files.internal(fileName));
	        tex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
	        TextureRegion textureRegion = new TextureRegion ( tex );
	        
	        kickFrames[n] = textureRegion; // if not thug, assign normally
	    }
	    Array<TextureRegion> kickFramesArray = new Array<TextureRegion>(kickFrames);

	    kick = new Animation(0.12f, kickFramesArray, Animation.PlayMode.LOOP_PINGPONG);
	    
	 // Set the damaged animations for the character
	    TextureRegion[] damagedFrames = new TextureRegion[damageNum];
	    for (int n = 0; n < damageNum; n++)
	    {   
	        String fileName = "assets/sprites/" + playerName + "/" + playerName + "_damaged_" + n + ".png";
	        Texture tex = new Texture(Gdx.files.internal(fileName));
	        tex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
	        TextureRegion textureRegion = new TextureRegion ( tex );
	        
	        damagedFrames[n] = textureRegion; // if not thug, assign normally
	        
	    }
	    Array<TextureRegion> damagedFramesArray = new Array<TextureRegion>(damagedFrames);

	    damaged = new Animation(0.17f, damagedFramesArray, Animation.PlayMode.LOOP_PINGPONG);
	    
	 // Set the dying animations for the character
	    TextureRegion[] dyingFrames = new TextureRegion[dyingNum];
	    for (int n = 0; n < dyingNum; n++)
	    {   
	        String fileName = "assets/sprites/" + playerName + "/" + playerName + "_dying_" + n + ".png";
	        Texture tex = new Texture(Gdx.files.internal(fileName));
	        tex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
	        TextureRegion textureRegion = new TextureRegion ( tex );
	        
	        dyingFrames[n] = textureRegion; // if not thug, assign normally
	    }
	    Array<TextureRegion> dyingFramesArray = new Array<TextureRegion>(dyingFrames);

	    dying = new Animation(0.12f, dyingFramesArray, Animation.PlayMode.LOOP_PINGPONG);
	    
	}

	public Animation getStationary() {
		return stationary;
	}

	public void setStationary(Animation stationary) {
		this.stationary = stationary;
	}


	public Animation getRight() {
		return right;
	}

	public void setRight(Animation right) {
		this.right = right;
	}
	
	public Animation getPunch() {
		return punch;
	}

	public void setPunch(Animation punch) {
		this.punch = punch;
	}
	
	public Animation getKick() {
		return kick;
	}

	public void setKick(Animation kick) {
		this.kick = kick;
	}
	
	public Animation getDamaged() {
		return damaged;
	}

	public void setDamaged(Animation damaged) {
		this.damaged = damaged;
	}
	
	public Animation getDying() {
		return dying;
	}

	public void setDying(Animation dying) {
		this.dying = dying;
	}
	
	public void animationNums(int stationaryNum, int rightNum, int punchNum, int kickNum, int damageNum, int dyingNum) {
		this.stationaryNum = stationaryNum;
		this.rightNum = rightNum;
		this.punchNum = punchNum;
		this.kickNum = kickNum;
		this.damageNum = damageNum;
		this.dyingNum = dyingNum;
	}
	
}
