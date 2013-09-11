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
    static String targetDir=null;
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
        String baseDir=args[0];
        Main.targetDir=args[1];
        FunctionParser fp = new FunctionParser();
        fp.init();
        BindingParser bp = new BindingParser();
        bp.init();
        bp.fp = fp;
        String[] files={"lib/cocos2d-x/scripting/lua/cocos2dx_support/Lua_extensions_CCB.cpp" ,
                "lib/cocos2d-x/scripting/lua/cocos2dx_support/LuaCocos2d.cpp" ,
                "lib/cocos2dx_extra/extra/luabinding/cocos2dx_extra_ios_iap_luabinding.cpp" ,
                "lib/cocos2dx_extra/extra/luabinding/cocos2dx_extra_luabinding.cpp" ,
                "lib/third_party/chipmunk/luabinding/CCPhysicsWorld_luabinding.cpp",
                "lib/third_party/CSArmature/luabinding/CSArmature_luabinding.cpp",
        "lib/cocos2d-x/scripting/lua/tolua/tolua_map.c"}    ;
        for(String f:files){
            parseFile(baseDir+"/"+f,fp,bp);
        }
//        parseFile("/Users/user/github/quick-cocos2d-x/lib/cocos2dx_extensions_luabinding/cocos2dx_extensions_luabinding.cpp", fp, bp);
//        parseFile("/Users/user/github/quick-cocos2d-x/lib/cocos2dx_extra/extra/luabinding/cocos2dx_extra_luabinding.cpp", fp, bp);
//        parseFile("/Users/user/github/quick-cocos2d-x/lib/cocos2d-x/scripting/lua/cocos2dx_support/LuaCocos2d.cpp", fp, bp);
//        parseFile("/Users/user/github/quick-cocos2d-x/lib/cocos2dx_extra/extra/luabinding/cocos2dx_extra_ios_iap_luabinding.cpp",fp,bp);
//        parseFile("/Users/user/github/quick-cocos2d-x/lib/cocos2d-x/scripting/lua/tolua/tolua_map.c",fp,bp);
//        File f = new File("/Users/user/lua.complete.json");
//        if (f.exists())
//            f.delete();
        for (String key : bp.functions.keySet()) {
            writeFilesForSublime(key, bp.functions.get(key));

        }
        writeFilesForSublime(null, null);
//        for (String key : bp.functions.keySet()) {
//            writeFilesForEclipse(key, bp.functions.get(key));
//
//        }
        return;
    }

    private static void writeFilesForEclipse(String module, Map<String, List<String>> funcs) throws IOException {
        String dir = "/Users/user/ec";
        File fdir = new File(dir);
        if (!fdir.exists())
            fdir.mkdirs();
        File f = new File(fdir.getAbsolutePath() + "/" + module + ".lua");
        f.createNewFile();
        PrintWriter ptr = new PrintWriter(new FileWriter(f));
        ptr.println("-- @module " + module);
        ptr.println();
        ptr.println("-----------------------");
        for (String fname : funcs.keySet()) {
//            ptr.print(module+"."+fname+"=function");
            List<String> args = funcs.get(fname);
            String argSign = "";
            if(args==null){
                continue;
            }

            ptr.println("-- @function [parent=#"+module+"] " + fname);

            boolean firstarg=true;
            for (String arg : args) {
                if(firstarg && arg.substring(1).equals(module))
                    arg="self";
                firstarg=false;
                ptr.println("-- @param  "+arg);
//                argSign += arg + ",";
            }
//            if(argSign.length()>0)
//                ptr.print(argSign.substring(0, argSign.length() - 1));
//            ptr.print(")");
            ptr.println();
            ptr.println("-----------------------");
        }
        ptr.println("return nil");
        ptr.close();

        String gl="#ifdef GL_ES\n" +
                "precision mediump float;\n" +
                "#endif\n" +
                "uniform sampler2D u_texture;\n" +
                "varying vec2 v_texCoord;\n" +
                "varying vec4 v_fragmentColor;\n" +
                "void main(void)\n" +
                "{\n" +
                "// Convert to greyscale using NTSC weightings\n" +
                "float alpha = texture2D(u_texture, v_texCoord).a;\n" +
                "float grey = dot(texture2D(u_texture, v_texCoord).rgb, vec3(0.299, 0.587, 0.114));\n" +
                "gl_FragColor = vec4(grey, grey, grey, alpha);\n" +
                "}";

    }


    private static void writeFilesForSublime(String module, Map<String, List<String>> funcs) throws IOException {
        String dir = Main.targetDir;
        File fdir = new File(dir);
        if (!fdir.exists())
            fdir.mkdirs();
        File f = new File(fdir.getAbsolutePath() + "/lua.sublime-completions");


        String header="{\n" +
                "\t\"scope\":\"source.lua - keyword.control.lua - constant.language.lua - string\",\n" +
                "\t\"completions\":[\n" +
                "\t\t\"in\",\n" +
                "\t\t\"else\",\n" +
                "\t\t\"return\",\n" +
                "\t\t\"false\",\n" +
                "\t\t\"true\",\n" +
                "\t\t\"break\",\n" +
                "\t\t\"or\",\n" +
                "\t\t\"and\",";
        String footer="{\n" +
                "\t\t\t\"trigger\":\"cjson_encode()\",\n" +
                "\t\t\t\"contents\":\"cjson.encode()\"\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"trigger\":\"cjson_decode()\",\n" +
                "\t\t\t\"contents\":\"cjson.decode()\"\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"trigger\":\"cjson_encode_sparse_array()\",\n" +
                "\t\t\t\"contents\":\"cjson.encode_sparse_array()\"\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"trigger\":\"cjson_encode_max_depth()\",\n" +
                "\t\t\t\"contents\":\"cjson.encode_max_depth()\"\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"trigger\":\"cjson_decode_max_depth()\",\n" +
                "\t\t\t\"contents\":\"cjson.decode_max_depth()\"\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"trigger\":\"cjson_encode_number_precision()\",\n" +
                "\t\t\t\"contents\":\"cjson.encode_number_precision()\"\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"trigger\":\"cjson_encode_keep_buffer()\",\n" +
                "\t\t\t\"contents\":\"cjson.encode_keep_buffer()\"\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"trigger\":\"cjson_encode_invalid_numbers()\",\n" +
                "\t\t\t\"contents\":\"cjson.encode_invalid_numbers()\"\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"trigger\":\"cjson_decode_invalid_numbers()\",\n" +
                "\t\t\t\"contents\":\"cjson.decode_invalid_numbers()\"\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"trigger\":\"cjson_new()\",\n" +
                "\t\t\t\"contents\":\"cjson.new()\"\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"trigger\":\"zlib_deflate()\",\n" +
                "\t\t\t\"contents\":\"zlib.deflate()\"\n" +
                "\t\t}\n" +
                "\t\t,\n" +
                "\t\t{\n" +
                "\t\t\t\"trigger\":\"zlib_inflate()\",\n" +
                "\t\t\t\"contents\":\"zlib.inflate()\"\n" +
                "\t\t}\n" +
                "\t\t,\n" +
                "\t\t{\n" +
                "\t\t\t\"trigger\":\"zlib_version()\",\n" +
                "\t\t\t\"contents\":\"zlib.version()\"\n" +
                "\t\t}\n" +
                "\t\t,\n" +
                "\t\t{\n" +
                "\t\t\t\n" +
                "\t\t}\n" +
                "\t\t\n" +
                "\t]\n" +
                "}";
        PrintWriter ptr = new PrintWriter(new FileWriter(f, true));
        if (f.length()==0L){
            ptr.println(header);
        }
        String line = "{ \"trigger\": \"%s\\t%s\", \"contents\": \"%s\"},";

        if(module==null){
            for(String str:CONSTS){
                ptr.println(String.format(line,str,str,str));
            }
            ptr.println(footer);
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
                func = (":" + fname).replace("~~","");
                fname=fname.replaceAll("~~\\d+$","");

                content = ":" + fname + "(" + argSign + ")";
                desc = String.format("%s:%s(%s)", module, fname, argSign);
            } else {
                func = ("." + fname).replace("~~","");
                fname=fname.replaceAll("~~\\d+$","");
                content = seprator + fname + "(" + argSign + ")";
                desc = String.format("%s"+seprator+"%s(%s)", module, fname, argSign);
            }

             seprator=(!module.equals(""))?"_":"";


//            if(s.indexOf("~~")>0){
//              System.out.println();
//            }

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
