package it.polimi.jaa.mobilefitness.utils;

/**
 * Created by andre on 30/03/15.
 */
public class ExerciseInfo {
    public final String name;
    public final String equipment;
    public final String rounds;
    public final String rep;
    public final String rest;
    public final String weight;
    public final String time;
    public final int image;

    public ExerciseInfo(String name, String equipment, String rounds, String rep, String rest, String weight, String time,int image) {
        this.name = name;
        this.equipment = equipment;
        this.rounds = rounds;
        this.rep = rep;
        this.rest = rest;
        this.weight = weight;
        this.time = time;
        this.image = image;
    }
}
