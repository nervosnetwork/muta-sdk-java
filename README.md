# Muta SDK

The Java SDK for [Muta](https://github.com/nervosnetwork/muta)(a High performance Blockchain framework). Allow you to interact with Muta node's GraphQL service.

## Quick Start

## Installation

The maven artifact will be soon on jcenter.

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

## Usage

```java

Muta muta = new Muta(new Client("url"),Account.fromHexString("0xprivKey"),MutaRequestOption.defaultMutaRequestOption() );

```

## Useful Classes

There are several classes you may interest on as follows.

- [Muta](./src/main/java/org/nervos/muta/Muta.java) - All-in-one SDK for Muta framework
- [Client](./src/main/java/org/nervos/muta/client/Client.java) - Wrapping the GraphQL like RPC.
- [Batch](./src/main/java/org/nervos/muta/client/batch/BatchClient.java) - BatchClient for only queries.
- [wallet/Wallet](./src/main/java/org/nervos/muta/wallet/Wallet.java) - BIP44 wallet
- [walletAccount](./src/main/java/org/nervos/muta/wallet/Account.java) - Account for managing private key and signing
- [util](./src/main/java/org/nervos/muta/util) - Several utilities
- [service](./src/main/java/org/nervos/muta/service) - Each service contain pre-defined method name and payload data structure

## Links

- [Tutorial](./doc/tutorial.md) - A quick start
- [Examples](./examples) - The examples code
- [API doc](https://nervosnetwork.github.io/muta-sdk-java/) - The api doc


## Development

- java version >= 1.8
