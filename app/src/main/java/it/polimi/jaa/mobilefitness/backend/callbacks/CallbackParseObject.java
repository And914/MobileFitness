package it.polimi.jaa.mobilefitness.backend.callbacks;

import com.parse.ParseObject;

/**
 * Created by andre on 06/05/15.
 */
public interface CallbackParseObject {
    void done(ParseObject parseObject);

    void error(int error);
}
