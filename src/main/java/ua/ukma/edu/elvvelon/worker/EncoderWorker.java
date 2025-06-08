package ua.ukma.edu.elvvelon.worker;

import ua.ukma.edu.elvvelon.Message;
import ua.ukma.edu.elvvelon.PacketBuilder;
import ua.ukma.edu.elvvelon.service.FakeSender;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

public class EncoderWorker implements Runnable {
    private final BlockingQueue<Message> inputQueue;
    private final FakeSender sender;
    private final AtomicLong packetIdCounter = new AtomicLong(0);

    public EncoderWorker(BlockingQueue<Message> inputQueue, FakeSender sender) {
        this.inputQueue = inputQueue;
        this.sender = sender;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Message response = inputQueue.take();
                try {
                    PacketBuilder builder = new PacketBuilder((byte) 0, packetIdCounter.incrementAndGet(), response);
                    byte[] packetBytes = builder.build();
                    sender.sendMessage(packetBytes, "client_address"); // Address is fake here
                } catch (Exception e) {
                    System.err.println("Failed to build response packet: " + e.getMessage());
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}