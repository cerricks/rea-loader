/*
 * Copyright 2017
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.cerricks.iconium.util;

import com.fasterxml.jackson.databind.JsonNode;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.YearMonth;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.util.StringUtils;

/**
 * A utility for parsing content from a {@link JsonNode} into various types.
 *
 * @author Clifford Errickson
 */
public final class JsonParseUtil {

    public static final DateTimeFormatter SHORT_DATE_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd");
    public static final DateTimeFormatter SHORT_DATE_TIME_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter MEDIUM_DATE_FORMATTER = DateTimeFormat.forPattern("dd MMM yyyy");
    public static final DateTimeFormatter MEDIUM_YEAR_MONTH_FORMATTER = DateTimeFormat.forPattern("MMM yyyy");

    /**
     * Reads the given field from the {@link JsonNode} and converts the
     * resulting value into a {@link DateTime}.
     *
     * @param node the node to retrieve the value from.
     * @param fieldName the name of the field to retrieve the value for.
     * @param formatter the expected format of the value.
     * @return a {@link DateTime} representing the value, or null if field not
     * found.
     */
    public static DateTime parseDateTime(final JsonNode node, final String fieldName, final DateTimeFormatter formatter) {
        if (!node.has(fieldName)) {
            return null;
        }

        String text = node.get(fieldName).asText();

        if (isNull(text)) {
            return null;
        }

        return DateTime.parse(text, formatter);
    }

    /**
     * Reads the given field from the {@link JsonNode} and converts the
     * resulting value into a {@link Integer}.
     *
     * @param node the node to retrieve the value from.
     * @param fieldName the name of the field to retrieve the value for.
     * @return a {@link Integer} representing the value, or null if field not
     * found.
     */
    public static Integer parseInteger(final JsonNode node, final String fieldName) {
        if (!node.has(fieldName)) {
            return null;
        }

        String text = node.get(fieldName).asText();

        if (isNull(text)) {
            return null;
        }

        return Integer.parseInt(text);
    }

    /**
     * Reads the given field from the {@link JsonNode} and converts the
     * resulting value into a {@link LocalDate}.
     *
     * @param node the node to retrieve the value from.
     * @param fieldName the name of the field to retrieve the value for.
     * @param formatter the expected format of the value.
     * @return a {@link LocalDate} representing the value, or null if field not
     * found.
     */
    public static LocalDate parseLocalDate(final JsonNode node, final String fieldName, final DateTimeFormatter formatter) {
        if (!node.has(fieldName)) {
            return null;
        }

        String text = node.get(fieldName).asText();

        if (isNull(text)) {
            return null;
        }

        return LocalDate.parse(text, formatter);
    }

    /**
     * Reads the given field from the {@link JsonNode} and converts the
     * resulting value into a {@link String}.
     *
     * @param node the node to retrieve the value from.
     * @param fieldName the name of the field to retrieve the value for.
     * @return a {@link String} representing the value, or null if field not
     * found.
     */
    public static String parseText(final JsonNode node, final String fieldName) {
        if (!node.has(fieldName)) {
            return null;
        }

        String text = node.get(fieldName).asText();

        if (isNull(text)) {
            return null;
        }

        return text.trim();
    }

    /**
     * Reads the given field from the {@link JsonNode} and converts the
     * resulting value into a {@link YearMonth}.
     *
     * @param node the node to retrieve the value from.
     * @param fieldName the name of the field to retrieve the value for.
     * @param formatter the expected format of the value.
     * @return a {@link YearMonth} representing the value, or null if field not
     * found.
     */
    public static YearMonth parseYearMonth(final JsonNode node, final String fieldName, final DateTimeFormatter formatter) {
        if (!node.has(fieldName)) {
            return null;
        }

        String text = node.get(fieldName).asText();

        if (isNull(text)) {
            return null;
        }

        return YearMonth.parse(text, formatter);
    }

    /**
     * Determine if given {@code String} value is to be considered null. Checks
     * if given value is null, empty, or equal to 'Unavailable'.
     *
     * @param str {@code true} if null, {@code false} otherwise.
     * @return true if the given string is null, empty, or equal to
     * 'Unavailable'.
     */
    private static boolean isNull(final String str) {
        return !StringUtils.hasText(str)
                || str.equalsIgnoreCase("Unavailable");
    }

}
