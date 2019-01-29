package com.gamejam.game.Items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.gamejam.game.GameJam;
import com.gamejam.game.Scenes.Hud;
import com.gamejam.game.Screens.PlayScreen;
import com.gamejam.game.Sprites.Player;
import com.gamejam.game.Tools.Funcoes;

public class Espada extends Item {

    private int contador;

    public Espada(PlayScreen screen, float x, float y) {
        super(screen, x, y, 32, 32);
        setRegion(screen.getAtlas().findRegion("espada"), 0, 0, 32, 32);
        contador = 0;
        velocity = new Vector2(Funcoes.retornaSin(0.1f, 0), Funcoes.retornaCose(0.1f,0));
    }



    @Override
    public void defineItem() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(), getY());
        bdef.type = BodyDef.BodyType.KinematicBody;
        body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(12 / GameJam.PPM);
        fdef.isSensor = true;
        fdef.filter.categoryBits = GameJam.ITEM_BIT;
        fdef.filter.maskBits = GameJam.PLAYER_BIT;

        fdef.shape = shape;
        body.createFixture(fdef).setUserData(this);
    }

    @Override
    public void use(Player player) {

        player.equipSword();
        GameJam.manager.get("audio/Sword.mp3", Sound.class).play();
        destroy();
    }

    @Override
    public void update(float dt){
        super.update(dt);

        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
        body.setLinearVelocity(velocity);
        contador++;
        if(contador == 360) contador = 0;
        velocity.x = Funcoes.retornaSin(0.1f, contador);
        velocity.y = Funcoes.retornaCose(0.1f, contador);
        body.setLinearVelocity(velocity);
    }
}
