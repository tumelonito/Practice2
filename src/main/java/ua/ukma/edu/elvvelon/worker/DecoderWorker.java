package ua.ukma.edu.elvvelon.worker;

import ua.ukma.edu.elvvelon.Packet;
import ua.ukma.edu.elvvelon.PacketParser;
import java.util.concurrent.BlockingQueue;

public class DecoderWorker implements Runnable {
    private final BlockingQueue<byte[]> inputQueue;
    private final BlockingQueue<Packet> outputQueue;
    private final PacketParser parser = new PacketParser();

    public DecoderWorker(BlockingQueue<byte[]> inputQueue, BlockingQueue<Packet> outputQueue) {
        this.inputQueue = inputQueue;
        this.outputQueue = outputQueue;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                byte[] data = inputQueue.take();
                try {
                    Packet packet = parser.parse(data);
                    System.out.println("DecoderWorker: Parsed packet " + packet.getPacketId());
                    outputQueue.put(packet);
                } catch (Exception e) {
                    System.err.println("Failed to parse packet: " + e.getMessage());
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}