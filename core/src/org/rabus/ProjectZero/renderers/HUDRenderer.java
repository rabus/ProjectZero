package org.rabus.ProjectZero.renderers;

import org.rabus.ProjectZero.entities.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class HUDRenderer
{
	AssetManager assets;
	Map map;
	SpriteBatch batch;
	BitmapFont font16, font24;
	TextureRegion heart;

	public HUDRenderer(AssetManager assets, Map map)
	{
		this.assets = assets;
		this.map = map;
		heart = assets.get("gfx/textures.atlas", TextureAtlas.class).findRegion("objects12x12").split(12, 12)[0][0];
		font24 = assets.get("gfx/fonts/verdana24.fnt", BitmapFont.class);
		font16 = assets.get("gfx/fonts/verdana16.fnt", BitmapFont.class);
		//font.setScale(0.7f);
		batch = new SpriteBatch();
		batch.getProjectionMatrix().setToOrtho2D(0, 0, 800, 480);
	}

	public void render()
	{
		batch.begin();
		font24.setColor(0, 0, 0, 1);
		font24.draw(batch, "Scores:   ", 20, 460);
		font24.draw(batch, "Crystals: ", 20, 440);
		for (int i = 1; i <= map.player.health; i++)
		{
			batch.draw(heart, i * 15 + 5, 380, 30, 30);
		}
		font24.setColor(1, 0.1f, 0.1f, 1);
		font24.draw(batch, Integer.toString(map.player.score), 140, 460);
		font24.draw(batch, Integer.toString(map.player.crystals), 140, 440);
		font16.setColor(1, 1, 1, 1);
		font16.draw(batch, "ProjectZero v.0.4.2 by Adam Rabiega", 490, 20);
		font16.setColor(0, 0, 0, 1);
		font16.draw(batch, "FPS: " + Integer.toString(Gdx.graphics.getFramesPerSecond()), 700, 470);
		font16.draw(batch, "PL0: " + Integer.toString((int) this.map.player.pos.x) + " " + Integer.toString((int) this.map.player.pos.y), 700, 450);
		batch.end();
	}

	public void dispose()
	{
		batch.dispose();
	}
}
