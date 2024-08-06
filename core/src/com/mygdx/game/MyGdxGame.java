package com.mygdx.game;

// MyGame.java
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

public class MyGdxGame extends Game {
	@Override
	public void create() {
		// Set the initial screen to the main menu
		setScreen(new GameScreen(this));
	}
}
