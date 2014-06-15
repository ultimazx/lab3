package com.example.lab3;
import java.util.Locale;

import com.jjoe64.graphview.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.opengl.EGLConfig;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import io.socket.*;

import org.json.*;

public class MakeAPlot extends ActionBarActivity implements Renderer {

	 /**
     * Maximum size of buffer
     */
    public static final int BUFFER_SIZE = 2048;
    //private SocketIO socket = null;

    public int[] data = new int[20];
    private SocketIO socket;
   
   

    public MakeAPlot() {
    	
    }
    
    private Cube mCube = new Cube();
    private float mCubeRotation;

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f); 
            
        gl.glClearDepthf(1.0f);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glDepthFunc(GL10.GL_LEQUAL);

        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT,
                  GL10.GL_NICEST);
            
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);        
        gl.glLoadIdentity();
        
        gl.glTranslatef(0.0f, 0.0f, -10.0f);
        gl.glRotatef(mCubeRotation, 1.0f, 1.0f, 1.0f);
            
        mCube.draw(gl);
           
        gl.glLoadIdentity();                                    
            
        mCubeRotation -= 0.15f; 
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        GLU.gluPerspective(gl, 45.0f, (float)width / (float)height, 0.1f, 100.0f);
        gl.glViewport(0, 0, width, height);

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
    }
    
    /**
     * Constructor with Host, Port and MAC Address
     * @param host
     * @param port
     * @param macAddress
     */
   
    private void setupSocket(){
    	try{
    		socket = new SocketIO("http://192.168.2.4:8080/");
    		
    		socket.connect(new IOCallback() {
                @Override
                public void onMessage(JSONObject json, IOAcknowledge ack) {
                    try {
                        System.out.println("Server said:" + json.toString(2));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                
                @Override
                public void onMessage(String data, IOAcknowledge ack) {
                    System.out.println("Server said: " + data);
                }

                @Override
                public void onError(SocketIOException socketIOException) {
                    System.out.println("an Error occured");
                    socketIOException.printStackTrace();
                }

                @Override
                public void onDisconnect() {
                    System.out.println("Connection terminated.");
                }

                @Override
                public void onConnect() {
                    System.out.println("Connection established");
                }
                
                
                @Override
                public void on(String event, IOAcknowledge ack, Object... args) {
                	int count = 0;
                    System.out.println("Server triggered event '" + event + "'");
                    System.out.println("Data: " + args[0].toString());
                    data[count] = Integer.parseInt(args[0].toString());
                    count++;
                }
            });

            // This line is cached until the connection is establisched.
            socket.send("Hello Server!");
    		
    		
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	
          
    
    }
	
    
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        GLSurfaceView view = new GLSurfaceView(MakeAPlot.this);
        view.setRenderer(new OpenGLRenderer());
        setContentView(view);
        setContentView(R.layout.activity_make_aplot);
        setupSocket();
		
      

        
        
        
        //write out over the socket
        //socket.emit("temp", "Hello");


       
        
      
//Button button2 = (Button) findViewById(R.id.button2);
//button2.setOnClickListener(new OnClickListener(){
//public void onClick(View v){
//		
//}
//});
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        
        }
        
        
        		
        	
        
    


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.make_aplot, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void button1(View v){
    	
    	socket.emit("temp", "1");
    	
    	  
        
		// int example series data
		GraphViewSeries plot1 = new GraphViewSeries(new GraphViewData[] {
		    new GraphViewData(1, data[0])
		    , new GraphViewData(2, data[1])
		    , new GraphViewData(3, data[2])
		    , new GraphViewData(4, data[3])
		    , new GraphViewData(5, data[4])
		    , new GraphViewData(6, data[5])
		    , new GraphViewData(7, data[6])
		    , new GraphViewData(8, data[7])
		    , new GraphViewData(9, data[8])
		    , new GraphViewData(10, data[9])
		    , new GraphViewData(11, data[10])
		    , new GraphViewData(12, data[11])
		    , new GraphViewData(13, data[12])
		    , new GraphViewData(14, data[13])
		    , new GraphViewData(15, data[14])
		    , new GraphViewData(16, data[15])
		    , new GraphViewData(17, data[16])
		    , new GraphViewData(18, data[17])
		    , new GraphViewData(19, data[18])
		    , new GraphViewData(20, data[19])
		});
		 
		GraphView graphView = new LineGraphView(
		    MakeAPlot.this // context
		    , "2DPlot" // heading
		);
		graphView.addSeries(plot1); // data
		 
		LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
		layout.addView(graphView);
    }
    

    
    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_make_aplot, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }
    
 
   public class GraphViewData implements GraphViewDataInterface {
	    private double x,y;

	    public GraphViewData(double x, double y) {
	        this.x = x;
	        this.y = y;
	    }

	    @Override
	    public double getX() {
	        return this.x;
	    }

	    @Override
	    public double getY() {
	        return this.y;
	    }
	}
@Override
public void onSurfaceCreated(GL10 arg0,
		javax.microedition.khronos.egl.EGLConfig arg1) {
	// TODO Auto-generated method stub
	
}

}
