package it.polimi.jaa.mobilefitness.backend.callbacks;

/**
 * Created by andre on 13/05/15.
 */
public interface CallbackBoolean {
    public void done(boolean result);

    public void error(int error);
}
