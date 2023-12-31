package com.example.tweb3demo1.grass.domain.repo;

import com.example.tweb3demo1.grass.domain.GrassConnection;

import java.util.Collection;

public interface ConnectionRepo {

    void save(GrassConnection grassConnection);

    void update(GrassConnection grassConnection);

    GrassConnection query(GrassConnection grassConnection);


    Collection<GrassConnection> queryAll();
}
