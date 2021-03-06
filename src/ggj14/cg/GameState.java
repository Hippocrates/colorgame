package ggj14.cg;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public abstract class GameState implements KeyListener {
	public GameWindow master;

	public static final int KEY1_UP = KeyEvent.VK_UP;
	public static final int KEY1_DOWN = KeyEvent.VK_DOWN;
	public static final int KEY1_LEFT = KeyEvent.VK_LEFT;
	public static final int KEY1_RIGHT = KeyEvent.VK_RIGHT;

	public static final int KEY2_UP = KeyEvent.VK_W;
	public static final int KEY2_DOWN = KeyEvent.VK_S;
	public static final int KEY2_LEFT = KeyEvent.VK_A;
	public static final int KEY2_RIGHT = KeyEvent.VK_D;

	public static final int KEY1_SELECT = KeyEvent.VK_SPACE;
	public static final int KEY2_SELECT = KeyEvent.VK_ENTER;
	public static final int KEY1_BACK = KeyEvent.VK_ESCAPE;
	
	public void create() {} // Called when a state is first pushed onto the stack
	public void destroy() {} // Called when a state is about to be popped from the stack
	public void pause() {} // Called when a state should stop executing
	public void resume() {} // Called when a state can start executing
	
	public void update(double s) {}
	public void draw(Graphics g) {}
	public void draw(Graphics g, int width, int height) { draw(g); }
	
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
