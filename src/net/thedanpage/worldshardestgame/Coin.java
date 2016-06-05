package net.thedanpage.worldshardestgame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Ellipse2D;

public class Coin {
	
	@Override
	public String toString() {
		return "Coin [x=" + x + ", y=" + y + ", snapX=" + snapX + ", snapY="
				+ snapY + ", collected=" + collected + "]";
	}

	private int x;
	private int y;
	private double snapX;
	private double snapY;
	public boolean collected;
	
	public Coin() {
		this.x = 0;
		this.y = 0;
		this.snapX = 0;
		this.snapY = 0;
		this.collected = false;
	}
	
	public Coin(int x, int y) {
		this.x = x;
		this.y = y;
		this.snapX = x / 40;
		this.snapY = y / 40;
		this.collected = false;
	}
	
	public void draw(Graphics g) {
		if (!this.collected) {
			g.setColor(Color.BLACK);
			g.fillOval(this.x + 10, this.y + 10 + 22, 20, 20);
			g.setColor(Color.YELLOW);
			g.fillOval(this.x + 12, this.y + 12 + 22, 16, 16);
		}
	}
	
	public void update() {
		
		
		
	}
	
	public int getX() {
		return this.x;
	}
	
	public int getY() {
		return this.y;
	}
	
	public int getSnapX() {
		return (int) this.snapX;
	}
	
	public int getSnapY() {
		return (int) this.snapY;
	}
	
	public Ellipse2D getBounds() {
		return new Ellipse2D.Double(this.x + 10, this.y + 10, 20, 20);
	}
	
}
