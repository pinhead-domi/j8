package org.pinhead;

public class OpcodeInfo {

    public Opcode opcode;
    public int address;
    public int nibble;
    public int x;
    public int y;
    public int kk;

    public OpcodeInfo(Opcode opcode, int address, int nibble, int x, int y, int kk) {
        this.opcode = opcode;
        this.address = address;
        this.nibble = nibble;
        this.x = x;
        this.y = y;
        this.kk = kk;
    }

    @Override
    public String toString() {
        return String.format("[%s]: addr: [%x], nibble: [%x], x: [%x], y[%x], kk: [%x]",
                opcode.name(),
                address,
                nibble,
                x,
                y,
                kk
        );
    }

}
