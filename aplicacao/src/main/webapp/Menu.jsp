<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Menu</title>
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>

	<h2>Sua Imagem</h2><br>
    <img src="${imagePath}" alt="Processed Image" style="max-width: 500px;" /><br><br>
    <h3>Texto Identificado</h3>
    <p>${ocrResult}</p>
    <br><br>
    
    <h3>Procurar Texto</h3>
    <form action="OCRServlet" method="post" enctype="multipart/form-data">
        <input type="file" name="image" accept="image/*" required />
        <button type="submit">Identificar Texto</button>
    </form>
    <br>
    
    <h3>Aplicar Marca d'água</h3>
    <form action="MarcaServlet" method="post" enctype="multipart/form-data">
    	<p>Imagem</p>
        <input type="file" name="image" accept="image/*" required /> <br>
        <p>Marca D'água</p>
        <input type="file" name="marcadagua" accept="image/*" required />
        <button type="submit">Aplicar Marca D'água</button>
    </form>
    <br>
    
    <h3>Alterar Tamanho da Imagem</h3>
    <form action="TamanhoServlet" method="post" enctype="multipart/form-data">
        <input type="file" name="image" accept="image/*" required />
        <input type=number name=width>
        <input type=number name=height>
        <button type="submit">Proceder</button>
    </form>
    <br>
    
    <h3>Aplicar um Filtro</h3>
    <form action="FiltroServlet" method="post" enctype='multipart/form-data'>
    	<input type="file" name="image" accept="image/*" required /><br>
	    <input type="radio" name="opcao" value="1" required> Tons de Cinza<br>
	    <input type="radio" name="opcao" value="2" required> Negativo<br>
	    <input type="radio" name="opcao" value="3" required> Binarizar
	    	<input type=number name=limiar min="0" max="254" placeholder="Limiar"><br>
	    <input type="radio" name="opcao" value="4" required> Remover Ruído<br>
	    <input type="radio" name="opcao" value="5" required> Adicionar Brilho
	    	<input type=number name=brilho placeholder="Valor"><br>
	    <button type="submit">Proceder</button>
	</form>
    
    <script>
        function performOperation(operation) {
            var formData = new FormData();
            if (operation === 'ocr') {
                formData.append('image', $('#textImage')[0].files[0]);
            } else if (operation === 'marca') {
                formData.append('image', $('#watermarkImage')[0].files[0]);
                formData.append('marcadagua', $('#watermark')[0].files[0]);
            }
            formData.append('operation', operation);

            $.ajax({
                url: operation,
                type: 'POST',
                data: formData,
                processData: false,
                contentType: false,
                success: function(response) {
                    $('#processedImage').attr('src', response.processedImage);
                    if (response.ocrResult) {
                        $('p').text(response.ocrResult);
                    }
                },
                error: function(xhr, status, error) {
                    console.log('Erro na requisição AJAX:', error);
                }
            });
        }
    </script>
    
</body>
</html>