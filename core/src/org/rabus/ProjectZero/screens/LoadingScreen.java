package org.rabus.ProjectZero.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class LoadingScreen extends BasicScreen
{
    AssetManager assets;
    SpriteBatch batch;
    BitmapFont font = new BitmapFont();

    public LoadingScreen(Game game, AssetManager assets)
    {
        super(game);
        this.assets = assets;
    }

    @Override
    public void show()
    {
        loadAssets();
        batch = new SpriteBatch();
        batch.getProjectionMatrix().setToOrtho2D(0, 0, 800, 480);
    }

    private void loadAssets() // Definitions for all in-game assets to be loaded by AssetManager
    {
        // Music
        //assets.load("msx/music1.ogg", Music.class);

        // Sounds
        assets.load("sfx/splat.wav", Sound.class);
        assets.load("sfx/pickup_crystal.wav", Sound.class);
        assets.load("sfx/explode.wav", Sound.class);

        // Levels

        // Broken for runnable jar (should use resourse handle for that)
        FileHandle dirHandle;
        if (Gdx.app.getType() == Application.ApplicationType.Desktop)
        {
            //dirHandle = Gdx.files.internal("./bin/levels/");
            assets.load("levels/level0.png", Pixmap.class);
            assets.load("levels/level1.png", Pixmap.class);
            assets.load("levels/level2.png", Pixmap.class);
        }
        else
        {
            dirHandle = Gdx.files.internal("./levels/");
            for (FileHandle level : dirHandle.list())
            {
                if (!level.isDirectory())
                {
                    assets.load("levels/" + level.name(), Pixmap.class);
                    Gdx.app.log("ProjectZero", level.name() + " is loaded.");
                }
            }
        }

        // Textures
        assets.load("gfx/fonts/verdana16.fnt", BitmapFont.class);
        assets.load("gfx/fonts/verdana24.fnt", BitmapFont.class);
        assets.load("gfx/fonts/verdana32.fnt", BitmapFont.class);

        // Controls
        assets.load("gfx/controls/controls.png", Texture.class);

        // Screens
        //assets.load("gfx/screens/title.png", Texture.class);
        //assets.load("gfx/screens/intro.png", Texture.class);
        //assets.load("gfx/screens/gameover.png", Texture.class);

        // Textures
        assets.load("gfx/textures.atlas", TextureAtlas.class);
    }

    @Override
    public void render(float delta)
    {
        if (assets.update()) // Updates assets loading and returns true is finished
        {
            Gdx.app.log("ProjectZero", "All assets loaded successfully ...");
            //game.setScreen(new MainMenu(game, assets)); // Loads main menu screen after loading assets
            game.setScreen(new GameScreen(game, assets, 0)); // If anything pressed or touched (android) moves on and it's playtime :) (loads 0 level)
        }
        else
        {
            float progress = assets.getProgress() * 100;
            Gdx.gl.glClearColor(0f, 0f, 0f, 1);
            Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
            batch.begin();
            font.draw(batch, Float.toString(progress) + " %", 400, 240); // Progress "bar" (percentage for now)
            font.draw(batch, Integer.toString(assets.getLoadedAssets()) + " loaded / " + Integer.toString(assets.getQueuedAssets()) + " to go ...", 360, 220);
            batch.end();
        }
    }

    @Override
    public void hide()
    {
        Gdx.app.debug("ProjectZero", "dispose loading screen");
        batch.dispose();
        font.dispose();
    }
}
