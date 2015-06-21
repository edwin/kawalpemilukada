/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kawalpemilukada.web.controller;

//import com.google.common.reflect.TypeToken;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.gson.Gson;
import com.googlecode.objectify.Key;
import static com.googlecode.objectify.ObjectifyService.ofy;
import com.googlecode.objectify.cmd.Query;
import com.googlecode.objectify.cmd.QueryKeys;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
//import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.kawalpemilukada.model.Dashboard;
import org.kawalpemilukada.model.Pesan;
import org.kawalpemilukada.model.StringKey;
import org.kawalpemilukada.model.UserData;
import org.kawalpemilukada.model.Wilayah;

/**
 *
 * @author khairulanshar
 */
public class getModelData extends HttpServlet {

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
        String form_action = request.getParameter("form_action");
        if (form_action == null) {
            form_action = "";
        }
        PrintWriter out = response.getWriter();
        response.setContentType("text/html;charset=UTF-8");
        LinkedHashMap record = new LinkedHashMap();
        Gson gson = new Gson();
        if (form_action.equalsIgnoreCase("getDashboard")) {
            String tahun = request.getParameter("tahun");
            if (tahun == null) {
                tahun = "";
            }
            Dashboard dashboard = CommonServices.getDashboard(CommonServices.setParentId(tahun, "0"));
            record.put("dashboard", JSONValue.parse(gson.toJson(dashboard)));
        }
        if (form_action.equalsIgnoreCase("getNumberUser")) {
            String tahun = request.getParameter("tahun");
            if (tahun == null) {
                tahun = "";
            }
            UserData user = CommonServices.getUser(request);
            if (user.userlevel < 1000) {
                return;
            }
            Dashboard dashboard = CommonServices.getDashboard(CommonServices.setParentId(tahun, "0"));
            CommonServices.changeDashboardUser(dashboard);
            record.put("dashboard", JSONValue.parse(gson.toJson(dashboard)));
        }
        if (form_action.equalsIgnoreCase("updateUser")) {
            StringBuffer sb = new StringBuffer();
            String line = null;
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            JSONObject input = (JSONObject) JSONValue.parse(sb.toString());
            try {
                UserData user = CommonServices.getUser(request);
                if (user.id.length() > 0 && user.terverifikasi.equalsIgnoreCase("Y")) {
                    if (input.get("id").toString().equalsIgnoreCase(user.id)) {
                        if (input.get("kabkota").toString().length() > 0) {
                            user.kabkota = input.get("kabkota").toString();
                            user.kabkotaId = input.get("kabkotaId").toString();
                        }
                        if (input.get("desa").toString().length() > 0) {
                            user.desa = input.get("desa").toString();
                            user.desaId = input.get("desaId").toString();
                        }
                        if (input.get("kecamatan").toString().length() > 0) {
                            user.kecamatan = input.get("kecamatan").toString();
                            user.kecamatanId = input.get("kecamatanId").toString();
                        }
                        if (input.get("provinsi").toString().length() > 0) {
                            user.provinsi = input.get("provinsi").toString();
                            user.provinsiId = input.get("provinsiId").toString();
                        }
                        if (input.get("email").toString().length() > 0) {
                            user.email = input.get("email").toString();
                        }
                        if (input.get("nokontak").toString().length() > 0) {
                            user.nokontak = input.get("nokontak").toString();
                        }
                        if (input.get("userlevel").toString().length() > 0) {
                            if (Integer.parseInt(input.get("userlevel").toString()) > 200) {
                                user.userlevel = 0;
                            } else {
                                user.userlevel = Integer.parseInt(input.get("userlevel").toString());
                            }
                        }
                        ofy().save().entity(user).now();
                        record.put("user", JSONValue.parse(gson.toJson(user)));
                    }
                }
            } catch (Exception e) {
            }
        }
        
        if (form_action.equalsIgnoreCase("setPesan")) {
            record.put("pesan", false);
            record.put("parentPesan", false);
            record.put("tanggapanPesan", false);
            record.put("setujuPesan", false);
            record.put("tidakSetujuPesan", false);
            record.put("parentId", "");
            StringBuffer sb = new StringBuffer();
            String line = null;
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            JSONArray input = (JSONArray) JSONValue.parse(sb.toString());
            try {
                UserData user = CommonServices.getUser(request);
                if (user.id.length() > 0 && user.terverifikasi.equalsIgnoreCase("Y")) {
                    String key = input.get(0).toString();
                    if (key.equalsIgnoreCase("Pesan Untuk Semua")) {
                        key = "wall";
                    } else if (key.equalsIgnoreCase("Pesan Untuk Saya")) {
                        key = "msg" + user.id;
                    }
                    Pesan pesan = new Pesan(key);
                    pesan.dari_id = user.id;
                    if (key.contains("#setuju#")) {
                        pesan.id = user.uid;//Long.parseLong(user.id.replace(user.type, ""));
                        pesan.setujutidaksetuju = input.get(5).toString();
                    } else {
                        CommonServices.addPoinToUser(user, 10);
                        record.put("user", JSONValue.parse(gson.toJson(user)));
                    }
                    pesan.dari_nama = user.nama;
                    pesan.dari_img = user.imgurl;
                    pesan.dari_link = user.link;
                    pesan.untuk_id = input.get(1).toString();
                    pesan.untuk_nama = input.get(2).toString();
                    pesan.untuk_img = input.get(3).toString();
                    pesan.untuk_link = input.get(4).toString();
                    pesan.msg = input.get(5).toString();
                    pesan.icon = input.get(6).toString();
                    String cursorStr = input.get(7).toString();
                    int limit = Integer.parseInt(input.get(8).toString());
                    if (limit > 50) {
                        limit = 50;
                    }
                    int offset = Integer.parseInt(input.get(9).toString());
                    if (offset < 0) {
                        offset = 0;
                    }
                    String parentId = input.get(10).toString();
                    String parentKeyString = input.get(11).toString();
                    JSONArray jsonArrays = (JSONArray) input.get(12);
                    List<String> files = new ArrayList<>();
                    for (Object jsonArray1 : jsonArrays) {
                        files.add(JSONValue.toJSONString(((JSONArray) jsonArray1)));
                    }
                    pesan.files = files;
                    ofy().save().entity(pesan).now();
                    record.put("parentId", parentId);

                    if (input.get(0).toString().equalsIgnoreCase("Pesan Untuk Semua")) {
                        record.put("pesan", JSONValue.parse(gson.toJson(pesan)));
                    } else if (input.get(0).toString().equalsIgnoreCase("Pesan Untuk Saya")) {
                        record.put("pesan", JSONValue.parse(gson.toJson(pesan)));
                    } else {

                        Key<StringKey> parentKey = Key.create(StringKey.class, parentKeyString);
                        Key<Pesan> keyWithParent = Key.create(parentKey, Pesan.class, Long.parseLong(parentId));
                        Pesan parentPesan = ofy().load().type(Pesan.class).ancestor(keyWithParent).first().now();
                        if (key.contains("#tanggapan#")) {
                            QueryKeys<Pesan> childPesan = ofy().load().type(Pesan.class).ancestor(pesan.key).keys();
                            parentPesan.jumlahTanggapan = childPesan.list().size();
                            record.put("tanggapanPesan", JSONValue.parse(gson.toJson(pesan)));
                        }
                        if (key.contains("#setuju#")) {
                            Query<Pesan> childPesan = ofy().load().type(Pesan.class).ancestor(pesan.key).filter("setujutidaksetuju", "Setuju");
                            parentPesan.jumlahSetuju = childPesan.keys().list().size();
                            record.put("setujuPesans", JSONValue.parse(gson.toJson(childPesan.offset(offset).limit(limit).list())));
                            childPesan = ofy().load().type(Pesan.class).ancestor(pesan.key).filter("setujutidaksetuju", "Tidak Setuju");
                            parentPesan.jumlahTidakSetuju = childPesan.keys().list().size();
                            record.put("tidakSetujuPesans", JSONValue.parse(gson.toJson(childPesan.offset(offset).limit(limit).list())));
                        }
                        ofy().save().entity(parentPesan).now();
                        record.put("parentPesan", JSONValue.parse(gson.toJson(parentPesan)));
                    }

                }
            } catch (Exception e) {
            }
        }

        if (form_action.equalsIgnoreCase("getPesan")) {
            record.put("pesans", new JSONArray());
            record.put("tanggapanPesans", new JSONArray());
            record.put("setujuPesans", new JSONArray());
            record.put("tidakSetujuPesans", new JSONArray());
            record.put("cursorStr", "");
            StringBuffer sb = new StringBuffer();
            String line = null;
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            JSONArray input = (JSONArray) JSONValue.parse(sb.toString());
            try {
                String key = input.get(0).toString();
                if (key.equalsIgnoreCase("Pesan Untuk Semua")) {
                    key = "wall";
                } else if (key.equalsIgnoreCase("Pesan Untuk Saya")) {
                    try {
                        UserData user = CommonServices.getUser(request);
                        key = "msg" + user.id;
                    } catch (Exception e) {
                        key = "wall";
                    }
                }
                String filter = input.get(1).toString();
                String filterBy = input.get(2).toString();
                String cursorStr = input.get(3).toString();
                int limit = Integer.parseInt(input.get(4).toString());
                if (limit > 50) {
                    limit = 50;
                }
                int offset = Integer.parseInt(input.get(5).toString());
                if (offset < 0) {
                    offset = 0;
                }
                Query<Pesan> pesan = CommonServices.getPesan(key, filter, filterBy, cursorStr, offset, limit);
                //record.put("cursorStr", query.iterator().getCursor().toWebSafeString());
                record.put("cursorStr", "");
                if (input.get(0).toString().equalsIgnoreCase("Pesan Untuk Semua")) {
                    record.put("pesans", JSONValue.parse(gson.toJson(pesan.list())));
                } else if (input.get(0).toString().equalsIgnoreCase("Pesan Untuk Saya")) {
                    record.put("pesans", JSONValue.parse(gson.toJson(pesan.list())));
                } else if (input.get(0).toString().contains("#tanggapan#")) {
                    record.put("tanggapanPesans", JSONValue.parse(gson.toJson(pesan.list())));
                } else if (input.get(0).toString().contains("#setuju#")) {
                    if (filter.equalsIgnoreCase("Setuju")) {
                        record.put("setujuPesans", JSONValue.parse(gson.toJson(pesan.list())));
                    } else {
                        record.put("tidakSetujuPesans", JSONValue.parse(gson.toJson(pesan.list())));
                    }
                }

            } catch (Exception e) {
            }
        }
        if (form_action.equalsIgnoreCase("getUrlFile")) {
            record.put("uploadurl", "");
            try {
                BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
                String uploadurl = blobstoreService.createUploadUrl("/upload");
                record.put("uploadurl", uploadurl);
            } catch (Exception e) {
            }
        }

        out.print(JSONValue.toJSONString(record));
        out.flush();
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
