package ggj14.cg;

import java.awt.Color;

public enum ColorType {
	
	//Make sure that blank is always first
	BLANK('X', new Color(0, 0, 0, 0)),
	RED('R', Color.RED),
	YELLOW('Y', Color.YELLOW),
	GREEN('G', Color.GREEN),
	CYAN('C', Color.CYAN),
	BLUE('B', Color.BLUE),
	MAGENTA('M', Color.MAGENTA),
	WHITE('W', Color.WHITE);
	
	private char code;
	private Color color;
	private static ColorType[] _all = ColorType.values();

	ColorType(char _code, Color _color){
		code = _code;
		color = _color;
	}
	
	public char getCode()
	{
		return code;
	}
	
	public Color getColor()
	{
		return color;
	}
	
	public static ColorType fromCode(char code)
	{
		for (ColorType c : _all)
		{
			if (c.code == code)	{
				return c;
			}
		}
		
		return null;
	}
	
	public static ColorType fromId(int id) {
		if (id >= 0 && id < _all.length) {
			return _all[id];
		}
		
		return null;
	}
	
	public static int size() {
		return _all.length;
	}
}
