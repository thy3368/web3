package com.example.tweb3demo1.web3j;

import com.google.common.collect.ImmutableList;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicHierarchy;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

public class EthereumManager {

    private final static ImmutableList<ChildNumber> BIP44_ETH_ACCOUNT_ZERO_PATH =
            ImmutableList.of(new ChildNumber(44, true), new ChildNumber(60, true),
                    ChildNumber.ZERO_HARDENED, ChildNumber.ZERO);

    private final static Web3j web3j = Web3j.build(new HttpService("localhost:8545"));

    /**
     * 通过助记词和id生成对应的子账户
     * @param mnemonic 助记词
     * @param id 派生子id
     * @return 子账户key
     */
    private static DeterministicKey generateKeyFromMnemonicAndUid(String mnemonic, int id) {
        byte[] seed = MnemonicUtils.generateSeed(mnemonic, "");

        DeterministicKey rootKey = HDKeyDerivation.createMasterPrivateKey(seed);
        DeterministicHierarchy hierarchy = new DeterministicHierarchy(rootKey);

        return hierarchy.deriveChild(BIP44_ETH_ACCOUNT_ZERO_PATH, false, true, new ChildNumber(id, false));
    }

    /**
     * 生成地址
     * @param id 用户id
     * @return 地址
     * @throws CipherException
     */
    public static String getEthAddress(String mnemonic, int id) {
        DeterministicKey deterministicKey = generateKeyFromMnemonicAndUid(mnemonic, id);
        ECKeyPair ecKeyPair = ECKeyPair.create(deterministicKey.getPrivKey());
        return Keys.getAddress(ecKeyPair);
    }

    /**
     * 生成私钥
     * @param id 用户id
     * @return 私钥
     */
    public static BigInteger getPrivateKey(String mnemonic, int id) {
        return generateKeyFromMnemonicAndUid(mnemonic, id).getPrivKey();
    }


    /**
     * 通过private key生成credentials
     */
    public static Credentials generateCredentials(String privateKey) {
        return Credentials.create(privateKey);
    }


    /**
     * 发送eth离线交易
     * @param from eth持有地址
     * @param to 发送目标地址
     * @param amount 金额（单位：eth）
     * @param credentials 秘钥对象
     * @return 交易hash
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static String sendEthRawTransaction(String from, String to, BigDecimal amount, Credentials credentials) throws IOException, ExecutionException, InterruptedException {

        BigInteger nonce = web3j.ethGetTransactionCount(from, DefaultBlockParameterName.PENDING).send().getTransactionCount();
        BigInteger gasPrice = web3j.ethGasPrice().send().getGasPrice();
        BigInteger gasLimit = BigInteger.valueOf(21000L);

        BigInteger amountWei = Convert.toWei(amount, Convert.Unit.ETHER).toBigInteger();

        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, to, amountWei, "");

        byte[] signMessage = TransactionEncoder.signMessage(rawTransaction, credentials);

        return web3j.ethSendRawTransaction(Numeric.toHexString(signMessage)).sendAsync().get().getTransactionHash();
    }

    /**
     * 发送代币离线交易
     * @param from 代币持有地址
     * @param to 代币目标地址
     * @param amount 金额（单位：代币最小单位）
     * @param coinAddress 代币合约地址
     * @param credentials 秘钥对象
     * @return 交易hash
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static String sendContractTransaction(String from, String to, BigInteger gasLimit, BigInteger amount, String coinAddress, Credentials credentials) throws IOException, ExecutionException, InterruptedException {
        BigInteger nonce = web3j.ethGetTransactionCount(from, DefaultBlockParameterName.PENDING).send().getTransactionCount();
        BigInteger gasPrice = web3j.ethGasPrice().send().getGasPrice();

        Function function = new Function(
                "transfer",
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(to),
                        new org.web3j.abi.datatypes.generated.Uint256(amount)),
                Collections.<TypeReference<?>>emptyList());
        String data = FunctionEncoder.encode(function);

        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, coinAddress, data);
        byte[] signMessage = TransactionEncoder.signMessage(rawTransaction, credentials);

        return web3j.ethSendRawTransaction(Numeric.toHexString(signMessage)).sendAsync().get().getTransactionHash();
    }

    /**
     * 发送账户内所有eth
     * @param from 持有地址
     * @param to 目标地址
     * @param credentials 秘钥对象
     * @return 交易hash
     * @throws IOException
     */
    public static String sendAllEth(String from, String to, Credentials credentials) throws IOException {

        BigInteger nonce = web3j.ethGetTransactionCount(from, DefaultBlockParameterName.PENDING).send().getTransactionCount();
        BigInteger gasPrice = web3j.ethGasPrice().send().getGasPrice();
        BigInteger gasLimit = BigInteger.valueOf(21000L);
        BigInteger balance = web3j.ethGetBalance(from, DefaultBlockParameterName.PENDING).send().getBalance();

        if (balance.compareTo(gasPrice.multiply(gasLimit)) <= 0) {
            return null;
        }

        BigInteger amount = balance.subtract(gasPrice.multiply(gasLimit));

        RawTransaction transaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, to, amount, "");
        byte[] signMessage = TransactionEncoder.signMessage(transaction, credentials);
        return web3j.ethSendRawTransaction(Numeric.toHexString(signMessage)).send().getTransactionHash();
    }

    /**
     * 发送账户内所有某代币
     * @param from 代币拥有地址
     * @param to 代币目标地址
     * @param coinAddress 代币合约地址
     * @param gasLimit gas值
     * @param gasPrice gas price
     * @param credentials 秘钥对象
     * @return 交易hash
     * @throws IOException
     */
    public static String sendAllCoin(String from, String to, String coinAddress, BigInteger gasLimit, BigInteger gasPrice, Credentials credentials) throws IOException {
        BigInteger nonce = web3j.ethGetTransactionCount(from, DefaultBlockParameterName.PENDING).send().getTransactionCount();

        BigInteger value = getBalanceOfCoin(from, coinAddress);
        System.out.println(value);

        Function transfer = new Function(
                "transfer",
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(to),
                        new org.web3j.abi.datatypes.generated.Uint256(value)),
                Collections.<TypeReference<?>>emptyList());
        String data = FunctionEncoder.encode(transfer);

        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, coinAddress, data);

        byte[] signMessage = TransactionEncoder.signMessage(rawTransaction, credentials);

        return web3j.ethSendRawTransaction(Numeric.toHexString(signMessage)).send().getTransactionHash();

    }

    /**
     * 获取账户代币余额
     * @param account 账户地址
     * @param coinAddress 代币地址
     * @return 代币余额 （单位：代币最小单位）
     * @throws IOException
     */
    public static BigInteger getBalanceOfCoin(String account, String coinAddress) throws IOException {
        Function balanceOf = new Function("balanceOf",
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(account)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
                }));

        if (coinAddress == null) {
            return null;
        }
        String value = web3j.ethCall(Transaction.createEthCallTransaction(account, coinAddress, FunctionEncoder.encode(balanceOf)), DefaultBlockParameterName.PENDING).send().getValue();
        return new BigInteger(value.substring(2), 16);
    }

    /**
     * 获取合约交易估算gas值
     * @param from 发送者
     * @param to 发送目标地址
     * @param coinAddress 代币地址
     * @param value 发送金额（单位：代币最小单位）
     * @return 估算的gas limit
     * @throws IOException
     */
    public static BigInteger getTransactionGasLimit(String from, String to, String coinAddress, BigInteger value) throws IOException {
        Function transfer = new Function(
                "transfer",
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(to),
                        new org.web3j.abi.datatypes.generated.Uint256(value)),
                Collections.<TypeReference<?>>emptyList());
        String data = FunctionEncoder.encode(transfer);
        return web3j.ethEstimateGas(new Transaction(from, null, null, null, coinAddress, BigInteger.ZERO, data)).send().getAmountUsed();
    }

}
