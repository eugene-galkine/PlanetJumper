package com.eg.planetjumper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class SoundHandler 
{
	private static final SoundHandler instance = new SoundHandler();
	
	private Sound failSound, jumpSound, landSound, highscoreSound, multiplierSound;
	private boolean muted;
	
	private SoundHandler()
	{
		failSound = Gdx.audio.newSound(Gdx.files.internal("sfx/fall.wav"));
		jumpSound = Gdx.audio.newSound(Gdx.files.internal("sfx/jump.wav"));
		landSound = Gdx.audio.newSound(Gdx.files.internal("sfx/landing.wav"));
		highscoreSound = Gdx.audio.newSound(Gdx.files.internal("sfx/highscore.wav"));
		multiplierSound = Gdx.audio.newSound(Gdx.files.internal("sfx/multiplier.wav"));
		
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
	
	public void playHighscore()
	{
		if (!muted)
			highscoreSound.play();
	}
	
	public void playMultiplier()
	{
		if (!muted)
			multiplierSound.play();
	}
	
	public void dispose()
	{
		failSound.dispose();
		jumpSound.dispose();
		landSound.dispose();
		multiplierSound.dispose();
		highscoreSound.dispose();
	}
	
	public boolean toggleSound()
	{
		muted = !muted;
		PlanetJumper.getPreferences().putBoolean("planetjumper.muted", muted).flush();
		return muted;
	}

	public boolean getToggle() 
	{
		return muted;
	}
}
