package com.eg.planetjumper;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.eg.planetjumper.PlanetJumper;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

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

		//set up ad banner
		final AdView adview = new AdView(this);
		adview.setAdListener(new AdListener()
		{
			@Override
			public void onAdLoaded()
			{
				//fix issue with first ad not being visible
				adview.setVisibility(adview.GONE);
				adview.setVisibility(adview.VISIBLE);
			}
		});
		adview.setAdSize(AdSize.SMART_BANNER);
		adview.setAdUnitId("ca-app-pub-1923584304619937/6974153183");

		//set up layout for ad banner
		RelativeLayout.LayoutParams adviewparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		adviewparams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		adviewparams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		layout.addView(adview, adviewparams);

		//start the ad
		AdRequest.Builder builder = new AdRequest.Builder();
		builder.addTestDevice("6FAF0C227CAC49AEF16E8B427D991CF1");
		adview.loadAd(builder.build());

		setContentView(layout);
	}
}
