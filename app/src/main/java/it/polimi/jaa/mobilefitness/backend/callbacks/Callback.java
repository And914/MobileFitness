package it.polimi.jaa.mobilefitness.backend.callbacks;

/**
 * Created by andre on 06/05/15.
 */
public interface Callback {
    void done();

    void error(int error);
}
