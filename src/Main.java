import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 13-5-10
 * Time: 下午8:51
 * To change this template use File | Settings | File Templates.
 */
public class Main {
    static List<String> CONSTS=new ArrayList<String>();
    static class ConstParser{
        public void parse(String line){
            if(line.indexOf("tolua_constant")>=0 && line.indexOf("tolua_S")>=0){
                  String consts=line.replaceAll("[^\"]+\"([^\"]+).+","$1");
                CONSTS.add(consts);
            }
        }
    }
    static ConstParser CP=new ConstParser();
    public static void main(String[] args) throws IOException {

        FunctionParser fp = new FunctionParser();
        fp.init();
        BindingParser bp = new BindingParser();
        bp.init();
        bp.fp = fp;

        parseFile("/Users/chris/Desktop/quick-cocos2d-x/lib/cocos2dx_extensions_luabinding/cocos2dx_extensions_luabinding.cpp", fp, bp);
        parseFile("/Users/chris/Desktop/quick-cocos2d-x/lib/cocos2dx_extra/extra/luabinding/cocos2dx_extra_luabinding.cpp", fp, bp);
        parseFile("/Users/chris/Desktop/quick-cocos2d-x/lib/cocos2d-x/scripting/lua/cocos2dx_support/LuaCocos2d.cpp", fp, bp);
        parseFile("/Users/chris/Desktop/quick-cocos2d-x/lib/cocos2dx_extra/extra/luabinding/cocos2dx_extra_ios_iap_luabinding.cpp",fp,bp);
        parseFile("/Users/chris/Desktop/quick-cocos2d-x/lib/cocos2d-x/scripting/lua/tolua/tolua_map.c",fp,bp);
        File f = new File("/Users/chris/Desktop/bingexport/lua.complete");
        if (f.exists())
            f.delete();
        for (String key : bp.functions.keySet()) {
//            writeFiles(key,bp.functions.get(key));
            writeFilesForSublime(key, bp.functions.get(key));

        }
        writeFilesForSublime(null, null);

        return;
    }

    private static void writeFiles(String module, Map<String, List<String>> funcs) throws IOException {
        String dir = "/Users/chris/Desktop/bingexport";
        File fdir = new File(dir);
        if (!fdir.exists())
            fdir.mkdirs();
        File f = new File(fdir.getAbsolutePath() + "/" + module + ".lua");
        f.createNewFile();
        PrintWriter ptr = new PrintWriter(new FileWriter(f));
        ptr.println("module \"" + module + "\"");
        for (String fname : funcs.keySet()) {
//            ptr.print(module+"."+fname+"=function");
            ptr.print("function " + fname);

            ptr.print("(");
            List<String> args = funcs.get(fname);
            String argSign = "";
            for (String arg : args) {
                argSign += arg + ",";
            }
            ptr.print(argSign.substring(0, argSign.length() - 1));
            ptr.print(")");
            ptr.println("end");
        }
//        ptr.println("return "+module);
        ptr.close();

    }


    private static void writeFilesForSublime(String module, Map<String, List<String>> funcs) throws IOException {
        String dir = "/Users/chris/Desktop/bingexport";
        File fdir = new File(dir);
        if (!fdir.exists())
            fdir.mkdirs();
        File f = new File(fdir.getAbsolutePath() + "/lua.complete");
        if (!f.exists())
            f.createNewFile();

        PrintWriter ptr = new PrintWriter(new FileWriter(f, true));
        String line = "{ \"trigger\": \"%s\\t%s\", \"contents\": \"%s\"},";

        if(module==null){
            for(String str:CONSTS){
                ptr.println(String.format(line,str,str,str));
            }
            ptr.close();
            return;
        }
        ptr.println(String.format(line, module, "", module));
        // ptr.println("module \""+module + "\"");
        for (String fname : funcs.keySet()) {
//            ptr.print(module+"."+fname+"=function");
            // ptr.print("function "+fname);
            List<String> args = funcs.get(fname);
            String argSign = "";
            if (fname.equals("create")) {
                boolean isCreate = args.get(0).endsWith(module);
            }
            if(args==null)    {
                args=new ArrayList<String>();
            }
            boolean isClassMethod =(!module.equals("")) && args.size() > 0 && (args.get(0).indexOf(module) == 0 || args.get(0).endsWith(module));
            if (isClassMethod)
                args = args.subList(1, args.size());
            for (String arg : args) {
                argSign += arg + ",";
            }
            if (argSign.length() > 1)
                argSign = argSign.substring(0, argSign.length() - 1);
            String desc = null;
            String func = null;
            String content = null;
            String seprator=(!module.equals(""))?".":"";
            if (isClassMethod) {
                func = ":" + fname;
                content = ":" + fname + "(" + argSign + ")";
                desc = String.format("%s:%s(%s)", module, fname, argSign);
            } else {
                func = "." + fname;
                content = seprator + fname + "(" + argSign + ")";
                desc = String.format("%s"+seprator+"%s(%s)", module, fname, argSign);
            }

             seprator=(!module.equals(""))?"_":"";
            ptr.println(String.format(line, (module + seprator + func.substring(1)), desc, module+content));
            if(isClassMethod)
                 ptr.println(String.format(line, func, desc, content.substring(1)));
        }
//        ptr.println("return "+module);
        ptr.close();

    }

    private static void parseFile(String file, FunctionParser fp, BindingParser bp) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(new File(file)));
        String line = null;
        while ((line = br.readLine()) != null) {
            fp.parse(line);
            bp.parse(line);
            CP.parse(line);
        }

        br.close();
    }


}
