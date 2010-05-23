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
import java.util.Properties;
import java.util.Vector;

import TUIO.TuioObject;
import TUIO.TuioProcessing;
import processing.core.*;

/**
 * 
 * @example KaterExample 
 * 
 * (the tag @example followed by the name of an example included in folder 'examples' will
 * automatically include the example in the javadoc.)
 *
 */

public class KaterKontrol {
	
	
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
	
	// properties for configuration
	private Properties config = new Properties();
	
	// myParent is a reference to the parent sketch
	private PApplet myParent;
	// tuioClient is a reference to the tuioClient in the parent sketch
	private TuioProcessing tuio;

    private int[] katerIds;
    private Vector<Kater> katerList = new Vector<Kater>();
    private Vector<Kater> activeKater = new Vector<Kater>();
    
	public final static String VERSION = "v0.1";

	/**
	 * a Constructor, usually called in the setup() method in your sketch to
	 * initialize and start the library.
	 * 
	 * @example KaterExample
	 * @param theParent
	 * @param tuioClient
	 * @param katerTuioIds
	 * 
	 */
	public KaterKontrol(PApplet theParent, TuioProcessing tuioClient, int[] katerTuioIds) {
		myParent = theParent;
		this.tuio = tuioClient;
		this.katerIds=katerTuioIds;
		
		readConfig();
	
		createKater();
	}
	
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
	public void setConfig(int cursorsize, int objectsize, int sensorradius, int sensorspotsize, int angleblur, int distanceblur, int xoffset,int yoffset, int angleOffset){
		this.cursorsize = cursorsize;
		this.objectsize = objectsize;
		this.sensorradius = sensorradius;
		this.sensorspotsize = sensorspotsize;
		this.angleblur = angleblur;
		this.distanceblur = distanceblur;
		this.xoffset = xoffset;
		this.yoffset = yoffset;
		this.angleOffset = angleOffset;
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
	 * @param kater
	 * @param theX
	 * @param theY
	 */
	public void KaterGoTo(Kater kater, float theX, float theY){
	    kater.goTo(theX, theY);
	    activeKater.add(kater);
	}
	
	public void KaterIdGoTo(int katerId, float theX, float theY){
		getKaterById(katerId).goTo(theX, theY);
	}
	
	public Kater getKaterById(int katerId){
		for (int i = 0; i < katerList.size(); i++) {
			if(katerList.get(i).getID()==katerId){
				return katerList.get(i);
			}
		}
		System.err.println("No Kater with this ID found");
		return null;
	}
	
	/**
	 * return the version of the library.
	 * 
	 * @return String
	 */
	public static String version() {
		return VERSION;
	}

}
