package com.blockman.game;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import blockman.logic.Logic;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.engine.camera.BoundCamera;
import org.andengine.engine.camera.hud.HUD;
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
 * Classe Game que gere um jogo criado, seja qual for o n�vel
 * @author Miguel
 *
 */
public class Game extends SimpleBaseGameActivity {
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
	 * Esquerda com colis�o
	 */
	private static final String LEFT_W_COLLISION = "left collision";
	
	/**
	 * Direita com colis�o
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
	 * In�cio do jogador na coordenada X
	 */
	final float PLAYER_START_X = 300 + 100 * 16;
	
	/**
	 * In�cio do jogador na coordenada Y
	 */
	final float PLAYER_START_Y = 7 * CAMERA_HEIGHT / 12 - 60;

	/**
	 * Visibilidade da f�sica de debug
	 */
	final boolean PHYSICS_VISIBILITY = false;

	/**
	 * Necess�rio � cria��o de sprites
	 */
	private VertexBufferObjectManager vertexBufferObjectManager;
	
	/**
	 * Sprite de retorno
	 */
	private Sprite back;
	//--------------------------
	//Camera--------------------
	/**
	 * Camara usada
	 */
	private BoundCamera myChaseCamera;
	//--------------------------
	//Physics-------------------
	
	/**
	 * F�sica que gere o jogo
	 */
	private PhysicsWorld physicsWorld;
	
	/**
	 * Corpo do jogador
	 */
	private Body player_body;
	
	/**
	 * Contacto com o ch�o
	 */
	private int footContact = 0; //how many footcontact there is

	//--------------------------
	//Add Scene-----------------
	/**
	 * Cena onde s�o carregados todos os elementos pretendidos
	 */
	private Scene scene;
	
	/**
	 * HUD
	 */
	private HUD hud;
	//--------------------------
	//Buttons-------------------
	/**
	 * Bitmap do bot�o de retorno
	 */
	private BitmapTextureAtlas go_back_bmp;
	
	/**
	 * Textura do bot�o de retorno
	 */
	private ITextureRegion go_back_texture;
	
	/**
	 * ButtonSprite do bot�o de retorno
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
	 * Bitmap do bot�o de salto
	 */
	private BitmapTextureAtlas jump_btn_bmp;
	
	/**
	 * Textura do bot�o de salto
	 */
	private ITextureRegion jump_btn_texture;
	
	/**
	 * ButtonSprite do bot�o de salto
	 */
	private ButtonSprite jump_btn;

	/**
	 * Bitmap do bot�o de refresh
	 */
	private BitmapTextureAtlas refresh_btn_bmp;
	
	/**
	 * Textura do bot�o de refresh
	 */
	private ITextureRegion refresh_btn_texture;
	
	/**
	 * ButtonSprite do bot�o de refresh
	 */
	private ButtonSprite refresh_btn;

	/**
	 * Bitmap do bot�o de play
	 */
	private BitmapTextureAtlas play_btn_bmp;
	
	/**
	 * Textura do bot�o de play
	 */
	private ITextureRegion play_btn_texture;
	//private BitmapTextureAtlas stop_btn_bmp;
	//private ITextureRegion stop_btn_texture;
	/**
	 * ButtonSprite do bot�o de play
	 */
	private ButtonSprite play_btn;
	//--------------------------
	/**
	 * Dire��o seguida pelo jogador
	 */
	String direction = "";
	//--------------------------
	//Sprites------------------
	/**
	 * Textura do layer frontal
	 */
	private ITextureRegion myLayerFront;
	
	/**
	 * Textura do fundo
	 */
	private BitmapTextureAtlas myBackgroundTexture;

	/**
	 * Textura do atlas
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
	 * Mapa de jogo
	 */
	Map map;
	
	/**
	 * Textura da pedra
	 */
	private ITextureRegion rock_layer;
	
	/**
	 * Bitmap da pedras
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
	//--------
	//MUSIC
	/**
	 * Music Player que gere toda a m�sica de jogo
	 */
	private Music musicPlayer;
	//--------

	//---------------------------
	//Text------------------------
	/**
	 * Fonte usada no t�tulo
	 */
	private Font title_font;
	
	/**
	 * Texto do t�tulo
	 */
	private Text title;
	
	/**
	 * Texto da mensagem de vit�ria
	 */
	private Text winningMessage;
	//--------------------------
	//---------------------
	/**
	 * L�gica do jogo
	 */
	Logic gameLogic =  new Logic();
	
	/**
	 * N�vel onde o utilizador se encontra
	 */
	private int curr_level;
	//--------------------------
	
	//Tests
	/**
	 * Jogo para efeito de testes
	 */
	private static Game game;
	//

	@Override
	/**
	 * Cria o Engine usado
	 */
	public EngineOptions onCreateEngineOptions() {
		this.myChaseCamera = new BoundCamera(CAMERA_WIDTH, CAMERA_HEIGHT , CAMERA_WIDTH ,CAMERA_HEIGHT);
		//myChaseCamera.setCenter(CAMERA_WIDTH/3, 7 * CAMERA_HEIGHT / 9);
		myChaseCamera.setBoundsEnabled(true);
		myChaseCamera.setBounds(0 , -2000, 3000 , 1708);

		EngineOptions options = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new FillResolutionPolicy(), myChaseCamera);

		options.getAudioOptions().setNeedsMusic(true);
		options.getAudioOptions().setNeedsSound(true);

		return options;
	}

	@Override
	/**
	 * Cria e carrega todos os recursos como imagens, m�sica e fontes
	 */
	protected void onCreateResources() {

		try {
			if(musicPlayer != null){
        		musicPlayer.release();
        	}
			this.musicPlayer = MusicFactory.createMusicFromAsset(mEngine.getMusicManager(), this,"music.mp3");
			this.musicPlayer.setLooping(true);
			Log.d(TAG, "Leu musica");
		} catch (IllegalStateException e) {
			Log.d(TAG, "Erro a ler musica");
			e.printStackTrace();
		} catch (IOException e) {
			Log.d(TAG, "Erro a ler musica");
			e.printStackTrace();
		}	

		this.myBackgroundTexture = new BitmapTextureAtlas(this.getTextureManager(), 3000, 1708);
		this.myLayerFront = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.myBackgroundTexture, this, "ingame_back.jpg", 0, 0);
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

		this.play_btn_bmp = new BitmapTextureAtlas(this.getTextureManager(), 144, 144, TextureOptions.BILINEAR);
		this.play_btn_texture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.play_btn_bmp, this, "play.png", 0, 0);
		this.play_btn_bmp.load();

		this.jump_btn_bmp = new BitmapTextureAtlas(this.getTextureManager(), 144, 144, TextureOptions.BILINEAR);
		this.jump_btn_texture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.jump_btn_bmp, this, "jump_btn.png", 0, 0);
		this.jump_btn_bmp.load();

		this.refresh_btn_bmp = new BitmapTextureAtlas(this.getTextureManager(), 144, 144, TextureOptions.BILINEAR);
		this.refresh_btn_texture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.refresh_btn_bmp, this, "retry.png", 0, 0);
		this.refresh_btn_bmp.load();


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

		this.rock_bmp = new BitmapTextureAtlas(this.getTextureManager(), 100, 100, TextureOptions.BILINEAR);
		this.rock_layer = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.rock_bmp, this, "img/rock.png", 0, 0);
		this.rock_bmp.load();

		this.title = new Text(PLAYER_START_X - 190, CAMERA_HEIGHT / 2 - 300, title_font, "LEVEL ",getVertexBufferObjectManager());

	}

	@Override
	/**
	 * Cria uma nova cena e adiciona-lhe todos os elementos necess�rios ao funcionamento do jogo
	 */
	protected Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		scene = new Scene();
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

		this.winningMessage = new Text(200, 50, title_font, "LEVEL COMPLETED",getVertexBufferObjectManager());

		scene.attachChild(player);
		scene.attachChild(title);

		Log.d(TAG, "Player height :" + player.getHeight());
		Log.d(TAG, "Player width :" + player.getWidth());

		FadeOutModifier mModifier = new FadeOutModifier(5.0f);
		title.registerEntityModifier(mModifier);

		scene.setOnSceneTouchListener(new IOnSceneTouchListener() {
			@Override
			public boolean onSceneTouchEvent(Scene pScene, final TouchEvent pSceneTouchEvent) {
				if (pSceneTouchEvent.isActionDown()) {
					System.currentTimeMillis();
					if(!gameLogic.getWin())
						if(pSceneTouchEvent.getX() > player.getX()) {
							if(direction != RIGHT) {
								if(direction != RIGHT_W_COLLISION) {
									player_walk_right();
									if(!gameLogic.getCarringBox())
										player_body.setLinearVelocity(new Vector2(6, player_body.getLinearVelocity().y));
									else
										player_body.setLinearVelocity(new Vector2(3, player_body.getLinearVelocity().y));
									direction = RIGHT;
								}
							}
						}else{
							if(direction != LEFT) {
								if(direction != LEFT_W_COLLISION) {
									player_walk_left();
									if(!gameLogic.getCarringBox())
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
					if(!gameLogic.getWin())
						if(pSceneTouchEvent.getX() > player.getX()) {
							if(direction != RIGHT) {
								if(direction != RIGHT_W_COLLISION) {
									player_walk_right();
								}
							}
						}else{
							if(direction != LEFT) {
								if(direction != LEFT_W_COLLISION) {
									player_walk_left();
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


		});

		myChaseCamera.setChaseEntity(player);

		//Botao em hud
		hud = new HUD();
		go_back =  new ButtonSprite(25, 25 , go_back_texture,  vertexBufferObjectManager){
			@Override
			/**
			 * Verifica a �rea tocada pelo utilizador
			 */
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if(pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
					go_back.registerEntityModifier(click);
				}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}};

			hud.attachChild(go_back);
			hud.registerTouchArea(go_back);
			go_back.setScale((float)0.8);

			refresh_btn =  new ButtonSprite(150, 25 , refresh_btn_texture,  vertexBufferObjectManager){
				@Override
				/**
				 * Verifica a �rea tocada pelo utilizador
				 */
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
						float pTouchAreaLocalX, float pTouchAreaLocalY) {
					if(pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
						refresh_btn.registerEntityModifier(click);
					}
					return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
				}};

				hud.attachChild(refresh_btn);
				hud.registerTouchArea(refresh_btn);
				refresh_btn.setScale(0.8f);

				play_btn = new ButtonSprite(1050, 50, play_btn_texture, vertexBufferObjectManager){
					@Override
					/**
					 * Verifica a �rea tocada pelo utilizador
					 */
					public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
							float pTouchAreaLocalX, float pTouchAreaLocalY) {
						if(gameLogic.getWin() == false)
							if(pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
								if (musicPlayer.isPlaying())
								{
									Log.d(TAG, "Pause Music");
									play_btn.setAlpha((float) 0.3);
									SharedPreferences settings = getSharedPreferences("data", 0);
									SharedPreferences.Editor editor = settings.edit();
									editor.putBoolean("sound", false);
									editor.commit();

									//play_btn_texture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.play_btn_bmp, this, "stop.png", 0, 0);
									musicPlayer.pause();
								}
								else
								{
									Log.d(TAG, "Play Music");
									play_btn.setAlpha(1);
									SharedPreferences settings = getSharedPreferences("data", 0);
									SharedPreferences.Editor editor = settings.edit();
									editor.putBoolean("sound", true);
									editor.commit();
									//play_btn_texture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.play_btn_bmp, this, "play.png", 0, 0);
									musicPlayer.resume();
								}
							}
						return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
					}};


					hud.attachChild(play_btn);
					play_btn.setAlpha(1);
					hud.registerTouchArea(play_btn);	


					jump_btn = new ButtonSprite(1050, 600, jump_btn_texture, vertexBufferObjectManager){
						@Override
						/**
						 * Verifica a �rea tocada pelo utilizador
						 */
						public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
								float pTouchAreaLocalX, float pTouchAreaLocalY) {
							if(footContact > 0 && gameLogic.getWin() == false)
								if(pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
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
							 * Verifica a �rea tocada pelo utilizador
							 */
							public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
									float pTouchAreaLocalX, float pTouchAreaLocalY) {
								if(pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
									if(box_btn.getAlpha() == 1){
										Log.d(TAG, "Picked up box");
										if(!gameLogic.getCarringBox()){
											if(direction == LEFT || direction == STOP_LEFT){
												if(gameLogic.remove_box_left(player.getX(), player.getY(), MAP_START_X, MAP_START_Y, SPACING, scene)){
													Log.d(TAG, "Box removed");
													gameLogic.setCarringBox(true);
													new CarringBox().start();
												}else{
													Log.d(TAG, "Box not removed");
												}
											}
											if(direction == RIGHT || direction == STOP_RIGHT){
												if(gameLogic.remove_box_right(player.getX(), player.getY(), MAP_START_X, MAP_START_Y, SPACING, scene)){
													Log.d(TAG, "Box removed");
													gameLogic.setCarringBox(true);
													new CarringBox().start();
												}else{
													Log.d(TAG, "Box not removed");
												}
											}

										}else{
											boolean space_to_place = true;
											if(direction == STOP_LEFT){
												Log.d("Teste", "Player Y = " + player.getY() + " Map start y = " + MAP_START_Y);
												if(gameLogic.leave_box_left(player.getX(), player.getY(), MAP_START_X, MAP_START_Y, SPACING, scene, box_layer, vertexBufferObjectManager, PHYSICS_VISIBILITY)){
													Log.d(TAG, "Box leaved");
													gameLogic.setCarringBox(false);
													box_btn.setAlpha((float)0.3);
												}else{
													Log.d(TAG, "Could not leave the box");
													space_to_place = false;
												}
											}else if(direction == STOP_RIGHT){
												if(gameLogic.leave_box_right(player.getX(), player.getY(), MAP_START_X, MAP_START_Y, SPACING, scene,  box_layer, vertexBufferObjectManager, PHYSICS_VISIBILITY)){
													Log.d(TAG, "Box leaved");
													gameLogic.setCarringBox(false);
													box_btn.setAlpha((float)0.3);
												}else{
													Log.d(TAG, "Could not leave the box");
													space_to_place = false;
												}
											}
											if(space_to_place == false)
												Game.this.toastOnUIThread("Not enough space to place the box here",
														Toast.LENGTH_SHORT);
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

							gameLogic.setMap(map);
							
							SharedPreferences settings = getSharedPreferences("data", 0);
					        boolean sound = settings.getBoolean("sound", true);
					        if(sound){
					        	musicPlayer.play();
					        	musicPlayer.resume();
					        	play_btn.setAlpha(1f);
					        }else{
					        	play_btn.setAlpha(0.4f);
					        }

							return scene;
	}


	/**
	 * Gera o mapa de jogo de acordo com o n�vel atual
	 */
	private void generateMap() {
		map = new Map();

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String data = (String)extras.getString("level");
			title.setText(title.getText() + data);
			curr_level = Integer.parseInt(data);
			if (data.equals("1"))
				map.generateMap();
			else if (data.equals("2"))
				map.generateLevel2();
			else if (data.equals("3"))
				map.generateLevel3();
			else if (data.equals("4"))
				map.generateLevel4();
			else if (data.equals("5"))
				map.generateLevel5();
			else if (data.equals("6"))
				map.generateTutorial();
			else map.generateMap();
		}

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
	 * Anima��o do jogador a parar virado para a direita
	 */
	private void player_walk_left() {
		//Animate
		long duration = 40;
		if(gameLogic.getCarringBox()) duration = 50;
		player.animate(new long[]{duration, duration, duration, duration, duration,
				duration, duration, duration, duration, duration,
				duration, duration, duration, duration, duration
		}, new int[] {303, 302, 301, 300
				, 299, 298, 297, 296, 295
				,294, 293, 292, 291, 290
				, 289}, 999);
		//Set speed
		if(!gameLogic.getCarringBox())
			player_body.setLinearVelocity(new Vector2(-6, player_body.getLinearVelocity().y));
		else
			player_body.setLinearVelocity(new Vector2(-3, player_body.getLinearVelocity().y));
		direction = LEFT;
	}

	/**
	 * Anima��o do jogador a andar para a direita
	 */
	private void player_walk_right() {
		//Animate
		long duration = 40;
		if(gameLogic.getCarringBox()) duration = 50;
		player.animate(new long[]{duration, duration, duration, duration, duration,
				duration, duration, duration, duration, duration,
				duration, duration, duration, duration, duration,
				duration}, 32, 47, true);
		//Set speed
		if(!gameLogic.getCarringBox())
			player_body.setLinearVelocity(new Vector2(6, player_body.getLinearVelocity().y));
		else
			player_body.setLinearVelocity(new Vector2(3, player_body.getLinearVelocity().y));
		direction = RIGHT;
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
	 * Inicia a f�sica do jogo
	 */
	private void initPhysics()
	{
		physicsWorld = new PhysicsWorld(new Vector2(0, 90f), false);
		//playerPhysics = new PhysicsHandler(player);

		gameLogic.setPhysics(physicsWorld);
		
		final IShape bottom = new Rectangle(0, PLAYER_START_Y + 60, 3000, 20, getVertexBufferObjectManager());
		bottom.setVisible(PHYSICS_VISIBILITY);

		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(2, 0, 0f);
		PhysicsFactory.createBoxBody(physicsWorld, (IAreaShape) bottom, BodyType.StaticBody, wallFixtureDef).setUserData("ground");
		scene.attachChild(bottom);

		//Add player-----------
		final FixtureDef playerFixtureDef = PhysicsFactory.createFixtureDef(0, 0, 0f);
		final IShape player_shape = new Rectangle(PLAYER_START_X, PLAYER_START_Y, 40, 60, getVertexBufferObjectManager());

		player_body = PhysicsFactory.createBoxBody(physicsWorld, (IAreaShape) player_shape, BodyType.DynamicBody,  playerFixtureDef);
		player_body.setFixedRotation(true); // prevent rotation
     
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
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(player, player_body, true, false));   
		scene.registerUpdateHandler(physicsWorld);

	}

	/**
	 * Fun��o que verifica o contacto entre o jogador e os objetos atrav�s de sensores
	 * @return contactListener
	 */
	private ContactListener createContactListener()
	{
		ContactListener contactListener = new ContactListener()
		{
			@Override
			/**
			 * In�cio do contacto entre o jogador e elementos
			 */
			public void beginContact(Contact contact)
			{
				Log.d(TAG, "Contact A: " + contact.getFixtureA().getUserData() + ", Contact B: " + contact.getFixtureB().getUserData());
				if(contact.getFixtureB().getUserData() == "feet" || contact.getFixtureA().getUserData() == "feet"){
					footContact++;
				}

				if(!gameLogic.getCarringBox()){
					if(contact.getFixtureA().getUserData() == "box sensor" && contact.getFixtureB().getUserData() == "box body"){
						box_btn.setAlpha((float)1);
					}else if(contact.getFixtureB().getUserData() == "box sensor" && contact.getFixtureA().getUserData() == "box body"){
						box_btn.setAlpha((float)1);
					}
				}

				if(contact.getFixtureA().getUserData() == "body" && contact.getFixtureB().getUserData() == "exit"){
					Log.d(TAG, "Reached exit");
					hud.attachChild(winningMessage);
					go_back.setPosition(350, 325);
					go_back.setScale(1.5f);

					
					refresh_btn.setScale(1.5f);
					refresh_btn.setPosition(1270 - 450, 325);

					//player_body.setLinearVelocity(new Vector2(0, 0));
					jump_btn.setAlpha((float) 0.3);
					gameLogic.setWin(true);
					
					Game.this.toastOnUIThread("Hit 'back' if you want to pick another level",
							Toast.LENGTH_SHORT);
				}else if(contact.getFixtureB().getUserData() == "body" && contact.getFixtureA().getUserData() == "exit"){
					Log.d(TAG, "Reached exit");
					hud.attachChild(winningMessage);
					go_back.setPosition(350, 325);
					go_back.setScale(1.5f);
					go_back.setAlpha(0.9f);

					hud.attachChild(refresh_btn);
					refresh_btn.setScale(1.5f);
					hud.registerTouchArea(refresh_btn);
					//player_body.setLinearVelocity(new Vector2(0, 0));
					jump_btn.setAlpha((float) 0.3);
					gameLogic.setWin(true);
					Game.this.toastOnUIThread("Hit 'back' if you want to pick another level",
							Toast.LENGTH_SHORT);
				}

			}

			@Override
			/**
			 * Fim do contacto entre o jogador e os elementos do jogo
			 */
			public void endContact(Contact contact)
			{
				if(contact.getFixtureB().getUserData() == "feet" || contact.getFixtureA().getUserData() == "feet"){
					footContact--;
				}
				if(!gameLogic.getCarringBox()){
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
		protected void onModifierStarted(IEntity pItem)
		{
			super.onModifierStarted(pItem);
			// Your action after starting modifier
		}

		@Override
		/**
		 * Cria um novo intent e sai do n�vel
		 */
		protected void onModifierFinished(IEntity pItem)
		{		
			super.onModifierFinished(pItem);
			if(pItem == go_back){
				if(gameLogic.getWin()){
					SharedPreferences settings = getSharedPreferences("data", 0);
					int level = settings.getInt("currLevel", -1);

					if(level != -1){
						SharedPreferences.Editor editor = settings.edit();
						editor.putInt("currLevel", curr_level + 1);
						editor.commit();
					}
					if(musicPlayer != null){
		        		musicPlayer.release();
		        	}
				}
				Intent back = new Intent(getBaseContext(), MainMenu.class);
				back.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
				back.putExtra("isBack", "back");
				startActivity(back);
				scene.clearChildScene();
				Game.this.finish();
			}else if(pItem == refresh_btn){
				Intent intent = getIntent();
				finish();
				startActivity(intent);
			}
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

			while(gameLogic.getCarringBox()){
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

}