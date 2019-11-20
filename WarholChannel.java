import java.awt.Color;

/**
 * Class WarholChannel creates 4 quarter sized images of the original in the format:
 * Original      / Red Channel
 * Green Channel / Blue Channel
 *
 * @author Erik Cooke
 * @version 2019.11.18
 */
public class WarholChannel extends Filter
{
    private OFImage original;
    
    /**
     * Constructor for objects of class WarholChannel
     * @param name Name of this filter
     */
    public WarholChannel(String name)
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
        original = new OFImage(image);
        int width = original.getWidth();
        int height = original.getHeight();        
        for(int y = 0; y < height / 2; y++)
        {
            for(int x = 0; x < width / 2; x++)
            {
                Color pix = original.getPixel(x * 2, y * 2);
                image.setPixel(x, y, original.getPixel(x * 2, y * 2));
                // Copies original image at quarter size
                image.setPixel(x + (width / 2), y, new Color(pix.getRed(), pix.getRed(), pix.getRed()));
                // Copies original image at quarter size using red channel
                image.setPixel(x, y + (height / 2), new Color(pix.getGreen(), pix.getGreen(), pix.getGreen()));
                // Copies original image at quarter size using green channel
                image.setPixel(x + (width / 2), y + (height / 2), new Color(pix.getBlue(), pix.getBlue(), pix.getBlue()));
                // Copies original image at quarter size using blue channel
            }
        }
    }
}
