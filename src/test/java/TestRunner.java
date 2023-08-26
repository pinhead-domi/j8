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

}
