package it.polimi.jaa.mobilefitness.utils;

/**
 * Created by andre on 30/03/15.
 */
public class UserInfo {
    public final String name;
    public final String surname;
    public final String birthDate;
    public final String height;
    public final String weight;

    public UserInfo(String name, String surname, String birthDate, String height, String weight) {
        this.name = name;
        this.surname = surname;
        this.birthDate = birthDate;
        this.height = height;
        this.weight = weight;
    }
}
