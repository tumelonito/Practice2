package ua.ukma.edu.elvvelon.service;

import ua.ukma.edu.elvvelon.Message;
import ua.ukma.edu.elvvelon.PacketBuilder;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

public class FakeMessageReceiver implements MessageReceiver {
    private final BlockingQueue<byte[]> inputQueue;
    private volatile boolean running = true;
    private final Random random = new Random();

    public FakeMessageReceiver(BlockingQueue<byte[]> inputQueue) {
        this.inputQueue = inputQueue;
    }

    @Override
    public void run() {
        while (running) {
            try {
                // Generate a random message and build a packet
                Message randomMessage = createRandomMessage();
                PacketBuilder builder = new PacketBuilder((byte) random.nextInt(10), random.nextLong(), randomMessage);
                byte[] packetBytes = builder.build();

                // Put the packet into the queue for the decoders
                inputQueue.put(packetBytes);

                System.out.println("Receiver: Generated and queued a new packet.");
                Thread.sleep(500); // Simulate message arrival rate

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running = false;
            } catch (Exception e) {
                System.err.println("Receiver error: " + e.getMessage());
            }
        }
    }

    private Message createRandomMessage() {
        int commandType = random.nextInt(5); // 0 to 4
        int userId = 1000 + random.nextInt(100);
        String payload = "{\"group\":\"Cereals\",\"product\":\"Buckwheat\",\"quantity\":10}"; // Example payload
        return new Message(commandType, userId, payload);
    }

    @Override
    public void stop() {
        running = false;
    }
}