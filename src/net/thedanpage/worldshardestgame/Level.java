package net.thedanpage.worldshardestgame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class Level {
	
	/** Spawn point of the level. */
	private Point spawnPoint;
	
	/** The ID of the level. The first level has an ID of 1. */
	private int id;
	
	/** The message that is displayed before the level. */
	private String levelTitle;
	
	/** A list of all of the level's tiles. */
	private ArrayList<Tile> tileMap;
	
	/** A list of all of the level's dots. */
	public ArrayList<Dot> dots;
	
	/** A list of all of the level's coins. **/
	public ArrayList<Coin> coins;
	
	/** The area of the level, not including background tiles. */
	Area levelArea;
	
	public Level() {
		this.levelArea = new Area();
		this.tileMap = new ArrayList<Tile>();
		this.dots = new ArrayList<Dot>();
		this.spawnPoint = new Point(20, 20);
		this.id = -1;
		this.levelTitle = "";
	}
	
	public Level(Point spawn, int id) {
		this.levelArea = new Area();
		this.tileMap = new ArrayList<Tile>();
		this.dots = new ArrayList<Dot>();
		this.spawnPoint = spawn;
		this.id = id;
		this.levelTitle = "";
	}
	
	public Point getSpawnPoint() {
		return this.spawnPoint;
	}
	
	public int getID() {
		return this.id;
	}
	
	public ArrayList<Tile> getTileMap() {
		return this.tileMap;
	}
	
	public String getTitle() {
		return this.levelTitle;
	}
	
	
	
	/** Draw the tiles based on a text file in the maps package. */
	public void drawTiles(Graphics g) {
		
		Graphics2D g2 = (Graphics2D) g;
		
		try {
			/*for (Tile t : this.tileMap) {
				//Background
				if (t.getType() == 0) {
					g.setColor(new Color(180, 181, 254));
					g.fillRect(t.getX(), t.getY(), 40, 40);
				}
			}*/
			
			g.setColor(new Color(180, 181, 254));
			g.fillRect(0, 0, 800, 600);
			
			//Border around level
			g2.setColor(Color.BLACK);
			g2.fill(this.levelArea);
			
			for (Tile t : this.tileMap) {
				
				if (t.getType() != 0) {
					//Floor Tile
					if (t.getType() == 1) {
					
						if (t.getSnapX() % 2 == 0) {
							if (t.getSnapY() % 2 == 0) {
								g.setColor(new Color(230, 230, 255));
							} else {
								g.setColor(Color.WHITE);
							}
						} else if (t.getSnapX() % 2 == 1) {
							if (t.getSnapY() % 2 == 0) {
								g.setColor(Color.WHITE);
							} else {
								g.setColor(new Color(230, 230, 255));
							}
						}
						g.fillRect(t.getX(), t.getY(), 40, 40);
					
					//Checkpoint
					} else if (t.getType() == 2) {
						g.setColor(new Color(181, 254, 180));
						g.fillRect(t.getX(), t.getY(), 40, 40);
						
					//Goal
					} else if (t.getType() == 3) {
						g.setColor(new Color(181, 254, 180));
						g.fillRect(t.getX(), t.getY(), 40, 40);
					}
				}
				
			}
		} catch (Exception e) {
			System.out.println("File not found.");
			e.printStackTrace();
		}
	}
	
	
	
	public void drawDots(Graphics g) {
		for (Dot dot : this.dots) dot.draw(g);
	}
	
	
	
	public void updateDots() {
		if (this.dots != null)
			for (Dot dot : this.dots) dot.update();
	}
	
	
	
	public void drawCoins(Graphics g) {
		if (this.coins != null)
			for (Coin coin : this.coins) coin.draw(g);
	}
	
	
	
	public boolean allCoinsCollected() {
		if (this.coins != null) {
			for (Coin coin : this.coins) {
				if (!coin.collected) return false;
			}
		}
		return true;
	}
	
	
	
	/**
	 * Load the current level data from
	 * net.thedanpage.worldshardestgame.resources.maps
	 */
	public void init(Player player, int levelNum) {
		
		//Clears the tile data
		this.tileMap = new ArrayList<Tile>();
		
		//Clears the dot data
		this.dots = new ArrayList<Dot>();
		
		//Clears the coin data
		this.coins = new ArrayList<Coin>();
		
		try {
			this.spawnPoint = new Point(
							Integer.parseInt(PropLoader
									.loadProperty("spawn_point",
											"net/thedanpage/worldshardestgame/resources/maps/level_" + levelNum + ".properties")
									.split(",")[0]) * 40 + 20,
							Integer.parseInt(PropLoader
									.loadProperty("spawn_point",
											"net/thedanpage/worldshardestgame/resources/maps/level_" + levelNum + ".properties")
									.split(",")[1]) * 40 + 20);
			
			this.id = Integer.parseInt(PropLoader.loadProperty("level_id",
					"net/thedanpage/worldshardestgame/resources/maps/level_"
							+ levelNum + ".properties"));
			
			this.levelTitle = PropLoader.loadProperty("level_title", 
					"net/thedanpage/worldshardestgame/resources/maps/level_"
							+ levelNum + ".properties").toString();
			
			String coinData = null;
			
			if (PropLoader.loadProperty("coins", 
					"net/thedanpage/worldshardestgame/resources/maps/level_"
							+ levelNum + ".properties") != "null") {
				coinData = PropLoader.loadProperty("coins", 
						"net/thedanpage/worldshardestgame/resources/maps/level_"
								+ levelNum + ".properties");
			}
			
			if (coinData != null) {
				if (coinData.contains("-")) {
					
					String[] coins = coinData.split("-");
					for (String s : coins) this.coins.add(new Coin(Integer.parseInt(s.split(",")[0]),Integer.parseInt(s.split(",")[1])));
					
				} else this.coins.add(new Coin(Integer.parseInt(coinData.split(",")[0]) * 40,Integer.parseInt(coinData.split(",")[1]) * 40));
			}
			
			//Retrieves the tile data
			InputStreamReader isr = new InputStreamReader(ClassLoader
					.getSystemResource(
							"net/thedanpage/worldshardestgame/resources/maps/level_"
									+ levelNum + ".txt").openStream());
			String content = "";
			Scanner scanner = new Scanner(isr);
			content = scanner.useDelimiter("\\Z").next();
			scanner.close();
			content = content.replaceAll("\n", "");

			for (int i = 0; i < content.length(); i++) {
				if (i > 299)
					break;
				else
					this.tileMap.add(new Tile((i % 20) * 40, (i / 20) * 40,
							Character.getNumericValue(content.charAt(i))));
			}

			System.out.println("Level " + Game.levelNum + "\n\n" + content + "\n");
			
			//Retrieves the dot data
			int lineNum = 0;
			String line = "";
			while ((Files.readAllLines(Paths.get(ClassLoader.getSystemResource(
					"net/thedanpage/worldshardestgame/resources/maps/level_"
							+ levelNum + ".txt").toURI())).get(19 + lineNum)) != null) {
				line = Files.readAllLines(
						Paths.get(getClass()
								.getClassLoader()
								.getResource(
										"net/thedanpage/worldshardestgame/resources/maps/level_"
												+ levelNum + ".txt").toURI())).get(19 + lineNum);
				String[] dotData = line.replaceAll(" ", "").split("-");
				this.dots.add(new Dot(
							Integer.parseInt(dotData[0]),
							Integer.parseInt(dotData[1]),
							new Point(Integer.parseInt(dotData[2].split(",")[0]),
									  Integer.parseInt(dotData[2].split(",")[1])),
							new Point(Integer.parseInt(dotData[3].split(",")[0]),
									  Integer.parseInt(dotData[3].split(",")[1])),
							Double.parseDouble(dotData[4]),
							Boolean.parseBoolean(dotData[5]),
							Boolean.parseBoolean(dotData[6])
						));
				lineNum++;
			}
		} catch (IndexOutOfBoundsException e) {
			System.out.println("\nDot initialization completed.\n");
		} catch (NullPointerException e) {
			System.out.println("ERROR: Null pointer!");
		} catch (Exception e) {
			if (e.getClass().getName() != "java.lang.IndexOutOfBoundsException" &&
					e.getClass().getName() != "java.lang.NullPointerException") {
				System.out.println("ERROR: Map unable to be loaded.");
			}
		}
		
		this.levelArea = new Area();
		for (Tile t : this.tileMap) {
			if (t.getType() != 0) {
				this.levelArea.add(new Area(
						new Rectangle(t.getX() - 3, t.getY() - 3, 46, 46)));
			}
			System.out.println("Adding tile " + this.tileMap.indexOf(t) +", type " + t.getType());
		}
		
		System.out.println("\nNumber of tiles: " + this.tileMap.size());
		
		System.out.println("\n");
		
		player.respawn(this);
	}

}