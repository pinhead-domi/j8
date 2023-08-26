package org.pinhead;

import java.util.logging.Logger;

public class Screen {

    private static final Logger logger = Logger.getLogger(Screen.class.getName());

    public byte[] screenBuffer = new byte[64*32];

    public Screen() {
        logger.info("Screen finished initialization");
    }

    public boolean drawByte(int sprite, int startX, int startY) {

        boolean erased = false;

        while (startY > 31)
            startY -= 32;
        while (startY < 0)
            startY += 32;
        while (startX < 0)
            startX += 64;

        for(byte index = 0; index < 8; index++) {

            startX += index;
            while (startX > 63)
                startX -= 64;

            byte screenBit = screenBuffer[(startY*64) + startX];
            byte spriteBit = (byte) ((sprite >>> (7-index)) & 1);

            erased |= screenBit == 1 && screenBit != spriteBit;
            screenBuffer[(startY*64) + startX] = (byte) (screenBit ^ spriteBit);
        }

        logger.info("Finished drawing sprite, " + (erased ? "erased" : "did not erase") + " bit");
        return erased;
    }

}
