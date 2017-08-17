package com.eg.planetjumper;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class LevelLoader
{
	private PlanetJumper planet;
	private Random r;
	private Texture planetImage;
	
	public LevelLoader(PlanetJumper main, Texture planetImage)
	{
		planet = main;
		this.planetImage = planetImage;
		r = new Random();
		
		//create planets
		reset();
	}
	
	protected void createObject(int x, int y, final float velocity) 
	{
		//define body
		final BodyDef def1 = new BodyDef();
		def1.type = BodyType.KinematicBody;
		def1.position.set(x/PlanetJumper.PPM,y/PlanetJumper.PPM);
		
		//define its shape
		final Shape s1;
		s1 = new CircleShape();
		s1.setRadius((planetImage.getWidth()/2)/PlanetJumper.PPM);
		
		//fixture to contain its shape
		final FixtureDef fdef1 = new FixtureDef();
		fdef1.shape = s1;
		fdef1.density = 1f;
		
		//initialize body and fixture
		Gdx.app.postRunnable(new Runnable() 
		{
			@Override
			public void run() 
			{
				Body bod  = PlanetJumper.getWorld().createBody(def1);
				
				//set velocity
				bod.createFixture(fdef1);
				bod.setAngularVelocity(velocity);
				
				//add it to list of drawables
				planet.addImageBody(new ImageBody(bod,new Sprite(planetImage)));
				
				s1.dispose();
			}
		});
	}

	public void reset()
	{
		createObject(0,0,3);
		createObject(800,0,r.nextFloat() - 3f);
		createObject(1600,0,r.nextFloat() + 2.5f);
	}

	public void update(float f) 
	{
		
	}
}
