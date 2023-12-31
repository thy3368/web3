package com.example.tweb3demo1.grass.outboundAdapter.repo;

import com.example.tweb3demo1.grass.domain.HttpProxy;
import com.example.tweb3demo1.grass.domain.repo.HttpProxyRepo;

import java.util.HashMap;
import java.util.Map;

public class HttpProxyRepoImpl implements HttpProxyRepo {

    private final Map<String, HttpProxy> httpProxyMap = new HashMap<>();

    public HttpProxyRepoImpl() {


        HttpProxy proxy = new HttpProxy();
        proxy.setIp("38.170.102.53");
        proxy.setPort(9847);
        proxy.setUsername("d85Ewe");
        proxy.setPassword("DBJ60N");


        this.save(proxy);

    }

    public void save(HttpProxy proxy) {

        httpProxyMap.put(proxy.getIp(), proxy);

    }

    public HttpProxy query(HttpProxy proxy) {

        return httpProxyMap.get(proxy.getIp());
    }


}
