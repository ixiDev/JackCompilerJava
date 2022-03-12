import java.util.HashMap;

/**
 * * Author : Abdelmajid ID ALI
 * * On : 06/03/2022
 * * Email :  abdelmajid.idali@gmail.com
 **/
public class SymbolTable {


    private final HashMap<String, SymbolTableItem> items = new HashMap<>();

    private int staticCount = 0;
    private int filedCount = 0;
    private int argCount = 0;
    private int varCount = 0;

    //    private int argCount=0;
    public void define(SymbolTableItem item) throws JackSyntaxError {

        switch (item.getKind()) {
            case STATIC:
                item.setAddress(staticCount++);
                break;
            case FILED:
                item.setAddress(filedCount++);
                break;
            case ARG:
                item.setAddress(argCount++);
                break;
            case VAR:
                item.setAddress(varCount++);
                break;
        }
        if (items.containsKey(item.getName())) {
            throw new JackSyntaxError(
                    "Variable " + item.getName() + " already exist"
            );
        }

        items.put(item.getName(), item);
    }

    public void reset() {
        items.clear();
        argCount = 0;
        varCount = 0;
        filedCount = 0;
        staticCount = 0;
    }

    public int varCount(SymbolTableItem.Kind kind) {
        switch (kind) {
            case STATIC:
                return staticCount;
            case FILED:
                return filedCount;
            case ARG:
                return argCount;
            case VAR:
                return varCount;
        }
        return 0;
    }

    public int indexOf(String varName) {
        if (!items.containsKey(varName))
            return -1;
        return items.get(varName).getAddress();
    }

    public SymbolTableItem.Kind kindOf(String varName) throws JackSyntaxError {
        if (!items.containsKey(varName))
            throw new JackSyntaxError("Undefined variable " + varName);
        return items.get(varName).getKind();
    }

    public String typeOf(String varName) throws JackSyntaxError {
        if (!items.containsKey(varName))
            throw new JackSyntaxError("Undefined variable " + varName);
        return items.get(varName).getType();
    }

    public SymbolTableItem getByName(String identifier) {
        return items.getOrDefault(identifier,null);
    }
    public boolean contains(String identifier) {
        return items.containsKey(identifier);
    }



}
