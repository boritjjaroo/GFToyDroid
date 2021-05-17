package com.github.boritjjaroo.gflib.encryption;

import android.util.Base64;
import android.util.Base64OutputStream;
//import org.apache.commons.codec.binary.Base64OutputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.zip.GZIPOutputStream;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class GfEncryptionOutputStream extends OutputStream implements AutoCloseable{

    private ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    private OutputStream out;

    public GfEncryptionOutputStream(final OutputStream out) {
        this.out = out;
    }

    @Override
    public void write(int b) {
        buffer.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        buffer.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) {
        buffer.write(b, off, len);
    }

    public void finish(final Date date, final Sign sign) throws IOException{
        finish(date, sign, false);
    }

    public void finish(final Date date, final Sign sign, final boolean compress) throws IOException{
        final ByteArrayOutputStream rawData = new ByteArrayOutputStream();
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        rawData.write(String.valueOf(date.getTime()).getBytes(), 0, 10);
        if (compress) {
            final GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
            buffer.flush();
            buffer.writeTo(gzipOutputStream);
            gzipOutputStream.finish();
            gzipOutputStream.flush();
            gzipOutputStream.close();
        } else {
            buffer.writeTo(byteArrayOutputStream);
        }
        byteArrayOutputStream.flush();
        final byte[] data = byteArrayOutputStream.toByteArray();
        byteArrayOutputStream.close();
        rawData.write(sign.generateCheck(data, 0, data.length));
        rawData.write(data);
        final Base64OutputStream base64OutputStream = new Base64OutputStream(out, Base64.DEFAULT);
        try {
            final Cipher rc4 = Cipher.getInstance("RC4");
            rc4.init(
                    Cipher.ENCRYPT_MODE,
                    sign.getSecretKeySpec(),
                    rc4.getParameters()
            );
            rawData.flush();
            base64OutputStream.write(rc4.doFinal(rawData.toByteArray()));
            rawData.close();
            flush();
            base64OutputStream.close();
        } catch (
                NoSuchAlgorithmException
                        | NoSuchPaddingException
                        | InvalidKeyException
                        | InvalidAlgorithmParameterException
                        | IllegalBlockSizeException
                        | BadPaddingException
                        e
        ) {
            throw new IOException(e);
        }
    }

    @Override
    public void close() throws IOException {
        out.close();
        super.close();
    }
}
