package net.thedanpage.worldshardestgame;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
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

	private static final long serialVersionUID = 1L;
	
	/** The frame that the panel goes in. */
	static JFrame frame = new JFrame();
	
	/** The panel used to draw everything. */
	static JPanel panel = new JPanel();
	
	/** The integer used for the game state. */
	public static int gameState;
	
	/** The enum instance used for switching the state of the game. */
	public static final int INTRO = 0,
							MAIN_MENU = 1,
							LEVEL_TITLE = 2,
							LEVEL = 3;
	
	/** Used for when the instructions should be shown. */
	boolean showIntro = false;
	
	
	/** This is the level that the player is on. */
	static int levelNum = 0;
	
	/** A player class, used to get information about the player. */
	Player player = new Player();
	
	/** The data of the current level. This should be given data in initLevel(). */
	public static Level level = new Level();
	
	/** Controls whether the game has sound or not. */
	public static boolean muted = false;
	
	/** Image for indicating volume. */
	ImageIcon speaker = new ImageIcon(getClass().getClassLoader().getResource("net/thedanpage/worldshardestgame/resources/volume.png"));
	
	/** Background music. */
	static Thread bgMusic = new Thread() {
		public void run() {
			TinySound.init();
			Music bgmusic = TinySound.loadMusic(Game.class.getResource(
					"/net/thedanpage/worldshardestgame/resources/music.wav"));
			bgmusic.play(true);
		}
	};
	
	
	//Intro objects
	
	/** True if the intro text should move down. */
	private boolean fadeOutIntro = false;
	
	/** The opacity of the intro text. */
	private int introTextOpacity = 0;
	
	/** A whoosh sound. */
	Sound drone = TinySound.loadSound(getClass().getClassLoader().getResource("net/thedanpage/worldshardestgame/resources/drone.wav"));
	
	/** A bell sound. */
	Sound bell = TinySound.loadSound(getClass().getClassLoader().getResource("net/thedanpage/worldshardestgame/resources/bell.wav"));
	
	
	
	
	
	public void paintComponent(final Graphics g) {
		super.paintComponent(g);
		
		update(g);
		
		//Garbage disposal
		Toolkit.getDefaultToolkit().sync();
		g.dispose();
	
		//Start the timer
		t.start();
	}
	
	
	
	
	
	/** Update the game and draw the graphics. */
	public void update(Graphics g) {
		
		final Graphics2D g2 = (Graphics2D) g;

		switch (gameState) {
		
		case INTRO:
			
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
							e.printStackTrace();
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
			
			if (fadeOutIntro && introTextOpacity == 0) {
				new Thread() {
					public void run() {
						try {
							Thread.sleep(1500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						gameState = MAIN_MENU;
					}
				}.start();
			}
			
			
			
			
			
		case MAIN_MENU:
			
			if (gameState != INTRO) {
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
						
						levelNum = 1;
						level.init(player, levelNum);
						
						//Wait 1.75 seconds then start the level.
						new Thread() {
							public void run() {
								try { Thread.sleep(1750); } catch (InterruptedException e) { e.printStackTrace(); }
								gameState = LEVEL;
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
			}
			
			
			
			
			
		case LEVEL:
			
			if (levelNum != 0) {
				
				level.drawTiles(g);
				
				level.drawCoins(g);
				
				level.drawDots(g);
				level.updateDots();
				
				player.draw(g);
				player.update(level);
				
				g.setFont(new Font("Tahoma", Font.BOLD, 36));
				g.setColor(Color.BLACK);
				drawCenteredString("Deaths: " + player.getDeaths(), 400, 45, g);
			}
			
		case LEVEL_TITLE:
			
			if (gameState == LEVEL_TITLE) {
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
			
		}
		
		g.drawImage(speaker.getImage(), 752, 0, null);
		if (muted) {
			g2.setStroke(new BasicStroke(3));
			g.setColor(Color.RED);
			g.drawLine(762, 38, 790, 10);
			g2.setStroke(new BasicStroke());
		}
		
	}
	
	
	
	
	
	public void actionPerformed(ActionEvent arg0) {
		repaint();
	}
	
	
	
	
	
	/** Draw a string centered on its x axis. */
	void drawCenteredString(String s, int w, int h, Graphics g) {
		FontMetrics fm = g.getFontMetrics();
		int x = (w*2 - fm.stringWidth(s)) / 2;
		g.drawString(s, x, h);
	}
	
	
	
	
	
	/** Draw a string centered on its x axis. */
	void drawCenteredString(String s, int w, int h, Graphics2D g2) {
		FontMetrics fm = g2.getFontMetrics();
		int x = (w*2 - fm.stringWidth(s)) / 2;
		g2.drawString(s, x, h);
	}
	
	
	
	
	
	/** Draw the outline of a string of text. */
	void drawTextOutline(String text, int x, int y, int thickness, Graphics2D g2) {
		TextLayout tl = new TextLayout(text, g2.getFont(), new FontRenderContext(null,false,false));
		AffineTransform textAt = new AffineTransform();
		textAt.translate(x, y);
		g2.setStroke(new BasicStroke(thickness));
		g2.draw(tl.getOutline(textAt));
		g2.setStroke(new BasicStroke());
	}
	
	
	
	
	
	/** Draw a string, with the use of \n implemented. */
	void drawString(String text, int x, int y, Graphics g) {
	    for (String line : text.split("\n"))
	        g.drawString(line, x, y += g.getFontMetrics().getHeight());
	}
	
	
	
	
	
	/**
	 * Pause the current thread for a certain number of seconds.
	 * 
	 * @throws InterruptedException
	 */
	void wait(double sec) throws InterruptedException {
		Thread.sleep((int) sec * 1000);
	}
	
	
	
	
	
	/** Resets all of the player's variables. */
	void resetPlayer() {
		player = new Player();
	}
	
	
	
	
	
	public static void main(String[] args) {
		
		TinySound.init();
		//bgMusic.start();
		
		window = new Game();

		frame.setTitle("World's Hardest Game");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(new Dimension(800, 600));
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.add(window);
		
		frame.addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseExited(MouseEvent e) {
			}

			public void mousePressed(MouseEvent e) {
				Input.mousePressed = true;
				
				if (e.getX() >= 752 && e.getY() <= 48) {
					if (!muted) {
						TinySound.setGlobalVolume(0);
						muted = true;
						System.out.println("Muted");
					} else {
						TinySound.setGlobalVolume(1);
						muted = false;
						System.out.println("Unmuted");
					}
				}
			}

			public void mouseReleased(MouseEvent e) {
				Input.mousePressed = false;
			}
		});
		
		frame.addMouseMotionListener(new MouseMotionListener() {

			public void mouseDragged(MouseEvent e) {
			}

			public void mouseMoved(MouseEvent e) {
				Input.mouseCoords = e.getPoint();
			}
			
		});
		
		frame.addKeyListener(new KeyListener() {

			public void keyTyped(KeyEvent e) {
			}

			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					Input.left.isPressed = true;
					Input.left.numTimesPressed++;
				}
				if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					Input.right.isPressed = true;
					Input.right.numTimesPressed++;
				}
				if (e.getKeyCode() == KeyEvent.VK_UP) {
					Input.up.isPressed = true;
					Input.up.numTimesPressed++;
				}
				if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					Input.down.isPressed = true;
					Input.down.numTimesPressed++;
				}
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					Input.enter.isPressed = true;
					Input.enter.numTimesPressed++;
				}
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					Input.escape.isPressed = true;
					Input.escape.numTimesPressed++;
				}
				if (e.getKeyCode() == KeyEvent.VK_SPACE) {
					Input.space.isPressed = true;
					Input.space.numTimesPressed++;
				}
			}

			public void keyReleased(KeyEvent e) {
				
				if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					Input.left.isPressed = false;
				}
				if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					Input.right.isPressed = false;
				}
				if (e.getKeyCode() == KeyEvent.VK_UP) {
					Input.up.isPressed = false;
				}
				if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					Input.down.isPressed = false;
				}
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					Input.enter.isPressed = false;
				}
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					Input.escape.isPressed = false;
				}
				if (e.getKeyCode() == KeyEvent.VK_SPACE) {
					Input.space.isPressed = false;
				}
			}
		});
		
		frame.setVisible(true);
		
	}
}
