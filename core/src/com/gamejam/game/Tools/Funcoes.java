package com.gamejam.game.Tools;

public class Funcoes {

    public static float retornaSin(float speed, int contador){
        return (float) (speed * Math.sin(Math.toRadians(contador)));
    }

    public static float retornaCose(float speed, int contador){
        return (float) (speed * Math.cos(Math.toRadians(contador)));
    }

    public static float retornaCosCos(float speed, int contador, float k){
        return (float) (speed * Math.cos(k * Math.toRadians(contador)) * Math.cos(Math.toRadians(contador)));
    }

    public static float retornaCosSin(float speed, int contador, float k){
        return (float) (speed * Math.cos(k * Math.toRadians(contador)) * Math.sin(Math.toRadians(contador)));
    }

}
