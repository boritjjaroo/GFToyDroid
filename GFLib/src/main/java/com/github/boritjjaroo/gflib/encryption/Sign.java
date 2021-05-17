package com.github.boritjjaroo.gflib.encryption;

import org.apache.commons.codec.binary.Hex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.spec.SecretKeySpec;

public class Sign {
    private byte[] key;

    private char[] checkHeader;

    private MessageDigest md5;

    private String sign;

    public Sign(final String sign) {
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new UncheckedIOException(new IOException(e));
        }

        this.sign = sign;

        final byte[] checkHeaderBase = new byte[16];
        final byte[] keyBase = new byte[16];

        try {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(32);
            final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(byteArrayOutputStream);
            outputStreamWriter.write(Hex.encodeHex(
                    md5.digest(sign.getBytes())
            ));
            outputStreamWriter.flush();
            outputStreamWriter.close();
            byteArrayOutputStream.flush();
            final ByteBuffer signHashHex = ByteBuffer.wrap(byteArrayOutputStream.toByteArray());
            outputStreamWriter.close();
            signHashHex.get(checkHeaderBase);
            signHashHex.get(keyBase);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        checkHeader = Hex.encodeHex(
                md5.digest(
                        checkHeaderBase
                )
        );

        try {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(byteArrayOutputStream);
            outputStreamWriter.write(Hex.encodeHex(
                    md5.digest(
                            keyBase
                    )
            ));
            outputStreamWriter.flush();
            byteArrayOutputStream.flush();
            outputStreamWriter.write(Hex.encodeHex(
                    md5.digest(
                            byteArrayOutputStream.toByteArray()
                    )
            ));
            outputStreamWriter.flush();
            outputStreamWriter.close();
            byteArrayOutputStream.flush();
            key = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.close();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public byte[] getKey() {
        return this.key;
    }

    public char[] getCheckHeader() {
        return this.checkHeader;
    }

    public SecretKeySpec getSecretKeySpec() {
        return new SecretKeySpec(
                key,
                0,
                key.length,
                "RC4"
        );
    }

    public byte[] generateCheck(byte[] bytes, int offset, int length) throws IOException{
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        output.write(bytes, offset, length);
        final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(output);
        outputStreamWriter.write(checkHeader);
        outputStreamWriter.flush();
        output.flush();
        byte[] outputBytes = output.toByteArray();
        output.close();
//        return Hex.encodeHexString(
//                Arrays.copyOf(md5.digest(outputBytes), 8)
//        ).getBytes();
        return new String(Hex.encodeHex(
                Arrays.copyOf(md5.digest(outputBytes), 8)
        )).getBytes();
    }

    @Override
    public String toString() {
        return sign;
    }
}
