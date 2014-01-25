package ggj14.cg.editor;

import ggj14.cg.ColorType;
import ggj14.cg.ImageOps;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

public class EditorPlayer implements Selectable {

	public static final int PLR_X = EditorMainWindow.PLR_X;
	public static final int PLR_Y = EditorMainWindow.PLR_Y;

	public int x;
	public int y;
	public ColorType color;
	public EditorMainWindow editor;
	private static BufferedImage images[];

	static {
		images = new BufferedImage[ColorType.size()];

		BufferedImage original = EditorMainWindow.toolSets.getImage(0, 0);
		for(ColorType c : ColorType.values()) {
			images[c.ordinal()] = ImageOps.makeColouredImage(original, c.getColor());
		}
	}

	public EditorPlayer(int x, int y, ColorType color, EditorMainWindow editor) {
		this.x = x;
		this.y = y;
		this.color = color;
		this.editor = editor;
	}
	
	public void set(int x, int y, ColorType color) {
		this.x = x;
		this.y = y;
		this.color = color;
	}

	@Override
	public Rectangle getRect(int xShift, int yShift) {
		// TODO Auto-generated method stub
		return new Rectangle(x - xShift, (editor.yDim - 1) * PLR_Y - y - yShift, PLR_X, PLR_Y);
	}

	@Override
	public boolean isGhost() {
		return false;
	}

	@Override
	public void addGhosts(LinkedList<Selectable> list) {
	}

	@Override
	public void draw(Graphics g, int xShift, int yShift) {
		// TODO Auto-generated method stub
		g.drawImage(images[color.ordinal()], x - xShift, (editor.yDim - 1) * PLR_Y - y - yShift, null);
	}

	@Override
	public void drawSelect(Graphics g, int xShift, int yShift) {
		// TODO Auto-generated method stub
		draw(g, xShift, yShift);

		//draw a white box
		g.setColor(Color.WHITE);
		g.drawRect(x - xShift, (editor.yDim - 1) * PLR_Y - y - yShift,
				PLR_X, PLR_Y);
	}

	@Override
	public void mouseDragged(MouseEvent e, int xShift, int yShift) {
		int x1 = e.getX() - editor.insets.left + xShift;
		int y1 = editor.yDim * PLR_Y - (e.getY() - editor.insets.top) + yShift;
		
		int newX;
		int newY;
		
		//snap if shift is not held
		if((e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) == MouseEvent.SHIFT_DOWN_MASK) {
			newX = x1 - PLR_X / 2;
			newY = y1 - PLR_Y / 2;
		} else {
			newX = PLR_X * (x1 / PLR_X);
			newY = PLR_Y * (y1 / PLR_Y);
		}
		
		//if they are both the same, do nothing
		if(newX != x || newY != y) {
			x = newX;
			y = newY;
			editor.repaint();
		}
		checkBounds();
	}

	@Override
	public void move(int dx, int dy) {
		// TODO Auto-generated method stub
		x += dx;
		y += dy;
		checkBounds();
		editor.repaint();
	}

	@Override
	public void setColor(ColorType c) {
		if(c == ColorType.BLANK || c == ColorType.WHITE) {return;}
		
		color = c;
	}

	@Override
	public void checkBounds() {

		int newX = x;
		int newY = y;
		
		//assert they fall within reasonable bounds and not touching map edge
		if(newX < PLR_X) {newX = PLR_X;}
		if(newY < PLR_Y) {newY = PLR_Y;}

		if(newX > PLR_X * (editor.xDim - 2)) {newX = PLR_X * (editor.xDim - 2);}
		if(newY > PLR_Y * (editor.yDim - 2)) {newY = PLR_Y * (editor.yDim - 2);}
		
		//if they are both the same, do nothing
		if(newX != x || newY != y) {
			x = newX;
			y = newY;
			editor.repaint();
		}
	}

	@Override
	public boolean delete() {
		return false;
	}

}
