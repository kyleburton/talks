package com.example;

public class Point {
  private long x, y;
  public Point(long _x, long _y) { x = _x; y = _y; }
  public long getX()        { return x; }
  public long getY()        { return y; }
  public void setX(long _x) { x = _x; }
  public void setY(long _y) { y = _y; }
}



(defmacro when [test & body]
  `(if ~test
     (do
       ~@body)))
