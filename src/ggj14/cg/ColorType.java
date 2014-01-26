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
	private static CollisionType[][] collisionMatrix;
	
	static {
		collisionMatrix = new CollisionType[size()][size()];
		
		for (int i = 0; i < size(); ++i)
		{
			collisionMatrix[0][i] = CollisionType.NOTHING;
		}
		
		for (int i = 0; i < size(); ++i)
		{
			collisionMatrix[size()-1][i] = CollisionType.SOLID;
		}
		
		int numNormals = size() - 2;
		
		for (int i = 1; i < size() - 1; ++i)
		{
			collisionMatrix[i][0] = CollisionType.NOTHING;
			collisionMatrix[i][size()-1] = CollisionType.SOLID;
			
			collisionMatrix[i][i] = CollisionType.SOLID;
			collisionMatrix[i][(i) % numNormals + 1] = CollisionType.SOLID;
			collisionMatrix[i][(i+1) % numNormals + 1] = CollisionType.NOTHING;
			collisionMatrix[i][(i+2) % numNormals + 1] = CollisionType.DEATH;
			collisionMatrix[i][(i+3) % numNormals + 1] = CollisionType.NOTHING;
			collisionMatrix[i][(i+4) % numNormals + 1] = CollisionType.SOLID;
		}
	}

	ColorType(char _code, Color _color){
		code = _code;
		color = _color;
	}
	
	public CollisionType getCollisionType(ColorType other)
	{
		return collisionMatrix[this.ordinal()][other.ordinal()];
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
