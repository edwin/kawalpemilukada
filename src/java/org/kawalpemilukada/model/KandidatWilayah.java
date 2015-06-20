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
public class KandidatWilayah {

    @Parent public Key<StringKey> key;
    @Id public String id;
    @Index public String kpuid;
    @Index public String nama;
    public int JumlahKandidat;
    public String dikunci;

    public KandidatWilayah() {
        this.JumlahKandidat=0;
        this.dikunci="Tidak";
    }

    public KandidatWilayah(String id) {
        this();
        this.key = Key.create(StringKey.class, id + "");
    }
}
