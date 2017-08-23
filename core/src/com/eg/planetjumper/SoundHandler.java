package com.eg.planetjumper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class SoundHandler 
{
	private static final SoundHandler instance = new SoundHandler();
	
	private Sound failSound, jumpSound, landSound;
	private boolean muted;
	
	private SoundHandler()
	{
		failSound = Gdx.audio.newSound(Gdx.files.internal("sfx/fall.wav"));
		jumpSound = Gdx.audio.newSound(Gdx.files.internal("sfx/jump.wav"));
		landSound = Gdx.audio.newSound(Gdx.files.internal("sfx/landing.wav"));
		
		muted = PlanetJumper.getPreferences().getBoolean("planetjumper.muted", false);
	}
	
	public static SoundHandler getIntance()
	{
		return instance;
	}

	public void playLand()
	{
		if (!muted)
			landSound.play();
	}
	
	public void playFall()
	{
		if (!muted)
			failSound.play();
	}
	
	public void playJump()
	{
		if (!muted)
			jumpSound.play();
	}
	
	public void dispose()
	{
		failSound.dispose();
		jumpSound.dispose();
		landSound.dispose();
	}
	
	public boolean toggleSound()
	{
		muted = !muted;
		PlanetJumper.getPreferences().putBoolean("planetjumper.muted", muted);
		return muted;
	}

	public boolean getToggle() 
	{
		return muted;
	}
}
