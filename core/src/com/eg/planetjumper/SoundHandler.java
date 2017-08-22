package com.eg.planetjumper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class SoundHandler 
{
	private static final SoundHandler instance = new SoundHandler();
	
	private Sound failSound, jumpSound, landSound;
	
	private SoundHandler()
	{
		failSound = Gdx.audio.newSound(Gdx.files.internal("sfx/fall.wav"));
		jumpSound = Gdx.audio.newSound(Gdx.files.internal("sfx/jump.wav"));
		landSound = Gdx.audio.newSound(Gdx.files.internal("sfx/landing.wav"));
	}
	
	public static SoundHandler getIntance()
	{
		return instance;
	}

	public void playLand()
	{
		landSound.play();
	}
	
	public void playFall()
	{
		failSound.play();
	}
	
	public void playJump()
	{
		jumpSound.play();
	}
	
	public void dispose()
	{
		failSound.dispose();
		jumpSound.dispose();
		landSound.dispose();
	}
}
