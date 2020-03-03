package com.angelsgate.sdk.AngelsGateDownload.downloader.internal.FileStream;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FileDownloadRandomAccessFile implements FileDownloadOutputStream {

    private final BufferedOutputStream out;
    private final FileDescriptor fd;
    private final RandomAccessFile randomAccess;

    private FileDownloadRandomAccessFile(File file) throws IOException {
        randomAccess = new RandomAccessFile(file, "rw");
        fd = randomAccess.getFD();
        out = new BufferedOutputStream(new FileOutputStream(fd));

    }

    @Override
    public synchronized void write(byte[] b ) throws IOException {
       out.write(b);

    }

    @Override
    public synchronized void flushAndSync() throws IOException {
        out.flush();
         fd.sync();
    }

    @Override
    public synchronized void close() throws IOException {
         out.close();
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

    public static FileDownloadOutputStream create(File file) throws IOException {
        return new FileDownloadRandomAccessFile(file);
    }

}
