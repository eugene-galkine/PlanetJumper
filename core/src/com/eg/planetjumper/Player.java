package com.eg.planetjumper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Joint;

public class Player extends ImageBody
{
	private boolean falling;
	private Joint playerJoint;

	public Player(Body bod, Sprite sprite2) 
	{
		super(bod, sprite2);
		falling = false;
		setPlayerJoint(null);
		bod.getFixtureList().get(0).setUserData(this);
	}

	@Override
	public void updateAndDraw(SpriteBatch batch) 
	{
		//launch player on input
		if (Gdx.input.isTouched() && getPlayerJoint() != null)
		{
			PlanetJumper.getWorld().destroyJoint(getPlayerJoint());
			setPlayerJoint(null);
			SoundHandler.getIntance().playJump();

			//set filter to prevent us from getting stuck
			Filter filter = getBody().getFixtureList().get(0).getFilterData();
			filter.categoryBits = 0x0004;
			getBody().getFixtureList().get(0).setFilterData(filter);
			
			//simple thread with timer to revert the collision filter we just set up
			new Thread()
			{
				@Override
				public void run()
				{
					try 
					{
						sleep(150);
					} catch (InterruptedException e) 
					{
						e.printStackTrace();
					}
					Filter filter = getBody().getFixtureList().get(0).getFilterData();
					filter.categoryBits = 0x0001;
					getBody().getFixtureList().get(0).setFilterData(filter);
				}
			}.start();;
		} 
		
		
		if (getBody().getPosition().y < -40 && !falling)
		{
			falling = true;
			SoundHandler.getIntance().playFall();
		}
		
		super.updateAndDraw(batch);
		
	}

	public Joint getPlayerJoint() 
	{
		return playerJoint;
	}

	public void setPlayerJoint(Joint playerJoint)
	{
		this.playerJoint = playerJoint;
	}
	
}
