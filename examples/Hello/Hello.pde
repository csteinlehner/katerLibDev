import template.*;
HelloWorld hello;

void setup() {
  size(400,400);
  hello = new HelloWorld(this);
  println(hello.getVariable());
}

void draw() {
  background(0);
}