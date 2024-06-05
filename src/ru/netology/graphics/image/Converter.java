package ru.netology.graphics.image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.net.URL;

public class Converter implements TextGraphicsConverter {

    /**
     * Максимальная ширина результирующего изображения в "текстовых пикселях"
     */
    private int maxWidth;

    /**
     * Максимальная высота результирующего изображения в "текстовых пикселях"
     */
    private int maxHeight;

    /**
     * Максимально допустимое соотношение сторон исходного изображения
     */
    private double maxRatio;

    private TextColorSchema textColorSchema;

    /**
     * Конвертация изображения, переданного как урл, в текстовую графику.
     *
     * @param url урл изображения
     * @return текст, представляющий собой текстовую графику переданного изображения
     * @throws IOException
     * @throws BadImageSizeException Если соотношение сторон изображения слишком большое
     */
    @Override
    public String convert(String url) throws IOException, BadImageSizeException {
        BufferedImage img = ImageIO.read(new URL(url));
        int width = img.getWidth();
        int height = img.getHeight();

        if (maxRatio > 0) {
            double ratio = (double) width / height;
            if (maxRatio < ratio) {
                throw new BadImageSizeException(ratio, maxRatio);
            }
        }

        int newWidth = width;
        int newHeight = height;

        if (maxWidth > 0) {
            if (maxWidth < width) {
                double ratioOfWidths = (double) width / maxWidth;
                newWidth = (int) (width / ratioOfWidths);
                newHeight = (int) (height / ratioOfWidths);
            }
        }
        if (maxHeight > 0) {
            if (maxHeight < height) {
                double ratioOfHeight = (double) height / maxHeight;
                newWidth = (int) (width / ratioOfHeight);
                newHeight = (int) (height / ratioOfHeight);
            }
        }
        Image scaledImage = img.getScaledInstance(newWidth, newHeight, BufferedImage.SCALE_SMOOTH);
        BufferedImage bwImg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D graphics = bwImg.createGraphics();
        graphics.drawImage(scaledImage, 0, 0, null);
        WritableRaster bwRaster = bwImg.getRaster();
        TextColorSchema schema = new Schema();
        char[][] array = new char[newWidth][newHeight];
        int[] ar = new int[3];

        for (int w = 0; w < newWidth; w++) {
            for (int h = 0; h < newHeight; h++) {
                int color = bwRaster.getPixel(w, h, ar)[0];
                char c = schema.convert(color);
                array[w][h] = c;
            }
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                builder.append(array[i][j]);
                builder.append(array[i][j]);
                builder.append(array[i][j]);
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    /**
     * Устанавливает максимальную ширину результирующего изображения в "текстовых пикселях".
     * Если исходное изображение имеет характеристики, превышающие максимальные, оно сжимается
     * с сохранением пропорций.
     *
     * @param width максимальная ширина текстовых картинок
     */
    @Override
    public void setMaxWidth(int width) {
        this.maxWidth = width;
    }

    /**
     * Устанавливает максимальную высоту результирующего изображения в "текстовых пикселях".
     * Если исходное изображение имеет характеристики, превышающие максимальные, оно сжимается
     * с сохранением пропорций.
     *
     * @param height максимальная высоту текстовых картинок
     */
    @Override
    public void setMaxHeight(int height) {
        this.maxHeight = height;
    }

    /**
     * Устанавливает максимально допустимое соотношение сторон исходного изображения.
     * Если исходное изображение имеет характеристики, превышающие максимальные,
     * при конвертации выбрасывается исключение.
     *
     * @param maxRatio
     */
    @Override
    public void setMaxRatio(double maxRatio) {
        this.maxRatio = maxRatio;
    }

    /**
     * Устанавливает символьную цветовую схему, которую будет использовать конвертер
     *
     * @param schema
     */
    @Override
    public void setTextColorSchema(TextColorSchema schema) {
        this.textColorSchema = schema;
    }
}


