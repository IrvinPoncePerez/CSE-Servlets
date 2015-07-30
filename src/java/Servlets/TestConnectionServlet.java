/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonObject;

        

/**
 *
 * @author IRVIN
 */
public class TestConnectionServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(TestConnectionServlet.class.getCanonicalName());
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet TestConnectionServlet</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet TestConnectionServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //processRequest(request, response);
        
        JsonObject jsonObject = null;
        
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        
        String url = "jdbc:oracle:thin:@192.1.1.193:1601:DEV";
        
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection(url, "apps", "apps");
            
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT DESC_CONNECTION FROM TEST_CONNECTION");
            
            while (resultSet.next()){
                jsonObject  = (JsonObject) Json.createObjectBuilder()
                        .add("status", resultSet.getString(1))
                        .build();
            }
            
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().print(jsonObject);
            
        } catch (ClassNotFoundException ex){
            response.getWriter().print("ClassNotFoundException" + ex.getMessage());
        } catch (SQLException ex){
            response.getWriter().print("SQLException" + ex.getMessage());
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
