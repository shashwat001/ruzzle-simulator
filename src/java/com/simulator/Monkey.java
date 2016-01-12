package com.simulator;

import com.android.chimpchat.adb.AdbBackend;
import com.android.chimpchat.core.IChimpDevice;
import com.android.chimpchat.core.IChimpImage;
import com.android.chimpchat.core.TouchPressType;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

public class Monkey
{
    private AdbBackend adb;
    private IChimpDevice device;

    int horizontaldiff = 260;
    int verticaldiff = 260;
    int width = 220;
    int height = 220;
    int offsetx = 40 + width / 2;
    int offsety = 550 + height / 2;

    public enum MODE
    {
        RANDOM,
        PRACTICE
    }

    Monkey()
    {
        adb = new AdbBackend();
        device = adb.waitForConnection();
        System.out.println("Device connected");
    }

    public void openRuzzle() throws InterruptedException
    {
        System.out.println("Opening Ruzzle");
        String pkgName = "se.maginteractive.rumble";
        String activityName = "se.maginteractive.rumble.activities.SplashActivity";
        String runComponent = pkgName + "/" + activityName;
        device.startActivity(null, null, null, null, new ArrayList<String>(),
                new HashMap<String, Object>(), runComponent, 0);
        TimeUnit.SECONDS.sleep(10);
        System.out.println("Ruzzle is running");
    }

    public BufferedImage takeSnapshot()
    {
        IChimpImage image = device.takeSnapshot();
        BufferedImage buffImage = image.createBufferedImage();
        return buffImage;
    }

    public void startNewGame(MODE gameMode) throws InterruptedException
    {
        System.out.println("Starting new game");
        device.touch(500, 650, TouchPressType.DOWN_AND_UP);
        TimeUnit.SECONDS.sleep(2);
        switch (gameMode)
        {
            case RANDOM:
                break;
            case PRACTICE:
                System.out.println("Practice mode");
                device.touch(500, 1900, TouchPressType.DOWN_AND_UP);
                TimeUnit.SECONDS.sleep(2);
                break;
        }
        System.out.println("Game started");
    }

    public void saveScreen(String filename)
    {
        IChimpImage image = device.takeSnapshot();
        image.writeToFile(filename, null);
    }

    public void shutDown()
    {
        System.out.println("Shutting Down");
        device.dispose();
        System.out.println("Shutdown Finished");
    }

    public void clickAtRuzzleBox(int x, int y) throws InterruptedException
    {
        System.out.println("Clicking at point");

        int coorx = offsetx + y * horizontaldiff;
        int coory = offsety + x * verticaldiff;
        System.out.println("Touching at " + coorx + "," + coory);
        device.touch(coorx, coory, TouchPressType.DOWN_AND_UP);
        TimeUnit.SECONDS.sleep(1);
    }

    public void drag(int startx, int starty, int endx, int endy) throws InterruptedException
    {
        int coorstartx = offsetx + starty * horizontaldiff;
        int coorstarty = offsety + startx * verticaldiff;
        int coorendx = offsetx + endy * horizontaldiff;
        int coorendy = offsety + endx * verticaldiff;

        device.touch(coorstartx, coorstarty, TouchPressType.DOWN);
        device.touch(coorstartx, coorstarty, TouchPressType.MOVE);
        device.touch(coorendx, coorendy, TouchPressType.MOVE);
        device.touch(coorendx, coorendy, TouchPressType.UP);
    }

    private void touchDown(int x, int y)
    {
        int coorx = offsetx + y * horizontaldiff;
        int coory = offsety + x * verticaldiff;

        device.touch(coorx, coory, TouchPressType.DOWN);
    }

    private void touchMove(int x, int y)
    {
        int coorx = offsetx + y * horizontaldiff;
        int coory = offsety + x * verticaldiff;

        device.touch(coorx, coory, TouchPressType.MOVE);
    }

    private void touchUp(int x, int y)
    {
        int coorx = offsetx + y * horizontaldiff;
        int coory = offsety + x * verticaldiff;

        device.touch(coorx, coory, TouchPressType.UP);
    }

    void dragCoorList(Vector<Pair> coorList) throws InterruptedException
    {
        touchDown(coorList.firstElement().x, coorList.firstElement().y);
        Thread.sleep(30);
        for (Pair pair : coorList)
        {
            touchMove(pair.x, pair.y);
            Thread.sleep(50);
        }
        touchUp(coorList.lastElement().x, coorList.lastElement().y);
        Thread.sleep(50);
    }

    public static void main(String[] args) throws InterruptedException, IOException
    {
        System.out.println("Starting main");
        final Monkey runner = new Monkey();
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            public void run()
            {
                System.out.println("Running Shutdown Hook");
                runner.shutDown();
            }
        });
//        runner.openRuzzle();
//        runner.startNewGame(MODE.PRACTICE);
//        BufferedImage image = runner.takeSnapshot();
        Solver game = new Solver();
        game.loadDictionary("enable1.txt");
        game.readWords(Solver.READ_STYLE.WITH_SCORE);
        game.printMatrix();
        game.solve(Solver.READ_STYLE.WITH_SCORE);
        for (RuzzlePattern pattern : game.getPatternListSortedByPoints())
        {
            runner.dragCoorList(pattern.wordCoordinates);
        }

        runner.shutDown();
    }
}
