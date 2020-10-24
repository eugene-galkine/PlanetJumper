package com.eg.planetjumper;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.eg.planetjumper.PlanetJumper;
//import com.google.android.gms.ads.AdListener;
//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdSize;
//import com.google.android.gms.ads.AdView;

public class AndroidLauncher extends AndroidApplication 
{
	@Override
	protected void onCreate (Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		//create a layout and add libgdx game to it
		RelativeLayout layout = new RelativeLayout(this);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		View game = initializeForView(new PlanetJumper(), config);
		layout.addView(game);

		setContentView(layout);
	}
}
