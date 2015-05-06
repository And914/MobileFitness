package it.polimi.jaa.mobilefitness.backend.callbacks;

import com.parse.ParseObject;

import java.util.List;

/**
 * Created by andre on 06/05/15.
 */
public interface CallbackParseObject {
    public void done(ParseObject parseObject);

    public void error(int error);
}
