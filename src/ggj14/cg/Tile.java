package ggj14.cg;

public class Tile {
	private int tileX;
	private int tileY;
	private ColorType color;
	
	public Tile(int _tileX, int _tileY, ColorType _color) {
		tileX = _tileX;
		tileY = _tileY;
		color = _color;
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
}
