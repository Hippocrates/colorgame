package ggj14.cg;

import java.io.IOException;
import java.util.Scanner;

//Flag file format:
//FLAG
//colour xpos ypos
//# events
//TILE_FLIP_EVENT
//xpos ypos width height
//[tile map]
//...

public class Flag implements Drawable {
	
	public static final int COLLISION_X_OFFSET = 3;
	public static final int COLLISION_WIDTH = 10;
	public static final int COLLISION_Y_OFFSET = 0;
	public static final int COLLISION_HEIGHT = 14;

	public static final int FLAG_X = GameScreen.FLAG_X;
	public static final int FLAG_Y = GameScreen.FLAG_Y;
	
	public Vector pos;
	public Vector pos2;
	public AABBox collision;
	public ColorType color;
	
	public boolean isActive = false;

	public int animX;
	public double animTimer;
	public final double flagAnimLength = 0.4;
	
	public MapEvent events[];
	
	public static Flag inputLeverFromFile(Scanner scanner) throws IOException {
		
		String line = scanner.nextLine();
		String[] tokens = line.split("\\s+");
		
		ColorType color = ColorType.fromCode(tokens[0].charAt(0));
		float x = Float.parseFloat(tokens[1]);
		float y = Float.parseFloat(tokens[2]);
		
		Flag flag = new Flag(x, y, color);
		
		int nEvents = scanner.nextInt();
		flag.events = new MapEvent[nEvents];
		
		scanner.nextLine();
		
		for(int i = 0; i < nEvents; i++) {
			flag.events[i] = MapEvent.inputEventFromFile(scanner);
		}
		
		return flag;
	}
	
	public Flag(float x, float y, ColorType color) {
		pos = new Vector(x, y);
		pos2 = new Vector(x + FLAG_X, y + FLAG_Y);
		collision = new AABBox(pos.x + COLLISION_X_OFFSET, pos.y + COLLISION_Y_OFFSET, pos.x + COLLISION_X_OFFSET + COLLISION_WIDTH, pos.y + COLLISION_Y_OFFSET + COLLISION_HEIGHT);
		this.color = color;
		events = new MapEvent[0];
	}
	
	public void activate(TileMap tileMap) {
		if(!isActive) {
			animTimer = 0;
			isActive = true;
			for(MapEvent event : events) {
				event.activate(tileMap);
			}
		}
	}
	
	public void update(double s) {
		if(isActive) {
			animTimer += s;
			if(animTimer > flagAnimLength) {
				animTimer -= flagAnimLength;
				animX++;
				if(animX >= 6) {
					animX = 2;
				}
			}
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
		return animX;
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
