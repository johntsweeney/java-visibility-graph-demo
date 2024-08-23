package com.mygdx.game;

// GameScreen.java
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.visibilitygraph.Octagon;
import com.mygdx.game.visibilitygraph.VGEdge;
import com.mygdx.game.visibilitygraph.VGVertex;
import com.mygdx.game.visibilitygraph.VisibilityGraph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class GameScreen implements Screen {
    private final MyGdxGame game;
    private Stage stage;
    private Skin skin;
    private ShapeRenderer shapeRenderer;

    private SpriteBatch spriteBatch;
    private BitmapFont bitmapFont;

    private ArrayList<Octagon> octagons;
    private int activeOctagonID = 0;
    private Octagon activeOctagon;

    private VisibilityGraph visibilityGraph;

    private ArrayList<Vector2> destinations;

    private final boolean DEBUG_ENABLED = false;

    public GameScreen(MyGdxGame game) {
        this.game = game;

        spriteBatch = new SpriteBatch();
        bitmapFont = new BitmapFont();
        bitmapFont.setColor(Color.WHITE);

        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
        destinations = new ArrayList<>();
        octagons = new ArrayList<>();

        Set<ArrayList<Vector2>> obstacles = new HashSet<>();
        Octagon octagon1 = new Octagon(new Vector2(100, 300), 50);
        octagons.add(octagon1);
        Octagon octagon2 = new Octagon(new Vector2(200, 200), 50);
        octagons.add(octagon2);
        Octagon octagon3 = new Octagon(new Vector2(500, 100), 50);
        octagons.add(octagon3);

        obstacles.add(octagons.get(0).getVertices());
        obstacles.add(octagons.get(1).getVertices());
        obstacles.add(octagons.get(2).getVertices());

        activeOctagon = octagons.get(activeOctagonID);

        Vector2 startPoint = new Vector2(10, 10);
        Vector2 endPoint = new Vector2(630, 470);

        VisibilityGraph visibilityGraph = new VisibilityGraph(
                startPoint,
                endPoint,
                obstacles,
                0f
        );

        this.visibilityGraph = visibilityGraph;
        destinations = visibilityGraph.aStar();
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

        updateVisGraph();

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

        shapeRenderer.setColor(Color.DARK_GRAY);
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

        shapeRenderer.setColor(Color.GREEN);
        Vector2 prev = visibilityGraph.getStartPoint();
        for (Vector2 curr : destinations) {
            shapeRenderer.line(prev, curr);
            prev = curr;
        }

        shapeRenderer.end();


        if (DEBUG_ENABLED) {        // DEBUG
            spriteBatch.begin();

            // Label Vertices
            ArrayList<VGVertex> vertices = visibilityGraph.getVertices();
            for (int i = 0; i < vertices.size(); i++) {
                VGVertex vertex = vertices.get(i);
                bitmapFont.setColor(Color.DARK_GRAY);
                bitmapFont.draw(spriteBatch, Integer.toString(i), vertex.pos.x, vertex.pos.y);
            }

            // Label Edges
            for (VGEdge edge : edges) {
                Vector2 midpoint = edge.getMidpoint();
                bitmapFont.setColor(Color.PINK);
                bitmapFont.draw(spriteBatch, Integer.toString(edge.ID), midpoint.x, midpoint.y);
            }

            spriteBatch.end();
        }

    }

    /**
     * Update Visibility Graph based upon user input.
     */
    public void updateVisGraph() {

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            activeOctagonID = (activeOctagonID + 1) % octagons.size();
            activeOctagon = octagons.get(activeOctagonID);
        }

        boolean updated = false;

        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            activeOctagon.position.y = activeOctagon.position.y + 1;
            updated = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            activeOctagon.position.y = activeOctagon.position.y - 1;
            updated = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            activeOctagon.position.x = activeOctagon.position.x - 1;
            updated = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            activeOctagon.position.x = activeOctagon.position.x + 1;
            updated = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.Z)) {
            activeOctagon.radius = activeOctagon.radius - 1;
            if (activeOctagon.radius < 10) activeOctagon.radius = 10;
            updated = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.X)) {
            activeOctagon.radius = activeOctagon.radius + 1;
            updated = true;
        }

        if (updated) {
            activeOctagon.set();

            Set<ArrayList<Vector2>> obstacles = new HashSet<>();

            obstacles.add(octagons.get(0).getVertices());
            obstacles.add(octagons.get(1).getVertices());
            obstacles.add(octagons.get(2).getVertices());

            Vector2 startPoint = new Vector2(10, 10);
            Vector2 endPoint = new Vector2(630, 470);

            VisibilityGraph visibilityGraph = new VisibilityGraph(
                    startPoint,
                    endPoint,
                    obstacles,
                    0f
            );

            this.visibilityGraph = visibilityGraph;
            destinations = visibilityGraph.aStar();
        }
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
    public void dispose() {
        spriteBatch.dispose();
        bitmapFont.dispose();
        shapeRenderer.dispose();
        stage.dispose();
        skin.dispose();
    }
}
