package org.pinhead;

public class Main {
    public static void main(String[] args) {
        int[] data = {  0x60, 0x05, // LD V0 <- 5
                        0x61, 0x00, // LD V1 <- 0
                        0x62, 0x00, // LD V2 <- 0
                        0xA0, 0x00, // LD I <- 0
                        0xD1, 0x25, // DRW
                        0xF0, 0x1E, // ADD V0 to I
                        0x71, 0x0A, // ADD 0xA to V1
                        0xD1, 0x25, // DRW
                        0xF0, 0x1E, // ADD V0 to I
                        0x71, 0x0A, // ADD 0xA to V1,
                        0xD1, 0x25, // DRW
                        0xF0, 0x1E, // ADD V0 to I
                        0x71, 0x0A, // ADD 0xA to V1,
                        0xD1, 0x25, // DRW
                        0xF0, 0x1E, // ADD V0 to I
                        0x71, 0x0A, // ADD 0xA to V1,
                        0xD1, 0x25, // DRW
                        0xF0, 0x1E, // ADD V0 to I
                        0x71, 0x0A, // ADD 0xA to V1,
                        0xF0, 0x0A, // LD K
                        0xF0, 0x29, // LD F
                        0xD1, 0x25, // DRW

        };
        Cpu cpu = new Cpu("/Users/dominik/Downloads/test_opcode.ch8");
        cpu.start();
    }
}