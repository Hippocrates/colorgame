package ggj14.cg.editor;

import ggj14.cg.ColorType;
import ggj14.cg.GameScreen;
import ggj14.cg.ImageOps;
import ggj14.cg.SpritePanel;
import ggj14.cg.SpriteSheet;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class EditorMainWindow extends JFrame implements KeyListener, MouseListener, MouseMotionListener {

	//width and height of the screen
	public static final int WIDTH = 640;
	public static final int HEIGHT = 480;
	
	//the size of each image
	public static final int TILE_X = GameScreen.TILE_X;
	public static final int TILE_Y = GameScreen.TILE_Y;

	public static final int PLR_X = GameScreen.PLR_X;
	public static final int PLR_Y = GameScreen.PLR_Y;

	public static final int FLAG_X = GameScreen.FLAG_X;
	public static final int FLAG_Y = GameScreen.FLAG_Y;
	
	//some key constants
	public static final int KEY_LEFT = KeyEvent.VK_LEFT;
	public static final int KEY_RIGHT = KeyEvent.VK_RIGHT;
	public static final int KEY_UP = KeyEvent.VK_UP;
	public static final int KEY_DOWN = KeyEvent.VK_DOWN;
	public static final int KEY_DEL = KeyEvent.VK_DELETE;
	
	public static final int SCROLL_SPEED = 16;

	private SpritePanel panel;
	public Insets insets;

	//a number of images
	public static SpriteSheet tileSets[];
	public static SpriteSheet toolSets;

	//block data and dimensions
	public int xDim = 20;
	public int yDim = 20;
	private int blocks[][];
	private ColorType colors[][];
	public static final int MIN_DIM = 10;
	public static final int MAX_DIM = 100;

	private int xShift = 0;
	private int yShift = 0;
	private int maxXShift;
	private int maxYShift;
	
	//editor objects
	private EditorPlayer plr1;
	private EditorPlayer plr2;
	
	private LinkedList<Selectable> selectables;
	private Selectable selected = null;
	
	//mouse location for dragging
	private int mouseX = 0;
	private int mouseY = 0;
	
	private EditorOptionsWindow options;
	private EditorPaletteWindow palette;
	
	static {
		try {
			BufferedImage originalImage = ImageIO.read(new File("res/img/tilesheet.png"));
			System.out.println(originalImage.getType());
			tileSets = new SpriteSheet[ColorType.size()];
			for (ColorType c : ColorType.values()) {
				tileSets[c.ordinal()] = new SpriteSheet(ImageOps.makeColouredImage(originalImage, c.getColor()), TILE_X, TILE_Y);
			}
			
			originalImage = ImageIO.read(new File("res/img/editor/tools.png"));
			toolSets = new SpriteSheet(originalImage, PLR_X, PLR_Y);
			
			//load other images
			
		} catch (Exception e) {
			System.err.println("Could not open image files!");
			System.exit(1);
		}
	}

	public EditorMainWindow() {
		super("Colour Game Level Editor");

		//create the input listeners
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		
		//set up the data arrays
		//make them all max size so we don't have to redim arrays later
		blocks = new int[MAX_DIM][MAX_DIM];
		colors = new ColorType[MAX_DIM][MAX_DIM];

		plr1 = new EditorPlayer(16, 16, ColorType.RED, this);
		plr2 = new EditorPlayer(32, 16, ColorType.GREEN, this);

		clearData();
		
		//set max scrolling
		calcMaxScroll();

		//create the window, add a drawing panel
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		panel = new SpritePanel(WIDTH, HEIGHT);
		getContentPane().add(panel, BorderLayout.CENTER);

		setResizable(false);
		pack(); //this is necessary to get insets before showing
		insets = getInsets();
		setSize(WIDTH + insets.left + insets.right, HEIGHT + insets.top + insets.bottom);

		palette = new EditorPaletteWindow(this);
		palette.setVisible(true);
		palette.setLocation(WIDTH + 20, 5);
		options = new EditorOptionsWindow(this);
		options.getFrame().setVisible(true);
		options.getFrame().setLocation(WIDTH + 20, EditorPaletteWindow.HEIGHT + 40);
		
		setVisible(true);
		setLocation(5, 5);
	}

	//Simplified drawing used here
	public void paint(Graphics g) {
		super.paint(g);

		panel.cls();
		Graphics g2 = panel.getGraphics();
		
		//Draw the blocks
		for(int yi = yShift / TILE_Y; yi <= (HEIGHT + yShift) / TILE_Y; yi++) {
			int yi2 = yDim - 1 - yi;
			for(int xi = xShift / TILE_X; xi <= (WIDTH + xShift) / TILE_X; xi++) {
				if(xi < blocks[0].length && yi2 < blocks.length && yi2 >= 0) {
					panel.drawImage(tileSets[colors[xi][yi2].ordinal()].getImage(blocks[xi][yi2]),
							xi * TILE_X - xShift, yi * TILE_Y - yShift);
				}
			}
		}
		
		//draw each selectable
		for(Selectable s : selectables) {
			s.draw(g2, xShift, yShift);
		}
		
		//Draw the grid on top of everything if enabled
		if(options.getShowGrid()) {
			g2.setColor(Color.WHITE);
			
			for(int i = TILE_X - (xShift % TILE_X); i < WIDTH; i += TILE_X) {
				g2.drawLine(i, 0, i, HEIGHT);
			}

			for(int i = TILE_Y - (yShift % TILE_Y); i < HEIGHT; i += TILE_Y) {
				g2.drawLine(0, i, WIDTH, i);
			}
		}
		
		//Draw the selected object
		if(selected != null) {
			selected.drawSelect(g2, xShift, yShift);
		}
		
		panel.flip();
		panel.repaint();
	}
	
	public void redimension(int newDimX, int newDimY) {
		//confine dimensions between 10 and 50
		if(newDimX < MIN_DIM) {newDimX = MIN_DIM;}
		if(newDimY < MIN_DIM) {newDimY = MIN_DIM;}
		if(newDimX > MAX_DIM) {newDimX = MAX_DIM;}
		if(newDimY > MAX_DIM) {newDimY = MAX_DIM;}
		
		//if we are trying to redimension to the same thing, do nothing
		if(newDimX == xDim && newDimY == yDim) {return;}
		
		xDim = newDimX;
		yDim = newDimY;

		calcMaxScroll();
		
		//also make sure hero is on map
		if(plr1.x > TILE_X * (xDim - 2)) {plr1.x = TILE_X * (xDim - 2);}
		if(plr1.y > TILE_Y * (yDim - 2)) {plr1.y = TILE_Y * (yDim - 2);}

		if(plr2.x > TILE_X * (xDim - 2)) {plr2.x = TILE_X * (xDim - 2);}
		if(plr2.y > TILE_Y * (yDim - 2)) {plr2.y = TILE_Y * (yDim - 2);}
		
		repaint();
	}
	
	private void calcMaxScroll() {
		maxXShift = xDim * TILE_X - WIDTH;
		maxYShift = yDim * TILE_Y - HEIGHT;
		if(maxXShift < 0) {maxXShift = 0;}
		if(maxYShift < 0) {maxYShift = 0;}
		
		//make sure the scroll is within acceptable limits
		if(xShift > maxXShift) {xShift = maxXShift;}
		if(yShift > maxYShift) {yShift = maxYShift;}
		if(xShift < 0) {xShift = 0;}
		if(yShift < 0) {yShift = 0;}
	}
	
	public void loadLevel(String level) throws IOException {
		//TODO: confirmation message for this, save, and reset
		clearData();
		
		Scanner scanner = new Scanner(new FileReader(level));
		
		try {
			xDim = scanner.nextInt();
			yDim = scanner.nextInt();
			
			options.setDim(xDim, yDim);
			calcMaxScroll();
			
			// read to EOL first
			scanner.nextLine();
			
			for (int y = yDim - 1; y >= 0; --y) {
				String line = scanner.nextLine();
				
				if (!(line.length() == xDim*3)) {
					throw new RuntimeException("Error, tile map line not right size");
				}
				
				for (int x = 0; x < xDim; ++x) {
					char code = line.charAt(x*3);
					int tileY = line.charAt(x*3 + 1) - 0x30;
					int tileX = line.charAt(x*3 + 2) - 0x30;
					blocks[x][y] = tileY * tileSets[0].getNumTilesX() + tileX;
					colors[x][y] = ColorType.fromCode(code);
				}
			}
			
			while (scanner.hasNextLine()) {
				String createLine = scanner.nextLine();
				System.out.println(createLine);
				String[] tokens = createLine.split("\\s+");
				
				String command = tokens[0];
				
				if (command == "REMARK") {
				}
				else if (command.equalsIgnoreCase("CAMERA")) {
				}
				else if (command.equalsIgnoreCase("PLAYER1") || command.equalsIgnoreCase("PLAYER2")) {
					int whichPlayer = command.charAt("PLAYER".length()) - 0x30;
					ColorType color = ColorType.fromCode(tokens[1].charAt(0));
					int playerX = (int) Float.parseFloat(tokens[2]);
					int playerY = (int) Float.parseFloat(tokens[3]);
					if (whichPlayer == 1)
					{
						plr1.set(playerX, playerY, color);
					}
					else {
						plr2.set(playerX, playerY, color);
					}
				}
				else if (command.equalsIgnoreCase("FLAG")) {

					tokens = scanner.nextLine().split("\\s+");
					
					ColorType color = ColorType.fromCode(tokens[0].charAt(0));
					int x = (int) Float.parseFloat(tokens[1]);
					int y = (int) Float.parseFloat(tokens[2]);
					
					EditorFlag flag = new EditorFlag(x, y, color, this);
					selectables.add(flag);
				}
			}
		}
		finally {
			scanner.close();
		}
		
		repaint();
	}
	
	// For now, we'll just use 0 0 400 300 as the camera.
	public void saveLevel(String level) throws IOException {
		
		try {
			PrintWriter writer = new PrintWriter(new FileOutputStream(level));
			
			//output level dimensions
			writer.println(xDim + " " + yDim);
			
			//write the level data
			for (int y = yDim - 1; y >= 0; --y) {
				StringBuilder line = new StringBuilder();
				
				for (int x = 0; x < xDim; ++x) {
					char tileY = (char) (blocks[x][y] / tileSets[0].getNumTilesX() + 0x30);
					char tileX = (char) (blocks[x][y] % tileSets[0].getNumTilesX() + 0x30);

					line.append(colors[x][y].getCode());
					line.append(tileY);
					line.append(tileX);
				}
				writer.println(line.toString());
			}
			
			//Write default camera
			writer.println("CAMERA 0 0 400 300");
			
			//Output hero location
			writer.println("PLAYER1 " + plr1.color.getCode() + " " + plr1.x + " " + plr1.y);
			writer.println("PLAYER2 " + plr2.color.getCode() + " " + plr2.x + " " + plr2.y);
			
			//Output everything else
			for(Selectable s : selectables) {
				if(!(s instanceof EditorPlayer)) {
					writer.println(s.toOutputString());
				}
			}
			
			writer.close();
			
		} catch(Exception e) {
			
		}
	}
	
	public void resetLevel() {

		clearData();
		repaint();
	}
	
	//a separate function for doing the clear itself to separate out the warning messages
	private void clearData() {
		
		//clear blocks
		for(int yi = 0; yi < MAX_DIM; yi++) {
			for(int xi = 0; xi < MAX_DIM; xi++) {
				blocks[yi][xi] = 0;
				colors[yi][xi] = ColorType.BLANK;
			}
		}

		//clear list
		selectables = new LinkedList<Selectable>();
		selectables.add(plr1);
		selectables.add(plr2);
		selected = null;

		plr1.x = 16;
		plr1.y = 16;

		plr2.x = 32;
		plr2.y = 16;
	}

	@Override
	public void mousePressed(MouseEvent e) {

		mouseX = e.getX() - insets.left;
		mouseY = e.getY() - insets.top;
		
		int cat = palette.getMouseCat();
		int index = palette.getMouseIndex();
		
		if(SwingUtilities.isRightMouseButton(e)) {
			//if this is a right click, do nothing
		} else if(cat == 0) { //block palette
			placeBlock(e);
		} else if(cat == 1 && index == 0) { //hero tool
			plr1.mouseDragged(e, xShift, yShift);
		} else if(cat == 1 && index == 1) { //hand tool
			
			//see if an object was clicked on
			selected = null;
			
			for(Selectable p : selectables) {
				Rectangle rect = p.getRect(xShift,  yShift);
				if(rect.contains(mouseX, mouseY)) {
					selected = p;
					break;
				}
			}
			
			//remove ghosts, if selected is a waypoint or enemy, it will re-add waypoints
			removeGhosts();
			
			//tell the selected to add ghost objects
			if(selected != null) {
				selected.addGhosts(selectables);
			}
			
			repaint();
		} else if(cat == 1 && index == 2) { //delete tool
			deleteObjects(e);
		} else if(cat == 1 && index == 3) { //paint tool
			colorBlock(e);
		} else if(cat == 1 && index == 5) { //flag tool
			EditorFlag flag = new EditorFlag(0, 0, palette.getPaintIndex(), this);
			selectables.add(flag);
			selected = flag;
			flag.mouseDragged(e, xShift, yShift);
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {

		int cat = palette.getMouseCat();
		int index = palette.getMouseIndex();
		
		if(SwingUtilities.isRightMouseButton(e)) {
			scrollScreen(e);
		} else if(cat == 0) {
			placeBlock(e);
		} else if(cat == 1 && index == 0) {
			plr1.mouseDragged(e, xShift, yShift);
		} else if((cat == 1 && index == 1)) { //hand tool
			
			//if we have a selected, drag it around
			if(selected != null) {
				selected.mouseDragged(e, xShift, yShift);
				//otherwise move the screen
			} else {
				scrollScreen(e);
			}
		} else if(cat == 1 && index == 2) {
			deleteObjects(e);
		} else if(cat == 1 && index == 3) { //paint tool
			colorBlock(e);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
		int cat = palette.getMouseCat();
		int index = palette.getMouseIndex();

		int mouseX2 = e.getX() - insets.left;
		int mouseY2 = e.getY() - insets.top;
		
		//releasing the mouse only does something on the rectangle tool
		if(cat == 1 && index == 4) {
			//editor.yDim * PLR_Y - (e.getY() - editor.insets.top) + yShift
			int x1 = (mouseX + xShift) / TILE_X;
			int y1 = yDim - 1 - (mouseY + yShift) / TILE_Y;
			int x2 = (mouseX2 + xShift) / TILE_X;
			int y2 = yDim - 1 - (mouseY2 + yShift) / TILE_Y;
			
			if(x1 > x2) {
				int temp = x1;
				x1 = x2;
				x2 = temp;
			}
			
			if(y1 > y2) {
				int temp = y1;
				y1 = y2;
				y2 = temp;
			}
			
			if(x1 == x2 && y1 == y2) {
				setTile(x1, y1, 0, 0, palette.getPaintIndex());
				
			} else if(x1 == x2) {
				
				setTile(x1, y1, 4, 2, palette.getPaintIndex());
				setTile(x1, y2, 4, 0, palette.getPaintIndex());
				
				for(int yi = y1 + 1; yi < y2; yi++) {
					setTile(x1, yi, 4, 1, palette.getPaintIndex());
				}
				
			} else if(y1 == y2) {
				
				setTile(x1, y1, 1, 0, palette.getPaintIndex());
				setTile(x2, y1, 3, 0, palette.getPaintIndex());
				
				for(int xi = x1 + 1; xi < x2; xi++) {
					setTile(xi, y1, 2, 0, palette.getPaintIndex());
				}
				
			} else {

				setTile(x1, y1, 5, 2, palette.getPaintIndex());
				setTile(x2, y1, 7, 2, palette.getPaintIndex());
				setTile(x1, y2, 5, 0, palette.getPaintIndex());
				setTile(x2, y2, 7, 0, palette.getPaintIndex());

				for(int xi = x1 + 1; xi < x2; xi++) {
					setTile(xi, y1, 6, 2, palette.getPaintIndex());
					setTile(xi, y2, 6, 0, palette.getPaintIndex());
				}
				
				for(int yi = y1 + 1; yi < y2; yi++) {
					setTile(x1, yi, 5, 1, palette.getPaintIndex());
					setTile(x2, yi, 7, 1, palette.getPaintIndex());
				}
				
			}
			
			repaint();
		}
	}
	
	private void setTile(int x, int y, int tileX, int tileY, ColorType c) {
		if(x >= 0 && x < xDim && y >= 0 && y < yDim) {
			blocks[x][y] = tileY * tileSets[0].getNumTilesX() + tileX;
			colors[x][y] = c;
		}
	}
	
	//scrolls the level around
	private void scrollScreen(MouseEvent e) {
		
		int newX = e.getX() - insets.left;
		int newY = e.getY() - insets.top;
		
		//change shift amount, check bounds, then repaint
		xShift -= (newX - mouseX); //scale this to drag faster
		yShift -= (newY - mouseY);
		calcMaxScroll();
		repaint();

		//assign these at the end se we can remeber for next time where mouse is
		mouseX = newX;
		mouseY = newY;
	}
	
	//removes all ghosts from the selectable list
	private void removeGhosts() {
		
		Selectable p;
		for(Iterator<Selectable> i = selectables.iterator(); i.hasNext(); ) {
			p = i.next();
			if(p.isGhost()) {
				i.remove();
			}
		}
	}

	//called by mousePressed or mouseDragged if palette is on block place
	private void placeBlock(MouseEvent e) {

		int x = e.getX() - insets.left + xShift;
		int y = e.getY() - insets.top + yShift;

		//if a block is selected, place a block
		if(palette.getMouseCat() == 0) {
			
			int xi = x / TILE_X;
			int yi = y / TILE_Y;
			if(0 <= xi && xi < xDim && 0 <= yi && yi < yDim) {
				int yi2 = yDim - 1 - yi;
				if(blocks[xi][yi2] != palette.getMouseIndex() || colors[xi][yi2] != palette.getPaintIndex()) {
					blocks[xi][yi2] = palette.getMouseIndex();
					colors[xi][yi2] = palette.getPaintIndex();
					repaint();
				}
			}
		}
	}

	//called by mousePressed or mouseDragged if palette is on block place
	private void colorBlock(MouseEvent e) {

		int x = e.getX() - insets.left + xShift;
		int y = e.getY() - insets.top + yShift;

		//if a block is selected, place a block
		if(palette.getMouseCat() == 0) {
			
			int xi = x / TILE_X;
			int yi = y / TILE_Y;
			if(0 <= xi && xi < xDim && 0 <= yi && yi < yDim) {
				int yi2 = blocks.length - 1 - yi;
				if(colors[yi2][xi] != palette.getPaintIndex()) {
					colors[yi2][xi] = palette.getPaintIndex();
					repaint();
				}
			}
		}
	}
	
	//called if the delete tool is selected, removes pickups and enemies, and birds' waypoints
	private void deleteObjects(MouseEvent e) {
		
		int x = e.getX() - insets.left;
		int y = e.getY() - insets.top;
		Selectable s;
		boolean modified = false; //set to true if something was deleted; if so, repaint
		
		//loop through each selectable and check
		for(Iterator<Selectable> i = selectables.iterator(); i.hasNext(); ) {
			s = i.next();
			if(s.getRect(xShift, yShift).contains(x, y)) {
				if(s.delete()) {
					modified = true;
					i.remove();
					
					//if this is the currently selected object, remove ghosts and such
					if(s == selected) {
						removeGhosts();
						selected = null;
					}
				}
			}
		}
		
		//if something was modified, then repaint
		if(modified) {repaint();}
	}
	
	public void colorChanged(ColorType c) {
		if(selected != null) {
			selected.setColor(c);
			repaint();
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {

		int code = e.getKeyCode();
		int newXScroll = xShift;
		int newYScroll = yShift;
		
		if(code == KEY_LEFT) {
			if(selected == null) {
				newXScroll -= SCROLL_SPEED;
				if(newXScroll < 0) {newXScroll = 0;}
			} else {
				selected.move(-TILE_X, 0);
			}
			
		} else if(code == KEY_RIGHT) {
			if(selected == null) {
				newXScroll += SCROLL_SPEED;
				if(newXScroll > maxXShift) {newXScroll = maxXShift;}
			} else {
				selected.move(TILE_X, 0);
			}
			
		} else if(code == KEY_UP) {
			if(selected == null) {
				newYScroll -= SCROLL_SPEED;
				if(newYScroll < 0) {newYScroll = 0;}
			} else {
				selected.move(0, TILE_Y);
			}

		} else if(code == KEY_DOWN) {
			if(selected == null) {
				newYScroll += SCROLL_SPEED;
				if(newYScroll > maxYShift) {newYScroll = maxYShift;}
			} else {
				selected.move(0, -TILE_Y);
			}
			
		} else if(code == KEY_DEL) {
			if(selected != null && selected.delete()) {
				selectables.remove(selected);
				selected = null;
				removeGhosts();
				repaint();
			}
			
		}
		
		//if we scrolled, repaint the screen
		if(xShift != newXScroll || yShift != newYScroll) {
			xShift = newXScroll;
			yShift = newYScroll;
			repaint();
		}
	}

	public void mouseMoved(MouseEvent e) {}

	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	
	public void keyTyped(KeyEvent e) {}
	public void keyReleased(KeyEvent e) {}

}
