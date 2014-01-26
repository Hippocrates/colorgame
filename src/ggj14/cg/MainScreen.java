package ggj14.cg;

import ggj14.cg.editor.EditorMainWindow;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class MainScreen extends GameState {

	public static final Font TITLE_FONT = new Font(Font.MONOSPACED, Font.BOLD, 48);
	public static final Font MENU_FONT = new Font(Font.MONOSPACED, Font.BOLD, 24);
	
	public ArrayList<MenuItem> menu;
	public int selected;
	public MenuItem selectedItem;
	
	public double animTimer = 0;
	public final double animLength = 0.75;
	public int colorIndex = 1;
	
	public MainScreen() {
		menu = new ArrayList<MenuItem>();
		menu.add(MenuItem.MENU_START);
		menu.add(MenuItem.MENU_OPTIONS);
		menu.add(MenuItem.MENU_CREDITS);
		menu.add(MenuItem.MENU_EDITOR);
		menu.add(MenuItem.MENU_EXIT);
		
		selected = 0;
		selectedItem = menu.get(0);
	}
	
	public void update(double s) {
		animTimer += s;
		if(animTimer > animLength) {
			animTimer -= animLength;
			colorIndex++;
			if(colorIndex >= ColorType.size() - 1) {
				colorIndex = 1;
			}
		}
	}
	
	public void draw(Graphics g) {
		g.setFont(TITLE_FONT);
		g.setColor(ColorType.fromId(colorIndex).getColor());
		g.drawString("Chromaticaste", 30, 75);
		
		g.setFont(MENU_FONT);
		int i = 0;
		int xPlus;
		for(MenuItem item : menu) {
			xPlus = 0;
			g.setColor(Color.RED);
			if(item == selectedItem) {
				xPlus = 14;
				g.setColor(Color.GREEN);
			}
			
			g.drawString(item.display, 30 + xPlus, 135 + 30 * i);
			i++;
		}
	}
	
	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()) {
		case KEY1_UP:
		case KEY2_UP:
			selected--;
			if(selected < 0) {selected = 0;}
			selectedItem = menu.get(selected);
			break;
		case KEY1_DOWN:
		case KEY2_DOWN:
			selected++;
			if(selected >= menu.size()) {selected = menu.size() - 1;}
			selectedItem = menu.get(selected);
			break;
		case KEY1_SELECT:
		case KEY2_SELECT:
			selectItem();
			break;
		case KEY1_BACK:
			closeState();
			break;
		}
	}
	
	public void selectItem() {
		switch(menu.get(selected)) {
		case MENU_START:
			nextState(new GameScreen());
			break;
			//TODO: Options and credits screens
		case MENU_OPTIONS:
			break;
		case MENU_CREDITS:
			break;
		case MENU_EDITOR:
			new EditorMainWindow();
			break;
		case MENU_EXIT:
			closeState();
			break;
		}
	}
	
	private static enum MenuItem {
		MENU_START("Start Game"),
		MENU_OPTIONS("Options"),
		MENU_CREDITS("Credits"),
		MENU_EDITOR("Editor"),
		MENU_EXIT("Exit Game");
		
		public final String display;
		
		MenuItem(String display) {
			this.display = display;
		}
	}
	
}
