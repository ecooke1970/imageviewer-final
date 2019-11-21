import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.border.*;

import java.io.File;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

/**
 * ImageViewer is the main class of the image viewer application. It builds and
 * displays the application GUI and initialises all other components.
 * 
 * To start the application, create an object of this class.
 * 
 * @author Erik Cooke
 * @version 2019.11.21
 * 
 * @author Michael Kölling and David J. Barnes.
 * @version 3.1
 */
public class ImageViewer
{
    // static fields:
    private static final String VERSION = "Version 3.1";
    private static JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));

    // fields:
    private JFrame frame;
    private ImagePanel imagePanel;
    private JLabel filenameLabel;
    private JLabel statusLabel;
    private JButton smallerButton;
    private JButton largerButton;
    private JButton undoButton;
    private JButton rotateRight;
    private JButton rotateLeft;
    private OFImage currentImage;
    private JMenuItem undoItem;
    private JMenuItem rotateRightMenu;
    private JMenuItem rotateLeftMenu;
    
    private List<Filter> filters;
    
    private Stack<OFImage> undoStack;
    
    /**
     * Create an ImageViewer and display its GUI on screen.
     */
    public ImageViewer()
    {
        currentImage = null;
        undoStack = new Stack<OFImage>();
        filters = createFilters();
        makeFrame();
    }


    // ---- implementation of menu functions ----
    
    /**
     * Open function: open a file chooser to select a new image file,
     * and then display the chosen image.
     */
    private void openFile()
    {
        int returnVal = fileChooser.showOpenDialog(frame);

        if(returnVal != JFileChooser.APPROVE_OPTION) {
            return;  // cancelled
        }
        File selectedFile = fileChooser.getSelectedFile();
        currentImage = ImageFileManager.loadImage(selectedFile);
        
        if(currentImage == null) {   // image file was not a valid image
            JOptionPane.showMessageDialog(frame,
                    "The file was not in a recognized image file format.",
                    "Image Load Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        imagePanel.setImage(currentImage);
        setButtonsEnabled(true);
        showFilename(selectedFile.getPath());
        showStatus("File loaded.");
        frame.pack();
    }

    /**
     * Close function: close the current image.
     */
    private void close()
    {
        currentImage = null;
        imagePanel.clearImage();
        showFilename(null);
        setButtonsEnabled(false);
        undoStack.clear();
        setUndoItemEnabled(false);
    }

    /**
     * Save As function: save the current image to a file.
     */
    private void saveAs()
    {
        if(currentImage != null) {
            int returnVal = fileChooser.showSaveDialog(frame);
    
            if(returnVal != JFileChooser.APPROVE_OPTION) {
                return;  // cancelled
            }
            File selectedFile = fileChooser.getSelectedFile();
            ImageFileManager.saveImage(currentImage, selectedFile);
            
            showFilename(selectedFile.getPath());
        }
    }

    /**
     * Quit function: quit the application.
     */
    private void quit()
    {
        System.exit(0);
    }

    /**
     * Apply a given filter to the current image.
     * 
     * @param filter   The filter object to be applied.
     */
    private void applyFilter(Filter filter)
    {
        if(currentImage != null) {
            // add currentImage to undoStack before making a change.
            OFImage tempImage = new OFImage(currentImage);
            undoStack.push(tempImage);
            setUndoItemEnabled(true);
            
            filter.apply(currentImage);
            frame.repaint();
            showStatus("Applied: " + filter.getName());
        }
        else {
            showStatus("No image loaded.");
        }
    }

    /**
     * 'About' function: show the 'about' box.
     */
    private void showAbout()
    {
        JOptionPane.showMessageDialog(frame, 
                    "ImageViewer\n" + VERSION,
                    "About ImageViewer", 
                    JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Make the current picture larger.
     */
    private void makeLarger()
    {
        if(currentImage != null) {
            // add currentImage to undoStack before making a change.
            undoStack.push(currentImage);
            setUndoItemEnabled(true);
            
            // create new image with double size
            int width = currentImage.getWidth();
            int height = currentImage.getHeight();
            OFImage newImage = new OFImage(width * 2, height * 2);

            // copy pixel data into new image
            for(int y = 0; y < height; y++) {
                for(int x = 0; x < width; x++) {
                    Color col = currentImage.getPixel(x, y);
                    newImage.setPixel(x * 2, y * 2, col);
                    newImage.setPixel(x * 2 + 1, y * 2, col);
                    newImage.setPixel(x * 2, y * 2 + 1, col);
                    newImage.setPixel(x * 2+1, y * 2 + 1, col);
                }
            }
            
            currentImage = newImage;
            imagePanel.setImage(currentImage);
            frame.pack();
        }
    }
    

    /**
     * Make the current picture smaller.
     */
    private void makeSmaller()
    {
        if(currentImage != null) {
            // add currentImage to undoStack before making a change.
            undoStack.push(currentImage);
            setUndoItemEnabled(true);
            
            // create new image with double size
            int width = currentImage.getWidth() / 2;
            int height = currentImage.getHeight() / 2;
            OFImage newImage = new OFImage(width, height);

            // copy pixel data into new image
            for(int y = 0; y < height; y++) {
                for(int x = 0; x < width; x++) {
                    newImage.setPixel(x, y, currentImage.getPixel(x * 2, y * 2));
                }
            }
            
            currentImage = newImage;
            imagePanel.setImage(currentImage);
            frame.pack();
        }
    }
    
    /**
     * Rotates current image 90 degrees to the right
     */
    private void rotateRight()
    {
        if(currentImage != null)
        {
            // add currentImage to undoStack before making a change.
            undoStack.push(currentImage);
            setUndoItemEnabled(true);
            
            int width = currentImage.getWidth();
            int height = currentImage.getHeight();
            OFImage original = new OFImage(height, width);
            for(int y = 0; y < height; y++)
            {
                for(int x = 0; x < width; x++)
                {
                    original.setPixel(height - 1 - y, x, currentImage.getPixel(x, y));
                }
            }
            currentImage = original;
            imagePanel.setImage(currentImage);
            frame.pack();
        }
    }
    
    /**
     * Rotates current image 90 degrees to the left.
     */
    private void rotateLeft()
    {
        if(currentImage != null)
        {
            // add currentImage to undoStack before making a change.
            undoStack.push(currentImage);
            setUndoItemEnabled(true);
            
            int width = currentImage.getWidth();
            int height = currentImage.getHeight();
            OFImage original = new OFImage(height, width);
            for(int y = 0; y < height; y++)
            {
                for(int x = 0; x < width; x++)
                {
                    original.setPixel(y, width - 1 - x, currentImage.getPixel(x, y));
                }
            }
            currentImage = original;
            imagePanel.setImage(currentImage);
            frame.pack();
        }
    }
        
    /**
     * Loads the previous image before the latest change.
     */
    private void undo()
    {
        if(!undoStack.empty())
        {
            currentImage = undoStack.pop();
            if(undoStack.empty())
            {
                setUndoItemEnabled(false);
            }
            imagePanel.setImage(currentImage);
            frame.pack();
        }
    }    
    
    // ---- support methods ----

    /**
     * Show the file name of the current image in the fils display label.
     * 'null' may be used as a parameter if no file is currently loaded.
     * 
     * @param filename  The file name to be displayed, or null for 'no file'.
     */
    private void showFilename(String filename)
    {
        if(filename == null) {
            filenameLabel.setText("No file displayed.");
        }
        else {
            filenameLabel.setText("File: " + filename);
        }
    }
    
    
    /**
     * Show a message in the status bar at the bottom of the screen.
     * @param text The status message.
     */
    private void showStatus(String text)
    {
        statusLabel.setText(text);
    }
    
    /**
     * Enable or disable all toolbar buttons.
     * 
     * @param status  'true' to enable the buttons, 'false' to disable.
     */
    private void setButtonsEnabled(boolean status)
    {
        smallerButton.setEnabled(status);
        largerButton.setEnabled(status);
        rotateRight.setEnabled(status);
        rotateLeft.setEnabled(status);
        rotateLeftMenu.setEnabled(status);
        rotateRightMenu.setEnabled(status);        
    }
    
    /**
     * Enable or disable the undo menu item
     * @param status 'true' to enable, 'false' to disable undo menu item.
     */
    private void setUndoItemEnabled(boolean status)
    {
        undoItem.setEnabled(status);
        undoButton.setEnabled(status);
    }
    
    
    /**
     * Create a list with all the known filters.
     * @return The list of filters.
     */
    private List<Filter> createFilters()
    {
        List<Filter> filterList = new ArrayList<>();
        filterList.add(new DarkerFilter("Darker"));
        filterList.add(new LighterFilter("Lighter"));
        filterList.add(new ThresholdFilter("Threshold"));
        filterList.add(new InvertFilter("Invert"));
        filterList.add(new SolarizeFilter("Solarize"));
        filterList.add(new SmoothFilter("Smooth"));
        filterList.add(new PixelizeFilter("Pixelize"));
        filterList.add(new MirrorFilter("Mirror"));
        filterList.add(new GrayScaleFilter("Grayscale"));
        filterList.add(new EdgeFilter("Edge Detection"));
        filterList.add(new FishEyeFilter("Fish Eye"));
        filterList.add(new RedChannel("Red Channel"));
        filterList.add(new GreenChannel("Green Channel"));
        filterList.add(new BlueChannel("Blue Channel"));
        filterList.add(new RedTint("Red Tint"));
        filterList.add(new GreenTint("Green Tint"));
        filterList.add(new BlueTint("Blue Tint"));
        filterList.add(new WarholChannel("Warhol Channel"));
        filterList.add(new WarholFlippedChannel("Warhol Flipped Channel"));
        filterList.add(new WarholTint("Warhol Tint"));
        filterList.add(new WarholFlippedTint("Warhol Flipped Tint"));
       
        return filterList;
    }
    
    // ---- Swing stuff to build the frame and all its components and menus ----
    
    /**
     * Create the Swing frame and its content.
     */
    private void makeFrame()
    {
        frame = new JFrame("ImageViewer");
        JPanel contentPane = (JPanel)frame.getContentPane();
        contentPane.setBorder(new EmptyBorder(12, 12, 12, 12));

        makeMenuBar(frame);
        
        // Specify the layout manager with nice spacing
        contentPane.setLayout(new BorderLayout(6, 6));
        
        // Create the image pane in the center
        imagePanel = new ImagePanel();
        imagePanel.setBorder(new EtchedBorder());
        contentPane.add(imagePanel, BorderLayout.CENTER);

        // Create two labels at top and bottom for the file name and status messages
        filenameLabel = new JLabel();
        contentPane.add(filenameLabel, BorderLayout.NORTH);

        statusLabel = new JLabel(VERSION);
        contentPane.add(statusLabel, BorderLayout.SOUTH);
        
        // Create the toolbar with the buttons
        JPanel toolbar = new JPanel();
        toolbar.setLayout(new GridLayout(0, 1));
        
        smallerButton = new JButton("Smaller");
        smallerButton.addActionListener(e -> makeSmaller());
        toolbar.add(smallerButton);
        
        largerButton = new JButton("Larger");
        largerButton.addActionListener(e -> makeLarger());
        toolbar.add(largerButton);
        
        rotateRight = new JButton("Rotate Right");
        rotateRight.addActionListener(e -> rotateRight());
        toolbar.add(rotateRight);
        
        rotateLeft = new JButton("Rotate Left");
        rotateLeft.addActionListener(e -> rotateLeft());
        toolbar.add(rotateLeft);
        
        undoButton = new JButton("Undo");
        undoButton.addActionListener(e -> undo());
        toolbar.add(undoButton);

        // Add toolbar into panel with flow layout for spacing
        JPanel flow = new JPanel();
        flow.add(toolbar);
        
        contentPane.add(flow, BorderLayout.WEST);
        
        // building is done - arrange the components      
        showFilename(null);
        setButtonsEnabled(false);
        setUndoItemEnabled(false);
        frame.pack();
        
        // place the frame at the center of the screen and show
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(d.width/2 - frame.getWidth()/2, d.height/2 - frame.getHeight()/2);
        frame.setVisible(true);
    }
    
    /**
     * Create the main frame's menu bar.
     * 
     * @param frame   The frame that the menu bar should be added to.
     */
    private void makeMenuBar(JFrame frame)
    {
        final int SHORTCUT_MASK =
            Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

        JMenuBar menubar = new JMenuBar();
        frame.setJMenuBar(menubar);
        
        JMenu menu;
        JMenuItem item;
        
        // create the File menu
        menu = new JMenu("File");
        menubar.add(menu);
        
        item = new JMenuItem("Open...");
            item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, SHORTCUT_MASK));
            item.addActionListener(e -> openFile());
        menu.add(item);

        item = new JMenuItem("Close");
            item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, SHORTCUT_MASK));
            item.addActionListener(e -> close());
        menu.add(item);
        menu.addSeparator();

        item = new JMenuItem("Save As...");
            item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, SHORTCUT_MASK));
            item.addActionListener(e -> saveAs());
        menu.add(item);
        menu.addSeparator();
        
        item = new JMenuItem("Quit");
            item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, SHORTCUT_MASK));
            item.addActionListener(e -> quit());
        menu.add(item);
        
        // create the Edit menu
        menu = new JMenu("Edit");
        menubar.add(menu);
        
        rotateRightMenu = new JMenuItem("Rotate Right");
            rotateRightMenu.addActionListener(e -> rotateRight());
        menu.add(rotateRightMenu);
        
        rotateLeftMenu = new JMenuItem("Rotate Left");
            rotateLeftMenu.addActionListener(e -> rotateLeft());
        menu.add(rotateLeftMenu);
        
        menu.addSeparator();
        undoItem = new JMenuItem("Undo");
            undoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, SHORTCUT_MASK));
            undoItem.addActionListener(e -> undo());
        menu.add(undoItem);

        // create the Filter menu
        menu = new JMenu("Filter");
        menubar.add(menu);
        
        for(Filter filter : filters) {
            item = new JMenuItem(filter.getName());
            item.addActionListener(e -> applyFilter(filter));
             menu.add(item);
         }

        // create the Help menu
        menu = new JMenu("Help");
        menubar.add(menu);
        
        item = new JMenuItem("About ImageViewer...");
            item.addActionListener(e -> showAbout());
        menu.add(item);

    }
}
