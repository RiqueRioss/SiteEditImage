package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import funcoes.Edicao;

@WebServlet("/filtro")
@MultipartConfig
public class FiltroServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Part filePart = request.getPart("image");

        Path tempDir = Files.createTempDirectory("ocr-temp");
        File imageFile = new File(tempDir.toFile(), "imageFile.png");

        try (InputStream inputStream = filePart.getInputStream()) {
            Files.copy(inputStream, imageFile.toPath());
        } catch (IOException e) {
            response.getWriter().write("Erro ao copiar o arquivo: " + e.getMessage());
            return;
        }
        
        String opcaoStr = request.getParameter("opcao");

        int opcao = 0;

        // Converter o valor para inteiro
        try {
            opcao = Integer.parseInt(opcaoStr);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            // Lidar com a exceção, caso o valor não seja um número inteiro válido
            // Você pode definir uma mensagem de erro ou tomar outra ação apropriada
        }
        
        switch (opcao) {
		case 1: Edicao.mudarCinza(imageFile);
			break;
			
		case 2: Edicao.negativo(imageFile);
			break;
			
		case 3: 
			String limiarString = request.getParameter("limiar");
			int limiar = 0;
			try {
	            limiar = Integer.parseInt(limiarString);
	        } catch (NumberFormatException e) {
	            e.printStackTrace();
	        }
			Edicao.binariza(imageFile, limiar);
			break;
			
		case 4:
			Edicao.removeRuido(imageFile);
			break;
			
		case 5:
			String brilhoString = request.getParameter("brilho");
			int brilho = 0;
			try {
	            brilho = Integer.parseInt(brilhoString);
	        } catch (NumberFormatException e) {
	            e.printStackTrace();
	        }
			Edicao.adicionaBrilho(imageFile, brilho);
			break;

		default:
			break;
		}

        // Save the processed image in a web-accessible location
        String uploadDir = getServletContext().getRealPath("/uploads");
        File uploadDirFile = new File(uploadDir);
        if (!uploadDirFile.exists()) {
            uploadDirFile.mkdirs();
        }
        File processedImageFile = new File(uploadDirFile, "processedImage.png");
        try {
            Files.copy(imageFile.toPath(), processedImageFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            response.getWriter().write("Erro ao salvar a imagem processada: " + e.getMessage());
            return;
        }

        request.setAttribute("imagePath", request.getContextPath() + "/uploads/processedImage.png");
        request.getRequestDispatcher("Menu.jsp").forward(request, response);

        Files.deleteIfExists(imageFile.toPath());
    }
}