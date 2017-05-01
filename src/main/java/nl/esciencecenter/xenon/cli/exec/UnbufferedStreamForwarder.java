package nl.esciencecenter.xenon.cli.exec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class UnbufferedStreamForwarder extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(nl.esciencecenter.xenon.util.StreamForwarder.class);

    private final InputStream in;
    private final OutputStream out;

    private boolean done = false;

    /**
     * Create a new StreamForwarder and start it immediately.
     *
     * @param in the {@link java.io.InputStream} to read from.
     * @param out the {@link java.io.OutputStream} to write to.
     */
    public UnbufferedStreamForwarder(InputStream in, OutputStream out) {
        this.in = in;
        this.out = out;

        setDaemon(true);
        setName("Stream forwarder");
        start();
    }

    /**
     * Closes the input stream, thereby stopping the stream forwarder, and closing the output stream.
     *
     * @param c The {@link java.io.Closeable} to close (i.e., the {@link java.io.InputStream} or {@link java.io.OutputStream})
     * @param error The error message to print if the close results in an Exception
     */
    private void close(Closeable c, String error) {
        try {
            c.close();
        } catch (Exception e) {
            if (error != null) {
                LOGGER.error(error, e);
            }
        }
    }

    /**
     * Tell the daemon thread that we are done.
     */
    private synchronized void done() {
        done = true;
        notifyAll();
    }

    /**
     * Wait for a given timeout for the StreamForwarder to terminate by reading an end-of-stream on the input. When the timeout
     * expires both input and output streams will be closed, regardless of whether the input has reached end-of-line.
     *
     * @param timeout
     *          The number of milliseconds to wait for termination.
     */
    public synchronized void terminate(long timeout) {

        if (done) {
            return;
        }

        if (timeout > 0) {
            long deadline = System.currentTimeMillis() + timeout;
            long left = timeout;

            while (!done && left > 0) {

                try {
                    wait(left);
                } catch (InterruptedException e) {
                    // ignored
                }

                left = deadline - System.currentTimeMillis();
            }
        }

        if (!done) {
            close(in, "InputStream did not close within " + timeout + " ms. Forcing close!");

            if (out != null) {
                close(out, null);
            }
        }
    }

    /**
     * Main entry method for the daemon thread.
     */
    public void run() {
        try {
            while (true) {
                int read = in.read();

                if (read == -1) {
                    // NOTE: Streams must be closed before done is called, or we'll have a race condition!
                    close(in, null);

                    if (out != null) {
                        close(out, null);
                    }

                    done();
                    return;
                }

                if (out != null) {
                    out.write(read);
                }
            }
        } catch (IOException e) {
            close(in, null);

            if (out != null) {
                close(out, null);
            }
        }
    }
}