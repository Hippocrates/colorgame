package ggj14.cg;

public final class CollisionOps {
	
	public static CollisionResult collidePlayerToWorld(Player p, TileMap map)
	{
		CollisionResult results = new CollisionResult();
		AABBox playerBox = p.getCollisionBox();
		TileRange range = map.getTilesOverlapping(playerBox, GameScreen.TILE_X, GameScreen.TILE_Y);
		
		for (int y = range.bottom; y <= range.top; ++y) {
			for (int x = range.left; x <= range.right; ++x) {
				Tile tile = map.getTile(x, y);
				Vector resolution = new Vector(0.0f, 0.0f);
				playerBox = p.getCollisionBox();
				
				if (tile.getColor() != ColorType.BLANK) {
					float rightSide = ((x+1)*GameScreen.TILE_X);
					float leftSide = x*GameScreen.TILE_X;
					float topSide = ((y+1)*GameScreen.TILE_Y);
					float bottomSide = y*GameScreen.TILE_Y;
					
					boolean overlaps[] = new boolean[4];
					boolean solid[] = new boolean[4];
					Vector delta[] = new Vector[4];
					float absDelta[] = new float[4];

					overlaps[0] = playerBox.minX < rightSide && playerBox.maxX > rightSide;
					overlaps[1] = playerBox.maxX > leftSide && playerBox.minX < leftSide;
					overlaps[2] = playerBox.minY < topSide && playerBox.maxY > topSide;
					overlaps[3] = playerBox.maxY > bottomSide && playerBox.minY < bottomSide;
					
					solid[0] = map.getTile(x + 1, y).getColor() != ColorType.BLANK;
					solid[1] = map.getTile(x - 1, y).getColor() != ColorType.BLANK;
					solid[2] = map.getTile(x, y + 1).getColor() != ColorType.BLANK;
					solid[3] = map.getTile(x, y - 1).getColor() != ColorType.BLANK;
					
					delta[0] = new Vector(rightSide - playerBox.minX, 0.0f);
					delta[1] = new Vector(leftSide - playerBox.maxX, 0.0f);
					delta[2] = new Vector(0.0f, topSide - playerBox.minY);
					delta[3] = new Vector(0.0f, bottomSide - playerBox.maxY);
		
					for (int i = 0; i < 4; ++i)
					{
						absDelta[i] = delta[i].lengthSq();
					}
					
					for (int i = 0; i < 4; ++i)
					{
						int curMin = Util.min(absDelta);
						
						if (overlaps[curMin] && !solid[curMin]) {
							resolution = resolution.add(delta[curMin]);
							break;
						}
						
						absDelta[curMin] = Float.MAX_VALUE;
					}
				}
				
				if (resolution.lengthSq() > 0)
				{
					p.pos = p.pos.add(resolution);
					
					if (resolution.y > 0)
					{
						results.landed = true;
					}
					if (resolution.x > 0)
					{
						results.blockLeft = true;
					}
					if (resolution.x < 0)
					{
						results.blockRight = true;
					}
					if (resolution.y < 0)
					{
						results.blockTop = true;
					}
				}
			}
		}
		
		return results;
	}
	
}
