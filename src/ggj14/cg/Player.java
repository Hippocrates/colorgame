package ggj14.cg;

public class Player {
	
	public static final int COLLISION_X_OFFSET = 4;
	public static final int COLLISION_WIDTH = 8;
	public static final int COLLISION_Y_OFFSET = 0;
	public static final int COLLISION_HEIGHT = 16;
	
	public boolean isDead = false;
	public boolean isWalking;
	public boolean isFalling;
	public boolean facingRight = true;
	public double animTimer;
	public final double walkAnimLength = 0.15;
	
	public void jumpPressed() {
		if (!isFalling) {
			yv = 95;
			isFalling = true;
		}
	}
	
	//call when left or right pressed
	public void walkPressed(boolean isRight) {
		if(isWalking && facingRight == isRight) {return;}
		
		isWalking = true;
		facingRight = isRight;
		animTimer = 0;
	}
	
	//cal when left or right depressed
	public void walkStopped(boolean isRight) {
		if(facingRight != isRight) {return;}
		
		isWalking = false;
	}
	
	public Vector pos;
	public float xv;
	public float yv;

	public final float maxxv = 48;
	public final float maxyv = 128;
	public final float xa = 80;
	public final float ya = 80;
	
	private ColorType color;
	private int animX;
	private int animY;
	
	public Player(float x, float y, ColorType color) {
		pos = new Vector(x, y);
		this.color = color;
	}
	
	public AABBox getCollisionBox()
	{
		return new AABBox(pos.x + COLLISION_X_OFFSET, pos.y + COLLISION_Y_OFFSET, pos.x + COLLISION_X_OFFSET + COLLISION_WIDTH, pos.y + COLLISION_Y_OFFSET + COLLISION_HEIGHT);
	}

	public void update(double s, TileMap tileMap, Camera camera) {
		
		yv -= (ya * s);
		if (yv > maxyv) { yv = maxyv; }
		if (yv < -maxyv) { yv = -maxyv; }
		
		if(isWalking) {
			if(facingRight) {
				xv += (xa * s);
				if(xv > maxxv) {xv = maxxv;}
			} else {
				xv -= (xa * s);
				if(xv < -maxxv) {xv = -maxxv;}
			}
			
		} else if(!isFalling) {
			if(xv > 0) {
				xv -= (xa * s);
				if(xv < 0) {xv = 0;}
			}
			if(xv < 0) {
				xv += (xa * s);
				if(xv > 0) {xv = 0;}
			}
		}
		
		pos.x += (xv * s);
		pos.y += (yv * s);
		
		CollisionResult result = CollisionOps.collidePlayerToWorld(this, tileMap, camera);
		
		if (result.landed)
		{
			isFalling = false;
			yv = 0.0f;
		}
		
		if (yv < 0.0f) {
			isFalling = true;
		}
		
		if (result.blockLeft || result.blockRight)
		{
			xv = 0.0f;
		}
		
		if (result.blockTop)
		{
			yv = 0.0f;
		}
		
		if (result.killed) {
			isDead = true;
		}

		//update the player's animation
		if(isFalling) {
			animY = 0;
			if(yv > 12) {
				animX = 7;
			} else if(yv < -12) {
				animX = 1;
			} else {
				animX = 0;
			}
		} else if(isWalking) {
			animTimer += s;
			if(animTimer >= walkAnimLength * 8) {animTimer -= walkAnimLength * 8;}
			animY = 0;
			animX = (int) Math.floor(animTimer / walkAnimLength);
		} else {
			animY = 1;
			animX = 0;
		}
	}
	
	public Vector getCenter() {
		return getCollisionBox().getCenter();
	}
	
	public ColorType getColor() {
		return color;
	}
	
	public int getAnimX() {
		return animX;
	}
	
	public int getAnimY() {
		return animY;
	}
	
}
