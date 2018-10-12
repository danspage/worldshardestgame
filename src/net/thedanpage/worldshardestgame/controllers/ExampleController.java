package net.thedanpage.worldshardestgame.controllers;

import net.thedanpage.worldshardestgame.Game;
import net.thedanpage.worldshardestgame.GameLevel;
import net.thedanpage.worldshardestgame.Move;

import java.util.Random;

public class ExampleController extends Controller {
    Random random = new Random();
    @Override
    public Move getMove(GameLevel game) {
        return Move.values()[random.nextInt(Move.values().length)];
    }
}
