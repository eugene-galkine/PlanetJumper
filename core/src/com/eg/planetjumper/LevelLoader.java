package com.eg.planetjumper;

import java.util.Random;

import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.graphics.Color;
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
	private static final int PLANET_SPACING = 1600;
	private static final int STARTING_PLANETS = 3;
	
	private PlanetJumper planet;
	private Random r;
	private Texture planetImage[];
	private int pos;
	private RayHandler rayHandler;
	
	public LevelLoader(PlanetJumper main, Texture planetImage[], RayHandler rayHandle)
	{
		planet = main;
		this.planetImage = planetImage;
		r = new Random();
		rayHandler = rayHandle;
		rayHandler.setBlur(false);
		rayHandler.setShadows(true);
		
		//create planets
		reset();
	}
	
	protected void createObject(int x, int y, boolean counterClockwise) 
	{
		float velocity = counterClockwise ? r.nextFloat() + 2.5f : r.nextFloat() - 3.5f;
		Texture image;
		
		if (x == 0)
			image = planetImage[12];
		else
			image = planetImage[r.nextInt(planetImage.length)];
		
		//define body
		final BodyDef def1 = new BodyDef();
		def1.type = BodyType.KinematicBody;
		def1.position.set(x/PlanetJumper.PPM,y/PlanetJumper.PPM);
		
		//define its shape
		final Shape s1;
		s1 = new CircleShape();
		s1.setRadius((image.getWidth()/2)/PlanetJumper.PPM);
		
		//fixture to contain its shape
		final FixtureDef fdef1 = new FixtureDef();
		fdef1.shape = s1;
		fdef1.density = 1f;
		//trick to get fix the player from sticking to the planet sometimes
		fdef1.filter.categoryBits = 0x0002;
		fdef1.filter.maskBits = 0x0001 | 0x0003;
		
		//initialize body and fixture	
		Body bod  = PlanetJumper.getWorld().createBody(def1);
		
		//set velocity and create fixture
		bod.createFixture(fdef1);
		bod.setAngularVelocity(velocity);
		
		//add it to list of drawables, this first planet should just be an ImageBody so we don't give points for landing on the first planet
		if (x == 0)
			planet.addImageBody(new ImageBody(bod,new Sprite(image)));
		else
			planet.addImageBody(new Planet(bod,new Sprite(image)));
		
		s1.dispose();
	}

	public void reset()
	{
		rayHandler.removeAll();
		pos = PLANET_SPACING;
		
		createObject(0,0,true);
		makeStars(-3000);
		makeStars(-2000);
		makeStars(-1000);
		makeStars(0);
		for (int i = 1; i <= STARTING_PLANETS; i++)
		{
			createObject(PLANET_SPACING*i,r.nextInt(100) - 50,(PLANET_SPACING*i) % (PLANET_SPACING*2) == 0);
			makeStars(PLANET_SPACING * i); 
		}
	}

	public void update(float f) 
	{
		//make a new planet when needed
		if (f >= pos)
		{
			makeStars(pos + PLANET_SPACING * STARTING_PLANETS);
			createObject((pos + PLANET_SPACING * STARTING_PLANETS) - (100 + r.nextInt(200)),r.nextInt(300) - 100,(pos + PLANET_SPACING * STARTING_PLANETS) % (PLANET_SPACING*2) == 0);
			pos += PLANET_SPACING;
		}
	}
	
	private void makeStars(int x)
	{
		//make some random stars
		for (int i = 0; i < 14; i++)
			new PointLight(rayHandler, r.nextInt(5) + 5, 
					new Color((r.nextInt(3) + 8) / 10f, (r.nextInt(6) + 5) / 10f, (r.nextInt(5) + 6) / 10f, 1), 
					100 + r.nextInt(120), 
					r.nextInt(PLANET_SPACING) + x, 
					r.nextInt(15000) - 1500);
	}
}
