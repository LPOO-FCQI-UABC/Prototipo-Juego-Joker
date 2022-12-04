package gameLibrary;

public class Plant extends Element{

    private String image;

    public Plant(int x, int y, String imagePlant) {
        // load the assets
        super(x,y,imagePlant);
        this.image=imagePlant;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
        super.loadImage(image);
    }

}