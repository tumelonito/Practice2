package ua.ukma.edu.elvvelon.service;

public class FakeSender {
    public void sendMessage(byte[] message, String targetAddress) {
        System.out.println("Sender: Sending " + message.length + " bytes to " + targetAddress + ".");
        // System.out.println("   Data: " + Arrays.toString(message)); // Uncomment for debug
    }
}