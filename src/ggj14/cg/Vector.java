package ggj14.cg;

public class Vector {
	
	public Vector(float _x, float _y) {
		x = _x;
		y = _y;
	}
	
	public float x;
	public float y;
	
	public Vector add(Vector other) {
		return new Vector(x + other.x, y + other.y);
	}
	
	public Vector sub(Vector other) {
		return new Vector(x - other.x, y - other.y);
	}
	
	public Vector mul(float a) {
		return new Vector(x * a, y * a);
	}
	
	public float lengthSq() {
		return x*x + y*y;
	}
	
	public String toString()
	{
		return "[" + x + "," + y + "]";
	}
}
