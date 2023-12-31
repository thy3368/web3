package com.example.tweb3demo1.grass.domain.repo;

import com.example.tweb3demo1.grass.domain.HttpProxy;

public interface HttpProxyRepo {

    void save(HttpProxy proxy);

    HttpProxy query(HttpProxy proxy);


}
