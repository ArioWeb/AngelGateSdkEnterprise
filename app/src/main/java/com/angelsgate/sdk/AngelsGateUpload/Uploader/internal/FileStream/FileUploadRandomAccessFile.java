package com.angelsgate.sdk.AngelsGateUpload.Uploader.internal.FileStream;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FileUploadRandomAccessFile implements FileUploadInputStream {

    private final BufferedInputStream in;
    private final FileDescriptor fd;
    private final RandomAccessFile randomAccess;

    private FileUploadRandomAccessFile(File file) throws IOException {
        randomAccess = new RandomAccessFile(file, "rw");
        fd = randomAccess.getFD();
        in = new BufferedInputStream(new FileInputStream(fd));

    }

    @Override
    public synchronized int read(byte b[]) throws IOException {
       return in.read(b);

    }


    @Override
    public synchronized void close() throws IOException {
        in.close();
        randomAccess.close();
    }

    @Override
    public synchronized void seek(long offset) throws IOException {
        randomAccess.seek(offset);
    }

    @Override
    public synchronized void setLength(long totalBytes) throws IOException {
        randomAccess.setLength(totalBytes);
    }

    public static FileUploadInputStream create(File file) throws IOException {
        return new FileUploadRandomAccessFile(file);
    }

}
