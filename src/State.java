import sun.net.idn.StringPrep;

/**
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 13-5-10
 * Time: 下午8:55
 * To change this template use File | Settings | File Templates.
 */
public abstract  class State {

    public State nextState;

    public String expect;


    public boolean matchState(String line){

        String[] matchs=matchString();
        for(String s:matchs){

            if(line.indexOf(s)>=0)   {
                parseString(line);
                return true;
            }
        }
        return false;
    }

    abstract protected void parseString(String line);
    abstract protected String[] matchString();
    public String getResult(){
        return null;
    }
}
