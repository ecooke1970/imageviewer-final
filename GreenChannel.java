import java.awt.Color;

/**
 * Write a description of class GreenChannel here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class GreenChannel extends Filter
{
    /**
     * Constructor for objects of class GreenChannel.
     * @param name The name of the filter.
     */
    public GreenChannel(String name)
    {
        super(name);
    }

    /**
     * Apply this filter to an image.
     *
     * @param  image The image to be changed by the filter.
     */
    public void apply(OFImage image)
    {
        int width = image.getWidth();
        int height = image.getHeight();
        for(int y = 0;y < height;y++)
        {
            for(int x = 0;x < width;x++)
            {
                Color pix = image.getPixel(x, y);
                image.setPixel(x, y, new Color(pix.getGreen(), pix.getGreen(), pix.getGreen()));
            }
        }
    }
}