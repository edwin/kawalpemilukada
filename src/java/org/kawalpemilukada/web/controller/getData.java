/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kawalpemilukada.web.controller;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.QueryResultList;
import com.googlecode.objectify.Key;
import static com.googlecode.objectify.ObjectifyService.ofy;
import com.googlecode.objectify.cmd.Query;
import com.googlecode.objectify.cmd.QueryKeys;
import facebook4j.Facebook;
import facebook4j.User;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import javax.servlet.http.HttpServletRequest;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.kawalpemilukada.model.Dashboard;
import org.kawalpemilukada.model.Kandidat;
import org.kawalpemilukada.model.KandidatWilayah;
import org.kawalpemilukada.model.Pesan;
import org.kawalpemilukada.model.StringKey;
import org.kawalpemilukada.model.UserData;
import org.kawalpemilukada.model.Wilayah;
import twitter4j.Twitter;

/**
 *
 * @author khairulanshar
 */
public class getData {

    public static final String version = "1";
    public static final String delimeter = "#";

    public static String setParentId(String tahun, String parentId) {
        return tahun + delimeter + parentId + delimeter + version;
    }
    public static String tingkat1 = "Provinsi";
    public static String tingkat2 = "Kabupaten-Kota";
    public static String tingkat3 = "Kecamatan";
    public static String tingkat4 = "Desa";
    public static String tingkat5 = "TPS";

    public static void addPoinToUser(UserData user, int point) {
        user.poin = user.poin + 10;
        ofy().save().entity(user).now();

    }

    public static void loadProvisnsi(Dashboard dashboard, String tahun) {
        try {
            if (tahun.equalsIgnoreCase("2015")) {
                InputStream feedStream = new FileInputStream("dist/data/propinsi" + tahun + ".json");
                InputStreamReader is = new InputStreamReader(feedStream);
                StringBuilder sb1 = new StringBuilder();
                BufferedReader br = new BufferedReader(is);
                String read = br.readLine();
                while (read != null) {
                    sb1.append(read);
                    read = br.readLine();
                }
                JSONArray propinsis = (JSONArray) JSONValue.parse(sb1.toString());
                for (int i = 0; i < propinsis.size(); i++) {
                    Wilayah wilayah = new Wilayah(setParentId(tahun, "0"));
                    JSONArray propinsi = (JSONArray) propinsis.get(i);
                    wilayah.id = getData.tingkat1 + propinsi.get(1).toString();
                    wilayah.kpuid = propinsi.get(1).toString();
                    wilayah.nama = propinsi.get(2).toString();
                    wilayah.tingkat = getData.tingkat1;
                    ofy().save().entity(wilayah).now();
                }
                dashboard.provinsi = propinsis.size() + "";
            }
            if (tahun.equalsIgnoreCase("2014")) {
                InputStream feedStream = new FileInputStream("dist/data/propinsi" + tahun + ".csv");
                InputStreamReader is = new InputStreamReader(feedStream);
                BufferedReader br = new BufferedReader(is);
                String read = br.readLine();
                String cvsSplitBy = ",";
                int i = 0;
                while (read != null) {
                    i++;
                    try {
                        String[] kelurahan = read.split(cvsSplitBy);
                        Wilayah wilayah = new Wilayah(setParentId(tahun, kelurahan[0]));
                        wilayah.id = getData.tingkat1 + kelurahan[1];
                        wilayah.kpuid = kelurahan[1];
                        wilayah.nama = kelurahan[2].replace("\"", "");
                        wilayah.tingkat = getData.tingkat1;
                        ofy().save().entity(wilayah).now();
                    } catch (Exception e) {
                    }
                    read = br.readLine();
                }
                dashboard.provinsi = i + "";
            }
            ofy().save().entity(dashboard).now();
        } catch (Exception e) {
        }
    }

    public static void loadKabupaten(Dashboard dashboard, String tahun) {
        try {
            if (tahun.equalsIgnoreCase("2015")) {
                InputStream feedStream = new FileInputStream("dist/data/kabkota" + tahun + ".json");
                InputStreamReader is = new InputStreamReader(feedStream);
                StringBuilder sb1 = new StringBuilder();
                BufferedReader br = new BufferedReader(is);
                String read = br.readLine();
                while (read != null) {
                    sb1.append(read);
                    read = br.readLine();
                }
                JSONArray propinsis = (JSONArray) JSONValue.parse(sb1.toString());
                for (int i = 0; i < propinsis.size(); i++) {
                    JSONArray propinsi = (JSONArray) propinsis.get(i);
                    Wilayah wilayah = new Wilayah(setParentId(tahun, propinsi.get(1).toString()));
                    wilayah.id = getData.tingkat2 + propinsi.get(2).toString();
                    wilayah.kpuid = propinsi.get(2).toString();
                    wilayah.nama = propinsi.get(4).toString();
                    wilayah.tingkat = getData.tingkat2;
                    ofy().save().entity(wilayah).now();
                }
                dashboard.kabupaten = propinsis.size() + "";
            }
            if (tahun.equalsIgnoreCase("2014")) {
                InputStream feedStream = new FileInputStream("dist/data/kabkota" + tahun + ".csv");
                InputStreamReader is = new InputStreamReader(feedStream);
                BufferedReader br = new BufferedReader(is);
                String read = br.readLine();
                String cvsSplitBy = ",";
                int i = 0;
                while (read != null) {
                    i++;
                    try {
                        String[] kelurahan = read.split(cvsSplitBy);
                        Wilayah wilayah = new Wilayah(setParentId(tahun, kelurahan[0]));
                        wilayah.id = getData.tingkat2 + kelurahan[1];
                        wilayah.kpuid = kelurahan[1];
                        wilayah.nama = kelurahan[2].replace("\"", "");
                        wilayah.tingkat = getData.tingkat2;
                        ofy().save().entity(wilayah).now();
                    } catch (Exception e) {
                    }
                    read = br.readLine();
                }
                dashboard.kabupaten = i + "";
            }
            ofy().save().entity(dashboard).now();
        } catch (Exception e) {
        }
    }

    public static void loadKecamatan(HttpServletRequest request, Dashboard dashboard, String tahun) {
        try {
            UserData user = getData.getUser(request);
            if (user.userlevel < 1000) {
                return;
            }
            InputStream feedStream = new FileInputStream("dist/data/kecamatan" + tahun + ".csv");
            InputStreamReader is = new InputStreamReader(feedStream);
            BufferedReader br = new BufferedReader(is);
            String read = br.readLine();
            String cvsSplitBy = ",";
            int i = 0;
            while (read != null) {
                i++;
                try {
                    String[] kelurahan = read.split(cvsSplitBy);
                    Wilayah wilayah = new Wilayah(setParentId(tahun, kelurahan[0]));
                    wilayah.id = getData.tingkat3 + kelurahan[1];
                    wilayah.kpuid = kelurahan[1];
                    wilayah.nama = kelurahan[2].replace("\"", "");
                    wilayah.tingkat = getData.tingkat3;
                    ofy().save().entity(wilayah).now();
                } catch (Exception e) {
                }
                read = br.readLine();
            }
            dashboard.kecamatan = i + "";
            ofy().save().entity(dashboard).now();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public static void loadDesa(HttpServletRequest request, Dashboard dashboard, String tahun) {
        try {
            UserData user = getData.getUser(request);
            if (user.userlevel < 1000) {
                return;
            }
            InputStream feedStream = new FileInputStream("dist/data/kelurahan" + tahun + ".csv");
            InputStreamReader is = new InputStreamReader(feedStream);
            BufferedReader br = new BufferedReader(is);
            String read = br.readLine();
            String cvsSplitBy = ",";
            int i = 0;
            while (read != null) {
                i++;
                try {
                    String[] kelurahan = read.split(cvsSplitBy);
                    Wilayah wilayah = new Wilayah(setParentId(tahun, kelurahan[0]));
                    wilayah.id = getData.tingkat4 + kelurahan[1];
                    wilayah.kpuid = kelurahan[1];
                    wilayah.nama = kelurahan[2].replace("\"", "");
                    wilayah.tingkat = getData.tingkat4;
                    ofy().save().entity(wilayah).now();
                } catch (Exception e) {
                }
                read = br.readLine();
            }
            dashboard.desa = i + "";
            ofy().save().entity(dashboard).now();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public static void loadTPS(HttpServletRequest request, Dashboard dashboard, String tahun, String no) {
        try {
            UserData user = getData.getUser(request);
            if (user.userlevel < 1000) {
                return;
            }
            InputStream feedStream = new FileInputStream("dist/data/tps" + tahun + no + ".csv");
            InputStreamReader is = new InputStreamReader(feedStream);
            BufferedReader br = new BufferedReader(is);
            String read = br.readLine();
            String cvsSplitBy = ",";
            int i = 0;
            while (read != null) {
                i++;
                try {
                    String[] tpsline = read.split(cvsSplitBy);
                    Wilayah wilayah = new Wilayah(setParentId(tahun, tpsline[0]));
                    wilayah.id = getData.tingkat5 + tpsline[1];
                    wilayah.kpuid = tpsline[1];
                    wilayah.nama = tpsline[2].replace("\"", "");
                    wilayah.tingkat = getData.tingkat5;
                    ofy().save().entity(wilayah).now();
                } catch (Exception e) {
                }
                read = br.readLine();
            }
            dashboard.TPS = (Integer.parseInt(dashboard.TPS) + i) + "";
            if (tahun.contains("2014")) {
                dashboard.TPS = 478731 + "";
            }
            ofy().save().entity(dashboard).now();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public static List<Wilayah> filterWilayah(String tahun, String parentId, String filterBy, String filter) {
        Key<StringKey> dashKey = Key.create(StringKey.class, setParentId(tahun, parentId));
        Query<Wilayah> query = ofy().load().type(Wilayah.class).ancestor(dashKey);
        if (filterBy.length() > 0 && filter.length() > 0) {
            query = query.filter(filterBy, filter);
        }
        return query.list();
    }

    public static List<Kandidat> filterKandidat(String tahun, String parentId, String filterBy, String filter) {
        Key<StringKey> dashKey = Key.create(StringKey.class, setParentId(tahun, parentId));
        Query<Kandidat> query = ofy().load().type(Kandidat.class).ancestor(dashKey);
        if (filterBy.length() > 0 && filter.length() > 0) {
            query = query.filter(filterBy, filter);
        }
        return query.list();
    }
    
    public static List<KandidatWilayah> filterKandidatWilayah(String tahun, String parentId, String filterBy, String filter) {
        Key<StringKey> dashKey = Key.create(StringKey.class, setParentId(tahun, parentId));
        Query<KandidatWilayah> query = ofy().load().type(KandidatWilayah.class).ancestor(dashKey);
        if (filterBy.length() > 0 && filter.length() > 0) {
            query = query.filter(filterBy, filter);
        }
        return query.list();
    }

    public static QueryResultList<Entity> getPesans(String key, String filter, String filterBy, String cursorStr, int offset, int limit) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        FetchOptions fetchOptions = FetchOptions.Builder.withLimit(limit);
        com.google.appengine.api.datastore.Key keyx = KeyFactory.createKey("StringKey", "wall");
        com.google.appengine.api.datastore.Query query = new com.google.appengine.api.datastore.Query("Pesan", keyx);
        query.addSort("creationDate", com.google.appengine.api.datastore.Query.SortDirection.DESCENDING);
        QueryResultList<Entity> pesans = datastore.prepare(query).asQueryResultList(fetchOptions);
        return pesans;
    }

    public static Query<Pesan> getPesan(String key, String filter, String filterBy, String cursorStr, int offset, int limit) {
        Key<StringKey> dashKey = Key.create(StringKey.class, key);
        Query<Pesan> query = ofy().load().type(Pesan.class).ancestor(dashKey);
        /*if (cursorStr != null && cursorStr.length() > 0) {
         query = query.startAt(Cursor.fromWebSafeString(cursorStr));
         }*/
        if (filterBy.length() > 0 && filter.length() > 0) {
            query = query.filter(filterBy, filter);
        }
        query = query.order("-creationDate").offset(offset).limit(limit);
        //https://code.google.com/p/getStartCursor-appengine/wiki/Queries#Cursor_Example
        return query;
    }

    public static UserData getUser(HttpServletRequest request) {
        UserData user = null;
        String id = "";
        try {
            Facebook facebook = (Facebook) request.getSession().getAttribute("userAccount");
            User u = facebook.getMe();
            id = "fb" + getVal(u.getId());
        } catch (Exception e) {
            try {
                Twitter twitter = (Twitter) request.getSession().getAttribute("userAccount");
                id = "twit" + getVal(twitter.getId());
            } catch (Exception ee) {
                id = "";
            }
        }
        if (id.length() > 0) {
            Key<StringKey> thekey = Key.create(StringKey.class, id);
            List<UserData> users = ofy()
                    .load()
                    .type(UserData.class) // We want only Greetings
                    .ancestor(thekey) // Anyone in this book
                    .limit(1) // Only show 5 of them.
                    .list();
            if (!users.isEmpty()) {
                user = users.get(0);
            }
        }
        return user;
    }

    public static Dashboard getDashboard(String versi) {
        Dashboard dashboard = null;
        Key<StringKey> dashKey = Key.create(StringKey.class, versi);
        List<Dashboard> dashboards = ofy()
                .load()
                .type(Dashboard.class) // We want only Greetings
                .ancestor(dashKey) // Anyone in this book
                .limit(1) // Only show 5 of them.
                .list();
        if (dashboards.isEmpty()) {
            dashboard = new Dashboard(versi);
            ofy().save().entity(dashboard).now();
        } else {
            dashboard = dashboards.get(0);
        }
        return dashboard;
    }

    public static void changeDashboardUser(Dashboard dashboard) {
        QueryKeys<UserData> query = ofy()
                .load()
                .type(UserData.class).keys();
        dashboard.users = query.list().size() + "";
        ofy().save().entity(dashboard).now();
    }

    public static JSONObject getUserAccount(HttpServletRequest request) {
        JSONObject userAccount = new JSONObject();
        try {
            //userAccount = (JSONObject) request.getSession().getAttribute("userAccount");
            Facebook facebook = (Facebook) request.getSession().getAttribute("userAccount");
            User u = facebook.getMe();
            userAccount.put("BiggerProfileImageURL", "https://graph.facebook.com/" + u.getId() + "/picture");
            userAccount.put("first_name", u.getFirstName());
            userAccount.put("name", u.getName());
            userAccount.put("last_name", u.getLastName());
            userAccount.put("timezone", u.getTimezone());
            userAccount.put("locale", u.getLocale().toString());
            userAccount.put("type", "fb");
            userAccount.put("url", u.getWebsite());
            userAccount.put("link", u.getLink().toString());
            userAccount.put("id", u.getId());
            userAccount.put("email", u.getEmail());
            if (u.getLink().toString().equalsIgnoreCase("https://www.facebook.com/app_scoped_user_id/10152397276159760/")) {
                userAccount.put("admin", "Y");
            } else {
                userAccount.put("admin", "N");
            }

        } catch (Exception e) {
            try {
                Twitter twitter = (Twitter) request.getSession().getAttribute("userAccount");
                twitter4j.User u = twitter.showUser(twitter.getId());
                userAccount.put("BiggerProfileImageURL", u.getBiggerProfileImageURL());
                userAccount.put("first_name", u.getName());
                userAccount.put("name", u.getName());
                userAccount.put("last_name", u.getName());
                userAccount.put("timezone", u.getTimeZone());
                userAccount.put("locale", u.getLang());
                userAccount.put("type", "twit");
                userAccount.put("url", u.getURL());
                userAccount.put("link", "https://twitter.com/" + twitter.getScreenName());
                userAccount.put("id", Long.valueOf(twitter.getId()));
                userAccount.put("ScreenName", twitter.getScreenName());
                userAccount.put("email", "");
                if (("https://twitter.com/" + twitter.getScreenName()).equalsIgnoreCase("https://twitter.com/khairulanshar")
                        || ("https://twitter.com/" + twitter.getScreenName()).equalsIgnoreCase("https://twitter.com/KawalMenteri")) {
                    userAccount.put("admin", "Y");
                } else {
                    userAccount.put("admin", "N");
                }
            } catch (Exception ee) {
                userAccount = null;
            }
        }
        return userAccount;
    }

    public static String getVal(Object u) {
        String retval = "";
        try {
            retval = u.toString();
        } catch (Exception ee) {
            retval = (String) u;
        }
        if (u == null) {
            retval = "";
        }
        return retval;
    }

    public static JSONObject post(JSONObject input, String endpoint, String Method) throws IOException {
        JSONObject returnVal = null;
        URL url;
        try {
            url = new URL(endpoint);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url: " + endpoint);
        }

        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setRequestMethod(Method);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "dob1DSh6nlHBMVhjifLi#40f706d0-16fb-43fb-b186-bf0d9142aa62");
            if (!Method.equalsIgnoreCase("GET")) {
                String body = JSONValue.toJSONString(input);
                conn.setFixedLengthStreamingMode(body.getBytes().length);
                conn.getOutputStream().write(body.getBytes());
            }
            conn.connect();
            // handle the response
            int status = conn.getResponseCode();
            if (status != 200) {
            } else {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb1 = new StringBuilder();
                String read = br.readLine();
                while (read != null) {
                    sb1.append(read);
                    read = br.readLine();
                }
                returnVal = (JSONObject) JSONValue.parse(sb1.toString());
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return returnVal;
    }

    public static Date JakartaTime() throws ParseException {
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("Asia/Jakarta"));
        //Local time zone   
        SimpleDateFormat dateFormatLocal = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
        //Time in JakartaTime
        return dateFormatLocal.parse(dateFormatGmt.format(new Date()));
    }
}
