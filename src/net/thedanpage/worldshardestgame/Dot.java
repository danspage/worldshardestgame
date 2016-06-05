package net.thedanpage.worldshardestgame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Ellipse2D;

public class Dot {
	
	@Override
	public String toString() {
		return "Dot [pos1=" + pos1 + ", pos2=" + pos2 + ", x=" + x + ", y=" + y
				+ ", snapX=" + snapX + ", snapY=" + snapY + ", speed=" + speed
				+ ", moveToPos1=" + moveToPos1 + ", vertMovement="
				+ vertMovement + "]";
	}



	/** The first point that the dot will move between. */
	private Point pos1;
	
	/** The second point that the dot will move between. */
	private Point pos2;
	
	/** The current X position of the dot. */
	private double x;
	
	/** The current Y position of the dot. */
	private double y;
	
	/**
	 * The X coordinate of the dot, snapped to the grid of 40x40 tiles.
	 * snapX = x/40
	 */
	private int snapX;
	
	/**
	 * The Y coordinate of the dot, snapped to the grid of 40x40 tiles.
	 * snapY = y/40
	 */
	private int snapY;
	
	/** The speed the dot will move at. */
	private double speed;
	
	/** Determines if the dot should be moving to pos1 or pos2. */
	private boolean moveToPos1;
	
	/** True if the dot moves vertically, false if it moves horizontally. */
	private boolean vertMovement;
	
	public Dot() {
		this.x = 0;
		this.y = 0;
		this.snapX = 0;
		this.snapY = 0;
		this.pos1 = new Point(0, 0);
		this.pos2 = new Point(0, 0);
		this.speed = 1;
		this.moveToPos1 = true;
		this.vertMovement = false;
	}
	
	
	
	public Dot(int x, int y, Point pos1, Point pos2, double speed, boolean moveToPos1, boolean vertMovement) {
		this.x = x*40;
		this.y = y*40;
		this.snapX = x;
		this.snapY = y;
		this.pos1 = pos1;
		this.pos2 = pos2;
		this.speed = speed;
		this.moveToPos1 = moveToPos1;
		this.vertMovement = vertMovement;
	}
	
	
	
	public void draw(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillOval((int) (this.x - 10) + 20 , (int) (this.y - 10) + 20 + 22, 20, 20);
		g.setColor(Color.BLUE);
		g.fillOval((int) (this.x - 8) + 20 , (int) (this.y - 8) + 20 + 22, 16, 16);
	}
	
	
	
	public void update() {
		this.snapX = (int) (this.x/40);
		this.snapY = (int) (this.y/40);
		
		if (moveToPos1) {
			if (!this.vertMovement)
				this.x -= this.speed;
			else
				this.y -= this.speed;
			if (this.snapX < this.pos1.x || this.snapY < this.pos1.y) this.moveToPos1 = false;
		} else {
			if (!this.vertMovement)
				this.x += this.speed;
			else
				this.y += this.speed;
			if (this.snapX > this.pos2.x || this.snapY > this.pos2.y) this.moveToPos1 = true;
		}
	}
	
	
	
	public Point getPos1() {
		return this.pos1;
	}
	
	
	
	public Point getPos2() {
		return this.pos2;
	}
	
	
	
	public double getX() {
		return this.x;
	}
	
	
	
	public double getY() {
		return this.y;
	}
	
	
	
	public int getSnapX() {
		return this.snapX;
	}
	
	
	
	public int getSnapY() {
		return this.snapY;
	}
	
	
	
	public double getSpeed() {
		return this.speed;
	}
	
	
	
	public Ellipse2D getBounds() {
		return new Ellipse2D.Double((this.x - 10) + 20 , (this.y - 10) + 20, 20, 20);
	}
	
}
