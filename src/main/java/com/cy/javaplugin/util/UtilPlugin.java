package com.cy.javaplugin.util;

import com.cy.javaplugin.common.Constants;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.*;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;

import java.io.File;

/**
 * 1、改变代码要放到WriteCommandAction中执行
          WriteCommandAction.runWriteCommandAction(getProject(), new Runnable() {
              @Override
              public void run() {

              }
          });

 */
public class UtilPlugin {

    public static Project getProject() {
        AnActionEvent anActionEvent = Constants.getAnActionEvent();
//        Project fatherProject = anActionEvent.getProject();
        Project fatherProject = anActionEvent.getData(PlatformDataKeys.PROJECT);
        ;
        return fatherProject;
    }

    public static Editor getEditor() {
        AnActionEvent anActionEvent = Constants.getAnActionEvent();
//        Editor editor = CommonDataKeys.EDITOR.getData(anActionEvent.getDataContext());
        Editor editor = anActionEvent.getData(PlatformDataKeys.EDITOR);
        return editor;
    }

    public static PsiFile getPsiFile() {
        AnActionEvent anActionEvent = Constants.getAnActionEvent();
        PsiFile file = anActionEvent.getData(PlatformDataKeys.PSI_FILE);
        return file;
    }

    public static PsiClass getPsiClass() {
        PsiClass psiClass = UtilPluginKtKt.getPsiClass(getEditor(), getPsiFile());
        return psiClass;
    }

    public static String getSelectedText() {
        String selectedText = getEditor().getSelectionModel().getSelectedText();
        return selectedText;
    }

    public static PsiFile getFirstPsiFileByFileName(String fileName) {
        Project project = getProject();
        PsiFile[] foundFiles = FilenameIndex.getFilesByName(project, fileName, GlobalSearchScope.allScope(project));
        if (foundFiles.length <= 0) {
            return null;
        }
        return foundFiles[0];
    }

    public static PsiElementFactory getPsiElementFactory() {
        return JavaPsiFacade.getElementFactory(getProject());
    }

    public static String getProjectRootPath() {
        String basePath = getProject().getBasePath();
        return basePath;
    }

    public static String getCurrFilePath() {
        String currFilePath = getPsiFile().getVirtualFile().getPath();
        return currFilePath;
    }

    /**
     * 插件安装后，会被解压到插件沙箱
     *
     * @param pluginId
     * @return C:\Users\xuechao.wang\.IdeaIC2018.3\system\plugins-sandbox\plugins\AutoGen for example.
     */
    public static String getPluginPath(String pluginId) {
        IdeaPluginDescriptor plugin = PluginManager.getPlugin(PluginId.getId(pluginId));
        String pluginPath = plugin.getPath().getAbsolutePath();
        return pluginPath;
    }

    /**
     * 刷新文件系统，重新从本地载入文件
     */
    public static void refreshFileSystem() {
//            VirtualFileManager.getInstance().syncRefresh();   //anAction null
        UtilPlugin.getPsiFile().getVirtualFile().refresh(false, false);
    }

    public static void extractJar() {
        String pluginPath = UtilPlugin.getPluginPath("com.cy.main.AutoGen");
        String pathToExtract = pluginPath + "/AutoGen";
        String pathTobeExtract = pluginPath + "/lib/AutoGen-0.1.jar";
        new File(pathToExtract).mkdirs();
        String cmd = String.format("unzip -o %s -d %s", pathTobeExtract, pathToExtract);
        UtilCmd.exec(cmd);

    }

    /**
     * 类上方无变量则最多在两行空白下面增加该变量，类上方有变量则在变量区（第一个方法上面）最下面增加该变量
     * 坑：为刚创建的变量添加注解
     * 不能基于createFieldFromText得到的PsiField来创建字段对应的注解PsiAnnotation，要重新从psiClass遍历获取PsiField
     *
     * @param statement
     */
    public static void classAddField(String statement) {
        PsiField psiField = getPsiElementFactory().createFieldFromText(statement, getPsiClass());
        getPsiClass().add(psiField);
    }

    public static void fieldAddAnnotation(String fieldName,String annotation) {
        PsiField[] psiFields = getPsiClass().getFields();
        for (PsiField psiField : psiFields) {
            if (psiField.getName() != null && psiField.getName().equals(fieldName)) {
                getPsiClass().addBefore(getPsiElementFactory().createAnnotationFromText(annotation, getPsiClass()), psiField);
                break;
            }
        }
    }

    public static PsiElement psiMethodAddStatement(PsiMethod psiMethod,String statement){
        return psiMethod.getBody().add(getPsiElementFactory().createStatementFromText(statement,psiMethod));
    }

    /**可以同时写入方法注解annotation
     * @param psiClass
     * @param method
     * @return
     */
    public static PsiElement psiClassAddMethod(PsiClass psiClass,String method){
        return psiClass.add(getPsiElementFactory().createMethodFromText(method, psiClass));
    }

    /**获取方法内的第一个switch块
     * @param psiMethod
     * @return
     */
    public static PsiSwitchStatement getPsiSwitchStatementFormPsiMethod(PsiMethod psiMethod){
        PsiCodeBlock psiCodeBlock=psiMethod.getBody();
        PsiSwitchStatement psiSwitchStatement = null;
        for (PsiElement psiElement : psiCodeBlock.getChildren()) {
            if (psiElement instanceof PsiSwitchStatement) {
                psiSwitchStatement = (PsiSwitchStatement) psiElement;
                break;
            }
        }
        return psiSwitchStatement;
    }

    public static PsiStatement[] getPsiStatementsFromPsiSwitchStatement(PsiSwitchStatement psiSwitchStatement){
        PsiCodeBlock psiSwitchStatementBody = psiSwitchStatement.getBody();
        PsiStatement[] statements = psiSwitchStatementBody.getStatements();
        return statements;
    }

    public static String getTextFromPsiStatement(PsiStatement psiStatement){
        return psiStatement.getText();
    }

    public static PsiElement psiSwitchStatementAddStatement(PsiSwitchStatement psiSwitchStatement,String code){
        PsiCodeBlock psiSwitchCodeBlock = psiSwitchStatement.getBody();
        return psiSwitchCodeBlock.add(getPsiElementFactory().createStatementFromText(code, psiSwitchCodeBlock));
    }

    public static PsiElement[] psiSwitchStatementAddStatements(PsiSwitchStatement psiSwitchStatement,String code){
        PsiCodeBlock psiSwitchCodeBlock = psiSwitchStatement.getBody();
        String[] codes=code.split("\n");
        PsiElement[] result=new PsiElement[codes.length];
        for (int i=0;i<codes.length;i++){
            PsiElement psiElement= psiSwitchCodeBlock.add(getPsiElementFactory().createStatementFromText(codes[i], psiSwitchCodeBlock));
            result[i]=psiElement;
        }
        return result;
    }

    public static PsiMethod getPsiMethodFromPsiClassByName(PsiClass psiClass,String methodName){
        PsiMethod[] psiMethods = psiClass.getAllMethods();
        for (PsiMethod psiMethod:psiMethods){
            if (psiMethod.getName().equals(methodName)){
                return psiMethod;
            }
        }
        return null;
    }
}
