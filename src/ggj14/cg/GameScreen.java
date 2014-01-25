package ggj14.cg;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import javax.imageio.ImageIO;

public class GameScreen extends GameState {
	
	public static final int TILE_X = 16;
	public static final int TILE_Y = 16;

	public static final int PLR_X = 16;
	public static final int PLR_Y = 16;
	
	public GameScreen()
	{
		try {
			BufferedImage originalImage = ImageIO.read(new File("res/img/tilesheet.png"));
			System.out.println(originalImage.getType());
			tileSets = new SpriteSheet[ColorType.size()];
			for (ColorType c : ColorType.values()) {
				tileSets[c.ordinal()] = new SpriteSheet(ImageOps.makeColouredImage(originalImage, c.getColor()), TILE_X, TILE_Y);
			}
			
			originalImage = ImageIO.read(new File("res/img/playeranim.png"));
			playerSets = new SpriteSheet[ColorType.size()];
			for (ColorType c : ColorType.values()) {
				playerSets[c.ordinal()] = new SpriteSheet(ImageOps.makeColouredImage(originalImage, c.getColor()), PLR_X, PLR_Y);
			}

			loadMap("res/map/testout.map");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	
	public SpriteSheet tileSets[];
	public SpriteSheet playerSets[];
	public TileMap tileMap;
	public Camera camera;
	public Player plr1;
	public Player plr2;
	
	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()) {
		case KEY1_LEFT:
			plr1.walkPressed(false);
			break;
		case KEY1_RIGHT:
			plr1.walkPressed(true);
			break;
		case KEY1_UP:
			plr1.jumpPressed();
			break;
		case KEY2_LEFT:
			plr2.walkPressed(false);
			break;
		case KEY2_RIGHT:
			plr2.walkPressed(true);
			break;
		case KEY2_UP:
			plr2.jumpPressed();
			break;
			
		case KEY1_BACK:
			closeState();
		}
	}
	
	public void keyReleased(KeyEvent e) {
		switch(e.getKeyCode()) {
		case KEY1_LEFT:
			plr1.walkStopped(false);
			break;
		case KEY1_RIGHT:
			plr1.walkStopped(true);
			break;
		case KEY2_LEFT:
			plr2.walkStopped(false);
			break;
		case KEY2_RIGHT:
			plr2.walkStopped(true);
			break;
		}
	}
	
	public void update(double s)
	{
		plr1.update(s, tileMap, camera);

		plr2.update(s, tileMap, camera);
		
		// re-start level
		if (plr1.isDead || plr2.isDead) {
			try
			{
				loadMap("res/map/testout.map");
			}
			catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		
		scrollCamera();
	}
	
	public void scrollCamera() {
		Vector plr1CamTarget = plr1.getCenter();
		Vector plr2CamTarget = plr2.getCenter();
		
		Vector cameraTarget = plr1CamTarget.add(plr2CamTarget).mul(0.5f).sub(camera.viewSize.mul(0.5f));
		
		Vector cameraDelta = cameraTarget.sub(camera.position);
		
		AABBox plr1Box = plr1.getCollisionBox();
		AABBox plr2Box = plr2.getCollisionBox();
		
		float tileMapSize = tileMap.getWidth() * TILE_X;
		
		float hardConstraintLeft = 0.0f;
		float hardConstraintRight = tileMapSize - camera.viewSize.x;
		
		if (tileMapSize < camera.viewSize.x) {
			hardConstraintLeft = (tileMapSize / 2.0f) - (camera.viewSize.x / 2.0f);
			hardConstraintRight = (tileMapSize / 2.0f) + (camera.viewSize.x / 2.0f);
		}
		
		hardConstraintLeft = Math.max(hardConstraintLeft, Math.max(plr1Box.maxX, plr2Box.maxX) - camera.viewSize.x);
		hardConstraintRight = Math.min(hardConstraintRight, Math.min(plr1Box.minX, plr2Box.minX));
		
		float hardConstraintBottom = Math.max(0.0f, Math.max(plr1Box.maxY, plr2Box.maxY) - camera.viewSize.y); 
		float hardConstraintTop =  Math.min(plr1Box.minY, plr2Box.minY);
		
		if (Math.abs(cameraDelta.x) > 48) {
			cameraDelta.x *= 48 / Math.abs(cameraDelta.x);
		}
		
		camera.position.x += cameraDelta.x;

		if (Math.abs(cameraDelta.y) > 128) {
			cameraDelta.y *= 128 / Math.abs(cameraDelta.y);
		}
			
		camera.position.y += cameraDelta.y;	
		
		if (camera.position.x < hardConstraintLeft) {
			camera.position.x = hardConstraintLeft;
		}
		else if (camera.position.x > hardConstraintRight) {
			camera.position.x = hardConstraintRight;
		}
		
		if (camera.position.y < hardConstraintBottom) {
			camera.position.y = hardConstraintBottom;
		}
		else if (camera.position.y > hardConstraintTop) {
			camera.position.y = hardConstraintTop;
		}
	}
	
	public void draw(Graphics g, int width, int height) {
		
		Graphics2D g2d = (Graphics2D) g;
		g2d.scale(1, -1);
		g2d.translate(0, -height);
		
		camera.screenSize.x = width;
		camera.screenSize.y = height;
		
		AABBox cameraBounds = camera.getViewBounds();
		
		TileRange range = tileMap.getTilesOverlapping(cameraBounds, TILE_X, TILE_Y);
		
		
		Vector bottomLeft;
		Vector topRight;
		BufferedImage image;
		
		for (int y = range.bottom; y <= range.top; ++y) {
			for (int x = range.left; x <= range.right; ++x) {

				Tile tile = tileMap.getTile(x, y);
				int ordinal = tile.getColor().ordinal();
				
				// Two vectors created per tile every frame?
				bottomLeft = camera.viewToScreen(new Vector(x * TILE_X, y * TILE_Y));
				topRight = camera.viewToScreen(new Vector((x + 1) * TILE_X, (y + 1) * TILE_Y));
				
				image = tileSets[ordinal].getImage(tile.getTileX(), tile.getTileY());
				
				g2d.drawImage(image, (int)bottomLeft.x, (int)topRight.y, (int)topRight.x, (int)bottomLeft.y, 0, 0, TILE_X, TILE_Y, null);
			}
		}

		bottomLeft = camera.viewToScreen(plr1.pos);
		topRight = camera.viewToScreen(new Vector(plr1.pos.x + PLR_X, plr1.pos.y + PLR_Y));
		image = playerSets[plr1.getColor().ordinal()].getImage(plr1.getAnimX(), plr1.getAnimY());
		if(plr1.facingRight) {
			g2d.drawImage(image, (int)bottomLeft.x, (int)topRight.y, (int)topRight.x, (int)bottomLeft.y, 0, 0, TILE_X, TILE_Y, null);
		} else {
			g2d.drawImage(image, (int)topRight.x, (int)topRight.y, (int)bottomLeft.x, (int)bottomLeft.y, 0, 0, TILE_X, TILE_Y, null);
		}

		bottomLeft = camera.viewToScreen(plr2.pos);
		topRight = camera.viewToScreen(new Vector(plr2.pos.x + PLR_X, plr2.pos.y + PLR_Y));
		image = playerSets[plr2.getColor().ordinal()].getImage(plr2.getAnimX(), plr2.getAnimY());
		if(plr2.facingRight) {
			g2d.drawImage(image, (int)bottomLeft.x, (int)topRight.y, (int)topRight.x, (int)bottomLeft.y, 0, 0, TILE_X, TILE_Y, null);
		} else {
			g2d.drawImage(image, (int)topRight.x, (int)topRight.y, (int)bottomLeft.x, (int)bottomLeft.y, 0, 0, TILE_X, TILE_Y, null);
		}
		
	}
	
	public void loadMap(String filename) throws IOException {
		
		Scanner scanner = new Scanner(new FileReader(filename));
		
		try {
			int width = scanner.nextInt();
			int height = scanner.nextInt();
			
			// read to EOL first
			scanner.nextLine();
			
			tileMap = new TileMap(width, height);
			
			for (int y = height - 1; y >= 0; --y) {
				String line = scanner.nextLine();
				
				System.out.println(line.length());
				
				if (!(line.length() == width*3)) {
					throw new RuntimeException("Error, tile map line not right size");
				}
				
				for (int x = 0; x < width; ++x) {
					char code = line.charAt(x*3);
					int tileY = line.charAt(x*3 + 1) - 0x30;
					int tileX = line.charAt(x*3 + 2) - 0x30;
					tileMap.setTile(x,  y,  tileX, tileY, ColorType.fromCode(code));
				}
			}
			
			while (scanner.hasNextLine()) {
				String createLine = scanner.nextLine();
				System.out.println(createLine);
				parseMapLine(createLine.split("\\s+"));
			}
		}
		finally {
			scanner.close();
		}
	}
	
	private void parseMapLine(String[] tokens) {
		String command = tokens[0];
		
		if (command == "REMARK") {
			// do nothing
		}
		else if (command.equalsIgnoreCase("CAMERA")) {
			float cameraX = Float.parseFloat(tokens[1]);
			float cameraY = Float.parseFloat(tokens[2]);
			float viewX = Float.parseFloat(tokens[3]);
			float viewY = Float.parseFloat(tokens[4]);
			camera = new Camera(new Vector(cameraX, cameraY), new Vector(viewX, viewY), new Vector(GameWindow.WIDTH, GameWindow.HEIGHT));
		}
		else if (command.equalsIgnoreCase("PLAYER1") || command.equalsIgnoreCase("PLAYER2")) {
			int whichPlayer = command.charAt("PLAYER".length()) - 0x30;
			ColorType color = ColorType.fromCode(tokens[1].charAt(0));
			float playerX = Float.parseFloat(tokens[2]);
			float playerY = Float.parseFloat(tokens[3]);
			if (whichPlayer == 1)
			{
				plr1 = new Player(playerX, playerY, color);
			}
			else {
				plr2 = new Player(playerX, playerY, color);
			}
		}
	}
}
