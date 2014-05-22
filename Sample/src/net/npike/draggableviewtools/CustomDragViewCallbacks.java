package net.npike.draggableviewtools;

public interface CustomDragViewCallbacks {
	
	public void onChangeDragViewScale(float scaleX, float scaleY);
	
	
	public float getMinimizedWidth(float scaleFactor);
	public float getMinimizedHeight(float scaleFactor);
	public float getMaximumWidth();
	public float getMaximumHeight();
}
