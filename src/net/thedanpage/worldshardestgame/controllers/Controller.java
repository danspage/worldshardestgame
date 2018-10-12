package net.thedanpage.worldshardestgame.controllers;

import net.thedanpage.worldshardestgame.Game;
import net.thedanpage.worldshardestgame.GameLevel;
import net.thedanpage.worldshardestgame.Move;

public abstract class Controller {
    public abstract Move getMove(GameLevel game);
}
