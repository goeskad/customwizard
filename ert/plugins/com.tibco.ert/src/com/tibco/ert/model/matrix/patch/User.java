package com.tibco.ert.model.matrix.patch;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class User implements Externalizable {
    private int id;

    private String name;

    private byte[] password;

    private byte[] salt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getPassword() {
        return password;
    }

    public void setPassword(byte[] password) {
        this.password = password;
    }

    public byte[] getSalt() {
        return salt;
    }

    public void setSalt(byte[] salt) {
        this.salt = salt;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        name = in.readUTF();
        int len = in.readInt();
        password = new byte[len];
        in.read(password, 0, len);
        len = in.readInt();
        salt = new byte[len];
        in.read(salt, 0, len);
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(name);
        out.writeInt(password.length);
        out.write(password, 0, password.length);
        out.writeInt(salt.length);
        out.write(salt, 0, salt.length);
    }
}
