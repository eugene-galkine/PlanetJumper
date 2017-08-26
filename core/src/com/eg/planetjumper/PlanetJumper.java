package com.eg.planetjumper;

import java.util.ArrayList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
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
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

import box2dLight.RayHandler;

public class PlanetJumper extends ApplicationAdapter 
{
	public static final float PPM = 20;
	
	private static World world;
	private static Preferences prefs;
	
	private float cameraZoomMod;
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private LevelLoader level;
	private Texture resetImage, rocketImage, muteImage, soundImage;
	private Stage ui;
	private ArrayList<ImageBody> b;
	private Player player;
	private RayHandler rayHandler;
	private World lightWorld;
	private Texture planetImages[];
	
	@Override
	public void create () 
	{
		//initialize stuff
		batch = new SpriteBatch();
		world = new World(new Vector2(0,-9.8f), true);
		lightWorld = new World(new Vector2(0, 0), true);
		world.setContactListener(new PlanetContactListener());
		camera = new OrthographicCamera();
		camera.setToOrtho(false);
		cameraZoomMod = 3000f / Gdx.graphics.getWidth();
		ui = new Stage();
		planetImages = new Texture[15];
		for (int i = 0; i < 15; i++)
			planetImages[i] = new Texture(Gdx.files.internal("planets/planet_" + (i + 34) + ".png"));
		rocketImage = new Texture(Gdx.files.internal("player.png"));
		resetImage = new Texture(Gdx.files.internal("reset.png"));
		muteImage = new Texture(Gdx.files.internal("mute.png"));
		soundImage = new Texture(Gdx.files.internal("sound.png"));
		b = new ArrayList<ImageBody>();
		rayHandler = new RayHandler(lightWorld);
		level = new LevelLoader(this, planetImages, rayHandler);
		prefs = Gdx.app.getPreferences("com.eg.planetjumper");
		
		//set up the UI
		Sprite resetButton = new Sprite(resetImage);
		final Sprite muteButton = new Sprite(muteImage);
		final Sprite soundButton = new Sprite(soundImage);
		resetButton.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		resetButton.setSize(Gdx.graphics.getWidth()/15, Gdx.graphics.getWidth()/15);
		muteButton.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		muteButton.setSize(Gdx.graphics.getWidth()/18, Gdx.graphics.getWidth()/18);
		soundButton.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		soundButton.setSize(Gdx.graphics.getWidth()/18, Gdx.graphics.getWidth()/18);
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
		final ImageButton btnM = new ImageButton(new SpriteDrawable(soundButton), new SpriteDrawable(muteButton), new SpriteDrawable(muteButton));
		btnM.setPosition(Gdx.graphics.getWidth() - (Gdx.graphics.getWidth()/18) - 5, Gdx.graphics.getHeight() - (Gdx.graphics.getWidth()/18) - 5);
		btnM.setChecked(SoundHandler.getIntance().getToggle());
		btnM.addListener(new ClickListener()
		{
			@Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button)
            {
				btnM.setChecked(!SoundHandler.getIntance().toggleSound());
                return true;
            }
		});
		ui.addActor(btn);
		ui.addActor(btnM);
		ScoreHandler.getInstance().initiate(ui);
		Gdx.input.setInputProcessor(ui);
		
		//create player
		createPlayer();
		
		//restart the game after a several milliseconds to fix a slow down bug on android
		new Thread()
		{
			@Override
			public void run()
			{
				try {
					sleep(200);
				} catch (InterruptedException e) 
				{
					e.printStackTrace();
				}
				Gdx.app.postRunnable(new Runnable() 
				{
					@Override
					public void run() 
					{	
						resetGame();
					}
				});
			}
		}.start();
	}

	void resetGame() 
	{
		if (player.getPlayerJoint() != null)
			world.destroyJoint(player.getPlayerJoint());
		
		//destroy all planets and player
		for (final ImageBody body : b)
			if (body != null && body.getBody() != null)
				world.destroyBody(body.getBody());
		
		//reset variables
		b.clear();
		level.reset();
		ScoreHandler.getInstance().reset();
	
		//new player
		createPlayer();
	}
	
	void addImageBody(ImageBody imageBody) 
	{
		b.add(imageBody);
	}
	
	private void createPlayer() 
	{
		int x = 0;
		int y = 310;
		
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
		//trick to get fix the player from sticking to the planet sometimes
		fdef1.filter.categoryBits = 0x0001;
		fdef1.filter.maskBits = 0x0002 | 0x0003;
		
		//initialize body and fixture
		Body bod = world.createBody(def1);
		bod.createFixture(fdef1);
		
		//add it to list of drawables
		player = new Player(bod,new Sprite(rocketImage));
		b.add(player);
		
		s1.dispose();
	}

	public static World getWorld()
	{
		return world;
	}
	
	public static Preferences getPreferences()
	{
		return prefs;
	}
	
	@Override
	public void render () 
	{
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		//update box2d
		world.step(Gdx.graphics.getDeltaTime(), 6, 2);
		
		//update level and camera
		if (player != null)
		{
			//player death
			if (player.getBody().getPosition().y < -60)
				resetGame();
			
			//update level to load new planets as needed
			level.update(player.getBody().getPosition().x * PPM);
		
			//update camera (only if player wasn't removed
			camera.position.set(
					player.getBody().getPosition().x * PPM + Gdx.graphics.getWidth() / 1.2f,
					player.getBody().getPosition().y * PPM + Gdx.graphics.getHeight() / 2, 
					0);
			camera.zoom = (player.getBody().getPosition().y > 40f ? (float)Math.pow(player.getBody().getPosition().y - 39f, 1/8f) : 1f) * cameraZoomMod;
			camera.update();
			batch.setProjectionMatrix(camera.combined);
		}
		
		//draw lights (stars)
		rayHandler.setCombinedMatrix(camera);
		rayHandler.updateAndRender();
		
		//draw everything
		batch.begin();
		for (ImageBody body : b)
			body.updateAndDraw(batch);
		batch.end();
		
		//draw the ui
		ui.draw();
	}

	@Override
	public void dispose () 
	{
		SoundHandler.getIntance().dispose();
		rayHandler.dispose();
		ui.dispose();
		world.dispose();
		lightWorld.dispose();
		batch.dispose();
		for (int i = 0; i < planetImages.length; i++)
			planetImages[i].dispose();
		rocketImage.dispose();
		resetImage.dispose();
		muteImage.dispose();
		soundImage.dispose();
	}
}
