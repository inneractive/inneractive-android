package com.inneractive.api.ads.sample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TabHost;
import android.widget.Toast;

import com.inneractive.api.ads.sdk.InneractiveAdView;
import com.inneractive.api.ads.sdk.InneractiveAdView.AdType;
import com.inneractive.api.ads.sdk.InneractiveAdView.Gender;
import com.inneractive.api.ads.sdk.InneractiveAdView.InneractiveBannerAdListener;
import com.inneractive.api.ads.sdk.InneractiveAdView.InneractiveErrorCode;
import com.inneractive.api.ads.sdk.InneractiveInterstitialView;
import com.inneractive.api.ads.sdk.InneractiveInterstitialView.InneractiveInterstitialAdListener;

public class IaSampleAdActivity extends Activity implements InneractiveBannerAdListener, InneractiveInterstitialAdListener{

	private Toast mToast;
	
    InneractiveAdView mBanner;  
    InneractiveAdView mRect;
    InneractiveInterstitialView mInterstitial;
    
    InneractiveAdView mXmlBanner;  
    InneractiveAdView mXmlRect;
    
    LinearLayout layout;
    
	String sdkVersion = "";
	
	EditText bannerAppIdText;
	EditText interstitialAppIdText;
	String appId;
	
	final String INNERACTIVE_LOG_TAG = "InneractiveSampleApp"; 

	
    public void onCreate(Bundle savedInstanceState) 
    { 
    	super.onCreate(savedInstanceState); 
    	setContentView(R.layout.tab_layout);
    	    	
    	initialize();
    }

    void initialize(){
    	TabHost tabs=(TabHost)findViewById(R.id.tabhost); 
    	tabs.setup(); 
    	TabHost.TabSpec spec=tabs.newTabSpec("tag1"); 
    	spec.setContent(R.id.tab1); 
       	spec.setIndicator("Banner"); 
    	tabs.addTab(spec); 
    	
    	spec=tabs.newTabSpec("Banner XML"); 
    	spec.setContent(R.id.tab2); 
    	spec.setIndicator("Banner XML"); 
    	tabs.addTab(spec); 
    	
    	spec=tabs.newTabSpec("Interstitial"); 
    	spec.setContent(R.id.tab3); 
    	spec.setIndicator("Interstitial"); 
    	tabs.addTab(spec); 
    	
    	tabs.setCurrentTab(0); 
    	
    	
    	layout = (LinearLayout) findViewById(R.id.ad_layout);
    	
    	bannerAppIdText = (EditText) findViewById(R.id.bannerAppId);
    	interstitialAppIdText = (EditText)findViewById(R.id.interstitialAppId);   	
    }
    
    
    @Override
    public void onResume() {
    	super.onResume();
	}
    
    @Override
    protected void onDestroy() {
    	if (mToast != null) {
    		mToast.cancel();
    		mToast = null;
    	}
    	// Call inneractive::destroy only if this activity is actually finishing, and not during restart.
        if (isFinishing()) {
        	if (mBanner != null) {
        		mBanner.destroy();
        		mBanner = null;
        	}
        	if (mInterstitial != null) {
        		mInterstitial.destroy();
        		mInterstitial = null;
        	}
        	if (mRect != null) {
        		mRect.destroy();
        		mRect = null;
        	}
        	if (mXmlBanner != null) {
        		mXmlBanner.destroy();
        		mXmlBanner = null;
        	}
        	if (mXmlRect != null) {
        		mXmlRect.destroy();
        		mXmlRect = null;
        	}

        }
        super.onDestroy();
    }
    public void onRadioXmlBannerClicked(View view) {
    	Log.d(INNERACTIVE_LOG_TAG, "onRadioXmlBannerClicked");
        // Is the button checked?
        boolean checked = ((RadioButton) view).isChecked();
        if (checked){
        	mXmlBanner = (InneractiveAdView) findViewById(R.id.banner_ad);
        	mXmlBanner.setBannerAdListener(this);
        	mXmlBanner.loadAd();
        }
    }
    
    public void onRadioXmlRectClicked(View view) {
    	Log.d(INNERACTIVE_LOG_TAG, "onRadioXmlRectClicked");
        // Is the button checked?
        boolean checked = ((RadioButton) view).isChecked();
        if (checked){
        	mXmlRect = (InneractiveAdView) findViewById(R.id.rectangle_ad);
        	mXmlRect.setBannerAdListener(this);
        	mXmlRect.loadAd();
        }
    }
    
	public void onRadioAdClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        
        // validate the app id value (use default value if needed)
        if (!validateAppId(bannerAppIdText)){
        	eventToast("You didn't set your specific appID! The default appId will be used. make sure that you're using your specific appId for tracking your traffic!" , Toast.LENGTH_LONG);
        }

        // destroy previous ads
    	if (mBanner != null) {
    		mBanner.destroy();
    		mBanner = null;
    	}
    	
    	if (mRect != null) {
    		mRect.destroy();
    		mRect = null;
    	}
    	
        // Check which radio button was clicked
        switch(view.getId()) {
        
            case R.id.radio_load_banner:
                if (checked){
                	// initialize banner
                	mBanner = new InneractiveAdView(this, appId, AdType.Banner);
                	
                	// setters
                	mBanner.setBannerAdListener(this);
                	mBanner.setRefreshInterval(30);
                	initializeOptionalParameters(mBanner);

                	
                	// get the SDK version 
                	sdkVersion = mBanner.getSDKversion();
                	
                	// load ad
                	mBanner.loadAd();
                	
                	// add the ad to the app layout
                	layout.addView(mBanner);
                }
                break;
                
            case R.id.radio_load_rectangle:
                if (checked){
                	// initialize rectangle
                	mRect = new InneractiveAdView(this, appId, AdType.Rectangle);  
                   
                	// setters
	                mRect.setBannerAdListener(this);
	                mRect.setRefreshInterval(120);
	                initializeOptionalParameters(mRect);
	                
	                // get the SDK version 
	            	sdkVersion = mRect.getSDKversion();
	            	
	            	// load ad
	            	mRect.loadAd();
	            	
	            	// add the ad to the app layout
	            	layout.addView(mRect);
                }
                break;
        }
        
    	this.setTitle("Inneractive Sample App. SDK v= " + sdkVersion);
  
    }
    
    
    public void onLoadInterstitialAdClicked(View view){
    	
    	// validate the app id value (use default value if needed)
    	if(!validateAppId(interstitialAppIdText)){
    		eventToast("You didn't set your specific appID! The default appId will be used. make sure that you're using your specific appId for tracking your traffic!" , Toast.LENGTH_LONG);
    	}

    	// destroy previous ad
    	if (mInterstitial != null) {
    		mInterstitial.destroy();
    		mInterstitial = null;
    	}
    	
    	// initialize the interstitial ad
    	mInterstitial = new InneractiveInterstitialView(this, appId);
    	
    	// setters
    	mInterstitial.setInterstitialAdListener(this);
    	initializeOptionalParameters(mInterstitial);
    	
    	// get the SDK version 
    	sdkVersion = mInterstitial.getSDKversion();
    	this.setTitle("Inneractive Sample App. SDK v= " + sdkVersion);
    	
    	// load ad
    	mInterstitial.loadAd();
   
    }
    
    public void onShowInterstitialAdClicked(View view){
    	// check if the ad is ready
    	if (mInterstitial != null && mInterstitial.isReady()){
    		mInterstitial.showAd();
    	}
    	else{
    		eventToast("The Interstitial ad is not ready yet.", Toast.LENGTH_SHORT);
    	}
    }
    
    public void initializeOptionalParameters(InneractiveAdView ad) {

    	ad.setAge(35);
    	ad.setGender(Gender.FEMALE);
    	ad.setKeywords("pop,rock,music");
    	ad.setZipCode("94401");	
    	ad.setAndroidIdEnabled(true);
    	ad.setDeviceIdEnabled(false);
	}

    public boolean validateAppId(EditText appIdEditText) {
		if(appIdEditText == null || appIdEditText.length() <= 0){
			appId = "MyCompany_MyApp";
			return false;
		}
		
		appId = appIdEditText.getText().toString();
		Log.i(INNERACTIVE_LOG_TAG,"appId = " + appId);
		return true;
    }
    
    
    protected void eventToast(String message, int length) {
        try{
        	mToast = Toast.makeText(this, message, length);
        	mToast.show();
        }
        catch(Exception e){
        	
        }
    }

    @Override
    public void inneractiveBannerLoaded(InneractiveAdView banner) {
        Log.d(INNERACTIVE_LOG_TAG, "*** inneractiveBannerLoaded ***" + banner);
        eventToast("Inneractive - inneractiveBannerLoaded",Toast.LENGTH_SHORT);
    }
    
    @Override
    public void inneractiveDefaultBannerLoaded(InneractiveAdView banner) {
        Log.d(INNERACTIVE_LOG_TAG, "*** inneractiveDefaultBannerLoaded ***" + banner);
        eventToast("Inneractive - inneractiveDefaultBannerLoaded",Toast.LENGTH_SHORT);
    }
    
    @Override
    public void inneractiveBannerFailed(InneractiveAdView banner, InneractiveErrorCode errorCode) {
        Log.d(INNERACTIVE_LOG_TAG, "*** inneractiveBannerFailed. error: " + errorCode.toString() + " ***");
        eventToast("Inneractive - inneractiveBannerFailed. " + errorCode, Toast.LENGTH_SHORT);
    }

    @Override
    public void inneractiveBannerClicked(InneractiveAdView banner) {
        Log.d(INNERACTIVE_LOG_TAG, "*** inneractiveBannerClicked ***" + banner);
        eventToast("Inneractive - inneractiveBannerClicked", Toast.LENGTH_SHORT);
    }

    @Override
    public void inneractiveBannerExpanded(InneractiveAdView banner) {
        Log.d(INNERACTIVE_LOG_TAG, "*** inneractiveBannerExpanded ***" + banner);
        eventToast("Inneractive - inneractiveBannerExpanded" , Toast.LENGTH_SHORT);
    }

    @Override
    public void inneractiveBannerCollapsed(InneractiveAdView banner) {
        Log.d(INNERACTIVE_LOG_TAG, "*** inneractiveBannerCollapsed ***" + banner);
        eventToast("Inneractive - inneractiveBannerCollapsed", Toast.LENGTH_SHORT);
    }
    
	@Override
	public void inneractiveBannerResized(InneractiveAdView banner) {
        Log.d(INNERACTIVE_LOG_TAG, "*** inneractiveBannerResized ***" + banner);
        eventToast("Inneractive - inneractiveBannerResized" , Toast.LENGTH_SHORT);
		
	}

	@Override
	public void inneractiveInterstitialLoaded(InneractiveInterstitialView interstitial) {
        Log.d(INNERACTIVE_LOG_TAG, "*** inneractiveInterstitialLoaded ***" + interstitial);
        eventToast("Inneractive - inneractiveInterstitialLoaded" , Toast.LENGTH_SHORT);
		
	}
	
	@Override
	public void inneractiveDefaultInterstitialLoaded(InneractiveInterstitialView interstitial) {
		Log.d(INNERACTIVE_LOG_TAG, "*** inneractiveDefaultInterstitialLoaded ***" + interstitial);
        eventToast("Inneractive - inneractiveDefaultInterstitialLoaded" , Toast.LENGTH_SHORT);
		
	}

	@Override
	public void inneractiveInterstitialFailed(InneractiveInterstitialView interstitial,
			InneractiveErrorCode errorCode) {
        Log.d(INNERACTIVE_LOG_TAG, "*** inneractiveInterstitialFailed. error: " + errorCode + " ***");
        eventToast("Inneractive - inneractiveInterstitialFailed. " + errorCode , Toast.LENGTH_SHORT);
		
	}

	@Override
	public void inneractiveInterstitialShown(InneractiveInterstitialView interstitial) {
		Log.d(INNERACTIVE_LOG_TAG, "*** inneractiveInterstitialShown *** " + interstitial);
		eventToast("Inneractive - inneractiveInterstitialShown", Toast.LENGTH_SHORT);
	}

	@Override
	public void inneractiveInterstitialClicked(InneractiveInterstitialView interstitial) {
		Log.d(INNERACTIVE_LOG_TAG, "*** inneractiveInterstitialClicked ***");
		eventToast("Inneractive - inneractiveInterstitialClicked" , Toast.LENGTH_SHORT);
	}

	@Override
	public void inneractiveInterstitialDismissed(InneractiveInterstitialView interstitial) {
		Log.d(INNERACTIVE_LOG_TAG, "*** inneractiveInterstitialDismissed ***");		
		eventToast("Inneractive - inneractiveInterstitialDismissed", Toast.LENGTH_SHORT);
	}


	@Override
	public void inneractiveAdWillOpenExternalApp(InneractiveAdView banner) {
		Log.d(INNERACTIVE_LOG_TAG, "*** inneractiveAdWillOpenExternalApp ***");		
		eventToast("Inneractive - inneractiveAdWillOpenExternalApp", Toast.LENGTH_SHORT);
	}


	@Override
	public void inneractiveInternalBrowserDismissed(InneractiveAdView banner) {
		Log.d(INNERACTIVE_LOG_TAG, "*** inneractiveInternalBrowserDismissed ***");		
		eventToast("Inneractive - inneractiveInternalBrowserDismissed",Toast.LENGTH_SHORT);
	}
}
