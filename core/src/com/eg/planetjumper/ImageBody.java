package com.eg.planetjumper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;

public class ImageBody 
{
	private Body body;
	private Sprite sprite;
	
	public ImageBody(Body bod, Sprite sprite2)
	{
		this.body = bod;
		this.sprite = sprite2;
	}

	public void updateImage()
	{
		sprite.setPosition(body.getPosition().x * PlanetJumper.PPM + Gdx.graphics.getWidth() / 2 - sprite.getWidth() / 2, body.getPosition().y * PlanetJumper.PPM + Gdx.graphics.getHeight() / 2 - sprite.getHeight() / 2);
	}

	public Sprite getImg() 
	{
		return sprite;
	}
	
}
