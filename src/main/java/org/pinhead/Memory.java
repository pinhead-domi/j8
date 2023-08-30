package org.pinhead;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;

public class Memory {

    private static final Logger logger = Logger.getLogger(Memory.class.getName());

    public static final int[] FONT = {
            0xF0, 0x90, 0x90, 0x90, 0xF0, // 0
            0x20, 0x60, 0x20, 0x20, 0x70, // 1
            0xF0, 0x10, 0xF0, 0x80, 0xF0, // 2
            0xF0, 0x10, 0xF0, 0x10, 0xF0, // 3
            0x90, 0x90, 0xF0, 0x10, 0x10, // 4
            0xF0, 0x80, 0xF0, 0x10, 0xF0, // 5
            0xF0, 0x80, 0xF0, 0x90, 0xF0, // 6
            0xF0, 0x10, 0x20, 0x40, 0x50, // 7
            0xF0, 0x90, 0xF0, 0x90, 0xF0, // 8
            0xF0, 0x90, 0xF0, 0x10, 0xF0, // 9
            0xF0, 0x90, 0xF0, 0x90, 0x90, // A
            0xE0, 0x90, 0xE0, 0x90, 0xE0, // B
            0xF0, 0x80, 0x80, 0x80, 0xF0, // C
            0xE0, 0x90, 0x90, 0x90, 0xE0, // D
            0xF0, 0x80, 0xF0, 0x80, 0xF0, // E
            0xF0, 0x80, 0xF0, 0x80, 0x80  // F
    };

    private final int[] ram = new int[4096];
    public int[] stack = new int[16];
    private int SP = -1;

    public Memory() {
        int index = 0;
        for(int data: FONT) {
            ram[index++] = data;
        }
        logger.info("Memory finished initialization");
    }

    public int getSP() {
        return SP;
    }

    public int[] getStack() {
        return stack.clone();
    }

    public void loadBinary(String pathName) {
        logger.info("Reading binary file: " + pathName);
    }

    public void storeByte(int offset, int data) {
        ram[offset] = data & 0xFF;
    }

    public int loadByte(int offset) {
        return ram[offset];
    }

    public void push(int value) {
        if(SP > 0xF) {
            logger.severe("Stack overflow!");
            return;
        }
        stack[++SP] = value & 0xFFFF;
    }

    public int pop() {
        if(SP < 0) {
            logger.severe("Stack underflow!");
            return -1;
        }
        return stack[SP--];
    }

    public void loadImage(int[] binary) {
        if(binary.length > (4096 - 512))
            throw new RuntimeException("Binary exceeds maximum rom size");

        logger.info("Loading data from array");

        int index = 512;
        for(int data: binary)
            ram[index++] = data & 0xFF;

        logger.info("Finished loading array data");
    }

    public void loadFromFile(String path) {
        File file = new File(path);
        try(FileInputStream stream = new FileInputStream(file)) {
            int read = -1;
            int num = 0;
            while (num++ < (4096 - 512)) {
                if((read = stream.read()) == -1)
                    break;
                ram[511 + num] = read;
            }
            logger.fine("Finished loading rom");
        } catch (IOException e) {
            logger.severe("Failed to read file!");
            System.exit(-1);
        }
    }

    public OpcodeInfo fetchAndDecode(int offset) {
        int instruction = ram[offset] << 8 | ram[offset+1];
        logger.info(String.format("Fetched instruction: %x", instruction));

        OpcodeInfo info = new OpcodeInfo(
                Opcode.NONE,
                instruction & 0x0FFF,
                instruction & 0x000F,
                (instruction & 0x0F00) >>> 8,
                (instruction & 0x00F0) >>> 4,
                instruction & 0x00FF
        );

        switch (instruction >>> 12) {
            case 0 -> {
                switch (info.kk) {
                    case 0xE0 -> info.opcode = Opcode.CLS;
                    case 0xEE -> info.opcode = Opcode.RET;
                }
            }
            case 0x1            -> info.opcode = Opcode.JMP_IMM;
            case 0x2            -> info.opcode = Opcode.CALL;
            case 0x3            -> info.opcode = Opcode.SE_IMM;
            case 0x4            -> info.opcode = Opcode.SNE_IMM;
            case 0x5            -> info.opcode = Opcode.SE;
            case 0x6            -> info.opcode = Opcode.LD_IMM;
            case 0x7            -> info.opcode = Opcode.ADD_IMM;
            case 0x8 -> {
                switch (info.nibble) {
                    case 0x0    -> info.opcode = Opcode.LD;
                    case 0x1    -> info.opcode = Opcode.OR;
                    case 0x2    -> info.opcode = Opcode.AND;
                    case 0x3    -> info.opcode = Opcode.XOR;
                    case 0x4    -> info.opcode = Opcode.ADD_CARRY;
                    case 0x5    -> info.opcode = Opcode.SUB_NCARRY;
                    case 0x6    -> info.opcode = Opcode.SHR;
                    case 0x7    -> info.opcode = Opcode.SUB_CARRY;
                    case 0xE    -> info.opcode = Opcode.SHL;
                }
            }
            case 0x9            -> info.opcode = Opcode.SNE;
            case 0xA            -> info.opcode = Opcode.LD_I;
            case 0xB            -> info.opcode = Opcode.JMP;
            case 0xC            -> info.opcode = Opcode.RND;
            case 0xD            -> info.opcode = Opcode.DRW;
            case 0xE -> {
                switch (info.kk) {
                    case 0x9E   -> info.opcode = Opcode.SKP;
                    case 0xA1   -> info.opcode = Opcode.SKNP;
                }
            }
            case 0xF -> {
                switch (info.kk) {
                    case 0x07   -> info.opcode = Opcode.LD_DT;
                    case 0x0A   -> info.opcode = Opcode.LD_K;
                    case 0x15   -> info.opcode = Opcode.SET_DT;
                    case 0x18   -> info.opcode = Opcode.SET_ST;
                    case 0x1E   -> info.opcode = Opcode.ADD_I;
                    case 0x29   -> info.opcode = Opcode.LD_F;
                    case 0x33   -> info.opcode = Opcode.BCD;
                    case 0x55   -> info.opcode = Opcode.STORE;
                    case 0x65   -> info.opcode = Opcode.LOAD;
                }
            }
        }

        if(info.opcode == Opcode.NONE)
            logger.severe("Could not decode instruction");
        else
            logger.info("Decoded instruction: " + info);

        return info;
    }

}
