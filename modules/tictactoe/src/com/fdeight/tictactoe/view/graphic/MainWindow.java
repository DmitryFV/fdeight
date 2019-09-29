package com.fdeight.tictactoe.view.graphic;

import com.fdeight.tictactoe.model.info.TTTInfo;
import com.fdeight.tictactoe.model.info.TTTInfo02;
import com.fdeight.tictactoe.view.TTTView02;
import com.fdeight.utils.DataByName;
import com.fdeight.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.function.Consumer;

public class MainWindow extends JFrame implements KeyListener, MouseListener {

    /**
     * Элемент поля.
     */
    private enum Item {
        EMPTY,
        CROSS,
        ZERO;

        public static Item convert(final int formatted) {
            Utils.checkArgument(formatted, 0, values().length - 1, "convert(), formatted");
            return values()[formatted];
        }
    }

    /**
     * Режим.
     */
    private enum Mode {
        READY,
        PLAY,
        END_OF_GAME,
    }

    private enum Command {
        START_OF_GAME(KeyEvent.VK_1, "1", "Start of game", true, MainWindow::startOfGame),
        EXIT(KeyEvent.VK_2, "2", "Exit", IS_FULL_SCREEN, MainWindow::exit),
        ;

        private final int keyCode;
        private final String strKeyCode;
        private final String title;
        private final boolean isPresented;
        private final Consumer<MainWindow> action;

        Command(final int keyCode, final String strKeyCode, final String title, final boolean isPresented,
                final Consumer<MainWindow> action) {
            this.keyCode = keyCode;
            this.strKeyCode = strKeyCode;
            this.title = title;
            this.isPresented = isPresented;
            this.action = action;
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Использовать ли режим полного экрана.
     */
    private static final boolean IS_FULL_SCREEN = /*Math.random() < 0.5*/ false;
    /**
     * Максимальный размер окна.
     */
    private static final Dimension MAIN_WINDOW_MAX_SIZE = new Dimension(700, 500);
    /**
     * Начальная координата по оси Y.
     */
    private static final int TOP = 50;
    /**
     * Зазор между элементами.
     */
    private static final int GAP = 30;
    /**
     * Размер рамки.
     */
    private static final int BORDER = 5;
    /**
     * Минимальный размер ячейки игрового поля.
     */
    private static final int MIN_CELL_SIZE = 40;
    /**
     * Основные команды.
     */
    private static final Command[] MAIN_COMMANDS = new Command[]{Command.START_OF_GAME, Command.EXIT};
    /**
     * Команда выхода.
     */
    private static final Command[] EXIT_COMMAND = new Command[]{Command.EXIT};

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Представление.
     */
    private final TTTView02 view;
    /**
     * Режим.
     */
    private Mode mode;
    /**
     * Максимальный размер окна.
     */
    private final Dimension mainWindowSize;
    /**
     * Размер ячейки игрового поля.
     */
    private int cellSize;
    /**
     * Положение верхней части поля по вертикали.
     */
    private int topField;
    /**
     * Информация о состоянии игры.
     */
    private TTTInfo info;
    /**
     * Информация о состоянии окончившейся игры.
     */
    private TTTInfo enfOfGameInfo;
    /**
     * Информация о размере окончившейся игры.
     */
    private int enfOfGameSize;
    /**
     * Действие игрока-человека.
     */
    private volatile TTTInfo action;

    //------------------------------------------------------------------------------------------------------------------

    public MainWindow(final String title, final TTTView02 view) throws HeadlessException {
        super(title);
        this.view = view;
        addKeyListener(this);
        addMouseListener(this);
        final GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        if (IS_FULL_SCREEN) {
            mainWindowSize = createFullScreen(gd);
        } else {
            mainWindowSize = createMainWindow(gd);
        }
        mode = Mode.READY;
        action = null;
    }

    private Dimension createFullScreen(final GraphicsDevice gd) {
        if (!gd.isFullScreenSupported()) {
            throw new UnsupportedOperationException("Full screen is not supported");
        }
        setUndecorated(true);
        gd.setFullScreenWindow(this);
        final DisplayMode displayMode = gd.getDisplayMode();
        return new Dimension(displayMode.getWidth(), displayMode.getHeight());
    }

    private Dimension createMainWindow(final GraphicsDevice gd) {
        final DisplayMode displayMode = gd.getDisplayMode();
        final int width = Math.min(MAIN_WINDOW_MAX_SIZE.width, displayMode.getWidth() / 4 * 3);
        final int height = Math.min(MAIN_WINDOW_MAX_SIZE.height, displayMode.getHeight() / 4 * 3);
        setSize(width, height);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        return new Dimension(width, height);
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public void keyTyped(final KeyEvent e) {
    }

    @Override
    public void keyPressed(final KeyEvent e) {
    }

    @Override
    public void keyReleased(final KeyEvent e) {
        processCommand(e.getKeyCode());
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public void mouseClicked(final MouseEvent e) {
    }

    @Override
    public void mousePressed(final MouseEvent e) {
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
        processMouseAction(e);
    }

    @Override
    public void mouseEntered(final MouseEvent e) {
    }

    @Override
    public void mouseExited(final MouseEvent e) {
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public void paint(final Graphics g) {
        super.paint(g);
        switch (mode) {
            case READY:
                paintReady(g);
                break;
            case PLAY:
                paintPlay(g, info, view.getSize(), EXIT_COMMAND);
                break;
            case END_OF_GAME:
                paintPlay(g, enfOfGameInfo, enfOfGameSize, MAIN_COMMANDS);
                break;
            default:
                throw new IllegalStateException(String.format("Unsupported mode: %s", mode));
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    private void paintReady(final Graphics g) {
        printRequest(g, MAIN_COMMANDS);
    }

    private void paintPlay(final Graphics g, final TTTInfo info, final int size, final Command[] commands) {
        final int countPresentedCommand = printRequest(g, commands);
        topField = TOP + (countPresentedCommand - 1) * GAP;
        drawField(g, info, size);
    }

    /**
     * Вывести запрос (доступые команды).
     *
     * @param g контекст графики.
     * @param commands команды
     * @return количество выведенных строк.
     */
    private int printRequest(final Graphics g, final Command[] commands) {
        final String state = String.format("[%s]", info == null ? "empty" : info.getState());
        int countPresentedCommand = 0;
        for (int i = 0; i < commands.length; i++) {
            final Command command = commands[i];
            if (!command.isPresented) {
                continue;
            }
            if (countPresentedCommand == 0) {
                g.drawString(String.format("Enter command, mode: %s, state: %s", mode, state), GAP, TOP);
                countPresentedCommand++;
            }
            g.drawString(String.format("%s: %s", command.title, command.strKeyCode), GAP, TOP + (i + 1) * GAP);
            countPresentedCommand++;
        }
        if (countPresentedCommand == 0) {
            g.drawString(String.format("Mode: %s, state: %s", mode, state), GAP, TOP);
            countPresentedCommand++;
        }
        final String s = "com.fdeight.utils.Utils";
        g.drawString(String.format("%s", DataByName.getValueByReflection(s)),
                GAP, TOP + countPresentedCommand * GAP);
        countPresentedCommand++;
        final String s1 = DataByName.getComString() + ".fdeight.utils." + DataByName.class.getSimpleName();
        g.drawString(String.format("%s", DataByName.getValueByReflection(s1)),
                GAP, TOP + countPresentedCommand * GAP);
        countPresentedCommand++;
        return countPresentedCommand;
    }

    private void processCommand(final int keyCode) {
        switch (mode) {
            case READY:
            case END_OF_GAME:
                processCommand(keyCode, MAIN_COMMANDS);
                break;
            case PLAY:
                processCommand(keyCode, EXIT_COMMAND);
                break;
            default:
                throw new IllegalStateException(String.format("Unsupported mode: %s", mode));
        }
    }

    private void processCommand(final int keyCode, final Command[] commands) {
        for (final Command command : commands) {
            if (!command.isPresented) {
                continue;
            }
            if (keyCode == command.keyCode) {
                command.action.accept(this);
            }
        }
    }

    private void processMouseAction(final MouseEvent e) {
        if (mode != Mode.PLAY) {
            return;
        }
        if (action != null) {
            return;
        }
        final int x = e.getX() - GAP;
        final int y = e.getY() - GAP - topField;
        if (x % cellSize == 0 || y % cellSize == 0) { // значение на линии, не однозначно
            return;
        }
        final int col = x / cellSize;
        final int row = y / cellSize;
        final int size = view.getSize();
        if (row < 0 || row >= size || col < 0 || col >= size) {
            return;
        }
        final TTTInfo lAction = new TTTInfo02(String.format("%d %d", row + 1, col + 1));
        synchronized (this) {
            action = lAction;
        }
    }

    private void startOfGame() {
        info = new TTTInfo02("");
        enfOfGameInfo = null;
        enfOfGameSize = 0;
        mode = Mode.PLAY;
        cellSize = computeCellSize();
        action = null;
        final int size = 3 + (int) Math.round(Math.random() * 4);
        final Runnable runnable = () -> view.startOfGame(size);
        final Thread thread = new Thread(runnable);
        thread.start();
    }

    private void exit() {
        setVisible(false);
        dispose();
    }

    private int computeCellSize() {
        if (mode != Mode.PLAY) {
            throw new IllegalStateException(String.format("computeCellSize(), unsupported mode != %s: %s",
                    Mode.PLAY, mode));
        }
        return MIN_CELL_SIZE;
    }

    public void setInfo(final TTTInfo info) {
        this.info = info;
        repaintMode();
    }

    private void drawField(final Graphics g, final TTTInfo info, final int size) {
        if (mode != Mode.PLAY && mode != Mode.END_OF_GAME) {
            throw new IllegalStateException(String.format("drawField(), unsupported mode: %s", mode));
        }
        if (info == null) {
            throw new IllegalArgumentException("drawField(), info is null");
        }
        g.drawRect(GAP - BORDER, topField + GAP - BORDER,
                size * cellSize + 2 * BORDER, size * cellSize + 2 * BORDER);

        final int[] field = info.parseField(size);
        int index = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                drawCell(g, topField, i, j, field[index]);
                index++;
            }
        }
    }

    private void drawCell(final Graphics g, final int top, final int row, final int col, final int cell) {
        g.drawRect(GAP + col * cellSize, top + GAP + row * cellSize, cellSize, cellSize);
        final Item item = Item.convert(cell);
        switch (item) {
            case EMPTY:
                break;
            case CROSS:
                g.drawLine(GAP + col * cellSize + BORDER, top + GAP + row * cellSize + BORDER,
                        GAP + (col + 1) * cellSize - BORDER, top + GAP + (row + 1) * cellSize - BORDER);
                g.drawLine(GAP + col * cellSize + BORDER, top + GAP + (row +1) * cellSize - BORDER,
                        GAP + (col + 1) * cellSize - BORDER, top + GAP + row * cellSize + BORDER);
                break;
            case ZERO:
                g.drawOval(GAP + col * cellSize + BORDER, top + GAP + row * cellSize + BORDER,
                        cellSize - 2 * BORDER, cellSize - 2 * BORDER);
                break;
        }
    }

    public TTTInfo getAction(final int playerId) {
        action = null;
        final Thread thread = new Thread(this::waitForAction);
        thread.start();
        try {
            thread.join();
        } catch (final InterruptedException ignored) {
        }
        return action;
    }

    private void waitForAction() {
        while (true) {
            synchronized (this) {
                if (action != null) {
                    return;
                }
            }
            try {
                Thread.sleep(100);
            } catch (final InterruptedException ignored) {
            }
        }
    }

    public void setAction(final int playerId, final TTTInfo action) {
    }

    public void endOfGame(final String state) {
        mode = Mode.END_OF_GAME;
        enfOfGameInfo = this.info;
        enfOfGameSize = view.getSize();
        setInfo(new TTTInfo02(String.format("End of game: %s", state)));

        //repeatedTests(state);
    }

    @SuppressWarnings("unused")
    private void repeatedTests(final String state) {
        if (state.startsWith("Cross")) {
            throw new RuntimeException("Error");
        }

        try {
            Thread.sleep(1000);
        } catch (final InterruptedException ignored) {
        }

        startOfGame();
    }

    private void repaintMode() {
        if (SwingUtilities.isEventDispatchThread()) {
            repaint();
        } else {
            SwingUtilities.invokeLater(this::repaintMode);
        }
    }
}
