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
 * A DeviceLimiter that doesn't limit the number of devices that can use a
 * given user's license.
 * <p>
 * Unless you have reason to believe that your application is being pirated
 * by multiple users using the same license (signing in to Market as the same
 * user), we recommend you use this implementation.
 */
public class NullDeviceLimiter implements DeviceLimiter {

    public int isDeviceAllowed(String userId) {
        return Policy.LICENSED;
    }
}
