package ggj14.cg;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public abstract class GameState implements KeyListener {
	public GameWindow master;
	
	public void create() {} // Called when a state is first pushed onto the stack
	public void destroy() {} // Called when a state is about to be popped from the stack
	public void pause() {} // Called when a state should stop executing
	public void resume() {} // Called when a state can start executing
	
	public void update(double s) {}
	public void draw(Graphics g) {}
	
	public void closeState() {
		if(master == null) {return;}
		
		master.popState(this);
	}
	
	public void nextState(GameState state) {
		if(master == null) {return;}
		
		master.pushState(state);
	}
	
	public void keyPressed(KeyEvent e) {}
	public void keyReleased(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {}
}
