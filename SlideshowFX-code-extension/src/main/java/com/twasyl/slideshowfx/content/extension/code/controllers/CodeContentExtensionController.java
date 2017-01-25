package com.twasyl.slideshowfx.content.extension.code.controllers;

import com.twasyl.slideshowfx.content.extension.code.controls.SupportedLanguageCell;
import com.twasyl.slideshowfx.content.extension.code.enums.SupportedLanguage;
import com.twasyl.slideshowfx.ui.controls.ZoomTextArea;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * This class is the controller used by the {@code CodeContentExtension.fxml} file.
 * @author Thierry Wasylczenko
 * @version 1.0
 * @since SlideshowFX 1.0
 */
public class CodeContentExtensionController implements Initializable {

    @FXML private ListView<SupportedLanguage> language;
    @FXML private ZoomTextArea code;
    @FXML private CheckBox showingLineNumbers;

    /**
     * Get the selected programming language in the UI.
     * @return The selected programming language in the UI.
     */
    public SupportedLanguage getLanguage() { return this.language.getSelectionModel().getSelectedItem();  }

    /**
     * Get the code that has been entered in the UI.
     * @return The code entered in the UI.
     */
    public String getCode() { return this.code.getText(); }

    /**
     * Indicates if the line numbers should be showed.
     * @return {@code true} if the line numbers showed, {@code false} otherwise.
     */
    public Boolean isShowingLineNumbers() { return this.showingLineNumbers.isSelected(); }

    /**
     * Get the background color for the code that will be displayed in the presentation as hexadecimal string. For the
     * the lightgray color, the string returned will be {@code #D3D3D3}.
     * @return The background color as hexadecimal string.
     */
    public String getBackgroundColorHexadecimal() {
        return "#" + Integer.toHexString(isShowingLineNumbers().hashCode()).substring(0, 6).toUpperCase();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.language.setCellFactory(listview -> new SupportedLanguageCell());

        this.language.getItems().addAll(SupportedLanguage.values());
    }
}
