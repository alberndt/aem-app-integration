package com.alexanderberndt.appintegration.engine.cache.impl;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.alexanderberndt.appintegration.engine.cache.impl.ReadAheadInputStream.CHUNK_SIZE;
import static org.junit.jupiter.api.Assertions.*;

class ReadAheadInputStreamTest {

    public static final byte[] DATA = "Hê|Ló Wòörld ĒŘǊǼ!".getBytes();

    @BeforeEach
    void setUp() {
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7, 8, 24, 25, 26, 27, 28, 29, 30, 100})
    void testTestClasses(int size) throws IOException {
        TestInputStream in = new TestInputStream(size);
        TestOutputStream out = new TestOutputStream();

        IOUtils.copy(in, out);

        assertEquals(size, out.getSize());
        assertTrue(out.isValidData());
    }


    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 5, 1024, CHUNK_SIZE - 1, CHUNK_SIZE, CHUNK_SIZE + 1, 5 * CHUNK_SIZE - 1, 5 * CHUNK_SIZE, 5 * CHUNK_SIZE + 1})
    void read(int size) throws IOException {
        ReadAheadInputStream readAheadInputStream = new ReadAheadInputStream(new TestInputStream(size));
        TestOutputStream out = new TestOutputStream();

        IOUtils.copy(readAheadInputStream, out);

        assertEquals(size, out.getSize());
        assertTrue(out.isValidData());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 5, 1024, CHUNK_SIZE - 1, CHUNK_SIZE, CHUNK_SIZE + 1, 5 * CHUNK_SIZE - 1, 5 * CHUNK_SIZE, 5 * CHUNK_SIZE + 1})
    void readAhead100(int size) throws IOException {
        ReadAheadInputStream readAheadInputStream = new ReadAheadInputStream(new TestInputStream(size));
        TestOutputStream out = new TestOutputStream();

        readAheadInputStream.readAhead(100);
        IOUtils.copy(readAheadInputStream, out);

        assertEquals(size, out.getSize());
        assertTrue(out.isValidData());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 5, 1024, CHUNK_SIZE - 1, CHUNK_SIZE, CHUNK_SIZE + 1, 5 * CHUNK_SIZE - 1, 5 * CHUNK_SIZE, 5 * CHUNK_SIZE + 1})
    void readAhead1400(int size) throws IOException {
        ReadAheadInputStream readAheadInputStream = new ReadAheadInputStream(new TestInputStream(size));
        TestOutputStream out = new TestOutputStream();

        readAheadInputStream.readAhead(1400);
        IOUtils.copy(readAheadInputStream, out);

        assertEquals(size, out.getSize());
        assertTrue(out.isValidData());
    }

    @Test
    void isInputFullyRead() throws IOException {
        ReadAheadInputStream readAheadInputStream = new ReadAheadInputStream(new TestInputStream(100));

        assertFalse(readAheadInputStream.isInputFullyRead());
        assertTrue(readAheadInputStream.readAhead(500));
        assertTrue(readAheadInputStream.isInputFullyRead());
    }


    private static class TestInputStream extends InputStream {

        private final int size;

        private int pos;

        public TestInputStream(int size) {
            this.size = size;
        }

        @Override
        public int read() throws IOException {
            if (pos == size) {
                return -1;
            } else {
                return DATA[(pos++) % DATA.length];
            }
        }
    }

    private static class TestOutputStream extends OutputStream {

        private int size;

        private boolean isValidData = true;

        @Override
        public void write(int b) throws IOException {
            isValidData = isValidData && (b == DATA[size % DATA.length]);
            size++;
        }

        public int getSize() {
            return size;
        }

        public boolean isValidData() {
            return isValidData;
        }
    }
}