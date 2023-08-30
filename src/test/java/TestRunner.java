import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.pinhead.Cpu;

public class TestRunner {

    @Test
    public void init_cpu() {
        Cpu cpu = new Cpu(true);

        assertTrue(cpu.isReady());
        assertFalse(cpu.isRunning());
    }

    @Test
    public void jmp_imm() {
        int[] data = {0x12, 0x34};
        Cpu cpu = new Cpu(data);

        cpu.singleStep();
        assertEquals(cpu.getPC(), 0x234);
    }

    @Test
    public void call() {
        int[] data = {0x22, 0x02, 0x00, 0xEE};

        Cpu cpu = new Cpu(data);

        cpu.singleStep();
        assertEquals(cpu.getPC(), 0x202);
        assertEquals(cpu.getStack()[0], 0x200);

        cpu.singleStep();
        assertEquals(cpu.getPC(), 0x200);
    }

    @Test
    public void ld_imm() {
        int[] data = {0x61, 0x69};
        Cpu cpu = new Cpu(data);

        cpu.singleStep();
        assertEquals(cpu.getV()[0x1], 0x69);
    }
    @Test
    public void se_imm() {
        int[] data = {0x61, 0x69, 0x31, 0x68, 0x31, 0x69};
        Cpu cpu = new Cpu(data);

        cpu.singleStep();
        assertEquals(cpu.getV()[0x1], 0x69);
        int prevPC = cpu.getPC();

        cpu.singleStep();
        assertEquals(cpu.getPC(), prevPC+2);
        prevPC = cpu.getPC();

        cpu.singleStep();
        assertEquals(cpu.getPC(), prevPC+4);
    }

    @Test
    public void sne_imm() {
        int[] data = {0x61, 0x69, 0x41, 0x69, 0x41, 0x68};
        Cpu cpu = new Cpu(data);

        cpu.singleStep();
        assertEquals(cpu.getV()[0x1], 0x69);
        int prevPC = cpu.getPC();

        cpu.singleStep();
        assertEquals(cpu.getPC(), prevPC+2);
        prevPC = cpu.getPC();

        cpu.singleStep();
        assertEquals(cpu.getPC(), prevPC+4);
    }

    @Test
    public void se() {
        int[] data = {
                0x61, 0x69, // LD V1 <- 69
                0x62, 0x69, // LD V2 <- 69
                0x63, 0x68, // LD V3 <- 68
                0x51, 0x30, // Skip if V1 and V2 are equal
                0x51, 0x20};// Skip if V1 and V3 are equal
        Cpu cpu = new Cpu(data);

        cpu.singleStep();
        assertEquals(cpu.getV()[1], 0x69);
        cpu.singleStep();
        assertEquals(cpu.getV()[2], 0x69);
        cpu.singleStep();
        assertEquals(cpu.getV()[3], 0x68);
        int prevPC = cpu.getPC();

        cpu.singleStep();
        assertEquals(cpu.getPC(), prevPC+2);
        prevPC = cpu.getPC();

        cpu.singleStep();
        assertEquals(cpu.getPC(), prevPC+4);
    }

    @Test
    public void add_imm() {
        int[] data = {
                0x61, 0x69, // LD V1 <- 69
                0x71, 0x01, // ADD 1 to V1
                0x61, 0xFF, // LD V1 <- 0xFF
                0x71, 0x01, // ADD 1 to V1
                };
        Cpu cpu = new Cpu(data);

        cpu.singleStep();
        assertEquals(0x69, cpu.getV()[1]);
        cpu.singleStep();
        assertEquals(0x6A, cpu.getV()[1]);
        cpu.singleStep();
        assertEquals(0xFF, cpu.getV()[1]);
        cpu.singleStep();
        assertEquals(0x00, cpu.getV()[1]);
    }

    @Test
    public void add_carry() {
        int[] data = {
                0x61, 0xFF, // LD V1 <- 0xFF
                0x62, 0x01, // LD V2 <- 0x01
                0x63, 0x00, // LD V3 <- 0x00
                0x81, 0x34, // ADD V3 to V1 w. carry
                0x81, 0x24, // ADD V2 to V1 w. carry
        };
        Cpu cpu = new Cpu(data);

        cpu.singleStep();
        assertEquals(0xFF, cpu.getV()[1]);
        cpu.singleStep();
        assertEquals(0x01, cpu.getV()[2]);
        cpu.singleStep();
        assertEquals(0x00, cpu.getV()[3]);
        cpu.singleStep();
        assertEquals(0xFF, cpu.getV()[1]);
        assertEquals(0x00, cpu.getV()[0xF]);
        cpu.singleStep();
        assertEquals(0x00, cpu.getV()[1]);
        assertEquals(0x01, cpu.getV()[0xF]);
    }

    @Test
    public void ld_reg() {
        int[] data = {
                0x61, 0x69, // LD V1 <- 69
                0x62, 0x00, // LD V2 <- 0
                0x82, 0x10, // LD V1 <- V2
        };
        Cpu cpu = new Cpu(data);

        cpu.singleStep();
        assertEquals(0x69, cpu.getV()[1]);
        cpu.singleStep();
        assertEquals(0x00, cpu.getV()[2]);
        cpu.singleStep();
        assertEquals(0x69, cpu.getV()[2]);
    }

    @Test
    public void shr() {
        int[] data = {
                0x61, 0x03, // LD V1 <- 3
                0x81, 0x06, // SHR V1
                0x81, 0x06, // SHR V1
                0x81, 0x06, // SHR V1
        };
        Cpu cpu = new Cpu(data);

        cpu.singleStep();
        assertEquals(0x03, cpu.getV()[1]);
        cpu.singleStep();
        assertEquals(0x01, cpu.getV()[1]);
        assertEquals(0x01, cpu.getV()[0xF]);
        cpu.singleStep();
        assertEquals(0x00, cpu.getV()[1]);
        assertEquals(0x01, cpu.getV()[0xF]);
        cpu.singleStep();
        assertEquals(0x00, cpu.getV()[1]);
        assertEquals(0x00, cpu.getV()[0xF]);
    }

    @Test
    public void shl() {
        int[] data = {
                0x61, 0xFF, // LD V1 <- 3
                0x81, 0x0E, // SHL V1
                0x81, 0x0E, // SHL V1
                0x61, 0x01, // LD V1 <- 3
                0x81, 0x0E, // SHL V1
                0x81, 0x0E, // SHL V1
        };
        Cpu cpu = new Cpu(data);

        cpu.singleStep();
        assertEquals(0xFF, cpu.getV()[1]);
        cpu.singleStep();
        assertEquals(0xFE, cpu.getV()[1]);
        assertEquals(0x01, cpu.getV()[0xF]);
        cpu.singleStep();
        assertEquals(0xFC, cpu.getV()[1]);
        assertEquals(0x01, cpu.getV()[0xF]);
        cpu.singleStep();
        assertEquals(0x01, cpu.getV()[1]);
        cpu.singleStep();
        assertEquals(0x02, cpu.getV()[1]);
        assertEquals(0x00, cpu.getV()[0xF]);
        cpu.singleStep();
        assertEquals(0x04, cpu.getV()[1]);
        assertEquals(0x00, cpu.getV()[0xF]);
    }

    @Test
    public void or() {
        int[] data = {
                0x61, 0x12, // LD V1 <- 0xFF
                0x62, 0x34, // LD V2 <- 0x01
                0x81, 0x21, // V1 OR V2
        };
        Cpu cpu = new Cpu(data);

        cpu.singleStep();
        assertEquals(0x12, cpu.getV()[1]);
        cpu.singleStep();
        assertEquals(0x34, cpu.getV()[2]);
        cpu.singleStep();
        assertEquals(0x12 | 0x34, cpu.getV()[1]);
    }

    @Test
    public void and() {
        int[] data = {
                0x61, 0x12, // LD V1 <- 0xFF
                0x62, 0x34, // LD V2 <- 0x01
                0x81, 0x22, // V1 AND V2
        };
        Cpu cpu = new Cpu(data);

        cpu.singleStep();
        assertEquals(0x12, cpu.getV()[1]);
        cpu.singleStep();
        assertEquals(0x34, cpu.getV()[2]);
        cpu.singleStep();
        assertEquals(0x12 & 0x34, cpu.getV()[1]);
    }

    @Test
    public void xor() {
        int[] data = {
                0x61, 0x12, // LD V1 <- 0xFF
                0x62, 0x34, // LD V2 <- 0x01
                0x81, 0x23, // V1 AND V2
        };
        Cpu cpu = new Cpu(data);

        cpu.singleStep();
        assertEquals(0x12, cpu.getV()[1]);
        cpu.singleStep();
        assertEquals(0x34, cpu.getV()[2]);
        cpu.singleStep();
        assertEquals(0x12 ^ 0x34, cpu.getV()[1]);
    }

    @Test
    public void sub_borrow() {
        int[] data = {
                0x61, 0x9,  // LD V1 <- 0xFF
                0x62, 0xA,  // LD V2 <- 0x01
                0x81, 0x25, // Subtract V2 from V1, set VF if V1 > V2
                0x61, 0xA,  // LD V1 <- 0xFF
                0x62, 0x9,  // LD V2 <- 0x01
                0x81, 0x25, // Subtract V2 from V1, set VF if V1 > V2
        };
        Cpu cpu = new Cpu(data);

        cpu.singleStep();
        assertEquals(0x9, cpu.getV()[1]);
        cpu.singleStep();
        assertEquals(0xA, cpu.getV()[2]);
        cpu.singleStep();
        assertEquals(0x1, cpu.getV()[1]);
        assertEquals(0x1, cpu.getV()[0xF]);

        cpu.singleStep();
        assertEquals(0xA, cpu.getV()[1]);
        cpu.singleStep();
        assertEquals(0x9, cpu.getV()[2]);
        cpu.singleStep();
        assertEquals((byte)0xFF, (byte)cpu.getV()[1]);
        assertEquals(0x0, cpu.getV()[0xF]);
    }

}
