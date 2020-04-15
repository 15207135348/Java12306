package com.yy.util;


import com.yy.domain.JsFunc;

import java.util.*;

public class JsParser {

    private static String getFuc_(String script, String startWith) {
        String temp = script;
        int index = script.indexOf(startWith);
        if (index < 0) {
            return null;
        }
        temp = temp.substring(index);
        index = temp.indexOf("{");
        if (index < 0) {
            return null;
        }
        StringBuilder func = new StringBuilder(temp.substring(0, index + 1));
        Stack<Character> stack = new Stack<>();
        stack.push('{');
        temp = temp.substring(index + 1);
        for (int i = 0; i < temp.length(); ++i) {
            char c = temp.charAt(i);
            func.append(c);
            if (c == '{') {
                stack.push(c);
            }
            if (c == '}') {
                stack.pop();
            }
            if (stack.isEmpty()) {
                break;
            }
        }
        if (!stack.isEmpty()) {
            return null;
        }
        return func.toString();
    }

    public static JsFunc getFunc(String script, String funcName) {
        String startWith1 = String.format("function %s(", funcName);
        String func = getFuc_(script, startWith1);
        if (func != null) {
            return new JsFunc(funcName, func);
        }
        String startWith2 = String.format("%s:function(", funcName);
        func = getFuc_(script, startWith2);
        if (func != null) {
            func = func.replace(startWith2, startWith1);
            return new JsFunc(funcName, func);
        }
        return null;
    }

    public static List<JsFunc> getFuncs(String script) {
        String temp = script;
        List<JsFunc> funcs = new ArrayList<>();
        int index;
        String name;
        JsFunc jsFunc;
        while (true) {
            index = temp.indexOf("function ");
            if (index < 0) {
                break;
            }
            temp = temp.substring(index);
            name = temp.substring(9, temp.indexOf("("));
            jsFunc = getFunc(temp, name);
            if (jsFunc != null) {
                funcs.add(jsFunc);
            }
            temp = temp.substring(9);
        }
        return funcs;
    }
}
