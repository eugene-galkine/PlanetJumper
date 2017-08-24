package com.eg.planetjumper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;

public class PlanetContactListener implements ContactListener
{
	@Override
	public void beginContact(Contact contact) 
	{
		final Fixture fa = contact.getFixtureA();
		final Fixture fb = contact.getFixtureB();
		
		if ((fa == null || fb == null) || (fa.getBody() == null || fb.getBody() == null))
		{
			System.out.println("Null in collision listener.");
			return;
		}
		
		if (fa.getUserData() instanceof Player || fb.getUserData() instanceof Player)
		{
			//adds a joint
			final WeldJointDef j = new WeldJointDef();
			
			j.bodyA = fa.getBody();
			j.bodyB = fb.getBody();
			j.initialize(fa.getBody(), fb.getBody(), fa.getBody().getPosition());
			
			//figure out which one is the planet so we can give the player points
			if (fa.getUserData() instanceof Planet)
			{
				((Planet) fa.getUserData()).land();
			}
			else if (fb.getUserData() instanceof Planet)
			{
				((Planet) fb.getUserData()).land();
			}
			
			//run on ui thread
			if (fa.getUserData() instanceof Player)
			{
				Gdx.app.postRunnable(new Runnable() 
				{
					@Override
					public void run() 
					{
						((Player) fa.getUserData()).setPlayerJoint(PlanetJumper.getWorld().createJoint(j));
					}
				});
			}
			else if (fb.getUserData() instanceof Player)
			{
				Gdx.app.postRunnable(new Runnable() 
				{
					@Override
					public void run() 
					{
						((Player) fb.getUserData()).setPlayerJoint(PlanetJumper.getWorld().createJoint(j));
					}
				});	
				
			}
		}	
	}

	@Override
	public void endContact(Contact contact)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) 
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) 
	{
		// TODO Auto-generated method stub
	}
}
