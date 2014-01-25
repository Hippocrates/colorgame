package ggj14.cg;

public class Util {
	public static int min(float[] params)
	{
		int min = 0;
		
		for (int i = 1; i < params.length; ++i)
		{
			if (params[i] < params[min])
			{
				min = i;
			}
		}
		
		return min;
	}
}
