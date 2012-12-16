package org.rabus.ProjectZero.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import org.rabus.ProjectZero.entities.Map;
import org.rabus.ProjectZero.entities.Monster;
import org.rabus.ProjectZero.entities.Player;
import org.rabus.ProjectZero.renderers.HUDRenderer;
import org.rabus.ProjectZero.renderers.MapRenderer;
import org.rabus.ProjectZero.renderers.OnscreenControlRenderer;
import org.rabus.ProjectZero.renderers.ParallaxBackground;

/**
 * @author Adam Rabiega
 */
public class GameScreen extends BasicScreen implements InputProcessor
{
    AssetManager assets = new AssetManager();
    Map map;
    Player player;
    MapRenderer renderer;
    ParallaxBackground bgRenderer;
    OnscreenControlRenderer controlRenderer;
    HUDRenderer hudRenderer;
    SpriteBatch bgBatch;
    long renderTime;
    int currentLevel;
    public boolean isLoaded = false;

    //static int howManyLevels = 2; // Hard set level number (should change to dynamic checking!!) - Changed below

    public GameScreen(Game game, AssetManager assets, int currentLevel)
    {
        super(game);
        this.assets = assets;
        this.currentLevel = currentLevel;
        this.player = new Player();
    }

    public GameScreen(Game game, AssetManager assets, int currentLevel, Player player)
    {
        super(game);
        this.assets = assets;
        this.currentLevel = currentLevel;
        this.player = player;
    }

    @Override
    public void show()
    {
        Gdx.input.setCatchBackKey(true);
        Gdx.input.setInputProcessor(this);
        map = new Map(assets, assets.get("levels/level" + Integer.toString(currentLevel) + ".png", Pixmap.class));
        map.player.health = this.player.health;
        map.player.crystals = this.player.crystals;
        map.player.score = this.player.score;
//        bgRenderer = new ParallaxBackground(new ParallaxLayer[]{
//                //new ParallaxLayer(assets.get("gfx/backgrounds.atlas", TextureAtlas.class).findRegion("bg1"), new Vector2(), new Vector2(0, 0)),
//                //new ParallaxLayer(assets.get("gfx/backgrounds.atlas", TextureAtlas.class).findRegion("bg1"), new Vector2(1.0f, 1.0f), new Vector2(0, 500)),
//                //new ParallaxLayer(assets.get("gfx/backgrounds.atlas", TextureAtlas.class).findRegion("bg1"), new Vector2(0.1f, 0), new Vector2(0, 140), new Vector2(0, 0)),
//        }, 800, 480, new Vector2(100f, 0));
        renderer = new MapRenderer(assets, map);
        controlRenderer = new OnscreenControlRenderer(assets, map);
        hudRenderer = new HUDRenderer(assets, map);
        isLoaded = true;
        renderTime = System.nanoTime();
        bgBatch = new SpriteBatch();
        bgBatch.getProjectionMatrix().setToOrtho2D(0, 0, 800, 480);
    }

    @Override
    public void render(float delta)
    {
        delta = Math.min(0.06f, Gdx.graphics.getDeltaTime());
        map.update(delta); // Updates game logic (there is logic? ;p) // Most heavy
        Gdx.gl.glClearColor(0.529f, 0.808f, 0.98f, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        //bgRenderer.render(delta);
        /* Background */
        bgBatch.begin();
        bgBatch.draw(assets.get("gfx/backgrounds.atlas", TextureAtlas.class).findRegion("sky"), 0, 0);
        bgBatch.end();
        renderer.render(delta); // Game content render
        controlRenderer.render(); // Android controls render
        hudRenderer.render(); // HUD (health, score, fps)

        if (map.player.health == 0)
            game.setScreen(new GameOverDefeat(game, assets)); // Player dies, game over

        if (map.player.bounds.overlaps(map.endFlag.bounds)) // Player finds exit doors (or flag), level complete ;p
        {
            if (assets.isLoaded("levels/level" + Integer.toString(currentLevel + 1) + ".png", Pixmap.class))
            {
                currentLevel += 1;
                game.setScreen(new GameScreen(game, assets, currentLevel, map.player));
                //assets.unload("levels/level" + Integer.toString(currentLevel - 1) + ".png"); // Unload previous level. Have to check this
            }
            else
                game.setScreen(new GameOverVictory(game, assets)); // If no more levels, then game is complete! (have to add Game Complete Screen :P)
        }

        if (Gdx.input.isKeyPressed(Keys.ESCAPE) || Gdx.input.isKeyPressed(Keys.BACK))
        {
            //Gdx.app.exit();
            game.setScreen(new MainMenu(game, assets));
        }
    }

    @Override
    public void resize(int width, int height)
    {
        renderer.resize(width, height);
        //		this.width = width;
        //		this.height = height;
        //		if (width == 480 && height == 320) {
        //			cam = new OrthographicCamera(700, 466);
        //			this.width = 700;
        //			this.height = 466;
        //		} else if (width == 320 && height == 240) {
        //			cam = new OrthographicCamera(700, 525);
        //			this.width = 700;
        //			this.height = 525;
        //		} else if (width == 400 && height == 240) {
        //			cam = new OrthographicCamera(800, 480);
        //			this.width = 800;
        //			this.height = 480;
        //		} else if (width == 432 && height == 240) {
        //			cam = new OrthographicCamera(700, 389);
        //			this.width = 700;
        //			this.height = 389;
        //		} else if (width == 960 && height == 640) {
        //			cam = new OrthographicCamera(800, 533);
        //			this.width = 800;
        //			this.height = 533;
        //		}  else if (width == 1366 && height == 768) {
        //			cam = new OrthographicCamera(1280, 720);
        //			this.width = 1280;
        //			this.height = 720;
        //		} else if (width == 1366 && height == 720) {
        //			cam = new OrthographicCamera(1280, 675);
        //			this.width = 1280;
        //			this.height = 675;
        //		} else if (width == 1536 && height == 1152) {
        //			cam = new OrthographicCamera(1366, 1024);
        //			this.width = 1366;
        //			this.height = 1024;
        //		} else if (width == 1920 && height == 1152) {
        //			cam = new OrthographicCamera(1366, 854);
        //			this.width = 1366;
        //			this.height = 854;
        //		} else if (width == 1920 && height == 1200) {
        //			cam = new OrthographicCamera(1366, 800);
        //			this.width = 1280;
        //			this.height = 800;
        //		} else if (width > 1280) {
        //			cam = new OrthographicCamera(1280, 768);
        //			this.width = 1280;
        //			this.height = 768;
        //		} else if (width < 800) {
        //			cam = new OrthographicCamera(800, 480);
        //			this.width = 800;
        //			this.height = 480;
        //		} else {
        //			cam = new OrthographicCamera(width, height);
        //		}
        //		cam.position.x = 400;
        //		cam.position.y = 240;
        //		cam.update();
    }

    @Override
    public void hide()
    {
        Gdx.app.debug("ProjectZero", "dispose game screen");
        player.dispose();
        map.dispose();
        renderer.dispose();
        controlRenderer.dispose();
        hudRenderer.dispose();
        bgBatch.dispose();
    }

    @Override
    public void pause()
    {
    }

    @Override
    public void resume()
    {
    }

    @Override
    public void dispose()
    {
        Gdx.app.debug("ProjectZero", "dispose game screen");
        map.dispose();
        renderer.dispose();
        controlRenderer.dispose();
        hudRenderer.dispose();
        bgBatch.dispose();
    }

    @Override
    public boolean keyDown(int keycode)
    {
        if (keycode == Keys.F1) // F1 adds monsters at spawner location
            if (map.spawners.size >= 1)
                map.monsters.add(new Monster(assets, map, map.spawners.get(0).pos.x, map.spawners.get(0).pos.y, Monster.ORC));
        return true;
    }

    @Override
    public boolean keyUp(int keycode)
    {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean keyTyped(char character)
    {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button)
    {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button)
    {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer)
    {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY)
    {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean scrolled(int amount)
    {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
