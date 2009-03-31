package freenet.client;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.onionnetworks.fec.FECCode;
import com.onionnetworks.fec.FECCodeFactory;
import com.onionnetworks.fec.FECMath;
import com.onionnetworks.fec.PureCode;
import com.onionnetworks.util.Buffer;
import com.onionnetworks.util.Util;

public class CodeTest extends TestCase {

	public static FECMath fecMath = new FECMath(8);

	public static final int KK = 192;
	public static final int PACKET_SIZE = 4096;

	/**
	 * Creates k packets of size sz of random data, encodes them, and tries to decode. Index
	 * contains the permutation entry.
	 */
	private static final void encodeDecode(FECCode encode, FECCode decode, int index[]) {
		byte[] src = new byte[KK * PACKET_SIZE];
		Util.rand.nextBytes(src);
		Buffer[] srcBufs = new Buffer[KK];
		for (int i = 0; i < srcBufs.length; i++)
			srcBufs[i] = new Buffer(src, i * PACKET_SIZE, PACKET_SIZE);

		byte[] repair = new byte[KK * PACKET_SIZE];
		Buffer[] repairBufs = new Buffer[KK];
		for (int i = 0; i < repairBufs.length; i++) {
			repairBufs[i] = new Buffer(repair, i * PACKET_SIZE, PACKET_SIZE);
		}

		encode.encode(srcBufs, repairBufs, index);
		decode.decode(repairBufs, index);

		for (int i = 0; i < src.length; i++)
			Assert.assertEquals(src[i], repair[i]);
	}

	public void testSimpleRev() {
		int lim = fecMath.gfSize + 1;
		FECCode code = FECCodeFactory.getDefault().createFECCode(KK, lim);
		FECCode code2 = new PureCode(KK, lim);
		int[] index = new int[KK];

		for (int i = 0; i < KK; i++)
			index[i] = KK - i;
		encodeDecode(code, code2, index);
		encodeDecode(code2, code, index);
	}

	public void testSimple() {
		int lim = fecMath.gfSize + 1;
		FECCode code = FECCodeFactory.getDefault().createFECCode(KK, lim);
		FECCode code2 = new PureCode(KK, lim);
		int[] index = new int[KK];

		for (int i = 0; i < KK; i++)
			index[i] = KK - i;
		encodeDecode(code, code2, index);
		encodeDecode(code2, code, index);
	}

	public void testShifted() {
		int lim = fecMath.gfSize + 1;
		FECCode code = FECCodeFactory.getDefault().createFECCode(KK, lim);
		FECCode code2 = new PureCode(KK, lim);
		int[] index = new int[KK];

		int max_i0 = KK / 2;
		if (max_i0 + KK > lim)
			max_i0 = lim - KK;

		for (int s = 1; s <= max_i0; s++) {
			for (int i = 0; i < KK; i++)
				index[i] = i + s;
			encodeDecode(code, code2, index);
			encodeDecode(code2, code, index);
		}
	}
}
