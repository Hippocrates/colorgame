package ggj14.cg;

public class TileMap {
	
	private static Tile defaultTile = new Tile(0, 0, ColorType.BLANK);
	
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
	
	public void setTile(int x, int y, int tileX, int tileY, ColorType color) {
		setTile(x, y, new Tile(tileX, tileY, color));
	}
	
	public void setTile(int x, int y, Tile tile) {
		tiles[index2d(x, y)] = tile;
	}
	
	public Tile getTile(int x, int y) {
		if (x < 0 || x >= width || y < 0 || y >= height)
			return defaultTile;
		
		Tile result = tiles[index2d(x, y)];
		if (result != null) {
			return result;
		}
		else {
			return defaultTile;
		}
	}
	
	public TileRange getTilesOverlapping(AABBox box, int tileX, int tileY)
	{
		int left = (int) Math.max(Math.floor(box.minX / tileX), 0);
		int bottom = (int) Math.max(Math.floor(box.minY / tileY), 0);
		int right = (int) Math.min(Math.ceil(box.maxX / tileX) - 1, width - 1);
		int top = (int) Math.min(Math.ceil(box.maxY / tileY) - 1, height - 1);
		
		return new TileRange(left, bottom, right, top);
	}
}
