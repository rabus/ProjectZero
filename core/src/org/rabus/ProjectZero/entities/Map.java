package org.rabus.ProjectZero.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.Array;

public class Map
{
    /* TILES */
    public static int EMPTY = 0; // BLACK (0 0 0)
    public static int START = 0xff0000; // RED (255 0 0)
    public static int CHECKPOINT = 0xff0100; // LIGHT RED (255 1 0)
    public static int END = 0xff00ff; // PURPLE (255 0 255)
    public static int BORDER = 0x646464; // GRAY (100 100 100)
    public static int MONSTER_BORDER = 0x969696; // GRAY (150 150 150)
    public static int TILE = 0xffffff; // WHITE (255 255 255)
    public static int ROCK = 0x303030; // DARK GRAY
    public static int PLATFORM = 0xc8c8c8; // LIGHT GRAY
    public static int WATER = 0x008aff; // LIGHT BLUE
    public static int LAVA = 0xff7500; // ORANGE
    public static int ACID = 0x008a00; // LIGHT GREEN
    public static int BLUECRYSTAL = 0x0099ff; // LIGHT BLUE (0 99 255)
    public static int GREENCRYSTAL = 0x99ff00; // LIMETTE
    public static int REDCRYSTAL = 0xff0099; // PINK
    public static int SPIKES = 0x00ff00; // GREEN
    public static int ENERGY_BALL = 0x0000ff; // 0 0 255
    public static int ROCKET = 0x1010ff; // 16 16 255
    public static int ARROW = 0x2020ff; // 32 32 255
    public static int MOVING_SPIKES = 0xffff00; // YELLOW
    public static int LASER = 0x00ffff; // CYAN
    public static int SPAWNER = 0xff6464; // PINK
    /* MONSTERS */
    public static int ORC = 0xa0b0c0;
    /* ITEMS */
    public static int ARMOR_WARRIOR = 0x321919; // BROWN (50 25 25)

    public static final float PIXELS_PER_METER = 20.0f;

    public AssetManager assets;
    public float volume = 0.5f;
    public boolean isLoaded = false;

    public int[][] tiles; //, fluids;
    public int width, height;
    int savedCrystals = 0;
    int savedScore = 0;
    int savedHealth = 0;
    public Player player;
    public EndFlag endFlag;
    public Array<Monster> monsters = new Array<Monster>();
    public Array<Checkpoint> checkpoints = new Array<Checkpoint>();
    public Array<Spawner> spawners = new Array<Spawner>(); // Monster spawners
    public Checkpoint activeCheckpoint = null;
    public Array<Projectile> projectiles = new Array<Projectile>();
    public Array<Crystal> crystals = new Array<Crystal>();
    public Array<PickupItem> pickupItems = new Array<PickupItem>();
    public Array<MovingSpikes> movingSpikes = new Array<MovingSpikes>();
    public Array<Platform> platforms = new Array<Platform>();
    public Array<Fluid> fluids = new Array<Fluid>();
    public Array<Spike> spikes = new Array<Spike>();
    public Array<Block> blocks = new Array<Block>();

    public Map(AssetManager assets, Pixmap mapname)
    {
        this.assets = assets;
        loadBinary(mapname);
        loadEntities();
        createBlocks();
        isLoaded = true;
    }

    private void loadBinary(Pixmap mapname)
    {
        Pixmap pixmap = mapname; // Map loading, providing width and height as well as structure
        width = pixmap.getWidth();
        height = pixmap.getHeight();
        tiles = new int[pixmap.getWidth()][pixmap.getHeight()];
        //fluids = new int[pixmap.getWidth()][pixmap.getHeight()];
        for (int y = 0; y < this.height; y++) // Using height from pixmap
        {
            for (int x = 0; x < this.width; x++) // Using width from pixmap
            {
                int pix = (pixmap.getPixel(x, y) >>> 8) & 0xffffff;
                //Gdx.app.log("Map", x + ", " + y + ", " + Integer.toHexString(pix)); // Display debug information about every pixel in log.
                if (match(pix, START))
                {
                    Checkpoint checkpoint = new Checkpoint(this, x, pixmap.getHeight() - 1 - y);
                    checkpoints.add(checkpoint);
                    activeCheckpoint = checkpoint;
                    activeCheckpoint.active = true;
                    player = new Player(assets, this, activeCheckpoint.bounds.x, activeCheckpoint.bounds.y);
                    player.state = Player.SPAWN;
                }
                else if (match(pix, CHECKPOINT))
                {
                    checkpoints.add(new Checkpoint(this, x, pixmap.getHeight() - 1 - y));
                }
                else if (match(pix, SPAWNER))
                {
                    spawners.add(new Spawner(this, x, pixmap.getHeight() - 1 - y));
                }
                else if (match(pix, ORC))
                {
                    monsters.add(new Monster(this.assets, this, x, pixmap.getHeight() - 1 - y, Monster.ORC));
                }
                else if (match(pix, ENERGY_BALL))
                {
                    projectiles.add(new Projectile(assets, this, x, pixmap.getHeight() - 1 - y, Projectile.ENERGY_BALL));
                }
                else if (match(pix, ROCKET))
                {
                    projectiles.add(new Projectile(assets, this, x, pixmap.getHeight() - 1 - y, Projectile.ROCKET));
                }
                else if (match(pix, BLUECRYSTAL))
                {
                    crystals.add(new Crystal(assets, this, x, pixmap.getHeight() - 1 - y, Crystal.BLUE));
                }
                else if (match(pix, GREENCRYSTAL))
                {
                    crystals.add(new Crystal(assets, this, x, pixmap.getHeight() - 1 - y, Crystal.GREEN));
                }
                else if (match(pix, REDCRYSTAL))
                {
                    crystals.add(new Crystal(assets, this, x, pixmap.getHeight() - 1 - y, Crystal.RED));
                }
                else if (match(pix, ARMOR_WARRIOR))
                {
                    pickupItems.add(new PickupItem(assets, this, x, pixmap.getHeight() - 1 - y, PickupItem.ARMOR_WARRIOR));
                }
                else if (match(pix, MOVING_SPIKES))
                {
                    movingSpikes.add(new MovingSpikes(this, x, pixmap.getHeight() - 1 - y));
                }
                else if (match(pix, SPIKES))
                {
                    spikes.add(new Spike(assets, this, x, pixmap.getHeight() - 1 - y, Spike.NORMAL));
                }
                else if (match(pix, PLATFORM))
                {
                    platforms.add(new Platform(this, x, pixmap.getHeight() - 1 - y));
                }
                else if (match(pix, LASER))
                {
                }
                else if (match(pix, END))
                {
                    endFlag = new EndFlag(x, pixmap.getHeight() - 1 - y);
                }
                else if (match(pix, WATER))
                {
                    fluids.add(new Fluid(assets, this, x, pixmap.getHeight() - 1 - y, Fluid.WATER));
                }
                else if (match(pix, LAVA))
                {
                    fluids.add(new Fluid(assets, this, x, pixmap.getHeight() - 1 - y, Fluid.LAVA));
                }
                else if (match(pix, ACID))
                {
                    fluids.add(new Fluid(assets, this, x, pixmap.getHeight() - 1 - y, Fluid.ACID));
                }
                else
                {
                    tiles[x][y] = pix;
                }
            }
        }
        Gdx.app.log("ProjectZero", "Map created successfully!");
    }

    private void createBlocks() // Create basic tiled blocks (ground)
    {
        int width = this.tiles.length;
        int height = this.tiles[0].length;
        for (int blockY = 0; blockY < this.tiles[0].length; blockY++)
        {
            for (int blockX = 0; blockX < this.tiles.length; blockX++)
            {
                for (int y = blockY; y < blockY + 1; y++)
                {
                    for (int x = blockX; x < blockX + 1; x++)
                    {
                        if (x >= width && x < 0)
                            continue;
                        if (y >= height && y < 0)
                            continue;
                        int posX = x;
                        int posY = height - y - 1;
                        if (this.match(this.tiles[x][y], Map.TILE)) // Defines default middle texture for tiles
                        {
                            if (y > 0 && !this.match(this.tiles[x][y - 1], Map.TILE)) // TOP_CENTER [2][0-7] or [0][0] - [row][column] in tiles.png
                                if (x > 0 && !this.match(this.tiles[x - 1][y], Map.TILE) && x < width - 1 && !this.match(this.tiles[x + 1][y], Map.TILE)) // VERTICAL_TOP [0][1]
                                    if (y < height - 1 && !this.match(this.tiles[x][y + 1], Map.TILE)) // ALONE [0][2]
                                        blocks.add(new Block(assets, this, posX, posY, Block.ALONE)); // ALONE [0][2]
                                    else
                                        blocks.add(new Block(assets, this, posX, posY, Block.VERTICAL_TOP)); // VERTICAL_TOP [0][1]
                                else if (x > 0 && !this.match(this.tiles[x - 1][y], Map.TILE)) // TOP_LEFT [0][3]
                                    if (y < height - 1 && !this.match(this.tiles[x][y + 1], Map.TILE)) // HORIZONTAL_LEFT [0][4]
                                        blocks.add(new Block(assets, this, posX, posY, Block.HORIZONTAL_LEFT)); // HORIZONTAL_LEFT [0][4]
                                    else
                                        blocks.add(new Block(assets, this, posX, posY, Block.TOP_LEFT)); // TOP_LEFT [0][3]
                                else if (x < width - 1 && !this.match(this.tiles[x + 1][y], Map.TILE)) // TOP_RIGHT [0][5]
                                    if (y < height - 1 && !this.match(this.tiles[x][y + 1], Map.TILE)) // HORIZONTAL_RIGHT [0][6]
                                        blocks.add(new Block(assets, this, posX, posY, Block.HORIZONTAL_RIGHT)); // HORIZONTAL_RIGHT [0][6]
                                    else
                                        blocks.add(new Block(assets, this, posX, posY, Block.TOP_RIGHT)); // TOP_RIGHT [0][5]
                                else if (!this.match(this.tiles[x][y - 1], Map.TILE) && y < height - 1 && !this.match(this.tiles[x][y + 1], Map.TILE)) // HORIZONTAL_CENTER [0][7]
                                    blocks.add(new Block(assets, this, posX, posY, Block.HORIZONTAL_CENTER)); // HORIZONTAL_CENTER [0][7]
                                else
                                    blocks.add(new Block(assets, this, posX, posY, Block.TOP_CENTER)); // TOP_CENTER [2][0-7] or [0][0]
                                //cache.add(blockTiles[2][0 + (int) (Math.random() * ((7 - 0) + 1))], posX, posY, 1, 1); // TOP_CENTER [2][0-7] or [0][0]
                            else if (y < height - 1 && !this.match(this.tiles[x][y + 1], Map.TILE)) // BOTTOM_CENTER [1][0]
                                if (x > 0 && !this.match(this.tiles[x - 1][y], Map.TILE) && x < width - 1 && !this.match(this.tiles[x + 1][y], Map.TILE)) // VERTICAL_BOTTOM [1][1]
                                    blocks.add(new Block(assets, this, posX, posY, Block.VERTICAL_BOTTOM)); // VERTICAL_BOTTOM [1][1]
                                else if (x > 0 && !this.match(this.tiles[x - 1][y], Map.TILE)) // BOTTOM_LEFT [1][2]
                                    blocks.add(new Block(assets, this, posX, posY, Block.BOTTOM_LEFT)); // BOTTOM_LEFT [1][2]
                                else if (x < width - 1 && !this.match(this.tiles[x + 1][y], Map.TILE)) // BOTTOM_RIGHT [1][3]
                                    blocks.add(new Block(assets, this, posX, posY, Block.BOTTOM_RIGHT)); // BOTTOM_RIGHT [1][3]
                                else
                                    blocks.add(new Block(assets, this, posX, posY, Block.BOTTOM_CENTER)); // BOTTOM_CENTER [1][0]
                            else if (x > 0 && !this.match(this.tiles[x - 1][y], Map.TILE) && x < width - 1 && !this.match(this.tiles[x + 1][y], Map.TILE)) // VERTICAL_MIDDLE [1][4]
                                blocks.add(new Block(assets, this, posX, posY, Block.VERTICAL_MIDDLE)); // VERTICAL_MIDDLE [1][4]
                            else if (x > 0 && !this.match(this.tiles[x - 1][y], Map.TILE)) // MIDDLE_LEFT [1][5]
                                blocks.add(new Block(assets, this, posX, posY, Block.MIDDLE_LEFT)); // MIDDLE_LEFT [1][5]
                            else if (x < width - 1 && !this.match(this.tiles[x + 1][y], Map.TILE)) // MIDDLE_RIGHT [1][6]
                                blocks.add(new Block(assets, this, posX, posY, Block.MIDDLE_RIGHT)); // MIDDLE_RIGHT [1][6]
                            else
                                // MIDDLE_CENTER [3][0-7] or [1,7]
                                blocks.add(new Block(assets, this, posX, posY, Block.MIDDLE_CENTER)); // MIDDLE_CENTER [3][0-7] or [1,7]
                            //cache.add(blockTiles[3][0 + (int) (Math.random() * ((7 - 0) + 1))], posX, posY, 1, 1); // MIDDLE_CENTER [3][0-7] or [1,7]

                        }
                        //if (this.match(this.tiles[x][y], Map.ROCK)) // Adds rock
                        //	cache.add(blockTiles[4][0], posX, posY, 1, 1);
                    }
                }
            }
        }
        Gdx.app.debug("ProjectZero", "blocks created");
    }

    private void loadEntities()
    {
        // Entities initialization:

        for (int i = 0; i < movingSpikes.size; i++) // Create movingSpikes
        {
            movingSpikes.get(i).init();
        }

        //		for (int i = 0; i < platforms.size; i++) // Create Platforms
        //		{
        //			platforms.get(i).init();
        //		}
    }

    public boolean match(int src, int dst)
    {
        return src == dst;
    }

    public void update(float deltaTime)
    {
        player.update(deltaTime);
        if (player.state == Player.DEAD)
        {
            // If player dier, he looses one health
            player.health -= 1;
            // Backup health, score, crystals
            savedHealth = player.health;
            savedScore = player.score;
            savedCrystals = player.crystals;
            // Respawns at last touched checkpoint
            player = new Player(assets, this, activeCheckpoint.bounds.x, activeCheckpoint.bounds.y);
            // Restore health, score, crystals
            player.health = savedHealth;
            player.score = savedScore;
            player.crystals = savedCrystals;
        }
        for (int i = 0; i < checkpoints.size; i++)
        {
            if (player.bounds.overlaps(checkpoints.get(i).bounds))
            {
                activeCheckpoint.active = false;
                activeCheckpoint = checkpoints.get(i);
                activeCheckpoint.active = true;
                //savedScore = player.score;
            }
        }
        for (Monster monster : monsters) // Modify every update to this structure
        {
            monster.update(deltaTime);
            if (monster.state == Monster.DEAD)
                monsters.removeValue(monster, true);
        }
        for (int i = 0; i < projectiles.size; i++)
        {
            if (this.player.pos.dst2(projectiles.get(i).pos) < 80f)
                projectiles.get(i).update(deltaTime);
        }
        for (int i = 0; i < crystals.size; i++)
        {
            if (this.player.pos.dst2(crystals.get(i).pos) < 40f)
            {
                crystals.get(i).update(deltaTime);
                if (crystals.get(i).collected)
                    crystals.removeIndex(i);
            }
        }
        for (int i = 0; i < pickupItems.size; i++)
        {
            if (this.player.pos.dst2(pickupItems.get(i).pos) < 40f)
            {
                pickupItems.get(i).update(deltaTime);
                if (pickupItems.get(i).collected)
                    pickupItems.removeIndex(i);
            }
        }
        for (int i = 0; i < movingSpikes.size; i++)
        {
            //			MovingSpikes spikes = movingSpikes.get(i);
            //			spikes.update(deltaTime);
            if (this.player.pos.dst2(movingSpikes.get(i).pos) < 240f)
                movingSpikes.get(i).update(deltaTime);
        }
        //		for (int i = 0; i < platforms.size; i++)
        //		{
        //			//			Platform platform = platforms.get(i);
        //			//			platform.update(deltaTime);
        //		}
        for (int i = 0; i < fluids.size; i++)
        {
            //			Fluid fluid = fluids.get(i);
            //			fluid.update(deltaTime);
            if (this.player.pos.dst2(fluids.get(i).pos) < 40f)
                fluids.get(i).update(deltaTime);
        }
        for (int i = 0; i < spikes.size; i++)
        {
            //			Fluid fluid = fluids.get(i);
            //			fluid.update(deltaTime);
            if (this.player.pos.dst2(spikes.get(i).pos) < 40f)
                spikes.get(i).update(deltaTime);
        }
    }

    public void dispose()
    {
        player.dispose();

        for (int i = 0; i < projectiles.size; i++)
            projectiles.clear();
        for (int i = 0; i < crystals.size; i++)
            crystals.clear();
        for (int i = 0; i < fluids.size; i++)
            fluids.clear();
        for (int i = 0; i < spikes.size; i++)
            spikes.clear();
        for (int i = 0; i < blocks.size; i++)
            blocks.clear();
        monsters.clear();
    }
}
