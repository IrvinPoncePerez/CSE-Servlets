/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servlets;

import com.sun.xml.rpc.processor.modeler.j2ee.xml.javaIdentifierType;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.json.Json;
import javax.json.JsonObject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import oracle.sql.BLOB;

/**
 *
 * @author IRVIN
 */
public class GetPictureServlet extends HttpServlet {

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
            out.println("<title>Servlet GetPictureServlet</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet GetPictureServlet at " + request.getContextPath() + "</h1>");
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
        
        JsonObject jsonObject = null;
        
        jsonObject = (JsonObject)Json.createObjectBuilder()
                .add("employee_number", request.getParameter("E"))
                .add("employee_name", getEmployeeData(Integer.parseInt(request.getParameter("E")), "EMPLOYEE_NAME"))
                .add("department", getEmployeeData(Integer.parseInt(request.getParameter("E")), "DEPARTMENT"))
                //.add("job", getEmployeeData(Integer.parseInt(request.getParameter("E")), "JOB"))
                .add("picture", " ")
                .build();
        
        response.getWriter().print(jsonObject);
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
    
    private String getEmployeeData(Integer employeeNumber, String data){
        Connection connection = null;
        CallableStatement statement = null;
        ResultSet resultSet = null;
        String result = " ";
        String sql = null;
        
        switch(data){
                case "EMPLOYEE_NAME" :{
                    sql = "{ ? = call PAC_HR_APPLICATION_ANDROID_PKG.GET_EMPLOYEE_NAME(?) }";
                } break;
                case "DEPARTMENT" :{
                    sql = "{ ? = call PAC_HR_APPLICATION_ANDROID_PKG.GET_DEPARTMENT(?) }";
                } break;
                case "JOB" : {
                    sql = "{ ? = call PAC_HR_APPLICATION_ANDROID_PKG.GET_JOB(?) }";
                } break;
            }
        
        String url = "jdbc:oracle:thin:@192.1.1.193:1601:DEV";
        
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection(url, "apps", "apps");
            
            statement = connection.prepareCall(sql);
            statement.setInt(2, employeeNumber);
            statement.registerOutParameter(1, java.sql.Types.VARCHAR);
            statement.execute();
            
            result = statement.getString(1);
                       
        } catch (ClassNotFoundException ex){
            System.out.println("ClassNotFoundException=" + ex.getMessage());
        } catch (SQLException ex){
            System.out.println("SQLException=" + ex.getMessage());
        }
        
        return result;
        
    }

}
