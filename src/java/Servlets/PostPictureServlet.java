/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import oracle.sql.BLOB;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.rowset.serial.SerialBlob;
import oracle.jdbc.OracleConnection;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
import oracle.sql.CLOB;
import sun.misc.BASE64Decoder;

/**
 *
 * @author IRVIN
 */
@WebServlet(name = "PostPictureServlet", urlPatterns = {"/PostPictureServlet"})
public class PostPictureServlet extends HttpServlet {

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
            out.println("<title>Servlet PostPictureServlet</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet PostPictureServlet at " + request.getContextPath() + "</h1>");
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
        
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = request.getReader();
        //BLOB blob;
        //byte[] byteArray = null;
        CLOB clob;
        String employeeNumber;
        
        try{
            String line;
            while ((line = reader.readLine()) != null){
                builder.append(line);
            }
                
            JsonReader jsonReader = Json.createReader(new StringReader(builder.toString()));
            JsonObject jsonObject = jsonReader.readObject();
            jsonReader.close();
            
            employeeNumber = jsonObject.getString("employee_number");
            //byteArray = decodeImageString(jsonObject.getString("picture"));
            clob = createClob(jsonObject.getString("picture"));
            
            if (setEmployeePicture(employeeNumber, clob)){            
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (IOException ex){
            System.out.println(ex.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } finally {
            reader.close();
        }      
        
    }
    
    public BLOB decodeToImage(String stringImage){
        BLOB blob = null;
        String url = "jdbc:oracle:thin:@192.1.1.193:1601:DEV";
        OracleConnection oracleConnection;
        Connection connection;
        
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection(url, "apps", "apps");            
            
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] decodeBytes = decoder.decodeBuffer(stringImage);
            
            oracleConnection = (OracleConnection) connection;
            
            
            blob = BLOB.createTemporary(oracleConnection, 
                                        true, 
                                        oracle.sql.BLOB.DURATION_SESSION);
            
            OutputStream outputStream = blob.setBinaryStream(0);
            outputStream.write(decodeBytes);
            outputStream.flush();
            outputStream.close();
            
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        } catch (ClassNotFoundException ex) {
            System.out.println(ex.getMessage());
        } 
        
        return blob;
    }
    
    public byte[] decodeImageString(String stringImage){
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] decodeBytes = null;
        try {
            decodeBytes = decoder.decodeBuffer(stringImage);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        
        return decodeBytes;
    }
    
    public CLOB createClob(String data){
        CLOB clob = null;
        BASE64Decoder decoder = new BASE64Decoder();
        String url = "jdbc:oracle:thin:@192.1.1.193:1601:DEV";
        OracleConnection oracleConnection;
        Connection connection;
        byte[] decodeBytes = null;
        
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection(url, "apps", "apps");
            oracleConnection = (OracleConnection) connection;
            
            decodeBytes = decoder.decodeBuffer(data);
            clob = CLOB.createTemporary(oracleConnection, false, oracle.sql.CLOB.DURATION_SESSION);
            clob.open(CLOB.MODE_READWRITE);
            
            OutputStream stream = (OutputStream) clob.setAsciiStream(0L);
            stream.write(decodeBytes);
            stream.flush();
            stream.close();
            clob.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        } catch (ClassNotFoundException ex) {
            System.out.println(ex.getMessage());
        }       
        
        return clob;
    }
    
    private Boolean setEmployeePicture(String employeeNumber, CLOB picture){
    
        Connection connection = null;
        CallableStatement statement = null;
        Boolean result = false;
        String stringResult = "";
        
        String sql = "{ ? = call PAC_HR_APPLICATION_ANDROID_PKG.SET_PICTURE(?, ?) }";
        String url = "jdbc:oracle:thin:@192.1.1.193:1601:DEV";
        
        try{
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection(url, "apps", "apps");
            
            statement = connection.prepareCall(sql);
            statement.setString(2, employeeNumber);
            statement.setClob(3, picture);
            
            statement.registerOutParameter(1, java.sql.Types.VARCHAR);
            statement.execute();
            
            stringResult = statement.getString(1);
            
            if (stringResult.equals("true") || stringResult.equals("false")){
                result = Boolean.parseBoolean(stringResult);
            } else {
                System.out.println("DBException=" + stringResult);
            }
            
            connection.close();
        } catch (ClassNotFoundException ex) {
            System.out.println("ClassNotFoundException=" + ex.getMessage());
            result = false;
        } catch (SQLException ex) {
            System.out.println("SQLException=" + ex.getMessage());
            result = false;
        }
        
        return result;
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
