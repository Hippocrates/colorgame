package ggj14.cg;

public class Tile {
	private int type;
	private ColorType color;
	
	public Tile(int _type, ColorType _color) {
		type = _type;
		color = _color;
	}
	
	public int getType() {
		return type;
	}
	
	public ColorType getColor() {
		return color;
	}
}
