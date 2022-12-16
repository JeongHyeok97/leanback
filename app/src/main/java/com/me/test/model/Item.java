package com.me.test.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Item implements Parcelable {

    public final long id;
    public final String category;
    public final int no;
    public final String name;
    public final String desc;
    public final String ver;
    public final int code;
    public final int size;
    public final int date;
    public final String packagename;
    public final String file;

    private Item(
            final long id,
            final int no,
            final String category,
            final String name,
            final String desc,
            final String ver,
            final int code,
            final int size,
            final int date,
            final String packagename,
            final String file
    )

    {
        this.id = id;
        this.no = no;
        this.category = category;
        this.name = name;
        this.desc = desc;
        this.ver = ver;
        this.code = code;
        this.size = size;
        this.date = date;
        this.packagename = packagename;
        this.file = file;
    }


    protected Item(Parcel in) {
        id = in.readLong();
        no = in.readInt();
        category = in.readString();
        name = in.readString();
        desc = in.readString();
        ver = in.readString();
        code = in.readInt();
        size = in.readInt();
        date = in.readInt();
        packagename = in.readString();
        file = in.readString();

    }


    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };
    @Override
    public boolean equals(Object m) {
        return m instanceof Item && id == ((Item) m).id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeInt(no);
        dest.writeString(category);
        dest.writeString(name);
        dest.writeString(desc);
        dest.writeString(ver);
        dest.writeInt(code);
        dest.writeInt(size);
        dest.writeInt(date);
        dest.writeString(packagename);
        dest.writeString(file);

    }

    @NonNull
    @Override
    public String toString() {
        String s = "Items{";
        s += "id=" + id;
        s += ", category=" + category + "'";
        s += ", no=" + no + "'";
        s += ", name='" + name + "'";
        s += ", desc='" + desc + "'";
        s += ", ver='" + ver + "'";
        s += ", code='" + code + "'";
        s += ", size='" + size + "'";
        s += ", date='" + date + "'";
        s += ", packagename='" + packagename + "'";
        s += ", file='" + file + "'";
        s += "}";

        return s;
    }

    public static class ItemBuilder {
        private long id;
        private int no;
        private String category;
        private String name;
        private String desc;
        private String ver;
        private int code;
        private int size;
        private int date;
        private String packagename;
        private String file;

        public ItemBuilder id(long id){
            this.id = id;
            return this;
        }

        public ItemBuilder no(int no) {
            this.no = no;
            return this;
        }

        public ItemBuilder category(String category){
            this.category = category;
            return this;
        }

        public ItemBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ItemBuilder desc(String desc) {
            this.desc = desc;
            return this;
        }

        public ItemBuilder ver(String ver) {
            this.ver = ver;
            return this;
        }

        public ItemBuilder code(int code) {
            this.code = code;
            return this;
        }

        public ItemBuilder size(int size) {
            this.size = size;
            return this;
        }

        public ItemBuilder date(int date) {
            this.date = date;
            return this;
        }

        public ItemBuilder packagename(String packagename) {
            this.packagename = packagename;
            return this;
        }

        public ItemBuilder file(String file) {
            this.file = file;
            return this;
        }

        public Item build(){
            return new Item(
                    id,
                    no,
                    category,
                    name,
                    desc,
                    ver,
                    code,
                    size,
                    date,
                    packagename,
                    file
            );
        }

    }



}
