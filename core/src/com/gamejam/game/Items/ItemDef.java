package com.gamejam.game.Items;

import com.badlogic.gdx.math.Vector2;

public class ItemDef {

    public Vector2 position;
    public Class<?> type;
    public int ident;


    public ItemDef(Vector2 position, Class<?> type){
        this.position = position;
        this.type = type;
    }


    public ItemDef(Vector2 position, Class<?> type,  Integer ident){
        this.position = position;
        this.type = type;
        this.ident = ident;
    }

}
