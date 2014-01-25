package ggj14.cg;

import javax.swing.JFrame;

public class GameWindow extends JFrame {

	public static final int WIDTH = 800;
	public static final int HEIGHT = 600;

	public GameWindow() {
		super("Colour Game");
		
		setSize(WIDTH, HEIGHT);
		
		setVisible(true);
	}
}
