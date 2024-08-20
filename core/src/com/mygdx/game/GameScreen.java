package com.mygdx.game;

// GameScreen.java
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.visibilitygraph.VGEdge;
import com.mygdx.game.visibilitygraph.VisibilityGraph;

import java.util.ArrayList;

public class GameScreen implements Screen {
    private final MyGdxGame game;
    private Stage stage;
    private Skin skin;
    private ShapeRenderer shapeRenderer;
    private VisibilityGraph visibilityGraph;

    public GameScreen(MyGdxGame game, VisibilityGraph visibilityGraph) {
        this.game = game;
        this.visibilityGraph = visibilityGraph;
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("uiskin.json"));

//        Label gameLabel = new Label("Game Screen", skin);
//        gameLabel.setPosition(Gdx.graphics.getWidth() / 2f - gameLabel.getWidth() / 2, Gdx.graphics.getHeight() / 2f);

//        stage.addActor(gameLabel);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();

        shapeRenderer.begin();

        shapeRenderer.setColor(Color.BLUE);
        Vector2 sp = visibilityGraph.getStartPoint();
        shapeRenderer.circle(sp.x, sp.y, 5);

        shapeRenderer.setColor(Color.GREEN);
        Vector2 ep = visibilityGraph.getEndPoint();
        shapeRenderer.circle(ep.x, ep.y, 5);

        ArrayList<VGEdge> edges = new ArrayList<>();
        edges.addAll(visibilityGraph.getObstacleEdges());
        edges.addAll(visibilityGraph.getVisibilityEdges());

        shapeRenderer.setColor(Color.WHITE);
        for (VGEdge edge : edges) {
            if (!edge.isSolid) {
                shapeRenderer.line(edge.a.pos, edge.b.pos);
            }
        }

        shapeRenderer.setColor(Color.RED);
        for (VGEdge edge : edges) {
            if (edge.isSolid) {
                shapeRenderer.line(edge.a.pos, edge.b.pos);
            }
        }

        shapeRenderer.end();

    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        stage.dispose();
        skin.dispose();
    }

    @Override
    public void dispose() {}
}
