package net.thedanpage.worldshardestgame;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dialog;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
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
import net.thedanpage.worldshardestgame.controllers.Controller;
import net.thedanpage.worldshardestgame.controllers.ExampleController;
import net.thedanpage.worldshardestgame.controllers.GeneticController;

import static java.lang.Thread.currentThread;
import static java.lang.Thread.sleep;

public class Game extends JPanel implements ActionListener {

    /** An instance of the game. */
    private static Game game;

    /** The timer used for the game's clock. */
    private Timer t = new Timer(5, this);

    /** Used for logging information during the game. */
    public final static Logger logger = Logger.getLogger(Game.class.getName());

    static String logFilePath = System.getProperty("user.home")
            + "/worldshardestgame/logs/" +  new SimpleDateFormat("YY-MM-dd").format(new Date()) + ".log";

    private static final long serialVersionUID = 1L;

    /** The frame that the panel goes in. */
    static JFrame frame = new JFrame();

    /** The enum instance used for switching the state of the game. */
    static final int INTRO = 0, MAIN_MENU = 1, LEVEL_TITLE = 2, LEVEL = 3;

    /** The integer used for the game state. */
    static int gameState = MAIN_MENU;

    /** Used for when the instructions should be shown. */
    private boolean showIntro = false;

    public Controller controller;

    /** This is the level that the player is on. */
    static int levelNum = 0;

    /** A player class, used to get information about the player. */
    private List<Player> population = new ArrayList<>();

    private int populationSize = 10;
    private int playerMoveCount = 5;

    private int generation = 1;

    /** The data of the current level. This should be given data in initLevel(). */
    public static GameLevel level = new GameLevel();

    /** Controls whether the game has sound or not. */
    static boolean muted = false;

    /** Background music. */
    static Thread bgMusic = new Thread() {
        public void run() {
            TinySound.init();
            Music bgmusic = TinySound.loadMusic(ClassLoader.getSystemResource(
                    "net/thedanpage/worldshardestgame/resources/music.ogg"));
            bgmusic.play(true);
        }
    };

    Thread endIntro = new Thread() {
        public void run() {
            try {
                sleep(1500);
            } catch (InterruptedException e) {
                TextFileWriter.appendToFile(logFilePath, e.getMessage());
            }
            gameState = MAIN_MENU;
            easyLog(logger, Level.INFO, "Game state set to MAIN_MENU");
        }
    };

    static boolean doLogging = false;

    private static int totalLevels = 0;


    public Game(Controller controller) {
        this.controller = controller;

        intializePopulation();
    }

    public GameLevel getLevel() {
        return level;
    }

    public void updateFitness() {
        for(var player : population) {
            player.fitness = calculateFitness(player);
        }
    }

    private double calculateFitness(Player player) {
        return level.getDistanceToGoal(player);
    }


    public void paintComponent(final Graphics g) {
        super.paintComponent(g);

        update(g);
        render(g);

        //Start the timer
        t.start();

        Toolkit.getDefaultToolkit().sync();
    }



    /** Update the game.
     *
     * @param g
     * */
    public void update(Graphics g) {
        if (gameState == MAIN_MENU) {
            showIntro = false;
            gameState = LEVEL_TITLE;
            easyLog(logger, Level.INFO, "Game state set to LEVEL_TITLE");
            levelNum = 1;

            for (var player : population) {
                player.reset();
                level.init(player, levelNum);
            }

            gameState = LEVEL;
            easyLog(logger, Level.INFO, "Game state set to LEVEL");
        }
    }

    /** Draw the game's graphics.
     *
     * @param g
     */
    private void render(Graphics g) {
        if (levelNum == 0) {
            g.dispose();
            return;
        }
        level.drawTiles(g);
        level.drawCoins(g);
        level.drawDots(g);
        level.updateDots();
        var deadPlayerCount = 0;
        for (var player : population) {
            if(player.isDead()) deadPlayerCount++;
            player.draw(g);
            player.update(this, controller);
        }

        if(deadPlayerCount == populationSize) {
            evaluateGeneration();
        }

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 800, 22);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Tahoma", Font.BOLD, 18));
        drawRightJustifiedString("Generation: " + this.generation, 750, 17, g);
        drawCenteredString(levelNum + "/" + totalLevels, 400, 17, g);
        g.dispose();
    }

    public void evaluateGeneration() {
        this.generation++;
        updateFitness();
        if(this.generation % 5 == 0) {
            this.playerMoveCount += 5;
        }

        var bestCandidates = selection();
        var newPopulation = mutate(bestCandidates);
        population = newPopulation;
        level.reset();
        for(Player player : population) {
            player.respawn(level);
        }

        System.out.println("Fitness: " + bestCandidates.get(0).fitness + " MoveCount: " + bestCandidates.get(0).getMoves().length);
    }

    public List<Player> selection() {
        Player bestPlayer = null;
        double bestFitness = Double.MAX_VALUE;
        for(var player : population) {
            if(player.fitness < bestFitness) {
                bestPlayer = player;
                bestFitness = player.fitness;
            }
        }
        return Arrays.asList(bestPlayer);
    }

    public List<Player> mutate(List<Player> candidates) {
        List<Player> children = new ArrayList<>();
        var childrenCountPerCandidate = populationSize / candidates.size();
        for(var candidate : candidates) {
            for(var i = 0; i < childrenCountPerCandidate; i++) {
                var child = new Player(this.playerMoveCount, candidate.getMoves());
                child.mutate();
                children.add(child);
            }
        }
        return children;
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
    private void drawCenteredString(String s, int w, int h, Graphics g) {
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
    private void drawCenteredString(String s, int w, int h, Graphics2D g2) {
        FontMetrics fm = g2.getFontMetrics();
        int x = (w*2 - fm.stringWidth(s)) / 2;
        g2.drawString(s, x, h);
    }





    /** Draw a right-justified string.
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
    private void drawRightJustifiedString(String s, int w, int h, Graphics g) {
        FontMetrics fm = g.getFontMetrics();
        int x = (w - fm.stringWidth(s));
        g.drawString(s, x, h);
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
    private void drawTextOutline(String text, int x, int y, int thickness, Graphics2D g2) {
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
    private void drawString(String text, int x, int y, Graphics g) {
        for (String line : text.split("\n"))
            g.drawString(line, x, y += g.getFontMetrics().getHeight());
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

    public static void loadLevels() {
        try {
            while (new File(ClassLoader
                    .getSystemResource("net/thedanpage/worldshardestgame/resources/maps/level_" + (totalLevels+1) + ".txt").toURI())
                    .exists()) {
                totalLevels++;
            }
        } catch (Exception e) {
            System.out.println("Total levels: " + totalLevels);
        }
    }

    public static void setupFrame() {
        frame.setTitle("World's Hardest Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(800, 622));
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setIconImage(new ImageIcon(ClassLoader.getSystemResource("net/thedanpage/worldshardestgame/resources/favicon.png")).getImage());
    }

    private void intializePopulation() {
        for (var i = 0; i < populationSize; i++) {
            population.add(new Player(playerMoveCount));
        }
    }

    public static void main(String[] args) {
        TinySound.init();
        loadLevels();
        setupFrame();

        bgMusic.start();

        var controller = new GeneticController();
        game = new Game(controller);
        frame.add(game);
        frame.setVisible(true);
    }
}