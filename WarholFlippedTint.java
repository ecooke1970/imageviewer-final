import java.awt.Color;

/**
 * Class WarholFlippedTint creates 4 quarter sized images of the original in the format:
 * Original                       / Red Tint Mirrored Horizontally
 * Green Tint Mirrored Vertically / Blue Tint Mirrored Horiz and Vert
 *
 * @author Erik Cooke
 * @version 2019.11.18
 */
public class WarholFlippedTint extends Filter
{
    private OFImage original;
    
    /**
     * Constructor for objects of class WarholFlippedTint
     * @param name Name of this filter
     */
    public WarholFlippedTint(String name)
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
                // Copies original image at quarter size
                image.setPixel(x, y, original.getPixel(x * 2, y * 2));
                // Copies original image at quarter size, Red Channel, flipped horizontally
                image.setPixel(width - x - 1, y, new Color(pix.getRed(), 0, 0));
                // Copies original image at quarter size, Green Channel, flipped vertically
                image.setPixel(x, height - y - 1, new Color(0, pix.getGreen(), 0));
                // Copies original image at quarter size, Blue Channel, flipped horizontally & vertically
                image.setPixel(width - x - 1, height - y - 1, new Color(0, 0, pix.getBlue()));
            }
        }
    }
}
