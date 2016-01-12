package com.simulator;

/**
 * Created by shashwat on 19/10/15.
 */
public class Cell {
    public char character;
    public int characterPoint;
    public MULTIPLIER cellMultiplier;

    public enum MULTIPLIER
    {
        DL,
        TL,
        DW,
        TW
    }
}
