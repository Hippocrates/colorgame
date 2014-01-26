package ggj14.cg;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;

public class GameScreen extends GameState {
	
	public static final int TILE_X = 16;
	public static final int TILE_Y = 16;

	public static final int PLR_X = 16;
	public static final int PLR_Y = 16;

	public static final int LEVER_X = 16;
	public static final int LEVER_Y = 16;
	
	public String level = "res/map/testlever.map";
	
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
			
			originalImage = ImageIO.read(new File("res/img/lever.png"));
			leverSets = new SpriteSheet[ColorType.size()];
			for (ColorType c : ColorType.values()) {
				leverSets[c.ordinal()] = new SpriteSheet(ImageOps.makeColouredImage(originalImage, c.getColor()), LEVER_X, LEVER_Y);
			}

			loadMap(level);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	
	public SpriteSheet tileSets[];
	public SpriteSheet playerSets[];
	public SpriteSheet leverSets[];
	public TileMap tileMap;
	public Camera camera;
	public Player plr1;
	public Player plr2;
	
	public ArrayList<Lever> levers;
	
	//keep these cached so that if down is held, we don't keep checking for lever press
	private boolean p1lever = false;
	private boolean p2lever = false;
	
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
		case KEY1_DOWN:
			if(!p1lever) {
				p1lever = true;
				checkLeverPress(plr1);
			}
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
		case KEY2_DOWN:
			if(!p2lever) {
				p2lever = true;
				checkLeverPress(plr2);
			}
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
		case KEY1_DOWN:
			p1lever = false;
			break;
			
		case KEY2_LEFT:
			plr2.walkStopped(false);
			break;
		case KEY2_RIGHT:
			plr2.walkStopped(true);
			break;
		case KEY2_DOWN:
			p2lever = false;
			break;
		}
	}
	
	private void checkLeverPress(Player plr) {
		AABBox plrBox = plr.getCollisionBox();
		
		for(Lever lever : levers) {
			if(plr.getColor().getCollisionType(lever.color) == CollisionType.SOLID &&
					plrBox.overlaps(lever.getCollisionBox())) {
				lever.activate(tileMap);
			}
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
				loadMap(level);
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
		
		
		Vector pos = new Vector(0, 0);
		Vector pos2 = new Vector(0, 0);
		
		for (int y = range.bottom; y <= range.top; ++y) {
			for (int x = range.left; x <= range.right; ++x) {

				Tile tile = tileMap.getTile(x, y);
				pos.set(x * TILE_X, y * TILE_Y);
				pos2.set((x + 1) * TILE_X, (y + 1) * TILE_Y);
				
				cameraDraw(tileSets, tile, g2d, pos, pos2);
			}
		}

		cameraDraw(playerSets, plr1, g2d, plr1.getPos(), plr1.getPos2());
		cameraDraw(playerSets, plr2, g2d, plr2.getPos(), plr2.getPos2());
		
		for(Lever lever : levers) {
			cameraDraw(leverSets, lever, g2d, lever.getPos(), lever.getPos2());
		}
		
	}
	
	private void cameraDraw(SpriteSheet[] sheet, Drawable draw, Graphics2D g, Vector pos, Vector pos2) {
		Vector bottomLeft = camera.viewToScreen(pos);
		Vector topRight = camera.viewToScreen(pos2);
		BufferedImage image = sheet[draw.getColor().ordinal()].getImage(draw.getTileX(), draw.getTileY());
		if(draw.drawFlipped()) {
			g.drawImage(image, (int)topRight.x, (int)topRight.y, (int)bottomLeft.x, (int)bottomLeft.y, 0, 0, image.getWidth(), image.getHeight(), null);
		} else {
			g.drawImage(image, (int)bottomLeft.x, (int)topRight.y, (int)topRight.x, (int)bottomLeft.y, 0, 0, image.getWidth(), image.getHeight(), null);
		}
	}
	
	public void loadMap(String filename) throws IOException {
		
		levers = new ArrayList<Lever>();
		
		Scanner scanner = new Scanner(new FileReader(filename));
		
		try {
		
			tileMap = TileMap.inputMapFromFile(scanner);
			
			while (scanner.hasNextLine()) {
				String createLine = scanner.nextLine();
				System.out.println(createLine);
				parseMapLine(createLine.split("\\s+"), scanner);
			}
		}
		finally {
			scanner.close();
		}
	}
	
	private void parseMapLine(String[] tokens, Scanner scanner) throws IOException {
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
		else if (command.equalsIgnoreCase("LEVER")) {
			levers.add(Lever.inputLeverFromFile(scanner));
		}
	}
}
