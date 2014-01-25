package ggj14.cg;

import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class GameScreen extends GameState {
	
	public TileMap tileMap;
	
	
	public void loadMap(String filename) throws IOException {
		
		Scanner scanner = new Scanner(new FileReader(filename));
		
		try {
			int width = scanner.nextInt();
			int height = scanner.nextInt();
			
			// read to EOL first
			scanner.nextLine();
			
			tileMap = new TileMap(width, height);
			
			for (int y = 0; y < height; ++y) {
				String line = scanner.nextLine();
				
				if (!(line.length() == width*2)) {
					throw new RuntimeException("Error, line not right size");
				}
				
				for (int x = 0; x < width; ++x) {
					char code = line.charAt(x*2);
					int type = line.charAt(x*2 + 1) - 0x30;
					tileMap.setTile(x,  y,  type, ColorType.fromCode(code));
				}
			}
			
			while (scanner.hasNextLine()) {
				String createLine = scanner.nextLine();
				System.out.println(createLine);
			}
		}
		finally {
			scanner.close();
		}
	}
	
	
}
