package com.alexanderberndt.appintegration.tasks.process;

import com.alexanderberndt.appintegration.engine.context.TaskContext;
import com.alexanderberndt.appintegration.engine.resources.ExternalResource;
import com.alexanderberndt.appintegration.pipeline.task.ProcessingTask;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;

import javax.annotation.Nonnull;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
public class FileSizeValidationTask implements ProcessingTask {

    public static final String MIN_SIZE_PROP = "min-size";

    public static final String ERROR_LEVEL_PROP = "error-level";

    public static final String MESSAGE_PROP = "message";

    @Override
    public void declareTaskPropertiesAndDefaults(TaskContext taskContext) {
        taskContext.setValue(MIN_SIZE_PROP, 100);
        taskContext.setValue(ERROR_LEVEL_PROP, "warn");
        taskContext.setValue(MESSAGE_PROP, "File is very small!");
    }

    @Override
    public void process(@Nonnull TaskContext context, @Nonnull ExternalResource resource) {
        final int minSize = context.getValue(MIN_SIZE_PROP, Integer.class);
        final String errorLevel = context.getValue(ERROR_LEVEL_PROP, String.class);
        final String message = context.getValue(MESSAGE_PROP, String.class);

        resource.appendInputStreamFilter(in -> new ByteCountingInputStream(in, context, minSize, errorLevel, message));
    }


    private static class ByteCountingInputStream extends FilterInputStream {

        private final TaskContext context;

        private final int minSize;

        private final String errorLevel;

        private final String message;

        private long byteCount;

        private long markedByteCount;

        public ByteCountingInputStream(InputStream in, TaskContext context, int minSize, String errorLevel, String message) {
            super(in);
            this.context = context;
            this.minSize = minSize;
            this.errorLevel = errorLevel;
            this.message = message;
        }

        private void incrementByteCount(long delta) {
            byteCount += delta;
        }


        @Override
        public int read() throws IOException {
            int value = super.read();
            if (value != -1) {
                incrementByteCount(1);
            }
            return value;
        }

        @Override
        public int read(@Nonnull byte[] b) throws IOException {
            int size = super.read(b);
            if (size != -1) {
                incrementByteCount(size);
            }
            return size;
        }

        @Override
        public int read(@Nonnull byte[] b, int off, int len) throws IOException {
            int size = super.read(b, off, len);
            if (size != -1) {
                incrementByteCount(size);
            }
            return size;
        }

        @Override
        public long skip(long n) throws IOException {
            long size = super.skip(n);
            incrementByteCount(size);
            return size;
        }

        @Override
        public synchronized void mark(int readlimit) {
            super.mark(readlimit);
            markedByteCount = byteCount;
        }

        @Override
        public synchronized void reset() throws IOException {
            super.reset();
            byteCount = markedByteCount;
        }

        @Override
        public void close() throws IOException {
            super.close();
            if (byteCount < this.minSize) {
                final String msg = this.message + " (size: " + byteCount + " bytes)";
                if (StringUtils.equals(this.errorLevel, "warn")) {
                    context.addWarning(msg);
                } else if (StringUtils.equals(this.errorLevel, "error")) {
                    context.addError(msg);
                } else {
                    context.addWarning("Unknown error-level %s! It should be either 'warn' or 'error'!", this.errorLevel);
                    context.addWarning(msg);
                }
            }
        }
    }
}
