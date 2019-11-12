/*
 * Copyright © 2019 By Geeks Empire.
 *
 * Created by Elias Fazel on 11/11/19 7:22 PM
 * Last modified 11/11/19 7:21 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package com.google.android.vending.licensing;

/**
 * Indicates that an error occurred while validating the integrity of data managed by an
 * {@link Obfuscator}.}
 */
public class ValidationException extends Exception {
    private static final long serialVersionUID = 1L;

    public ValidationException() {
        super();
    }

    public ValidationException(String s) {
        super(s);
    }
}
