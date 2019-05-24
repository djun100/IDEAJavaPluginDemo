package com.cy.javaplugin.common;

import com.intellij.openapi.actionSystem.AnActionEvent;

public class Constants {

    private static AnActionEvent sAnActionEvent;

    public static void setAnActionEvent(AnActionEvent anActionEvent){
        sAnActionEvent=anActionEvent;
    }

    public static AnActionEvent getAnActionEvent(){
        return sAnActionEvent;
    }
}
