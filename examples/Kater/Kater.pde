import katerlib.*;
import TUIO.*;
import proxml.*;

KaterKontrol katerkontrol;
TuioProcessing tuioClient;
int[] katerIDs = {8,9};

void setup() {
  size(400,400);
  tuioClient= new TuioProcessing(this);
  katerkontrol = new KaterKontrol(this, tuioClient, katerIDs);
}

void draw() {
  background(0);
}
