package gameLibrary;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;

public abstract class Element {
    // image that represents the coin's position on the board
    private BufferedImage image;

    // current position of the coin on the board grid
    public Point pos;
    private int x;
    private int y;


    public Element(int x, int y,String imageElement) {
        // load the assets
        loadImage(imageElement);
        this.x=x;
        this.y=y;

        // initialize the state
        pos = new Point(x, y);
    }
    public Element(int x, int y) {
        // load the assets

        this.x=x;
        this.y=y;

        // initialize the state
        pos = new Point(x, y);
    }


    public void loadImage(String imageElement) {
        try {
            // you can use just the filename if the image file is in your
            // project folder, otherwise you need to provide the file path.
            image = ImageIO.read(new File("images/"+imageElement));
        } catch (IOException exc) {
            System.out.println("Error opening image file: " + exc.getMessage());
        }
    }

    public void draw(Graphics g, ImageObserver observer) {
        // with the Point class, note that pos.getX() returns a double, but
        // pos.x reliably returns an int. https://stackoverflow.com/a/30220114/4655368
        // this is also where we translate board grid position into a canvas pixel
        // position by multiplying by the tile size.
        g.drawImage(
                image,
                pos.x * Board.tileSize,
                pos.y * Board.tileSize,
                observer
        );
    }

    public Point getPos() {
        return pos;
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }

}
