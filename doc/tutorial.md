# Tutorial

## A Quick Glance

Muta is a high-performance blockchain framework, aiming to build a high-quality blockchain system. `@nervosnetwork/muta-sdk-js`is an SDK(Software Development Kit) implemented by TypeScript, and used to interact with Muta instance. The runnable demo in this tutorial works well in [NodeJS](https://nodejs.org/en/download/) >= 10 as it is written by JavaScript, and some structures that need to be explained will be described by TypeScript interface. It is highly recommended that editing the demo code in [VS Code](https://code.visualstudio.com/) or [WebStorm](https://www.jetbrains.com/webstorm/), since a modern IDE would be better to help you to auto-complete the code.

## Installation

### gradle

```groovy
repositories {
    maven {
        url  "https://dl.bintray.com/lycrushamster/Muta-Java-SDK"
    }
}

dependencies {
    //please choose the version you want
    api "org.nervos:muta-sdk-java"
}
```

### maven

```
    <repository>
      <id>muta</id>
      <name>Muta-Java-SDK-bintray</name>
      <url>https://dl.bintray.com/lycrushamster/Muta-Java-SDK</url>
      <layout>default</layout>
      <snapshots>
        <enabled>false</enabled>
      </snapshots>
    </repository>

    <dependency>
      <groupId>org.nervos</groupId>
      <artifactId>muta-sdk-java</artifactId>
      <version>choose your version</version>
    </dependency>
   
```


## Scope

- **Account**, manage private key, public key, address and sign transaction.
- **Wallet**, manage mnemonic, and derive account from the master node.
- **Client**, manage GraphqQl communication with server.
- **BatchClient**, manage limited batch GraphQl query.
- **Muta**, Common interfaces for normal usage, providing easy methods, backended by Accoutn and Client. 
- **Service**, A better way to use Muta with pre-defined methods, payloads and auto marshall/unmarshall data.

## Use Mnemonic to Derive Accounts

```
// give 12 phrases to creat a seed
Wallet wallet = Wallet.from_mnemonic( "drastic behave exhaust enough tube judge real logic escape critic horror gold");

// you can get the seed
wallet.getSeed();

// give HD path to derive a 'node', which automatically encapsulated into Accout
// the first param is coin_type, the seconde param is account_index
// here we derive the first of ETH addresses
Account account = wallet.derive(60, 0);

```

## Create Account from Private Key
```
// generate account by a private key in Hex String format
Account acc = Account.fromHexString( "0x45c56be699dca666191ad3446897e0f480da234da896270202514a0e1a587c3f");

// get account's address data in Hex
acc.getAddressHex();
// get account's address in Bech32
acc.getAddress
// get account's private key
acc.getPrivateKeyHex();
// get account's public key
acc.getPublicKeyHex();

// use account's private key to sign digest
byte[] sig = acc.sign(Hex.decode("0000000000000000000000000000000000000000000000000000000000000000"));
```

## Muta to Communicate with server/node via GraphQl grammar

```
// create a Muta instance with a GraphQl url, an account and default option
// "url" is the remote url is for Client to talk to
// "0xprivKey" is the private key for Account, you can explore Account for more details
// MutaRequestOption is some useful param while whole communication, you can use pre-defined one, or customize it by yourself
Muta muta = new Muta(new Client("url"),Account.fromHexString("0xprivKey"),MutaRequestOption.defaultMutaRequestOption() );
```

Muta defines 4 GraphQl query and 1 mutation:

- GetBlock, get the target block by given height, or get the latest block without height.
- GetTransaction, get the sent transaction. 
- GetReceipt, get the receipt of the transaction.
- QueryService, ask node to run a request by certain service, method and payload, and do not modify any state.

- SendTransaction, send a transaction to the node in order to modify the state.


```
// get the block of certain height.
muta.getClient().getBlock(GUint64.fromBigInteger(BigInteger.ONE))

// get the latest height.
muta.getLatestHeight()

// get a transaction by transaction hash
muta.getClient().getTransaction(GHash.fromHexString("0xhex"));

// get a receitp by transaction hash
muta.getClient().getReceipt(GHash.fromHexString("0xhex"));

// send a query
// please refer java doc to get detail info about params
muta.queryService("service", "method", "payload", new TypeReference<String>(){});

// send a tx
// please refer java doc to get detail info about params
muta.sendTransaction();

// you can also use sendTransactionAndPollResult to send a tx and poll its receipt and return when the receipt's ready
muta.sendTransactionAndPollResult()

// you can compose a tx offline,
muta.compose();

// and then sign it also offline,
muta.signTransaction();

// if you are using Multi-Sig, you could combine many signatures together

// sign a tx and append with another signature, this is the best way
muta.appendSignedTransaction

// of course you can sign a tx and get signature and combine them later, or offline.
InputTransactionEncryption.appendSignatureAndPubkey()
```

## Service to Send Elegant Request and Get Checked Response

Service is an easy class to talk to certain Muta service.

Service pre-defines the service name, method names, and hold a lot of payload java objects to mapping with JSON object.
Thus, you can get rid of these tedious and boring things and focus on business.

```
// create a asset service with provided Muta
AssetService assetService = new AssetService(Muta.defaultMuta());

// now you can invoke java method to talk with node, instead of compose GraphQl grammar and HTTP request.
 assetService.createAsset(new CreateAssetPayload())

```