package com.blockman.game;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;
import blockman.logic.Logic;
import org.andengine.engine.camera.BoundCamera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.FadeInModifier;
import org.andengine.entity.modifier.FadeOutModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.AutoParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground;
import org.andengine.entity.shape.IAreaShape;
import org.andengine.entity.shape.IShape;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.color.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.blockman.data.Map;


@SuppressLint("RtlHardcoded")
/**
 * Classe Tutorial que cria o tutorial inicial do jogo
 * @author Miguel
 *
 */
public class Tutorial extends SimpleBaseGameActivity {
	//COSNTANTS-----------------
	/**
	 * Largura da C�mara
	 */
	private static final int CAMERA_WIDTH = 1270;
	
	/**
	 * Altura da C�mara
	 */
	private static final int CAMERA_HEIGHT = 800;

	/**
	 * TAG usada em debugs
	 */
	private static final String TAG = "Block Man";

	/**
	 * Esquerda
	 */
	private static final String LEFT = "left";
	
	/**
	 * Direita
	 */
	private static final String RIGHT = "right";
	
	/**
	 * Esquerda Com Colis�o
	 */
	private static final String LEFT_W_COLLISION = "left collision";
	
	/**
	 * Direita com Colis�o
	 */
	private static final String RIGHT_W_COLLISION = "right collision";
	
	/**
	 * Parar virado para a direita
	 */
	private static final String STOP_RIGHT = "stop right";
	
	/**
	 * Parar virado para a esquerda
	 */
	private static final String STOP_LEFT = "stop left";
	
	/**
	 * Parar
	 */
	private static final String STOP = "STOP";

	/**
	 * In�cio do mapa na coordenada X
	 */
	private static final int MAP_START_X = 300;
	
	/**
	 * In�cio do mapa na coordenada Y
	 */
	private static final int MAP_START_Y = 7 * CAMERA_HEIGHT / 12 - 100;
	
	/**
	 * Espa�amento
	 */
	private static final int SPACING = 100;

	/**
	 * Coordenada X onde o Jogador inicia
	 */
	final float PLAYER_START_X = 1150;
	
	/**
	 * Coordenada Y onde o Jogador inicia
	 */
	final float PLAYER_START_Y = 7 * CAMERA_HEIGHT / 12 - 60;

	/**
	 * Visibilidade da f�sica de debug
	 */
	final boolean PHYSICS_VISIBILITY = false;

	/**
	 * O quanto o jogador anda
	 */
	final float WALKING_VY = (float) 1.6424394E-8;
	//----------------------------------
	//Game Variables---------------------
	/**
	 * Vari�vel que verifica se o jogador est� a carregar uma caixa
	 */
	private boolean carringBox = false;
	
	/**
	 * Vari�vel que verifica se o jogador ganhou
	 */
	private boolean win = false;
	//------------------------------
	//---------------------------
	/**
	 * Necess�rio para desenhar Sprites
	 */
	private VertexBufferObjectManager vertexBufferObjectManager;
	
	/**
	 * Sprite de retorno
	 */
	private Sprite back;
	//--------------------------
	//Camera--------------------
	/**
	 * Camara
	 */
	private BoundCamera myChaseCamera;
	
	/**
	 * HUD
	 */
	private HUD hud;
	//--------------------------
	//Physics-------------------
	/**
	 * F�sica do jogo
	 */
	private PhysicsWorld physicsWorld;

	/**
	 * Corpo do jogador
	 */
	private Body player_body;

	/**
	 * Contacto com o ch�o
	 */
	private boolean footContact = false; //how many footcontact there is

	//--------------------------
	//Add Scene-----------------
	/**
	 * Cena que tem todos os elementos do jogo
	 */
	private Scene scene;
	
	/**
	 * Listener de toque
	 */
	private IOnSceneTouchListener tListener;
	//--------------------------
	//Buttons-------------------
	/**
	 * Bitmap de retorno
	 */
	private BitmapTextureAtlas go_back_bmp;
	
	/**
	 * Textura de retorno
	 */
	private ITextureRegion go_back_texture;
	
	/**
	 * Sprite de bot�o de retorno
	 */
	private ButtonSprite go_back;

	/**
	 * Bitmap do bot�o de caixa
	 */
	private BitmapTextureAtlas box_btn_bmp;
	
	/**
	 * Textura do bot�o de caixa
	 */
	private ITextureRegion box_btn_texture;
	
	/**
	 * ButtonSprite do bot�o de caixa
	 */
	private ButtonSprite box_btn;

	/**
	 * Bitmap de bot�o de salto
	 */
	private BitmapTextureAtlas jump_btn_bmp;
	
	/**
	 * Textura de bot�o de salto
	 */
	private ITextureRegion jump_btn_texture;
	
	/**
	 * ButtonSprite do bot�o de salto
	 */
	private ButtonSprite jump_btn;
	//--------------------------
	/**
	 * Dire��o que o jogador ir� seguir
	 */
	String direction = "";
	//--------------------------
	//Sprites-------------------
	/**
	 * Textura da layer interm�dia
	 */
	private ITextureRegion myLayerMid;
	
	/**
	 * Textura da layer frontal
	 */
	private ITextureRegion myLayerFront;
	
	/**
	 * Bitmap do fundo
	 */
	private BitmapTextureAtlas myBackgroundTexture;

	/**
	 * Bitmap da textura
	 */
	private BitmapTextureAtlas mBitmapTextureAtlas;
	
	/**
	 * Regi�o da textura do jogador
	 */
	private TiledTextureRegion mPlayerTextureRegion;

	/**
	 * Sprite animada do jogador
	 */
	private AnimatedSprite player;
	//-----------------------------
	//Map Sprites-----------------
	/**
	 * Mapa do jogo
	 */
	Map map;
	
	/**
	 * Textura da pedra
	 */
	private ITextureRegion rock_layer;
	
	/**
	 * Bitmap da pedra
	 */
	private BitmapTextureAtlas rock_bmp;
	
	/**
	 * Bitmap da caixa
	 */
	private BitmapTextureAtlas box_bmp;
	
	/**
	 * Textura da caixa
	 */
	private ITextureRegion box_layer;
	
	/**
	 * Bitmap da porta
	 */
	private BitmapTextureAtlas door_bmp;
	
	/**
	 * Textura da porta
	 */
	private ITextureRegion door_layer;
	//---------------------------
	//Text------------------------
	/**
	 * Fonte usada no t�tulo
	 */
	private Font title_font;
	
	/**
	 * Texto do T�tulo
	 */
	private Text title;
	
	/**
	 * Fonte usada no texto de info
	 */
	private Font info_font;
	
	/**
	 * Texto do info
	 */
	private Text info;
	//--------------------------
	//Logic---------------------
	/**
	 * L�gica da jogo
	 */
	Logic gameLogic;
	//--------------------------
	//Tutorial scene-----------
	/**
	 * Estado do tutorial
	 */
	private int state = 0;
	//-------------------------

	@Override
	/**
	 * Cria as op��es do Engine usado
	 */
	public EngineOptions onCreateEngineOptions() {
		this.myChaseCamera = new BoundCamera(CAMERA_WIDTH, CAMERA_HEIGHT , CAMERA_WIDTH ,CAMERA_HEIGHT);
		//myChaseCamera.setCenter(CAMERA_WIDTH/3, 7 * CAMERA_HEIGHT / 9);
		myChaseCamera.setBoundsEnabled(true);
		myChaseCamera.setBounds(0 , 0, 3000 , 1708);
		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new FillResolutionPolicy(), myChaseCamera);
	}

	@Override
	/**
	 * Cria e carrega todos os recursos usados, como imagens, m�sica e textos
	 */
	protected void onCreateResources() {
		this.myBackgroundTexture = new BitmapTextureAtlas(this.getTextureManager(), 3000, 1708);
		this.myLayerFront = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.myBackgroundTexture, this, "ingame_back.jpg", 0, 0);
		this.myLayerMid = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.myBackgroundTexture, this, "clouds.png", 0, 188);
		this.myBackgroundTexture.load();
		Log.d("BlockMan", "Starting to load player");
		long current_time = System.currentTimeMillis();

		this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 2048, 4000, TextureOptions.BILINEAR);
		this.mPlayerTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "mustache_man_3.png", 0, 0, 16, 31);
		this.mBitmapTextureAtlas.load();

		Log.d("BlockMan", "Player loaded, it tooked " +  (System.currentTimeMillis() - current_time) + " ms");

		this.go_back_bmp = new BitmapTextureAtlas(this.getTextureManager(), 144, 144, TextureOptions.BILINEAR);
		this.go_back_texture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.go_back_bmp, this, "go_back.png", 0, 0);
		this.go_back_bmp.load();

		this.box_btn_bmp = new BitmapTextureAtlas(this.getTextureManager(), 144, 144, TextureOptions.BILINEAR);
		this.box_btn_texture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.box_btn_bmp, this, "box_btn.png", 0, 0);
		this.box_btn_bmp.load();

		this.jump_btn_bmp = new BitmapTextureAtlas(this.getTextureManager(), 144, 144, TextureOptions.BILINEAR);
		this.jump_btn_texture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.jump_btn_bmp, this, "jump_btn.png", 0, 0);
		this.jump_btn_bmp.load();

		this.box_bmp = new BitmapTextureAtlas(this.getTextureManager(), 144, 144, TextureOptions.BILINEAR);
		this.box_layer = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.box_bmp, this, "img/box.png", 0, 0);
		this.box_bmp.load();

		this.door_bmp = new BitmapTextureAtlas(this.getTextureManager(), 144, 144, TextureOptions.BILINEAR);
		this.door_layer = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.door_bmp, this, "img/signExit.png", 0, 0);
		this.door_bmp.load();


		title_font = FontFactory.createFromAsset(mEngine.getFontManager(),
				mEngine.getTextureManager(), 256, 256, TextureOptions.BILINEAR,
				this.getAssets(), "fonts/3Dumb.ttf", 90f, true,
				Color.BLACK_ABGR_PACKED_INT);
		title_font.load();
		
		info_font = FontFactory.createFromAsset(mEngine.getFontManager(),
				mEngine.getTextureManager(), 256, 256, TextureOptions.BILINEAR,
				this.getAssets(), "fonts/3Dumb.ttf", 50f, true,
				Color.BLACK_ABGR_PACKED_INT);
		info_font.load();

		this.rock_bmp = new BitmapTextureAtlas(this.getTextureManager(), 100, 100, TextureOptions.BILINEAR);
		this.rock_layer = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.rock_bmp, this, "img/rock.png", 0, 0);
		this.rock_bmp.load();



		this.title = new Text(PLAYER_START_X - 300, CAMERA_HEIGHT / 2 - 300, title_font, "A quick tutorial",getVertexBufferObjectManager());
	}

	@Override
	/**
	 * Gera a cena de jogo
	 */
	protected Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		scene = new Scene();
		final AutoParallaxBackground autoParallaxBackground = new AutoParallaxBackground(0, 0, 0, 5);
		vertexBufferObjectManager = this.getVertexBufferObjectManager();
		autoParallaxBackground.attachParallaxEntity(new ParallaxBackground.ParallaxEntity(0f, new Sprite(0, CAMERA_HEIGHT - this.myLayerFront.getHeight(), this.myLayerFront, vertexBufferObjectManager)));
		autoParallaxBackground.attachParallaxEntity(new ParallaxBackground.ParallaxEntity(0f, new Sprite(0, 0, this.myLayerMid, vertexBufferObjectManager)));
		//scene.setBackground(autoParallaxBackground);

		back = new Sprite(0, CAMERA_HEIGHT - this.myLayerFront.getHeight(), this.myLayerFront, vertexBufferObjectManager);
		scene.attachChild(back);

		player = new AnimatedSprite(PLAYER_START_X, PLAYER_START_Y , this.mPlayerTextureRegion, vertexBufferObjectManager);
		player.setScaleCenterY(this.mPlayerTextureRegion.getHeight() - 80);
		player.setScale((float)0.7);
		player_stop();

		//---------------------
		//add physics
		initPhysics();
		//------------------

		generateMap();
		scene.attachChild(player);
		scene.attachChild(title);

		Log.d(TAG, "Player height :" + player.getHeight());
		Log.d(TAG, "Player width :" + player.getWidth());

		FadeOutModifier mModifier = new FadeOutModifier(2.9f);
		title.registerEntityModifier(mModifier);

		tListener = new IOnSceneTouchListener() {
			@Override
			public boolean onSceneTouchEvent(Scene pScene, final TouchEvent pSceneTouchEvent) {
				if (pSceneTouchEvent.isActionDown()) {
					System.currentTimeMillis();
					if(!win)
						if(pSceneTouchEvent.getX() > player.getX()) {
							if(direction != RIGHT) {
								if(direction != RIGHT_W_COLLISION) {
									if(state == 1){ 
										state = 2;
										Tutorial.this.toastOnUIThread("Good!",
												Toast.LENGTH_SHORT);
										new Tut().start();
									}
									player_walk_right();
									if(!carringBox)
										player_body.setLinearVelocity(new Vector2(6, player_body.getLinearVelocity().y));
									else
										player_body.setLinearVelocity(new Vector2(3, player_body.getLinearVelocity().y));
									direction = RIGHT;
								}
							}
						}else{
							if(direction != LEFT) {
								if(direction != LEFT_W_COLLISION) {
									if(state == 2){ 
										state = 3;
										Tutorial.this.toastOnUIThread("Excellent!",
												Toast.LENGTH_SHORT);
										new Tut().start();
									}
									player_walk_left();
									if(!carringBox)
										player_body.setLinearVelocity(new Vector2(-6, player_body.getLinearVelocity().y));
									else
										player_body.setLinearVelocity(new Vector2(-3, player_body.getLinearVelocity().y));
									direction = LEFT;
								}
							}
						}
					return true;
				}

				if (pSceneTouchEvent.isActionMove()) {
					if(!win)
						if(pSceneTouchEvent.getX() > player.getX()) {
							if(direction != RIGHT) {
								if(direction != RIGHT_W_COLLISION) {
									if(!carringBox)
										player_body.setLinearVelocity(new Vector2(6, player_body.getLinearVelocity().y));
									else
										player_body.setLinearVelocity(new Vector2(3, player_body.getLinearVelocity().y));
									direction = RIGHT;
								}
							}
						}else{
							if(direction != LEFT) {
								if(direction != LEFT_W_COLLISION) {
									if(!carringBox)
										player_body.setLinearVelocity(new Vector2(-6, player_body.getLinearVelocity().y));
									else
										player_body.setLinearVelocity(new Vector2(-3, player_body.getLinearVelocity().y));
									direction = LEFT;
								}
							}
						}
					return true;
				}

				if (pSceneTouchEvent.isActionUp()){
					if(direction == RIGHT){
						player_stop_right();
						player_body.setLinearVelocity(new Vector2(0, player_body.getLinearVelocity().y));
						direction = STOP_RIGHT;
					}else if(direction == LEFT){
						player_stop_left();
						player_body.setLinearVelocity(new Vector2(0, player_body.getLinearVelocity().y));
						direction = STOP_LEFT;
					}else{
						direction = STOP;
					}
					return true;
				}
				return false;
			}


		};

		myChaseCamera.setChaseEntity(player);

		//Botao em hud
		hud = new HUD();
		go_back =  new ButtonSprite(25, 25 , go_back_texture,  vertexBufferObjectManager){
			@Override
			/**
			 * Verifica a �rea onde o jogador tocou
			 */
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if(pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
					
					SharedPreferences settings = getSharedPreferences("data", 0);
					int level = settings.getInt("currLevel", -1);

					if(level == -1){
						SharedPreferences.Editor editor = settings.edit();
						editor.putInt("currLevel", 1);
						editor.commit();
					}
					
					
					go_back.registerEntityModifier(click);
				}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}};

			go_back.setScale((float)0.8);

			
			jump_btn = new ButtonSprite(1050, 600, jump_btn_texture, vertexBufferObjectManager){
				@Override
				/**
				 * Verifica a �rea onde o jogador tocou
				 */
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
						float pTouchAreaLocalX, float pTouchAreaLocalY) {
					if(footContact == true)
					if(pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
						if(state == 3){
							Tutorial.this.toastOnUIThread("You're a pro!",
									Toast.LENGTH_SHORT);
							state = 4;
							if(!gameLogic.leave_box_left(player.getX(), player.getY(), MAP_START_X, MAP_START_Y, SPACING, scene, box_layer, vertexBufferObjectManager, PHYSICS_VISIBILITY))
								gameLogic.leave_box_right(player.getX(), player.getY(), MAP_START_X, MAP_START_Y, SPACING, scene, box_layer, vertexBufferObjectManager, PHYSICS_VISIBILITY);
							
							new Tut().start();
						}
						player_body.setLinearVelocity(new Vector2(player_body.getLinearVelocity().x, -26f));
					}
					return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
				}};

				hud.attachChild(jump_btn);
				jump_btn.setAlpha(1);
				hud.registerTouchArea(jump_btn);

				myChaseCamera.setHUD(hud);

				box_btn =  new ButtonSprite(850, 600 , box_btn_texture,  vertexBufferObjectManager){
					@Override
					/**
					 * Verifica a �rea onde o jogador tocou
					 */
					public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
							float pTouchAreaLocalX, float pTouchAreaLocalY) {
						if(pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
							if(box_btn.getAlpha() == 1){
								Log.d(TAG, "Picked up box");
								if(!carringBox){
									if(direction == LEFT || direction == STOP_LEFT){
										if(gameLogic.remove_box_left(player.getX(), player.getY(), MAP_START_X, MAP_START_Y, SPACING, scene)){
											if(state == 4){
												Tutorial.this.toastOnUIThread("Nicely done!",
														Toast.LENGTH_SHORT);
												state = 5;
												new Tut().start();
											}
											Log.d(TAG, "Box removed");
											carringBox = true;
											new CarringBox().start();
										}else{
											Log.d(TAG, "Box not removed");
										}
									}
									if(direction == RIGHT || direction == STOP_RIGHT){
										if(gameLogic.remove_box_right(player.getX(), player.getY(), MAP_START_X, MAP_START_Y, SPACING, scene)){
											if(state == 4){
												Tutorial.this.toastOnUIThread("Nicely done!",
														Toast.LENGTH_SHORT);
												state = 5;
												new Tut().start();
											}
											Log.d(TAG, "Box removed");
											carringBox = true;
											new CarringBox().start();
										}else{
											Log.d(TAG, "Box not removed");
										}
									}

								}else{
									boolean space_to_place = true;
									if(direction == STOP_LEFT){
										if(gameLogic.leave_box_left(player.getX(), player.getY(), MAP_START_X, MAP_START_Y, SPACING, scene, box_layer, vertexBufferObjectManager, PHYSICS_VISIBILITY)){
											Log.d(TAG, "Box leaved");
											carringBox = false;
											box_btn.setAlpha((float)0.3);
										}else{
											Log.d(TAG, "Could not leave the box");
											space_to_place = false;
										}
									}else if(direction == STOP_RIGHT){
										if(gameLogic.leave_box_right(player.getX(), player.getY(), MAP_START_X, MAP_START_Y, SPACING, scene,  box_layer, vertexBufferObjectManager, PHYSICS_VISIBILITY)){
											Log.d(TAG, "Box leaved");
											carringBox = false;
											box_btn.setAlpha((float)0.3);
										}else{
											Log.d(TAG, "Could not leave the box");
											space_to_place = false;
										}
									}
									if(space_to_place == false)
										Tutorial.this.toastOnUIThread("Not enough space to place the box here",
												Toast.LENGTH_SHORT);
									else{
										if(state == 5){
											Tutorial.this.toastOnUIThread("Cool!",
													Toast.LENGTH_SHORT);
											state = 6;
											new Tut().start();
										}
									}
								}
								//box_btn.registerEntityModifier(click);
							}
						}
						return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
					}};
					hud.attachChild(box_btn);
					box_btn.setAlpha((float) 0.3);
					hud.registerTouchArea(box_btn);

					myChaseCamera.setHUD(hud);



					physicsWorld.setContactListener(createContactListener());

					gameLogic = new Logic(map,physicsWorld);
						
					new Tut().start();

					return scene;
	}

	/**
	 * Cria o mapa de jogo (com gr�ficos) e insere-o na scene
	 */
	private void generateMap() {
		map = new Map();
		map.generateTutorial();
		for(int i = 0; i < map.getHeight(); i++){
			for(int a = 0; a < map.getWidth(); a++){
				if(map.getMap()[a][i].getKind() == "rock"){
					//sprites stuff
					map.getMap()[a][i].setSprite(new Sprite(MAP_START_X + SPACING * a, MAP_START_Y - SPACING * i,rock_layer, vertexBufferObjectManager));
					scene.attachChild(map.getMap()[a][i].getSprite());

					//physics stuff
					IShape box = new Rectangle(MAP_START_X + SPACING * a, MAP_START_Y - SPACING * i, 100, 100, getVertexBufferObjectManager());
					box.setVisible(PHYSICS_VISIBILITY);
					FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0, 1f);
					Body b = PhysicsFactory.createBoxBody(physicsWorld, (IAreaShape) box, BodyType.StaticBody, wallFixtureDef);
					map.getMap()[a][i].setBody(b);
					scene.attachChild(box);


				}else if(map.getMap()[a][i].getKind() == "box"){
					//sprites stuff
					map.getMap()[a][i].setSprite(new Sprite(MAP_START_X + SPACING * a, MAP_START_Y - SPACING * i,box_layer, vertexBufferObjectManager));
					scene.attachChild(map.getMap()[a][i].getSprite());
					//physics suff
					IShape box = new Rectangle(MAP_START_X + SPACING * a, MAP_START_Y - SPACING * i, 100, 100, getVertexBufferObjectManager());
					box.setVisible(PHYSICS_VISIBILITY);
					FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0, 1f);
					Body b = PhysicsFactory.createBoxBody(physicsWorld, (IAreaShape) box, BodyType.StaticBody, wallFixtureDef);
					map.getMap()[a][i].setBody(b);
					b.getFixtureList().get(0).setUserData("box body");
					scene.attachChild(box);
				}else if(map.getMap()[a][i].getKind() == "exit"){
					map.getMap()[a][i].setSprite(new Sprite(MAP_START_X + SPACING * a + 15, MAP_START_Y - SPACING * i + 30,door_layer, vertexBufferObjectManager));
					scene.attachChild(map.getMap()[a][i].getSprite());

					IShape box = new Rectangle(MAP_START_X + SPACING * a + 45, MAP_START_Y - SPACING * i + 90, 10, 10, getVertexBufferObjectManager());
					box.setVisible(PHYSICS_VISIBILITY);
					FixtureDef exitFixtureDef = PhysicsFactory.createFixtureDef(0, 0, 0f);
					exitFixtureDef.isSensor = true;
					Body b = PhysicsFactory.createBoxBody(physicsWorld, (IAreaShape) box, BodyType.StaticBody, exitFixtureDef);
					b.getFixtureList().get(0).setUserData("exit");
				}
			}
		}
	}


	/**
	 * Jogador para
	 */
	private void player_stop() {
		player_stop_left();
	}
	
	/**
	 * Anima��o do jogador a parar virado para a esquerda
	 */
	private void player_stop_left() {
		player.animate(new long[]{50, 50, 50, 50, 50,
				50, 50, 50, 50, 50,
				50, 50, 50, 50, 50,
				50}, new int[] {272, 271, 270, 269, 268
				, 267, 266, 265, 264, 263
				,262, 261, 260, 259, 258
				, 257}, 999);
		//player.animate(new long[]{100, 100}, 12, 13, true);
	}

	/**
	 * Anima��o do jogador a andar para a esquerda
	 */
	private void player_walk_left() {
		long duration = 40;
		if(carringBox) duration = 50;
		player.animate(new long[]{duration, duration, duration, duration, duration,
				duration, duration, duration, duration, duration,
				duration, duration, duration, duration, duration
		}, new int[] {303, 302, 301, 300
				, 299, 298, 297, 296, 295
				,294, 293, 292, 291, 290
				, 289}, 999);
		//player.animate(new long[]{200, 200, 200, 200}, 14, 17, true);
	}

	/**
	 * Anima��o do jogador a andar para a direita
	 */
	private void player_walk_right() {
		long duration = 40;
		if(carringBox) duration = 50;
		player.animate(new long[]{duration, duration, duration, duration, duration,
				duration, duration, duration, duration, duration,
				duration, duration, duration, duration, duration,
				duration}, /*1*/32, /*16*/47, true);
	}

	/**
	 * Anima��o do jogador a parar virado para a direita
	 */
	private void player_stop_right() {
		player.animate(new long[]{50, 50, 50, 50, 50,
				50, 50, 50, 50, 50,
				50, 50, 50, 50, 50,
				50}, 1, 16, true);
	}


	/**
	 * Inicia a f�sica de jogo
	 */
	private void initPhysics()
	{
		physicsWorld = new PhysicsWorld(new Vector2(0, 90f), false);
		new PhysicsHandler(player);

		final IShape bottom = new Rectangle(0, PLAYER_START_Y + 60, 3000, 20, getVertexBufferObjectManager());
		bottom.setVisible(PHYSICS_VISIBILITY);

		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(2, 0, 0f);
		PhysicsFactory.createBoxBody(physicsWorld, (IAreaShape) bottom, BodyType.StaticBody, wallFixtureDef).setUserData("ground");
		scene.attachChild(bottom);

		//Add player-----------
		final FixtureDef playerFixtureDef = PhysicsFactory.createFixtureDef(2, 0, 0f);
		final IShape player_shape = new Rectangle(PLAYER_START_X, PLAYER_START_Y, 40, 60, getVertexBufferObjectManager());

		player_body = PhysicsFactory.createBoxBody(physicsWorld, (IAreaShape) player_shape, BodyType.DynamicBody,  playerFixtureDef);
		player_body.setFixedRotation(true); // prevent rotation

		physicsWorld.registerPhysicsConnector(new PhysicsConnector(player, player_body, true, false));        
		//----------------------
		//Add onFloor sensor-----

		PolygonShape sensor = new PolygonShape();
		sensor.setAsBox((float)0.3, (float)1.2,new Vector2(0, 0), 0);
		player_body.createFixture(sensor, 0).setSensor(true);

		PolygonShape sensor_box = new PolygonShape();
		sensor_box.setAsBox((float)1, (float)0.3,new Vector2(0, 0), 0);
		player_body.createFixture(sensor_box, 0).setSensor(true);

		player_body.getFixtureList().get(2).setUserData("box sensor");
		player_body.getFixtureList().get(1).setUserData("feet");
		player_body.getFixtureList().get(0).setUserData("body");

		scene.registerUpdateHandler(physicsWorld);

	}

	/**
	 * Listener que verifica onde o jogador se encontra atrav�s de sensores � volta dos objetos
	 * @return contactListener
	 */
	private ContactListener createContactListener()
	{
		ContactListener contactListener = new ContactListener()
		{
			@Override
			public void beginContact(Contact contact)
			{
				Log.d(TAG, "Contact A: " + contact.getFixtureA().getUserData() + ", Contact B: " + contact.getFixtureB().getUserData());
				if(state == 6)
				if((contact.getFixtureB().getUserData() == "feet" && contact.getFixtureA().getUserData() == "box body") || 
						(contact.getFixtureA().getUserData() == "feet" && contact.getFixtureB().getUserData() == "box body")){
					footContact = true;
					state = 7;
					new Tut().start();
				}
				
				if(contact.getFixtureB().getUserData() == "feet" || contact.getFixtureA().getUserData() == "feet"){
					footContact = true;
				}

				if(!carringBox){
					if(contact.getFixtureA().getUserData() == "box sensor" && contact.getFixtureB().getUserData() == "box body"){
						box_btn.setAlpha((float)1);
					}else if(contact.getFixtureB().getUserData() == "box sensor" && contact.getFixtureA().getUserData() == "box body"){
						box_btn.setAlpha((float)1);
					}
				}

				if(contact.getFixtureA().getUserData() == "body" && contact.getFixtureB().getUserData() == "exit"){
					Log.d(TAG, "Reached exit");
					player_body.setLinearVelocity(new Vector2(0, 0));
					win = true;
				}else if(contact.getFixtureB().getUserData() == "body" && contact.getFixtureA().getUserData() == "exit"){
					Log.d(TAG, "Reached exit");
					player_body.setLinearVelocity(new Vector2(0, 0));
					win = true;
				}

			}

			@Override
			/**
			 * Fim do contacto entre os objetos
			 */
			public void endContact(Contact contact)
			{
				if(contact.getFixtureB().getUserData() == "feet" || contact.getFixtureA().getUserData() == "feet"){
					footContact = false;
				}
				if(!carringBox){
					if(contact.getFixtureA().getUserData() == "box sensor" && contact.getFixtureB().getUserData() == "box body"){
						box_btn.setAlpha((float)0.3);
					}else if(contact.getFixtureB().getUserData() == "box sensor" && contact.getFixtureA().getUserData() == "box body"){
						box_btn.setAlpha((float)0.3);
					}
				}


			}

			@Override
			public void preSolve(Contact contact, Manifold oldManifold)
			{

			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse)
			{

			}
		};
		return contactListener;
	}

	
	final SequenceEntityModifier click = new SequenceEntityModifier(new FadeOutModifier(0.10f), new FadeInModifier(0.10f){
		@Override
		/**
		 * A a��o executada
		 */
		protected void onModifierStarted(IEntity pItem)
		{
			super.onModifierStarted(pItem);
			// Your action after starting modifier
		}

		@Override
		/**
		 * Cria um novo intent e termina o tutorial
		 */
		protected void onModifierFinished(IEntity pItem)
		{
				super.onModifierFinished(pItem);
				Intent back = new Intent(getBaseContext(), MainMenu.class);
				back.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
				back.putExtra("isBack", "back");
				startActivity(back);
				direction = "finish";
				scene.clearChildScene();
				Tutorial.this.finish();
		}
	});


	/**
	 * Classe CarringBox que d� detach ou attach da caixa � cena
	 * @author Miguel
	 *
	 */
	class CarringBox extends Thread {
		public CarringBox() {
		}
		/**
		 * Fun��o usada para adicionar ou remover objetos � cena de forma segura
		 */
		public void run() {
			final Sprite box = new Sprite(player.getX() + 15, player.getY() - 75, box_layer, vertexBufferObjectManager);
			/**
			 * Fun��o usada para adicionar ou remover objetos � cena de forma segura
			 */
			runOnUpdateThread(new Runnable() {
				@Override
				// to safely detach and re-attach the sprites
				public void run() {
					scene.attachChild(box);
				}
			});

			while(carringBox){
				box.setPosition(player.getX() + 15, player.getY() - 75);
				try {
					sleep(5);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			/**
			 * Fun��o usada para adicionar ou remover objetos � cena de forma segura
			 */
			runOnUpdateThread(new Runnable() {
				@Override
				// to safely detach and re-attach the sprites
				/**
				 * Fun��o usada para adicionar ou remover objetos � cena de forma segura
				 */
				public void run() {
					scene.detachChild(box);
					box.detachSelf();
					box.dispose();
				}
			});
		}
	}
	
	/**
	 * Classe Tut que funciona como uma state machine para o Tutorial
	 * @author Miguel
	 *
	 */
	class Tut extends Thread {
		/**
		 * Construtor vazio de Tut
		 */
		public Tut() {
		}
		/**
		 * Corre o tutorial e espera que o utilizador execute os passos pedidos
		 */
		public void run() {
			if(state == 0){
				try {
					sleep(3000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				state = 1;
			}
			if(state == 1){
				info = new Text(PLAYER_START_X - 830, CAMERA_HEIGHT / 2 - 300, info_font, "Move right by taping the\nright side of the screen",getVertexBufferObjectManager());
				scene.setOnSceneTouchListener(tListener);
				hud.attachChild(info);
			}else if(state == 2){
				info.setText("Now move left");
				info.setX(info.getX() + 100);
			}else if(state == 3){
				info.setText("Now jump by taping the\njump button");
				info.setX(PLAYER_START_X - 830);
			}else if(state == 4){
				info.setText("Now pick up the box,\nget close and hit the\n'BOX' button");
			}else if(state == 5){
				info.setText("Place the box anywhere,\nby tapping the 'BOX'\nbutton again");
			}else if(state == 6){
				info.setText("Now jump over the box!\nYou're almost ready to play");
			}else if(state == 7){
				state = 8;
				info.setText("You're ready!");
				info.setX(info.getX() + 130);
				Tutorial.this.toastOnUIThread("Feel free to go back by pressing\nthe 'back button!",
						Toast.LENGTH_LONG);
				hud.attachChild(go_back);
				hud.registerTouchArea(go_back);
			}

		}
	}

}