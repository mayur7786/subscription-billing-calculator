package com.example.subscription.http;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

final class SimpleJsonParser {
    private final String json;
    private int index;

    SimpleJsonParser(String json) {
        this.json = json == null ? "" : json;
    }

    Object parse() {
        Object value = readValue();
        skipWhitespace();
        if (index != json.length()) {
            throw error("Unexpected trailing content");
        }
        return value;
    }

    private Object readValue() {
        skipWhitespace();
        if (index >= json.length()) {
            throw error("Unexpected end of JSON");
        }
        char current = json.charAt(index);
        return switch (current) {
            case '{' -> readObject();
            case '[' -> readArray();
            case '"' -> readString();
            case 'n' -> readNull();
            default -> {
                if (current == '-' || Character.isDigit(current)) {
                    yield readNumber();
                }
                throw error("Unexpected character '" + current + "'");
            }
        };
    }

    private Map<String, Object> readObject() {
        expect('{');
        Map<String, Object> object = new LinkedHashMap<>();
        skipWhitespace();
        if (peek('}')) {
            index++;
            return object;
        }

        while (true) {
            String key = readString();
            skipWhitespace();
            expect(':');
            object.put(key, readValue());
            skipWhitespace();
            if (peek('}')) {
                index++;
                return object;
            }
            expect(',');
            skipWhitespace();
        }
    }

    private List<Object> readArray() {
        expect('[');
        List<Object> array = new ArrayList<>();
        skipWhitespace();
        if (peek(']')) {
            index++;
            return array;
        }

        while (true) {
            array.add(readValue());
            skipWhitespace();
            if (peek(']')) {
                index++;
                return array;
            }
            expect(',');
        }
    }

    private String readString() {
        expect('"');
        StringBuilder result = new StringBuilder();
        while (index < json.length()) {
            char current = json.charAt(index++);
            if (current == '"') {
                return result.toString();
            }
            if (current == '\\') {
                result.append(readEscapedCharacter());
            } else {
                result.append(current);
            }
        }
        throw error("Unterminated string");
    }

    private char readEscapedCharacter() {
        if (index >= json.length()) {
            throw error("Unterminated escape sequence");
        }
        char escaped = json.charAt(index++);
        return switch (escaped) {
            case '"', '\\', '/' -> escaped;
            case 'b' -> '\b';
            case 'f' -> '\f';
            case 'n' -> '\n';
            case 'r' -> '\r';
            case 't' -> '\t';
            default -> throw error("Unsupported escape sequence");
        };
    }

    private Object readNull() {
        if (json.startsWith("null", index)) {
            index += 4;
            return null;
        }
        throw error("Invalid literal");
    }

    private BigDecimal readNumber() {
        int start = index;
        if (peek('-')) {
            index++;
        }
        while (index < json.length() && Character.isDigit(json.charAt(index))) {
            index++;
        }
        if (peek('.')) {
            index++;
            while (index < json.length() && Character.isDigit(json.charAt(index))) {
                index++;
            }
        }
        return new BigDecimal(json.substring(start, index));
    }

    private void skipWhitespace() {
        while (index < json.length() && Character.isWhitespace(json.charAt(index))) {
            index++;
        }
    }

    private void expect(char expected) {
        if (!peek(expected)) {
            throw error("Expected '" + expected + "'");
        }
        index++;
    }

    private boolean peek(char expected) {
        return index < json.length() && json.charAt(index) == expected;
    }

    private IllegalArgumentException error(String message) {
        return new IllegalArgumentException(message + " at position " + index);
    }
}

