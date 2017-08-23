package com.eg.planetjumper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;

public class ScoreHandler 
{
	private static final ScoreHandler instance = new ScoreHandler();
	
	private int score;
	private Label scoreLabel;
	
	private ScoreHandler()
	{
		score = 0;
	}
	
	public static ScoreHandler getInstance() 
	{
		return instance;
	}
	
	public Label initiate()
	{
		//set up the label
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fabrik.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = Gdx.graphics.getWidth() / 18;
		BitmapFont font = generator.generateFont(parameter);
		generator.dispose(); // don't forget to dispose to avoid memory leaks!
		Label.LabelStyle style = new Label.LabelStyle();
		style.font = font;
		style.fontColor = Color.YELLOW;
		scoreLabel = new Label("0", style);
		scoreLabel.setPosition(Gdx.graphics.getWidth() - scoreLabel.getWidth() - (Gdx.graphics.getWidth()/15) - 10, Gdx.graphics.getHeight() - scoreLabel.getHeight());
		scoreLabel.setAlignment(Align.topRight);
		
		return scoreLabel;
	}
	
	public void getPoints()
	{
		//give player points
		SoundHandler.getIntance().playLand();
		score++;
		scoreLabel.setText(score + "");
	}

	public void reset() 
	{
		//reset the score
		score = 0;
		scoreLabel.setText(score + "");
	}

}
