package com.gamejam.game.Sprites;

import com.badlogic.gdx.Game;
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
import com.gamejam.game.Tools.Funcoes;


public class Ghost extends Enemy {


    public enum State { HAPPY, SAD, DANO }
    public State currentState;
    public State previousState;

    private float stateTime;
    private Animation happyAnimation;
    private Animation sadAnimation;
    private Array<TextureRegion> frames;
    private TextureRegion danoRegion;

    private boolean justTurnedDown;
    private boolean justTurnedUp;
    private boolean isSad;
    private boolean changeToHappy;
    private double dano = 1.5;
    private double HP = 12.0;
    private int contador;
    private boolean ehEspecial;


    public Ghost(PlayScreen screen, float x, float y){
        super(screen, x, y);
        frames = new Array<TextureRegion>();
        for(int i = 0; i < 5; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("inimigo"), i*32, 0, 32, 32));
        happyAnimation = new Animation(0.1f, frames);
        frames.clear();
        for(int i = 6; i < 15; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("inimigo"), i*32, 0, 32, 32));
        sadAnimation = new Animation(0.2f, frames);

        danoRegion = new TextureRegion(screen.getAtlas().findRegion("ghost_dano"), 0, 0, 32, 32);

        velocity = new Vector2(0, 1f);
        stateTime = 0;
        contador = 0;


        currentState = State.HAPPY;
        previousState = State.HAPPY;

        setBounds(getX() - getWidth() / 2, getY()- getHeight() / 2, 48 / GameJam.PPM, 48 / GameJam.PPM);

        isSad = false;
        justTurnedDown = false;
        justTurnedUp = false;
        changeToHappy = false;
        ehEspecial = false;

        setDano(dano);
        setHP(HP);
    }

    public void update(float dt){
        if (toDestroy && !destroyed) {
            world.destroyBody(b2body);
            destroyed = true;
        } else if (!destroyed) {
            double var = 0.5;
            if(ehEspecial) {
                contador++;
                if (contador == 360) contador = 0;
                velocity.x = Funcoes.retornaCosCos(0.2f, contador, (float) (5.0));
                velocity.y = Funcoes.retornaCosSin(0.2f, contador, (float) (5.0));
                //   velocity.x = Funcoes.retornaSin(0.2f, 1 * contador);
                //   velocity.y = Funcoes.retornaCose(0.2f, 2 * contador);
            }
            b2body.setLinearVelocity(velocity);
            setPosition(b2body.getPosition().x -= getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
            setRegion(getFrame(dt));
            if(!ehEspecial) {
                if (b2body.getPosition().y > startY + var && !justTurnedDown) {
                    reverseVelocity(false, true);
                    justTurnedDown = true;
                } else if (b2body.getPosition().y < startY + var) {
                    justTurnedDown = false;
                }
                if (b2body.getPosition().y < startY && !justTurnedUp) {
                    reverseVelocity(false, true);
                    justTurnedUp = true;
                } else if (b2body.getPosition().y > startY) {
                    justTurnedUp = false;
                }

            }
            if (changeToHappy && stateTime > 2) {

                isSad = false;
                changeToHappy = false;
            }
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
            case SAD:
                region = (TextureRegion) sadAnimation.getKeyFrame(stateTime, true);
                break;
            case HAPPY:
            default:
                region = (TextureRegion) happyAnimation.getKeyFrame(stateTime, true);
                break;
        }

        stateTime = currentState == previousState ? stateTime + dt : 0;
        previousState = currentState;
        return region;
    }

    public State getState(){
        if(tomarDano) return State.DANO;
        if(isSad) return State.SAD;
        return State.HAPPY;

    }

    public void aplicarGiro(){
        ehEspecial = true;
        velocity = new Vector2(0, 0);
    }


    @Override
    public void die(){
        destroy();
        stateTime = 0;
        Hud.addScore(150);
    }

    @Override
    protected void defineEnemy() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(), getY());
        bdef.type = BodyDef.BodyType.KinematicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();

        CircleShape shape = new CircleShape();
        shape.setRadius(18 / GameJam.PPM);
        fdef.filter.categoryBits = GameJam.ENEMY_BIT;
        // configura os bits com que pode colidir
        fdef.filter.maskBits = GameJam.GROUND_BIT | GameJam.ENEMY_BIT | GameJam.OBJECT_BIT | GameJam.PLAYER_BIT | GameJam.ITEM_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        // Cabeça
        PolygonShape head = new PolygonShape();
        Vector2[] vertice = new Vector2[4];
        vertice[0] = new Vector2(-8, 20).scl(1 / GameJam.PPM);
        vertice[1] = new Vector2(8, 20).scl(1 / GameJam.PPM);
        vertice[2] = new Vector2(-3, 6).scl(1 / GameJam.PPM);
        vertice[3] = new Vector2(3, 6).scl(1 / GameJam.PPM);
        head.set(vertice);
        fdef.shape = head;
        fdef.restitution = 0.5f;
        fdef.filter.categoryBits = GameJam.GHOST_HEAD_BIT;
        b2body.createFixture(fdef).setUserData(this);
    }

    @Override
    public void hitOnHead(Player player) {
        if(!isSad) {
            Gdx.app.log("status", "GOT HIT ON HEAD");
            isSad = true;
            player.isAboveGhost = true;
        }
    }

    public void stopSad() {
        if(!changeToHappy) {
            changeToHappy = true;
            stateTime = 0;
        }
    }

    @Override
    public void gotHit(Player player, double dano) {
        if(!canTakeDamage) return;
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

    public void draw(Batch batch) {
        if (toDestroy) return;
        if (!toDestroy || stateTime < 1)
            super.draw(batch);
    }


}
