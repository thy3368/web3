package com.example.tweb3demo1.web3j;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGasPrice;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;

public class Abcd {

    public static void main(String[] args) {
        /*SpringApplication.run(LongApplication.class, args);
        System.out.printf("启动成功");*/
        // 转出地址
        String from = "0x9175F9EcBbddC078e40C5e037AD31F4abf36628a";
        //转入地址
        String to = "0xa7B049d3A19796B195B0e976ec43EB7a12f07Bf9";
        //转入数量
        String value = "5";
        //转出地址私钥
        String privateKey = "";
        //合约地址
        String contractAddress = "0x57E0297510fA155eF165646c208C495E563B3342";
        //位数，根据合约里面的来
        int decimal = 4;
        tokenDeal(from, to, value, privateKey, contractAddress, decimal);
    }


    public static String tokenDeal(String from, String to, String value, String privateKey, String contractAddress, int decimal) {
        Web3j web3j = Web3j.build(new HttpService("https://ropsten.infura.io/v3/9aa3d95b3bc440fa88ea12eaa4456161"));
        try {
            //转账的凭证，需要传入私钥
            Credentials credentials = Credentials.create(privateKey);
            //获取交易笔数
            BigInteger nonce;
            EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(from, DefaultBlockParameterName.PENDING).send();
            if (ethGetTransactionCount == null) {
                return null;
            }
            nonce = ethGetTransactionCount.getTransactionCount();
            //手续费
            BigInteger gasPrice;
            EthGasPrice ethGasPrice = web3j.ethGasPrice().sendAsync().get();
            if (ethGasPrice == null) {
                return null;
            }
            gasPrice = ethGasPrice.getGasPrice();
            //注意手续费的设置，这块很容易遇到问题
            BigInteger gasLimit = BigInteger.valueOf(60000L);

            BigInteger val = new BigDecimal(value).multiply(new BigDecimal("10").pow(decimal)).toBigInteger();// 单位换算
            Function function = new Function("transfer", Arrays.asList(new Address(to), new Uint256(val)), Collections.singletonList(new TypeReference<Type>() {
            }));
            //创建交易对象
            String encodedFunction = FunctionEncoder.encode(function);
            RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, contractAddress, encodedFunction);

            //进行签名操作
            byte[] signMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
            String hexValue = Numeric.toHexString(signMessage);
            //发起交易
            EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
            String hash = ethSendTransaction.getTransactionHash();
            if (hash != null) {
                //执行业务
                System.out.printf("执行成功：" + hash);
                return hash;
            }
        } catch (Exception ex) {
            //报错应进行错误处理
            ex.printStackTrace();
        }
        return null;
    }
}
