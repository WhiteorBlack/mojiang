package cn.idcby.jiajubang;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    private Map<Integer,List<String>> firstMap = new HashMap<>() ;
    private Map<Integer,List<String>> childMap = new HashMap<>() ;

    @Test
    public void testMap(){
        List<String> paList = new ArrayList<>(2) ;
        paList.add("1的内容1") ;
        paList.add("1的内容2") ;

        childMap.put(1,paList) ;

        Set<Integer> set1 = childMap.keySet() ;
        for(int key : set1){
            List<String> values = childMap.get(key) ;
            List<String> newStrList = new ArrayList<>(values.size()) ;
            newStrList.addAll(values) ;
            firstMap.put(key ,newStrList);
        }

        System.out.println("childMap=" + childMap.toString() + "firstMap=" + firstMap.toString()) ;

        List<String> child1List = childMap.get(1) ;
        child1List.remove(0) ;

        System.out.println("childMap=" + childMap.toString() + "firstMap=" + firstMap.toString()) ;

        childMap.clear();

        Set<Integer> set2 = firstMap.keySet() ;
        for(int key : set2){
            List<String> values = firstMap.get(key) ;
            List<String> newStrList = new ArrayList<>(values.size()) ;
            newStrList.addAll(values) ;
            childMap.put(key ,newStrList);
        }

        System.out.println("childMap=" + childMap.toString() + "firstMap=" + firstMap.toString()) ;
    }

}