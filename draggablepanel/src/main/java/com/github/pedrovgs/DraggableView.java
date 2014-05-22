/*
 * Copyright (C) 2014 Pedro Vicente Gómez Sánchez.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.pedrovgs;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Class created to extends a ViewGroup and simulate the YoutubeLayoutComponent
 * 
 * @author Pedro Vicente Gómez Sánchez
 */
public class DraggableView extends RelativeLayout {

	private static final float HALF = 0.5f;
	private static final double QUARTER = 0.25;
	private static final int ZERO = 0;
	private static final int DEFAULT_SCALE_FACTOR = 2;
	private static final float DEFAULT_TOP_VIEW_HEIGHT = -1;
	private static final int DEFAULT_TOP_VIEW_MARGIN = 30;
	private static final float SLIDE_TOP = 0f;
	private static final float SLIDE_BOTTOM = 1f;
	private static final boolean DEFAULT_ENABLE_HORIZONTAL_ALPHA_EFFECT = true;
	private static final int ONE_HUNDRED = 100;
	private static final float SENSITIVITY = 1f;

	private View dragView;
	private View secondView;

	private FragmentManager fragmentManager;
	private ViewDragHelper viewDragHelper;

	private float xScaleFactor = DEFAULT_SCALE_FACTOR;
	private float yScaleFactor = DEFAULT_SCALE_FACTOR;
	private float topViewMarginRight = DEFAULT_TOP_VIEW_MARGIN;
	private float topViewMarginBottom = DEFAULT_TOP_VIEW_MARGIN;
	private float topViewHeight = DEFAULT_TOP_VIEW_HEIGHT;
	private boolean enableHorizontalAlphaEffect;
	private int dragViewId;
	private int secondViewId;

	private DraggableListener listener;

	private int lastTopPosition;
	private int lastLeftPosition;
	private int mMaximizedDragViewHeight;
	private int mMaximizedDragViewWidth;
	private DraggableViewCallback draggableViewCallback;

	public DraggableView(Context context) {
		super(context);
		initializeViewDragHelper();
	}

	public DraggableView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initializeAttributes(attrs);
	}

	public DraggableView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initializeAttributes(attrs);
	}
	
	public void setDragEnabled(boolean enabled) {
		draggableViewCallback.setDragEnabled(enabled);
	}
	
	public boolean isDragEnabled() {
		return draggableViewCallback.isDragEnabled();
	}

	/**
	 * Configure the horizontal scale factor applied when the view is dragged to
	 * the bottom of the custom view.
	 * 
	 * @param xScaleFactor
	 */
	public void setXTopViewScaleFactor(float xScaleFactor) {
		this.xScaleFactor = xScaleFactor;
	}

	/**
	 * Configure the vertical scale factor applied when the view is dragged to
	 * the bottom of the custom view.
	 * 
	 * @param yScaleFactor
	 */
	public void setYTopViewScaleFactor(float yScaleFactor) {
		this.yScaleFactor = yScaleFactor;
	}

	/**
	 * Configure the dragged view margin right applied when the dragged view is
	 * minimized.
	 * 
	 * @param topFragmentMarginRight
	 *            in pixels.
	 */
	public void setTopViewMarginRight(float topFragmentMarginRight) {
		this.topViewMarginRight = topFragmentMarginRight;
	}

	/**
	 * Configure the dragView margin bottom applied when the dragView is
	 * minimized.
	 * 
	 * @param topFragmentMarginBottom
	 */
	public void setTopViewMarginBottom(float topFragmentMarginBottom) {
		this.topViewMarginBottom = topFragmentMarginBottom;
	}

	/**
	 * Configure the dragged view height.
	 * 
	 * @param topFragmentHeight
	 *            in pixels
	 */
	public void setTopViewHeight(float topFragmentHeight) {
		if (topFragmentHeight > 0) {
			this.topViewHeight = topFragmentHeight;
			LayoutParams layoutParams = (LayoutParams) dragView
					.getLayoutParams();
			layoutParams.height = (int) topFragmentHeight;
			dragView.setLayoutParams(layoutParams);
		}
	}

	/**
	 * Configure the disabling of the alpha effect applied when the dragView is
	 * dragged horizontally.
	 * 
	 * @param enableHorizontalAlphaEffect
	 */
	public void setHorizontalAlphaEffectEnabled(
			boolean enableHorizontalAlphaEffect) {
		this.enableHorizontalAlphaEffect = enableHorizontalAlphaEffect;
	}

	/**
	 * Configure the DraggableListener notified when the view is minimized,
	 * maximized, closed to the right or closed to the left.
	 * 
	 * @param listener
	 */
	public void setDraggableListener(DraggableListener listener) {
		this.listener = listener;
	}

	/**
	 * To ensure the animation is going to work this method has been override to
	 * call postInvalidateOnAnimation if the view is not settled yet.
	 */
	@Override
	public void computeScroll() {
		if (!isInEditMode() && viewDragHelper.continueSettling(true)) {
			ViewCompat.postInvalidateOnAnimation(this);
		}
	}

	/**
	 * Maximize the custom view applying an animation to return the view to the
	 * initial position.
	 */
	public void maximize() {
		smoothSlideTo(SLIDE_TOP);
		notifyMaximizeToListener();
	}

	/**
	 * Minimize the custom view applying an animation to put the top fragment on
	 * the bottom right corner of the screen.
	 */
	public void minimize() {
		smoothSlideTo(SLIDE_BOTTOM);
		notifyMinimizeToListener();
	}

	/**
	 * Close the custom view applying an animation to close the view to the
	 * right side of the screen.
	 */
	public void closeToRight() {
		if (viewDragHelper.smoothSlideViewTo(dragView, dragView.getWidth(),
				getHeight() - dragView.getHeight())) {
			ViewCompat.postInvalidateOnAnimation(this);
			notifyCloseToRightListener();
		}
	}

	/**
	 * Close the custom view applying an animation to close the view to the left
	 * side of the screen.
	 */
	public void closeToLeft() {
		if (viewDragHelper.smoothSlideViewTo(dragView, -dragView.getWidth(),
				getHeight() - dragView.getHeight())) {
			ViewCompat.postInvalidateOnAnimation(this);
			notifyCloseToLeftListener();
		}
	}

	/**
	 * Checks if the top view is minimized.
	 * 
	 * @return true if the view is minimized.
	 */
	public boolean isMinimized() {
		return isDragViewAtBottom() && isDragViewAtRight();
	}

	/**
	 * Checks if the top view is maximized.
	 * 
	 * @return true if the view is maximized.
	 */
	public boolean isMaximized() {
		return isDragViewAtTop();
	}

	/**
	 * Checks if the top view closed at the right place.
	 * 
	 * @return true if the view is closed at right.
	 */
	public boolean isClosedAtRight() {
		return dragView.getLeft() >= getWidth();
	}

	/**
	 * Checks if the top view is closed at the left place.
	 * 
	 * @return true if the view is closed at left.
	 */
	public boolean isClosedAtLeft() {
		return dragView.getRight() <= 0;
	}

	/**
	 * Checks if the top view is closed at the right or left place.
	 * 
	 * @return true if the view is closed.
	 */
	public boolean isClosed() {
		return isClosedAtLeft() || isClosedAtRight();
	}

	/**
	 * Override method to intercept only touch events over the drag view and to
	 * cancel the drag when the action associated to the MotionEvent is equals
	 * to ACTION_CANCEL or ACTION_UP.
	 * 
	 * @param ev
	 *            captured.
	 * @return true if the view is going to process the touch event or false if
	 *         not.
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		final int action = MotionEventCompat.getActionMasked(ev);

		if (action == MotionEvent.ACTION_CANCEL
				|| action == MotionEvent.ACTION_UP) {
			viewDragHelper.cancel();
			return false;
		}
		boolean interceptTap = viewDragHelper.isViewUnder(dragView,
				(int) ev.getX(), (int) ev.getY());
		return viewDragHelper.shouldInterceptTouchEvent(ev) || interceptTap;
	}

	/**
	 * Override method to dispatch touch event to the dragged view.
	 * 
	 * @param ev
	 *            captured.
	 * @return true if the touch event is realized over the drag or second view.
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		viewDragHelper.processTouchEvent(ev);
		if (isClosed()) {
			return false;
		}
		boolean isDragViewHit = isViewHit(dragView, (int) ev.getX(),
				(int) ev.getY());
		boolean isSecondViewHit = isViewHit(secondView, (int) ev.getX(),
				(int) ev.getY());
		if (isMaximized()) {
			dragView.dispatchTouchEvent(ev);
		}
		return isDragViewHit || isSecondViewHit;
	}

	/**
	 * Override method to configure the dragged view and secondView layout
	 * properly.
	 * 
	 * @param changed
	 * @param left
	 * @param top
	 * @param right
	 * @param bottom
	 */
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		int newTop = this.topViewHeight == DEFAULT_TOP_VIEW_HEIGHT ? dragView
				.getMeasuredHeight() : (int) this.topViewHeight;

		dragView.layout(lastLeftPosition, lastTopPosition, lastLeftPosition
				+ dragView.getMeasuredWidth(), lastTopPosition + newTop);
		secondView.layout(0, lastTopPosition + newTop, right, lastTopPosition
				+ bottom);
	}

	/**
	 * Override method to map dragged view, secondView to view objects, to
	 * configure dradragged viewgView height and to initialize DragViewHelper.
	 */
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		if (!isInEditMode()) {
			dragView = findViewById(dragViewId);
			secondView = findViewById(secondViewId);
			setTopViewHeight(topViewHeight);
			initializeViewDragHelper();
		}

	}

	/**
	 * Configure the FragmentManager used to attach top and bottom Fragments to
	 * the view. The FragmentManager is going to be provided only by
	 * DraggablePanel view.
	 * 
	 * @param fragmentManager
	 */
	void setFragmentManager(FragmentManager fragmentManager) {
		this.fragmentManager = fragmentManager;
	}

	/**
	 * Attach one fragment to the dragged view.
	 * 
	 * @param topFragment
	 *            to be attached.
	 */
	void attachTopFragment(Fragment topFragment) {
		addFragmentToView(R.id.drag_view, topFragment);
	}

	/**
	 * Attach one fragment to the secondView.
	 * 
	 * @param bottomFragment
	 *            to be attached.
	 */
	void attachBottomFragment(Fragment bottomFragment) {
		addFragmentToView(R.id.second_view, bottomFragment);
	}

	/**
	 * Update the last top and left position when the view is dragged. This last
	 * positions are used to recreate the dragged view and secondView positions
	 * if the requestLayout method is called.
	 * 
	 * @param lastTopPosition
	 * @param lastLeftPosition
	 */
	void updateLastDragViewPosition(int lastTopPosition, int lastLeftPosition) {
		this.lastTopPosition = lastTopPosition;
		this.lastLeftPosition = lastLeftPosition;
	}

	/**
	 * Modify dragged view pivot based on the dragged view vertical position to
	 * simulate a horizontal displacement while the view is dragged..
	 */
	void changeDragViewPosition() {

		if (mMaximizedDragViewHeight == 0) {
			mMaximizedDragViewHeight = dragView.getHeight();
		}
		if (mMaximizedDragViewWidth == 0) {
			mMaximizedDragViewWidth = dragView.getWidth();
		}

		dragView.setPivotX(mMaximizedDragViewWidth - getDragViewMarginRight());
		dragView.setPivotY(mMaximizedDragViewHeight - getDragViewMarginBottom());
	}

	/**
	 * Modify secondView position to be always below dragged view.
	 */
	void changeSecondViewPosition() {
		secondView.setY(dragView.getBottom());
	}

	/**
	 * Modify dragged view scale based on the dragged view vertical position and
	 * the scale factor.
	 */
	void changeDragViewScale() {

		if (listener != null) {
			listener.onDraggableViewScaleChanged(1 - getVerticalDragOffset()
					/ xScaleFactor, 1 - getVerticalDragOffset() / yScaleFactor);
		}
	}

	/**
	 * Modify the background alpha if has been configured to applying an alpha
	 * effect when the view is dragged.
	 */
	void changeBackgroundAlpha() {
		Drawable background = getBackground();
		if (background != null) {
			int newAlpha = (int) (ONE_HUNDRED * (1 - getVerticalDragOffset()));
			background.setAlpha(newAlpha);
		}
	}

	/**
	 * Modify the second view alpha based on dragged view vertical position.
	 */
	void changeSecondViewAlpha() {
		secondView.setAlpha(1 - getVerticalDragOffset());
	}

	/**
	 * Modify dragged view alpha based on the horizontal position while the view
	 * is being horizontally dragged.
	 */
	void changeDragViewViewAlpha() {
		// if (enableHorizontalAlphaEffect) {
		// float alpha = 1 - getHorizontalDragOffset();
		// if (alpha == 0) {
		// alpha = 1;
		// }
		// dragView.setAlpha(alpha);
		// }
	}

	/**
	 * Check if dragged view is above the middle of the custom view.
	 * 
	 * @return true if dragged view is above the middle of the custom view or
	 *         false if is below.
	 */
	boolean isDragViewAboveTheMiddle() {
		int viewHeight = getHeight();
		float viewHeaderY = dragView.getY() + (dragView.getHeight() * HALF);
		return viewHeaderY < (viewHeight * HALF);
	}

	/**
	 * Check if dragged view is next to the left bound.
	 * 
	 * @return true if dragged view right position is behind the right half of
	 *         the custom view.
	 */
	boolean isNextToLeftBound() {
		return (dragView.getRight() - getDragViewMarginRight()) < getWidth()
				* HALF;
	}

	/**
	 * Check if dragged view is next to the right bound.
	 * 
	 * @return true if dragged view left position is behind the left quarter of
	 *         the custom view.
	 */
	boolean isNextToRightBound() {
		return (dragView.getLeft() - getDragViewMarginRight()) > getWidth()
				* QUARTER;
	}

	/**
	 * Check if dragged view is at the top of the custom view.
	 * 
	 * @return true if dragged view top position is equals to zero.
	 */
	boolean isDragViewAtTop() {
		return dragView.getTop() == ZERO;
	}

	/**
	 * Check if dragged view is at the right of the custom view.
	 * 
	 * @return true if dragged view right position is equals to custom view
	 *         width.
	 */
	boolean isDragViewAtRight() {
		return dragView.getRight() == getWidth();
	}

	/**
	 * Check if dragged view is at the bottom of the custom view.
	 * 
	 * @return true if dragged view bottom position is equals to custom view
	 *         height.
	 */
	boolean isDragViewAtBottom() {
		return dragView.getBottom() == getHeight();
	}

	/**
	 * Calculate if one position is above any view.
	 * 
	 * @param view
	 *            to analyze.
	 * @param x
	 *            position.
	 * @param y
	 *            position.
	 * @return true if x and y positions are below the view.
	 */
	private boolean isViewHit(View view, int x, int y) {
		int[] viewLocation = new int[2];
		view.getLocationOnScreen(viewLocation);
		int[] parentLocation = new int[2];
		this.getLocationOnScreen(parentLocation);
		int screenX = parentLocation[0] + x;
		int screenY = parentLocation[1] + y;
		return screenX >= viewLocation[0]
				&& screenX < viewLocation[0] + view.getWidth()
				&& screenY >= viewLocation[1]
				&& screenY < viewLocation[1] + view.getHeight();
	}

	/**
	 * Use FragmentManager to attach one fragment to one view using the viewId.
	 * 
	 * @param viewId
	 *            used to obtain the view.
	 * @param fragment
	 *            to be attached.
	 */
	private void addFragmentToView(final int viewId, final Fragment fragment) {
		fragmentManager.beginTransaction().replace(viewId, fragment).commit();
	}

	/**
	 * Initialize the viewDragHelper.
	 */
	private void initializeViewDragHelper() {
		draggableViewCallback = new DraggableViewCallback(this, dragView);
		viewDragHelper = ViewDragHelper.create(this, SENSITIVITY,
				draggableViewCallback);
	}

	/**
	 * Initialize XML attributes.
	 * 
	 * @param attrs
	 *            to be analyzed.
	 */
	private void initializeAttributes(AttributeSet attrs) {
		TypedArray attributes = getContext().obtainStyledAttributes(attrs,
				R.styleable.draggable_view);
		this.dragViewId = attributes.getResourceId(
				R.styleable.draggable_view_top_view_id, R.id.drag_view);
		this.secondViewId = attributes.getResourceId(
				R.styleable.draggable_view_bottom_view_id, R.id.second_view);
		this.topViewHeight = attributes.getDimension(
				R.styleable.draggable_view_top_view_height,
				DEFAULT_TOP_VIEW_HEIGHT);
		this.xScaleFactor = attributes.getFloat(
				R.styleable.draggable_view_top_view_x_scale_factor,
				DEFAULT_SCALE_FACTOR);
		this.yScaleFactor = attributes.getFloat(
				R.styleable.draggable_view_top_view_y_scale_factor,
				DEFAULT_SCALE_FACTOR);
		this.topViewMarginRight = attributes.getDimension(
				R.styleable.draggable_view_top_view_margin_right,
				DEFAULT_TOP_VIEW_MARGIN);
		this.topViewMarginBottom = attributes.getDimension(
				R.styleable.draggable_view_top_view_margin_bottom,
				DEFAULT_TOP_VIEW_MARGIN);
		this.enableHorizontalAlphaEffect = attributes
				.getBoolean(
						R.styleable.draggable_view_enable_minimized_horizontal_alpha_effect,
						DEFAULT_ENABLE_HORIZONTAL_ALPHA_EFFECT);
		attributes.recycle();

	}

	/**
	 * Realize an smooth slide to an slide offset passed as argument. This
	 * method is the base of maximize, minimize and close methods.
	 * 
	 * @param slideOffset
	 *            to apply
	 * @return true if the view is slided.
	 */
	private boolean smoothSlideTo(float slideOffset) {
		final int topBound = getPaddingTop();
		int y = (int) (topBound + slideOffset * getVerticalDragRange());

		// calculate an x position to animate to if we are sliding to the bottom
		// of the screen.
		int x = (int) (mMaximizedDragViewWidth * (1 - 1 / xScaleFactor));

		if (slideOffset == SLIDE_TOP) {
			x = 0;
		}

		if (viewDragHelper.smoothSlideViewTo(dragView, x, y)) {
			ViewCompat.postInvalidateOnAnimation(this);
			return true;
		}
		return false;
	}

	/**
	 * @return configured dragged view margin right configured.
	 */
	private float getDragViewMarginRight() {
		return topViewMarginRight;
	}

	/**
	 * @return configured dragged view margin bottom.
	 */
	private float getDragViewMarginBottom() {
		return topViewMarginBottom;
	}

	/**
	 * Calculate the dragged view left position normalized between 1 and 0.
	 * 
	 * @return absolute value between the dragged view left position divided by
	 *         custon view width
	 */
	private float getHorizontalDragOffset() {
		return (float) Math.abs(dragView.getLeft()) / (float) getWidth();
	}

	/**
	 * Calculate the dragged view top position normalized between 1 and 0.
	 * 
	 * @return dragged view top divided by vertical drag range.
	 */
	private float getVerticalDragOffset() {
		return dragView.getTop() / getVerticalDragRange();
	}

	/**
	 * Calculate the vertical drag range between the custom view and dragged
	 * view.
	 * 
	 * @return the difference between the custom view height and the dragged
	 *         view height.
	 */
	private float getVerticalDragRange() { 
		return getHeight() - (mMaximizedDragViewHeight / xScaleFactor);
	}

	/**
	 * Notify te view is maximized to the DraggableListener
	 */
	private void notifyMaximizeToListener() {
		if (listener != null) {
			listener.onMaximized();
		}
	}

	/**
	 * Notify te view is minimized to the DraggableListener
	 */
	private void notifyMinimizeToListener() {
		if (listener != null) {
			listener.onMinimized();
		}
	}

	/**
	 * Notify te view is closed to the right to the DraggableListener
	 */
	private void notifyCloseToRightListener() {
		if (listener != null) {
			listener.onClosedToRight();
		}
	}

	/**
	 * Notify te view is closed to the left to the DraggableListener
	 */
	private void notifyCloseToLeftListener() {
		if (listener != null) {
			listener.onClosedToLeft();
		}
	}

}