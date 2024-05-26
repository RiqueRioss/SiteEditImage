package funcoes;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

public class Edicao {
	
	public void salvarImagem(BufferedImage img, File arq) throws IOException {
        ImageIO.write(img, "png", arq);
    }
	
	public static int[] obterVizinhanca(BufferedImage imagemEntrada, int w, int h, int tamanho) {
	    int[] vizinhanca = new int[tamanho * tamanho];
	    int count = 0;
	    int metade = tamanho / 2;

	    for (int i = -metade; i <= metade; i++) {
	        for (int j = -metade; j <= metade; j++) {
	            int x = w + j;
	            int y = h + i;
	            // Check if the coordinates are within the image bounds
	            if (x >= 0 && x < imagemEntrada.getWidth() && y >= 0 && y < imagemEntrada.getHeight()) {
	                Color cor = new Color(imagemEntrada.getRGB(x, y));
	                int valorPixel = cor.getRed();
	                vizinhanca[count] = valorPixel;
	            } else {
	                // Handle out-of-bounds case, e.g., assign a default value or a special marker
	                vizinhanca[count] = 0; // or any other value that makes sense in your context
	            }
	            count++;
	        }
	    }
	    return vizinhanca;
	}

	
	private static boolean verificaPixelBordas(int h, int w, int height, int width, int tamanho) {
        int metade = tamanho/2;
        return h < metade-1 || w < metade-1 || h > height - metade || w > width - metade;
    }

	public static int validaLimitesRGB(int valor, int acrescimo) {
        int soma = valor + acrescimo;
        if (soma + acrescimo > 255) soma = 255;
        else if (soma + acrescimo < 0) soma = 0;
        return soma;
    }
	
	public static void mudarCinza (File arquivo) throws IOException{
		
		BufferedImage img = ImageIO.read(arquivo);
		
        int width = img.getWidth();
        int height = img.getHeight();

        BufferedImage imgSaida = new BufferedImage(
                width,
                height,
                BufferedImage.TYPE_INT_BGR);

        for(int h=0; h < height; h++){
            for(int w=0; w < width; w++){
                Color cor = new Color(img.getRGB(w, h));
                int red = cor.getRed();
                int green = cor.getGreen();
                int blue = cor.getBlue();
                int media = (red + green + blue) / 3;
                Color novaCor = new Color(media, media, media);
                imgSaida.setRGB(w, h, novaCor.getRGB());
            }
        }
        
        ImageIO.write(imgSaida, "png", arquivo);
    }
	
	public static BufferedImage mudarNegativo (BufferedImage img){
        int width = img.getWidth();
        int height = img.getHeight();

        BufferedImage imgSaida = new BufferedImage(
                width,
                height,
                BufferedImage.TYPE_INT_BGR);

        for(int h=0; h < height; h++){
            for(int w=0; w < width; w++){
                Color cor = new Color(img.getRGB(w, h));
                int red = cor.getRed();
                int green = cor.getGreen();
                int blue = cor.getBlue();
                Color novaCor = new Color(255-red, 255-green, 255-blue);
                imgSaida.setRGB(w, h, novaCor.getRGB());
            }
        }
        return imgSaida;
    }
	
	public static void marcaDagua (File arquivo, File marcaD) throws IOException{
		
		BufferedImage img = ImageIO.read(arquivo);
		BufferedImage marca = ImageIO.read(marcaD);
		
        int width = img.getWidth();
        int height = img.getHeight();

        BufferedImage imgSaida = new BufferedImage(
                width,
                height,
                BufferedImage.TYPE_INT_BGR);
        
        int marcaW = marca.getWidth();
        int marcaH = marca.getHeight();

        for(int h=0; h < height; h++){
            for(int w=0; w < width; w++){
            	
            	if (h < marcaH && w < marcaW ) {
            		Color corMarca = new Color(marca.getRGB(w, h));
					imgSaida.setRGB(w, h, corMarca.getRGB());
					continue;
				}
            	
                Color cor = new Color(img.getRGB(w, h));
                int red = cor.getRed();
                int green = cor.getGreen();
                int blue = cor.getBlue();
                Color novaCor = new Color(red, green, blue);
                imgSaida.setRGB(w, h, novaCor.getRGB());
            }
        }
        
        ImageIO.write(imgSaida, "png", arquivo);
    }
	
	public static void alteraTamanho(File arquivo, int width, int height) throws IOException{
		
		BufferedImage imagem = ImageIO.read(arquivo);
		
        BufferedImage imgsaida = new BufferedImage(width, height, imagem.getType());

        int maxH = imagem.getHeight();
        int maxW = imagem.getWidth();

        int espacoH = maxH / height;
        int espacoW = maxW / width;

        for(int h=0; h < height; h++){
            for(int w=0; w < width; w++){

                if (espacoW*w <= imagem.getWidth() || espacoH*h <= imagem.getHeight()){
                    Color cor = new Color(imagem.getRGB(espacoW * w, espacoH * h));
                    imgsaida.setRGB(w, h, cor.getRGB());
                    continue;
                }
                if (espacoW*w > imagem.getWidth() || espacoH*h > imagem.getHeight()){
                    Color cor = new Color(imagem.getRGB(maxW, maxH));
                    imgsaida.setRGB(w, h, cor.getRGB());
                    continue;
                }
            }
        }
        
        ImageIO.write(imgsaida, "png", arquivo);
    }
	
	public static void negativo(File arquivo) throws IOException {
		
		BufferedImage img = ImageIO.read(arquivo);
		
        int width = img.getWidth();
        int height = img.getHeight();

        BufferedImage imgSaida = new BufferedImage(
                width,
                height,
                BufferedImage.TYPE_INT_BGR);

        for(int h=0; h < height; h++){
            for(int w=0; w < width; w++){
                Color cor = new Color(img.getRGB(w, h));
                int red = cor.getRed();
                int green = cor.getGreen();
                int blue = cor.getBlue();
                Color novaCor = new Color(255-red, 255-green, 255-blue);
                imgSaida.setRGB(w, h, novaCor.getRGB());
            }
        }
        
        ImageIO.write(imgSaida, "png", arquivo);
	}
	
	public static void binariza(File arquivo, int limiar) throws IOException {
		
		if (limiar < 1 || limiar > 254)
            throw new IllegalArgumentException("Limiar inválido");
		
		BufferedImage img = ImageIO.read(arquivo);
		
        int width = img.getWidth();
        int height = img.getHeight();

        BufferedImage imgSaida = new BufferedImage(
                width,
                height,
                BufferedImage.TYPE_INT_BGR);

        for(int h=0; h < height; h++){
            for(int w=0; w < width; w++){
                Color cor = new Color(img.getRGB(w, h));
                int red = cor.getRed();
                int green = cor.getGreen();
                int blue = cor.getBlue();
                int media = (red + green + blue) / 3;

                Color novaCor;
                if(limiar > media){
                    novaCor = new Color(255,255,255);
                }
                else {
                    novaCor = new Color(0,0,0);
                }
                imgSaida.setRGB(w, h, novaCor.getRGB());
            }
        }
        
        ImageIO.write(imgSaida, "png", arquivo);
	}
	
	public static void removeRuido(File arquivo) throws IOException {
		
		BufferedImage img = ImageIO.read(arquivo);
		
        int width = img.getWidth();
        int height = img.getHeight();

        BufferedImage imgSaida = new BufferedImage(
                width,
                height,
                BufferedImage.TYPE_INT_BGR);
        
        int tamVizinhanca = 3;

        for(int h=0; h < height; h++){
            for(int w=0; w < width; w++){
                if (verificaPixelBordas(h, w, height, width, tamVizinhanca)) {
                    int rgb = img.getRGB(w,h);
                    imgSaida.setRGB(w, h, rgb);
                    continue;
                }

                int[] vizinhanca = obterVizinhanca(img, w, h, tamVizinhanca);

                Arrays.sort(vizinhanca);
                int mediana = vizinhanca[tamVizinhanca/2];

                Color novaCor = new Color(mediana, mediana, mediana);
                imgSaida.setRGB(w, h, novaCor.getRGB());
            }
        }
        
        ImageIO.write(imgSaida, "png", arquivo);
	}
	
	public static void adicionaBrilho(File arquivo, int aditivo) throws IOException {
		BufferedImage img = ImageIO.read(arquivo);
		
        int width = img.getWidth();
        int height = img.getHeight();

        BufferedImage imgSaida = new BufferedImage(
                width,
                height,
                BufferedImage.TYPE_INT_BGR);

        for(int h=0; h < height; h++){
            for(int w=0; w < width; w++){
                Color cor = new Color(img.getRGB(w, h));

                int red = cor.getRed() + aditivo;
                red = validaLimitesRGB(red,0);

                int green = cor.getGreen() + aditivo;
                green = validaLimitesRGB(green,0);

                int blue = cor.getBlue() + aditivo;
                blue = validaLimitesRGB(blue,0);

                Color novaCor = new Color(red,green,blue);
                imgSaida.setRGB(w, h, novaCor.getRGB());

            }
        }
        
        ImageIO.write(imgSaida, "png", arquivo);
	}
}

