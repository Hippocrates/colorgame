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
				//Copy the image over instead of using getSubImage, which slows down rendering
				images[x][y] = new BufferedImage(tileX, tileY, image.getType());
				for(int imy = 0; imy < tileY; imy++) {
					for(int imx = 0; imx < tileX; imx++) {
						images[x][y].setRGB(imx, imy, rootImage.getRGB(x*tileX + imx, y*tileY + imy));
					}
				}
			}
		}
	}
	
	public BufferedImage getImage(int x, int y)
	{
		return images[x][y];
	}
}
