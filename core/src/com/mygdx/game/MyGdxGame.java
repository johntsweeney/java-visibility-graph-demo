package com.mygdx.game;

// MyGame.java
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.visibilitygraph.VisibilityGraph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MyGdxGame extends Game {
	@Override
	public void create() {


		Set<ArrayList<Vector2>> obstacles = new HashSet<>();

		ArrayList<Vector2> obs1 = new ArrayList<>();
		obs1.add(new Vector2(477,250));
		obs1.add(new Vector2(515,397));
		obs1.add(new Vector2(370,360));
		obstacles.add(obs1);

		ArrayList<Vector2> obs2 = new ArrayList<>();
		obs2.add(new Vector2(320,221));
		obs2.add(new Vector2(330,63));
		obs2.add(new Vector2(475,114));
		obstacles.add(obs2);

		ArrayList<Vector2> obs3 = new ArrayList<>();
		obs3.add(new Vector2(327,297));
		obs3.add(new Vector2(204,437));
		obs3.add(new Vector2(130,313));
		obs3.add(new Vector2(200,225));
		obstacles.add(obs3);


		Vector2 startPoint = new Vector2(10, 10);
		Vector2 endPoint = new Vector2(630, 470);

		VisibilityGraph visibilityGraph = new VisibilityGraph(
				startPoint,
				endPoint,
				obstacles,
				5f
		);

		// Set the initial screen to the main menu
		setScreen(new GameScreen(this, visibilityGraph));
	}
}
