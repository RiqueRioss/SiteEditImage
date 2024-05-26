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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

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

        /*
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
        */
        
        Path tempTessDataDir;
        try {
            tempTessDataDir = Files.createTempDirectory("tessdata");
            extractResourceFolder("/tessdata", tempTessDataDir);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        instance.setDatapath(tempTessDataDir.toString());
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
    
    private static void extractResourceFolder(String resourceFolderPath, Path targetFolder) throws IOException, URISyntaxException {
        URL resourceUrl = OCRServlet.class.getResource(resourceFolderPath);
        if (resourceUrl == null) {
            throw new IllegalArgumentException("Resource not found: " + resourceFolderPath);
        }

        String resourceUrlStr = resourceUrl.toString();
        if (resourceUrlStr.startsWith("jar:")) {
            String jarPath = resourceUrlStr.substring(10, resourceUrlStr.indexOf("!"));
            try (JarFile jar = new JarFile(jarPath)) {
                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    if (entry.getName().startsWith(resourceFolderPath.substring(1))) {
                        Path entryDestination = Paths.get(targetFolder.toString(), entry.getName().substring(resourceFolderPath.length()));
                        if (entry.isDirectory()) {
                            Files.createDirectories(entryDestination);
                        } else {
                            Files.copy(jar.getInputStream(entry), entryDestination, StandardCopyOption.REPLACE_EXISTING);
                        }
                    }
                }
            }
        } else {
            Path resourcePath = Paths.get(resourceUrl.toURI());
            Files.walk(resourcePath).forEach(source -> {
                try {
                    Path destination = Paths.get(targetFolder.toString(), resourcePath.relativize(source).toString());
                    if (Files.isDirectory(source)) {
                        Files.createDirectories(destination);
                    } else {
                        Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}