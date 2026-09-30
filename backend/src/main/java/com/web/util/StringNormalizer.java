/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.web.util;

public final class StringNormalizer {

    private StringNormalizer() {
    }

    public static String normalizeName(String value) {
        if (value == null) {
            return null;
        }

        return value
                .trim()
                .replaceAll("\\s+", " ");
    }
}