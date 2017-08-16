package com.eg.planetjumper;

import java.util.ArrayList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class PlanetJumper extends ApplicationAdapter {
	public static final float PPM = 16;
	
	private World world;
	private Box2DDebugRenderer debugRenderer;
	private OrthographicCamera camera;
	private SpriteBatch batch;

	private Texture planetImage;
	private Texture rocketImage;
	private ArrayList<ImageBody> b;
	
	@Override
	public void create () 
	{
		//initialize stuff
		batch = new SpriteBatch();
		world = new World(new Vector2(0,-9.8f), true);
		camera = new OrthographicCamera(Gdx.graphics.getWidth() / PPM, Gdx.graphics.getHeight() / PPM);
		debugRenderer = new Box2DDebugRenderer();
		
		planetImage = new Texture("planet.png");
		rocketImage = new Texture("player.png");
		
		b = new ArrayList<ImageBody>();
		
		createPlanet(0,0);
		createPlayer(0,180);
	}

	private void createPlanet(int x, int y) 
	{
		//define body
		BodyDef def1 = new BodyDef();
		def1.type = BodyType.KinematicBody;
		def1.position.set(x/PPM,y/PPM);
		
		//define its shape
		CircleShape s1 = new CircleShape();
		s1.setRadius((planetImage.getWidth()/2)/PPM);
		
		//fixture to contain its shape
		FixtureDef fdef1 = new FixtureDef();
		fdef1.shape = s1;
		fdef1.density = 1f;
		
		//initialize body and fixture
		Body bod = world.createBody(def1);
		bod.createFixture(fdef1);
		//set velocity
		bod.setAngularVelocity(3);
		
		//add it to list of drawables
		b.add(new ImageBody(bod,new Sprite(planetImage)));
		
		s1.dispose();
	}
	
	private void createPlayer(int x, int y) 
	{
		//define body
		BodyDef def1 = new BodyDef();
		def1.type = BodyType.DynamicBody;
		def1.position.set(x/PPM,y/PPM);
		
		//define its shape
		PolygonShape s1 = new PolygonShape();
		s1.setAsBox((rocketImage.getWidth()/2)/PPM, (rocketImage.getHeight()/2)/PPM);
		
		//fixture to contain its shape
		FixtureDef fdef1 = new FixtureDef();
		fdef1.shape = s1;
		fdef1.density = 1f;
		
		//initialize body and fixture
		Body bod = world.createBody(def1);
		bod.createFixture(fdef1);
		//set velocity
		
		//add it to list of drawables
		b.add(new ImageBody(bod,new Sprite(rocketImage)));
		
		s1.dispose();
	}

	@Override
	public void render () 
	{
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		//update box2d
		world.step(Gdx.graphics.getDeltaTime(), 6, 2);
		
		//draw everything
		batch.begin();
		for (ImageBody body : b)
		{
			body.updateImage();
			Sprite img = body.getImg();
			batch.draw(img, img.getX(),img.getY());
		}
		batch.end();
		
		//draw box2d debugging
		debugRenderer.render(world, camera.projection);
	}
	
	@Override
	public void dispose () 
	{
		batch.dispose();
		planetImage.dispose();
		rocketImage.dispose();
	}
}
