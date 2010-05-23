package katerlib;

import java.util.Vector;

import TUIO.TuioObject;
import TUIO.TuioProcessing;
import processing.core.PApplet;
import processing.core.PVector;

public class Kater implements IKaterEventDispatcher{
	private PApplet p;
	private TuioProcessing tuio;
	
	// config vars
    private int mySensorRadius;
    private int mySensorSpotsize;
    private int myAngleBlur;
    private int myDistanceBlur;
    private int myXoffset;
    private int myYoffset;
    private int myAngleOffset;
    private int myInnerRadius;
    
    // 
    private int myID;
    private float myX;
    private float myY;
    private float myAngle;
    private int myActionState = 0;
    private int myLastActionState = 0;
    private Boolean[] mySensorImage = {false, false, false, false};
    private PVector myVector = new PVector(0, 0);
    private PVector myTarget = new PVector(0, 0); 
    private Vector<IKaterEventListener> listenerList = new Vector<IKaterEventListener>();
    
    // activity vars
    private Boolean active = false;
    private Boolean running= false;
    
    /**
     * The single Kater object has to be created with a reference to the PApplet, the Tuio and the referencing tuioID
     * 
     * @param p
     * @param katerkontrol
     * @param theID
     */
    Kater(PApplet p, TuioProcessing tuio, int theID) {
		myID = theID;
		System.out.println("KaterLib: Kater No. "+myID+" created!");
		this.p=p;
		this.tuio=tuio;
	}
    
    void setValues(int theSensorRadius, int theSensorSpotsize, int theAngleBlur, int theDistanceBlur, int theXoffset, int theYoffset, int theAngleOffset, int theInnerRadius) {
        mySensorRadius = theSensorRadius;
        mySensorSpotsize = theSensorSpotsize;
        myAngleBlur = theAngleBlur;
        myDistanceBlur = theDistanceBlur;
        myXoffset = theXoffset;
        myYoffset = theYoffset;
        myAngleOffset = theAngleOffset;
        myInnerRadius = theInnerRadius;
    }
    /**
     * 
     * Send Coordinate to Kater by two floats
     * 
     * @param targetX
     * @param targetY
     */
    void goTo(float targetX, float targetY) {
    	this.running=true;
        myTarget.x = targetX;
        myTarget.y = targetY;
    }
    /**
     * 
     * Update the Kater each frame with his Coordiantes and Angle from TUIO
     * 
     * @param theX
     * @param theY
     * @param theAngle
     */
    public void update(float theX, float theY, float theAngle) {
        myX = theX;
        myY = theY;
        myVector.x = theX;
        myVector.y = theY;
        myAngle = theAngle+myAngleOffset;
        /*
         * Update the Status
         */
        float movementAngle = myAngle + PApplet.degrees(PApplet.atan2(myVector.x - myTarget.x, myVector.y - myTarget.y));
        if (movementAngle > 180) {
            movementAngle -= 360;
        }
            
        if(running){
        if (myVector.dist(myTarget) > myDistanceBlur) {
        	
        	
        // check, if you are already there
            if ((movementAngle < myAngleBlur) && (movementAngle > -myAngleBlur)) {
            	
                // go ahead
                myActionState = 1;
            } else {
                if (movementAngle > myAngleBlur) {
                    // turn
                    myActionState = 3;
                } else if (movementAngle < -myAngleBlur) {
                    // turn
                    myActionState = 2;
                }
            }
        } else {
            myActionState = 0;
            System.out.println("Robot finished");
            dispatchFinish();
        }
 }else{	// not active
	    myActionState = 0;
        }
        if(myActionState!=myLastActionState){
        switch (myActionState) {
            case 0: // stop
                mySensorImage[0] = false;
                mySensorImage[1] = false;
                mySensorImage[3] = false;
                break;

            case 1: // forward
                mySensorImage[0] = true;
                mySensorImage[1] = false;
                mySensorImage[3] = false;
                break;

            case 2: // turn right
                mySensorImage[0] = false;
                mySensorImage[1] = true;
                mySensorImage[3] = false;
                break;

            case 3: // turn left
                mySensorImage[0] = false;
                mySensorImage[1] = false;
                mySensorImage[3] = true;
                break;
        }
        myLastActionState=myActionState;
        }
    }
    
    /**
     * 
     * Draw the Kater to the PApplet
     * 
     */
    void draw() {
    		p.pushMatrix();
    		p.translate(myX, myY);
        	p.ellipseMode(PApplet.CENTER);
            // Adjust the Sensor by 45 degrees

            p.rotate(PApplet.radians(135));
            p.noStroke();
            p.fill(0);
            // sensor fields to black
            p.ellipse(-mySensorRadius, 0, mySensorSpotsize, mySensorSpotsize);     // left
            p.ellipse(0, mySensorRadius, mySensorSpotsize, mySensorSpotsize);      // top
            p.ellipse(mySensorRadius, 0, mySensorSpotsize, mySensorSpotsize);      // right
            p.ellipse(0, -mySensorRadius, mySensorSpotsize, mySensorSpotsize);     // bottom
            // switching on sensor fields
            
            p.fill(255);
            
            if (mySensorImage[0]) {
                p.ellipse(-mySensorRadius, 0, mySensorSpotsize, mySensorSpotsize);     // left
            }
            
            if (mySensorImage[1]) {
                p.ellipse(0, -mySensorRadius, mySensorSpotsize, mySensorSpotsize);      // top
            }

            if (mySensorImage[2]) {
                p.ellipse(mySensorRadius, 0, mySensorSpotsize, mySensorSpotsize);      // right
            }

            if (mySensorImage[3]) {
                p.ellipse(0, mySensorRadius, mySensorSpotsize, mySensorSpotsize);     // bottom
            }
            
          
            p.fill(0);
            p.ellipse(0, 0, myInnerRadius, myInnerRadius);
            p.popMatrix();
    	}
    /**
     * 
     * @return the X Coordinate
     */
    float getX(){return myX;}
    /**
     * 
     * @return the Y Coordinate
     */
    float getY(){return myY;}
    /**
     * 
     * @return the Center X
     */
    float getCenterX(){return (myX+myXoffset);}
    /**
     * 
     * @return the Center Y
     */
    float getCenterY(){return (myY+myYoffset);}
    /**
     * 
     * @return the TUIO ID
     */
    int getID(){return myID;}
    /**
     * set the running state to true
     * 
     */
    void startrun(){running=true;}
    /**
     * 
     * set the running state to false and dispatch the finish event
     */
    void stop(){
    	running=false;
    	dispatchFinish();
    	}
    /**
     * 
     * set the Kater to an active state
     */
    void activate(){active=true;}
    /**
     * 
     * set the Kater to an deactive state
     */
    void deactivate(){active=false;}
    /**
     * 
     * @return if the Kater is currently running
     */
    Boolean getRunning(){return running;}
    /**
     * 
     * @return the TuioObject of the Kater
     */
    TuioObject getTobj(){
    	 Vector tuioObjectList = tuio.getTuioObjects();
    	    for (int i = 0; i < tuioObjectList.size(); i++) {
    	        TuioObject tobj = (TuioObject) tuioObjectList.elementAt(i);
    	        if(tobj.getSymbolID()==myID){
    	        	return tobj;
    	        }
    	    }
    	    System.err.println("Kater TUIO Object not found");
    	    return null;
    	}
    /**
     * 
     * @return the angle
     */
    public float getAngle(){return myAngle;}

    /**
     * 
     * dispatch the finish event
     */
    public void dispatchFinish() {
    	this.running=false;
    	for (int i = 0; i < listenerList.size(); i++) {
    		listenerList.get(i).onActionStateChange(this, EKaterEventState.finished);
    	}
    }
    /**
     * 
     * turn the lights on the Kater on
     */
    public void lightsOn(){
    	mySensorImage[2] = true;
    }
    /**
     * 
     * turn the lights on the Kater off
     */
    public void lightsOff(){
    	mySensorImage[2] = false;
    }
    /**
     * add an event Listener
     * 
     * @param event
     */
	public void addActionListener(IKaterEventListener event) {
		listenerList.add(event);
		
	}
	/**
	 * remove an event Listener
	 * 
	 * @param event
	 */
	public void removeActionListener(IKaterEventListener event) {
    	listenerList.remove(event);
    }
  }
