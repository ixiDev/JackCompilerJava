import java.io.File;
import java.io.IOException;

/**
 * * Author : Abdelmajid ID ALI
 * * On : 08/03/2022
 * * Email :  abdelmajid.idali@gmail.com
 **/
public class VMCompileEngine {

    private final VMWriter vmWriter;
    private final JackParser parser;
    private final JackAnalyzer analyzer;

    private final SymbolTable classVarsST;
    private final SymbolTable subroutineVarsST;

    private final File jackFile;
    private String jackFileName;
    private String currentCLassName = "";
    private String currentFunName = "";
    private String currentFunType = "";
    private String funRetType = "";
    private int ifCount = 0;
    private int whileCount = 0;
    private int nArgs = 0;

    public VMCompileEngine(File jackFile) throws IOException {
        this.jackFile = jackFile;
        this.jackFileName = jackFile.getName();
        this.jackFileName = jackFileName.substring(0, jackFileName.lastIndexOf("."));
        this.vmWriter = new VMWriter(jackFile);
        this.analyzer = new JackAnalyzer(jackFile);
        parser = new JackParser(jackFile);
        subroutineVarsST = new SymbolTable();
        classVarsST = new SymbolTable();
    }


    public void compileClass() throws Exception {
        parser.parse();
        parser.advance();
        expected("class");

        writeTag("<class>");
        writeTokenTag();

        parser.advance();
        currentCLassName = getIdentifierOrThrow();

        expected("{");
        writeTokenTag();
        parser.advance();

        compileClassVarDec();
        compileSubroutineDec();
//        compileStatements();

        expected("}");
        writeTag("</class>");
        close();

    }


    private void compileClassVarDec() throws Exception {
        if (isNotKeyword("static") && isNotKeyword("field")) return;
        writeTag("<classVarDec>");


        SymbolTableItem.Kind kind = SymbolTableItem.Kind.FILED;
        if (parser.tokenValue().equals("static")) kind = SymbolTableItem.Kind.STATIC;

        writeTokenTag();
        parser.advance();
        String varType = getVarType();
        compileClassVar(kind, varType);

        expected(";");
        writeTokenTag();
        parser.advance();

        writeTag("</classVarDec>");
        compileClassVarDec();
    }

    private void compileClassVar(SymbolTableItem.Kind kind, String varType) throws Exception {
        String identifier = getIdentifierOrThrow();
        classVarsST.define(new SymbolTableItem(identifier, varType, kind));
        if (isSymbol(",")) {
            writeTokenTag();
            parser.advance();
            compileClassVar(kind, varType);
        }
    }


    private void compileSubroutineDec() throws Exception {
        if (isNotSubroutine()) return;
        subroutineVarsST.reset();

        writeTag("<subroutineDec>");
        writeTokenTag();

        currentFunType = parser.tokenValue();
        if (currentFunType.equals("method")) {
            subroutineVarsST.define(new SymbolTableItem("this", currentCLassName, SymbolTableItem.Kind.ARG, 0));
        }
        parser.advance();
        funRetType = getVarType();
        currentFunName = getIdentifierOrThrow();
        compileParameterList();

        compileSubroutineBody();

        writeTag(" </subroutineDec>");
        compileSubroutineDec();

    }

    private void compileSubroutineBody() throws Exception {
        expected("{");
        writeTokenTag();
        parser.advance();

        ifCount = 0; // reset if conditions count for each subroutine
        whileCount = 0;

        compileSubVarDec();

        // function className.subroutineName nVars

        int nVars = subroutineVarsST.varCount(SymbolTableItem.Kind.VAR);
        vmWriter.writeFunction(currentCLassName + "." + currentFunName, nVars);

        // push constant nFields, call Memory.alloc
        //1 , pop pointer 0 , where nFields
        if (currentFunType.equals("constructor")) {
            int nFields = classVarsST.varCount(SymbolTableItem.Kind.FILED);
            vmWriter.writePush(SegmentsEnum.CONSTANT, nFields);
            vmWriter.writeCall("Memory.alloc", 1);
            vmWriter.writePop(SegmentsEnum.POINTER, 0);
        } else if (currentFunType.equals("method")) {
            vmWriter.writePush(SegmentsEnum.ARGUMENT, 0);
            vmWriter.writePop(SegmentsEnum.POINTER, 0);
        }
        compileStatements();


        expected("}");
        writeTokenTag();
        parser.advance();

        writeTag("</subroutineBody>");
    }


    private void compileParameterList() throws Exception {
        expected("(");
        writeTokenTag();
        parser.advance();

        writeTag("<parameterList>");
        if (isNotSymbol(")")) compileParameter();
        writeTag("</parameterList>");

        expected(")");
        writeTokenTag();
        parser.advance();
    }

    private void compileParameter() throws Exception {
        String varType = getVarType();
        String identifier = getIdentifierOrThrow();
        subroutineVarsST.define(new SymbolTableItem(identifier, varType, SymbolTableItem.Kind.ARG));
        if (isSymbol(",")) {
            writeTokenTag();
            parser.advance();
            compileParameter();
        }
    }


    private void compileSubVarDec() throws Exception {
        if (isNotKeyword("var")) return;
        writeTag("<varDec>");
        writeTokenTag();
        parser.advance();

        String varType = getVarType();
        compileSubVar(varType);
        expected(";");
        writeTokenTag();
        parser.advance();
        writeTag("</varDec>");
        compileSubVarDec();
    }

    private void compileSubVar(String varType) throws Exception {
        String identifier = getIdentifierOrThrow();
        subroutineVarsST.define(new SymbolTableItem(identifier, varType, SymbolTableItem.Kind.VAR));
        if (isSymbol(",")) {
            writeTokenTag();
            parser.advance();
            compileSubVar(varType);
        }
    }


    private void compileStatements() throws Exception {
        writeTag("<statements>");
        compileStatement();
        writeTag("</statements>");
    }

    private void compileStatement() throws Exception {
        String statement = parser.tokenValue();
        if (!statement.matches("(let)|(if)|(while)|(do)|(return)")) return;
        switch (statement) {
            case "let":
                compileLet();
                break;
            case "if":
                compileIf(ifCount++);
                break;
            case "while":
                compileWhile(whileCount++);
                break;
            case "do":
                compileDo();
                break;
            case "return":
                compileReturn();
                break;
            default:
                break;
        }
        compileStatement();
    }

    private void compileLet() throws Exception {
        expected("let");
        writeTag("<letStatement>");
        writeTokenTag();
        parser.advance();

        String identifier = getIdentifierOrThrow();
        SymbolTableItem letVar = getVarOrThrow(identifier);

        boolean arr = false;
        if (isSymbol("[")) {

            writeTokenTag();
            parser.advance();
            arr = true;
            compileExpression();

            expected("]");
            writeTokenTag();
            parser.advance();

            pushVar(letVar);
            vmWriter.writeArithmetic(CommandEnum.ADD);
        }

        expected("=");
        writeTokenTag();
        parser.advance();

        compileExpression();


        expected(";");
        writeTokenTag();
        parser.advance();

        if (arr) {
            vmWriter.writePop(SegmentsEnum.TEMP, 0);
            vmWriter.writePop(SegmentsEnum.POINTER, 1);
            vmWriter.writePush(SegmentsEnum.TEMP, 0);
            vmWriter.writePop(SegmentsEnum.THAT, 0);
        } else
            popVar(letVar);

        writeTag("</letStatement>");


    }

    private void popVar(SymbolTableItem letVar) throws Exception {
        SegmentsEnum segment = getVarSegment(letVar);

        vmWriter.writePop(segment, letVar.getAddress());
    }

    private void pushVar(SymbolTableItem letVar) throws Exception {

        SegmentsEnum segment = getVarSegment(letVar);

        vmWriter.writePush(segment, letVar.getAddress());
    }

    private SegmentsEnum getVarSegment(SymbolTableItem var) {
//        if (var.getType().equals("Array"))
//            return SegmentsEnum.THAT;
        SegmentsEnum segment = null;
        switch (var.getKind()) {
            case STATIC:
                segment = SegmentsEnum.STATIC;
                break;
            case FILED:
                segment = SegmentsEnum.THIS;
                break;
            case ARG:
                segment = SegmentsEnum.ARGUMENT;
                break;
            case VAR:
                segment = SegmentsEnum.LOCAL;
                break;
        }
        return segment;
    }

    private void compileExpression() throws Exception {
        writeTag("<expression>");
        compileTerm();
        writeTag("</expression>");
    }

    private void compileTerm() throws Exception {
        writeTag("<term>");
        JackInstructionType type = parser.getCurrentToken().getType();
        if (isConstant()) {
            if (type == JackInstructionType.INTEGER_CONSTANT) {
                vmWriter.writePush(SegmentsEnum.CONSTANT, Integer.parseInt(parser.tokenValue()));
            } else if (type == JackInstructionType.STRING_CONSTANT) {
                vmWriter.writeString(parser.tokenValue());
            } else if (type == JackInstructionType.KEYWORD) {
                if (isKeyword("null") || isKeyword("false")) {
                    vmWriter.writePush(SegmentsEnum.CONSTANT, 0);
                } else if (isKeyword("true")) {
                    vmWriter.writePush(SegmentsEnum.CONSTANT, 0);
                    vmWriter.writeArithmetic(CommandEnum.NOT);
                } else if (isKeyword("this")) {
                    vmWriter.writePush(SegmentsEnum.POINTER, 0);
                } else {
                    syntaxError("true or false or null");
                }
                // TODO: 09/03/2022 convert keyword to value
            }
            writeTokenTag();
            parser.advance();

        } else if (isIdentifier()) {
            String identifier = getIdentifierOrThrow();
            if (isSymbol(".")) {
                writeTokenTag();
                parser.advance();
                if (isIdentifierVariable(identifier)) {
                    SymbolTableItem var = getVarOrThrow(identifier);
                    nArgs += 1;
                    pushVar(var);
                    identifier = var.getType();
                }
                compileSubroutineCall(identifier);
            } else {
                SymbolTableItem var = getVarOrThrow(identifier);

                if (isNotSymbol("["))
                    pushVar(var);
            }
        }
        if (isSymbol("[")) {
            JackToken prevToken = parser.getPrevToken();
            writeTokenTag();
            parser.advance();

            compileExpression();
            expected("]");
            writeTokenTag();
            parser.advance();
            SymbolTableItem var = getVarOrThrow(prevToken.getValue());
            pushVar(var);
            vmWriter.writeArithmetic(CommandEnum.ADD);
            vmWriter.writePop(SegmentsEnum.POINTER, 1);
//            vmWriter.writePush(SegmentsEnum.THAT, var.getAddress());
            vmWriter.writePush(SegmentsEnum.THAT, 0);
        }

        if (isSymbol("(")) {
            writeTokenTag();
            parser.advance();
            compileExpression();
            expected(")");
            writeTokenTag();
            parser.advance();
        }

        if (isUnaryOp()) {
            writeTokenTag();
            parser.advance();
            compileTerm();
            vmWriter.writeArithmetic(CommandEnum.NOT);
        }
        writeTag("</term>");
        if (isOp()) {
            switch (parser.tokenValue()) {
                case "+":
                    writeTokenTag();
                    parser.advance();
                    compileTerm();
                    vmWriter.writeArithmetic(CommandEnum.ADD);
                    break;
                case "-":
                    JackToken prev = parser.getPrevToken();
                    writeTokenTag();
                    parser.advance();
                    compileTerm();
                    if (prev != null && prev.getType() == JackInstructionType.SYMBOL && !prev.getValue().equals(")"))
                        vmWriter.writeArithmetic(CommandEnum.NEG);
                    else
                        vmWriter.writeArithmetic(CommandEnum.SUB);
                    break;
                case "*":
                    writeTokenTag();
                    parser.advance();
                    compileTerm();
                    vmWriter.writeCall("Math.multiply", 2);
                    break;
                case "/":
                    writeTokenTag();
                    parser.advance();
                    compileTerm();
                    vmWriter.writeCall("Math.divide", 2);
                    break;
                case "<":
                    writeTokenTag();
                    parser.advance();
                    compileTerm();
                    vmWriter.writeArithmetic(CommandEnum.LT);
                    break;
                case ">":
                    writeTokenTag();
                    parser.advance();
                    compileTerm();
                    vmWriter.writeArithmetic(CommandEnum.GT);
                    break;
                case "|":
                    writeTokenTag();
                    parser.advance();
                    compileTerm();
                    vmWriter.writeArithmetic(CommandEnum.OR);
                    break;
                case "&":
                    writeTokenTag();
                    parser.advance();
                    compileTerm();
                    vmWriter.writeArithmetic(CommandEnum.AND);
                    break;
                case "=":
                    writeTokenTag();
                    parser.advance();
                    compileTerm();
                    vmWriter.writeArithmetic(CommandEnum.EQ);
                    break;
            }

        }


    }

    private void compileSubroutineCall(String parent) throws Exception {

        String callName;
        if (parent == null) {
            String identifier = getVarIdentifier();
            parent = checkIdentifierAndFunctionScope(identifier);
            // TODO: 12/03/2022  add  
//            if (isNotSymbol("."))
//                expected(".");
//            writeTokenTag();
//            parser.advance();
        }
        if (isNotSymbol("("))
            callName = parent + "." + compileSubroutineCallName();
        else
            callName = parent;

        if (callName.split("\\.").length == 1) {
            callName = currentCLassName + "." + callName;
            nArgs += 1;
            vmWriter.writePush(SegmentsEnum.POINTER, 0);
        }

        expected("(");
        writeTokenTag();
        parser.advance();

        nArgs += compileExpressionList();

        expected(")");
        writeTokenTag();
        parser.advance();


        vmWriter.writeCall(callName, nArgs);
        nArgs = 0;
        //        vmWriter.writePop(SegmentsEnum.TEMP, 0);

    }

    private String checkIdentifierAndFunctionScope(String identifier) throws Exception {

        if (identifier.equals("this") && currentFunType.equals("function")) {
            illegalError("using 'this' not allowed in static function");
        }

        if (isIdentifierVariable(identifier)) {
            SymbolTableItem item = getVarOrThrow(identifier);
            if (item.getKind() == SymbolTableItem.Kind.FILED && currentFunType.equals("function")) {
                illegalError("using filed variables not allowed in static function");
            }
            pushVar(item); // used to push the object of the call fun
            nArgs += 1;
            return item.getType();
        }
        return identifier;
    }

    private String compileSubroutineCallName() throws Exception {

        if (isSymbol(".")) {
            writeTokenTag();
            parser.advance();
        }
        if (isNotIdentifier())
            illegalError("Expected subroutine name");
        String identifier = getIdentifierOrThrow();
        if (isSymbol(".")) {
            writeTokenTag();
            parser.advance();
            return identifier + "." + compileSubroutineCallName();
        } else
            return identifier;
    }

    private int compileExpressionList() throws Exception {
        writeTag("<expressionList>");
        int callExpression = compileSubCallExpression();
        writeTag("</expressionList>");
        return callExpression;
    }

    private int compileSubCallExpression() throws Exception {
        if (isSymbol(")"))
            return 0;
        compileExpression();
        if (isSymbol(",")) {
            writeTokenTag();
            parser.advance();
            return 1 + compileSubCallExpression();
        }
        return 1;
    }

    private SymbolTableItem getVarOrThrow(String identifier) throws JackSyntaxError {
        SymbolTableItem item;
        item = subroutineVarsST.getByName(identifier);
        if (item == null) item = classVarsST.getByName(identifier);
        if (item == null) throw new JackSyntaxError("Undefined variable " + identifier);
        return item;
    }

    private boolean isIdentifierVariable(String identifier) {
        return subroutineVarsST.contains(identifier) || classVarsST.contains(identifier);
    }

    private void compileIf(int id) throws Exception {
        expected("if");

        writeTag("<ifStatement>");
        writeTokenTag();
        parser.advance();

        expected("(");
        writeTokenTag();
        parser.advance();

        compileExpression();

        expected(")");
        writeTokenTag();
        parser.advance();

        // generate label
//        vmWriter.writeArithmetic(CommandEnum.NOT);
//        vmWriter.writeArithmetic(CommandEnum.EQ);
//        String ifLabel = currentCLassName + "." + currentFunName + ".if" + ifCount++;
        String ifTrue = "IF_TRUE" + id;
        String ifFalse = "IF_FALSE" + id;
        String ifEnd = "IF_END" + id;

        vmWriter.writeIf(ifTrue);
        vmWriter.writeGoto(ifFalse);
        vmWriter.writeLabel(ifTrue);

        expected("{");
        writeTokenTag();
        parser.advance();

        compileStatements();
        expected("}");
        writeTokenTag();
        parser.advance();


        if (isKeyword("else")) {
            vmWriter.writeGoto(ifEnd);
            vmWriter.writeLabel(ifFalse);
            writeTokenTag();
            parser.advance();

            if (isNotSymbol("{"))
                syntaxError("}");
            writeTokenTag();
            parser.advance();

            compileStatements();

            if (isNotSymbol("}"))
                syntaxError("}");
            writeTokenTag();
            parser.advance();
            vmWriter.writeLabel(ifEnd);
        } else {
            vmWriter.writeLabel(ifFalse);
        }

//        ifCount++;
        writeTag("</ifStatement>");

    }

    private void compileWhile(int id) throws Exception {
        if (isNotKeyword("while"))
            syntaxError("while");

        writeTag("<whileStatement>");

        writeTokenTag();
        parser.advance();

        if (isNotSymbol("("))
            syntaxError("(");
        writeTokenTag();
        parser.advance();

        String whileStartLabel = "WHILE_EXP" + id;
        String whileEndLabel = "WHILE_END" + id;
        vmWriter.writeLabel(whileStartLabel);

        compileExpression();

        vmWriter.writeArithmetic(CommandEnum.NOT);
        vmWriter.writeIf(whileEndLabel);

        expected(")");
        writeTokenTag();
        parser.advance();

        expected("{");

        writeTokenTag();
        parser.advance();

        compileStatements();

        vmWriter.writeGoto(whileStartLabel);
        vmWriter.writeLabel(whileEndLabel);

        expected("}");
        writeTokenTag();
        parser.advance();
        writeTag("</whileStatement>");
    }

    private void compileDo() throws Exception {
        expected("do");
        writeTag("<doStatement>");
        writeTokenTag();
        parser.advance();

//        String identifier = getIdentifier();
        compileSubroutineCall(null);
        vmWriter.writePop(SegmentsEnum.TEMP, 0); // drop return value to tmp 0

        if (isNotSymbol(";"))
            syntaxError(";");

        writeTokenTag();
        parser.advance();

        writeTag("</doStatement>");

    }

    private void compileReturn() throws Exception {


        expected("return");
        writeTag("<returnStatement>");
        writeTokenTag();
        parser.advance();

        if (isNotSymbol(";"))
            compileExpression();

        expected(";");
        writeTokenTag();
        parser.advance();
        if (funRetType.equals("void")) {
            vmWriter.writePush(SegmentsEnum.CONSTANT, 0);
        }
//push constant 0
        vmWriter.writeReturn();
        funRetType = "";

        writeTag("</returnStatement>");
    }


    private String getVarType() throws IOException, IllegalJackException {
        if (!TokenPatterns.isType(parser.tokenValue())) illegalError("Expected a valid type ");
        writeTokenTag();
        String value = parser.tokenValue();
        parser.advance();
        return value;
    }

    private String getIdentifierOrThrow() throws Exception {
        if (isNotIdentifier()) illegalError("Expected identifier name ");
        writeTokenTag();
        String value = parser.tokenValue();
        parser.advance();
        return value;
    }

    private String getVarIdentifier() throws Exception {
        writeTokenTag();
        String value = parser.tokenValue();
        parser.advance();
        return value;
    }

    private void writeTokenTag() throws IOException {
        analyzer.writeTag(parser.getCurrentToken().getTag());
    }

    private void writeTag(String tag) throws IOException {
        analyzer.writeTag(tag);
    }

    private boolean isConstant() {
        String value = parser.tokenValue();
        return TokenPatterns.PATTERN_STRING_CONSTANT.matcher(value).matches()
                || TokenPatterns.PATTERN_INTEGER_CONSTANT.matcher(value).matches()
                || isKeywordConstant();
    }

    private boolean isUnaryOp() {
        return parser.tokenValue().matches("[~]");
    }

    private boolean isOp() {
        return parser.tokenValue().matches("([+\\-*/&|<>=])");
    }

    private boolean isKeywordConstant() {
        return parser.tokenValue().matches("(true)|(false)|(null)|(this)");
    }


    private void illegalError(String message) throws IllegalJackException, IOException {
        close();
        throw new IllegalJackException(message + " '" + parser.getCurrentToken().toString() + " in file ' " + jackFile.getAbsolutePath() + " '");
    }

    private void close() throws IOException {
        analyzer.close();
        vmWriter.close();
    }

    private void syntaxError(String expected) throws JackSyntaxError, IOException {
        close();
        throw new JackSyntaxError("Expected token " + expected + " but was " + "'" + parser.tokenValue() + "'" + " at line " + parser.getCurrentToken().getLineNumber() + " in file ' " + jackFile.getAbsolutePath() + " '");
    }

    private boolean isNotSubroutine() {
        return !(parser.tokenValue().matches("((constructor)|(function)|(method))") && parser.tokenType() == JackInstructionType.KEYWORD);
    }

    private boolean isKeyword(String key) {
        return (parser.tokenType() == JackInstructionType.KEYWORD) && (parser.tokenValue().equals(key));
    }

    private boolean isNotKeyword(String keyword) {
        return !isKeyword(keyword);
    }

    private boolean isIdentifier() {
        return (parser.tokenType() == JackInstructionType.IDENTIFIER) && TokenPatterns.PATTERN_IDENTIFIER.matcher(parser.tokenValue()).matches();
    }

    private boolean isNotIdentifier() {
        return !isIdentifier();
    }


    private boolean isNotSymbol(String symbol) {
        return !isSymbol(symbol);
    }

    private boolean isSymbol(String symbol) {
        return (parser.tokenType() == JackInstructionType.SYMBOL) && (parser.tokenValue().equals(symbol));
    }

    private void expected(String token) throws JackSyntaxError {
        if (!parser.tokenValue().equals(token))
            throw new JackSyntaxError("Expected " + token + " at " + parser.getLine() + " but was " + parser.tokenValue());
    }

}
