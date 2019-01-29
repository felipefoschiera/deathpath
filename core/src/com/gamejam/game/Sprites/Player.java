package com.gamejam.game.Sprites;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.gamejam.game.GameJam;
import com.gamejam.game.Items.Heart;
import com.gamejam.game.Items.Item;
import com.gamejam.game.Items.ItemDef;
import com.gamejam.game.Items.Portal;
import com.gamejam.game.Scenes.Hud;
import com.gamejam.game.Screens.PlayScreen;
import com.gamejam.game.Tools.B2WorldCreator;

import javax.xml.soap.Text;

public class Player extends Sprite {

    public enum State { STANDING, JUMPING, DEAD, ON_GHOST, DANO, ATACANDO }
    public State currentState;
    public State previousState;


    public World world;
    public Body b2body;

    private Animation playerAnim;
    private Animation playerEspadaAnim;
    private TextureRegion texturaDano;
    private TextureRegion texturaEspadaDano;
    private TextureRegion texturaAttack;

    private boolean runningRight;
    private float stateTimer;
    public boolean isAboveGhost;
    public boolean toLeaveGhost;
    private boolean isDead;
    private boolean tomarDano;
    private boolean darAtaque;
    private boolean podeAtacar;
    private boolean swordEquiped;
    private boolean timeToDefineSwordPlayer;
    private boolean timeToEnterPortal;

    public double vida;
    public double danoCounter;
    private double dano;
    private double attackCounter = 0.0;

    private int vidas = 3;

    private int lastPortalId = -1;

    private boolean readyForTP = true;
    private double tpCounter = 0.0;

    public boolean acabar;

    public PlayScreen screen;

    public double getVida() {
        return vida;
    }

    public void setVida(double vida) {
        this.vida = vida;
    }

    public Player(PlayScreen screen){
        super(screen.getAtlas().findRegion("fantasma"));
        this.screen = screen;
        this.world = screen.getWorld();

        currentState = State.STANDING;
        previousState = State.STANDING;

        vida = 10.0;
        stateTimer = 0.0f;
        danoCounter = 0.0;
        dano = 3.0;

        swordEquiped = false;
        runningRight = true;
        isDead = false;
        podeAtacar = true;
        isAboveGhost = false;
        toLeaveGhost = false;
        tomarDano = false;
        acabar = false;
        darAtaque = false;

        /** Texturas e Animação */

        texturaDano = new TextureRegion(screen.getAtlas().findRegion("fantasma_dano"), 0, 0, 16, 32);
        texturaEspadaDano = new TextureRegion(screen.getAtlas().findRegion("fantasma_espada_dano"), 0, 0, 32, 32);
        texturaAttack = new TextureRegion(screen.getAtlas().findRegion("fantasma_attack"), 0, 0, 32, 32);
        Array<TextureRegion> frames = new Array<TextureRegion>();
        for(int i = 0; i < 6; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("fantasma"), i*16, 0, 16, 32));
        playerAnim = new Animation(0.1f, frames);
        frames.clear();

        for(int i = 0; i < 6; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("fantasma_espada"), i*32, 0, 32, 32));
        playerEspadaAnim = new Animation(0.1f, frames);
        frames.clear();

        definePlayer();

        setBounds(0, 0, 16 / GameJam.PPM, 32 / GameJam.PPM);
    }

    public void update(float dt){
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2 + (7 / GameJam.PPM));
        setRegion(getFrame(dt));
        if (isAboveGhost && toLeaveGhost && stateTimer > 0.4) {
            toLeaveGhost = isAboveGhost = false;
        }

        if(!podeAtacar){
            attackCounter += dt;
            if(attackCounter > 0.5){
                podeAtacar = true;
            }
        }
        if(tomarDano){
            danoCounter += dt;
            if(danoCounter > 1.0){
                tomarDano = false;
            }
        }

        if(!readyForTP){
            tpCounter += dt;
            if(tpCounter > 1.0){
                readyForTP = true;
            }
        }

        for(Item item : PlayScreen.items) {
            if (item instanceof Portal) {
                Portal p = (Portal) item;
                if(p.getIdent() == lastPortalId+1){
                    if(getX() > p.getX()){
                        lastPortalId++;
                        break;
                    }
                }
            }
        }

        if(getX() >= 10150 / GameJam.PPM){
            acabar = true;
        }




        if(timeToDefineSwordPlayer)
            defineSwordPlayer();
        if(timeToEnterPortal)
            definePortalPlayer();
    }

    public void die(){
      //  isDead = true;
        vidas--;
        Hud.diminuiVida();
        if(vidas == 0){
            isDead = true;
            b2body.applyLinearImpulse(new Vector2(0, 2f), b2body.getWorldCenter(), true);
             GameJam.manager.get("audio/musica.mp3", Music.class).stop();
        }
        Filter filter = new Filter();
        filter.maskBits = GameJam.NOTHING_BIT;
        for(Fixture fixture : b2body.getFixtureList()){
            fixture.setFilterData(filter);
        }
        for(Item item : PlayScreen.items) {
            if (item instanceof Portal) {
                Portal p = (Portal) item;
                if(p.getIdent() == lastPortalId){
                    enterPortal(new Vector2(p.getX(), p.getY()));
                        break;
                    }
            }
        }
        vida = 10.0;
        Hud.setHP(vida);
    }

    public void leaveGhost(){
        stateTimer = 0;
        toLeaveGhost = true;
    }

    public void enterPortal(Vector2 position){
        if(readyForTP) {
            readyForTP = false;
            tpCounter = 0.0;
            setBounds(getX(), getY(), getWidth(), getHeight());
            Gdx.app.log("status", "TELEPORTED");
            teleportCoordVector = new Vector2(position);
            timeToEnterPortal = true;
        }
    }

    public void equipSword(){
        swordEquiped = true;
        timeToDefineSwordPlayer = true;
        setBounds(getX(), getY(), getWidth() * 2, getHeight());
    }

    public TextureRegion getFrame(float dt) {
        currentState = getState();
        TextureRegion region;
        switch(currentState){
            case ATACANDO:
                region = texturaAttack;
                break;
            case DANO:
                region = swordEquiped ? texturaEspadaDano : texturaDano;
                break;
            case STANDING:
            case JUMPING:
            default:
                if(swordEquiped)
                    region = (TextureRegion) playerEspadaAnim.getKeyFrame(stateTimer, true);
                else
                    region = (TextureRegion) playerAnim.getKeyFrame(stateTimer, true);

                break;
        }


        if((b2body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()){
            region.flip(true, false);
            runningRight = false;
        }else if((b2body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()){
            region.flip(true, false);
            runningRight = true;
        }

        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        previousState = currentState;
        return region;
    }

    public State getState(){
        if(isDead) return State.DEAD;
        if(!podeAtacar) return State.ATACANDO;
        if(tomarDano && danoCounter <= 2) return State.DANO;
        if(isAboveGhost) return State.ON_GHOST;
        if(b2body.getLinearVelocity().y > 0 || (b2body.getLinearVelocity().y < 0 && previousState == State.JUMPING))
            return State.JUMPING;
        return State.STANDING;
    }

    public void definePlayer(){
        BodyDef bdef = new BodyDef();
        bdef.position.set(250 / GameJam.PPM, 32 / GameJam.PPM);
       // bdef.position.set(9975 / GameJam.PPM, 150 / GameJam.PPM);

        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(8/ GameJam.PPM);
        fdef.filter.categoryBits = GameJam.PLAYER_BIT;
        fdef.filter.maskBits =
                GameJam.GROUND_BIT |
                        GameJam.OBJECT_BIT   |
                        GameJam.ITEM_BIT |
                        GameJam.ENEMY_BIT |
                        GameJam.GHOST_HEAD_BIT;
        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        shape = new CircleShape();
        shape.setRadius(6 / GameJam.PPM);
        shape.setPosition(new Vector2(0, 15).scl(1 / GameJam.PPM));
        fdef.filter.categoryBits = GameJam.PLAYER_HEAD_BIT;
        // configura os bits com que pode colidir
        fdef.filter.maskBits = GameJam.GROUND_BIT | GameJam.ENEMY_BIT | GameJam.OBJECT_BIT | GameJam.ITEM_BIT | GameJam.OBJECT_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);
    }

    public Vector2 teleportCoordVector;

    public void definePortalPlayer(){
        world.destroyBody(b2body);
        BodyDef bdef = new BodyDef();
        bdef.position.set(teleportCoordVector);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(8 / GameJam.PPM);
        fdef.filter.categoryBits = GameJam.PLAYER_BIT;
        fdef.filter.maskBits =
                GameJam.GROUND_BIT |
                        GameJam.OBJECT_BIT   |
                        GameJam.ITEM_BIT |
                        GameJam.ENEMY_BIT |
                        GameJam.GHOST_HEAD_BIT;
        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        shape = new CircleShape();
        shape.setRadius(6 / GameJam.PPM);
        shape.setPosition(new Vector2(0, 15).scl(1 / GameJam.PPM));
        fdef.filter.categoryBits = GameJam.PLAYER_HEAD_BIT;
        // configura os bits com que pode colidir
        fdef.filter.maskBits = GameJam.GROUND_BIT | GameJam.ENEMY_BIT | GameJam.OBJECT_BIT | GameJam.ITEM_BIT | GameJam.OBJECT_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);
        timeToEnterPortal = false;
    }

    public void defineSwordPlayer(){
        Vector2 currentPosition = b2body.getPosition();
        world.destroyBody(b2body);

        BodyDef bdef = new BodyDef();
        bdef.position.set(currentPosition);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(8 / GameJam.PPM);
        shape.setPosition(new Vector2(0, 0).scl(1 / GameJam.PPM));
        fdef.filter.categoryBits = GameJam.PLAYER_BIT;
        fdef.filter.maskBits =
                GameJam.GROUND_BIT |
                        GameJam.OBJECT_BIT   |
                        GameJam.ITEM_BIT |
                        GameJam.ENEMY_BIT |
                        GameJam.GHOST_HEAD_BIT;
        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        shape = new CircleShape();
       shape.setRadius(6 / GameJam.PPM);
        shape.setPosition(new Vector2(0, 15).scl(1 / GameJam.PPM));
        fdef.filter.categoryBits = GameJam.PLAYER_HEAD_BIT;
       // configura os bits com que pode colidir
        fdef.filter.maskBits = GameJam.GROUND_BIT | GameJam.ENEMY_BIT | GameJam.OBJECT_BIT | GameJam.ITEM_BIT | GameJam.OBJECT_BIT;
        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);
        timeToDefineSwordPlayer = false;
    }


    public void hitBy(Enemy enemy) {
        if(!tomarDano) {
            vida -= enemy.getDano();
            if (vida <= 0) vida = 0;
            Hud.setHP(vida);
            tomarDano = true;
            danoCounter = 0.0;
            if (vida == 0) die();
        }
        GameJam.manager.get("audio/Damage.mp3", Sound.class).play(0.5f);

    }

    private double attackDistance = 30.0 / GameJam.PPM;
    private double alturaDistance = 25.0 / GameJam.PPM;

    public float absoluteDistance(float a, float b){
        return Math.abs(a-b);
    }

    public void atacar(){
        if(swordEquiped && podeAtacar) {
            podeAtacar = false;
            attackCounter = 0.0;
            for (Enemy enemy : B2WorldCreator.getEnemies()) {
                // Ataca inimigos na direção que está olhando à uma distância de attackDistance
                if (runningRight) {
                    if (enemy.getX() >= getX() && absoluteDistance(enemy.getX(), getX()) <= attackDistance && absoluteDistance(enemy.getY(), getY()) <= alturaDistance) {
                        enemy.gotHit(this, dano);
                    }
                } else {
                    if (enemy.getX() <= getX() && absoluteDistance(enemy.getX(), getX()) <= attackDistance && absoluteDistance(enemy.getY(), getY()) <= alturaDistance) {
                        enemy.gotHit(this, dano);
                    }
                }
            }
        }
    }

    public float getStateTimer() {
        return stateTimer;
    }
}
