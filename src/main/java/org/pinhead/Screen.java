package org.pinhead;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.logging.Logger;

public class Screen {

    private static final Logger logger = Logger.getLogger(Screen.class.getName());

    public boolean[] screenBuffer = new boolean[64*32];

    private JFrame frame;
    private JPanel panel;

    private Keypad listener;

    public Screen(Keypad keys) {

        listener = keys;

        frame = new JFrame("J-8");
        panel = new JPanel();

        frame.setSize(640, 320);
        frame.setLayout(new BorderLayout());
        frame.add(panel, BorderLayout.CENTER);

        frame.setVisible(true);
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                listener.keyDown(e.getKeyChar());
            }

            @Override
            public void keyReleased(KeyEvent e) {
                listener.keyUp(e.getKeyChar());
            }
        });

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

            while (startX > 63)
                startX -= 64;

            boolean screenBit = screenBuffer[(startY*64) + startX];
            boolean spriteBit = ((sprite >>> (7-index)) & 1) == 1;

            erased |= screenBit && spriteBit;
            screenBuffer[(startY*64) + startX] = screenBit ^ spriteBit;

            startX++;
        }

        logger.info("Finished drawing sprite, " + (erased ? "erased" : "did not erase") + " bit");
        return erased;
    }

    public void update() {

        Graphics2D g2d = (Graphics2D) panel.getGraphics();

        for(int i=0; i<64*32; i++) {
            int row = i / 64;
            int col = i % 64;
            g2d.setColor(screenBuffer[i] ? Color.RED : Color.BLACK);
            g2d.fillRect(col*10, row*10, 10, 10);
        }

    }

    public void printDisplaySummary() {
        StringBuilder builder = new StringBuilder();
        for(int i=0; i<64*32; i++) {
            builder.append(screenBuffer[i] ? "*" : "_");
            builder.append(((i+1) % 64 == 0) ? "\n" : "");
        }
        logger.info("VRAM: \n" + builder);
    }

}
