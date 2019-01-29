package com.gamejam.game.Items;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Array;
import com.gamejam.game.GameJam;
import com.gamejam.game.Scenes.Hud;
import com.gamejam.game.Screens.PlayScreen;
import com.gamejam.game.Sprites.Player;
import com.gamejam.game.Tools.CreateItems;
import com.gamejam.game.Tools.Funcoes;

import javax.xml.soap.Text;

public class Portal extends Item {

    private float stateTime;
    private Animation animation;
    private Array<TextureRegion> frames;

    private int ident;

    public Portal(PlayScreen screen, float x, float y, int ident) {
        super(screen, x, y, 32, 32);
        frames = new Array<TextureRegion>();
        for(int i = 0; i < 3; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("portal"), i*32, 0, 32, 32));
        animation = new Animation(0.2f, frames);
        frames.clear();
        stateTime = 0;
        velocity = new Vector2(0, 0);
        this.ident = ident;
    }

    public TextureRegion getFrame(float dt) {
        TextureRegion region = (TextureRegion) animation.getKeyFrame(stateTime, true);
        stateTime += dt;
        return region;
    }

    @Override
    public void defineItem() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(), getY());
        bdef.type = BodyDef.BodyType.StaticBody;
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

    }

    public int getIdent() {
        return ident;
    }

    public void setIdent(int ident) {
        this.ident = ident;
    }

    @Override
    public void update(float dt){
        super.update(dt);
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
        setRegion(getFrame(dt));
        body.setLinearVelocity(velocity);
    }
}
