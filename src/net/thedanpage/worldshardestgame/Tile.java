package net.thedanpage.worldshardestgame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Tile {
	
	/** X coordinate of the tile, multiplied by 20 to draw on the screen. */
	private int x = 0;
	
	/** Y coordinate of the tile, multiplied by 20 to draw on the screen. */
	private int y = 0;
	
	/** X coordinate of the player, snapped to the grid of 40x40 squares. */
	private int snapX = 0;
	
	/** Y coordinate of the player, snapped to the grid of 40x40 squares. */
	private int snapY = 0;
	
	/** Determines what type of tile it is.
	 * 0 = background
	 * 1 = floor tile
	 * 2 = checkpoint
	 * 3 = goal */
	private int type = 0;
	
	
	
	/** Constructor of the Tile class */
	public Tile() {
		this.x = 0;
		this.y = 0;
		this.snapX = x/40;
		this.snapY = y/40;
		this.type = 0;
	}
	
	
	
	/** Constructor of the Tile class */
	public Tile(int x, int y, int t) {
		this.x = x;
		this.y = y;
		this.snapX = x/40;
		this.snapY = y/40;
		this.type = t;
	}
	
	
	
	public void draw(GameLevel level, Graphics g) {
		if (this.getType() != 0) {
			//Floor Tile
			if (this.getType() == 1) {
			
				if (this.getSnapX() % 2 == 0) {
					if (this.getSnapY() % 2 == 0) {
						g.setColor(new Color(230, 230, 255));
					} else {
						g.setColor(Color.WHITE);
					}
				} else if (this.getSnapX() % 2 == 1) {
					if (this.getSnapY() % 2 == 0) {
						g.setColor(Color.WHITE);
					} else {
						g.setColor(new Color(230, 230, 255));
					}
				}
				g.fillRect(this.getX(), this.getY() + 22, 40, 40);
			
			//Checkpoint
			} else if (this.getType() == 2) {
				g.setColor(new Color(181, 254, 180));
				g.fillRect(this.getX(), this.getY() + 22, 40, 40);
				
			//Goal
			} else if (this.getType() == 3) {
				g.setColor(new Color(181, 254, 180));
				g.fillRect(this.getX(), this.getY() + 22, 40, 40);
			}
		}
	}
	
	
	
	public Rectangle getBounds() {
		return new Rectangle(this.x, this.y, 39, 39);
	}
	
	
	
	public int getX() {
		return this.x;
	}
	
	
	
	public int getY() {
		return this.y;
	}
	
	
	
	public int getSnapX() {
		return this.snapX;
	}
	
	
	
	public int getSnapY() {
		return this.snapY;
	}
	
	
	
	public int getType() {
		return this.type;
	}



	@Override
	public String toString() {
		return "Tile [snapX=" + snapX + ", snapY=" + snapY + ", type=" + type
				+ "]";
	}

}
