package it.polimi.jaa.mobilefitness.backend.callbacks;

import com.parse.ParseObject;

import java.util.List;

/**
 * Created by andre on 06/05/15.
 */
public interface CallbackParseObjects {
    void done(List<ParseObject> parseObjects);

    void error(int error);
}
