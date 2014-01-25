package ggj14.cg;

import java.awt.image.BufferedImage;

public class SpriteSheet {
	
	BufferedImage rootImage;
	BufferedImage[][] images;
	
	public SpriteSheet(BufferedImage image, int tileX, int tileY) {
		rootImage = image;
		
		int xTiles = rootImage.getWidth() / tileX;
		int yTiles = rootImage.getHeight() / tileY;
		
		images = new BufferedImage[xTiles][yTiles];
		
		for (int y = 0; y < yTiles; ++y) {
			for (int x = 0; x < xTiles; ++x) {
				images[x][y] = rootImage.getSubimage(x*tileX, y*tileY, tileX, tileY);
			}
		}
	}
	
	public BufferedImage getImage(int x, int y)
	{
		return images[x][y];
	}
}
