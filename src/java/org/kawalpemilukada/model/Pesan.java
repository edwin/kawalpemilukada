/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kawalpemilukada.model;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Parent;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.kawalpemilukada.web.controller.CommonServices;

/**
 *
 * @author khairulanshar
 */
@Entity
public class Pesan {
    /*key nya bisa [wall,msguserid,alertuserid]*/
    @Parent public Key<StringKey> key;
    @Id public Long id;
    @Index public String dari_id;
    public String dari_nama;
    public String dari_img;
    public String dari_link;
    @Index public String untuk_id;
    public String untuk_nama;
    public String untuk_img;
    public String untuk_link;
    @Index public String setujutidaksetuju;
    public String msg;
    public List<String> files = new ArrayList<>();
    public String icon;
    @Index public Date creationDate;
    @Index public boolean punyaReply;
    public int jumlahSetuju;
    public int jumlahTidakSetuju;
    public int jumlahTanggapan;

    public Pesan() throws ParseException {
        this.creationDate = CommonServices.JakartaTime();
        this.punyaReply = false;
        this.jumlahSetuju = 0;
        this.jumlahTidakSetuju = 0;
        this.jumlahTanggapan = 0;
    }

    public Pesan(String key) throws ParseException {
        this();
        this.key = Key.create(StringKey.class, key);
    }
}
