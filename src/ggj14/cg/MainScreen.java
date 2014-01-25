package ggj14.cg;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class MainScreen extends GameState {
	
	public static final Font MENU_FONT = new Font(Font.MONOSPACED, Font.BOLD, 24);
	
	public ArrayList<MenuItem> menu;
	public int selected;
	public MenuItem selectedItem;
	
	public double elapsed = 0;
	
	public MainScreen() {
		menu = new ArrayList<MenuItem>();
		menu.add(MenuItem.MENU_START);
		menu.add(MenuItem.MENU_OPTIONS);
		menu.add(MenuItem.MENU_CREDITS);
		menu.add(MenuItem.MENU_EXIT);
		
		selected = 0;
		selectedItem = menu.get(0);
	}
	
	public void draw(Graphics g) {
		g.setFont(MENU_FONT);
		
		int i = 0;
		for(MenuItem item : menu) {
			g.setColor(Color.RED);
			if(item == selectedItem) {
				g.setColor(Color.GREEN);
			}
			
			g.drawString(item.display, 30, 45 + 30 * i);
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
		case MENU_EXIT:
			closeState();
			break;
		}
	}
	
	private static enum MenuItem {
		MENU_START("Start Game"),
		MENU_OPTIONS("Options"),
		MENU_CREDITS("Credits"),
		MENU_EXIT("Exit Game");
		
		public final String display;
		
		MenuItem(String display) {
			this.display = display;
		}
	}
	
}
