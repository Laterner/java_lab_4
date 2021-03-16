import java.awt.*;
import javax.swing.*;
import java.awt.geom.Rectangle2D;
import java.awt.event.*;

public class FractalExplorer
{
    // Размер экрана в пикселях
    private int displaySize;

    private JImageDisplay display;

    private FractalGenerator fractal;

    private Rectangle2D.Double range;

    public FractalExplorer(int size)
    {
        // Задаём размер дисплея
        displaySize = size;

        // Инициализация FractalGenerator
        fractal = new Mandelbrot();
        // Задаём диапазон
        range = new Rectangle2D.Double();
        fractal.getInitialRange(range);
        // Создаём новый дисплей
        display = new JImageDisplay(displaySize, displaySize);
    }

    // Создание окна
    public void createAndShowGUI()
    {
        display.setLayout(new BorderLayout());
        JFrame frame = new JFrame("Fractal Explorer");

        frame.add(display, BorderLayout.CENTER);

        JButton resetButton = new JButton("Reset Display");

        ResetHandler handler = new ResetHandler();
        resetButton.addActionListener(handler);

        frame.add(resetButton, BorderLayout.SOUTH);

        MouseHandler click = new MouseHandler();
        display.addMouseListener(click);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
    }

    private void drawFractal()
    {
        // Смотрим каждый пиксель на дисплее
        for (int x=0; x<displaySize; x++)
        {
            for (int y=0; y<displaySize; y++)
            {
                double xCoord = fractal.getCoord(range.x, range.x + range.width, displaySize, x);
                double yCoord = fractal.getCoord(range.y, range.y + range.height, displaySize, y);

                int iteration = fractal.numIterations(xCoord, yCoord);

                if (iteration == -1){
                    display.drawPixel(x, y, 0);
                }
                else {
                    float hue = 0.7f + (float) iteration / 200f;
                    int rgbColor = Color.HSBtoRGB(hue, 1f, 1f);

                    // Окрашивает пиксель в выбранный цвет
                    display.drawPixel(x, y, rgbColor);
                }

            }
        }
        
        display.repaint();
    }
    // Внутренний класс для реализации конструктора ActionListener и сброса значений.
    private class ResetHandler implements ActionListener
    {
        // Функция сбрасывает диапазон до начального значения, заданного генератором, а затем рисует фрактал.
        public void actionPerformed(ActionEvent e)
        {
            fractal.getInitialRange(range);
            drawFractal();
        }
    }

    // Обработчик нажатия кновки мыши
    private class MouseHandler extends MouseAdapter
    {
        @Override
        public void mouseClicked(MouseEvent e)
        {
            // Перевод позиции мыши в Х координату
            int x = e.getX();

            double xCoord = fractal.getCoord(range.x, range.x + range.width, displaySize, x);

            int y = e.getY();
            double yCoord = fractal.getCoord(range.y, range.y + range.height, displaySize, y);

            fractal.recenterAndZoomRange(range, xCoord, yCoord, 0.5);

            drawFractal();
        }
    }

    // Точка входа
    public static void main(String[] args)
    {
        FractalExplorer displayExplorer = new FractalExplorer(600);
        displayExplorer.createAndShowGUI();
        displayExplorer.drawFractal();
    }
}