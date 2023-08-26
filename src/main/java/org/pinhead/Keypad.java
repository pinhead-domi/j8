package org.pinhead;

import java.util.Optional;
import java.util.logging.Logger;

public class Keypad {

    private static final Logger logger = Logger.getLogger(Keypad.class.getName());

    private boolean[] keys = new boolean[4*4];

    public Keypad() {
        logger.info("Keypad finished initialization");
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
