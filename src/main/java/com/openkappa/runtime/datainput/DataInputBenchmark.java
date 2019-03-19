package com.openkappa.runtime.datainput;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import sun.misc.Unsafe;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;

import static com.openkappa.runtime.DataUtil.createByteArray;
import static java.util.concurrent.TimeUnit.MICROSECONDS;

@OutputTimeUnit(MICROSECONDS)
public class DataInputBenchmark {


  private static final Unsafe UNSAFE;
  private static final long BYTE_ARRAY_OFFSET;

  static {
    try {
      Field f = Unsafe.class.getDeclaredField("theUnsafe");
      f.setAccessible(true);
      UNSAFE = (Unsafe) f.get(null);
      BYTE_ARRAY_OFFSET = UNSAFE.arrayBaseOffset(byte[].class);
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  private static short getShort(byte[] value, int i) {
    return UNSAFE.getShort(value, BYTE_ARRAY_OFFSET + i);
  }

  private static int getInt(byte[] value, int i) {
    return UNSAFE.getInt(value, BYTE_ARRAY_OFFSET + i);
  }

  private static long getLong(byte[] value, int i) {
    return UNSAFE.getLong(value, BYTE_ARRAY_OFFSET + i);
  }

  @State(Scope.Benchmark)
  public static class ByteBufferState extends ByteArrayState {

    ByteBuffer buffer;

    @Param({"8", "16", "24", "32"})
    int size;

    byte[] data;

    public void createData() {
      this.data = createByteArray(size);
    }

    @Setup(Level.Trial)
    public void init() {
      createData();
      this.buffer = ByteBuffer.wrap(data);
    }

    void reset() {
      buffer.position(0);
    }
  }

  @State(Scope.Benchmark)
  public static class ByteBufferBackedDataInputState extends ByteArrayState {

    ByteBuffer buffer;
    DataInput dataInput;
    @Param({"8", "16", "24", "32"})
    int size;

    byte[] data;

    public void createData() {
      this.data = createByteArray(size);
    }

    @Setup(Level.Trial)
    public void init() {
      createData();
      this.buffer = ByteBuffer.wrap(data);
      this.dataInput = new BufferDataInput(buffer);
    }

    void reset() {
      buffer.position(0);
    }
  }

  @State(Scope.Benchmark)
  public static class StreamBackedDataInpuState extends ByteArrayState {

    ByteArrayInputStream bis;
    DataInput dataInput;
    @Param({"8", "16", "24", "32"})
    int size;

    byte[] data;

    public void createData() {
      this.data = createByteArray(size);
    }

    @Setup(Level.Trial)
    public void init() {
      createData();
      this.bis = new ByteArrayInputStream(data);
      this.dataInput = new DataInputStream(bis);
    }

    void reset() {
      bis.reset();
    }
  }

  @State(Scope.Benchmark)
  public static class UnsafeDataInputState extends ByteArrayState {

    UnsafeDataInput dataInput;
    @Param({"8", "16", "24", "32"})
    int size;

    byte[] data;

    public void createData() {
      this.data = createByteArray(size);
    }

    @Setup(Level.Trial)
    public void init() {
      createData();
      this.dataInput = new UnsafeDataInput(data);
    }

    void reset() {
      dataInput.reset();
    }
  }

  @State(Scope.Benchmark)
  public static class ByteArrayState {

    @Param({"8", "16", "24", "32"})
    int size;

    byte[] data;

    public void createData() {
      this.data = createByteArray(size);
    }

    @Setup(Level.Trial)
    public void init() {
      createData();
    }
  }

  @Benchmark
  public void streamBackedDataInputReadShort(StreamBackedDataInpuState state, Blackhole bh) throws IOException {
    DataInput input = state.dataInput;
    int size = state.size;
    int pos = 0;
    while (pos < size) {
      bh.consume(input.readShort());
      pos += 2;
    }
    state.reset();
  }

  @Benchmark
  public void bufferBackedDataInputReadShort(ByteBufferBackedDataInputState state, Blackhole bh) throws IOException {
    DataInput input = state.dataInput;
    int size = state.size;
    int pos = 0;
    while (pos < size) {
      bh.consume(input.readShort());
      pos += 2;
    }
    state.reset();
  }

  @Benchmark
  public void streamBackedDataInputReadInt(StreamBackedDataInpuState state, Blackhole bh) throws IOException {
    DataInput input = state.dataInput;
    int size = state.size;
    int pos = 0;
    while (pos < size) {
      bh.consume(input.readInt());
      pos += 4;
    }
    state.reset();
  }

  @Benchmark
  public void bufferBackedDataInputReadInt(ByteBufferBackedDataInputState state, Blackhole bh) throws IOException {
    DataInput input = state.dataInput;
    int size = state.size;
    int pos = 0;
    while (pos < size) {
      bh.consume(input.readInt());
      pos += 4;
    }
    state.reset();
  }

  @Benchmark
  public void streamBackedDataInputReadLong(StreamBackedDataInpuState state, Blackhole bh) throws IOException {
    DataInput input = state.dataInput;
    int size = state.size;
    int pos = 0;
    while (pos < size) {
      bh.consume(input.readLong());
      pos += 8;
    }
    state.reset();
  }

  @Benchmark
  public void bufferBackedDataInputReadLong(ByteBufferBackedDataInputState state, Blackhole bh) throws IOException {
    DataInput input = state.dataInput;
    int size = state.size;
    int pos = 0;
    while (pos < size) {
      bh.consume(input.readLong());
      pos += 8;
    }
    state.reset();
  }

  @Benchmark
  public void bufferReadShort(ByteBufferBackedDataInputState state, Blackhole bh) {
    ByteBuffer input = state.buffer;
    int size = state.size;
    int pos = 0;
    while (pos < size) {
      bh.consume(input.getShort());
      pos += 2;
    }
    state.reset();
  }

  @Benchmark
  public void bufferReadInt(ByteBufferBackedDataInputState state, Blackhole bh) {
    ByteBuffer input = state.buffer;
    int size = state.size;
    int pos = 0;
    while (pos < size) {
      bh.consume(input.getInt());
      pos += 4;
    }
    state.reset();
  }

  @Benchmark
  public void bufferReadLong(ByteBufferState state, Blackhole bh) {
    ByteBuffer input = state.buffer;
    int size = state.size;
    int pos = 0;
    while (pos < size) {
      bh.consume(input.getLong());
      pos += 8;
    }
    state.reset();
  }

  @Benchmark
  public void unsafeDataInputReadShort(UnsafeDataInputState state, Blackhole bh) throws IOException {
    DataInput input = state.dataInput;
    int size = state.size;
    int pos = 0;
    while (pos < size) {
      bh.consume(input.readShort());
      pos += 2;
    }
    state.reset();
  }

  @Benchmark
  public void unsafeDataInputReadInt(UnsafeDataInputState state, Blackhole bh) throws IOException {
    DataInput input = state.dataInput;
    int size = state.size;
    int pos = 0;
    while (pos < size) {
      bh.consume(input.readInt());
      pos += 4;
    }
    state.reset();
  }

  @Benchmark
  public void unsafeDataInputReadLong(UnsafeDataInputState state, Blackhole bh) throws IOException {
    DataInput input = state.dataInput;
    int size = state.size;
    int pos = 0;
    while (pos < size) {
      bh.consume(input.readLong());
      pos += 8;
    }
    state.reset();
  }

  @Benchmark
  public void readLongUnsafe(ByteArrayState state, Blackhole bh) {
    byte[] bytes = state.data;
    int size = state.size;
    int pos = 0;
    while (pos < size) {
      bh.consume(getLong(bytes, pos));
      pos += 8;
    }
  }

  @Benchmark
  public void readIntUnsafe(ByteArrayState state, Blackhole bh) {
    byte[] bytes = state.data;
    int size = state.size;
    int pos = 0;
    while (pos < size) {
      bh.consume(getInt(bytes, pos));
      pos += 4;
    }
  }

  @Benchmark
  public void readShortUnsafe(ByteArrayState state, Blackhole bh) {
    byte[] bytes = state.data;
    int size = state.size;
    int pos = 0;
    while (pos < size) {
      bh.consume(getShort(bytes, pos));
      pos += 2;
    }
  }


  @Benchmark
  public void readLong(ByteArrayState state, Blackhole bh) {
    byte[] bytes = state.data;
    int size = state.size;
    int pos = 0;
    while (pos < size) {
      bh.consume((bytes[pos] & 0xFFL) << 56
              | (bytes[pos + 1] & 0xFFL) << 48
              | (bytes[pos + 2] & 0xFFL) << 40
              | (bytes[pos + 3] & 0xFFL) << 32
              | (bytes[pos + 4] & 0xFFL) << 24
              | (bytes[pos + 5] & 0xFFL) << 16
              | (bytes[pos + 6] & 0xFFL) << 8
              | (bytes[pos + 7] & 0xFFL));
      pos += 8;
    }
  }

  @Benchmark
  public void readInt(ByteArrayState state, Blackhole bh) {
    byte[] bytes = state.data;
    int size = state.size;
    int pos = 0;
    while (pos < size) {
      bh.consume(((bytes[pos] & 0xFF) << 24) | ((bytes[pos + 1] & 0xFF) << 16)| ((bytes[pos + 2] & 0xFF) << 8) | (bytes[pos + 3] & 0xFF));
      pos += 4;
    }
  }

  @Benchmark
  public void readShort(ByteArrayState state, Blackhole bh) {
    byte[] bytes = state.data;
    int size = state.size;
    int pos = 0;
    while (pos < size) {
      bh.consume(((bytes[pos] & 0xFF) << 8) | (bytes[pos + 1] & 0xFF));
      pos += 2;
    }
  }

public static class BufferDataInput implements DataInput {

  private final ByteBuffer data;

  public BufferDataInput(ByteBuffer data) {
    this.data = data;
  }

  @Override
  public void readFully(byte[] bytes) {
  }

  @Override
  public void readFully(byte[] bytes, int i, int i1) {
  }

  @Override
  public int skipBytes(int i) {
    data.position(data.position() + i);
    return data.position();
  }

  @Override
  public boolean readBoolean() {
    return data.get() != 0;
  }

  @Override
  public byte readByte() {
    return data.get();
  }

  @Override
  public int readUnsignedByte() {
    return data.get() & 0xFF;
  }

  @Override
  public short readShort() {
    return data.getShort();
  }

  @Override
  public int readUnsignedShort() {
    return data.getShort() & 0xffff;
  }

  @Override
  public char readChar() {
    return data.getChar();
  }

  @Override
  public int readInt() {
    return data.getInt();
  }

  @Override
  public long readLong() {
    return data.getLong();
  }

  @Override
  public float readFloat() {
    return data.getFloat();
  }

  @Override
  public double readDouble() {
    return data.getDouble();
  }

  @Override
  public String readLine() {
    return null;
  }

  @Override
  public String readUTF() {
    return null;
  }
}

public static class UnsafeDataInput implements DataInput {

    private final byte[] data;
    int pos;

  public UnsafeDataInput(byte[] data) {
    this.data = data;
  }

  @Override
  public void readFully(byte[] b) {

  }

  @Override
  public void readFully(byte[] b, int off, int len) {

  }

  @Override
  public int skipBytes(int n) {
    return pos += n;
  }

  @Override
  public boolean readBoolean() {
    return data[pos++] != 0;
  }

  @Override
  public byte readByte() {
    return data[pos++];
  }

  @Override
  public int readUnsignedByte() {
    return data[pos++] & 0xFF;
  }

  @Override
  public short readShort() {
    pos += 2;
    return getShort(data,pos - 2);
  }

  @Override
  public int readUnsignedShort() {
    pos += 2;
    return getShort(data, pos - 2) & 0xFFFF;
  }

  @Override
  public char readChar() {
    return 0;
  }

  @Override
  public int readInt() {
    pos += 4;
    return getInt(data,pos - 4);
  }

  @Override
  public long readLong() {
    pos += 8;
    return getLong(data, pos - 8);
  }

  @Override
  public float readFloat() {
    return 0;
  }

  @Override
  public double readDouble() {
    return 0;
  }

  @Override
  public String readLine() {
    return null;
  }

  @Override
  public String readUTF() {
    return null;
  }

  public void reset() {
    this.pos = 0;
  }
}
}
