package com.alexanderberndt.appintegration.engine.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public abstract class LineFilterReader extends Reader {

    private final BufferedReader input;

    private Line curFilteredLine = null;

    private int curFilteredLinePos = 0;

    private boolean isBeforeLineEnd = false;

    public LineFilterReader(Reader input) {
        this.input = new BufferedReader(input);
    }

    /**
     * if null, then line is skipped in output. if empty string, then the line-end of the current line is in output.
     *
     * @param line
     * @return
     * @throws IOException
     */
    protected abstract String filterLine(String line) throws IOException;

    @Override
    public final int read(char[] cbuf, int off, int len) throws IOException {

        final int maxLen = Math.min(cbuf.length - off, len);
        int pos = 0;
        while (pos < maxLen) {
            int nextChar = getNextChar();
            if (nextChar == -1) {
                return (pos == 0) ? -1 : pos;
            } else {
                cbuf[off++] = (char) nextChar;
                pos++;
            }
        }
        return pos;
    }

    private int getNextChar() throws IOException {
        if (curFilteredLine == null) {
            curFilteredLine = getNextFilteredLine();
            if (curFilteredLine == null) {
                return -1;
            } else {
                curFilteredLinePos = 0;
                isBeforeLineEnd = curFilteredLine.getLine().length() > 0;
            }
        }

        final int nextChar;
        if (isBeforeLineEnd) {
            nextChar = curFilteredLine.getLine().charAt(curFilteredLinePos++);
            if (curFilteredLinePos >= curFilteredLine.getLine().length()) {
                isBeforeLineEnd = false;
                curFilteredLinePos = 0;
            }
        } else {
            if (curFilteredLine.getLineEnd() == null) {
                nextChar = -1;
            } else {
                nextChar = curFilteredLine.getLineEnd().charAt(curFilteredLinePos++);
                if (curFilteredLinePos >= curFilteredLine.getLineEnd().length()) {
                    curFilteredLine = null;
                }
            }
        }

        return nextChar;
    }

    private Line getNextFilteredLine() throws IOException {
        Line inputLine;
        String filteredLine;
        do {
            inputLine = getNextInputLine();
            if (inputLine == null) {
                return null;
            }
            filteredLine = filterLine(inputLine.getLine());
        } while (filteredLine == null);

        return new Line(filteredLine, inputLine.getLineEnd());
    }

    private Line getNextInputLine() throws IOException {

        final StringBuilder curLine = new StringBuilder();

        while (true) {
            int curChar = input.read();

            if (curChar == -1) {
                if (curLine.length() == 0) {
                    return null;
                } else {
                    return new Line(curLine.toString(), null);
                }
            }

            if (curChar == '\n') {
                return new Line(curLine.toString(), "\n");
            }

            if (curChar == '\r') {
                input.mark(1);
                int nextChar = input.read();
                if (nextChar == '\n') {
                    return new Line(curLine.toString(), "\r\n");
                } else {
                    input.reset();
                    return new Line(curLine.toString(), "\r");
                }
            }

            curLine.append((char) curChar);
        }
    }

    @Override
    public final void close() throws IOException {
        curFilteredLine = null;
        input.close();
    }

    private static class Line {

        private final String line;
        private final String lineEnd;

        public Line(String line, String lineEnd) {
            this.line = line;
            this.lineEnd = lineEnd;
        }

        public String getLine() {
            return line;
        }

        public String getLineEnd() {
            return lineEnd;
        }
    }
}
