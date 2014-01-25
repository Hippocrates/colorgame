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
						
						state.update(elapsed);
						
						//display
						panel.cls();
						Graphics g = panel.getGraphics();
						
						state.draw(g);
						
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
			stateStack.peek().pause();
		}
		stateStack.add(state);
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
		} else {
			stateStack.peek().resume();
		}
	}
}
