package com.gamejam.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gamejam.game.Screens.StartScreen;

public class GameJam extends Game {
	// 400x208
	public static final int V_WIDTH = 400;
	public static final int V_HEIGHT = 208;
	public static final float PPM = 100;

	public static final short NOTHING_BIT = 0;
	public static final short GROUND_BIT = 1;
	public static final short PLAYER_BIT = 2;
	public static final short OBJECT_BIT = 4;
	public static final short DESTROYED_BIT = 8;
	public static final short ENEMY_BIT = 16;
	public static final short ITEM_BIT = 32;
	public static final short GHOST_HEAD_BIT = 64;
	public static final short PLAYER_HEAD_BIT = 128;

	public SpriteBatch batch;
	public static AssetManager manager;

	@Override
	public void create () {
		batch = new SpriteBatch();
		manager = new AssetManager();
		manager.load("audio/musica.mp3", Music.class);
		manager.load("audio/Ambience.mp3", Music.class);
		manager.load("audio/Collect.mp3", Sound.class);
		manager.load("audio/Damage.mp3", Sound.class);
		manager.load("audio/Attack.mp3", Sound.class);
		manager.load("audio/Game_over.mp3", Sound.class);
		manager.load("audio/Game_over_2.mp3", Sound.class);
		manager.load("audio/Jump.mp3", Sound.class);
		manager.load("audio/Win.mp3", Sound.class);
		manager.load("audio/Coin.mp3", Sound.class);
		manager.load("audio/Sword.mp3", Sound.class);

		manager.finishLoading();
		setScreen(new StartScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		super.dispose();
		batch.dispose();

	}
}
