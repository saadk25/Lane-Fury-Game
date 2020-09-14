package com.lanefury.game.world;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;

import com.lanefury.game.entities.AnimatedActor;
import com.lanefury.game.entities.BaseActor;
import com.lanefury.game.entities.Boss;
import com.lanefury.game.entities.Player;
import com.lanefury.game.entities.PlayerAnimations;
import com.lanefury.game.entities.Thug;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

public class GameLevel implements Screen {
	private TiledMap tiledMap;
	private OrthographicCamera camera;
	private TiledMapRenderer tiledMapRenderer;
	
	// initialize the layer arrays based on their layer number in tiled
	private int[] background = new int[] { 0 };
	private int[] bossDoor = new int[] { 3 };

	private TiledMapTileLayer walls;
	MapObjects wallObjects;
	public static MapObjects teleportObject;
	
	private Stage mainStage;
	private Stage uiStage;

	// initialize mainPlayer
	private Player mainPlayer;

	// initialize the thugs
	private Thug thug1, thug2, thug3, thug4, thug5, thug6, thug7;
	private Boss boss;

	private BaseActor playerHealthBar;
	private BaseActor currentPlayerHealthBar = new BaseActor();
	private BaseActor thugHealthBar;
	private BaseActor currentThugHealthBar = new BaseActor();
	private BaseActor bossHealthBar;
	private BaseActor currentBossHealthBar = new BaseActor();
	
	Pixmap srcKyoHealthBar = new Pixmap(Gdx.files.internal("assets/sprites/kyo/kyoCurrentHealth.png"));
	Pixmap updatingKyoHealthBar;
	Texture kyoHealthTexture;

	Pixmap srcThugHealthBar = new Pixmap(Gdx.files.internal("assets/sprites/thug/thugCurrentHealth.png"));
	Pixmap updatingThugHealthBar;
	Texture thugHealthTexture;
	
	Pixmap srcBossHealthBar = new Pixmap(Gdx.files.internal("assets/sprites/boss/bossCurrentHealth.png"));
	Pixmap updatingBossHealthBar;
	Texture bossHealthTexture;

	// labels for player, thug and boss & their styles
	private Label playerLabel;
	private Label thugLabel;
	private Label bossLabel;
	BitmapFont font = new BitmapFont();
	LabelStyle playerLabelStyle = new LabelStyle(font, Color.YELLOW);
	LabelStyle thugLabelStyle = new LabelStyle(font, Color.BLUE);
	LabelStyle bossLabelStyle = new LabelStyle(font, Color.PURPLE);

	// game world dimensions
	final int mapWidth = 5080;
	final int mapHeight = 480;
	// window dimensions
	final int viewWidth = 800;
	final int viewHeight = 480;

	// storing a list of all the enemies
	public static HashMap<String, Thug> listOfEnemies = new HashMap<String, Thug>();
	private Iterator enemyIterator;

	public Game game;
	
	private boolean win;
	
	public GameLevel(Game g) {
		game = g;
		create();
	}

	public void create() {
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

//        blank = new Texture("blank.png");

		mainStage = new Stage();
		uiStage = new Stage();
		camera = new OrthographicCamera();

		camera.setToOrtho(false, w, h);
		camera.update();

		tiledMap = new TmxMapLoader().load("assets/stageA.tmx");
		tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

		wallObjects = tiledMap.getLayers().get(1).getObjects();
		teleportObject = tiledMap.getLayers().get(2).getObjects();

		// Initialize mainPlayer
		String playerName = "kyo";
		mainPlayer = new Player(playerName, wallObjects, "Saad");

		mainPlayer.setOrigin(mainPlayer.getWidth() / 2, mainPlayer.getHeight() / 2);

		// Set Player's initial position on map
//		mainPlayer.setPosition(60, 270);
		mainPlayer.setPosition(2000, 20);

		// initialize camera position for mainPlayer
		mainPlayer.setPlayerCameraPosition();

		// Initialize thugs and boss
		String thugSprite = "thug";
		String bossSprite = "boss";
		
		thug1 = new Thug(thugSprite, wallObjects, mainPlayer, "Deadlyft");
		thug2 = new Thug(thugSprite, wallObjects, mainPlayer, "Utoranto");
		thug3 = new Thug(thugSprite, wallObjects, mainPlayer, "Zoobear");
		thug4 = new Thug(thugSprite, wallObjects, mainPlayer, "Ah-yawn");
		thug5 = new Thug(thugSprite, wallObjects, mainPlayer, "Javah");
		thug6 = new Thug(thugSprite, wallObjects, mainPlayer, "Paithon");
		thug7 = new Thug(thugSprite, wallObjects, mainPlayer, "Seasharp");
		boss = new Boss(bossSprite, wallObjects, mainPlayer, "Aleri Cam");

		thug1.setOrigin(thug1.getWidth() / 2, thug1.getHeight() / 2);
		thug2.setOrigin(thug2.getWidth() / 2, thug2.getHeight() / 2);
		thug3.setOrigin(thug3.getWidth() / 2, thug3.getHeight() / 2);
		thug4.setOrigin(thug4.getWidth() / 2, thug4.getHeight() / 2);
		thug5.setOrigin(thug5.getWidth() / 2, thug5.getHeight() / 2);
		thug6.setOrigin(thug6.getWidth() / 2, thug6.getHeight() / 2);
		thug7.setOrigin(thug7.getWidth() / 2, thug7.getHeight() / 2);
		boss.setOrigin(boss.getWidth() / 2, boss.getHeight() / 2);

		// Set thug's initial position on map & add him to map
		thug1.setPosition(700, 200);
		mainStage.addActor(thug1);

		thug2.setPosition(1000, 5);
		mainStage.addActor(thug2);

		thug3.setPosition(1020, 20);
		mainStage.addActor(thug3);

		thug4.setPosition(300, 270);
		mainStage.addActor(thug4);

		thug5.setPosition(400, 265);
		mainStage.addActor(thug5);

		thug6.setPosition(1500, 10);
		mainStage.addActor(thug6);

		thug7.setPosition(2000, 10);
		mainStage.addActor(thug7);
		
		boss.setPosition(4900, 20);
		mainStage.addActor(boss);

		// put thugs into listOfEnemies hashmap, with their name as the key
		listOfEnemies.put(thug1.getCharName(), thug1);
		listOfEnemies.put(thug2.getCharName(), thug2);
		listOfEnemies.put(thug3.getCharName(), thug3);
		listOfEnemies.put(thug4.getCharName(), thug4);
		listOfEnemies.put(thug5.getCharName(), thug5);
		listOfEnemies.put(thug6.getCharName(), thug6);
		listOfEnemies.put(thug7.getCharName(), thug7);
		listOfEnemies.put(boss.getCharName(), boss);

		// add mainPlayer to map
		mainStage.addActor(mainPlayer);

		// can probably use this at end for win screen / game over screen

//        winText = new BaseActor();
//        winText.setTexture( new Texture(Gdx.files.internal("assets/you-win.png")) );
//        winText.setPosition( 900, 70 );
//        winText.setVisible( false );
//        uiStage.addActor( winText );

		// create mainPlayer's health bar template and icon
		playerHealthBar = new BaseActor();
		playerHealthBar.setTexture(new Texture(Gdx.files.internal("assets/sprites/kyo/kyo_healthBar.png")));
		playerHealthBar.setPosition(300, 400);
		uiStage.addActor(playerHealthBar);
		
		// create thug's health bar template and icon
		thugHealthBar = new BaseActor();
		thugHealthBar.setTexture(new Texture(Gdx.files.internal("assets/sprites/thug/thug_healthBar.png")));
		thugHealthBar.setPosition(355, 400);
		uiStage.addActor(thugHealthBar);
		thugHealthBar.setVisible(false); // initially hide the thug health bar
		
		// create thug's health bar template and icon
		bossHealthBar = new BaseActor();
		bossHealthBar.setTexture(new Texture(Gdx.files.internal("assets/sprites/boss/boss_healthBar.png")));
		bossHealthBar.setPosition(355, 400);
		uiStage.addActor(bossHealthBar);
		bossHealthBar.setVisible(false); // initially hide the boss health bar
		

		// set main character name label above health bar
		playerLabel = new Label(mainPlayer.getCharName(), playerLabelStyle);
		playerLabel.setFontScale(2);
		playerLabel.setPosition(95, 440);
		uiStage.addActor(playerLabel);
		
		// set thug character name label above health bar
		thugLabel = new Label(" ", thugLabelStyle);
		thugLabel.setFontScale(2);
		thugLabel.setPosition(400, 440);
		uiStage.addActor(thugLabel);
		
		// set thug character name label above health bar
		bossLabel = new Label(" ", bossLabelStyle);
		bossLabel.setFontScale(2);
		bossLabel.setPosition(400, 440);
		uiStage.addActor(bossLabel);

		win = false;

		Gdx.input.setInputProcessor(mainPlayer);
	}

	public void render(float dt) {

		// clear the screen before rendering
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// Set camera to middle of player
		camera.position.set(mainPlayer.getPlayerMiddleX(), mainPlayer.getPlayerMiddleY(), 0);
		camera.update();

		// switch to main screen if M is pressed
		if (Gdx.input.isKeyPressed(Keys.M))
			game.setScreen(new GameMenu(game));

		// get list of all thugs

		enemyIterator = GameLevel.listOfEnemies.entrySet().iterator();

		// Display the enemies in that are in the background of the player
		while (enemyIterator.hasNext()) {
			Map.Entry mapElement = (Map.Entry) enemyIterator.next();
			Thug thug = (Thug) mapElement.getValue();

			if (thug.getY() > mainPlayer.getY()) { // If thug above player, put thug in background
				mainStage.addActor(thug);
			}
		}
		
		// display player after placing thugs behind him in background
		mainStage.addActor(mainPlayer);
		
		enemyIterator = GameLevel.listOfEnemies.entrySet().iterator();
		
		// Display the enemies in that are in the foreground of the player
		while (enemyIterator.hasNext()) {
			Map.Entry mapElement = (Map.Entry) enemyIterator.next();
			Thug thug = (Thug) mapElement.getValue();
			
			if (thug.getY() < mainPlayer.getY()) { // If thug below player, put thug in foreground
				mainStage.addActor(thug);
			}
			
			if (mainPlayer.getThugLastHit() == thug.getCharName()) {
				
				if (thug.getSpriteName() == "thug") {
					thugHealthBar.setVisible(true);
					if (thug.getHealth() > -1) {
						updatingThugHealthBar = new Pixmap(thug.getHealth() * 2, srcThugHealthBar.getHeight(), srcThugHealthBar.getFormat());
						updatingThugHealthBar.drawPixmap(srcThugHealthBar, 0, 0, srcThugHealthBar.getWidth(), srcThugHealthBar.getHeight(), 0, 0, updatingThugHealthBar.getWidth(), updatingThugHealthBar.getHeight());
						thugHealthTexture = new Texture(updatingThugHealthBar);
					} else { // to prevent errors from multiplying pixel by negative health
						updatingThugHealthBar = new Pixmap(0, srcThugHealthBar.getHeight(), srcThugHealthBar.getFormat());
						updatingThugHealthBar.drawPixmap(srcThugHealthBar, 0, 0, srcThugHealthBar.getWidth(), srcThugHealthBar.getHeight(), 0, 0, updatingThugHealthBar.getWidth(), updatingThugHealthBar.getHeight());
						thugHealthTexture = new Texture(updatingThugHealthBar);
					}
					
					currentThugHealthBar.setTexture(thugHealthTexture);
					// the thug's icon and health bar container
					thugHealthBar.setPosition(355, 400);	
					// set yellow health bar on top of health bar container
					currentThugHealthBar.setPosition(360, 408);
					uiStage.addActor(currentThugHealthBar);
					
					// set thug character name label above health bar
					thugLabel.setText(thug.getCharName());
				} else if (thug.getSpriteName() == "boss") {
					
					// hide the thug health bar
					thugHealthBar.setVisible(false);
					thugLabel.setVisible(false);
					currentThugHealthBar.setVisible(false);
					
					// display the boss health bar
					bossHealthBar.setVisible(true);
					
					if (thug.getHealth() > -1) {
						updatingBossHealthBar = new Pixmap(thug.getHealth() / 2, srcBossHealthBar.getHeight(), srcBossHealthBar.getFormat());
						updatingBossHealthBar.drawPixmap(srcBossHealthBar, 0, 0, srcBossHealthBar.getWidth(), srcBossHealthBar.getHeight(), 0, 0, updatingBossHealthBar.getWidth(), updatingBossHealthBar.getHeight());
						bossHealthTexture = new Texture(updatingBossHealthBar);
					} else { // to prevent errors from multiplying pixel by negative health
						updatingBossHealthBar = new Pixmap(0, srcBossHealthBar.getHeight(), srcBossHealthBar.getFormat());
						updatingBossHealthBar.drawPixmap(srcBossHealthBar, 0, 0, srcBossHealthBar.getWidth(), srcBossHealthBar.getHeight(), 0, 0, updatingBossHealthBar.getWidth(), updatingBossHealthBar.getHeight());
						bossHealthTexture = new Texture(updatingBossHealthBar);
					}
					
					currentBossHealthBar.setTexture(bossHealthTexture);
					// the thug's icon and health bar container
					bossHealthBar.setPosition(355, 400);	
					// set yellow health bar on top of health bar container
					currentBossHealthBar.setPosition(360, 408);
					uiStage.addActor(currentBossHealthBar);
					
					// set thug character name label above health bar
					bossLabel.setText(thug.getCharName());
				}
				
			}
			
			
		}

		// mainPlayer health bar

		// updating player's health
		
		if (mainPlayer.getHealth() > -1) {
			updatingKyoHealthBar = new Pixmap(mainPlayer.getHealth() * 2, srcKyoHealthBar.getHeight(), srcKyoHealthBar.getFormat());
			updatingKyoHealthBar.drawPixmap(srcKyoHealthBar, 0, 0, srcKyoHealthBar.getWidth(), srcKyoHealthBar.getHeight(), 0, 0, updatingKyoHealthBar.getWidth(), updatingKyoHealthBar.getHeight());
			kyoHealthTexture = new Texture(updatingKyoHealthBar);
		} else { // to prevent errors from multiplying pixel by negative health
			updatingKyoHealthBar = new Pixmap(0, srcKyoHealthBar.getHeight(), srcKyoHealthBar.getFormat());
			updatingKyoHealthBar.drawPixmap(srcKyoHealthBar, 0, 0, srcKyoHealthBar.getWidth(), srcKyoHealthBar.getHeight(), 0, 0, updatingKyoHealthBar.getWidth(), updatingKyoHealthBar.getHeight());
			kyoHealthTexture = new Texture(updatingKyoHealthBar);
		}
		
	
		// currentHealth bar is the yellow health of mainPlayer that reduces
		currentPlayerHealthBar.setTexture(kyoHealthTexture);
		
		// the player's icon and health bar container
		playerHealthBar.setPosition(20, 400);
		
		// set yellow health bar on top of health bar container
		currentPlayerHealthBar.setPosition(80, 408);

		uiStage.addActor(currentPlayerHealthBar);
		
		// update
		mainStage.act(dt);
		uiStage.act(dt);

		// draw graphics
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();

		tiledMapRenderer.setView(camera);
		tiledMapRenderer.render(background);

		mainStage.draw();
		uiStage.draw();

		Camera cam = mainStage.getCamera();

		// center camera on player

		cam.position.set(mainPlayer.getPlayerCenterX(), mainPlayer.getPlayerCenterY(), 0);
		cam.update();

		mainStage.draw();
		tiledMapRenderer.render(bossDoor);
		uiStage.draw();
		
		

	}

	public void resize(int width, int height) {
	}

	public void pause() {
	}

	public void resume() {
	}

	public void dispose() {
	}

	@Override
	public void show() {

	}

	public void hide() {
	}

	public TiledMapTileLayer getWalls() {
		return walls;
	}

	public void setWalls(TiledMapTileLayer walls) {
		this.walls = walls;
	}

	public MapObjects getWallObjects() {
		return wallObjects;
	}

	public void setWallObjects(MapObjects wallObjects) {
		this.wallObjects = wallObjects;
	}

}