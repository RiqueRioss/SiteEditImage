package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/register")
public class RegistrarServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            // Criar o comando de imprimir com suporte a html
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();

            // Driver do mysql connector
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            String jdbcURL = "jdbc:mysql://http://18.190.155.155:3306/teste";
            String jdbcUsername = "root"; // Usuário do banco de dados
            String jdbcPassword = "FusionFall1!";
            
            Connection conexao = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);

            // Obtendo parâmetros inseridos na página de registro
            String nome = request.getParameter("txtNome");
            String senha = request.getParameter("txtSenha");

            // Comando sql que será executado para inserir novo usuário
            PreparedStatement pStatement = conexao.prepareStatement(
                    "insert into login (nome, senha) values (?, ?)");

            // Colocando os valores no comando sql
            pStatement.setString(1, nome);
            pStatement.setString(2, senha);

            // Executa operação de inserção de usuário no banco de dados
            int result = pStatement.executeUpdate();

            if (result > 0) {
                out.println("<h1>Registro bem-sucedido!</h1>");
                out.println("<a href=\"Login.jsp\">Ir para login</a>");
            } else {
                out.println("<h1>Falha no registro. Tente novamente.</h1>");
                out.println("<a href=\"Register.jsp\">Tente novamente</a>");
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
