package com.cy.javaplugin.demo.main;


import com.cy.javaplugin.common.Constants;
import com.cy.javaplugin.util.UtilPlugin;
import com.cy.javaplugin.util.UtilPluginKtKt;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiStatement;
import com.intellij.psi.PsiSwitchStatement;

import java.util.ArrayList;
import java.util.List;

public class EntranceAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Constants.setAnActionEvent(e);
        WriteCommandAction.runWriteCommandAction(UtilPlugin.getProject(), new Runnable() {
            @Override
            public void run() {
                UtilPlugin.classAddField("private View view;");
                UtilPlugin.fieldAddAnnotation("@BindView()", "view");
                List<String> ids = new ArrayList<>();
                ids.add("R.id.a");
                ids.add("R.id.b");
//                UtilPlugin.psiClassAddMethod(UtilPlugin.getPsiClass(), UtilPluginKtKt.createButterKnifeOnClickMethodAndSwitch(ids));
                UtilPlugin.psiClassAddMethod(UtilPlugin.getPsiClass(), "private void test(){}");
                PsiMethod psiMethod = UtilPlugin.getPsiMethodFromPsiClassByName(UtilPlugin.getPsiClass(), "test");
                UtilPlugin.psiMethodAddStatement(psiMethod, UtilPluginKtKt.createSwitchByCaseValues("condition", ids));
                PsiSwitchStatement psiSwitchStatement = UtilPlugin.getPsiSwitchStatementFormPsiMethod(psiMethod);
                PsiStatement[] psiStatements = UtilPlugin.getPsiStatementsFromPsiSwitchStatement(psiSwitchStatement);
                for (PsiStatement psiStatement:psiStatements){
                    System.out.println("a statement:"+UtilPlugin.getTextFromPsiStatement(psiStatement));
                }
                UtilPlugin.psiSwitchStatementAddStatements(psiSwitchStatement,"case lala:\nint lalala;\nbreak;");
            }
        });
    }

}
