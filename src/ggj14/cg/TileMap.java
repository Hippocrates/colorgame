package ggj14.cg;

public class TileMap {
	
	private static Tile defaultTile = new Tile(0, ColorType.BLANK);
	
	private int width;
	private int height;
	private Tile[] tiles;
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}

	public TileMap(int _width, int _height) {
		width = _width;
		height = _height;
		tiles = new Tile[index2d(width, height)];
	}
	
	private int index2d(int x, int y) {
		return y*width + x;
	}
	
	public void setTile(int x, int y, int type, ColorType color) {
		setTile(x, y, new Tile(type, color));
	}
	
	public void setTile(int x, int y, Tile tile) {
		tiles[index2d(x, y)] = tile;
	}
	
	public Tile getTile(int x, int y) {
		Tile result = tiles[index2d(x, y)];
		if (result != null) {
			return result;
		}
		else {
			return defaultTile;
		}
	}
}
