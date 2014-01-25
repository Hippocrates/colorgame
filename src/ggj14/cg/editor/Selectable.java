package ggj14.cg.editor;

import ggj14.cg.ColorType;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.LinkedList;

public interface Selectable {

	public Rectangle getRect(int xShift, int yShift); //returns a rectangle for screen coordinates
	
	public boolean isGhost(); //is this object a ghost ie should it be removed whenever something new is selected
	public void addGhosts(LinkedList<Selectable> list); //tells this to add any ghost selectables to the list

	public void draw(Graphics g, int xShift, int yShift); //draws something normally
	public void drawSelect(Graphics g, int xShift, int yShift); //draws something to show selected
	public void mouseDragged(MouseEvent e, int xShift, int yShift); //this was dragged somewhere
	public void move(int dx, int dy); //move this
	
	public void setColor(ColorType c); //change color, if possible
	
	public void checkBounds(); //tells this to adjust its location eg the map size changed
	
	public boolean delete(); //if this object can be deleted, then do so and return true
}
