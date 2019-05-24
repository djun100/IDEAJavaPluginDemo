package com.cy.javaplugin.util
import java.util.*


/**
 * 第一个字母大写
 *
 * @param key key
 *
 * @return String
 */
fun String.firstToUpperCase(): String = this.substring(0, 1).toUpperCase(Locale.CHINA) + this.substring(1)
