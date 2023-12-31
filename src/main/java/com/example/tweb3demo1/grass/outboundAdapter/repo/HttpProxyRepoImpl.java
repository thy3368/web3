package com.example.tweb3demo1.grass.outboundAdapter.repo;

import com.example.tweb3demo1.grass.domain.HttpProxy;
import com.example.tweb3demo1.grass.domain.repo.HttpProxyRepo;

import java.util.HashMap;
import java.util.Map;

public class HttpProxyRepoImpl implements HttpProxyRepo {

    private final Map<String, HttpProxy> httpProxyMap = new HashMap<>();

    public HttpProxyRepoImpl() {


        HttpProxy proxy = new HttpProxy("38.170.102.53", 9847, "d85Ewe", "DBJ60N");
        HttpProxy proxy2 = new HttpProxy("122.115.73.41", 65443, null, null);


        this.save(proxy);
        this.save(proxy2);


    }

    public void save(HttpProxy proxy) {

        httpProxyMap.put(proxy.getIp(), proxy);

    }

    public HttpProxy query(HttpProxy proxy) {

        return httpProxyMap.get(proxy.getIp());
    }


}
