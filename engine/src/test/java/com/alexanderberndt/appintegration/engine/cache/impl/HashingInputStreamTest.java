package com.alexanderberndt.appintegration.engine.cache.impl;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class HashingInputStreamTest {


    @Test
    void getHashString() throws IOException {

        HashingInputStream in1 = new HashingInputStream(new ByteArrayInputStream("Hello World".getBytes()));
        IOUtils.toByteArray(in1);

        HashingInputStream in2 = new HashingInputStream(new ByteArrayInputStream("Hello World!".getBytes()));
        IOUtils.toByteArray(in2);

        HashingInputStream in3 = new HashingInputStream(new ByteArrayInputStream("Hello World".getBytes()));
        IOUtils.toByteArray(in3);

        String hash1 = in1.getHashString();
        String hash2 = in2.getHashString();
        String hash3 = in3.getHashString();

        assertNotNull(hash1);
        assertNotNull(hash2);
        assertNotNull(hash3);

        assertNotEquals(hash1, hash2);
        assertEquals(hash1, hash3);


        assertEquals(hash1, in1.getHashString());
        assertEquals(hash2, in2.getHashString());
        assertEquals(hash3, in3.getHashString());

        assertNotEquals(in1.getHashString(), in2.getHashString());
        assertEquals(in1.getHashString(), in3.getHashString());
    }

    @Test
    void testWrongAlgorithm() throws IOException {

        HashingInputStream in = new HashingInputStream(new ByteArrayInputStream("Hello World".getBytes()), "does-not-exists");
        IOUtils.toByteArray(in);

        assertNull(in.getHashString());
    }
}