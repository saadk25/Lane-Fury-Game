package com.lanefury.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

public class Boss extends Thug {

	public Boss(String spriteName, MapObjects wallObjects, Player mainPlayer, String charName) {
		super(spriteName, wallObjects, mainPlayer, charName);
		this.setHealth(400);
		this.setThugSpeed(35);
	}
	

}
