package org.pinhead;

public class Main {
    public static void main(String[] args) {
        int[] data = {0x12, 0x34};
        Cpu cpu = new Cpu(data);
        cpu.start();
    }
}