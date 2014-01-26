package ggj14.cg;

public class Player implements Drawable {
	
	public static final int COLLISION_X_OFFSET = 5;
	public static final int COLLISION_WIDTH = 6;
	public static final int COLLISION_Y_OFFSET = 0;
	public static final int COLLISION_HEIGHT = 16;

	public static final int PLR_X = GameScreen.PLR_X;
	public static final int PLR_Y = GameScreen.PLR_Y;
	
	public boolean isWalking;
	public boolean isFalling;
	public double fallDur = 0; //how long the player has been falling, used to fix animation/jump issues
	public final double fallCutoff = 0.05;
	
	public boolean facingRight = true;
	public double animTimer;
	public double transitionTimer;
	public final double walkAnimLength = 0.15;
	public final double lookAnimLength = 1.20;
	public final double blinkAnimLength = 0.20;
	public final double winAnimLength = 0.15;
	public final double lostAnimLength = 0.35;
	public final double dieAnimLength = 0.10;
	
	public Vector pos;
	public Vector pos2;
	public AABBox collision;
	public float xv;
	public float yv;

	public final float maxxv = 48;
	public final float maxyv = 128;
	public final float xa = 80;
	public final float ya = 80;
	
	private ColorType color;
	private int animX;
	private int animY;

	public boolean isDead = false;
	private boolean hasWon = false;
	private boolean hasLost = false;
	
	public void jumpPressed() {
		if ((!isFalling || fallDur < fallCutoff) && !hasWon) {
			yv = 95;
			isFalling = true;
			fallDur = fallCutoff;
		}
	}
	
	//call when left or right pressed
	public void walkPressed(boolean isRight) {
		if(isWalking && facingRight == isRight || hasWon || hasLost) {return;}
		
		isWalking = true;
		facingRight = isRight;
		animTimer = 0;
		animX = 0;
		animY = 0;
	}
	
	//cal when left or right depressed
	public void walkStopped(boolean isRight) {
		if(facingRight != isRight || hasWon || hasLost) {return;}
		
		isWalking = false;
		setTransitionTimer();
		animTimer = 0;
		animX = 0;
		animY = 1;
	}
	
	public Player(float x, float y, ColorType color) {
		pos = new Vector(x, y);
		pos2 = new Vector(x + PLR_X, y + PLR_Y);
		collision = new AABBox(pos.x + COLLISION_X_OFFSET, pos.y + COLLISION_Y_OFFSET, pos.x + COLLISION_X_OFFSET + COLLISION_WIDTH, pos.y + COLLISION_Y_OFFSET + COLLISION_HEIGHT);
		this.color = color;
		setTransitionTimer(); //so players don't start looking immediately
	}
	
	public AABBox getCollisionBox()
	{
		return collision;
	}

	public void update(double s, TileMap tileMap, Camera camera) {
		
		yv -= (ya * s);
		if (yv > maxyv) { yv = maxyv; }
		if (yv < -maxyv) { yv = -maxyv; }
		
		if(!isDead) {
			
			if(isWalking && !hasWon) {
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
		}
		
		pos.x += (xv * s);
		pos.y += (yv * s);
		collision.set(pos.x + COLLISION_X_OFFSET, pos.y + COLLISION_Y_OFFSET, pos.x + COLLISION_X_OFFSET + COLLISION_WIDTH, pos.y + COLLISION_Y_OFFSET + COLLISION_HEIGHT);
		
		if(!isDead) {
			CollisionResult result = CollisionOps.collidePlayerToWorld(this, tileMap, camera);
			
			if (result.landed) {
				isFalling = false;
				yv = 0.0f;
				animY = 0;
			}
			
			if (yv < 0.0f && !isFalling) {
				isFalling = true;
				fallDur = 0;
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
				yv = maxyv;
				if(xv > 0) {xv = -maxxv;}
				else {xv = maxxv;}
			}
		}
		
		pos2.set(pos.x + PLR_X, pos.y + PLR_Y);
		collision.set(pos.x + COLLISION_X_OFFSET, pos.y + COLLISION_Y_OFFSET, pos.x + COLLISION_X_OFFSET + COLLISION_WIDTH, pos.y + COLLISION_Y_OFFSET + COLLISION_HEIGHT);

		//update the player's animation
		if(hasWon) { //winning animation trumps all others
			animTimer += s;
			if(animTimer >= winAnimLength * 4) {animTimer -= winAnimLength * 4;}
			animY = 0;
			if(animTimer < winAnimLength) {
				animX = 1;
			} else if(animTimer < winAnimLength * 2) {
				animX = 7;
			} else {
				animX = 0;
			}
			
		} else if(hasLost && isDead) {
			animTimer += s;
			if(animTimer >= dieAnimLength) {
				animTimer -= dieAnimLength;
				facingRight = !facingRight;
			}
			
		} else if(hasLost) {
			animTimer += s;
			if(animTimer >= lostAnimLength) {
				animTimer -= lostAnimLength;
				facingRight = !facingRight;
			}
			
		} else if(isFalling && fallDur >= fallCutoff) {
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
			animTimer += s;
			animY = 1;
			//if standing, switch to a random animation when done
			if(animX == 0) {
				if(animTimer >= transitionTimer) {
					animTimer = 0;
					animX = (int) Math.floor(Math.random() * 3) + 1;
				}
				//otherwise switch to standing when this animation is done
			} else {
				if((animX == 1 && animTimer >= blinkAnimLength) || animTimer >= lookAnimLength) {
					animTimer = 0;
					animX = 0;
					setTransitionTimer();
				}
			}
			if(animX > 3) {animX = 0;} //to fix disapperaing player bug
		}
		fallDur += s;
	}
	
	//sets the transition timer to a random number between 3 and 5
	//players will wait that long before transitioning to a random idle animation
	private void setTransitionTimer() {
		transitionTimer = 3 + Math.random() * 2;
	}
	
	public void setHasWon(boolean won) {
		if(won && !hasWon) {
			animTimer = 0;
		}
		hasWon = won;
	}
	
	public void setHasLost(boolean lost) {
		if(lost && !hasLost) {
			animTimer = 0;
		}
		hasLost = lost;
	}
	
	public Vector getCenter() {
		return getCollisionBox().getCenter();
	}
	
	public Vector getPos() {
		return pos;
	}
	
	public Vector getPos2() {
		return pos2;
	}
	
	public int getTileX() {
		return animX;
	}
	
	public int getTileY() {
		return animY;
	}
	
	public ColorType getColor() {
		return color;
	}
	
	public boolean drawFlipped() {
		return !facingRight;
	}
	
}
