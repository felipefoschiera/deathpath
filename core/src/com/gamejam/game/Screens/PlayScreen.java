package com.gamejam.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.gamejam.game.GameJam;
import com.gamejam.game.Items.Espada;
import com.gamejam.game.Items.Heart;
import com.gamejam.game.Items.Item;
import com.gamejam.game.Items.ItemDef;
import com.gamejam.game.Items.Portal;
import com.gamejam.game.Scenes.Hud;
import com.gamejam.game.Sprites.Enemy;
import com.gamejam.game.Sprites.Player;
import com.gamejam.game.Tools.B2WorldCreator;
import com.gamejam.game.Tools.CreateItems;
import com.gamejam.game.Tools.WorldContactListener;

import java.util.concurrent.LinkedBlockingQueue;

public class PlayScreen implements Screen {

    private GameJam game;
    private TextureAtlas atlas;

    private OrthographicCamera gamecam;
    private Viewport gameport;
    private Hud hud;

    private TmxMapLoader mapLoader;
    private TiledMap map;

    private World world;
    private Box2DDebugRenderer b2dr;
    private B2WorldCreator creator;
    private OrthogonalTiledMapRenderer renderer;

    private Player player;

    private Music music;
    private Music ambiente;

    public static Array<Item> items;
    private LinkedBlockingQueue<ItemDef> itemsToSpawn;

    private Rectangle screenBounds;

    public PlayScreen(GameJam game){
        atlas = new TextureAtlas("SpritesFinal.pack");
        this.game = game;

        gamecam = new OrthographicCamera();
        gameport = new FitViewport(GameJam.V_WIDTH / GameJam.PPM, GameJam.V_HEIGHT / GameJam.PPM, gamecam);
        hud = new Hud(game.batch);


        mapLoader = new TmxMapLoader();
        map = mapLoader.load("level1.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / GameJam.PPM);
        gamecam.position.set(gameport.getWorldWidth()/2, gameport.getWorldHeight()/2, 0);
        world = new World(new Vector2(0, -10), true);
        b2dr = new Box2DDebugRenderer();



        creator = new B2WorldCreator(this);
        player = new Player(this);

        world.setContactListener(new WorldContactListener());

        music = GameJam.manager.get("audio/musica.mp3", Music.class);
        music.setLooping(true);
        music.setVolume(0.3f);
        music.play();
        ambiente = GameJam.manager.get("audio/Ambience.mp3", Music.class);
        ambiente.setLooping(true);
        ambiente.setVolume(0.3f);
        ambiente.play();


        items = new Array<Item>();
        itemsToSpawn = new LinkedBlockingQueue<ItemDef>();

        screenBounds = new Rectangle(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        new CreateItems(this);
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                // metade = screenBounds.getWidth() / 2
                double difference = screenBounds.getWidth() * 0.25;
                if(screenX > screenBounds.getWidth() / 2 - difference && screenX < screenBounds.getWidth() / 2 + difference){

                    if(screenY <= screenBounds.getHeight() / 2 + (0.15 * screenBounds.getHeight())){
                        jump();
                    }else{
                        player.atacar();
                    }
                }
            //    Gdx.app.log("status", "SCREEN SIZE: " + screenBounds.getWidth() + " " + screenBounds.getHeight());
         //       Gdx.app.log("status", "CLICKED AT: " + screenX + " " + screenY);
                return super.touchDown(screenX, screenY, pointer, button);
            }
        });


    }

    @Override
    public void show() {

    }



    public void moveLeft(){
        if(player.b2body.getLinearVelocity().x >= -1.3){
            player.b2body.applyLinearImpulse(new Vector2(-0.07f, 0), player.b2body.getWorldCenter(), true);
        }
    }

    public void moveRight(){
        if(player.b2body.getLinearVelocity().x <= 1.3){
            player.b2body.applyLinearImpulse(new Vector2(0.07f, 0), player.b2body.getWorldCenter(), true);
        }
    }

    public void jump(){
        if (player.getState() != Player.State.JUMPING){
            player.b2body.applyLinearImpulse(new Vector2( 0, 3.5f), player.b2body.getWorldCenter(), true);
            player.currentState = Player.State.JUMPING;
            GameJam.manager.get("audio/Jump.mp3", Sound.class).play();
        }else if(player.getState() == Player.State.ON_GHOST){
            player.b2body.applyLinearImpulse(new Vector2( 0, 0.5f), player.b2body.getWorldCenter(), true);
            player.currentState = Player.State.JUMPING;
            GameJam.manager.get("audio/Jump.mp3", Sound.class).play();
        }
    }

    public void handleInput(float dt){

        if(Gdx.input.isTouched()){
            int posX = Gdx.input.getX();
            if(posX <= screenBounds.getWidth() / 2 - 600){
                moveLeft();
            }else if(posX >= screenBounds.getWidth() / 2 + 600) {
                moveRight();
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            jump();
        }
        if ((Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D))){
            moveRight();
        }
        if ((Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A))){
            moveLeft();

        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
            player.atacar();
        }
    }

    public TextureAtlas getAtlas(){
        return atlas;
    }


    public void update(float dt){
        handleInput(dt);
        handleSpawningItems();

        world.step(1/60f, 6, 2);

        player.update(dt);

        for(Enemy enemy : creator.getEnemies()){
            enemy.update(dt);
            if(enemy.getX() < player.getX() + 200 / GameJam.PPM)
                enemy.b2body.setActive(true);
        }


        for(Item item : items)
            item.update(dt);

        if(player.b2body.getPosition().x < player.getWidth() / 2){
            player.b2body.applyLinearImpulse(new Vector2(0.07f, 0), player.b2body.getWorldCenter(), true);
        }


        if(player.b2body.getPosition().y < 0)
            player.die();

        if(player.b2body.getPosition().x > gameport.getWorldWidth() / 2)
            gamecam.position.x = player.b2body.getPosition().x;
        gamecam.update();
        renderer.setView(gamecam);
    }

    public boolean gameOver(){
        if(player.currentState == Player.State.DEAD && player.getStateTimer() > 1.3){
            return true;
        }
        return false;
    }


    public boolean venceu(){
        return player.acabar;
    }

    public void spawnItem(ItemDef idef){
        itemsToSpawn.add(idef);
    }

    public void handleSpawningItems(){
        if(!itemsToSpawn.isEmpty()){
            ItemDef idef = itemsToSpawn.poll();
            if(idef.type == Heart.class){
                items.add(new Heart(this, idef.position.x, idef.position.y));
            }
            if(idef.type == Espada.class){
                items.add(new Espada(this, idef.position.x, idef.position.y));
            }
            if(idef.type == Portal.class){
                items.add(new Portal(this, idef.position.x, idef.position.y, idef.ident));
            }

        }
    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        renderer.render();
  //      b2dr.render(world, gamecam.combined);
        game.batch.setProjectionMatrix(gamecam.combined);
        game.batch.begin();
        player.draw(game.batch);
        for(Enemy enemy : creator.getEnemies())
            enemy.draw(game.batch);

        for(Item item : items)
            item.draw(game.batch);
        game.batch.end();


        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

        if(venceu()){
            game.setScreen(new WinScreen(game));
            dispose();
        }

        if(gameOver()){
            game.setScreen(new GameOverScreen(game));
            dispose();
        }

    }

    @Override
    public void resize(int width, int height) {
        gameport.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    public TiledMap getMap(){
        return map;
    }

    public World getWorld(){
        return world;
    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
    }
}
