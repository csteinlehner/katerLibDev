/**
 * you can put a one sentence description of your library here.
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

import java.util.Vector;

import TUIO.TuioProcessing;
import processing.core.*;
import proxml.*;

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
	
	// myParent is a reference to the parent sketch
	private PApplet myParent;
	// tuioClient is a reference to the tuioClient in the parent sketch
	private TuioProcessing tuioClient;
	private proxml.XMLElement katerconfig;
	private XMLInOut xmlInOut;
	
//	private float cursor_size = 15;
//    private float object_size = 90;
    private int[] katerIds;
    private Vector<Kater> katerList = new Vector<Kater>();
    
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
		this.tuioClient = tuioClient;
		this.katerIds=katerTuioIds;
// load XML 
		try{
		    xmlInOut.loadElement("./config/katerkonfig.xml"); 
		  }catch(Exception e){
			  System.err.println(e);
			  System.out.println("xml not loaded");
		  }
	}
//	public void setConfig(int cursorsize, int objectsize, int sensorradius, int sensorspotsize, int angleblur, int distanceblur, int xoffset,int yoffset, int angleOffset){
//		this.cursorsize = cursorsize;
//		this.objectsize = objectsize;
//		this.sensorradius = sensorradius;
//		this.sensorspotsize = sensorspotsize;
//		this.angleblur = angleblur;
//		this.distanceblur = distanceblur;
//		this.xoffset = xoffset;
//		this.yoffset = yoffset;
//		this.angleOffset = angleOffset;
//	}
	/**
	 * 
	 * event evoked if Config-XML is loaded
	 * 
	 * @param element
	 */
	private void xmlEvent(proxml.XMLElement element){
		katerconfig=element;
		System.out.println(katerconfig.getElement());
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
