package com.eg.planetjumper;

import java.util.ArrayList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

public class PlanetJumper extends ApplicationAdapter {
	public static final float PPM = 20;
	public static Joint playerJoint = null;
	
	private static World world;
	private static float cameraZoomMod;
	
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private LevelLoader level;
	private Texture planetImage, resetImage, rocketImage;
	private Stage ui;
	private ArrayList<ImageBody> b;
	private ImageBody player;
	private Sound failSound, jumpSound, landSound;
	private boolean falling;
	
	@Override
	public void create () 
	{
		//initialize stuff
		batch = new SpriteBatch();
		world = new World(new Vector2(0,-9.8f), true);
		world.setContactListener(new PlanetContactListener());
		camera = new OrthographicCamera();
		camera.setToOrtho(false);
		cameraZoomMod = 2000f / Gdx.graphics.getWidth();
		ui = new Stage();
		planetImage = new Texture("planet.png");
		rocketImage = new Texture("player.png");
		resetImage = new Texture("reset.png");
		b = new ArrayList<ImageBody>();
		level = new LevelLoader(this, planetImage);
		failSound = Gdx.audio.newSound(Gdx.files.internal("sfx/fall.wav"));
		jumpSound = Gdx.audio.newSound(Gdx.files.internal("sfx/jump.wav"));
		landSound = Gdx.audio.newSound(Gdx.files.internal("sfx/landing.wav"));
		falling = false;
		
		//set up the UI
		Sprite resetButton = new Sprite(resetImage);
		resetButton.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		resetButton.setSize(Gdx.graphics.getWidth()/15, Gdx.graphics.getWidth()/15);
		Button btn = new Button(new SpriteDrawable(resetButton));
		btn.setPosition(5, Gdx.graphics.getHeight() - (Gdx.graphics.getWidth()/15) - 5);
		btn.addListener(new ClickListener()
		{
			@Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button)
            {
				Gdx.app.postRunnable(new Runnable() 
				{
					@Override
					public void run() 
					{	
						resetGame();
					}
				});
                return true;
            }
		});
		
		ui.addActor(btn);
		ui.addActor(ScoreHandler.getInstance().initiate(landSound));
		Gdx.input.setInputProcessor(ui);
		
		//create player
		createPlayer();
	}

	private void resetGame() 
	{
		if (playerJoint != null)
			world.destroyJoint(playerJoint);
		
		//destroy all planets and player
		for (final ImageBody body : b)
			if (body != null && body.getBody() != null)
				world.destroyBody(body.getBody());
		
		//reset variables
		playerJoint = null;
		b.clear();
		level.reset();
		ScoreHandler.getInstance().reset();
		falling = false;	
	
		//new player
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
		fdef1.filter.categoryBits = 0x0001;
		fdef1.filter.maskBits = 0x0002 | 0x0003;
		
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
				jumpSound.play();
				//add a little bit of force to our block just for fun
				player.getBody().applyForceToCenter(1, 1, true);
				//set filter to prevent us from getting stuck
				Filter filter = player.getBody().getFixtureList().get(0).getFilterData();
				filter.categoryBits = 0x0004;
				player.getBody().getFixtureList().get(0).setFilterData(filter);
				
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
						Filter filter = player.getBody().getFixtureList().get(0).getFilterData();
						filter.categoryBits = 0x0001;
						player.getBody().getFixtureList().get(0).setFilterData(filter);
					}
				}.start();;
			}
			
			//player death
			if (player.getBody().getPosition().y < -50)
				resetGame();
			else if (player.getBody().getPosition().y < -30 && !falling)
			{
				falling = true;
				failSound.play();
			}
			else
			{
				//update level to load new planets as needed
				level.update(player.getBody().getPosition().x * PPM);
			
				//update camera (only if player wasn't removed
				camera.position.set(player.getBody().getPosition().x * PPM + Gdx.graphics.getWidth() / 2,
						player.getBody().getPosition().y * PPM + Gdx.graphics.getHeight() / 2, 0);
				camera.zoom = (player.getBody().getPosition().y > 15f ? (float)Math.pow(player.getBody().getPosition().y - 14f, 1/5f) : 1f) * cameraZoomMod;
				camera.update();
				batch.setProjectionMatrix(camera.combined);
			}
		}
		
		//draw everything
		batch.begin();
		for (ImageBody body : b)
			body.updateAndDraw(batch);
		batch.end();
		
		//draw the ui
		ui.draw();
		
		//draw box2d debugging
		//debugRenderer.render(world, camera.projection);
	}

	@Override
	public void dispose () 
	{
		ui.dispose();
		world.dispose();
		batch.dispose();
		planetImage.dispose();
		rocketImage.dispose();
		resetImage.dispose();
	}
}
