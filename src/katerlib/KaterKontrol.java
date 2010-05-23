/**
 * This is the Control Library for the Kater Project
 *
 * ##copyright##
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA  02111-1307  USA
 * 
 * @author		##author##
 * @modified	##date##
 * @version		##version##
 */

package katerlib;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.Vector;

import TUIO.TuioObject;
import TUIO.TuioProcessing;
import processing.core.*;

/**
 * 
 * @example Kater
 * 
 * (the tag @example followed by the name of an example included in folder 'examples' will
 * automatically include the example in the javadoc.)
 *
 */

public class KaterKontrol implements IKaterEventListener{
	
	
	/*
	 *  Config Parameter
	 */
	private int cursorsize = 15;
	private int objectsize = 90;
	private int sensorradius = 45;
	private int sensorspotsize = 70;
	private int angleblur = 20;
	private int distanceblur = 40;
	private int xoffset = 0;
	private int yoffset = 0;
	private int angleOffset = 90;
	private int innerRadius = 90;
	
	// Java-properties for configuration
	private Properties config = new Properties();
	
	// myParent is a reference to the parent sketch
	private PApplet myParent;
	
	// tuioClient is a reference to the tuioClient in the parent sketch
	private TuioProcessing tuio;
	
	// Array of Fiducial IDs
    private int[] katerIds;
    
    // Arrays for the Kater-Management
    private Vector<Kater> katerList = new Vector<Kater>();
    private Vector<Kater> activeKater = new Vector<Kater>();
    
    
    // Methods for EventSystem
    Method katerStarted;
    Method katerFinished;
    
    // Version of the Library
	public final static String VERSION = "v0.1";

	/**
	 * The KaterKontrol Constructor which needs a Reference to PApplet, TUIO and an array with Fiducial IDs 
	 * 
	 * @example KaterExample
	 * @param theParent	Reference to the PApplet (usually this)
	 * @param tuioClient Reference to the TUIOProcessing Client
	 * @param katerTuioIds	An Array of ints with the Fiducial IDs
	 * 
	 */
	public KaterKontrol(PApplet theParent, TuioProcessing tuioClient, int[] katerTuioIds) {
		myParent = theParent;
		this.tuio = tuioClient;
		this.katerIds=katerTuioIds;
		myParent.registerDispose(this);
		checkImplementations();
		readConfig();
	
		createKater();
	}
	/**
	 * 
	 * Checks if the EventDispatch Methods are implemented
	 */
	private void checkImplementations(){
		try {
		      katerFinished = myParent.getClass().getMethod("katerFinished",new Class[] { Kater.class } );
		    } catch (Exception e) {
		    	System.err.println("KaterLib: katerFinished Method ('katerFinished(Kater k)') isn't properly implemented");
		    }
			try {
			      katerStarted = myParent.getClass().getMethod("katerStarted",new Class[] { Kater.class } );
			    } catch (Exception e) {
			    	System.err.println("KaterLib: katerStarted Method ('katerStarted(Kater k)') isn't properly implemented");
			    } 
		
	}
	/**
	 * Reads the config.properties from the data folder or creates one if there isn't any
	 * 
	 */
	private void readConfig(){
		// load config into Input else create file
		InputStream configInput;
		if(myParent.sketchFile("data/config.properties").canRead()){
			configInput = myParent.createInput("config.properties");
		}
		else{
			  System.err.println("KaterLib: Config File can't be found, so I create one.");
			  PrintWriter output = myParent.createWriter("data/config.properties");
			  output.println("cursorsize = "+cursorsize);
			  output.println("objectsize = "+objectsize);
			  output.println("sensorradius = "+sensorradius);
			  output.println("sensorspotsize = "+sensorspotsize);
			  output.println("angleblur = "+angleblur);
			  output.println("distanceblur = "+distanceblur);
			  output.println("xoffset = "+xoffset);
			  output.println("yoffset = "+yoffset);
			  output.println("angleOffset = "+angleOffset);
			  output.println("innerRadius = "+innerRadius);
			  output.flush();
			  output.close();
			  configInput = myParent.createInput("config.properties");
		}
		// load config into variables
		try {
            config.load(configInput);   
            cursorsize = Integer.parseInt(config.getProperty("cursorsize"));
            objectsize = Integer.parseInt(config.getProperty("objectsize"));
            sensorradius = Integer.parseInt(config.getProperty("sensorradius")); 
            sensorspotsize = Integer.parseInt(config.getProperty("sensorspotsize")); 
            angleblur = Integer.parseInt(config.getProperty("angleblur"));
            distanceblur = Integer.parseInt(config.getProperty("distanceblur"));
            xoffset = Integer.parseInt(config.getProperty("xoffset"));
            yoffset = Integer.parseInt(config.getProperty("yoffset"));
            angleOffset = Integer.parseInt(config.getProperty("angleOffset"));
            innerRadius = Integer.parseInt(config.getProperty("innerRadius"));
             }catch(Exception e)
             {
                System.err.println("KaterLib: There is something wrong with your config file, please fix it!");
             }
	}
	/**
	 * create the Kater Objects
	 */
	private void createKater(){
		 for (int i=0; i<katerIds.length;i++){
			 Kater tKater = new Kater(myParent, tuio, katerIds[i]);
			 katerList.add(tKater);
			 tKater.setValues(sensorradius, sensorspotsize, angleblur, distanceblur, xoffset, yoffset, angleOffset, innerRadius);
		    }
	}
	
	/**
	 * 
	 * Kater will be update in the Processing Pre Cycle
	 */
	public void pre(){
			 Vector tuioObjectList = tuio.getTuioObjects();
			    for (int i = 0; i < tuioObjectList.size(); i++) {
			        TuioObject tobj = (TuioObject) tuioObjectList.elementAt(i);
			        for (int j=0; j<katerIds.length;j++){
			            if (tobj.getSymbolID()==katerIds[j]){
			            	katerList.get(j).update(tobj.getScreenX(myParent.width),tobj.getScreenY(myParent.height), tobj.getAngleDegrees());
			            }
			        }
			    }
	}
	/**
	 * 
	 * Kater will be drawn in the Processing Draw Cycle
	 */
	public void draw(){
		for (int i = 0; i < katerList.size(); i++) {
			katerList.get(i).draw();
		}
	}
	
	/**
	 * send a Kater by its Object to a Coordinate
	 * 
	 * @param kater	Reference to the Kater Object
	 * @param theX	X Coordinate
	 * @param theY	Y Coordinate
	 */
	public void KaterGoTo(Kater kater, float theX, float theY){
	    kater.goTo(theX, theY);
	    activeKater.add(kater);
	}
	/**
	 * send a Kater by its Fiducial ID to a Coordinate
	 * 
	 * @param katerId	Fiducial ID of the Kater
	 * @param theX	X Coordinate
	 * @param theY	Y Coordinate
	 */
	public void KaterIdGoTo(int katerId, float theX, float theY){
		getKaterById(katerId).goTo(theX, theY);
	}
	/**
	 * 
	 * Returns a Kater Object by its Fiducial ID
	 * 
	 * @param katerId Fiducial ID of the Kater 
	 * @return	the Kater Object
	 */
	public Kater getKaterById(int katerId){
		for (int i = 0; i < katerList.size(); i++) {
			if(katerList.get(i).getID()==katerId){
				return katerList.get(i);
			}
		}
		System.err.println("No Kater with this ID found");
		return null;
	}
	

	public void onActionStateChange(IKaterEventDispatcher dispatcher,
			EKaterEventState theState) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * return the version of the library.
	 * 
	 * @return String Version of the Library
	 */
	public static String version() {
		return VERSION;
	}
	/**
	 * 
	 *  Should only called by Processing. Empties the Vectors and connection to Processing and TUIO
	 */
	public void dispose(){
		katerList=null;
		activeKater=null;
		tuio=null;
		myParent=null;
	}
}
