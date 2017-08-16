package com.eg.planetjumper;

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
	public static final int PPM = 16;
	
	private World world;
	private Box2DDebugRenderer debugRenderer;
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Sprite img;
	private Texture image;
	private Body b;
	
	@Override
	public void create () 
	{
		batch = new SpriteBatch();
		world = new World(new Vector2(0,-9.8f), true);
		camera = new OrthographicCamera(Gdx.graphics.getWidth() / PPM, Gdx.graphics.getHeight() / PPM);
		image = new Texture("planet.png");
		img = new Sprite(image);
		debugRenderer = new Box2DDebugRenderer();
		
		BodyDef def1 = new BodyDef();
		def1.type = BodyType.StaticBody;
		//def1.position.set((Gdx.graphics.getWidth() + img.getWidth()/2) / PPM,(Gdx.graphics.getHeight() + img.getHeight()/2) / PPM);
		def1.position.set(0,0);
		
		CircleShape s1 = new CircleShape();
		s1.setRadius((image.getWidth()/2)/PPM);
		
		FixtureDef fdef1 = new FixtureDef();
		fdef1.shape = s1;
		fdef1.density = 1f;
		
		b = world.createBody(def1);
		b.createFixture(fdef1);
		
		s1.dispose();
	}

	@Override
	public void render () 
	{
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		

		world.step(Gdx.graphics.getDeltaTime(), 6, 2);
		
		img.setPosition(b.getPosition().x * PPM, b.getPosition().y * PPM);
		
		batch.begin();
		batch.draw(img, img.getX(),img.getY());
		batch.end();
		
		debugRenderer.render(world, camera.projection);
	}
	
	@Override
	public void dispose () 
	{
		batch.dispose();
		image.dispose();
	}
}
