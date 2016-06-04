package net.thedanpage.worldshardestgame;

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

}
