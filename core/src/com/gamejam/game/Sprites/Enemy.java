package com.gamejam.game.Sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.gamejam.game.Screens.PlayScreen;

public abstract class Enemy extends Sprite {

    protected World world;
    protected PlayScreen screen;
    public Body b2body;
    public Vector2 velocity;
    public float startX, startY;
    public double dano;
    public double hp;
    public double danoCounter;

    public boolean canTakeDamage;

    public boolean toDestroy;
    public boolean destroyed;
    public boolean tomarDano;

    public Enemy(PlayScreen screen, float x, float y){
        this.world = screen.getWorld();
        this.screen = screen;
        setPosition(x, y);
        startX = x;
        startY = y;
        defineEnemy();
        velocity = new Vector2(0, 0);
        tomarDano = false;
        toDestroy = false;
        destroyed = false;
        canTakeDamage = true;
        danoCounter = 0.0;
        b2body.setActive(false);
    }

    public double getDano() { return dano; }

    protected abstract void defineEnemy();
    public abstract void hitOnHead(Player player);
    public abstract void gotHit(Player player, double dano);
    public abstract void update(float dt);
    public abstract void die();

    public void destroy(){
        toDestroy = true;
    }


    public void setCanTakeDamage(boolean b){
        canTakeDamage = false;
    }

    public void setDano(double dano){
        this.dano = dano;
    }

    public void reverseVelocity(boolean x, boolean y){
        if(x)
            velocity.x = -velocity.x;
        if(y)
            velocity.y = -velocity.y;
    }

    public double getHP() { return hp; }

    public void setHP(double hp){
        this.hp = hp;
    }

}
