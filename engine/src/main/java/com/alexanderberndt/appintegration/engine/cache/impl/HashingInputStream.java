package com.alexanderberndt.appintegration.engine.cache.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashingInputStream extends InputStream {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Nonnull
    private final InputStream inputStream;

    @Nullable
    private final MessageDigest digest;

    private String hash;

    public HashingInputStream(InputStream inputStream) {
        this(inputStream, "SHA-256");
    }

    public HashingInputStream(@Nonnull InputStream inputStream, @Nonnull String algorithm) {
        this.inputStream = inputStream;
        this.digest = getDigest(algorithm);
    }

    @Nullable
    private MessageDigest getDigest(String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            LOG.error("Cannot create {} hash!", algorithm, e);
            return null;
        }
    }

    @Override
    public int read() throws IOException {
        int b = inputStream.read();
        if ((digest != null) && (hash == null)) {
            if (b == -1) {
                hash = bytesToHex(digest.digest());
            } else {
                digest.update((byte) b);
            }
        }
        return b;
    }

    @Nullable
    public String getHashString() {
        return hash;
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
