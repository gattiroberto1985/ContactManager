package org.bob.android.app.contactmanager.exceptions;

/**
 * Created by roberto.gatti on 11/12/2014.
 */
public class CMBaseException extends Throwable
{
    public CMBaseException(String message, Exception inner)
    {
        super(message, inner);
    }

}
