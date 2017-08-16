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
		Fixture fa = contact.getFixtureA();
		Fixture fb = contact.getFixtureB();
		
		if ((fa == null || fb == null) || (fa.getBody() == null || fb.getBody() == null))
		{
			System.out.println("Null in collision listener.");
			return;
		}
		
		if (fa.getUserData() instanceof PlanetJumper || fb.getUserData() instanceof PlanetJumper)
		{
			//adds a joint
			final WeldJointDef j = new WeldJointDef();
			
			j.bodyB = fb.getBody();
			j.bodyA = fa.getBody();
			j.initialize(fa.getBody(), fb.getBody(), fa.getBody().getPosition());
			
			//run on ui thread
			Gdx.app.postRunnable(new Runnable() 
			{
		         @Override
		         public void run() 
		         {
		        	 PlanetJumper.getWorld().createJoint(j);
		         }
		    });	
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
