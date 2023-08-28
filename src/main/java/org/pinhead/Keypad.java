package org.pinhead;

import java.util.Optional;
import java.util.logging.Logger;

public class Keypad {

    private static final Logger logger = Logger.getLogger(Keypad.class.getName());

    private boolean[] keys = new boolean[4*4];

    public Keypad() {
        logger.info("Keypad finished initialization");
    }

    public int getKeyMapping(char keyCode) {
        int index = -1;
        switch (keyCode) {
            case 'x' -> index = 0x0;
            case '1' -> index = 0x1;
            case '2' -> index = 0x2;
            case '3' -> index = 0x3;
            case 'q' -> index = 0x4;
            case 'w' -> index = 0x5;
            case 'e' -> index = 0x6;
            case 'a' -> index = 0x7;
            case 's' -> index = 0x8;
            case 'd' -> index = 0x9;
            case 'y' -> index = 0xA;
            case 'c' -> index = 0xB;
            case '4' -> index = 0xC;
            case 'r' -> index = 0xD;
            case 'f' -> index = 0xE;
            case 'v' -> index = 0xF;
        }
        return index;
    }

    public void keyDown(char keyCode) {
        int index = getKeyMapping((keyCode));
        if(!keys[index])
            logger.info(String.format("Key %x is now pressed", index));
        keys[index] = true;
    }

    public void keyUp(char keyCode) {
        int index = getKeyMapping((keyCode));
        if(keys[index])
            logger.info(String.format("Key %x is now released", index));
        keys[index] = false;
    }

    public boolean isKeyPressed(int key) {
        throw new RuntimeException("Not implemented");
    }

    public Optional<Integer> pressedKey() {
        for(int i=0; i<16; i++)
            if(keys[i])
                return Optional.of(i);

        return Optional.empty();
    }

}
