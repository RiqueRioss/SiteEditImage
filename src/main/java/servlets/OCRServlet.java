package servlets;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

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
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

import funcoes.Edicao;

@WebServlet("/ocr")
@MultipartConfig
public class OCRServlet extends HttpServlet {
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

        ITesseract instance = new Tesseract();

        // Obtendo o caminho absoluto para o diretório tessdata dentro de resources
        URI uri = null;
		try {
			uri = OCRServlet.class.getClassLoader().getResource("tessdata").toURI();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        Path tessDataPath;
        if (uri.getScheme().equals("jar")) {
            try (FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap())) {
                tessDataPath = fileSystem.getPath("/tessdata");
            }
        } else {
            tessDataPath = Paths.get(uri);
        }

        instance.setDatapath(tessDataPath.toString());

        // Definindo o idioma para 'por' (Português)
        instance.setLanguage("eng");

        String result;
        try {
            result = instance.doOCR(imageFile);
        } catch (TesseractException e) {
            e.printStackTrace();
            response.getWriter().write("Erro ao processar imagem: " + e.getMessage());
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

        request.setAttribute("ocrResult", result);
        request.setAttribute("imagePath", request.getContextPath() + "/uploads/processedImage.png");
        request.getRequestDispatcher("Menu.jsp").forward(request, response);

        Files.deleteIfExists(imageFile.toPath());
    }
}