package net.thedanpage.worldshardestgame;

import java.awt.Point;

public class Input {
	
	/** The coordinates of the mouse relative to the JFrame. */
	static Point mouseCoords = new Point(0, 0);
	
	/** True/false depending on if the mouse is pressed. */
	static boolean mousePressed = false;
	
	/** Used to easily store information of keys. */
	public static class Key {
		public boolean isPressed;
		public int numTimesPressed;

		public Key() {
			isPressed = false;
			numTimesPressed = 0;
		}
	}

	public static Key left = new Key();
	public static Key right = new Key();
	public static Key up = new Key();
	public static Key down = new Key();
	public static Key enter = new Key();
	public static Key escape = new Key();
	public static Key space = new Key();
}
