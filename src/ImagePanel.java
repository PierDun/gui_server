import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class ImagePanel extends JPanel{

    private BufferedImage image;

    ImagePanel(String state) {
        try {
            String PATH = "C:\\Users\\User\\Desktop\\gui_server\\extensions\\";

            switch(state){
                case "OK" :
                    image = ImageIO.read(new File(PATH + "OK.png"));
                    break;
                case "ERROR" :
                    image = ImageIO.read(new File(PATH + "ERROR.png"));
                    break;
                default:
                    break;
            }

            } catch (IOException ex) {
            System.out.println("IMAGE NOT FOUND");
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0,70,70 ,this);
    }

}
