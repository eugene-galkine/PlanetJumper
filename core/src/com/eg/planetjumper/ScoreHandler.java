package com.eg.planetjumper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;

public class ScoreHandler 
{
	private static final ScoreHandler instance = new ScoreHandler();
	
	private int score;
	private Label scoreLabel;
	private Label highscoreLabel;
	private int multiplier;
	
	private ScoreHandler()
	{
		score = 0;
	}
	
	public static ScoreHandler getInstance() 
	{
		return instance;
	}
	
	public void initiate(Stage ui)
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
		scoreLabel.setPosition((Gdx.graphics.getWidth() / 2) - (scoreLabel.getWidth() / 2), Gdx.graphics.getHeight() - (scoreLabel.getHeight() * 2));
		scoreLabel.setAlignment(Align.center);

		Label.LabelStyle style1 = new Label.LabelStyle();
		style1.font = font;
		style1.fontColor = Color.WHITE;
		highscoreLabel = new Label("Highscore: " + PlanetJumper.getPreferences().getInteger("planetjumper.highscore", 0), style1);
		highscoreLabel.setPosition(Gdx.graphics.getWidth() - highscoreLabel.getWidth() - (Gdx.graphics.getWidth()/15) - 10, Gdx.graphics.getHeight() - highscoreLabel.getHeight());
		highscoreLabel.setAlignment(Align.topRight);
		
		ui.addActor(scoreLabel);
		ui.addActor(highscoreLabel);
	}
	
	public void getPoints()
	{
		//give player points
		SoundHandler.getIntance().playLand();
		score += 1 * multiplier;
		multiplier = 1;
		scoreLabel.setText(score + "");
		
		//handle highscores
		if (score > PlanetJumper.getPreferences().getInteger("planetjumper.highscore", 0))
		{
			PlanetJumper.getPreferences().putInteger("planetjumper.highscore", score).flush();
			Label.LabelStyle style = highscoreLabel.getStyle();
			style.fontColor = Color.GOLDENROD;
			highscoreLabel.setStyle(style);
			highscoreLabel.setText("NEW HIGHSCORE: " + score + "!");
		}
	}

	public void reset() 
	{
		//reset the score
		multiplier = 1;
		score = 0;
		scoreLabel.setText(score + "");
		highscoreLabel.setText("Highscore: " + PlanetJumper.getPreferences().getInteger("planetjumper.highscore", 0));
		Label.LabelStyle style = highscoreLabel.getStyle();
		style.fontColor = Color.WHITE;
		highscoreLabel.setStyle(style);
	}

	public void setMultiplier(int mult)
	{
		multiplier = mult + 1;
	}
}
