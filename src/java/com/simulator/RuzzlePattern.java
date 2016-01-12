package com.simulator;

import java.util.Vector;

/**
 * Created by shashwat on 20/10/15.
 */
public class RuzzlePattern
{
    Vector<Pair> wordCoordinates;
    String word;
    int score;

    public RuzzlePattern(Vector<Pair> wordCoordinates, String word)
    {
        this.wordCoordinates = wordCoordinates;
        this.word = word;
    }
}
