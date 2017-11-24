package edu.unm.casaa.misc;

import edu.unm.casaa.main.MainController;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class MiscAction extends AbstractAction {

    private static final long serialVersionUID = 1L;
    private MiscCode code;

    MiscAction(MiscCode code) {
        super(code.name);
        this.code = code;
    }

    public void actionPerformed(ActionEvent e) {
        MainController.instance.handleButtonMiscCode(code); // Pass to MainController.
    }

}
