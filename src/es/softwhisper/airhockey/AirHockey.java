package es.softwhisper.airhockey;

import es.softwhisper.airhockey.renderers.AirHockeyRenderer;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.view.Menu;
import android.widget.Toast;

/**
 * 
 * @author pablo
 *
 */
public class AirHockey extends Activity {

	private GLSurfaceView glSurfaceView;
	private boolean rendererset = false;
	private boolean supportEs2;
	private ActivityManager activityManager;
	private ConfigurationInfo configurationInfo;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air_hockey);
    	
        activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
    	configurationInfo = activityManager.getDeviceConfigurationInfo();
    	supportEs2 = configurationInfo.reqGlEsVersion >= 0x20000;
    	
        glSurfaceView = new GLSurfaceView(this);
        
        if (configureGLSurface()) {
        	setContentView(glSurfaceView);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_air_hockey, menu);
        return true;
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	
    	if (rendererset) {
    		glSurfaceView.onPause();
    	}
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	
    	if (rendererset) {
    		glSurfaceView.onResume();
    	}
    }
    
    private boolean configureGLSurface() {
    	if (supportEs2) {
    		glSurfaceView.setEGLContextClientVersion(2); // Set GL to ES 2.0
    		glSurfaceView.setRenderer(new AirHockeyRenderer(this));
    		
    		rendererset = true;
    		
    		return true;
    	} else {
    		Toast.makeText(this, "Este cacharro no soporta GL 2", Toast.LENGTH_LONG).show();
    		return false;
    	}
    }
}

