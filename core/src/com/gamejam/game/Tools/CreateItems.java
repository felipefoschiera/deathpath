package com.gamejam.game.Tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.gamejam.game.GameJam;
import com.gamejam.game.Items.Espada;
import com.gamejam.game.Items.Heart;
import com.gamejam.game.Items.ItemDef;
import com.gamejam.game.Items.Portal;
import com.gamejam.game.Screens.PlayScreen;

public class CreateItems {

    public CreateItems(PlayScreen screen){
        World world = screen.getWorld();
        TiledMap map = screen.getMap();

        for(MapObject object : map.getLayers().get(7).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rectangle = ((RectangleMapObject) object).getRectangle();
            screen.spawnItem(new ItemDef(new Vector2((rectangle.getX() + rectangle.getWidth() / 2) / GameJam.PPM, (rectangle.getY() + rectangle.getHeight() / 2) / GameJam.PPM),
                    Heart.class));
        }

        for(MapObject object : map.getLayers().get(10).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rectangle = ((RectangleMapObject) object).getRectangle();
            screen.spawnItem(new ItemDef(new Vector2((rectangle.getX() + rectangle.getWidth() / 2) / GameJam.PPM, (rectangle.getY() + rectangle.getHeight() / 2) / GameJam.PPM),
                    Espada.class));
        }
        for(MapObject object : map.getLayers().get(11).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rectangle = ((RectangleMapObject) object).getRectangle();
            if(object.getProperties().containsKey("id")) {
                int id = (Integer) object.getProperties().get("id");
                screen.spawnItem(new ItemDef(new Vector2((rectangle.getX() + rectangle.getWidth() / 2) / GameJam.PPM, (rectangle.getY() + rectangle.getHeight() / 2) / GameJam.PPM),
                        Portal.class, id));
            }
        }
    }
}
