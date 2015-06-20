/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kawalpemilukada.web.controller;

import com.google.gson.Gson;
import static com.googlecode.objectify.ObjectifyService.ofy;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.kawalpemilukada.model.Kandidat;
import org.kawalpemilukada.model.KandidatWilayah;
import org.kawalpemilukada.model.UserData;
import org.kawalpemilukada.model.Wilayah;

/**
 *
 * @author khairulanshar
 */
public class kandidat extends HttpServlet {

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
        JSONArray kandidats = new JSONArray();
        try {
            String[] filters = request.getRequestURI().replace("/kandidat/", "").split("/");
            String method = filters[0];
            String tahun = filters[1];
            String tingkat = filters[2];
            Gson gson = new Gson();
            if (method.equalsIgnoreCase("post")) {
                StringBuffer sb = new StringBuffer();
                String line = null;
                BufferedReader reader = request.getReader();
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                JSONObject input = (JSONObject) JSONValue.parse(sb.toString());
                try {
                    UserData user = getData.getUser(request);
                    if (user.id.length() > 0) {
                        String tingkatId = filters[3];
                        Kandidat person = new Kandidat(getData.setParentId(tahun, tingkat + tingkatId));
                        person.provinsiId = input.get("provinsiId").toString();
                        person.provinsiNama = input.get("provinsi").toString();
                        person.kabkotaId = input.get("kabupatenId").toString();
                        person.kabkotaNama = input.get("kabupaten").toString();
                        person.nama = input.get("nama").toString();
                        ofy().save().entity(person).now();
                        List<Kandidat> kandidatList = getData.filterKandidat(tahun, tingkat + tingkatId, "", "");
                        KandidatWilayah kandidatWilayah = new KandidatWilayah(getData.setParentId(tahun, tingkat));
                        kandidatWilayah.id = tingkat + tingkatId;
                        kandidatWilayah.JumlahKandidat = kandidatList.size();
                        if (tingkat.equalsIgnoreCase("Provinsi")) {
                            kandidatWilayah.kpuid = person.provinsiId;
                            kandidatWilayah.nama = person.provinsiNama;
                        } else {
                            kandidatWilayah.kpuid = person.kabkotaId;
                            kandidatWilayah.nama = person.kabkotaNama;
                        }
                        ofy().save().entity(kandidatWilayah).now();
                    }
                    List<KandidatWilayah> kandidatWilayahs = getData.filterKandidatWilayah(tahun, tingkat, "", "");
                    kandidats.add(JSONValue.parse(gson.toJson(kandidatWilayahs)));
                } catch (Exception e) {
                    System.out.println(e.toString());
                }
            } else {
                if (filters.length < 4) {
                    List<KandidatWilayah> kandidatWilayahs = getData.filterKandidatWilayah(tahun, tingkat, "", "");
                    kandidats.add(JSONValue.parse(gson.toJson(kandidatWilayahs)));
                } else {
                    String tingkatId = filters[3];
                    List<Kandidat> kandidat = getData.filterKandidat(tahun, tingkat + tingkatId, "", "");
                    kandidats.add(JSONValue.parse(gson.toJson(kandidat)));
                }
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        PrintWriter out = response.getWriter();
        response.setContentType("text/html;charset=UTF-8");
        out.print(JSONValue.toJSONString(kandidats));
        out.flush();
        out.close();
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
