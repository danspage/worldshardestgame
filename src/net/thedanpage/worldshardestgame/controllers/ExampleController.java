package net.thedanpage.worldshardestgame.controllers;

import net.thedanpage.worldshardestgame.Game;
import net.thedanpage.worldshardestgame.GameLevel;
import net.thedanpage.worldshardestgame.Move;

import java.util.Random;

public class ExampleController extends Controller {
    Random random = new Random();
    int wtf = 0;
    @Override
    public Move getMove(GameLevel game) {
        wtf++;
        if(0 < wtf && wtf < 1000) { return Move.DOWN; }
        else if (1000 < wtf && wtf < 2000) { return Move.RIGHT; }
        else return Move.UP;
        //return Move.values()[random.nextInt(Move.values().length)];
    }
}
