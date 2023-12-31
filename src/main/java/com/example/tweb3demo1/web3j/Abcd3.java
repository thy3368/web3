package com.example.tweb3demo1.web3j;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.http.HttpService;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Abcd3 {

    public static final String RPC_URL = "https://mainnet.infura.io/v3/9eb78bae70c34116a2b28db3fdb96dd0";//config your endpoint regesiter on infura.io


    public static void main(String[] args) throws ExecutionException, InterruptedException {


        Web3j web3j = Web3j.build(new HttpService(RPC_URL));

        String address = "9eb78bae70c34116a2b28db3fdb96dd0";


        EthGetBalance ethGetBalance = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).sendAsync().get();
        BigInteger balance = ethGetBalance.getBalance();
        System.out.println(balance);


    }


    public static void abc() throws ExecutionException, InterruptedException {
//        Web3j web3j = Web3j.build(new HttpService(ConstantLibs.WEB3_ADDRESS));
        Web3j web3j = Web3j.build(new HttpService("https://ropsten.infura.io/v3/9aa3d95b3bc440fa88ea12eaa4456161"));
        String address = "0x9175F9EcBbddC078e40C5e037AD31F4abf36628a";
        String contract = "0x57E0297510fA155eF165646c208C495E563B3342";

        Function function = new Function("balanceOf", List.of(new Address(address)),  // Solidity Types in smart contract functions
                List.of(new TypeReference<Type>() {
                }));

        String encodedFunction = FunctionEncoder.encode(function);
        org.web3j.protocol.core.methods.response.EthCall response = web3j.ethCall(org.web3j.protocol.core.methods.request.Transaction.createEthCallTransaction(address, contract, encodedFunction), DefaultBlockParameterName.LATEST).sendAsync().get();

        String returnValue = response.getValue(); //返回16进制余额
        returnValue = returnValue.substring(2);
        BigInteger balance = new BigInteger(returnValue, 16);
    }

}
