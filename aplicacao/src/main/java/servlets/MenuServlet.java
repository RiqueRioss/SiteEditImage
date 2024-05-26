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

@WebServlet("/menu")
@MultipartConfig
public class MenuServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String imagePath = request.getParameter("imagePath");

        if (imagePath != null && !imagePath.isEmpty()) {
            // The imagePath is provided via the form's hidden field
            // Further processing can be done here using the provided imagePath
            
            request.setAttribute("imagePath", imagePath);
            request.getRequestDispatcher("Menu.jsp").forward(request, response);
        } else {
            // Handle file upload as before
            Part filePart = request.getPart("image");

            Path tempDir = Files.createTempDirectory("ocr-temp");
            File imageFile = new File(tempDir.toFile(), "imageFile.png");

            try (InputStream inputStream = filePart.getInputStream()) {
                Files.copy(inputStream, imageFile.toPath());
            } catch (IOException e) {
                response.getWriter().write("Erro ao copiar o arquivo: " + e.getMessage());
                return;
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
}
