public static void int8_to_buf(byte[] buf, int pos, int value) {
		buf[(pos + 0)] = (byte) ((value >> 0) & 0xFF);
	}

	public static void int16_to_buf(byte[] buf, int pos, int value) {
		buf[(pos + 1)] = (byte) ((value >> 0) & 0xFF);
		buf[(pos + 0)] = (byte) ((value >> 8) & 0xFF);
	}

	public static void int32_to_buf(byte[] buf, int pos, int value) {
		buf[(pos + 3)] = (byte) ((value >> 0) & 0xFF);
		buf[(pos + 2)] = (byte) ((value >> 8) & 0xFF);
		buf[(pos + 1)] = (byte) ((value >> 16) & 0xFF);
		buf[(pos + 0)] = (byte) ((value >> 24) & 0xFF);
	}

	public static void int64_to_buf(byte[] buf, int pos, long value) {
		buf[(pos + 7)] = (byte) (int) ((value >> 0) & 0xFF);
		buf[(pos + 6)] = (byte) (int) ((value >> 8) & 0xFF);
		buf[(pos + 5)] = (byte) (int) ((value >> 16) & 0xFF);
		buf[(pos + 4)] = (byte) (int) ((value >> 24) & 0xFF);
		buf[(pos + 3)] = (byte) (int) ((value >> 32) & 0xFF);
		buf[(pos + 2)] = (byte) (int) ((value >> 40) & 0xFF);
		buf[(pos + 1)] = (byte) (int) ((value >> 48) & 0xFF);
		buf[(pos + 0)] = (byte) (int) ((value >> 56) & 0xFF);
	}

	public static void int64_to_buf32(byte[] buf, int pos, long value) {
		buf[(pos + 3)] = (byte) (int) ((value >> 0) & 0xFF);
		buf[(pos + 2)] = (byte) (int) ((value >> 8) & 0xFF);
		buf[(pos + 1)] = (byte) (int) ((value >> 16) & 0xFF);
		buf[(pos + 0)] = (byte) (int) ((value >> 24) & 0xFF);
	}

	public static int buf_to_int8(byte[] buf, int pos) {
		return buf[pos] & 0xFF;
	}

	public static int buf_to_int16(byte[] buf, int pos) {
		return ((buf[pos] << 8) & 0xFF00) | ((buf[(pos + 1)] << 0) & 0xFF);
	}

	public static int buf_to_int32(byte[] buf, int pos) {
		return ((buf[pos] << 24) & 0xFF000000)
				| ((buf[(pos + 1)] << 16) & 0xFF0000)
				| ((buf[(pos + 2)] << 8) & 0xFF00)
				| ((buf[(pos + 3)] << 0) & 0xFF);
	}

	public static long buf_to_int64(byte[] buf, int pos) {
		return (((long) buf[pos] << 56) & 0xFF00000000000000L)
			| (((long) buf[(pos + 1)] << 48) & 0xFF000000000000L)
			| (((long) buf[(pos + 2)] << 40) & 0xFF0000000000L)
			| (((long) buf[(pos + 3)] << 32) & 0xFF00000000L)
			| (((long) buf[(pos + 4)] << 24) & 0xFF000000L)
			| (((long) buf[(pos + 5)] << 16) & 0xFF0000L)
			| (((long) buf[(pos + 6)] << 8) & 0xFF00L)
			| (((long) buf[(pos + 7)] << 0) & 0xFFL);
	}
