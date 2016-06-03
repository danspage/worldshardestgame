package net.thedanpage.worldshardestgame;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import kuusisto.tinysound.Music;
import kuusisto.tinysound.Sound;
import kuusisto.tinysound.TinySound;

public class Game extends JPanel implements ActionListener {
	
	/** An instance of the game. */
	static Game window;
	
	/** The timer used for the game's clock. */
	Timer t = new Timer(5, this);
	
	/** The number of game ticks that have passed. Resets at 1000 ticks. */
	public static int ticks = 0;
	
	/** Used for logging information during the game. */
	public final static Logger logger = Logger.getLogger(Game.class.getName());
	
	static String logFilePath = System.getProperty("user.home")
    		  + "/worldshardestgame/logs/" +  new SimpleDateFormat("YY-MM-dd").format(new Date()) + ".log";

	private static final long serialVersionUID = 1L;
	
	/** The frame that the panel goes in. */
	static JFrame frame = new JFrame();
	
	/** The panel used to draw everything. */
	static JPanel panel = new JPanel();
	
	/** The enum instance used for switching the state of the game. */
	public static final int INTRO = 0,
							MAIN_MENU = 1,
							LEVEL_TITLE = 2,
							LEVEL = 3;
	
	/** The integer used for the game state. */
	public static int gameState = INTRO;
	
	/** Used for when the instructions should be shown. */
	boolean showIntro = false;
	
	
	/** This is the level that the player is on. */
	static int levelNum = 0;
	
	/** A player class, used to get information about the player. */
	Player player = new Player();
	
	/** The data of the current level. This should be given data in initLevel(). */
	public static GameLevel level = new GameLevel();
	
	/** Controls whether the game has sound or not. */
	public static boolean muted = false;
	
	/** Images for indicating volume. */
	Image volume_black = new ImageIcon(ClassLoader.getSystemResource(
			"net/thedanpage/worldshardestgame/resources/volume_black.png")).getImage();
	Image volume_black_mute = new ImageIcon(ClassLoader.getSystemResource(
			"net/thedanpage/worldshardestgame/resources/volume_black_mute.png")).getImage();
	Image volume_white = new ImageIcon(ClassLoader.getSystemResource(
			"net/thedanpage/worldshardestgame/resources/volume_white.png")).getImage();
	Image volume_white_mute = new ImageIcon(ClassLoader.getSystemResource(
			"net/thedanpage/worldshardestgame/resources/volume_white_mute.png")).getImage();
	
	/** Background music. */
	static Thread bgMusic = new Thread() {
		public void run() {
			TinySound.init();
			Music bgmusic = TinySound.loadMusic(ClassLoader.getSystemResource(
					"net/thedanpage/worldshardestgame/resources/music.wav"));
			bgmusic.play(true);
		}
	};
	
	Thread endIntro = new Thread() {
		public void run() {
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				TextFileWriter.appendToFile(logFilePath, e.getMessage());
			}
			gameState = MAIN_MENU;
			easyLog(logger, Level.INFO, "Game state set to MAIN_MENU");
		}
	};
	
	public static boolean doLogging = false;
	
	
	//Intro objects
	
	/** True if the intro text should move down. */
	private boolean fadeOutIntro = false;
	
	/** The opacity of the intro text. */
	private int introTextOpacity = 0;
	
	/** A whoosh sound. */
	Sound drone = TinySound.loadSound(ClassLoader.getSystemResource("net/thedanpage/worldshardestgame/resources/drone.wav"));
	
	/** A bell sound. */
	Sound bell = TinySound.loadSound(ClassLoader.getSystemResource("net/thedanpage/worldshardestgame/resources/bell.wav"));
	
	
	
	
	
	public void paintComponent(final Graphics g) {
		super.paintComponent(g);
		
		update(g);
		
		//Garbage disposal
		Toolkit.getDefaultToolkit().sync();
		g.dispose();
	
		//Start the timer
		t.start();
		
		ticks++;
		if (ticks > 999) ticks = 0;
	}
	
	
	
	
	
	/** Update the game and draw the graphics. */
	public void update(Graphics g) {
		
		final Graphics2D g2 = (Graphics2D) g;
		
		if (gameState == INTRO) {
			
			//Background
			g2.setPaint(new GradientPaint(0, 0, new Color(213, 213, 255), 0, 600, Color.WHITE));
			g2.fillRect(0, 0, 800, 600);
			
			if (introTextOpacity == 0 && !fadeOutIntro) {
				drone.play();
			}
			
			if (introTextOpacity < 255 && !fadeOutIntro) {
				introTextOpacity += 255/10;
				if (introTextOpacity > 255) introTextOpacity = 255;
			}
			
			if (introTextOpacity == 225) {
				new Thread() {
					public void run() {
						try {
							Thread.sleep(3500);
						} catch (InterruptedException e) {
							TextFileWriter.appendToFile(logFilePath, e.getMessage());
						}
						fadeOutIntro = true;
						bgMusic.start();
					}
				}.start();
			}
			
			if (fadeOutIntro) {
				if (introTextOpacity > 0) {
					introTextOpacity -= 255/20;
					if (introTextOpacity < 0) introTextOpacity = 0;
				}
			}
			
			g2.setFont(new Font("Tahoma", Font.BOLD, 50));
			g2.setColor(new Color(0, 0, 0, introTextOpacity));
			drawCenteredString("Made by Dan95363", 400, 250, g2);
				
			if (fadeOutIntro && introTextOpacity == 0 && !endIntro.isAlive()) {
				endIntro.start();
			}
			
			
			
			
			
		} else if (gameState == MAIN_MENU) {
				
			if (showIntro) {
				//Instructions
				g2.setFont(new Font("Tahoma", Font.BOLD, 20));
				g2.setColor(Color.BLACK);
				drawString("You are the red square. Avoid the blue circles and collect the\n" +
						   "yellow circles. Once you have collected all of the yellow\n" +
						   "circles, move to the green beacon to complete the level.\n" +
						   "Some levels consist of more than one beacon; the\n" +
						   "intermediary beacons act as checkpoints. You must complete\n" +
						   "all 30 levels in order to submit your score. Your score is a\n" +
						   "reflection of how many times you have died; the less, the better.", 30, 40, g2);
				
				g2.setColor(Color.BLUE);
				drawCenteredString("Press enter to continue", 400, 350, g2);
				
				if (Input.enter.isPressed) {
					showIntro = false;
					gameState = LEVEL_TITLE;
					easyLog(logger, Level.INFO, "Game state set to LEVEL_TITLE");
					
					player.reset();
					
					levelNum = 1;
					level.init(player, levelNum);
					
					//Wait 1.75 seconds then start the level.
					new Thread() {
						public void run() {
							try { Thread.sleep(1750); } catch (InterruptedException e) { TextFileWriter.appendToFile(logFilePath, e.getMessage()); }
							gameState = LEVEL;
							easyLog(logger, Level.INFO, "Game state set to LEVEL");
						}
					}.start();
				}
			} else {
				//Background
				g2.setPaint(new GradientPaint(0, 0, new Color(213, 213, 255), 0, 600, Color.WHITE));
				g2.fillRect(0, 0, 800, 600);
				
				//Draw and outline the title
				g2.setPaint(Color.BLACK);
				g2.setFont(new Font("SansSerif", Font.BOLD, 32));
				g2.drawString("The world's...", 40, 60);
				g2.setPaint(new Color(66, 117, 192));
				g2.setFont(new Font("SansSerif", Font.BOLD, 80));
				g2.drawString("HARDEST GAME", 40, 145);
				g2.setPaint(Color.BLACK);
				drawTextOutline("HARDEST GAME", 40, 145, 5, g2);
				
				g2.setFont(new Font("SansSerif", Font.BOLD, 60));
				
				//Gradient of "play game" text depending on the mouse location
				if (Input.mouseCoords.x > 284 && Input.mouseCoords.y < 343
						&& Input.mouseCoords.x < 515 && Input.mouseCoords.y > 192) {
					g2.setPaint(new GradientPaint(0, 175, new Color(220, 220, 220), 0, 255, new Color(190, 60, 60)));
				} else {
					g2.setPaint(new GradientPaint(0, 175, Color.WHITE, 0, 255, Color.RED));
				}
				
				//Draw and outline the "play game" text
				drawCenteredString("PLAY", 400, 255, g2);
				drawCenteredString("GAME", 400, 320, g2);
				g2.setColor(Color.BLACK);
				drawTextOutline("PLAY", 315, 255, 3, g2);
				drawTextOutline("GAME", 302, 320, 3, g2);
				
				//Click to start the first level
				if (Input.mousePressed && Input.mouseCoords.x > 304 && Input.mouseCoords.y < 323
						&& Input.mouseCoords.x < 515 && Input.mouseCoords.y > 192) {
					showIntro = true;
					bell.play();
				}
				
			}
			
			
			
			
			
		} else if (gameState == LEVEL) {
			
			if (levelNum != 0) {
				
				level.drawTiles(g);
				
				level.drawCoins(g);
				
				level.drawDots(g);
				level.updateDots();
				
				player.draw(g);
				player.update(level);
				
				g.setColor(Color.BLACK);
				g.fillRect(0, 0, 800, 22);
				
				g.setColor(Color.WHITE);
				g.setFont(new Font("Tahoma", Font.BOLD, 18));
				drawCenteredString("Deaths: " + player.getDeaths(), 400, 17, g);
				
				if (Input.mouseOnWindow && Input.mouseCoords.x <= 65 && Input.mouseCoords.y <= 22) {
					g.setColor(Color.LIGHT_GRAY);
					if (Input.mousePressed) {
						showIntro = false;
						gameState = MAIN_MENU;
						easyLog(logger, Level.INFO, "Game state set to MAIN_MENU");
						bell.play();
					}
				}
				g.drawString("MENU", 0, 17);
				
				if (muted) {
					g.drawImage(volume_white_mute, 760, -12, null);
				} else {
					g.drawImage(volume_white, 760, -12, null);
				}
			}
			
		} else if (gameState == LEVEL_TITLE) {
			
			//Background
			g2.setPaint(new GradientPaint(0, 0, new Color(213, 213, 255), 0, 600, Color.WHITE));
			g2.fillRect(0, 0, 800, 600);
			
			//Draw the title text
			g2.setFont(new Font("Tahoma", Font.BOLD, 48));
			g.setColor(Color.BLACK);
			int textY = 200;
			for (String s : level.getTitle().split("\n")) {
				drawCenteredString(s, 400, textY += g.getFontMetrics().getHeight(), g);
			}
			
		}
		
		if (gameState != LEVEL) {
			if (muted) {
				g.drawImage(volume_black_mute, 760, -12, null);
			} else {
				g.drawImage(volume_black, 760, -12, null);
			}
		}
		
	}
	
	
	
	
	
	public void actionPerformed(ActionEvent arg0) {
		repaint();
	}
	
	
	
	
	
	/** Draw a string centered on its x axis.
	 * 
	 * @param text
	 * 		the text to be drawn
	 * @param x
	 * 		the x coordinate of the text
	 * @param y
	 * 		the y coordinate of the text
	 * @param g
	 * 		the graphics the text will be drawn with
	 */
	void drawCenteredString(String s, int w, int h, Graphics g) {
		FontMetrics fm = g.getFontMetrics();
		int x = (w*2 - fm.stringWidth(s)) / 2;
		g.drawString(s, x, h);
	}
	
	
	
	
	
	/** Draw a string centered on its x axis.
	 * 
	 * @param text
	 * 		the text to be drawn
	 * @param x
	 * 		the x coordinate of the text
	 * @param y
	 * 		the y coordinate of the text
	 * @param g2
	 * 		the 2D graphics the text will be drawn with
	 */
	void drawCenteredString(String s, int w, int h, Graphics2D g2) {
		FontMetrics fm = g2.getFontMetrics();
		int x = (w*2 - fm.stringWidth(s)) / 2;
		g2.drawString(s, x, h);
	}
	
	
	
	
	
	/** Draw the outline of a string of text.
	 * 
	 * @param text
	 * 		the text to be drawn
	 * @param x
	 * 		the x coordinate of the text
	 * @param y
	 * 		the y coordinate of the text
	 * @param thickness
	 * 		the thickness of the outline
	 * @param g2
	 * 		the 2D graphics the text will be drawn with
	 */
	void drawTextOutline(String text, int x, int y, int thickness, Graphics2D g2) {
		TextLayout tl = new TextLayout(text, g2.getFont(), new FontRenderContext(null,false,false));
		AffineTransform textAt = new AffineTransform();
		textAt.translate(x, y);
		g2.setStroke(new BasicStroke(thickness));
		g2.draw(tl.getOutline(textAt));
		g2.setStroke(new BasicStroke());
	}
	
	
	
	
	
	/** Draw a string, with the use of \n implemented.
	 * 
	 * @param text
	 * 		the text to be drawn
	 * @param x
	 * 		the x coordinate of the text
	 * @param y
	 * 		the y coordinate of the text
	 * @param g
	 * 		the graphics the text will be drawn with
	 */
	void drawString(String text, int x, int y, Graphics g) {
	    for (String line : text.split("\n"))
	        g.drawString(line, x, y += g.getFontMetrics().getHeight());
	}
	
	
	
	
	
	/**
	 * Pause the current thread for a certain number of seconds.
	 * 
	 * @param sec
	 * 		The number of seconds for the thread to wait
	 * @throws InterruptedException
	 */
	void wait(double sec) throws InterruptedException {
		Thread.sleep((int) sec * 1000);
	}
	
	
	
	
	
	/** Resets all of the player's variables.*/
	void resetPlayer() {
		player = new Player();
	}
	
	
	
	
	
	/**
	 * Convert an exception to a String with full stack trace
	 * 
	 * @param ex
	 *            the exception
	 * @return A string with the full stacktrace error text
	 */
	public static String getStringFromStackTrace(Throwable ex) {
		if (ex == null) {
			return "";
		}
		StringWriter str = new StringWriter();
		PrintWriter writer = new PrintWriter(str);
		try {
			ex.printStackTrace(writer);
			return str.getBuffer().toString();
		} finally {
			try {
				str.close();
				writer.close();
			} catch (IOException e) {
				// ignore
			}
		}
	}
	
	
	
	
	
	/**
	 * Easily log a string of text, and write it to the log file
	 * 
	 * @param logger
	 * 		The logger for the string to be logged with
	 * @param level
	 * 		The level of the logger
	 * @param s
	 * 		The string of text to be logged
	 */
	static void easyLog(Logger logger, Level level, String s) {
		if (doLogging) {
			logger.setLevel(level);
			
			if (level == Level.CONFIG) logger.config(s);
			else if (level == Level.FINE) logger.fine(s);
			else if (level == Level.FINER) logger.finer(s);
			else if (level == Level.FINEST) logger.finest(s);
			else if (level == Level.INFO) logger.info(s);
			else if (level == Level.SEVERE) logger.severe(s);
			else if (level == Level.WARNING) logger.warning(s);
			
			else {
				logger.setLevel(Level.WARNING);
				logger.warning("Logging error");
			}
			
			TextFileWriter.appendToFile(logFilePath, new SimpleDateFormat(
					"MMM dd, YYYY h:mm:ss a").format(new Date())
					+ " net.thedanpage.worldshardestgame easyLog\n" + level + ": " + s);
		}
	}
	
	
	
	
	
	public static void main(String[] args) {
		int option = JOptionPane.showConfirmDialog(null, "Would you like to enable logging?", "Setup", JOptionPane.YES_NO_OPTION);
		if (option == JOptionPane.YES_OPTION) doLogging = true;
		else doLogging = false;
		
		if (doLogging) {
			
			//Create directory for logs if it does not exist
			if (!new File(System.getProperty("user.home") + "/worldshardestgame/logs").isDirectory()) {
				new File(System.getProperty("user.home") + "/worldshardestgame/logs").mkdirs();
			}
			
			if (new File(logFilePath + ".zip").exists()) {
				LogZipper.unzip(
					System.getProperty("user.home") + "/worldshardestgame/logs", logFilePath + ".zip");
				new File(logFilePath + ".zip").delete();
			}
			
			try {
				if (new File(logFilePath).exists() && new BufferedReader(new FileReader(logFilePath)).readLine() != null) {
					TextFileWriter.appendToFile(logFilePath, "\n");
				}
			} catch (IOException e) {
				easyLog(logger, Level.WARNING, getStringFromStackTrace(e));
			}
		}
		
		easyLog(logger, Level.INFO, "Starting The World's Hardest Game");
		
		TinySound.init();
		easyLog(logger, Level.INFO, "TinySound initialized");
		
		if (muted) TinySound.setGlobalVolume(0);
		
		window = new Game();

		frame.setTitle("World's Hardest Game");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(new Dimension(800, 622));
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.add(window);
		
		frame.setIconImage(new ImageIcon(ClassLoader.getSystemResource("net/thedanpage/worldshardestgame/resources/favicon.png")).getImage());
		frame.setVisible(true);
		
		Input.init();
		easyLog(logger, Level.INFO, "Input initialized");
		
	}
}
