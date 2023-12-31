package com.example.tweb3demo1.grass.outboundAdapter.repo;

import com.example.tweb3demo1.grass.domain.GrassConnection;
import com.example.tweb3demo1.grass.domain.repo.ConnectionRepo;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ConnectionRepoImpl implements ConnectionRepo {


    private final Map<String, GrassConnection> map = new HashMap<>();

    public ConnectionRepoImpl() {

        GrassConnection connection = new GrassConnection();

        connection.setUserId("30901ea9-bf85-43b0-954c-206450000284");
        connection.setIp("38.170.102.53");

        this.save(connection);


    }

    @Override
    public void save(GrassConnection grassConnection) {
        map.put(grassConnection.getUserId() + grassConnection.getIp(), grassConnection);
    }

    @Override
    public void update(GrassConnection grassConnection) {
        map.put(grassConnection.getUserId() + grassConnection.getIp(), grassConnection);
    }

    @Override
    public GrassConnection query(GrassConnection grassConnection) {
        GrassConnection queryGrassConnection = map.get(grassConnection.getUserId() + grassConnection.getIp());
        return queryGrassConnection;
    }

    @Override
    public Collection<GrassConnection> queryAll() {
        return map.values();

    }
}
