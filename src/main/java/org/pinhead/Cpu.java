package org.pinhead;

import java.util.Random;
import java.util.logging.Logger;

public class Cpu {
    private static final Logger logger = Logger.getLogger(Cpu.class.getName());
    private final Memory memory;
    private final Screen screen;
    private final Keypad keypad;
    private final Thread timerThread;

    private int PC;
    private int I;
    private int DT;
    private int ST;
    private final int[] V = new int[16];

    private OpcodeInfo state;

    private boolean running = false;
    private boolean ready = false;

    private final Random rng = new Random();

    public Cpu(int[] data) {
        this();
        memory.loadImage(data);
    }

    public Cpu() {
        this.memory = new Memory();
        this.keypad = new Keypad();
        this.screen = new Screen(this.keypad);

        this.running = false;
        this.ready = true;

        this.timerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(running) {
                    updateTimers();
                    try {
                        Thread.sleep((int)((1.0/60.0)*1000.0));
                    } catch (InterruptedException e) {
                        running = false;
                    }
                }
            }
        });

        PC = 0x200;

        logger.info("Cpu finished initialization");
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isReady() {
        return ready;
    }

    public int getPC() {
        return PC;
    }

    public void start() {
        running = true;
        logger.info("Cpu starts running");
        timerThread.start();

        while (running) {
            run();
            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        logger.info("Cpu stopped running");
    }

    public void singleStep() {
        run();
    }

    private void run() {
        state = memory.fetchAndDecode(PC);

        switch (state.opcode) {
            case RET        -> RET();
            case JMP_IMM    -> JUMP_IMM();
            case CALL       -> CALL();
            case SE_IMM     -> SE_IMM();
            case SNE_IMM    -> SNE_IMM();
            case SE         -> SE();
            case LD_IMM     -> LD_IMM();
            case ADD_IMM    -> ADD_IMM();
            case LD         -> LD();
            case OR         -> OR();
            case AND        -> AND();
            case XOR        -> XOR();
            case ADD_CARRY  -> ADD_CARRY();
            case SUB_CARRY  -> SUB_CARRY();
            case SHR        -> SHR();
            case SUB_NCARRY -> SUB_NCARRY();
            case SHL        -> SHL();
            case SNE        -> SNE();
            case LD_I       -> LD_I();
            case JMP        -> JMP();
            case RND        -> RND();
            case DRW        -> DRW();
            case SKP        -> SKP();
            case SKNP       -> SKNP();
            case LD_DT      -> LD_DT();
            case LD_K       -> LD_K();
            case LD_F       -> LD_F();
            case SET_DT     -> SET_DT();
            case SET_ST     -> SET_ST();
            case ADD_I      -> ADD_I();
            case BCD        -> BCD();
            case STORE      -> STORE();
            case LOAD       -> LOAD();

            case NONE       -> {
                logger.severe("Unimplemented opcode!");
                running = false;
            }
            default         -> {
                logger.severe("Uninterpreted opcode!");
                running = false;
            }
        }

        PC &= 0xFFFF;
    }

    private void updateTimers() {
        if(DT > 0)
            DT--;
        if(ST > 0)
            ST--;
    }

    private void RET() {
        PC = memory.pop();
    }

    private void JUMP_IMM() {
        PC = state.address;
    }

    private void CALL() {
        memory.push(PC);
        PC = state.address;
    }

    private void SE_IMM() {
        PC += V[state.x] == state.kk ? 4 : 2;
    }

    private void SNE_IMM() {
        PC += V[state.x] != state.kk ? 4 : 2;
    }

    private void SE() {
        PC += V[state.x] == V[state.y] ? 4 : 2;
    }

    private void LD_IMM() {
        V[state.x] = state.kk;
        PC += 2;
    }

    private void ADD_IMM() {
        V[state.x] += state.kk;
        PC += 2;
    }

    private void LD() {
        V[state.x] = V[state.y];
        PC += 2;
    }

    private void OR() {
        V[state.x] = V[state.x] | V[state.y];
        PC += 2;
    }

    private void AND() {
        V[state.x] = V[state.x] & V[state.y];
        PC += 2;
    }

    private void XOR() {
        V[state.x] = V[state.x] ^ V[state.y];
        PC += 2;
    }

    private void ADD_CARRY() {
        V[state.x] += V[state.y];
        V[0xF] = V[state.x] > 0xFF ? 1 : 0;
        V[state.x] &= 0xFF;

        PC += 2;
    }

    private void SUB_CARRY() {
        V[0xF] = V[state.x] > V[state.y] ? 1 : 0;
        V[state.x] -= V[state.y];
        V[state.x] &= 0xFF;

        PC += 2;
    }

    private void SHR() {
        V[0xF] = V[state.x] & 1;
        V[state.x] = V[state.x] >>> 1;

        PC += 2;
    }

    private void SUB_NCARRY() {
        V[0xF] = V[state.y] > V[state.x] ? 1 : 0;
        V[state.x] = V[state.y] - V[state.x];
        V[state.y] &= 0xFF;

        PC += 2;
    }

    private void SHL() {
        V[0xF] = V[state.x] >>> 7 & 1;
        V[state.x] = (V[state.x] << 1) & 0xFF;

        PC += 2;
    }

    private void SNE() {
        PC += V[state.x] != V[state.y] ? 4 : 2;
    }

    private void LD_I() {
        I = state.address;
        PC += 2;
    }

    private void JMP() {
        PC = state.address + V[0];
    }

    private void RND() {
        V[state.x] = rng.nextInt() & 0xFF & state.kk;
        PC += 2;
    }

    private void DRW() {
        boolean erased = false;

        for (int line=0; line<state.nibble; line++)
            erased |= screen.drawByte(memory.loadByte(I + line), V[state.x], V[state.y] + line);

        screen.update();
        V[0xF] = erased ? 1 : 0;
        PC += 2;
    }

    private void SKP() {
        PC += keypad.isKeyPressed(V[state.x]) ? 4 : 2;
    }

    private void SKNP() {
        PC += !keypad.isKeyPressed(V[state.x]) ? 4 : 2;
    }

    private void LD_DT() {
        V[state.x] = DT;
        PC += 2;
    }

    private void LD_K() {
        if(keypad.pressedKey().isPresent()) {
            V[state.x] = keypad.pressedKey().get();
            PC += 2;
        }
    }

    private void ADD_I() {
        I += V[state.x];
        PC += 2;
    }

    private void LD_F() {
        I = V[state.x] * 5;
        PC += 2;
    }

    private void SET_DT() {
        DT = V[state.x];
        PC += 2;
    }

    private void SET_ST() {
        ST = V[state.x];
        PC += 2;
    }

    private void BCD() {
        int temp = V[state.x];

        memory.storeByte(I, temp/100);
        temp %= 100;

        memory.storeByte(I+1, temp/10);
        temp %= 10;

        memory.storeByte(I+2, temp);

        PC += 2;
    }

    private void STORE() {
        for(int i=0; i<=V[state.x] && i<= 0xF; i++) {
            memory.storeByte(I + i, V[i]);
        }
        PC += 2;
    }

    private void LOAD() {
        for(int i=0; i<=V[state.x] && i<= 0xF; i++) {
            V[i] = memory.loadByte(I + i);
        }
        PC += 2;
    }

}
