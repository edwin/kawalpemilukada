/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kawalpemilukada.login;

import com.google.gson.Gson;
import com.googlecode.objectify.Key;
import static com.googlecode.objectify.ObjectifyService.ofy;
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
import org.kawalpemilukada.web.controller.CommonServices;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.auth.RequestToken;

/**
 *
 * @author khairulanshar
 */
public class callbackTwit extends HttpServlet {

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
        Twitter twitter = (Twitter) request.getSession().getAttribute("userAccount");
        RequestToken requestToken = (RequestToken) request.getSession().getAttribute("requestToken");
        String verifier = request.getParameter("oauth_verifier");
        String rurl = request.getParameter("rurl");
        try {
            twitter.getOAuthAccessToken(requestToken, verifier);
            request.getSession().removeAttribute("requestToken");
        } catch (TwitterException e) {
        }
        request.getSession().setAttribute("userAccount", twitter);
        String errorMsg = "Data Anda belum terverifikasi.";
        UserData user = null;
        String tahun = (String) request.getSession().getAttribute("tahun");
        Dashboard dashboard = CommonServices.getDashboard(CommonServices.setParentId(tahun, "0"));
        request.getSession().removeAttribute("tahun");
        try {
            User u = twitter.showUser(twitter.getId());
            Key<StringKey> thekey = Key.create(StringKey.class, "twit" + CommonServices.getVal(u.getId()));
            List<UserData> users = ofy()
                    .load()
                    .type(UserData.class) // We want only Greetings
                    .ancestor(thekey) // Anyone in this book
                    .limit(1) // Only show 5 of them.
                    .list();
            if (users.isEmpty()) {
                user = new UserData("twit" + CommonServices.getVal(twitter.getId()));
                user.imgurl = u.getBiggerProfileImageURL().replace("http://", "https://");
                user.nama = CommonServices.getVal(u.getName());
                user.link = "https://twitter.com/" + CommonServices.getVal(twitter.getScreenName());
                user.email = "";
                user.type = "twit";
                ofy().save().entity(user).now();
                CommonServices.changeDashboardUser(dashboard);
            } else {
                user = users.get(0);
                if (user.type.equalsIgnoreCase("twit") && user.nama.equalsIgnoreCase(CommonServices.getVal(u.getName()))) {
                    user.lastlogin = CommonServices.JakartaTime();
                    user.type = "twit";
                    user.imgurl = u.getBiggerProfileImageURL().replace("http://", "https://");
                    if (user.terverifikasi.equalsIgnoreCase("Y")) {
                        errorMsg = "";
                    }
                }
                ofy().save().entity(user).now();
            }
        } catch (Exception e) {
            errorMsg = "callbackfb [processRequest] ==> " + e.toString();
            System.out.println("callbackTwit [processRequest] ==> " + e.toString());
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
            out.println("}else{window.location='/#" + rurl + "'}");
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