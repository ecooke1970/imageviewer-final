import java.awt.Color;

/**
 * Class RotateClockwise turns the image 90 degrees to the right:
 *
 * @author Erik Cooke
 * @version 2019.11.18
 */
public class RotateClockwise extends Filter
{
    private OFImage original;
    
    /**
     * Constructor for objects of class RotateClockwise
     * @param name Name of this filter
     */
    public RotateClockwise(String name)
    {
        super(name);
    }

    /**
     * Apply this filter to an image.
     *
     * @param  image The image to apply the filter to.
     */
    public void apply(OFImage image)
    {
        original = new OFImage(image.getHeight(), image.getWidth());
        int width = image.getWidth();
        int height = image.getHeight();        
        for(int y = 0; y < height / 2; y++)
        {
            for(int x = 0; x < width / 2; x++)
            {
                original.setPixel(width - y, x, image.getPixel(x, y));
            }
        }
        image = original;
    }
}
