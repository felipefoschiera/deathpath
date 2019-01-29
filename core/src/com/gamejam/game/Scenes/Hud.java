package com.gamejam.game.Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.gamejam.game.GameJam;

public class Hud implements Disposable {

    public Stage stage;
    private Viewport viewport;
    private static Integer score;
    private static Integer vidas;
    private static Double hp;


    private static Label scoreLabel;
    private static Label hpLabel;
    private static Label vidaslabel;
    public Hud(SpriteBatch sb){
        score = 0;
        vidas = 3;
        hp = 10.0;
        viewport = new FitViewport(GameJam.V_WIDTH, GameJam.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport);

        Table table = new Table();
        table.top();
        table.setFillParent(true);
        scoreLabel = new Label(String.format("Points: %d", score), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        hpLabel = new Label(String.format("HP: %.1f / %.1f", hp, hp), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        vidaslabel = new Label(String.format("Lifes: %d / %d", vidas, vidas), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        table.add(scoreLabel).expandX();
        table.add(vidaslabel).expandX();
        table.add(hpLabel).expandX();
        stage.addActor(table);
      
    }

    public static void addScore(int value){
        score += value;
        scoreLabel.setText(String.format("Points: %d", score));
    }

    public static void setHP(double value){
        hp = value;
        hpLabel.setText(String.format("HP: %.1f / %.1f", hp, 10.0));
    }

    public static void diminuiVida(){
        vidas--;
        vidaslabel.setText(String.format("Lifes: %d / %d", vidas, 3));
    }
    @Override
    public void dispose() {
        stage.dispose();
    }


}
