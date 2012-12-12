package org.rabus.ProjectZero.screens;

import org.rabus.ProjectZero.entities.Map;
import org.rabus.ProjectZero.entities.Player;
import org.rabus.ProjectZero.renderers.HUDRenderer;
import org.rabus.ProjectZero.renderers.MapRenderer;
import org.rabus.ProjectZero.renderers.OnscreenControlRenderer;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Pixmap;

/** @author Adam Rabiega */
public class GameScreen extends BasicScreen
{
	AssetManager assets = new AssetManager();
	Map map;
	Player player;
	MapRenderer renderer;
	OnscreenControlRenderer controlRenderer;
	HUDRenderer hudRenderer;
	int currentLevel;

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

	//	public GameScreen(Game game, AssetManager assets) // Obsolete
	//	{
	//		super(game);
	//		this.assets = assets;
	//		this.currentLevel = 0;
	//	}

	@Override
	public void show()
	{
		Gdx.input.setCatchBackKey(true);
		map = new Map(assets, assets.get("levels/level" + Integer.toString(currentLevel) + ".png", Pixmap.class));
		map.player.health = this.player.health;
		map.player.crystals = this.player.crystals;
		map.player.score = this.player.score;
		renderer = new MapRenderer(assets, map);
		controlRenderer = new OnscreenControlRenderer(assets, map);
		hudRenderer = new HUDRenderer(assets, map);
	}

	@Override
	public void render(float delta)
	{
		delta = Math.min(0.06f, Gdx.graphics.getDeltaTime());
		map.update(delta); // Updates game logic (there is logic? ;p) // Most heavy
		Gdx.gl.glClearColor(0.529f, 0.808f, 0.98f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
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

			// This below is obsolete way of dealing with level progress (static, using howManyLevels)
			//			if (currentLevel == howManyLevels - 1) // There is no more levels, you won! :D
			//				game.setScreen(new GameOver(game, assets));
			//			else
			//			// There is next level coming up :), loading it.
			//			{
			//				currentLevel += 1;
			//				game.setScreen(new GameScreen(game, assets, currentLevel));
			//				//assets.unload("levels/level" + Integer.toString(currentLevel - 1) + ".png"); // Unload previous level.
			//			}
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
	}
}
