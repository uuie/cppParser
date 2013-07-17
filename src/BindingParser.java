import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 13-5-10
 * Time: 下午9:59
 * To change this template use File | Settings | File Templates.
 */
public class BindingParser {
    public Map<String,Map<String,List<String>>> functions=new HashMap<String,Map<String,List<String>>>();
    private Map<String,List<String>> lastModule;

    private State currentState;
    private String lastModuleName=null;
    public FunctionParser fp;
    public void init(){
        final State funcBegain=new State() {
            @Override
            protected void parseString(String line) {
                if(line.indexOf("tolua_beginmodule")>=0){
                    String module=line.replaceAll(".+?\"([^\"]*).+", "$1");
                    if(module.equals(line))
                        return;
                    lastModule=  new HashMap<String,List<String>>();
                    lastModuleName=module;
                    functions.put(lastModuleName,lastModule);
                    currentState=nextState;
                } else if(line.indexOf("tolua_function")>=0){
                    lastModuleName="";
                    lastModule =functions.get(lastModuleName);
                    if(lastModule==null)
                            lastModule=  new HashMap<String,List<String>>();
                    functions.put(lastModuleName,lastModule);
                    currentState=nextState;
                    nextState.parseString(line);
                }
            }

            @Override
            protected String[] matchString() {
                return new String[]{"tolua_beginmodule","tolua_function"};
            }
        }  ;
        final State funcSign=new State() {
            @Override
            protected void parseString(String line) {
                if(line.indexOf("tolua_endmodule")>=0)
                {
                    currentState=funcBegain;
                    return;
                }
                String[] signs=line.split(",");
                String sign=signs[1].replaceAll("\\s*\"?\\.?([^\\)]+).+","$1");
                String cfname=signs[2].replaceAll("\\s*([^\\)]+).+","$1");

                int cfIndex=0;
                try{
                    cfIndex=Integer.parseInt(cfname.substring(cfname.length()-2));
                }catch(Exception e){

                }
                if(cfIndex>0){
                    sign=sign+"~~"+cfIndex;
                    System.out.println(sign);
                }
                List<String> params=fp.functions.get(cfname);
//                if(params==null)
//                {
//                    System.out.println(cfname);
//                }
                lastModule.put(sign,params);
                if(lastModuleName.equals(""))
                   currentState=funcBegain;
            }

            @Override
            protected String[] matchString() {
                return new String[]{"tolua_function","tolua_endmodule"};  //To change body of implemented methods use File | Settings | File Templates.
            }
        };
        funcBegain.nextState=funcSign;
        currentState=funcBegain;
    }


    public void parse(String line){
        currentState.matchState(line);

    }

}
