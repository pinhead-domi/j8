package org.pinhead;

import java.util.logging.Logger;

public class Memory {

    private static final Logger logger = Logger.getLogger(Memory.class.getName());

    private final int[] ram = new int[4096];
    public int[] stack = new int[16];
    private int SP = -1;

    public Memory() {
        logger.info("Memory finished initialization");
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

    public OpcodeInfo fetchAndDecode(int offset) {
        int instruction = ram[offset] << 8 | ram[offset+1];
        OpcodeInfo info = new OpcodeInfo(
                Opcode.NONE,
                instruction & 0x0FFF,
                instruction & 0x000F,
                (instruction & 0x0F00) >>> 8,
                (instruction & 0x00F0) >>> 4,
                instruction & 0x00FF
        );

        switch (instruction & 0xF000 >>> 12) {
            case 0 -> {
                switch (info.kk) {
                    case 0x00 -> info.opcode = Opcode.CLS;
                    case 0xE0 -> info.opcode = Opcode.RET;
                    default -> logger.warning("Invalid opcode!");
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
                    case 0x5    -> info.opcode = Opcode.SUB_CARRY;
                    case 0x6    -> info.opcode = Opcode.SHR;
                    case 0x7    -> info.opcode = Opcode.SUB_NCARRY;
                    case 0xE    -> info.opcode = Opcode.SHL;
                }
            }
            case 0x9            -> info.opcode = Opcode.SNE;
            case 0xA            -> info.opcode = Opcode.LD_I;
            case 0xB            -> info.opcode = Opcode.JMP;
            case 0xC            -> info.opcode = Opcode.RND;
        }

        return info;
    }

}
