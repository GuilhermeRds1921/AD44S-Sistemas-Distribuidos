import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class SobelSerial {

    // Aplica o filtro de Sobel manualmente em uma imagem em escala de cinza
    public static BufferedImage aplicarSobel(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        int[][] gx = {
            {-1, 0, 1},
            {-2, 0, 2},
            {-1, 0, 1}
        };

        int[][] gy = {
            {-1, -2, -1},
            {0,  0,  0},
            {1,  2,  1}
        };

        for (int y = 1; y < height-1; y++) {
            for (int x = 1; x < width-1; x++) {
                int sumX = 0;
                int sumY = 0;

                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        int pixel = img.getRGB(x+j, y+i) & 0xFF;
                        sumX += gx[i+1][j+1] * pixel;
                        sumY += gy[i+1][j+1] * pixel;
                    }
                }

                int magnitude = (int)Math.min(255, Math.sqrt(sumX*sumX + sumY*sumY));
                int rgb = (magnitude << 16) | (magnitude << 8) | magnitude;
                result.setRGB(x, y, rgb);
            }
        }
        return result;
    }

    public static void main(String[] args) throws Exception {
        String dir1 = "imagens1/Praia";
        String dir2 = "imagens2/Animais";

        long inicio = System.currentTimeMillis();

        // Processa todas as imagens em ambas as pastas
        processarPasta(dir1);
        processarPasta(dir2);

        long fim = System.currentTimeMillis();
        System.out.printf("Tempo total (Sobel Serial): %.2f segundos%n", (fim-inicio)/1000.0);
    }

    public static void processarPasta(String dirPath) throws Exception {
        File pasta = new File(dirPath);
        File[] arquivos = pasta.listFiles((d,name) -> name.toLowerCase().endsWith(".png") 
                                                    || name.toLowerCase().endsWith(".jpg")
                                                    || name.toLowerCase().endsWith(".jpeg"));
        if (arquivos == null) return;

        for (File imgFile : arquivos) {
            BufferedImage img = ImageIO.read(imgFile);
            if (img == null) continue;
            BufferedImage sobel = aplicarSobel(img);

            // Opcional: salvar a imagem processada
            File out = new File("saida/" + imgFile.getName());
            ImageIO.write(sobel, "png", out);
        }
    }
}
