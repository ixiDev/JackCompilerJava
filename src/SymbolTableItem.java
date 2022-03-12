/**
 * * Author : Abdelmajid ID ALI
 * * On : 06/03/2022
 * * Email :  abdelmajid.idali@gmail.com
 **/
public class SymbolTableItem {
    private String name;
    private String type;
    private Kind kind;
    private int address;

    public SymbolTableItem() {
    }

    public SymbolTableItem(String name, String type, Kind kind, int address) {
        this.name = name;
        this.type = type;
        this.kind = kind;
        this.address = address;
    }

    public SymbolTableItem(String name, String type, Kind kind) {
        this.name = name;
        this.type = type;
        this.kind = kind;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Kind getKind() {
        return kind;
    }

    public void setKind(Kind kind) {
        this.kind = kind;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "SymbolTableItem{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", kind='" + kind + '\'' +
                ", address=" + address +
                '}';
    }

    enum Kind{
        STATIC,FILED,ARG,VAR
    }

}
