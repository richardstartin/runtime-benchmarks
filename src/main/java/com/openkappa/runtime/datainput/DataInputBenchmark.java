package com.openkappa.runtime.datainput;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import static com.openkappa.runtime.DataUtil.createByteArray;
import static java.util.concurrent.TimeUnit.MICROSECONDS;

@OutputTimeUnit(MICROSECONDS)
public class DataInputBenchmark {


  public static abstract class ByteArrayState {

    abstract void reset();
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

    @Override
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

    @Override
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

    @Override
    void reset() {
      bis.reset();
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
}
