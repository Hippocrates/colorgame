package ggj14.cg;

import java.awt.Color;
import java.awt.image.BufferedImage;

public final class ImageOps {
	public static BufferedImage makeColouredImage(BufferedImage input, Color targetColor) {
		
		BufferedImage copy = new BufferedImage(input.getWidth(), input.getHeight(), input.getType());
		
		for (int y = 0; y < input.getHeight(); ++y) {
			for (int x = 0; x < input.getWidth(); ++x) {
				
				Color original = new Color(input.getRGB(x, y));
				
				double scale = ((double) original.getRed()) / 255.0;
				
				double redScale = scale * targetColor.getRed();
				double greenScale = scale * targetColor.getGreen();
				double blueScale = scale * targetColor.getBlue();
				
				Color outputColor = new Color((int)redScale, (int)greenScale, (int)blueScale, original.getAlpha());
				
				copy.setRGB(x, y, outputColor.getRGB());
			}
		}
		
		return copy;
	}
}
