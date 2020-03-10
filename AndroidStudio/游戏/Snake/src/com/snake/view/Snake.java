package com.snake.view;

import android.graphics.Point;

public class Snake {
	private Point before = new Point();;
	private Point point = new Point();
	public Point getBefore() {
		return before;
	}
	public void setBefore(Point before) {
		this.before.x = before.x;
//		this.before = before ;
		this.before.y = before.y;
	}
	public Point getPoint() {
		return point;
	}
	public void setPoint(Point point) {
		this.point.x = point.x;
		this.point.y = point.y;
//		this.point = point;
	}
//	public void setPoint(int x, int y){
//		point.x = x;
//		point.y = y;
//	}
}
