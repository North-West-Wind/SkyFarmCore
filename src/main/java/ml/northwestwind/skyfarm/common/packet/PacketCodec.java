package ml.northwestwind.skyfarm.common.packet;

import java.io.*;

public class PacketCodec {
    public static byte[] encode(IPacket o) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            ObjectOutputStream out;
            out = new ObjectOutputStream(bos);
            out.writeObject(o);
            out.flush();
            byte[] bytes = bos.toByteArray();
            return bytes;
        } catch (IOException ignored) { }
        return new byte[0];
    }

    public static IPacket decode(byte[] bytes) {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        try (ObjectInput in = new ObjectInputStream(bis)) {
            Object o = in.readObject();
            return (IPacket) o;
        } catch (IOException | ClassNotFoundException ignored) { }
        return null;
    }
}
