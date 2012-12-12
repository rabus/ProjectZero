package org.rabus.ProjectZero.entities;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Monster extends Entity
{
	public static final int IDLE = 0;
	public static final int RUN = 1;
	public static final int JUMP = 2;
	public static final int SPAWN = 3;
	public static final int DYING = 4;
	public static final int DEAD = 999;
	public static final int LEFT = -1;
	public static final int RIGHT = 1;
	public static final float ACCELERATION = 20f; // Acceleration from idle to running
	public static final float JUMP_VELOCITY = 10; // Jumping velocity
	public static final float GRAVITY = 20.0f; // G-force
	public static final float MAX_VEL = 1f; // Maximum running velocity
	public static final float DAMP = 0.90f; // Bounce effect (dampening)

	public static final int SKELETON = 100;

	AssetManager assets;
	Map map;
	public int type;

	public Vector2 accel = new Vector2();
	public Vector2 vel = new Vector2();

	public int state = SPAWN;
	public float stateTime = 0;
	public int dir = RIGHT;
	boolean grounded = false;
	boolean godmode = false;

	Sound splat;

	public Monster(AssetManager assets, Map map, float x, float y, int type)
	{
		this.type = type;
		this.assets = assets;
		this.map = map;
		this.pos.x = x;
		this.pos.y = y;
		this.bounds.width = 0.6f;
		this.bounds.height = 0.9f;
		this.bounds.x = pos.x + 0.2f;
		this.bounds.y = pos.y;

		splat = assets.get("sfx/splat.wav", Sound.class);
	}

	public void update(float deltaTime)
	{
		processAI();

		accel.y = -GRAVITY;
		accel.mul(deltaTime);
		vel.add(accel.x, accel.y);
		if (accel.x == 0)
			vel.x *= DAMP;
		if (vel.x > MAX_VEL)
			vel.x = MAX_VEL;
		if (vel.x < -MAX_VEL)
			vel.x = -MAX_VEL;
		vel.mul(deltaTime);
		tryMove();
		vel.mul(1.0f / deltaTime);

		if (state == SPAWN)
		{
			if (stateTime > 0.4f)
			{
				state = RUN;
			}
		}

		if (state == DYING)
		{
			if (stateTime > 0.8f) // Time for death animation
			{
				state = DEAD;
			}
		}

		stateTime += deltaTime;

		if (map.player.bounds.overlaps(bounds))
		{
			if (map.player.state != Player.DYING)
			{
				map.player.splat.play(1f);
				map.player.state = Player.DYING;
				map.player.stateTime = 0;
			}
		}
	}

	private void processAI()
	{
		if (state == SPAWN || state == DYING)
			return;

		if (state == IDLE)
			state = RUN;
		else
			if (state == RUN)
			{
				if (dir == LEFT)
				{
					//state = RUN;
					//dir = LEFT;
					accel.x = ACCELERATION * dir;
				}
				else
					if (dir == RIGHT)
					{
						//state = RUN;
						//dir = RIGHT;
						accel.x = ACCELERATION * dir;
					}
			}

		//state = IDLE;
		//accel.x = 0;

		//		if ((Gdx.input.isKeyPressed(Keys.W) || jumpButton) && state != JUMP)
		//		{
		//			//jump.play(1f);
		//			state = JUMP;
		//			vel.y = JUMP_VELOCITY;
		//			grounded = false;
		//		}
	}

	Rectangle[] r = { new Rectangle(), new Rectangle(), new Rectangle(), new Rectangle(), new Rectangle() };

	private void tryMove()
	{
		bounds.x += vel.x;
		fetchCollidableRects();
		for (int i = 0; i < r.length; i++)
		{
			Rectangle rect = r[i];
			if (bounds.overlaps(rect))
			{
				if (vel.x < 0)
					bounds.x = rect.x + rect.width + 0.01f;
				else
					bounds.x = rect.x - bounds.width - 0.01f;
				vel.x = 0;
				dir = -dir; // Reverses direction
			}
		}

		bounds.y += vel.y;
		fetchCollidableRects();
		for (int i = 0; i < r.length; i++)
		{
			Rectangle rect = r[i];
			if (bounds.overlaps(rect))
			{
				if (vel.y < 0)
				{
					bounds.y = rect.y + rect.height + 0.01f;
					grounded = true;
					if (state != DYING && state != SPAWN)
						state = Math.abs(accel.x) > 0.1f ? RUN : IDLE;
				}
				else
					bounds.y = rect.y - bounds.height - 0.01f;
				vel.y = 0;
			}
		}

		pos.x = bounds.x - 0.2f;
		pos.y = bounds.y;
	}

	private void fetchCollidableRects()
	{
		int p1x = (int) bounds.x;
		int p1y = (int) Math.floor(bounds.y);
		int p2x = (int) (bounds.x + bounds.width);
		int p2y = (int) Math.floor(bounds.y);
		int p3x = (int) (bounds.x + bounds.width);
		int p3y = (int) (bounds.y + bounds.height);
		int p4x = (int) bounds.x;
		int p4y = (int) (bounds.y + bounds.height);

		int[][] tiles = map.tiles;
		int tile1 = tiles[p1x][map.tiles[0].length - 1 - p1y];
		int tile2 = tiles[p2x][map.tiles[0].length - 1 - p2y];
		int tile3 = tiles[p3x][map.tiles[0].length - 1 - p3y];
		int tile4 = tiles[p4x][map.tiles[0].length - 1 - p4y];

		if (tile1 == Map.TILE || tile1 == Map.BORDER || tile1 == Map.ROCK)
			r[0].set(p1x, p1y, 1, 1);
		else
			r[0].set(-1, -1, 0, 0);
		if (tile2 == Map.TILE || tile2 == Map.BORDER || tile2 == Map.ROCK)
			r[1].set(p2x, p2y, 1, 1);
		else
			r[1].set(-1, -1, 0, 0);
		if (tile3 == Map.TILE || tile3 == Map.BORDER || tile3 == Map.ROCK)
			r[2].set(p3x, p3y, 1, 1);
		else
			r[2].set(-1, -1, 0, 0);
		if (tile4 == Map.TILE || tile4 == Map.BORDER || tile4 == Map.ROCK)
			r[3].set(p4x, p4y, 1, 1);
		else
			r[3].set(-1, -1, 0, 0);
	}
}