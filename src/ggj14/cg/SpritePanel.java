package ggj14.cg;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

/**
 * SpritePanel class
 * 
 * A canvas for drawing sprites and drawables on a JFrame
 * Draws to front and back buffers (BufferedImages), then scales
 * during paint()
 * 
 * @author Sean Gilhuly
 *
 */

@SuppressWarnings("serial")
public class SpritePanel extends JComponent {

	private BufferedImage frontBuffer;
	private BufferedImage backBuffer;
	private Graphics canvas;
	private Color backColor;
	private int w;
	private int h;
	private final double SCALE;

	public SpritePanel() {
		this(640, 480, 1.0);
	}
	
	public SpritePanel(int width, int height) {
		this(width, height, 1.0);
	}

	public SpritePanel(int width, int height, double scale) {
		super();
		w = width;
		h = height;
		SCALE = scale;
		
		frontBuffer = new BufferedImage((int) (w / SCALE) + 8, (int) (h / SCALE) + 8, BufferedImage.TYPE_INT_ARGB);
		backBuffer = new BufferedImage((int) (w / SCALE) + 8, (int) (h / SCALE) + 8, BufferedImage.TYPE_INT_ARGB);
		backColor = new Color(0, 0, 0);

		canvas = backBuffer.createGraphics();
		cls();
	}

	public void flip() {
		BufferedImage temp = frontBuffer;
		frontBuffer = backBuffer;
		backBuffer = temp;

		canvas = backBuffer.createGraphics();
	}

	public void cls() {
		canvas.setColor(backColor);
		canvas.fillRect(0, 0, w, h);
	}

	public void drawImage(BufferedImage im, int x, int y) {
		canvas.drawImage(im, x, y, null);
	}

	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.scale(SCALE, SCALE);
		g.drawImage(frontBuffer, 0, 0, null);
	}
	
	public Graphics getGraphics() {
		return canvas;
	}

}
