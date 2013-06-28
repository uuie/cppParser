import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 13-5-10
 * Time: 下午8:59
 * To change this template use File | Settings | File Templates.
 */
public class FunctionParser {

    public Map<String, List<String>> functions = new HashMap<String, List<String>>();
    private List<String> lastFunc;

    private State currentState;

    public void init() {
        final State funcBegain = new State() {
            @Override
            protected void parseString(String line) {
                String sign = line.replaceAll(".+?(tolua_[^\\(]+).+", "$1");
                lastFunc = new ArrayList<String>();
                functions.put(sign, lastFunc);
                currentState = nextState;
            }

            @Override
            protected String[] matchString() {
                return new String[]{"static int tolua_"};
            }
        };

        final State funcReturn = new State() {
            @Override
            protected void parseString(String line) {
                currentState = nextState;
            }

            @Override
            protected String[] matchString() {
                return new String[]{"TOLUA_RELEASE", "static "};  //To change body of implemented methods use File | Settings | File Templates.
            }
        };

        final State funcParam = new State() {
            @Override
            protected void parseString(String line) {

                if (line.indexOf("return") >= 0) {
//                    lastFunc.add(String.valueOf(0));
                    currentState = nextState;
                    return;

                }
                if (line.indexOf("tolua_isnoobj") >= 0) {
//                    int args = Integer.parseInt(line.replaceAll(".+?(\\d+).+", "$1"));
//                    lastFunc.add(String.valueOf(args - 1));
                    currentState = nextState;
                    return;
                }
                String arg=line.substring(line.indexOf("tolua_is"));
                arg="_"+arg.substring("tolua_is".length(),"tolua_is".length()+3);
                if(line.indexOf("tolua_isuserdata")>=0){
                    arg="_userdata";
                } else  if(line.indexOf("toluafix_isfunction")>0){
                    arg="_listener";
                }   else
                if(line.indexOf("tolua_isuser")>=0){
                    arg=line.replaceAll(".+?tolua_isuser.+?\"([^\"]+).+","$1");
                    arg=arg.replaceAll("\\s+","_");
                    arg="_"+arg;
                }
                lastFunc.add(arg);

            }

            @Override
            protected String[] matchString() {
                return new String[]{"tolua_isnoobj", "return","tolua_is"};  //To change body of implemented methods use File | Settings | File Templates.
            }
        };
        funcBegain.nextState = funcParam;
        funcParam.nextState = funcBegain;
        currentState = funcBegain;
    }


    public void parse(String line) {
        currentState.matchState(line);

    }
}
