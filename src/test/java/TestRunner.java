import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.pinhead.Cpu;

public class TestRunner {

    @Test
    public void init_cpu() {
        Cpu cpu = new Cpu();

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

}
