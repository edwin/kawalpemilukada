/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kawalpemilukada.login;

import org.kawalpemilukada.web.controller.getData;
//import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.googlecode.objectify.Key;
import static com.googlecode.objectify.ObjectifyService.ofy;
import facebook4j.Facebook;
import facebook4j.FacebookException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.kawalpemilukada.model.Dashboard;
import org.kawalpemilukada.model.StringKey;
import org.kawalpemilukada.model.UserData;

/**
 *
 * @author khairulanshar
 */
public class callbackfb extends HttpServlet {

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

        Facebook facebook = (Facebook) request.getSession().getAttribute("userAccount");
        String oauthCode = request.getParameter("code");
        try {
            facebook.getOAuthAccessToken(oauthCode);
        } catch (FacebookException e) {
            throw new ServletException(e);
        }
        request.getSession().setAttribute("userAccount", facebook);
        String errorMsg = "Data Anda belum terverifikasi.";
        UserData user = null;
        String tahun = (String) request.getSession().getAttribute("tahun");
        Dashboard dashboard = getData.getDashboard(getData.setParentId(tahun, "0"));
        request.getSession().removeAttribute("tahun");
        try {
            facebook4j.User u = facebook.getMe();
            Key<StringKey> thekey = Key.create(StringKey.class, "fb" + getData.getVal(u.getId()));
            List<UserData> users = ofy()
                    .load()
                    .type(UserData.class) // We want only Greetings
                    .ancestor(thekey) // Anyone in this book
                    .limit(1) // Only show 5 of them.
                    .list();
            if (users.isEmpty()) {
                user = new UserData("fb" + getData.getVal(u.getId()));
                user.imgurl = "https://graph.facebook.com/" + u.getId() + "/picture";
                user.nama = getData.getVal(u.getName());
                user.link = getData.getVal(u.getLink());
                user.email = getData.getVal(u.getEmail());
                user.terverifikasi = "N";
                user.type = "fb";
                user.userlevel = 0;
                ofy().save().entity(user).now();
                getData.changeDashboardUser(dashboard);
            } else {
                user = users.get(0);
                if (user.type.equalsIgnoreCase("fb") && user.nama.equalsIgnoreCase(getData.getVal(u.getName()))) {
                    user.lastlogin = getData.JakartaTime();
                    user.type = "fb";
                    user.imgurl = "https://graph.facebook.com/" + u.getId() + "/picture";
                    if (user.terverifikasi.equalsIgnoreCase("Y")) {
                        errorMsg = "";
                    }
                } else {
                    user.terverifikasi = "N";
                }
                ofy().save().entity(user).now();
            }

        } catch (Exception e) {
            errorMsg = "callbackfb [processRequest] ==> " + e.toString();
            System.out.println("callbackfb [processRequest] ==> " + e.toString());
        }

        response.setContentType("text/html;charset=UTF-8");
        Gson gson = new Gson();
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Kawal Pemilu Kepala Daerah</title>");
            out.println("<script src=\"/bower_components/jquery/dist/jquery.min.js\"></script>");
            out.println("<script src=\"/dist/js/vendor.js\"></script>");
            out.println("</head>");
            out.println("<body>Sedang Login...");
            out.println("<script>");
            out.println("if (!jQuery.browser.mobile) {");
            out.println("try{window.opener.inviteCallback(" + gson.toJson(user) + "," + gson.toJson(dashboard) + ",'" + errorMsg + "');}catch(e){window.location='/'}");
            out.println("self.close();");
            out.println("}else{window.location='/'}");
            out.println("</script>");
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
        processRequest(request, response);
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

}
