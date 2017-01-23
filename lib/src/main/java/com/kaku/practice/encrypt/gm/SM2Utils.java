package com.kaku.practice.encrypt.gm;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Enumeration;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERInteger;
import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DEROutputStream;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Base64;

public class SM2Utils
{
	public static byte[] encrypt(byte[] publicKey, byte[] data)
	{
		if (publicKey == null || publicKey.length == 0)
		{
			return null;
		}

		if (data == null || data.length == 0)
		{
			return null;
		}

		byte[] source = new byte[data.length];
		System.arraycopy(data, 0, source, 0, data.length);

        byte[] formatedPubKey;
        if (publicKey.length == 64){
            //添加一字节标识，用于ECPoint解析
            formatedPubKey = new byte[65];
            formatedPubKey[0] = 0x04;
            System.arraycopy(publicKey,0,formatedPubKey,1,publicKey.length);
        }
        else
            formatedPubKey = publicKey;

		Cipher cipher = new Cipher();
		SM2 sm2 = SM2.Instance();
		ECPoint userKey = sm2.ecc_curve.decodePoint(formatedPubKey);

		ECPoint c1 = cipher.Init_enc(sm2, userKey);
		cipher.Encrypt(source);
		byte[] c3 = new byte[32];
		cipher.Dofinal(c3);

		DERInteger x = new DERInteger(c1.getX().toBigInteger());
		DERInteger y = new DERInteger(c1.getY().toBigInteger());
		DEROctetString derDig = new DEROctetString(c3);
		DEROctetString derEnc = new DEROctetString(source);
		ASN1EncodableVector v = new ASN1EncodableVector();
		v.add(x);
		v.add(y);
		v.add(derDig);
		v.add(derEnc);
		DERSequence seq = new DERSequence(v);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DEROutputStream dos = new DEROutputStream(bos);
        try {
            dos.writeObject(seq);
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

	public static byte[] decrypt(byte[] privateKey, byte[] encryptedData)
	{
		if (privateKey == null || privateKey.length == 0)
		{
			return null;
		}

		if (encryptedData == null || encryptedData.length == 0)
		{
			return null;
		}

		byte[] enc = new byte[encryptedData.length];
		System.arraycopy(encryptedData, 0, enc, 0, encryptedData.length);

		SM2 sm2 = SM2.Instance();
		BigInteger userD = new BigInteger(1, privateKey);

		ByteArrayInputStream bis = new ByteArrayInputStream(enc);
		ASN1InputStream dis = new ASN1InputStream(bis);
        try {
            DERObject derObj = dis.readObject();
            ASN1Sequence asn1 = (ASN1Sequence) derObj;
            DERInteger x = (DERInteger) asn1.getObjectAt(0);
            DERInteger y = (DERInteger) asn1.getObjectAt(1);
            ECPoint c1 = sm2.ecc_curve.createPoint(x.getValue(), y.getValue(), true);

            Cipher cipher = new Cipher();
            cipher.Init_dec(userD, c1);
            DEROctetString data = (DEROctetString) asn1.getObjectAt(3);
            enc = data.getOctets();
            cipher.Decrypt(enc);
            byte[] c3 = new byte[32];
            cipher.Dofinal(c3);
            return enc;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 使用默认ID计算
     * @param privateKey
     * @param sourceData
     * @return
     */
    public static byte[] sign(byte[] privateKey, byte[] sourceData){
        String userId = "1234567812345678";
        return sign(userId.getBytes(), privateKey, sourceData);
    }
	public static byte[] sign(byte[] userId, byte[] privateKey, byte[] sourceData)
	{
		if (privateKey == null || privateKey.length == 0)
		{
			return null;
		}

		if (sourceData == null || sourceData.length == 0)
		{
			return null;
		}

		SM2 sm2 = SM2.Instance();
		BigInteger userD = new BigInteger(privateKey);
		System.out.println("userD: " + userD.toString(16));
		System.out.println("");

		ECPoint userKey = sm2.ecc_point_g.multiply(userD);
		System.out.println("椭圆曲线点X: " + userKey.getX().toBigInteger().toString(16));
		System.out.println("椭圆曲线点Y: " + userKey.getY().toBigInteger().toString(16));
		System.out.println("");

		SM3Digest sm3 = new SM3Digest();
		byte[] z = sm2.sm2GetZ(userId, userKey);
		System.out.println("SM3摘要Z: " + Util.getHexString(z));
	    System.out.println("");

	    System.out.println("M: " + Util.getHexString(sourceData));
		System.out.println("");

		sm3.update(z, 0, z.length);
	    sm3.update(sourceData, 0, sourceData.length);
	    byte[] md = new byte[32];
	    sm3.doFinal(md, 0);

	    System.out.println("SM3摘要值: " + Util.getHexString(md));
	    System.out.println("");

	    SM2Result sm2Result = new SM2Result();
	    sm2.sm2Sign(md, userD, userKey, sm2Result);
	    System.out.println("r: " + sm2Result.r.toString(16));
	    System.out.println("s: " + sm2Result.s.toString(16));
	    System.out.println("");

	    DERInteger d_r = new DERInteger(sm2Result.r);
	    DERInteger d_s = new DERInteger(sm2Result.s);
	    ASN1EncodableVector v2 = new ASN1EncodableVector();
	    v2.add(d_r);
	    v2.add(d_s);
	    DERObject sign = new DERSequence(v2);
        return sign.getDEREncoded();
	}

    /**
     * 使用默认id计算
     * @param publicKey
     * @param sourceData
     * @param signData
     * @return
     */
    public static boolean verifySign(byte[] publicKey, byte[] sourceData, byte[] signData){
        String userId = "1234567812345678";
        return verifySign(userId.getBytes(),publicKey,sourceData,signData);
    }
	@SuppressWarnings("unchecked")
	public static boolean verifySign(byte[] userId, byte[] publicKey, byte[] sourceData, byte[] signData)
	{
		if (publicKey == null || publicKey.length == 0)
		{
			return false;
		}

		if (sourceData == null || sourceData.length == 0)
		{
			return false;
		}

        byte[] formatedPubKey;
        if (publicKey.length == 64){
            //添加一字节标识，用于ECPoint解析
            formatedPubKey = new byte[65];
            formatedPubKey[0] = 0x04;
            System.arraycopy(publicKey,0,formatedPubKey,1,publicKey.length);
        }
        else
            formatedPubKey = publicKey;

		SM2 sm2 = SM2.Instance();
		ECPoint userKey = sm2.ecc_curve.decodePoint(formatedPubKey);

		SM3Digest sm3 = new SM3Digest();
		byte[] z = sm2.sm2GetZ(userId, userKey);
		sm3.update(z, 0, z.length);
		sm3.update(sourceData, 0, sourceData.length);
	    byte[] md = new byte[32];
	    sm3.doFinal(md, 0);
	    System.out.println("SM3摘要值: " + Util.getHexString(md));
	    System.out.println("");

	    ByteArrayInputStream bis = new ByteArrayInputStream(signData);
	    ASN1InputStream dis = new ASN1InputStream(bis);
		SM2Result sm2Result = null;
		try {
			DERObject derObj = dis.readObject();
			Enumeration<DERInteger> e = ((ASN1Sequence) derObj).getObjects();
			BigInteger r = ((DERInteger)e.nextElement()).getValue();
			BigInteger s = ((DERInteger)e.nextElement()).getValue();
			sm2Result = new SM2Result();
			sm2Result.r = r;
			sm2Result.s = s;
			System.out.println("r: " + sm2Result.r.toString(16));
			System.out.println("s: " + sm2Result.s.toString(16));
			System.out.println("");
			sm2.sm2Verify(md, userKey, sm2Result.r, sm2Result.s, sm2Result);
			return sm2Result.r.equals(sm2Result.R);
		} catch (IOException e1) {
			e1.printStackTrace();
            return false;
        }
	}



	public static void main(String[] args) throws Exception
	{
		String plainText = "message digest";
		byte[] sourceData = plainText.getBytes();

		// 国密规范测试私钥
        String prik = "444E6EA3EE0C7E0AAA5EE5C6BBC7A2D8DE3FB3FA990AD470232D07FB445F92D7";
		String prikS = new String(Base64.encode(Util.hexToByte(prik)));
		System.out.println("prikS: " + prikS);
		System.out.println("");

		// 国密规范测试用户ID
		String userId = "yuhonqin@163.com";

		System.out.println("ID: " + Util.getHexString(userId.getBytes()));
		System.out.println("");

		System.out.println("签名: ");
		byte[] c = SM2Utils.sign(userId.getBytes(), Base64.decode(prikS.getBytes()), sourceData);
		System.out.println("sign: " + Util.getHexString(c));
		System.out.println("");

		// 国密规范测试公钥
		String pubk = "2E9173C4DB1DB0B22980DD3235ABF99B787DE8E5C6D08BDBA4503D61EE2B32F0F7083CC46D92DAE72FD0223305D0B44A95D438142C45382B23B2A58122E1F3DF";
		String pubkS = new String(Base64.encode(Util.hexToByte(pubk)));
		System.out.println("pubkS: " + pubkS);
		System.out.println("");

		System.out.println("验签: ");
		boolean vs = SM2Utils.verifySign(userId.getBytes(), Base64.decode(pubkS.getBytes()), sourceData, c);
		System.out.println("验签结果: " + vs);
		System.out.println("");

		System.out.println("加密: ");
		byte[] cipherText = SM2Utils.encrypt(Base64.decode(pubkS.getBytes()), sourceData);
		System.out.println(new String(Base64.encode(cipherText)));
		System.out.println("");

		System.out.println("解密: ");
		plainText = new String(SM2Utils.decrypt(Base64.decode(prikS.getBytes()), cipherText));
		System.out.println(plainText);
	}

    public static Sm2KeyPair generateKeyPair(){
        SM2 sm2 = SM2.Instance();
        AsymmetricCipherKeyPair keypair = sm2.ecc_key_pair_generator.generateKeyPair();
        ECPrivateKeyParameters ecpriv = (ECPrivateKeyParameters) keypair.getPrivate();
        ECPublicKeyParameters ecpub = (ECPublicKeyParameters) keypair.getPublic();

//        System.out.println("私钥: " + ecpriv.getD().toString(16).toUpperCase());
//        System.out.println("公钥: " + ecpub.getQ().getX().toBigInteger().toString(16).toUpperCase() +
//                ecpub.getQ().getY().toBigInteger().toString(16).toUpperCase());

        byte[] priKey = new byte[32];
        byte[] pubKey = new byte[64];

        byte[] bigNumArray = ecpriv.getD().toByteArray();
        System.arraycopy(bigNumArray, bigNumArray[0]==0?1:0, priKey, 0, 32);
        System.arraycopy(ecpub.getQ().getEncoded(), 1, pubKey, 0, 64);

//        System.out.println("私钥bigNumArray: " + Util.getHexString(bigNumArray));
//        System.out.println("私钥: " + Util.getHexString(priKey));
//        System.out.println("公钥: " + Util.getHexString(pubKey));

        return new Sm2KeyPair(priKey, pubKey);
    }

    public static void main(){
        String plainText = "Hello SM !";
        byte[] sourceData = plainText.getBytes();

        // 国密规范测试私钥
        String prik = "444E6EA3EE0C7E0AAA5EE5C6BBC7A2D8DE3FB3FA990AD470232D07FB445F92D7";

        byte[] c = SM2Utils.sign(Util.hexToByte(prik), sourceData);
        System.out.println("sign: " + Util.getHexString(c));

        // 国密规范测试公钥
        String pubk = "2E9173C4DB1DB0B22980DD3235ABF99B787DE8E5C6D08BDBA4503D61EE2B32F0F7083CC46D92DAE72FD0223305D0B44A95D438142C45382B23B2A58122E1F3DF";
        boolean vs = SM2Utils.verifySign(Util.hexToByte(pubk), sourceData, c);
        System.out.println("验签结果: " + vs);

        System.out.println("加密: ");
        byte[] cipherText = SM2Utils.encrypt(Util.hexToByte(pubk), sourceData);
        System.out.println(Util.getHexString(cipherText));

        System.out.println("解密: ");
        plainText = new String(SM2Utils.decrypt(Util.hexToByte(prik), cipherText));
        System.out.println(plainText);
    }

    public static void Sm2Test(){
        String plainText = "Hello SM !";
        byte[] sourceData = plainText.getBytes();
        Sm2KeyPair keyPair = generateKeyPair();

        System.out.println("私钥: " + Util.getHexString(keyPair.getPriKey()));
        System.out.println("公钥: " + Util.getHexString(keyPair.getPubKey()));

        byte[] c = SM2Utils.sign(keyPair.getPriKey(), sourceData);
        System.out.println("sign: " + Util.getHexString(c));

        boolean vs = SM2Utils.verifySign(keyPair.getPubKey(), sourceData, c);
        System.out.println("验签结果: " + vs);

        System.out.println("加密: ");
        byte[] cipherText = SM2Utils.encrypt(keyPair.getPubKey(), sourceData);
        System.out.println(Util.getHexString(cipherText));

        System.out.println("解密: ");
        plainText = new String(SM2Utils.decrypt(keyPair.getPriKey(), cipherText));
        System.out.println(plainText);
    }
}
