package com.eg.planetjumper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.JointDef;

public class Player extends ImageBody
{
	private boolean falling;
	private Joint playerJoint;
	private float releasePos;
	private float lastScorePos;
	
	public Player(Body bod, Sprite sprite2) 
	{
		super(bod, sprite2);
		falling = false;
		setPlayerJoint(null);
		bod.getFixtureList().get(0).setUserData(this);
		releasePos = 0;
		lastScorePos = 0;
	}

	@Override
	public void updateAndDraw(SpriteBatch batch) 
	{
		//calculate points for flying over planets
		if (getPlayerJoint() == null)
		{
			ScoreHandler.getInstance().setMultiplier(
					(int)(((getBody().getPosition().x - releasePos) * PlanetJumper.PPM) / LevelLoader.PLANET_SPACING));
		}
		
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
		if (playerJoint == null)
			releasePos = getBody().getPosition().x;
		
		this.playerJoint = playerJoint;
	}
	
	public void land(final JointDef j)
	{
		//don't give points if we land on the same planet twice, go backwards or on the starting planet
		if (getBody().getPosition().x > lastScorePos + 20)
		{
			ScoreHandler.getInstance().getPoints();
			lastScorePos = getBody().getPosition().x;
		}
		
		//make the joint on the libgdx thread
		Gdx.app.postRunnable(new Runnable() 
		{
			@Override
			public void run() 
			{
				setPlayerJoint(PlanetJumper.getWorld().createJoint(j));
				ScoreHandler.getInstance().setMultiplier(0);
			}
		});	
	}
}
