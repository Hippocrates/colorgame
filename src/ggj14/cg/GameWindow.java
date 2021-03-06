package ggj14.cg;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Insets;
import java.util.Stack;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class GameWindow extends JFrame {

	public static final int WIDTH = 800;
	public static final int HEIGHT = 600;
	
	public SpritePanel panel;
	private Insets insets;
	
	private Stack<GameState> stateStack;
	
	public Thread gameThread;
	public boolean gameRunning = false;

	public GameWindow() {
		super("Colour Game");
		
		//create the window, add a drawing panel
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		panel = new SpritePanel(WIDTH, HEIGHT);
		getContentPane().add(panel, BorderLayout.CENTER);

		setResizable(false);
		pack(); //this is necessary to get insets before showing
		insets = getInsets();
		setSize(WIDTH + insets.left + insets.right, HEIGHT + insets.top + insets.bottom);
		
		setVisible(true);
		
		//create the stack, start with main screen
		stateStack = new Stack<GameState>();
		pushState(new MainScreen());
		
		startThread();
	}
	
	private void startThread() {
		if(gameRunning) {return;}
		
		gameRunning = true;
		
		gameThread = new Thread() {
			public void run() {
				long startTime = System.nanoTime();
				long stopTime;
				double elapsed;
				final double nanosPerSecond = 1000000000;
				
				while(gameRunning) {
					try {
						Thread.sleep(30);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					stopTime = System.nanoTime();
					elapsed = (stopTime - startTime) / nanosPerSecond;
					startTime = stopTime;
					
					if(!stateStack.isEmpty()) {
						GameState state = stateStack.peek();
						
						//Make sure we don't simulate for too long, primarily for testing purposes
						if(elapsed > 0.5) {elapsed = 0.5;}
						state.update(elapsed);
						
						//display
						panel.cls();
						Graphics g = panel.getGraphics();
						
						state.draw(g, panel.getWidth(), panel.getHeight());
						
						panel.flip();
						panel.repaint();
					}
				}
			}
		};
		gameThread.start();
	}
	
	public void pushState(GameState state) {
		if(state == null) {return;}
		
		if(!stateStack.isEmpty()) {
			GameState oldState = stateStack.peek();
			removeKeyListener(oldState);
			oldState.pause();
		}
		stateStack.add(state);
		state.master = this;
		state.create();
		state.resume();
		addKeyListener(state);
	}
	
	public void popState(GameState state) {
		if(state == null || stateStack.isEmpty() || stateStack.peek() != state) {return;}
		
		removeKeyListener(state);
		state.pause();
		state.destroy();
		
		stateStack.pop();
		
		if(stateStack.isEmpty()) {
			//TODO Clean up the game
			dispose();
			System.exit(0);
			//WindowEvent wev = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
            //Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
		} else {
			GameState newState = stateStack.peek();
			newState.resume();
			addKeyListener(newState);
		}
	}
}
