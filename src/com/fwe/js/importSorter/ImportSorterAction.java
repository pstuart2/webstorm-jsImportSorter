package com.fwe.js.importSorter;

import com.intellij.lang.Language;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;


public class ImportSorterAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        final Project project = e.getData(CommonDataKeys.PROJECT);
        final Editor editor = e.getData(CommonDataKeys.EDITOR);
        final Document document = editor.getDocument();

        String text = document.getText();
        ImportSorter sorter = new ImportSorter();
        ImportSorterResult sortResult = sorter.tryParse(text);
        if (sortResult == null || sortResult.getReplacement().length() == 0) {
            return;
        }

        //New instance of Runnable to make a replacement
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                document.replaceString(sortResult.getStart(), sortResult.getEnd(), sortResult.getReplacement());
            }
        };
        //Making the replacement
        WriteCommandAction.runWriteCommandAction(project, runnable);
    }

    @Override
    public void update(AnActionEvent e) {
        //Get required data keys
        final Project project = e.getData(CommonDataKeys.PROJECT);
        final Editor editor = e.getData(CommonDataKeys.EDITOR);
        //PlatformDataKeys/LangDataKeys for non-Intellij
        Language[] languages = e.getDataContext().getData(LangDataKeys.CONTEXT_LANGUAGES);
        boolean isValidLanguage = false;

        if (languages != null && languages.length > 0) {
            isValidLanguage = languages[0].isKindOf("ECMAScript 6");
        }

        //Set visibility only in case of existing project and editor
        e.getPresentation().setVisible((project != null && editor != null && isValidLanguage));
    }

}
