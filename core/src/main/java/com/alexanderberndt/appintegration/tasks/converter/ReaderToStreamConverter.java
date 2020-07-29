package com.alexanderberndt.appintegration.tasks.converter;

@Deprecated
public class ReaderToStreamConverter {

//    @Override
//    public String getName() {
//        return "reader-to-stream-converter";
//    }
//
//    @Override
//    public Class<Reader> getInputType() {
//        return Reader.class;
//    }
//
//    @Override
//    public Class<InputStream> getOutputType() {
//        return InputStream.class;
//    }
//
//    @Override
//    public InputStream filter(TaskContext context, Reader input) {
//        return new ReaderInputStream(input, Charset.defaultCharset());
//    }


//    private static class InnerReaderInputStream extends InputStream {
//
//        private final Reader input;
//
//        private byte[] buffer;
//
//        private int pos;
//
//        public InnerReaderInputStream(Reader input) {
//            this.input = input;
//        }
//
//        @Override
//        public int read() throws IOException {
//            if (buffer == null) {
//                fill();
//                if (buffer == null) {
//                    return -1;
//                }
//            }
//
//            int nextByte = buffer[pos++];
//            if (pos >= buffer.length) {
//                buffer = null;
//            }
//
//            return nextByte;
//        }
//
//        private void fill() throws IOException {
//            char[] charBuffer = new char[1024];
//            int charBufferLen;
//            charBufferLen = input.read(charBuffer);
//            if (charBufferLen != -1) {
//                return;
//            }
//
//            buffer = new String(charBuffer, 0, charBufferLen).getBytes(Charset.defaultCharset());
//            pos = 0;
//        }
//
//        @Override
//        public void close() throws IOException {
//            super.close();
//            input.close();
//        }
//    }
}

