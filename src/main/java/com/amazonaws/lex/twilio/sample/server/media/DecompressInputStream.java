package com.amazonaws.lex.twilio.sample.server.media;


import com.google.common.io.ByteStreams;

import java.io.ByteArrayInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

/**
 * referenced from https://thorntonzone.com/manuals/Compression/Fax,%20IBM%20MMR/MMSC/mmsc/uk/co/mmscomputing/sound/DecompressInputStream.java
 * <p>
 * Do not use this class in production
 */
public class DecompressInputStream extends FilterInputStream {

  /*
    Convert A-Law or u-Law byte stream into mono PCM byte stream

    static AudioFormat alawformat= new AudioFormat(AudioFormat.Encoding.ALAW,8000,8,1,1,8000,false);
    static AudioFormat ulawformat= new AudioFormat(AudioFormat.Encoding.ULAW,8000,8,1,1,8000,false);

    PCM 8000.0 Hz, 16 bit, mono, SIGNED, little-endian
    static AudioFormat pcmformat = new AudioFormat(8000,16,1,true,false);

  */

  /*
  Mathematical Tools in Signal Processing with C++ and Java Simulations
  by Willi-Hans Steeb International School for Scientific Computing
  */

    private static int[] alawtable = {
            0x80ea, 0x80eb, 0x80e8, 0x80e9, 0x80ee, 0x80ef, 0x80ec, 0x80ed,
            0x80e2, 0x80e3, 0x80e0, 0x80e1, 0x80e6, 0x80e7, 0x80e4, 0x80e5,
            0x40f5, 0xc0f5, 0x40f4, 0xc0f4, 0x40f7, 0xc0f7, 0x40f6, 0xc0f6,
            0x40f1, 0xc0f1, 0x40f0, 0xc0f0, 0x40f3, 0xc0f3, 0x40f2, 0xc0f2,
            0x00aa, 0x00ae, 0x00a2, 0x00a6, 0x00ba, 0x00be, 0x00b2, 0x00b6,
            0x008a, 0x008e, 0x0082, 0x0086, 0x009a, 0x009e, 0x0092, 0x0096,
            0x00d5, 0x00d7, 0x00d1, 0x00d3, 0x00dd, 0x00df, 0x00d9, 0x00db,
            0x00c5, 0x00c7, 0x00c1, 0x00c3, 0x00cd, 0x00cf, 0x00c9, 0x00cb,
            0xa8fe, 0xb8fe, 0x88fe, 0x98fe, 0xe8fe, 0xf8fe, 0xc8fe, 0xd8fe,
            0x28fe, 0x38fe, 0x08fe, 0x18fe, 0x68fe, 0x78fe, 0x48fe, 0x58fe,
            0xa8ff, 0xb8ff, 0x88ff, 0x98ff, 0xe8ff, 0xf8ff, 0xc8ff, 0xd8ff,
            0x28ff, 0x38ff, 0x08ff, 0x18ff, 0x68ff, 0x78ff, 0x48ff, 0x58ff,
            0xa0fa, 0xe0fa, 0x20fa, 0x60fa, 0xa0fb, 0xe0fb, 0x20fb, 0x60fb,
            0xa0f8, 0xe0f8, 0x20f8, 0x60f8, 0xa0f9, 0xe0f9, 0x20f9, 0x60f9,
            0x50fd, 0x70fd, 0x10fd, 0x30fd, 0xd0fd, 0xf0fd, 0x90fd, 0xb0fd,
            0x50fc, 0x70fc, 0x10fc, 0x30fc, 0xd0fc, 0xf0fc, 0x90fc, 0xb0fc,
            0x8015, 0x8014, 0x8017, 0x8016, 0x8011, 0x8010, 0x8013, 0x8012,
            0x801d, 0x801c, 0x801f, 0x801e, 0x8019, 0x8018, 0x801b, 0x801a,
            0xc00a, 0x400a, 0xc00b, 0x400b, 0xc008, 0x4008, 0xc009, 0x4009,
            0xc00e, 0x400e, 0xc00f, 0x400f, 0xc00c, 0x400c, 0xc00d, 0x400d,
            0x0056, 0x0052, 0x005e, 0x005a, 0x0046, 0x0042, 0x004e, 0x004a,
            0x0076, 0x0072, 0x007e, 0x007a, 0x0066, 0x0062, 0x006e, 0x006a,
            0x002b, 0x0029, 0x002f, 0x002d, 0x0023, 0x0021, 0x0027, 0x0025,
            0x003b, 0x0039, 0x003f, 0x003d, 0x0033, 0x0031, 0x0037, 0x0035,
            0x5801, 0x4801, 0x7801, 0x6801, 0x1801, 0x0801, 0x3801, 0x2801,
            0xd801, 0xc801, 0xf801, 0xe801, 0x9801, 0x8801, 0xb801, 0xa801,
            0x5800, 0x4800, 0x7800, 0x6800, 0x1800, 0x0800, 0x3800, 0x2800,
            0xd800, 0xc800, 0xf800, 0xe800, 0x9800, 0x8800, 0xb800, 0xa800,
            0x6005, 0x2005, 0xe005, 0xa005, 0x6004, 0x2004, 0xe004, 0xa004,
            0x6007, 0x2007, 0xe007, 0xa007, 0x6006, 0x2006, 0xe006, 0xa006,
            0xb002, 0x9002, 0xf002, 0xd002, 0x3002, 0x1002, 0x7002, 0x5002,
            0xb003, 0x9003, 0xf003, 0xd003, 0x3003, 0x1003, 0x7003, 0x5003,
    };

    private static int[] ulawtable = {
            0x8482, 0x8486, 0x848a, 0x848e, 0x8492, 0x8496, 0x849a, 0x849e,
            0x84a2, 0x84a6, 0x84aa, 0x84ae, 0x84b2, 0x84b6, 0x84ba, 0x84be,
            0x84c1, 0x84c3, 0x84c5, 0x84c7, 0x84c9, 0x84cb, 0x84cd, 0x84cf,
            0x84d1, 0x84d3, 0x84d5, 0x84d7, 0x84d9, 0x84db, 0x84dd, 0x84df,
            0x04e1, 0x04e2, 0x04e3, 0x04e4, 0x04e5, 0x04e6, 0x04e7, 0x04e8,
            0x04e9, 0x04ea, 0x04eb, 0x04ec, 0x04ed, 0x04ee, 0x04ef, 0x04f0,
            0xc4f0, 0x44f1, 0xc4f1, 0x44f2, 0xc4f2, 0x44f3, 0xc4f3, 0x44f4,
            0xc4f4, 0x44f5, 0xc4f5, 0x44f6, 0xc4f6, 0x44f7, 0xc4f7, 0x44f8,
            0xa4f8, 0xe4f8, 0x24f9, 0x64f9, 0xa4f9, 0xe4f9, 0x24fa, 0x64fa,
            0xa4fa, 0xe4fa, 0x24fb, 0x64fb, 0xa4fb, 0xe4fb, 0x24fc, 0x64fc,
            0x94fc, 0xb4fc, 0xd4fc, 0xf4fc, 0x14fd, 0x34fd, 0x54fd, 0x74fd,
            0x94fd, 0xb4fd, 0xd4fd, 0xf4fd, 0x14fe, 0x34fe, 0x54fe, 0x74fe,
            0x8cfe, 0x9cfe, 0xacfe, 0xbcfe, 0xccfe, 0xdcfe, 0xecfe, 0xfcfe,
            0x0cff, 0x1cff, 0x2cff, 0x3cff, 0x4cff, 0x5cff, 0x6cff, 0x7cff,
            0x88ff, 0x90ff, 0x98ff, 0xa0ff, 0xa8ff, 0xb0ff, 0xb8ff, 0xc0ff,
            0xc8ff, 0xd0ff, 0xd8ff, 0xe0ff, 0xe8ff, 0xf0ff, 0xf8ff, 0x0000,
            0x7c7d, 0x7c79, 0x7c75, 0x7c71, 0x7c6d, 0x7c69, 0x7c65, 0x7c61,
            0x7c5d, 0x7c59, 0x7c55, 0x7c51, 0x7c4d, 0x7c49, 0x7c45, 0x7c41,
            0x7c3e, 0x7c3c, 0x7c3a, 0x7c38, 0x7c36, 0x7c34, 0x7c32, 0x7c30,
            0x7c2e, 0x7c2c, 0x7c2a, 0x7c28, 0x7c26, 0x7c24, 0x7c22, 0x7c20,
            0xfc1e, 0xfc1d, 0xfc1c, 0xfc1b, 0xfc1a, 0xfc19, 0xfc18, 0xfc17,
            0xfc16, 0xfc15, 0xfc14, 0xfc13, 0xfc12, 0xfc11, 0xfc10, 0xfc0f,
            0x3c0f, 0xbc0e, 0x3c0e, 0xbc0d, 0x3c0d, 0xbc0c, 0x3c0c, 0xbc0b,
            0x3c0b, 0xbc0a, 0x3c0a, 0xbc09, 0x3c09, 0xbc08, 0x3c08, 0xbc07,
            0x5c07, 0x1c07, 0xdc06, 0x9c06, 0x5c06, 0x1c06, 0xdc05, 0x9c05,
            0x5c05, 0x1c05, 0xdc04, 0x9c04, 0x5c04, 0x1c04, 0xdc03, 0x9c03,
            0x6c03, 0x4c03, 0x2c03, 0x0c03, 0xec02, 0xcc02, 0xac02, 0x8c02,
            0x6c02, 0x4c02, 0x2c02, 0x0c02, 0xec01, 0xcc01, 0xac01, 0x8c01,
            0x7401, 0x6401, 0x5401, 0x4401, 0x3401, 0x2401, 0x1401, 0x0401,
            0xf400, 0xe400, 0xd400, 0xc400, 0xb400, 0xa400, 0x9400, 0x8400,
            0x7800, 0x7000, 0x6800, 0x6000, 0x5800, 0x5000, 0x4800, 0x4000,
            0x3800, 0x3000, 0x2800, 0x2000, 0x1800, 0x1000, 0x0800, 0x0000,
    };

    private int[] table;

    public DecompressInputStream(InputStream in, boolean useALaw) {
        super(in);
        table = (useALaw) ? alawtable : ulawtable;
    }

    public int read() throws IOException {
        throw new IOException(getClass().getName() + ".read() :\n\tDo not support simple read().");
    }

    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    public int read(byte[] b, int off, int len) throws IOException {
        byte[] inb;
        int value;

        inb = new byte[len >> 1]; // get A-Law or u-Law bytes
        len = in.read(inb);
        if (len == -1) {
            return -1;
        }

        for (int i = 0; i < len; i++) {
            value = table[inb[i] & 0x00FF];
            b[off++] = (byte) ((value >> 8) & 0x00FF);      // little-endian
            b[off++] = (byte) (value & 0x00FF);
        }
        return len << 1;
    }

    public static byte[] decompressULawBytes(byte[] bytes) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        try {
            return ByteStreams.toByteArray(new DecompressInputStream(byteArrayInputStream, false));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
