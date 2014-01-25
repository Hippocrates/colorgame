package ggj14.cg;

public class Camera {
	
	public Vector position;
	public Vector viewSize;
	public Vector screenSize;
	
	public Camera(Vector _position, Vector _viewSize, Vector _screenSize)
	{
		position = _position;
		viewSize = _viewSize;
		screenSize = _screenSize;
	}
	
	public AABBox getViewBounds() {
		return new AABBox(position.x, position.y, position.x + viewSize.x, position.y + viewSize.y);
	}
	
	public Vector viewToScreen(Vector inPos) {
		Vector raw = inPos.sub(position);
		
		float xRatio = screenSize.x / viewSize.x;
		float yRatio = screenSize.y / viewSize.y;
		
		return new Vector(raw.x * xRatio, raw.y * yRatio);
	}
	
	public Vector screenToView(Vector inPos) {
		float xRatio = viewSize.x / screenSize.x;
		float yRatio = viewSize.y / screenSize.y;
		
		return new Vector(inPos.x * xRatio, inPos.y * yRatio).add(position);
	}
}
