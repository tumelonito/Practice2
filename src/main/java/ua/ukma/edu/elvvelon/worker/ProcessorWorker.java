package ua.ukma.edu.elvvelon.worker;

import ua.ukma.edu.elvvelon.Message;
import ua.ukma.edu.elvvelon.Packet;
import ua.ukma.edu.elvvelon.model.Store;

import java.util.concurrent.BlockingQueue;

public class ProcessorWorker implements Runnable {
    private final BlockingQueue<Packet> inputQueue;
    private final BlockingQueue<Message> outputQueue;
    private final Store store;

    public ProcessorWorker(BlockingQueue<Packet> inputQueue, BlockingQueue<Message> outputQueue, Store store) {
        this.inputQueue = inputQueue;
        this.outputQueue = outputQueue;
        this.store = store;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Packet requestPacket = inputQueue.take();
                Message requestMessage = requestPacket.getMessage();

                store.addProductToGroup("Cereals", "Buckwheat");
                store.addProductQuantity("Cereals", "Buckwheat", 10);

                // Create a simple "OK" response
                String responsePayload = "{\"status\":\"OK\", \"processedCommand\":" + requestMessage.getCommandType() + "}";
                Message responseMessage = new Message(
                        requestMessage.getCommandType() + 1000, // Response command type
                        requestMessage.getUserId(),
                        responsePayload
                );

                System.out.println("ProcessorWorker: Processed command, created response for user " + responseMessage.getUserId());
                outputQueue.put(responseMessage);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}