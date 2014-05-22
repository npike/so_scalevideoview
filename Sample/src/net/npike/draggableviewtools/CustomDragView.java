package net.npike.draggableviewtools;

public interface CustomDragView {
	
	public void onChangeDragViewScale(float scaleX, float scaleY);
	
	
	public float getMinimizedWidth(float scaleFactor);
	public float getMinimizedHeight(float scaleFactor);
	public float getMaximumWidth();
	public float getMaximumHeight();
}
