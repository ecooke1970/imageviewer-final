import java.awt.Color;

/**
 * Write a description of class Warhol here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Warhol extends Filter
{
    /**
     * Constructor for objects of class Warhol
     */
    public Warhol(String name)
    {
        super(name);
    }

    /**
     * Apply this filter to an image.
     *
     * @param  y  a sample parameter for a method
     * @return    the sum of x and y
     */
    public void apply(OFImage image)
    {
        int width = image.getWidth();
        int height = image.getHeight();
        OFImage newImage = image;
        for(int y = 0; y < height / 2; y++)
        {
            for(int x = 0; x < width / 2; x++)
            {
                Color pix = newImage.getPixel(x * 2, y * 2);
                image.setPixel(x, y, newImage.getPixel(x * 2, y * 2));
                image.setPixel(x + (width / 2), y, new Color(pix.getRed(), pix.getRed(), pix.getRed()));
            }
        }
    }
}
