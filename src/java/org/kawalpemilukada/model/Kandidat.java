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

/**
 *
 * @author 403036
 */
@Entity
public class Kandidat {

    @Parent public Key<StringKey> key;
    @Id public Long id;
    public String nama;
    public String tahun;
    @Index public String provinsiId;
    @Index public String provinsiNama;
    @Index public String kabkotaId;
    @Index public String kabkotaNama;
    public int perolahan_suara;

    public Kandidat() {
        this.perolahan_suara=0;
    }

    public Kandidat(String id) {
        this();
        this.key = Key.create(StringKey.class, id + "");
    }
}
