/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/28/20 11:16 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.Utils.InAppStore.DigitalAssets.Extensions

fun String.convertToItemTitle() : String {

    val temporaryText = this.split(".")

    return "${temporaryText[0][0].uppercaseChar()}${temporaryText[0].substring(1)} ${temporaryText[1][0].uppercaseChar()}${temporaryText[1].substring(1)}"
}

fun String.convertToRemoteConfigDescriptionKey() : String {

    return this.replace(".", "_") + "_description"
}


fun String.convertToRemoteConfigPriceInformation() : String {

    return this.replace(".", "_") + "_price_information"
}

fun String.convertToStorageScreenshotsDirectory() : String{

    val temporaryText = this.split(".")

    return "${temporaryText[0][0].uppercaseChar()}${temporaryText[0].substring(1)}${temporaryText[1][0].uppercaseChar()}${temporaryText[1].substring(1)}"
}