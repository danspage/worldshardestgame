package net.thedanpage.worldshardestgame.controllers;

import net.thedanpage.worldshardestgame.Game;
import net.thedanpage.worldshardestgame.GameLevel;
import net.thedanpage.worldshardestgame.Move;
import net.thedanpage.worldshardestgame.Player;

public abstract class Controller {
    public abstract Move getMove(Game game, Player player);
}
