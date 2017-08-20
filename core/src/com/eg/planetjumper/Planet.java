package com.eg.planetjumper;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;

public class Planet extends ImageBody 
{
	private boolean spent;
	
	public Planet(Body bod, Sprite sprite2) 
	{
		super(bod, sprite2);

		bod.getFixtureList().get(0).setUserData(this);		
		spent = false;
	}

	public void land()
	{
		if (!spent)
		{
			ScoreHandler.getInstance().getPoints();
			spent = true;
		}
	}
}
