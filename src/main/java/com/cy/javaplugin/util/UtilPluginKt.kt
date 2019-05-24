package com.cy.javaplugin.util

import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiMethod
import com.intellij.psi.SyntheticElement
import com.intellij.psi.util.PsiTreeUtil
import java.util.ArrayList

/**
 * 根据当前文件获取对应的class文件
 * @param editor editor
 *
 * @param file   file
 *
 * @return PsiClass
 */
infix fun Editor.getPsiClass(file: PsiFile?): PsiClass? {
    val offset = this.caretModel.offset
    val element = file?.findElementAt(offset) ?: return null
    val target = PsiTreeUtil.getParentOfType(element, PsiClass::class.java)
    return if (target is SyntheticElement) null else target
}

/**根据函数注解的全限定名找到第一个对应的函数
 * @return PsiMethod
 */
fun PsiClass.getPsiMethodByAnnotation(qualifiedName:String): PsiMethod? {
    for (psiMethod in this.methods) {
        // 获取方法的注解
        val modifierList = psiMethod.modifierList
        val annotations = modifierList.annotations
        annotations
            .map { it.qualifiedName }
            .filter { it != null && it == qualifiedName }
            .forEach {
                // 包含@OnClick注解
                return psiMethod
            }
    }
    return null
}

/**
 * ButterKnife，在OnClick方法里面创建switch
 *
 * @param psiMethodParamsViewField View类型的变量名
 *
 * @param onClickValues            注解里面跟OnClickList的id集合R.id.xxx
 *
 * @return String
 */
fun String.createSwitchByCaseValues(onClickValues: List<String>): String {
    val psiSwitch = StringBuilder()
    psiSwitch.append("switch ($this.getId()) {\n")
    // add default statement
    psiSwitch.append("\tdefault:\n")
    psiSwitch.append("\t\tbreak;\n")
    onClickValues.forEach {
        psiSwitch.append("\tcase $it:\n")
        psiSwitch.append("\t\tbreak;\n")
    }
    psiSwitch.append("}")
    return psiSwitch.toString()
}

/**
 * ButterKnife，创建OnClick方法和switch
 *
 * @param mOnClickList 可onclick的Element的集合
 *
 * @return String
 */
fun List<String>.createButterKnifeOnClickMethodAndSwitch(): String {
    val onClick = StringBuilder()
    onClick.append("@butterknife.OnClick(")
    if (this.size == 1) {
        onClick.append(this[0])
    } else {
        onClick.append("{")
        this.forEachIndexed { i, element ->
            if (i != 0) {
                onClick.append(", ")
            }
            onClick.append(element)
        }
        onClick.append("}")
    }
    onClick.append(")\n")
    onClick.append("public void onClick(View v) {\n")
    onClick.append("switch (v.getId()) {\n")
    // add default statement
    onClick.append("\tdefault:\n")
    onClick.append("\t\tbreak;\n")
    this.forEach {
        onClick.append("\tcase ${it}:\n")
        onClick.append("\t\tbreak;\n")
    }
    onClick.append("}\n")
    onClick.append("}\n")
    return onClick.toString()
}