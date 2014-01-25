package ggj14.cg;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class SpriteSheet {
	
	BufferedImage rootImage;
	BufferedImage[] images;
	
	public SpriteSheet(BufferedImage image, int tileX, int tileY) {
		rootImage = image;
		
		int xTiles = rootImage.getWidth() / tileX;
		int yTiles = rootImage.getHeight() / tileY;
		
		images = new BufferedImage[xTiles * yTiles];
		
		int currentImage = 0;
		
		for (int y = 0; y < yTiles; ++y) {
			for (int x = 0; x < xTiles; ++x) {
				images[currentImage] = rootImage.getSubimage(x*tileX, y*tileY, tileX, tileY);
				++currentImage;
			}
		}
	}
	
	public BufferedImage getImage(int i)
	{
		return images[i];
	}
}
