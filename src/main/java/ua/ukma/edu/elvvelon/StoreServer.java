package ua.ukma.edu.elvvelon;

import ua.ukma.edu.elvvelon.model.Store;
import ua.ukma.edu.elvvelon.service.FakeMessageReceiver;
import ua.ukma.edu.elvvelon.service.FakeSender;
import ua.ukma.edu.elvvelon.service.MessageReceiver;
import ua.ukma.edu.elvvelon.worker.DecoderWorker;
import ua.ukma.edu.elvvelon.worker.EncoderWorker;
import ua.ukma.edu.elvvelon.worker.ProcessorWorker;

import java.util.concurrent.*;

public class StoreServer {
    private final ExecutorService decoderPool = Executors.newFixedThreadPool(2);
    private final ExecutorService processorPool = Executors.newFixedThreadPool(4);
    private final ExecutorService encoderPool = Executors.newFixedThreadPool(2);

    private final BlockingQueue<byte[]> receiveQueue = new LinkedBlockingQueue<>(100);
    private final BlockingQueue<Packet> processQueue = new LinkedBlockingQueue<>(100);
    private final BlockingQueue<Message> sendQueue = new LinkedBlockingQueue<>(100);

    private final Store store = new Store();
    private final FakeSender sender = new FakeSender();
    private final MessageReceiver receiver = new FakeMessageReceiver(receiveQueue);
    private final Thread receiverThread = new Thread(receiver);

    public void start() {
        System.out.println("Server is starting...");
        // Start workers for each stage of the pipeline
        for (int i = 0; i < 2; i++) {
            decoderPool.submit(new DecoderWorker(receiveQueue, processQueue));
        }
        for (int i = 0; i < 4; i++) {
            processorPool.submit(new ProcessorWorker(processQueue, sendQueue, store));
        }
        for (int i = 0; i < 2; i++) {
            encoderPool.submit(new EncoderWorker(sendQueue, sender));
        }

        // Start the message receiver
        receiverThread.start();
        System.out.println("Server started successfully.");
    }

    public void stop() throws InterruptedException {
        System.out.println("Server is shutting down...");

        // Stop generating new messages
        receiver.stop();
        receiverThread.join(1000);

        // Gracefully shutdown executor pools
        shutdownPool(decoderPool, "Decoder");
        shutdownPool(processorPool, "Processor");
        shutdownPool(encoderPool, "Encoder");

        System.out.println("Server has shut down.");
        store.printStoreState();
    }

    private void shutdownPool(ExecutorService pool, String name) throws InterruptedException {
        pool.shutdown(); // Disable new tasks from being submitted
        if (!pool.awaitTermination(5, TimeUnit.SECONDS)) {
            System.err.println(name + " pool did not terminate in 5 seconds.");
            pool.shutdownNow(); // Cancel currently executing tasks
        }
    }

    public static void main(String[] args) throws InterruptedException {
        StoreServer server = new StoreServer();
        server.start();

        // Run the server for a period of time then shut it down
        Thread.sleep(10000); // Run for 10 seconds

        server.stop();
    }
}