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

@WebServlet("/tamanho")
@MultipartConfig
public class TamanhoServlet extends HttpServlet {
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
        String widthSTR = request.getParameter("width");
        String heightSTR = request.getParameter("height");
        int width = 0;
        int height = 0;
        
        try {
            width = Integer.parseInt(widthSTR);
            height = Integer.parseInt(heightSTR);
        } catch (NumberFormatException e) {
            // Lidar com a exceção, caso os valores não sejam números inteiros válidos
            e.printStackTrace();
            // Você pode definir uma mensagem de erro ou tomar outra ação apropriada
        }

        Edicao.alteraTamanho(imageFile, width, height);

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