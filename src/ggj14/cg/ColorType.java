package ggj14.cg;

import java.awt.Color;

public enum ColorType {
	
	BLANK('X', new Color(0, 0, 0, 0)),
	RED('R', Color.RED),
	GREEN('G', Color.GREEN),
	BLUE('B', Color.BLUE),
	YELLOW('Y', Color.YELLOW),
	PURPLE('P', Color.MAGENTA),
	CYAN('C', Color.CYAN);
	
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
}
