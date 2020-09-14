package com.lanefury.game.world;

import com.badlogic.gdx.Game;

public class FuryGame extends Game
{
    public void create() 
    {  
        GameMenu cm = new GameMenu(this);
        setScreen( cm );
    }
}
