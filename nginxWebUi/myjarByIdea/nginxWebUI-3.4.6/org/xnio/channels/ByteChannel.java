package org.xnio.channels;

import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;

public interface ByteChannel extends java.nio.channels.ByteChannel, GatheringByteChannel, ScatteringByteChannel {
}
