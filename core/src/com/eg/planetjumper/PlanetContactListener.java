package com.eg.planetjumper;

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
			
			//figure out which one is the player so we can give the player points and create the joint
			if (fa.getUserData() instanceof Player)
			{
				((Player) fa.getUserData()).land(j);
			}
			else if (fb.getUserData() instanceof Player)
			{
				((Player) fb.getUserData()).land(j);			
			}
		}	
	}

	@Override
	public void endContact(Contact contact)
	{
		
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) 
	{
		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) 
	{
		
	}
}
