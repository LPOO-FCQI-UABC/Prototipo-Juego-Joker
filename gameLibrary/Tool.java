package gameLibrary;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class Tool extends Element{

    private String type;

    public Tool(int x, int y, String imageType) {
        // load the assets
        super(x,y,imageType);
        this.type=imageType;
    }

    public String getType() {
        return type;
    }
}

