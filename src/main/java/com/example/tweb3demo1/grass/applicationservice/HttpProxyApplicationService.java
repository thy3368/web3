package com.example.tweb3demo1.grass.applicationservice;

import com.example.tweb3demo1.grass.domain.HttpProxy;
import com.example.tweb3demo1.grass.domain.repo.HttpProxyRepo;
import com.example.tweb3demo1.grass.outboundAdapter.repo.HttpProxyRepoImpl;

public class HttpProxyApplicationService {


    HttpProxyRepo httpProxyRepo = new HttpProxyRepoImpl();

    void save() {

        HttpProxy httpProxy = null;
        httpProxyRepo.save(httpProxy);


    }

    HttpProxy query() {

        HttpProxy httpProxy = new HttpProxy();
        return httpProxyRepo.query(httpProxy);

    }


}
