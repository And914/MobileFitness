package it.polimi.jaa.mobilefitness.backend.callbacks;

/**
 * Created by andre on 13/05/15.
 */
public interface CallbackBoolean {
    void done(boolean result);

    void error(int error);
}
