package ggj14.cg;

public class Tile implements Drawable {
	private Vector pos;
	private Vector pos2;
	private int tileX;
	private int tileY;
	private ColorType color;
	
	public Tile(int _tileX, int _tileY, ColorType _color) {
		tileX = _tileX;
		tileY = _tileY;
		color = _color;
	}
	
	public Vector getBottomLeft() {
		return pos;
	}
	
	public Vector getTopRight() {
		return pos2;
	}
	
	public int getTileX() {
		return tileX;
	}
	
	public int getTileY() {
		return tileY;
	}
	
	public ColorType getColor() {
		return color;
	}
	
	public boolean drawFlipped() {
		return false;
	}
}
