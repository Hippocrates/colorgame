package ggj14.cg;

public enum GameKey {
	LEFT,
	RIGHT,
	JUMP,
	DUCK;
	
	private static GameKey[] _all = GameKey.values();
	
	public GameKey get(int i)
	{
		return _all[i];
	}
}
