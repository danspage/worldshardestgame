package net.thedanpage.worldshardestgame.controllers;

import net.thedanpage.worldshardestgame.Game;
import net.thedanpage.worldshardestgame.GameLevel;
import net.thedanpage.worldshardestgame.Move;
import net.thedanpage.worldshardestgame.Player;

public class GeneticController extends Controller {

    @Override
    public Move getMove(Game game, Player player) {
        return player.getNextMove();
    }
}
