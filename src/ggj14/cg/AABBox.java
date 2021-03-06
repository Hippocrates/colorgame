package ggj14.cg;

public class AABBox {
	
	public float minX;
	public float maxX;
	public float minY;
	public float maxY;
	
	public AABBox(float _minX, float _minY, float _maxX, float _maxY) {
		minX = _minX;
		minY = _minY;
		maxX = _maxX;
		maxY = _maxY;
	}
	
	public AABBox() {
		this(0.0f, 0.0f, 0.0f, 0.0f);
	}
	
	public Vector getMin()
	{
		return new Vector(minX, minY);
	}
	
	public Vector getMax()
	{
		return new Vector(maxX, maxY);
	}
	
	public boolean contains(Vector v) {
		return minX <= v.x && minY <= v.y && maxX >= v.x && maxY >= v.y;
	}
	
	public boolean overlaps(AABBox other) {
		return minX < other.maxX && minY < other.maxY && maxX > other.minX && maxY > other.minY;
	}
	
	public boolean contains(AABBox other) {
		return contains(other.getMin()) && contains(other.getMax());
	}
	
	public AABBox translated(Vector delta) {
		return new AABBox(minX + delta.x, minY + delta.y, maxX + delta.x, maxY + delta.y);
	}
	
	public Vector getCenter() {
		return new Vector((minX + maxX) / 2, (minY + maxY) / 2);
	}
	
	public void set(float _minX, float _minY, float _maxX, float _maxY) {
		minX = _minX;
		minY = _minY;
		maxX = _maxX;
		maxY = _maxY;
	}
}
