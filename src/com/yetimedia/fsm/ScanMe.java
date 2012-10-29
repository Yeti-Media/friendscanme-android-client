package com.yetimedia.fsm;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class ScanMe extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_me);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        String fb_name = intent.getStringExtra(MainActivity.FB_NAME);
        String fb_uid = intent.getStringExtra(MainActivity.FB_UID);
        try {
			getUserData(fb_name, fb_uid);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public void getUserData(String fb_name, String fb_uid) throws IOException{
      
      String uri = "http://friendscan.me/api/v1/authentication";
      String fsmToken = "90bbf880f9dd2ec3459cb47d1feb67bc";
      HttpClient httpClient = new DefaultHttpClient();
      HttpPost httpPost = new HttpPost(uri);

      httpPost.setHeader("X-Access-Token", fsmToken);
      
      // Building post parameters, key and value pair
      List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
      nameValuePair.add(new BasicNameValuePair("auth[provider]", "facebook"));
      nameValuePair.add(new BasicNameValuePair("auth[info][name]", fb_name));
      nameValuePair.add(new BasicNameValuePair("auth[uid]", fb_uid));
            

   // Url Encoding the POST parameters
      try {
          httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
      } catch (UnsupportedEncodingException e) {
          // writing error to Log
          e.printStackTrace();
      }
      
      try {
    	    HttpResponse response = httpClient.execute(httpPost);

    	    String responseBody = EntityUtils.toString(response.getEntity());    	    
    	    JSONObject json = new JSONObject(responseBody);
    	    String qr_code = json.getString("qr_code");
      	    ImageView qrImg = (ImageView) findViewById(R.id.qr_code);
      	    ImageView profileImg = (ImageView) findViewById(R.id.profile_pic);
      	 
            // Set name on TextView
            TextView name = (TextView) findViewById(R.id.name);
      	   name.setText(fb_name);

      	   // Set qr code
      	   URL url = new URL(qr_code);
           URLConnection conn = url.openConnection();                   
           HttpURLConnection httpConn = (HttpURLConnection)conn;
           httpConn.setRequestMethod("GET");
           httpConn.connect();               
           if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
             InputStream inputStream = httpConn.getInputStream();                     
             Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
             inputStream.close();
             qrImg.setImageBitmap(bitmap);
           }

           
     	   // Set profile picture
      	   URL url_profile = new URL("http://graph.facebook.com/"+fb_uid+"/picture?type=large");
           URLConnection conn_profile = url_profile.openConnection();                   
           HttpURLConnection httpConn_profile = (HttpURLConnection)conn_profile;
           httpConn_profile.setRequestMethod("GET");
           httpConn_profile.connect();               
           if (httpConn_profile.getResponseCode() == HttpURLConnection.HTTP_OK) {
             InputStream inputStream = httpConn_profile.getInputStream();                     
             Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
             inputStream.close();
             profileImg.setImageBitmap(bitmap);
           }

   
    	    
   //         Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL(qr_code).getContent());
		//	qr_code_image.setImageBitmap(bitmap);
			 
    	} catch (ClientProtocolException e) {
    	    // writing exception to log
    	    e.printStackTrace();
		} catch (MalformedURLException e) {
		  e.printStackTrace();
    	 
    	} catch (IOException e) {
    	    // writing exception to log
    	    e.printStackTrace();
    	} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_scan_me, menu);
        return true;
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
/*        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        } */
        return super.onOptionsItemSelected(item);
    }

}
