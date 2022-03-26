import java.io.File;
import java.io.IOException;

/**
 * * Author : Abdelmajid ID ALI
 * * On : 03/03/2022
 * * Email :  abdelmajid.idali@gmail.com
 **/
public class CompileEngine {

    private final VMWriter vmWriter;
    private final JackParser parser;
    private final JackAnalyzerWriter analyzer;

    private final SymbolTable classVarsST;
    private final SymbolTable subroutineVarsST;
    private String jackFileName;
    private final File jackFile;

    private String currentCLassName;
    private String currentFunName;
    private String currentFunType;

    public CompileEngine(File jackFile) throws IOException {
        this.jackFile = jackFile;
        this.jackFileName = jackFile.getName();
        this.jackFileName = jackFileName.substring(0, jackFileName.lastIndexOf("."));
        this.vmWriter = new VMWriter(jackFile);
        this.analyzer = new JackAnalyzerWriter(jackFile);
        parser = new JackParser(jackFile);
        subroutineVarsST = new SymbolTable();
        classVarsST = new SymbolTable();
    }


    private void writeTokenTag() throws IOException {
        analyzer.writeTokenTag(parser.getCurrentToken());
    }

    //region compile functions

    public void compileClass() throws Exception {
        parser.parse();
        parser.advance();
        if (isNotKeyword("class"))
            syntaxError("class");
        analyzer.writeTag("<class>");
        writeTokenTag();

        parser.advance();
        if (isNotIdentifier())
            illegalError("Identifier not allowed");
        writeTokenTag();
        currentCLassName = parser.tokenValue();

        parser.advance();
        if (isNotSymbol("{"))
            syntaxError("{");
        writeTokenTag();
        parser.advance();

        compileClassVarDec();
        compileSubroutine();

        if (isNotSymbol("}"))
            syntaxError("}");
        writeTokenTag();
        // make sure that there is no more token
        if (parser.hasMoreTokens())
            illegalError("Unexpected syntax near ");

        analyzer.writeTag("</class>");
        close();
    }


    private void compileClassVarDec() throws Exception {
        if (isNotKeyword("static") && isNotKeyword("field"))
            return;
        writeTag("<classVarDec>");
        compileClassVar();
        writeTag("</classVarDec>");
        compileClassVarDec();
    }

    private void compileClassVar() throws Exception {
        if (isNotKeyword("static") && isNotKeyword("field"))
            syntaxError("static or filed ");
        SymbolTableItem.Kind kind = SymbolTableItem.Kind.FILED;
        if (parser.tokenValue().equals("static"))
            kind = SymbolTableItem.Kind.STATIC;

        writeTokenTag();
        parser.advance();
//        compileType();
//        compileIdentifierName();
        String type = getVarType();
        compileCLLassVarIdentifier(kind, type);

        if (isNotSymbol(";"))
            syntaxError(";");

        writeTokenTag();
        parser.advance();
    }

    private void compileCLLassVarIdentifier(SymbolTableItem.Kind kind, String type) throws Exception {
        if (isNotIdentifier())
            illegalError("Expected identifier name ");
        String name = parser.tokenValue();
        writeTokenTag();
        parser.advance();
        classVarsST.define(
                new SymbolTableItem(name, type, kind)
        );
        if (isSymbol(",")) {
            writeTokenTag();
            parser.advance();
            compileCLLassVarIdentifier(kind, type);
        }
    }

    private String getVarType() throws IOException, IllegalJackException {
        if (!TokenPatterns.isType(parser.tokenValue()))
            illegalError("Expected a valid type ");
        writeTokenTag();
        String value = parser.tokenValue();
        parser.advance();
        return value;
    }


    private void compileType() throws IllegalJackException, IOException {
        if (!TokenPatterns.isType(parser.tokenValue()))
            illegalError("Expected a valid type ");
        writeTokenTag();
        parser.advance();
    }

    private void compileIdentifierName() throws Exception {
        if (isNotIdentifier())
            illegalError("Expected identifier name ");
        writeTokenTag();
        parser.advance();

        if (isSymbol(",")) {
            writeTokenTag();
            parser.advance();
            compileIdentifierName();
        }

    }


    private void compileSubroutine() throws Exception {
        if (isNotSubroutine())
            return;

        subroutineVarsST.reset();

        writeTag("<subroutineDec>");
        writeTokenTag();

        currentFunType = parser.tokenValue();
        if (currentFunType.equals("method")) {
            subroutineVarsST.define(
                    new SymbolTableItem(
                            "this", currentCLassName,
                            SymbolTableItem.Kind.ARG, 0
                    )
            );
        }

        parser.advance();
        compileType();
        currentFunName = getIdentifier();
//        compileSubArgs();
//        compileIdentifierName();
        compileParameterList();

//        int nArgs = subroutineVarsST.varCount(SymbolTableItem.Kind.ARG);

        compileSubroutineBody();

        writeTag(" </subroutineDec>");
        compileSubroutine();
    }


    private String getIdentifier() throws Exception {
        if (isNotIdentifier())
            illegalError("Expected identifier name ");
        writeTokenTag();
        String value = parser.tokenValue();
        parser.advance();
        return value;
    }

    private void compileParameterList() throws Exception {
        if (isNotSymbol("("))
            syntaxError("(");
        writeTokenTag();
        parser.advance();

        writeTag("<parameterList>");
        if (isNotSymbol(")"))
            compileParameter();

        writeTag("</parameterList>");

        if (isNotSymbol(")"))
            syntaxError(")");
        writeTokenTag();

        parser.advance();
    }

    private void compileParameter() throws Exception {
        String argType = getVarType();
        if (isNotIdentifier())
            illegalError("Expected identifier name ");
        String argName = getIdentifier();

        subroutineVarsST.define(
                new SymbolTableItem(
                        argName, argType, SymbolTableItem.Kind.ARG
                )
        );
//        writeTokenTag();
//        parser.advance();
        if (isSymbol(",")) {
            writeTokenTag();
            parser.advance();
            compileParameter();
        }
    }

    private void compileSubroutineBody() throws Exception {
        if (isNotSymbol("{"))
            syntaxError("{");
        writeTag("<subroutineBody>");
        writeTokenTag();
        parser.advance();

        compileVarDec();

        // function className.subroutineName nVars

//        int nVars = subroutineVarsST.varCount(SymbolTableItem.Kind.VAR);
//        vmWriter.writeFunction(currentCLassName + "." + currentFunName, nVars);

        // push constant nFields, call Memory.alloc
        //1 , pop pointer 0 , where nFields
        if (currentFunType.equals("constructor")) {
            int nFields = subroutineVarsST.varCount(SymbolTableItem.Kind.FILED);
            vmWriter.writePush(SegmentsEnum.CONSTANT, nFields);
            vmWriter.writeCall("Memory.alloc", 1);
            vmWriter.writePop(SegmentsEnum.POINTER, 0);
        } else if (currentFunType.equals("method")) {
            vmWriter.writePush(SegmentsEnum.ARGUMENT, 0);
            vmWriter.writePush(SegmentsEnum.POINTER, 0);
        }
        compileStatements();


        if (isNotSymbol("}"))
            syntaxError("}");
        writeTokenTag();
        parser.advance();

        writeTag("</subroutineBody>");

    }

    private void compileVarDec() throws Exception {
        if (isNotKeyword("var"))
            return;

        writeTag("<varDec>");
        writeTokenTag(); // write 'var'
        parser.advance();

        String varType = getVarType();
        compileSubVarDec(varType);
//        compileType();
//        compileIdentifierName();

        if (isNotSymbol(";"))
            syntaxError(";");
        writeTokenTag();
        parser.advance();

        writeTag("</varDec>");
        compileVarDec();
    }

    private void compileSubVarDec(String varType) throws Exception {
        String identifier = getIdentifier();
        subroutineVarsST.define(
                new SymbolTableItem(
                        identifier, varType, SymbolTableItem.Kind.VAR
                )
        );
        if (isSymbol(",")) {
            writeTokenTag();
            parser.advance();
            compileSubVarDec(varType);
        }
    }

    private void compileStatements() throws Exception {
        writeTag("<statements>");
        compileStatement();
        writeTag("</statements>");
    }

    private void compileStatement() throws Exception {
        String statement = parser.tokenValue();
        if (!statement.matches("(let)|(if)|(while)|(do)|(return)"))
            return;
        switch (statement) {
            case "let":
                compileLet();
                break;
            case "if":
                compileIf();
                break;
            case "while":
                compileWhile();
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
        if (isNotKeyword("let"))
            syntaxError("let");
        writeTag("<letStatement>");

        writeTokenTag(); // let
        parser.advance();

//        if (isNotIdentifier())
//            illegalError("Expected identifier name");
//        writeTokenTag();
//        parser.advance();
        String letVarName = getIdentifier();

        if (isSymbol("[")) {

            writeTokenTag();
            parser.advance();

            compileExpression();
            if (isNotSymbol("]"))
                syntaxError("]");
            writeTokenTag();
            parser.advance();
        }

        if (isNotSymbol("="))
            syntaxError("=");
        writeTokenTag();
        parser.advance();

        compileExpression();

        vmWriter.writePop(SegmentsEnum.LOCAL, subroutineVarsST.indexOf(letVarName));
        if (isNotSymbol(";"))
            syntaxError(";");
        writeTokenTag();
        parser.advance();

        writeTag("</letStatement>");
    }


    private void compileIf() throws Exception {
        if (isNotKeyword("if"))
            syntaxError("if");
        writeTag("<ifStatement>");

        writeTokenTag();
        parser.advance();

        if (isNotSymbol("("))
            syntaxError("(");
        writeTokenTag();
        parser.advance();

        compileExpression();

        if (isNotSymbol(")"))
            syntaxError(")");
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

        if (isKeyword("else")) {
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

        }

        writeTag("</ifStatement>");
    }

    private void compileWhile() throws Exception {
        if (isNotKeyword("while"))
            syntaxError("while");
        writeTag("<whileStatement>");

        writeTokenTag();
        parser.advance();

        if (isNotSymbol("("))
            syntaxError("(");
        writeTokenTag();
        parser.advance();

        compileExpression();

        if (isNotSymbol(")"))
            syntaxError(")");
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

        writeTag("</whileStatement>");
    }

    private void compileDo() throws Exception {
        if (isNotKeyword("do"))
            syntaxError("do");
        writeTag("<doStatement>");
        writeTokenTag();
        parser.advance();

        compileSubroutineCall();

        if (isNotSymbol(";"))
            syntaxError(";");

        writeTokenTag();
        parser.advance();

        writeTag("</doStatement>");

    }

    private void compileReturn() throws Exception {
        if (isNotKeyword("return"))
            illegalError("Expected return statement");
        writeTag("<returnStatement>");
        writeTokenTag();
        parser.advance();


//        if (currentFunType.equals("constructor")){
//            if (isNotSymbol(";"))
//                syntaxError(";");
//        }
        if (isNotSymbol(";"))
            compileExpression();

        if (isNotSymbol(";"))
            syntaxError(";");
        writeTokenTag();
        parser.advance();

        writeTag("</returnStatement>");

    }


    private void compileExpression() throws Exception {
        writeTag("<expression>");
        compileTerm();
        writeTag("</expression>");

    }

    private void compileTerm() throws Exception {
        writeTag("<term>");

        if (isConstant()) {
//            vmWriter.writePush(SegmentsEnum.CONSTANT,);
            writeTokenTag();
            parser.advance();
        } else if (isIdentifier()) {
            writeTokenTag();
            parser.advance();
            if (isSymbol(".")) {
                writeTokenTag();
                parser.advance();
                compileSubroutineCall();
            }
        }
        if (isSymbol("[")) {
            writeTokenTag();
            parser.advance();
            compileExpression();
            if (isNotSymbol("]"))
                syntaxError("]");
            writeTokenTag();
            parser.advance();
        }
        if (isSymbol("(")) {
            writeTokenTag();
            parser.advance();
            compileExpression();
            if (isNotSymbol(")"))
                syntaxError(")");
            writeTokenTag();
            parser.advance();
        }


        if (isUnaryOp()) {
            writeTokenTag();
            parser.advance();
            compileTerm();
        }

        writeTag("</term>");


        if (isOp()) {
            writeTokenTag();
            parser.advance();
            compileTerm();
        }
    }

    private void compileSubroutineCall() throws Exception {
        compileSubroutineCallName();

        if (isNotSymbol("("))
            syntaxError("(");
        writeTokenTag();
        parser.advance();

        compileExpressionList();

        if (isNotSymbol(")"))
            syntaxError(")");
        writeTokenTag();
        parser.advance();


    }

    private void compileSubroutineCallName() throws Exception {
        if (isNotIdentifier())
            illegalError("Expected subroutine name");
        writeTokenTag();
        parser.advance();
        if (isSymbol(".")) {
            writeTokenTag();
            parser.advance();
            compileSubroutineCallName();
        }
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


    //#endregion


    //region helpers

    private void writeTag(String tag) throws IOException {
        analyzer.writeTag(tag);
    }

    private boolean isConstant() {
        String value = parser.tokenValue();
        return TokenPatterns.PATTERN_STRING_CONSTANT
                .matcher(value).matches()
                || TokenPatterns.PATTERN_INTEGER_CONSTANT
                .matcher(value).matches()
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

    private String getOutputFileName(File jackFile) {
        String name = jackFile.getName();
        return name.substring(0, name.lastIndexOf(".")) + ".xml";
    }

//    private void writeTag(String tage) throws IOException {
//        writer.write(tage);
//        writer.newLine();
//    }
//
//    private void writeTokenTag() throws IOException {
//        writer.write(parser.getCurrentToken().getTag());
//        writer.newLine();
//    }

    private void illegalError(String message) throws IllegalJackException, IOException {
        close();
        throw new IllegalJackException(
                message + " '" + parser.getCurrentToken().toString()
                        + " in file ' " + jackFile.getAbsolutePath() + " '"
        );
    }

    private void close() throws IOException {
        analyzer.close();
        vmWriter.close();
    }

    private void syntaxError(String expected) throws JackSyntaxError, IOException {
        close();
        throw new JackSyntaxError("Expected token "
                + expected + " but was "
                + "'" + parser.tokenValue() + "'"
                + " at line " + parser.getCurrentToken().getLineNumber()
                + " in file ' " + jackFile.getAbsolutePath() + " '"
        );
    }


    public boolean isNotSubroutine() {
        return !(parser.tokenValue()
                .matches("((constructor)|(function)|(method))")
                && parser.tokenType() == JackInstructionType.KEYWORD
        );
    }

    private boolean isKeyword(String key) {
        return (parser.tokenType() == JackInstructionType.KEYWORD) && (parser.tokenValue().equals(key));
    }

    private boolean isNotKeyword(String keyword) {
        return !isKeyword(keyword);
    }

    private boolean isIdentifier() {
        return (parser.tokenType() == JackInstructionType.IDENTIFIER) &&
                TokenPatterns.PATTERN_IDENTIFIER
                        .matcher(parser.tokenValue())
                        .matches();
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
    //#endregion
}
