package blockman.logic;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.shape.IAreaShape;
import org.andengine.entity.shape.IShape;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.util.Log;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.blockman.data.Map;
import com.blockman.game.Game;

public class Logic {
	private Map map;
	private PhysicsWorld physicsWorld;
	
	public Logic(Map map,PhysicsWorld physicsWorld){
		this.map = map;
		this.physicsWorld = physicsWorld;
	}
	
	public boolean remove_box_left(float player_pos_x, float player_pos_y, int mapStartX, int mapStartY, int spacing, Scene scene) {
		for(int i = 0; i < map.getHeight(); i++)
			for(int a = 0; a < map.getWidth(); a++){
				int pos_x = mapStartX + a * spacing;
				int pos_y = mapStartY + i * spacing;
				if(pos_x < player_pos_x && pos_x + 100 > player_pos_x)
					if(pos_y < player_pos_y && pos_y + 100 > player_pos_y){
						if(map.getMap()[a][i].getKind() != "blank"){
							scene.detachChild(map.getMap()[a][i].getSprite());
							physicsWorld.destroyBody(map.getMap()[a][i].getBody());
							map.getMap()[a][i].getSprite().detachSelf();
							map.getMap()[a][i].getSprite().dispose();
							map.getMap()[a][i].setBlank();
							return true;
						}
					}
			}
		return false;
	}
	
	public boolean remove_box_right(float player_pos_x, float player_pos_y, int mapStartX, int mapStartY, int spacing, Scene scene) {
		for(int i = 0; i < map.getHeight(); i++)
			for(int a = 0; a < map.getWidth(); a++){
				int pos_x = mapStartX + a * spacing;
				int pos_y = mapStartY + i * spacing;
				if(pos_x - 100 < player_pos_x && pos_x > player_pos_x)
					if(pos_y < player_pos_y && pos_y + 100 > player_pos_y){
						if(map.getMap()[a][i].getKind() != "blank"){
							scene.detachChild(map.getMap()[a][i].getSprite());
							physicsWorld.destroyBody(map.getMap()[a][i].getBody());
							map.getMap()[a][i].getSprite().detachSelf();
							map.getMap()[a][i].getSprite().dispose();
							map.getMap()[a][i].setBlank();
							return true;
						}
					}
			}
		return false;
	}
	
	public boolean leave_box_left(float player_pos_x, float player_pos_y, int mapStartX,
			int mapStartY, int spacing, Scene scene, ITextureRegion box_layer, VertexBufferObjectManager vertexBufferObjectManager, boolean vis) {
		for(int i = 0; i < map.getHeight(); i++)
			for(int a = 0; a < map.getWidth(); a++){
				int pos_x = mapStartX + a * spacing;
				int pos_y = mapStartY + i * spacing;
				if(pos_x - 100 < player_pos_x && pos_x > player_pos_x)
					if(pos_y < player_pos_y && pos_y + 100 > player_pos_y){
						int box_to_change = a - 1;
						int space = 100;
						if(player_pos_x - (pos_x - 100) <= 60){
							box_to_change = a - 2;
							space = 200;
						}
							if(map.getMap()[a - 1][i].getKind() != "rock" && map.getMap()[a - 1][i].getKind() != "box" )	
								if(map.getMap()[box_to_change][i].getKind() == "blank"){
									//Sprite
									map.getMap()[box_to_change][i].setSprite(new Sprite(pos_x-space, pos_y ,box_layer, vertexBufferObjectManager));
									map.getMap()[box_to_change][i].setKind("box");
									scene.attachChild(map.getMap()[box_to_change][i].getSprite());
									Log.d("BlockMan","Player pos " + player_pos_x);
									Log.d("BlockMan","Box pos " + (pos_x - space));
									Log.d("BlockMan","Space between = " + (player_pos_x - (pos_x - 100)));
									//Placing physics Body
									IShape box = new Rectangle(pos_x - space, pos_y, 100, 100, vertexBufferObjectManager);
									box.setVisible(vis);
									FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0, 1f);
									Body b = PhysicsFactory.createBoxBody(physicsWorld, (IAreaShape) box, BodyType.StaticBody, wallFixtureDef);
									b.getFixtureList().get(0).setUserData("box body");
									map.getMap()[box_to_change][i].setBody(b);

									return true;
								}
					}
			}
		return false;
	}

	public boolean leave_box_right(float player_pos_x, float player_pos_y, int mapStartX,
			int mapStartY, int spacing, Scene scene, ITextureRegion box_layer, VertexBufferObjectManager vertexBufferObjectManager, boolean vis) {
		for(int i = 0; i < map.getHeight(); i++)
			for(int a = 0; a < map.getWidth(); a++){
				int pos_x = mapStartX + a * spacing;
				int pos_y = mapStartY + i * spacing;
				if(pos_x - 100 < player_pos_x && pos_x > player_pos_x)
					if(pos_y < player_pos_y && pos_y + 100 > player_pos_y){
						if(map.getMap()[a][i].getKind() != "rock" && map.getMap()[a][i].getKind() != "box" )
						if(map.getMap()[a + 1][i].getKind() == "blank"){
							//Placing sprites
							map.getMap()[a + 1][i].setSprite(new Sprite(pos_x + 100, pos_y ,box_layer, vertexBufferObjectManager));
							map.getMap()[a + 1][i].setKind("box");
							scene.attachChild(map.getMap()[a + 1][i].getSprite());
							//Placing physics Body
							IShape box = new Rectangle(pos_x + 100, pos_y, 100, 100, vertexBufferObjectManager);
		                    box.setVisible(vis);
		                    FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0, 1f);
		                    Body b = PhysicsFactory.createBoxBody(physicsWorld, (IAreaShape) box, BodyType.StaticBody, wallFixtureDef);
		                    b.getFixtureList().get(0).setUserData("box body");
		                    map.getMap()[a + 1][i].setBody(b);
							return true;
						}
						else
							Log.d("BlockMan", "kind : " + map.getMap()[a][i].getKind());
					}
			}
		return false;
	}
}