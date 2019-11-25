import java.awt.Color;

/**
 * Creates a blue tinted version of the image.
 *
 * @author (Erik Cooke)
 * @version (2019.11.18)
 */
public class BlueTint extends Filter
{
    /**
     * Constructor for objects of class BlueTint.
     * @param name The name of the filter.
     */
    public BlueTint(String name)
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
                image.setPixel(x, y, new Color(0, 0, pix.getBlue()));
            }
        }
    }
}
