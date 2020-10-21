package com.alexanderberndt.appintegration.engine.cache.impl;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Deque;

public class ReadAheadInputStream extends InputStream {

    public static final int CHUNK_SIZE = 8 * 1024;

    private final InputStream inputStream;

    private byte[] buffer;

    private int pos = 0;

    private IOException readAheadException;

    private boolean isInputFullyRead = false;


    public ReadAheadInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }



    @Override
    public int read() throws IOException {
        if (readAheadException != null) throw readAheadException;

        if (!isInputFullyRead && ((buffer == null) || (pos >= buffer.length))) {
            fillBuffer();
        }
        if (pos >= buffer.length) {
            return -1;
        }
        return buffer[pos++] & 0xFF;
    }

    public boolean isInputFullyRead() {
        return isInputFullyRead;
    }


    public boolean readAhead(int minSize) {
        if (buffer == null) {
            fillBuffer(1 + minSize / CHUNK_SIZE);
        }
        return isInputFullyRead();
    }

    private void fillBuffer(int maxChunks) {

        final Deque<byte[]> chunksQueue = new ArrayDeque<>();

        int lastChunkSize = CHUNK_SIZE;
        while ((chunksQueue.size() < maxChunks) && (lastChunkSize == CHUNK_SIZE)) {
            byte[] chunk = new byte[CHUNK_SIZE];
            try {
                lastChunkSize = IOUtils.read(inputStream, chunk);
            } catch (IOException e) {
                readAheadException = e;
                return;
            }
            chunksQueue.addLast(chunk);
        }

        int offset = 0;
        this.buffer = new byte[(chunksQueue.size() - 1) * CHUNK_SIZE + lastChunkSize];
        for (byte[] chunk : chunksQueue) {
            int nextChunkSize = Math.min(CHUNK_SIZE, buffer.length - offset);
            if (nextChunkSize > 0) {
                System.arraycopy(chunk, 0, this.buffer, offset, nextChunkSize);
                offset += CHUNK_SIZE;
            }
        }
        this.pos = 0;
        this.isInputFullyRead = (lastChunkSize != CHUNK_SIZE);
    }

    private void fillBuffer() throws IOException {
        final byte[] chunk = new byte[CHUNK_SIZE];
        final int chunkSize = IOUtils.read(inputStream, chunk);
        if (chunkSize == CHUNK_SIZE) {
            this.buffer = chunk;
            this.pos = 0;
            this.isInputFullyRead = false;
        } else {
            this.buffer = new byte[chunkSize];
            System.arraycopy(chunk, 0, this.buffer, 0, chunkSize);
            this.pos = 0;
            this.isInputFullyRead = true;
        }
    }

}
