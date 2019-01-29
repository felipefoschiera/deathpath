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

public class Heart extends Item {

    private int contador;

    public Heart(PlayScreen screen, float x, float y) {
        super(screen, x, y, 16, 16);
        setRegion(screen.getAtlas().findRegion("heart"), 0, 0, 16, 16);
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
        shape.setRadius(6 / GameJam.PPM);
        fdef.isSensor = true;
        fdef.filter.categoryBits = GameJam.ITEM_BIT;
        fdef.filter.maskBits = GameJam.PLAYER_BIT |
                GameJam.OBJECT_BIT |
                GameJam.GROUND_BIT;

        fdef.shape = shape;
        body.createFixture(fdef).setUserData(this);
    }

    @Override
    public void use(Player player) {
        double ganho = 2.5, increase, max_hp = 10.0;
        if(player.getVida() == max_hp) return;
        if(player.getVida() + ganho > max_hp){
            increase = max_hp - player.getVida();
        }else{
            increase = ganho;
        }
        player.setVida(player.getVida() + increase);
        GameJam.manager.get("audio/Collect.mp3", Sound.class).play();
        Hud.setHP(player.getVida());
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
