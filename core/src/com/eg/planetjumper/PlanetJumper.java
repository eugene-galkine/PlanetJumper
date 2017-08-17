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
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class PlanetJumper extends ApplicationAdapter {
	public static final float PPM = 16;
	public static Joint playerJoint = null;
	
	private static World world;
	private Box2DDebugRenderer debugRenderer;
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private LevelLoader level;
	private Texture planetImage;
	private Texture rocketImage;
	private ArrayList<ImageBody> b;
	private ImageBody player;
	
	@Override
	public void create () 
	{
		//initialize stuff
		batch = new SpriteBatch();
		world = new World(new Vector2(0,-9.8f), true);
		world.setContactListener(new PlanetContactListener());
		camera = new OrthographicCamera();
		camera.setToOrtho(false);
		debugRenderer = new Box2DDebugRenderer();
		
		planetImage = new Texture("planet.png");
		rocketImage = new Texture("player.png");
		b = new ArrayList<ImageBody>();
		level = new LevelLoader(this, planetImage);
		
		//create player
		createPlayer();
	}

	protected void addImageBody(ImageBody imageBody) 
	{
		b.add(imageBody);
	}
	
	protected void createPlayer() 
	{
		int x = 0;
		int y = 200;
		
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
		bod.createFixture(fdef1).setUserData(this);
		
		//add it to list of drawables
		player = new ImageBody(bod,new Sprite(rocketImage));
		b.add(player);
		
		s1.dispose();
	}

	public static World getWorld()
	{
		return world;
	}
	
	@Override
	public void render () 
	{
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		//update box2d
		world.step(Gdx.graphics.getDeltaTime(), 6, 2);
		
		//stuff that requires player to exist
		if (player != null)
		{
			//launch player on input
			if (Gdx.input.isTouched() && playerJoint != null)
			{
				world.destroyJoint(playerJoint);
				playerJoint = null;
				
				player.getBody().applyForceToCenter(0, 2, true);
			}
			
			//player death
			if (player.getBody().getPosition().y < -60)
			{
				//destroy all planets and player
				for (final ImageBody body : b)
					Gdx.app.postRunnable(new Runnable() 
					{
						@Override
						public void run() 
						{
							world.destroyBody(body.getBody());
						}
					});	
				
				//reset level
				b.clear();
				level.reset();
				
				//new player
				createPlayer();
			} else
				//update level to load new planets as needed
				level.update(player.getBody().getPosition().x * PPM);
			
			//update camera
			camera.position.set(player.getBody().getPosition().x * PPM + Gdx.graphics.getWidth() / 2,
					player.getBody().getPosition().y * PPM + Gdx.graphics.getHeight() / 2, 0);
			camera.update();
			batch.setProjectionMatrix(camera.combined);
		}
		
		//draw everything
		batch.begin();
		for (ImageBody body : b)
		{
			body.updateImage();
			body.getImg().draw(batch);
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
