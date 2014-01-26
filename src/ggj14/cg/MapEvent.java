package ggj14.cg;

import java.io.IOException;
import java.util.Scanner;

public abstract class MapEvent {
	
	public abstract void activate(TileMap tileMap);
	
	public static MapEvent inputEventFromFile(Scanner scanner) throws IOException {
		String line = scanner.nextLine();
		if(line.equalsIgnoreCase("TILE_FLIP_EVENT")) {
			int x = scanner.nextInt();
			int y = scanner.nextInt();
			TileMap tileMap = TileMap.inputMapFromFile(scanner);
			
			return new TileFlipEvent(x, y, tileMap);
		}
		
		System.out.println("Unknown map event:" + line);
		
		return null;
	}
}

class TileFlipEvent extends MapEvent {
	
	public int x;
	public int y;
	public TileMap tileMap;
	
	public TileFlipEvent(int x, int y, TileMap tileMap) {
		this.x = x;
		this.y = y;
		this.tileMap = tileMap;
	}
	
	public void activate(TileMap worldMap) {
		for(int yi = 0; yi < tileMap.getHeight(); yi++) {
			for(int xi = 0; xi < tileMap.getWidth(); xi++) {
				Tile temp = worldMap.getTile(x + xi, y + yi);
				worldMap.setTile(x + xi, y + yi, tileMap.getTile(xi, yi));
				tileMap.setTile(xi, yi, temp);
			}
		}
	}
}
