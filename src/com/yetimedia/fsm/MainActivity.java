package com.yetimedia.fsm;

import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;



public class MainActivity extends Activity {

    private static String APP_ID = "131951090214210"; // Replace your App ID here
    public final static String FB_NAME = "com.yetimedia.fsm-android.FB_NAME";
    public final static String FB_UID = "com.yetimedia.fsm-android.FB_UID";
    public final static String FB_PROFILE = "com.yetimedia.fsm-android.FB_PROFILE";
    
    // Instance of Facebook Class
    private Facebook facebook;
    String FILENAME = "AndroidSSO_data";
    private SharedPreferences mPrefs;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        facebook = new Facebook(APP_ID);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    public void fbLogin(View view){
    	ImageButton btnFbLogin = (ImageButton) findViewById(R.id.login);
    	btnFbLogin.setOnClickListener(new View.OnClickListener() {
    	    @Override
    	    public void onClick(View v) {
    	            loginToFacebook();
   	            	String response = new String();
					try {
						response = facebook.request("me");
					} catch (MalformedURLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					JSONObject reponse_json = null;
	            	try {
						reponse_json = Util.parseJson(response);
					} catch (FacebookError e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    	            Intent intent = new Intent(MainActivity.this, ScanMe.class);
    	            
    	            String response_name = null;
    	            String response_uid = null;
    	            
					try {
						response_name = reponse_json.getString("name");
	    	            response_uid = reponse_json.getString("id");
	    	            
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    	            
    	            
	            	intent.putExtra(FB_NAME, response_name);
	            	intent.putExtra(FB_UID, response_uid);
	            	startActivity(intent);
    	            
    	        }
    	});
    }
    public void loginToFacebook() {
        mPrefs = getPreferences(MODE_PRIVATE);
        String access_token = mPrefs.getString("access_token", null);
        long expires = mPrefs.getLong("access_expires", 0);
     
        if (access_token != null) {
            facebook.setAccessToken(access_token);
        }
     
        if (expires != 0) {
            facebook.setAccessExpires(expires);
        }
     
        if (!facebook.isSessionValid()) {
            facebook.authorize(this,
                    new String[] { "email", "publish_stream" },
                    new DialogListener() {
     
                        @Override
                        public void onCancel() {
                            // Function to handle cancel event
                        }
     
                        @Override
                        public void onComplete(Bundle values) {
                            // Function to handle complete event
                            // Edit Preferences and update facebook acess_token
                            SharedPreferences.Editor editor = mPrefs.edit();
                            editor.putString("access_token",
                                    facebook.getAccessToken());
                            editor.putLong("access_expires",
                                    facebook.getAccessExpires());
                            editor.commit();
                        }
     
                        @Override
                        public void onError(DialogError error) {
                            // Function to handle error
     
                        }
     
                        @Override
                        public void onFacebookError(FacebookError fberror) {
                            // Function to handle Facebook errors
     
                        }
     
                    });
        }
    }    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        facebook.authorizeCallback(requestCode, resultCode, data);
    }
}
