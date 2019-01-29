package com.gamejam.game.Tools;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.gamejam.game.GameJam;
import com.gamejam.game.Screens.PlayScreen;
import com.gamejam.game.Sprites.Caveira;
import com.gamejam.game.Sprites.Coin;
import com.gamejam.game.Sprites.Enemy;
import com.gamejam.game.Sprites.Ghost;

public class B2WorldCreator {

    private static Array<Enemy> enemies;

    public B2WorldCreator(PlayScreen screen){
        World world = screen.getWorld();
        TiledMap map = screen.getMap();

        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;

        // Create ground bodies / fixtures
        for(MapObject object : map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / GameJam.PPM, (rect.getY() + rect.getHeight() / 2) / GameJam.PPM );

            body = world.createBody(bdef);

            shape.setAsBox(rect.getWidth() / 2 / GameJam.PPM , rect.getHeight() / 2 / GameJam.PPM);
            fdef.shape = shape;
            body.createFixture(fdef);
        }

        // Create coin bodies / fixtures
        for(MapObject object : map.getLayers().get(8).getObjects().getByType(RectangleMapObject.class)){
            new Coin(screen, object);
        }


        enemies = new Array<Enemy>();

        for(MapObject object : map.getLayers().get(6).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            Ghost ghost = new Ghost(screen, rect.getX() / GameJam.PPM, rect.getY() / GameJam.PPM);
            if(object.getProperties().containsKey("especial")){
                ghost.aplicarGiro();
            }
            if(object.getProperties().containsKey("noDamage")){
                ghost.setCanTakeDamage(false);
            }
            enemies.add(ghost);
        }
        for(MapObject object : map.getLayers().get(9).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            Caveira caveira = new Caveira(screen, rect.getX() / GameJam.PPM, rect.getY() / GameJam.PPM);
            enemies.add(caveira);
        }
    }


    public static Array<Enemy> getEnemies(){
        return enemies;
    }

}
