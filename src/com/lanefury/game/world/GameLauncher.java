package com.lanefury.game.world;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.math.Vector2;
public class GameLauncher
{
    public static void main (String[] args)
    {
        FuryGame myProgram = new FuryGame();
        LwjglApplication launcher = new LwjglApplication( myProgram );
    }
}