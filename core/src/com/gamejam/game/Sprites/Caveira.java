package com.gamejam.game.Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.gamejam.game.GameJam;
import com.gamejam.game.Scenes.Hud;
import com.gamejam.game.Screens.PlayScreen;
import com.gamejam.game.Tools.B2WorldCreator;
import com.gamejam.game.Tools.Funcoes;

import javax.xml.soap.Text;

public class Caveira extends Enemy {

    private enum State { NORMAL, DANO };
    private State currentState;
    private State previousState;
    private float stateTime;
    private TextureRegion caveiraRegion;
    private TextureRegion danoRegion;
    private double dano = 1;
    private double hp = 6.0;

    private int contador;
    public Caveira(PlayScreen screen, float x, float y){
        super(screen, x, y);

        velocity = new Vector2(0, 0f);
        setBounds(getX() - getWidth() / 2, getY()- getHeight() / 2, 32 / GameJam.PPM, 32 / GameJam.PPM);

        stateTime = 0;
        contador = 0;

        caveiraRegion = new TextureRegion(screen.getAtlas().findRegion("caveira"), 0, 0, 32, 32);
        danoRegion = new TextureRegion(screen.getAtlas().findRegion("caveira_dano"), 0, 0, 32, 32);

        currentState = State.NORMAL;
        setDano(dano);
        setHP(hp);
    }

    public void update(float dt) {
        if (toDestroy && !destroyed) {
            world.destroyBody(b2body);
            destroyed = true;
        } else if (!destroyed) {
            stateTime += dt;
            contador++;
            if (contador == 360) contador = 0;
            velocity.x = Funcoes.retornaCosCos(0.2f, contador, (float) (3.0));
            velocity.y = Funcoes.retornaCosSin(0.2f, contador, (float) (3.0));
            b2body.setLinearVelocity(velocity);
            setPosition(b2body.getPosition().x -= getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
            setRegion(getFrame(dt));
            if (tomarDano) {
                danoCounter += dt;
                if (danoCounter > 0.25) {
                    tomarDano = false;
                }
            }
        }
    }


    public TextureRegion getFrame(float dt) {
        TextureRegion region;
        currentState = getState();
        switch(currentState){
            case DANO:
                region = danoRegion;
                break;
            case NORMAL:
            default:
                region = caveiraRegion;
                break;
        }

        stateTime = currentState == previousState ? stateTime + dt : 0;
        previousState = currentState;
        return region;
    }

    public State getState(){
        if(tomarDano) return State.DANO;
        return State.NORMAL;
    }

    @Override
    protected void defineEnemy() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(), getY());
        bdef.type = BodyDef.BodyType.KinematicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();

        CircleShape shape = new CircleShape();
        shape.setRadius(12 / GameJam.PPM);
        fdef.filter.categoryBits = GameJam.ENEMY_BIT;
        // configura os bits com que pode colidir
        fdef.filter.maskBits = GameJam.GROUND_BIT | GameJam.ENEMY_BIT | GameJam.OBJECT_BIT | GameJam.PLAYER_BIT | GameJam.ITEM_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);
    }

    @Override
    public void hitOnHead(Player player) {
    }

    @Override
    public void die(){
        destroy();
        stateTime = 0;
        Hud.addScore(100);

    }

    @Override
    public void gotHit(Player player, double dano) {
        if(!tomarDano) {
            hp -= dano;
            tomarDano = true;
            danoCounter = 0.0;
            GameJam.manager.get("audio/Attack.mp3", Sound.class).play();
            if(hp <= 0.0){
                hp = 0.0;
                die();
            }
        }
    }


    public void draw(Batch batch){
        if(toDestroy) return;
        if(!toDestroy || stateTime < 1)
            super.draw(batch);
    }


}
