package com.gamejam.game.Tools;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.gamejam.game.GameJam;
import com.gamejam.game.Items.Item;
import com.gamejam.game.Sprites.Enemy;
import com.gamejam.game.Sprites.Ghost;
import com.gamejam.game.Sprites.InteractiveTileObject;
import com.gamejam.game.Sprites.Player;

public class WorldContactListener implements ContactListener {


    @Override
    public void beginContact(Contact contact) {

        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        // Bitmask com os bits da colisão
        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        // Checa que colisão aconteceu
        switch(cDef){
                // caso cabeça do inimigo e Player
            case GameJam.GHOST_HEAD_BIT | GameJam.PLAYER_BIT:
                if(fixA.getFilterData().categoryBits == GameJam.GHOST_HEAD_BIT)
                    ((Enemy) fixA.getUserData()).hitOnHead((Player) fixB.getUserData());
                else
                    ((Enemy) fixB.getUserData()).hitOnHead((Player) fixA.getUserData());
                break;
                // case player e inimigo
            case GameJam.PLAYER_BIT | GameJam.ENEMY_BIT:
            case GameJam.PLAYER_HEAD_BIT | GameJam.ENEMY_BIT:
                if(fixA.getFilterData().categoryBits == GameJam.PLAYER_BIT)
                    ((Player) fixA.getUserData()).hitBy((Enemy) fixB.getUserData());
                else
                    ((Player) fixB.getUserData()).hitBy((Enemy) fixA.getUserData());
                break;
            case GameJam.ENEMY_BIT | GameJam.GROUND_BIT:
                break;
            // caso inimigo com inimigo
            case GameJam.ENEMY_BIT:
                //((Enemy) fixA.getUserData()).onEnemyHit((Enemy) fixB.getUserData());
                //((Enemy) fixB.getUserData()).onEnemyHit((Enemy) fixA.getUserData());
                break;
                // caso item com moeda
            case GameJam.PLAYER_BIT | GameJam.OBJECT_BIT:
                if(fixA.getFilterData().categoryBits == GameJam.PLAYER_BIT)
                    ((InteractiveTileObject) fixB.getUserData()).onCollision((Player) fixA.getUserData());
                else
                    ((InteractiveTileObject) fixA.getUserData()).onCollision((Player) fixB.getUserData());
                break;
            case GameJam.ITEM_BIT | GameJam.PLAYER_BIT:
                if(fixA.getFilterData().categoryBits == GameJam.ITEM_BIT)
                    ((Item) fixA.getUserData()).use((Player) fixB.getUserData());
                else
                    ((Item) fixB.getUserData()).use((Player) fixA.getUserData());
                break;
        }
    }

    @Override
    public void endContact(Contact contact) {

        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();
        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;
        switch (cDef){
            case GameJam.GHOST_HEAD_BIT | GameJam.PLAYER_BIT:
                if(fixA.getFilterData().categoryBits == GameJam.GHOST_HEAD_BIT) {
                    ((Ghost) fixA.getUserData()).stopSad();
                    ((Player) fixB.getUserData()).leaveGhost();
                }
                else {
                    ((Ghost) fixB.getUserData()).stopSad();
                    ((Player) fixA.getUserData()).leaveGhost();
                }
                break;
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
