package ggj14.cg.editor;

import ggj14.cg.ColorType;
import ggj14.cg.SpritePanel;
import ggj14.cg.SpriteSheet;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class EditorPaletteWindow extends JFrame implements MouseListener {

	//width and height of the screen
	public static final int WIDTH;
	public static final int HEIGHT;
	
	//the size of each image
	public static final int IMG_SIZE = EditorMainWindow.TILE_X;
	
	private EditorMainWindow editor;

	private SpritePanel panel;
	private Insets insets;
	
	private static SpriteSheet tileSets[];
	private static SpriteSheet toolSets;
	
	private int mouseCat = 0; //which category of objects is selected
	//blocks - 0, tools - 1
	private int mouseIndex = 0; //what index of the category is selected
	private ColorType colorIndex = ColorType.RED; //which color is selected
	
	static {
		//temporary storage for height/width values
		int w = 640;
		int h = 480;
		
		try {
			//load all of the images
			tileSets = EditorMainWindow.tileSets;
			toolSets = EditorMainWindow.toolSets;
			
			//get the highest of the widths
			w = tileSets[0].getNumTilesX();
			if(toolSets.getNumTilesX() > w) {w = toolSets.getNumTilesX();}
			if(ColorType.size() > w) {w = ColorType.size();}
			w *= IMG_SIZE;
			
			//sum the heights
			h = tileSets[0].getNumTilesY() + toolSets.getNumTilesY() + 1;
			h *= IMG_SIZE;
			
		} catch (Exception e) {
			System.err.println("Could not open image files!");
			System.exit(1);
		}
		
		WIDTH = w;
		HEIGHT = h;
	}

	public EditorPaletteWindow(EditorMainWindow editor) {
		super("Colour Game Level Editor Palette");

		//create the input listeners
		addMouseListener(this);
		
		this.editor = editor;

		//create the window, add a drawing panel
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		panel = new SpritePanel(WIDTH, HEIGHT);
		getContentPane().add(panel, BorderLayout.CENTER);

		setResizable(false);
		pack(); //this is necessary to get insets before showing
		insets = getInsets();
		setSize(WIDTH + insets.left + insets.right, HEIGHT + insets.top + insets.bottom);
		setVisible(true);
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		int y = 0; //counter for y dimension
		int numfx;
		
		panel.cls();
		Graphics g2 = panel.getGraphics();
		
		BufferedImage im = tileSets[colorIndex.ordinal()].getRootImage();
		panel.drawImage(im, 0, y);
		if(mouseCat == 0) {
			numfx = im.getWidth() / IMG_SIZE;
			drawRect(g2, mouseIndex % numfx * IMG_SIZE, mouseIndex / numfx * IMG_SIZE + y);
		}
		y += im.getHeight();
		
		im = toolSets.getRootImage();
		panel.drawImage(im, 0, y);
		if(mouseCat == 1) {
			numfx = im.getWidth() / IMG_SIZE;
			drawRect(g2, mouseIndex % numfx * IMG_SIZE, mouseIndex / numfx * IMG_SIZE + y);
		}
		y += im.getHeight();
		
		for(ColorType c : ColorType.values()) {
			g2.setColor(c.getColor());;
			g2.fillRect(c.ordinal() * IMG_SIZE, y, IMG_SIZE, IMG_SIZE);
		}
		drawRect(g2, colorIndex.ordinal() * IMG_SIZE, y);
		
		panel.flip();
		panel.repaint();
	}
	
	private void drawRect(Graphics g, int xLoc, int yLoc) {
		g.setColor(Color.BLUE);
		g.drawLine(xLoc, yLoc, xLoc + IMG_SIZE - 1, yLoc);
		g.drawLine(xLoc, yLoc, xLoc, yLoc + IMG_SIZE - 1);
		g.drawLine(xLoc, yLoc + IMG_SIZE - 1, xLoc + IMG_SIZE - 1, yLoc + IMG_SIZE - 1);
		g.drawLine(xLoc + IMG_SIZE - 1, yLoc, xLoc + IMG_SIZE - 1, yLoc + IMG_SIZE - 1);

		g.setColor(Color.CYAN);
		g.drawLine(xLoc + 1, yLoc + 1, xLoc + IMG_SIZE - 2, yLoc + 1);
		g.drawLine(xLoc + 1, yLoc + 1, xLoc + 1, yLoc + IMG_SIZE - 2);
		g.drawLine(xLoc + 1, yLoc + IMG_SIZE - 2, xLoc + IMG_SIZE - 2, yLoc + IMG_SIZE - 2);
		g.drawLine(xLoc + IMG_SIZE - 2, yLoc + 1, xLoc + IMG_SIZE - 2, yLoc + IMG_SIZE - 2);
	}
	
	public void dispose() {
		super.dispose();
	}
	
	public void mousePressed(MouseEvent e) {
		
		int x = e.getX() - insets.left;
		int y = e.getY() - insets.top;

		int numfx;
		int numfy;

		BufferedImage tiles = tileSets[colorIndex.ordinal()].getRootImage();
		BufferedImage tools = toolSets.getRootImage();
		
		boolean wasColor = false;
		
		//see what region it is in
		if(y < tiles.getHeight()) {
			mouseCat = 0;
			numfx = tiles.getWidth() / IMG_SIZE;
			numfy = tiles.getHeight() / IMG_SIZE;
			if(x >= tiles.getWidth()) {x = tiles.getWidth() - 1;}
			
		} else if((y -= tiles.getHeight()) < tools.getHeight()) {
			mouseCat = 1;
			numfx = tools.getWidth() / IMG_SIZE;
			numfy = tools.getHeight() / IMG_SIZE;
			if(x >= tools.getWidth()) {x = tools.getWidth() - 1;}
			
		} else {
			y -= tools.getHeight();
			numfx = ColorType.size();
			numfy = 1;
			if(x >= ColorType.size() * IMG_SIZE) {x = ColorType.size() * IMG_SIZE - 1;}
			
			wasColor = true;
		}
		
		if(wasColor) {
			colorIndex = ColorType.fromId(x / IMG_SIZE);
			editor.colorChanged(colorIndex);
		} else {
			mouseIndex = x / IMG_SIZE + y / IMG_SIZE * numfx;
			if(mouseIndex >= numfx * numfy) {mouseIndex = numfx * numfy - 1;}
		}
		
		repaint();
	}
	
	public int getMouseCat() {
		return mouseCat;
	}
	
	public int getMouseIndex() {
		return mouseIndex;
	}
	
	public ColorType getPaintIndex() {
		return colorIndex;
	}

	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	
}
