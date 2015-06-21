/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kawalpemilukada.web.controller;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.kawalpemilukada.model.Dashboard;
import org.kawalpemilukada.model.UserData;
import org.kawalpemilukada.model.Wilayah;

/**
 *
 * @author khairulanshar
 */
public class wilayah extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        JSONArray wilayahs = new JSONArray();
        try {
            String[] filters = request.getRequestURI().replace("/wilayah/", "").split("/");
            String tahun = filters[0];
            String filterby = filters[1];
            if (filterby.equalsIgnoreCase("setup")) {
                try {
                    UserData user = CommonServices.getUser(request);
                    if (user.id.length() > 0 && user.userlevel >= 1000) {
                        try {
                            CommonServices.createWilayah(filters);
                            wilayahs.add("Completed");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    System.out.println(e.toString());
                }
            } else if (filterby.equalsIgnoreCase("loadProvisnsi")) {
                try {
                    UserData user = CommonServices.getUser(request);
                    if (user.id.length() > 0 && user.userlevel >= 1000) {
                        Dashboard dashboard = CommonServices.getDashboard(CommonServices.setParentId(tahun, "0"));
                        CommonServices.loadProvisnsi(dashboard, tahun);
                        wilayahs.add("Completed");
                    }
                } catch (Exception e) {
                    System.out.println(e.toString());
                }
            } else if (filterby.equalsIgnoreCase("loadKabupaten")) {
                try {
                    UserData user = CommonServices.getUser(request);
                    if (user.id.length() > 0 && user.userlevel >= 1000) {
                        Dashboard dashboard = CommonServices.getDashboard(CommonServices.setParentId(tahun, "0"));
                        CommonServices.loadKabupaten(dashboard, tahun);
                        wilayahs.add("Completed");
                    }
                } catch (Exception e) {
                    System.out.println(e.toString());
                }
            } else if (filterby.equalsIgnoreCase("setKecamatan")) {
                try {
                    UserData user = CommonServices.getUser(request);
                    if (user.id.length() > 0 && user.userlevel >= 1000) {
                        Dashboard dashboard = CommonServices.getDashboard(CommonServices.setParentId(tahun, "0"));
                        CommonServices.loadKecamatan(request, dashboard, tahun);
                        wilayahs.add("Completed");
                    }
                } catch (Exception e) {
                    System.out.println(e.toString());
                }
            } else if (filterby.equalsIgnoreCase("setKelurahan")) {
                try {
                    UserData user = CommonServices.getUser(request);
                    if (user.id.length() > 0 && user.userlevel >= 1000) {
                        Dashboard dashboard = CommonServices.getDashboard(CommonServices.setParentId(tahun, "0"));
                        CommonServices.loadDesa(request, dashboard, tahun);
                        wilayahs.add("Completed");
                    }
                } catch (Exception e) {
                    System.out.println(e.toString());
                }
            } else if (filterby.equalsIgnoreCase("setTPS")) {
                try {
                    UserData user = CommonServices.getUser(request);
                    if (user.id.length() > 0 && user.userlevel >= 1000) {
                        Dashboard dashboard = CommonServices.getDashboard(CommonServices.setParentId(tahun, "0"));
                        String no = filters[2];
                        CommonServices.loadTPS(request, dashboard, tahun, "-" + no);
                        wilayahs.add("Completed");
                    }
                } catch (Exception e) {
                    System.out.println(e.toString());
                }
            } else {
                Gson gson = new Gson();
                if (filterby.equalsIgnoreCase("kpuid") || filterby.equalsIgnoreCase("nama") || filterby.equalsIgnoreCase("tingkat")) {
                    String parentId = filters[2];
                    String filter = filters[3];
                    List<Wilayah> wilayah = CommonServices.filterWilayah(tahun, parentId, filterby, filter);
                    wilayahs.add(JSONValue.parse(gson.toJson(wilayah)));
                } else {
                    String parentId = filterby;
                    List<Wilayah> wilayah = CommonServices.filterWilayah(tahun, parentId, "", "");
                    wilayahs.add(JSONValue.parse(gson.toJson(wilayah)));
                }
            }
        } catch (Exception e) {
        }
        PrintWriter out = response.getWriter();
        response.setContentType("text/html;charset=UTF-8");
        out.print(JSONValue.toJSONString(wilayahs));
        out.flush();
        out.close();
    }

}
