/*
 * Copyright © 2019 By Geeks Empire.
 *
 * Created by Elias Fazel on 11/11/19 7:22 PM
 * Last modified 11/11/19 7:21 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package com.google.android.vending.licensing.util;

/**
 * Exception thrown when encountering an invalid Base64 input character.
 *
 * @author nelson
 */
public class Base64DecoderException extends Exception {
    private static final long serialVersionUID = 1L;

    public Base64DecoderException() {
        super();
    }

    public Base64DecoderException(String s) {
        super(s);
    }
}
