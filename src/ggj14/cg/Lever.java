package ggj14.cg;

import java.io.IOException;
import java.util.Scanner;

// Lever file format:
// LEVER
// colour xpos ypos
// # events
// TILE_FLIP_EVENT
// xpos ypos width height
// [tile map]
// ...

public class Lever implements Drawable {
	
	public static final int COLLISION_X_OFFSET = 1;
	public static final int COLLISION_WIDTH = 14;
	public static final int COLLISION_Y_OFFSET = 0;
	public static final int COLLISION_HEIGHT = 8;

	public static final int LEVER_X = GameScreen.LEVER_X;
	public static final int LEVER_Y = GameScreen.LEVER_Y;
	
	public Vector pos;
	public Vector pos2;
	public AABBox collision;
	public ColorType color;
	public boolean flipped = false;
	
	public MapEvent events[];
	
	public static Lever inputLeverFromFile(Scanner scanner) throws IOException {
		
		String line = scanner.nextLine();
		String[] tokens = line.split("\\s+");
		
		ColorType color = ColorType.fromCode(tokens[0].charAt(0));
		float x = Float.parseFloat(tokens[1]);
		float y = Float.parseFloat(tokens[2]);
		
		Lever lever = new Lever(x, y, color);
		
		int nEvents = scanner.nextInt();
		lever.events = new MapEvent[nEvents];
		
		scanner.nextLine();
		
		for(int i = 0; i < nEvents; i++) {
			lever.events[i] = MapEvent.inputEventFromFile(scanner);
		}
		
		return lever;
	}
	
	public Lever(float x, float y, ColorType color) {
		pos = new Vector(x, y);
		pos2 = new Vector(x + LEVER_X, y + LEVER_Y);
		collision = new AABBox(pos.x + COLLISION_X_OFFSET, pos.y + COLLISION_Y_OFFSET, pos.x + COLLISION_X_OFFSET + COLLISION_WIDTH, pos.y + COLLISION_Y_OFFSET + COLLISION_HEIGHT);
		this.color = color;
	}
	
	public void activate(TileMap tileMap) {
		flipped = !flipped;
		for(MapEvent event : events) {
			event.activate(tileMap);
		}
	}
	
	public AABBox getCollisionBox() {
		return collision;
	}
	
	public Vector getPos() {
		return pos;
	}
	
	public Vector getPos2() {
		return pos2;
	}

	public int getTileX() {
		if(flipped) {return 2;}
		return 0;
	}

	public int getTileY() {
		return 0;
	}

	public ColorType getColor() {
		return color;
	}

	public boolean drawFlipped() {
		return false;
	}
}
