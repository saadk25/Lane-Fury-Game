package com.lanefury.game.entities;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.Color;

public class BaseActor extends Actor
{

	public TextureRegion region;
    public Rectangle boundary;
    public Rectangle collisionBoundary;
    public float velocityX;
    public float velocityY;

    public BaseActor()
    {
        super();
        region = new TextureRegion();
        boundary = new Rectangle();
        collisionBoundary = new Rectangle();
        velocityX = 0;
        velocityY = 0;
    }
    
    public void setTexture(Texture t)
    { 
        int w = t.getWidth();
        int h = t.getHeight();
        setWidth( w );
        setHeight( h );
        region.setRegion( t );
    }
    
    public Rectangle getBoundingRectangle()
    {
        boundary.set( getX(), getY(), getWidth(), getHeight() );
        return boundary;
    }
    
    public Rectangle getCollisionRectangle() {
    	collisionBoundary.set(getX() + (getWidth() / 3), getY(), getWidth() / 3, getHeight() / 10);
        return collisionBoundary;
    }

    public void act(float dt)
    {
        super.act( dt );
        moveBy( velocityX * dt, velocityY * dt );
    }
    
    public void draw(Batch batch, float parentAlpha) 
    {
        //super.draw( batch, parentAlpha ); // but.... this is empty, so can be deleted here...
        Color c = getColor();
        batch.setColor(c.r, c.g, c.b, c.a);
        if ( isVisible() )
            batch.draw( region, getX(), getY(), getOriginX(), getOriginY(),
                getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation() );
    }
    
    public float getVelocityX() {
		return velocityX;
	}

	public void setVelocityX(float velocityX) {
		this.velocityX = velocityX;
	}

	public float getVelocityY() {
		return velocityY;
	}

	public void setVelocityY(float velocityY) {
		this.velocityY = velocityY;
	}
    
}