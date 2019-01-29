package com.gamejam.game.Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.gamejam.game.GameJam;
import com.gamejam.game.Scenes.Hud;
import com.gamejam.game.Screens.PlayScreen;

public class Coin extends InteractiveTileObject {

    public Coin(PlayScreen screen, MapObject object){
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(GameJam.OBJECT_BIT);
    }

    @Override
    public void onCollision(Player player) {
        getCell().setTile(null);

        setCategoryFilter(GameJam.DESTROYED_BIT);
        Hud.addScore(50);
        GameJam.manager.get("audio/Coin.mp3", Sound.class).play(0.6f);


    }
}