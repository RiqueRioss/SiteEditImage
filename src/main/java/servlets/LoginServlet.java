package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
       
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            // Criar o comando de imprimir com suporte a html
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            
            // Driver do mysql connector
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Conectando com o banco de dados (local, nome, senha)
            String jdbcURL = "jdbc:mysql://http://18.190.155.155:3306/teste";
            String jdbcUsername = "root"; // Usuário do banco de dados
            String jdbcPassword = "FusionFall1!";
            
            Connection conexao = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
            
            // Obtendo parâmetros inseridos na página de login
            String nome = request.getParameter("txtNome");
            String senha = request.getParameter("txtSenha");
            
            // Comando sql que será executado para verificar se o login existe
            PreparedStatement pStatement = conexao.prepareStatement(
                    "select nome from login where nome=? and senha=?");
            
            // Colocando o login no comando sql
            pStatement.setString(1, nome);
            pStatement.setString(2, senha);
            
            // Executa operação de busca de usuário no banco de dados
            ResultSet rSet = pStatement.executeQuery();
            
            // True se o usuário for encontrado com o comando select
            if (rSet.next()) {
                RequestDispatcher rDispatcher = request.getRequestDispatcher("Menu.jsp");
                rDispatcher.forward(request, response);
            } else {
                out.println("<h1>Login Falhou</h1>");
                out.println("<a href=\"Login.jsp\">Tente novamente</a>");
            }
            
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
